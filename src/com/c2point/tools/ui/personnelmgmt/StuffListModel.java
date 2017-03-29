package com.c2point.tools.ui.personnelmgmt;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.AccessRightsFacade;
import com.c2point.tools.datalayer.AuthenticationFacade;
import com.c2point.tools.datalayer.PresenceFilterType;
import com.c2point.tools.datalayer.UsersFacade;
import com.c2point.tools.entity.access.FunctionalityType;
import com.c2point.tools.entity.authentication.Account;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.ui.listeners.StuffChangedListener;
import com.c2point.tools.ui.util.AbstractModel;

public class StuffListModel extends AbstractModel {
	private static Logger logger = LogManager.getLogger( StuffListModel.class.getName());

	private PresenceFilterType	presenceFilter = PresenceFilterType.CURRENT;
	
	private Organisation 		selectedOrg;
	private OrgUser 			selectedUser;
	
	private boolean				accountUpdated;
	
	public StuffListModel() {
		this( null );

		
	}
	
	public StuffListModel( Organisation org ) {
		super();
		
		this.selectedOrg = ( org != null ? org : getApp().getSessionData().getOrg());
		
		setupAccess( FunctionalityType.USERS_MGMT, this.selectedOrg );
		
	}
	
	public void initModel() {
		
		// Initial model initialization here if necesary
		
		fireListChanged();
	}
	
	public void addListener( StuffChangedListener listener ) {
		listenerList.add( StuffChangedListener.class, listener);
	}

	
	protected void fireAdded( OrgUser user ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == StuffChangedListener.class) {
	    		(( StuffChangedListener )listeners[ i + 1 ] ).wasAdded( user );
	         }
	     }
	 }
	
	protected void fireChanged( OrgUser user ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == StuffChangedListener.class) {
	    		(( StuffChangedListener )listeners[ i + 1 ] ).wasChanged( user );
	         }
	     }
	 }
	
	protected void fireDeleted( OrgUser user ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == StuffChangedListener.class) {
	    		(( StuffChangedListener )listeners[ i + 1 ] ).wasDeleted( user );
	         }
	     }
	 }
	
	protected void fireListChanged() {
		Object[] listeners = listenerList.getListenerList();
		
		if ( logger.isDebugEnabled()) logger.debug( "StuffModel issued WhleListChanged event!" );

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == StuffChangedListener.class) {
	    		(( StuffChangedListener )listeners[ i + 1 ] ).wholeListChanged();
	         }
	     }
	 }

	protected void fireSelected( OrgUser user ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == StuffChangedListener.class) {
	    		(( StuffChangedListener )listeners[ i + 1 ] ).currentWasSet( user );
	         }
	     }
	 }

	public PresenceFilterType getPresenceFilter() { return presenceFilter; }
	public void setPresenceFilter( PresenceFilterType presenceFilter ) { this.presenceFilter = presenceFilter; }

	public OrgUser getSelectedUser() { return selectedUser; }
	public void setSelectedUser( OrgUser selectedUser ) {
		
		if ( getSelectedUser() != selectedUser ) {
			
			clearAccountChanged();
			this.selectedUser = selectedUser; 

			// Fire selection event if this is new user only
			if ( selectedUser != null && selectedUser.getId() > 0 ) {
				fireSelected( selectedUser );
			} else {
				fireSelected( null );
			}
			
		}
	}

	public Organisation getSelectedOrg() { return selectedOrg; }
	public void setSelectedOrg( Organisation selectedOrg ) {
		
		if ( getSelectedOrg() != selectedOrg ) {
			
			this.selectedOrg = selectedOrg;
			clearAccountChanged();
			

			setupAccess( FunctionalityType.USERS_MGMT, this.selectedOrg );
			
			fireListChanged();
		}
	}
	

	public Collection<OrgUser> getUsers() {
	
		return UsersFacade.getInstance().list( getSelectedOrg(), getPresenceFilter());
		
	}

	public boolean isSuperUser() {
		
		return getApp().getSessionData().getOrgUser().isSuperUserFlag();
		
	}

	public OrgUser add( OrgUser addedUser ) {
		
		OrgUser newUser = null;
		
		// Add to DB
		if ( addedUser != null ) {

			newUser = UsersFacade.getInstance().add( addedUser );
			
			if ( newUser != null ) {
				
				fireAdded( newUser );
/*				
				// Add account or connect  to existing one
				
				// 1. Find account by usrname
				Account account = AuthenticationFacade.getInstance().findByUserName( addedUser.getAccount().getUsrName());

				// 2. If not found than add new one
				if ( account == null ) {
					account = AuthenticationFacade.getInstance().addAccount( 
							addedUser.getAccount().getUsrName(),
							addedUser.getAccount().getPwd(),
							newUser );

				} else {
				// 3. If found than select to add to it or return false to create new account
					
				}
				
				// 4. Update user with account info
*/			
			} 
		}
		
		return newUser;
		
	}
	
	public OrgUser update( OrgUser updatedUser ) {
		
		OrgUser newUser = null;
		
		// Update DB
		if ( updatedUser != null ) {

			newUser = UsersFacade.getInstance().update( updatedUser );
			
			if ( newUser != null ) {
				
				fireChanged( newUser );
			
			} 
			
		}
		
		return newUser;
		
	}
	
	public OrgUser delete( OrgUser deletedUser ) {
		
		OrgUser newUser = null;

		// Delete DB
		if ( deletedUser != null ) {

			newUser = UsersFacade.getInstance().delete( deletedUser );
			
			if ( newUser != null ) {
				
				fireDeleted( newUser );
			
			} 
			
		}
		
		return newUser;
		
	}

	public void clearAccessRights( OrgUser user ) {

		AccessRightsFacade.getInstance().clearAccessRights( user );
		
	}
	
	
	
	public boolean wasAccountChanged() { return this.accountUpdated; }
	public boolean setAccountChanged() { return this.accountUpdated = true; }
	public boolean clearAccountChanged() { return this.accountUpdated = false; }
/*	
	public boolean checkName( String newName ) {
		
		boolean bRes = false;
	
		if ( getEditMode() == EditModeType.ADD ) {
			logger.debug( "ADD mode. Just search for existing name" );
			bRes = AuthenticationFacade.getInstance().findByUserName( newName ) == null;
			
		} else if ( getEditMode() == EditModeType.EDIT ) {
			logger.debug( "Edit mode. Just search for existing name" );
			
			Account account = AuthenticationFacade.getInstance().findByUserName( newName );
			
			if ( account == null  
				||
				this.getSelectedUser().getAccount() != null 
					&&
				this.getSelectedUser().getAccount().getId() == account.getId()) {

				// Account found and this is account we are editing right now!
				bRes = true;
			}
			
		} else {
			logger.debug( "VIEW mode. Nothing to check" );
			bRes = true;
		}
			
		logger.debug( "newName was checked. Result: " + bRes );
		return bRes;
	}
*/	
	
	/*
	 *  Check usrname from account
	 * Return:
	 *	0 - account with such usrName exists and it is the same
	 *  1 - account with such usrName exists but other than checked
	 * -1 - account with such usrName does not exist
	 * 
	 */
	public enum  CheckNameType { NO, EXIST, DUPLICATE };
	
	public CheckNameType checkName( String newName, OrgUser user ) {
		
		CheckNameType eRes = CheckNameType.EXIST;
		
		Account account = AuthenticationFacade.getInstance().findByUserName( newName );
		
		if ( account == null ) {
			// Account with specified Usrname doesNOT exist
			eRes = CheckNameType.NO; 
		} else if ( user.getAccount() != null && account.getId() != user.getAccount().getId()) {
			//There is account with the same username. 
			eRes = CheckNameType.DUPLICATE;
			// Try to update usrname
				
			
		} else {
			eRes = CheckNameType.EXIST;
		}
		
		return eRes;
	}
	
	public String updateUserName( String usrName ) {
	
		return AuthenticationFacade.getInstance().getModifiedName( usrName );
	}
	
	
	public boolean saveAccount( Account account, OrgUser persistentUser ) {
		
		boolean bRes = false;
		
		if ( account == null || persistentUser == null ) {
			logger.error( "SaveAccount: account and/or user == null. Must be specified!" );
			return bRes;
		}
		
		if ( persistentUser.getId() <=0 ) {
			logger.error( "SaveAccount: User is not persistent. Account cannot be managed!" );
			return bRes;
		}
		
		String usrName = account.getUsrName(); 
		String pwd = account.getPwd();
		
		if ( StringUtils.isBlank( usrName ) || StringUtils.isBlank( pwd )) {
			logger.error( "SaveAccount: UsrName and/or Password == null. Must be specified!" );
			return bRes;
		}
		
		// Existed account if any
		Account existedAccount = persistentUser.getAccount();
		
		if ( existedAccount == null ) {
			// Account does not exist. Shall be added
			persistentUser.setAccount( account );
			
			
		} else {
			// Account exists. Shall be modified
			
		}
		
		
		

			
			
			
		fireChanged( persistentUser );
		bRes = true;
			

/*			
		if ( this.shownOrg.getId() > 0 ) {
			// This is new record. It must be added
			Organisation newOrg = model.add( this.shownOrg );
			if ( newOrg == null ) {

				String template = model.getApp().getResourceStr( "general.error.add.header" );
				Object[] params = { this.shownOrg.getName() };
				template = MessageFormat.format( template, params );

				Notification.show( template, Notification.Type.ERROR_MESSAGE );

			} else {
				currentWasSet( newOrg );
			}

		}
*/		
//		this.accountUpdated = this.accountUpdated || bRes;
		this.accountUpdated = true;
		
		
//		logger.debug( "Tried to save account. Result: " + bRes );
		return bRes;
	}

	
	
	
}
