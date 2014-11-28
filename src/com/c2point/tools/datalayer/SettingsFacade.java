package com.c2point.tools.datalayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.Settings;
import com.c2point.tools.entity.organisation.Organisation;

public class SettingsFacade extends DataFacade {
	private static Logger logger = LogManager.getLogger( SettingsFacade.class.getName()); 

	private static int					MAX_INSTANCE_NUMBER = 1;
	private static SettingsFacade []	instances;
	private static int					next_instance_number;
	
	private static Map<Long, Properties>	propMap = new HashMap<Long, Properties>();
	
	public static SettingsFacade getInstance() {
		
		if ( instances == null ) {
			instances = new SettingsFacade[ MAX_INSTANCE_NUMBER ];
			for ( int i = 0; i < MAX_INSTANCE_NUMBER; i++ ) {
				instances[ i ] = new SettingsFacade();  
			}
			next_instance_number = 0;
			
		}
		
		SettingsFacade ret = instances[ next_instance_number ];
		if ( logger.isDebugEnabled()) logger.debug( "Settings instance number retirned is " + next_instance_number + " from " + MAX_INSTANCE_NUMBER + " available!" );
		
		next_instance_number = ++next_instance_number % MAX_INSTANCE_NUMBER ;
		
		return ret;
	}
	
	private SettingsFacade() {
		super();
	}

	public String getProperty( Organisation org, String key ) {
		
		return getProperty( org, key, null );
	}
	
	public String getProperty( Organisation org, String key, String defValue ) {
		String value = null;
		
		if ( org != null && org.getId()> 0 ) {
			Properties props = propMap.get( org.getId());
			
			if ( props == null ) {
				
				Settings settings = new Settings( org ); 
				settings = this.insert( settings );
				
				props = settings.getProperties();
				
				propMap.put( settings.getOrganisation().getId(), props);
				
			}
			
			value = props.getProperty( key, defValue );
		} else {
			
			value = defValue;
		}
		
		return value;
	}
	
	public void setProperty( Organisation org, String key, String value ) {

		Settings settings; 
		
		settings = this.getSettings( org );
		
		if ( settings == null ) {

			settings = new Settings( org ); 
			settings = this.insert( settings );
			
		}
		
		Properties props = propMap.get( org.getId());
		
		if ( props == null ) {
			
			props = settings.getProperties();
			
			propMap.put( settings.getOrganisation().getId(), props);
			
		}
		
		props.setProperty( key,  value );
		settings.setProperties( props );
		
		this.merge( settings );
		
	}

	private Settings getSettings( Organisation org ) {
		
		if ( org == null )
			throw new IllegalArgumentException( "Valid Organisation cannot be null!" );

		EntityManager em = DataFacade.getInstance().createEntityManager();
		
		TypedQuery<Settings> query = null;
		Settings result = null;

		try {
			
			query = em.createNamedQuery( "getSettings", Settings.class )
				.setParameter( "org", org );
	
			result = query.getSingleResult();
			
			
		} catch ( NoResultException e ) {
			logger.debug( "No Settings record for '" + org.getName() + "' yet!" );
		} catch ( NonUniqueResultException e ) {
			logger.error( "More than one Settings records for '" + org.getName() + "'!" );
		} catch ( Exception e ) {
			logger.error( e );
		} finally {
			em.close();
		}
			
		return result;
		
	}

	
}
