package com.c2point.tools.datalayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Category;
import com.c2point.tools.entity.tool.Tool;
import com.c2point.tools.entity.tool.identity.ToolIdentity;
import com.c2point.tools.entity.tool.identity.ToolIdentityType;

public class UsersFacade extends DataFacade {

	private static Logger logger = LogManager.getLogger( UsersFacade.class.getName()); 

	private static int				MAX_INSTANCE_NUMBER = 6;
	private static UsersFacade []	instances;
	private static int				next_instance_number;
	
	public static UsersFacade getInstance() {
		
		if ( instances == null ) {
			instances = new UsersFacade[ MAX_INSTANCE_NUMBER ];
			for ( int i = 0; i < MAX_INSTANCE_NUMBER; i++ ) {
				instances[ i ] = new UsersFacade();  
			}
			next_instance_number = 0;
			
		}
		
		UsersFacade ret = instances[ next_instance_number ];
		if ( logger.isDebugEnabled()) logger.debug( "ToolsAndItemsFacade instance number retirned is " + next_instance_number + " from " + MAX_INSTANCE_NUMBER + " available!" );
		
		next_instance_number = ++next_instance_number % MAX_INSTANCE_NUMBER ;
		
		return ret;
	}
	
	private UsersFacade() {
		super();
	}
	
	/*
	 * Below methods for OrgUsers objects manipulation
	 */

	public Collection<OrgUser> list( Organisation org ) {
		
		return list( org, PresenceFilterType.CURRENT );
	}

	public Collection<OrgUser> list( Organisation org, PresenceFilterType presenceFilter ) {
		
		if ( org == null )
			throw new IllegalArgumentException( "Valid Organisation cannot be null!" );

		Collection<OrgUser> results = null;
		
		EntityManager em = DataFacade.getInstance().createEntityManager();
		TypedQuery<OrgUser> query = null;
		String queryName = null;
		
		try {
			switch ( presenceFilter ) {
			case ALL:
				queryName = "listUsersAll";
				break;
			case CURRENT:
				queryName = "listUsersCurrent";
				break;
			case DELETED:
				queryName = "listUsersDeleted";
				break;
			default:
				break;
			
			}

			query = em.createNamedQuery( queryName, OrgUser.class )
					.setParameter( "org", org );
			
			results = query.getResultList();
			if ( logger.isDebugEnabled()) logger.debug( "**** Fetched list of OrgUsers. Size = " + results.size());
		} catch ( NoResultException e ) {
			if ( logger.isDebugEnabled()) logger.debug( "No users found!" );
		} catch ( Exception e ) {
			results = null;
			logger.error( e );
		} finally {
			em.close();
		}
		
		return results;
		
	}
			
}

