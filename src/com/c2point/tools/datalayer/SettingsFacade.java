package com.c2point.tools.datalayer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.RollbackException;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.Configuration;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.settings.AllProperties;
import com.c2point.tools.entity.settings.OrgProperties;
import com.c2point.tools.entity.settings.Property;

public class SettingsFacade extends DataFacade {
	private static Logger logger = LogManager.getLogger( SettingsFacade.class.getName()); 

	private static int					MAX_INSTANCE_NUMBER = 1;
	private static SettingsFacade []	instances;
	private static int					next_instance_number;
	
	private static Map<Long, Properties>	propMap = new HashMap<Long, Properties>();
	private static long						SYSTEM_PROPS_KEY = -10;
	
	public static SettingsFacade getInstance() {
		
		if ( instances == null ) {
			instances = new SettingsFacade[ MAX_INSTANCE_NUMBER ];
			for ( int i = 0; i < MAX_INSTANCE_NUMBER; i++ ) {
				instances[ i ] = new SettingsFacade();  
			}
			next_instance_number = 0;

			// Read settings
			instances[ next_instance_number ].readAllSettings();
		}
		
		SettingsFacade ret = instances[ next_instance_number ];
		if ( logger.isDebugEnabled()) logger.debug( "Settings instance number retirned is " + next_instance_number + " from " + MAX_INSTANCE_NUMBER + " available!" );
		
		next_instance_number = ++next_instance_number % MAX_INSTANCE_NUMBER ;
		
		return ret;
	}
	
	private SettingsFacade() {
		super();
	}

	public Boolean getBoolean( Organisation org, String name ) {
		
		return get( org, name, false );
	}
		
	public Boolean getBoolean( Organisation org, String name, Boolean defValue ) {
		
		return get( org, name, defValue );
	}
	
	public Integer getInteger( Organisation org, String name ) {
		
		return get( org, name, -1 );
	}
	
	public Integer getInteger( Organisation org, String name, Integer defValue ) {
		
		return get( org, name, defValue );
	}
	
	public Long getLong( Organisation org, String name ) {
		
		return get( org, name, -1L );
	}
	
	public Long getLong( Organisation org, String name, Long defValue ) {
		
		return get( org, name, defValue );
	}
	
	public String getString( Organisation org, String name ) {
		
		return get( org, name, ( String )null );
	}
	
	public String getString( Organisation org, String name, String defValue ) {
		
		return get( org, name, defValue );
	}
	
	public void set( Organisation org, String name, Object value ) {

		OrgProperties props = AllProperties.getProperties( org );
		
		props.set( name, value );

		Property prop;
		while ( props.hasToUpdate()) {
			
			prop = props.getNextToUpdate();
			
			if ( prop != null ) {
				storeProperty( prop );
			}
		}
		
	}

	public String getSystemProperty( String key ) {
		String value = null;
		
		Properties props = propMap.get( SYSTEM_PROPS_KEY );
		
		if ( props == null ) {
			
			try {
				props = readSystemProperties();
				propMap.put( SYSTEM_PROPS_KEY, props);
				
			} catch (IOException e) {
				logger.error( e.getMessage());
			}
			
		}

		if ( props != null ) {
			value = props.getProperty( key );
		}
		if ( value == null ) {
			
			logger.error( "Property '" + key + "' is not found in System Settings File!!! Define property!" );
			
		}
		
		
		return value;
	}

	private Properties readSystemProperties() throws IOException {
		
		Properties props = new Properties();

		props.load( new FileInputStream( Configuration.getSystemConfigFileName()));
		
		return props;
	}
	

	private Property storeProperty( Property prop ) {
		
		Property newProp = null;

		EntityManager em = createEntityManager();
		try {
			em.getTransaction().begin();
			
			// Find property
			List<Property> list = null;
			Property oldProp = null;

			try {
				// Fetched property, Should be one  
				TypedQuery<Property> q = em.createNamedQuery( "findNamedProperty", Property.class )
						.setParameter( "org", prop.getOrganisation())
						.setParameter( "name", prop.getName());
					list = q.getResultList();
				
			} catch ( NoResultException e ) {
				if ( logger.isDebugEnabled())
					logger.debug( "No Property was stored before. Will be added: '" + prop.getName() + "'" );
			} catch ( Exception e ) {
				logger.error( e );
			}
			
			if ( list == null || list != null && list.size() == 0 ) {
				// No Property was stored before. Will be added
				em.persist( prop );
				newProp = prop;
				
			} else {
				// Check that find one. Warning if more than one but take first one
				if ( list.size() > 1 ) {
					logger.error( "There is more than 1 property '" + prop.getName() + "' for org '" + prop.getOrganisation().getName() + "'. Check the set of properties!" );
				}
					
				oldProp = list.get( 0 );
				
				// Update property
				oldProp.setValue( prop );
				
				// merge
				newProp = em.merge(  oldProp );

			}

			em.flush();
			newProp = em.merge(newProp); // Related entities marked cascade-merge will
			// become merged too.
			em.getTransaction().commit();
			
		} catch (RollbackException e) {
			logger.error( "Cannot save property '" + prop.getName() + "' for org '" + prop.getOrganisation().getName() + "'!" );
		} finally {
			em.close();
		}
		
		return newProp;
	}

	public <T> T get( Organisation org, String name, T defValue ) {

		Class<T> c = null;
		T value = null;
		
		OrgProperties props = AllProperties.getProperties( org );
		
		if ( props != null ) {
			value = props.get( c, name );
		}
		
		if ( value == null ) {
			
			value = defValue;
		}
		
		return value;
	}

	private void readAllSettings() {
		
		Collection<Property> list = this.list( Property.class );
		
		if ( list != null ) {
			
			for( Property prop : list ) {
				
				AllProperties.getProperties( prop.getOrganisation()).set( prop );
			}
		}
	}
}
