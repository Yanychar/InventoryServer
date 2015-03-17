package com.c2point.tools.datalayer;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.InventoryUI;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.transactions.TransactionOperation;
import com.vaadin.ui.UI;

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

	public long count( Organisation org ) {
		
		long result = 0;

		if ( org == null )
			throw new IllegalArgumentException( "Valid Organisation cannot be null!" );
		
		EntityManager em = DataFacade.getInstance().createEntityManager();
		TypedQuery<Long> query = null;
		
		try {
			query = em.createNamedQuery( "countAll", Long.class )
					.setParameter( "org", org );
			
			result = query.getSingleResult();
			
			if ( logger.isDebugEnabled()) logger.debug( "**** Number of all users for Org = " + result );
			
		} catch ( NoResultException e ) {
			if ( logger.isDebugEnabled()) logger.debug( "No users found!" );
		} catch ( Exception e ) {
			logger.error( e );
		} finally {
			em.close();
		}
		
		return result;
		
	}

	public OrgUser delete( OrgUser user ) {
		
		user.setDeleted();
		return updateOrDelete( user, true );
	}

	public OrgUser update( OrgUser user ) {
		
		return updateOrDelete( user, false );
	}

	// Internal implementation update and delete
	public OrgUser updateOrDelete( OrgUser user, boolean delete ) {

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

		try {
			OrgUser whoDid = (( InventoryUI )UI.getCurrent()).getSessionOwner();
			TransactionsFacade.getInstance().writeUser( whoDid, newUser, TransactionOperation.EDIT );
			
		} catch ( Exception e ) {
			logger.error( "Cannot identify who edited User");
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

		try {
			OrgUser whoDid = (( InventoryUI )UI.getCurrent()).getSessionOwner();
			TransactionsFacade.getInstance().writeUser( whoDid, newUser, TransactionOperation.ADD );
			
		} catch ( Exception e ) {
			logger.error( "Cannot identify who added User");
		}
		
		if ( logger.isDebugEnabled() && newUser != null ) 
			logger.debug( "OrgUser has been added: " + newUser );
		
		
		return newUser;
		
	}

	public List<OrgUser> listByFIO( Organisation org, String firstName, String lastName ) {
		
		List<OrgUser> results = null;
		
		if ( org == null )
			throw new IllegalArgumentException( "Valid Organisation cannot be null!" );

		
		EntityManager em = DataFacade.getInstance().createEntityManager();

		TypedQuery<OrgUser> query;

		try {
			query = em.createNamedQuery( "listByFIO", OrgUser.class )
					.setParameter( "org", org )
					.setParameter( "firstname", firstName )
					.setParameter( "lastname", lastName );

				results = query.getResultList();

				if ( results != null && results.size() > 0 ) {
					if ( logger.isDebugEnabled()) 
						logger.debug( "User '" 
								+ firstName + " " + lastName
								+ "' exists!!!"
						);
					
				} else {
					
					results = null;
					
				}
				
				
		} catch ( NoResultException e ) {
			if ( logger.isDebugEnabled()) logger.debug( "No users found!" );
			results = null;
			
		} catch ( Exception e ) {
			logger.error( e );
			results = null;
		} finally {
			em.close();
		}
		
		return results;
	}
/*	
	public void setUniqueCode( OrgUser user ) {

		long lastUniqueCode = 0;
		
		try {
			lastUniqueCode = Long.parseLong( 
					SettingsFacade.getInstance().getProperty( user.getOrganisation(), "lastPersonnelCode" ));
		} catch ( NumberFormatException e ) {
			
			logger.error( "Wrong value for lastPersonnelCode was written in properties: " + 
					SettingsFacade.getInstance().getProperty( user.getOrganisation(), "lastPersonnelCode" ));	
		}
		
		if ( lastUniqueCode == 0 && user.getOrganisation() != null ) {
			
			lastUniqueCode = this.count( user.getOrganisation());

		}

		int codeLength = 6;
		try {
			codeLength = Integer.parseInt( 
					SettingsFacade.getInstance().getProperty( user.getOrganisation(), "personnelCodeLength", "6" ));
		} catch ( NumberFormatException e ) {
			
			logger.error( "Wrong value for length of PersonnelCode was written in properties: " + 
					SettingsFacade.getInstance().getProperty( user.getOrganisation(), "personnelCodeLength" ));	
		}
		
		lastUniqueCode++;
		
		String newCode = StringUtils.leftPad(
				Long.toString( lastUniqueCode ),
				codeLength,	
				'0'
		);

		// Store new lastUniqueCode
		SettingsFacade.getInstance().setProperty( user.getOrganisation(), 
												  "lastPersonnelCode", 
												  Long.toString( lastUniqueCode ));
		// set up User code
		user.setCode( newCode );
		
	}
*/	
}

