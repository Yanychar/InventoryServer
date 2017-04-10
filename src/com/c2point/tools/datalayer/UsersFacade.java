package com.c2point.tools.datalayer;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.InventoryUI;
import com.c2point.tools.entity.authentication.Account;
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
		
		return updateOrDelete( user, true );
	}

	public OrgUser update( OrgUser user ) {
		
		return updateOrDelete( user, false );
	}

	// Internal implementation update and delete
	private OrgUser updateOrDelete( OrgUser user, boolean delete ) {

		OrgUser editedUser = null;

		if ( user == null )
			throw new IllegalArgumentException( "Valid User cannot be null!" );
		
		try {
			
			editedUser = DataFacade.getInstance().find( OrgUser.class, user.getId());
			
		} catch ( Exception e ) {
			logger.error( "Failed to update OrgUser: " + user );
			logger.error( e );
			return null;
		}

		if ( delete ) {
			// Delete user and Account
			editedUser.setDeleted();
			
			// Delete account if one user only
			Account account = editedUser.getAccount();
			if ( account != null && account.getActiveUsers().size() == 0 ) {
				
				account.setDeleted();
			}
			
		} else {
			// Edit fields
			
			editedUser.setFirstName( user.getFirstName());
			editedUser.setLastName( user.getLastName());
			editedUser.setBirthday( user.getBirthday());

			editedUser.setAddress( user.getAddress());
			
			editedUser.setEmail( user.getEmail());
			editedUser.setPhoneNumber( user.getPhoneNumber());
			editedUser.setAccessGroup( user.getAccessGroup());

			editedUser.setAccount( user.getAccount());
		}
		
		
		try {
			editedUser = DataFacade.getInstance().merge( editedUser );
		} catch ( Exception e ) {
			logger.error( "Failed to update/delete OrgUser: " + user );
			logger.error( e );
			return null;
		}

		try {
			OrgUser whoDid = (( InventoryUI )UI.getCurrent()).getSessionOwner();
			TransactionsFacade.getInstance().writeUser( whoDid, editedUser, delete ? TransactionOperation.DELETE : TransactionOperation.EDIT );
			
		} catch ( Exception e ) {
			logger.error( "Cannot identify who edited/deleted User");
		}
		
		if ( logger.isDebugEnabled() && editedUser != null ) 
			logger.debug( "OrgUser has been updated/deleted: " + editedUser );
		
		
		return editedUser;
		
	}
	
	public OrgUser add( OrgUser user ) {

		OrgUser newUser = null;
		
		if ( user == null )
			throw new IllegalArgumentException( "Valid User cannot be null!" );
	
		setUniqueCode( user );
		
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
	
	public void setUniqueCode( OrgUser user ) {

		boolean useUserCode = SettingsFacade.getInstance().getBoolean( user.getOrganisation(), "usePersonnelCode", false );

		// Force to assign user code even it is not used lately
		SettingsFacade.getInstance().set( user.getOrganisation(), "usePersonnelCode", true );
		useUserCode = true;
		
		if ( useUserCode ) {
		
			long lastUniqueCode = SettingsFacade.getInstance().getLong( user.getOrganisation(), "lastPersonnelCode", Long.valueOf( 1 ));

			if ( lastUniqueCode <= 0 && user.getOrganisation() != null ) {
				
				lastUniqueCode = this.count( user.getOrganisation());
	
			}
	
			int codeLength = SettingsFacade.getInstance().getInteger( user.getOrganisation(), "personnelCodeLength", 6 );
			
			lastUniqueCode++;
			
			String newCode = StringUtils.leftPad(
					Long.toString( lastUniqueCode ),
					codeLength,	
					'0'
			);
	
			// Store new lastUniqueCode
			SettingsFacade.getInstance().set( user.getOrganisation(), 
													  "lastPersonnelCode", 
													  lastUniqueCode );
			// set up User code
			user.setCode( newCode );
			
		}
	}

}

