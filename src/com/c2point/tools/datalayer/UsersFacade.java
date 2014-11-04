package com.c2point.tools.datalayer;

import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.person.OrgUser;

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
		if ( logger.isDebugEnabled()) logger.debug( "ToolsAndItemsFacade instance number returned is " + next_instance_number + " from " + MAX_INSTANCE_NUMBER + " available!" );
		
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

	public OrgUser update( OrgUser user ) {

		OrgUser newUser = null;
		
		if ( user == null )
			throw new IllegalArgumentException( "Valid User cannot be null!" );
		
		try {
			newUser = DataFacade.getInstance().merge( user );
		} catch ( Exception e ) {
			logger.error( "Failed to update OrgUser: " + user );
			logger.error( e );
			return null;
		}
		

		if ( logger.isDebugEnabled() && newUser != null ) 
			logger.debug( "OrgUser has been updated: " + newUser );
		
		
		return newUser;
		
	}
	
	public OrgUser add( OrgUser user ) {

		OrgUser newUser = null;
		
		if ( user == null )
			throw new IllegalArgumentException( "Valid User cannot be null!" );
		
		try {
			newUser = DataFacade.getInstance().insert( user );
		} catch ( Exception e ) {
			logger.error( "Failed to add OrgUser: " + user );
			logger.error( e );
			return null;
		}
		

		if ( logger.isDebugEnabled() && newUser != null ) 
			logger.debug( "OrgUser has been added: " + newUser );
		
		
		return newUser;
		
	}

	public boolean doesExistByFIO( Organisation org, String firstName, String lastName ) {
		
		boolean bRes = false;
		
		if ( org == null )
			throw new IllegalArgumentException( "Valid Organisation cannot be null!" );

		Collection<OrgUser> results = null;
		
		EntityManager em = DataFacade.getInstance().createEntityManager();

		TypedQuery<OrgUser> query;

		try {
			query = em.createNamedQuery( "listByFIO", OrgUser.class )
					.setParameter( "org", org )
					.setParameter( "firstname", firstName )
					.setParameter( "lastname", lastName );

				results = query.getResultList();

				if ( results.size() > 0 ) {
					if ( logger.isDebugEnabled()) 
						logger.debug( "User '" 
								+ firstName + " " + lastName
								+ "' exists!!!"
						);
					
					
					bRes = true;
					
				}
				
		} catch ( NoResultException e ) {
			if ( logger.isDebugEnabled()) logger.debug( "No users found!" );
			
			bRes = false;
			
		} catch ( Exception e ) {
			logger.error( e );
		} finally {
			em.close();
		}
		
		return bRes;
	}
	
	
	
}

