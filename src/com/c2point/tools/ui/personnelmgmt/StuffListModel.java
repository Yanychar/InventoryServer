package com.c2point.tools.ui.personnelmgmt;

import java.util.Collection;

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
import com.c2point.tools.ui.listeners.EditInitiationListener;
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
			
			this.accountUpdated = false;
			this.selectedUser = selectedUser; 

			fireSelected( selectedUser );
			
		}
	}

	public Organisation getSelectedOrg() { return selectedOrg; }
	public void setSelectedOrg( Organisation selectedOrg ) {
		
		if ( getSelectedOrg() != selectedOrg ) {
			
			this.selectedOrg = selectedOrg;
			this.accountUpdated = false;
			

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
	
	
	public boolean saveAccount( String newName, String newPwd ) {
		
		boolean bRes = false;

		OrgUser user = this.getSelectedUser();
		
		if ( user != null ) {
			
			Account account = user.getAccount();
			
			if ( account == null ) {
				
				account = new Account( newName, newPwd, user );
				
			} else {
				
				account.setUsrName( newName );
				account.setPwd( newPwd );
				
			}
			
//			fireChanged( user );
			bRes = true;
			
		}

/*			
		if ( this.shownOrg.getId() > 0 ) {
			// This is existing record update
			Organisation newOrg = model.update( this.shownOrg );
			if ( newOrg == null ) {

				String template = model.getApp().getResourceStr( "general.error.update.header" );
				Object[] params = { this.shownOrg.getName() };
				template = MessageFormat.format( template, params );

				Notification.show( template, Notification.Type.ERROR_MESSAGE );

			} else {
				currentWasSet( newOrg );
			}
		} else {
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
		
		this.accountUpdated = this.accountUpdated || bRes;
		
		
		logger.debug( "Tried to save account. Result: " + bRes );
		return bRes;
	}
	
	public boolean accountWasChanged() { return this.accountUpdated; }
	
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
	
	
/*	
	public boolean checkPassword( String newPwd ) {
		
		boolean bRes = true;
		
		
		
		logger.debug( "newPwd was checked. Result: " + bRes );
		return bRes;
	}
*/
	
}
