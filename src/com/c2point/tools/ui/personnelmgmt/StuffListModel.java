package com.c2point.tools.ui.personnelmgmt;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.PresenceFilterType;
import com.c2point.tools.datalayer.UsersFacade;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.tool.Category;
import com.c2point.tools.ui.AbstractModel;
import com.c2point.tools.ui.category.CategoryModelListener;

public class StuffListModel extends AbstractModel {
	private static Logger logger = LogManager.getLogger( StuffListModel.class.getName());

	private PresenceFilterType	presenceFilter = PresenceFilterType.CURRENT;
	
	private Organisation 		org;
	private OrgUser 			selectedUser;
	

	public StuffListModel() {
		this( null );

		
	}
	
	public StuffListModel( Organisation org ) {
		super();
		
		setOrg( 
				org != null ? 
					org 
				: 
					getApp().getSessionData().getOrg()
		);
	}
	
	public void initModel() {
		
		// Initial model initialization here if necesary
		
		fireListChanged();
	}
	
	public void addChangedListener( StuffChangedListener listener ) {
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
	public void setSelectedUser( OrgUser selectedUser ) { this.selectedUser = selectedUser; }

	public Organisation getOrg() { return org; }
	public void setOrg( Organisation org ) { this.org = org; }

	public Collection<OrgUser> getUsers() {
	
		return UsersFacade.getInstance().list( getOrg(), getPresenceFilter());
		
	}

	public boolean isSuperUser() {
		
		return getApp().getSessionData().getOrgUser().isSuperUserFlag();
		
	}
	

}
