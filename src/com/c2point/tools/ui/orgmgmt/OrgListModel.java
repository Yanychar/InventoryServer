package com.c2point.tools.ui.orgmgmt;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.DataFacade;
import com.c2point.tools.datalayer.OrganisationFacade;
import com.c2point.tools.datalayer.PresenceFilterType;
import com.c2point.tools.datalayer.UsersFacade;
import com.c2point.tools.entity.access.FunctionalityType;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.ui.listeners.OrgChangedListener;
import com.c2point.tools.ui.util.AbstractModel;

public class OrgListModel extends AbstractModel {
	private static Logger logger = LogManager.getLogger( OrgListModel.class.getName());

	private Organisation 		selectedOrg;
	
	public OrgListModel() {
		
		this( null );
		
	}
	
	public OrgListModel( Organisation org ) {
		super();

		this.selectedOrg = ( org != null ? org : getApp().getSessionData().getOrg());
		setViewMode();

		setupAccess( FunctionalityType.ORGS_MGMT, this.selectedOrg );
		
	}
	
	public void initModel() {
		
		// Initial model initialization here if necesary
		
		fireListChanged();
	}
	
	public void addChangedListener( OrgChangedListener listener ) {
		listenerList.add( OrgChangedListener.class, listener);
	}
	
	protected void fireAdded( Organisation org ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == OrgChangedListener.class) {
	    		(( OrgChangedListener )listeners[ i + 1 ] ).wasAdded( org );
	         }
	     }
	 }
	
	protected void fireChanged( Organisation org ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == OrgChangedListener.class) {
	    		(( OrgChangedListener )listeners[ i + 1 ] ).wasChanged( org );
	         }
	     }
	 }
	
	protected void fireDeleted( Organisation org ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == OrgChangedListener.class) {
	    		(( OrgChangedListener )listeners[ i + 1 ] ).wasDeleted( org );
	         }
	     }
	 }
	
	protected void fireListChanged() {
		Object[] listeners = listenerList.getListenerList();
		
		if ( logger.isDebugEnabled()) logger.debug( "OrgListModel issued WhleListChanged event!" );

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == OrgChangedListener.class) {
	    		(( OrgChangedListener )listeners[ i + 1 ] ).wholeListChanged();
	         }
	     }
	 }

	protected void fireSelected( Organisation org ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == OrgChangedListener.class) {
	    		(( OrgChangedListener )listeners[ i + 1 ] ).currentWasSet( org );
	         }
	     }
	 }

	public Organisation getSelectedOrg() { return this.selectedOrg; }
	public void setSelectedOrg( Organisation selectedOrg ) {
		
		
		if ( getSelectedOrg() != selectedOrg ) {
			
			this.selectedOrg = selectedOrg;
			
			checkOwnerSelectable();

			setupAccess( FunctionalityType.ORGS_MGMT, this.selectedOrg );
			
			fireSelected( this.selectedOrg );
		}
		
		
	}

	public Collection<Organisation> getOrganisations() {

		Collection<Organisation> list = null;
		
		if ( allowsOtherCompanies()) {
			
			return DataFacade.getInstance().list( Organisation.class );
			
		} else {
			
			list = new ArrayList<Organisation>();
			
			if ( getSelectedOrg() != null ) 
				list.add( getSelectedOrg());
			
		}
		
		return list;
		
	}

	public Organisation add( Organisation addedOrg ) {
	
		Organisation newOrg = null;

		// Add to DB
		if ( allowsToEdit() && addedOrg != null ) {
			
			newOrg = OrganisationFacade.getInstance().add( addedOrg );
			
//			addUpdateResponsibleIfNecessary( newOrg );
			
			if ( newOrg != null ) {
				
				fireAdded( newOrg );
			
			} 
			
		}
		
		return newOrg;
		
	}
	
	public Organisation update( Organisation updatedOrg ) {
		
		Organisation newOrg = null;
		
		// Update DB
		if ( allowsToEdit() && updatedOrg != null ) {

//			addUpdateResponsibleIfNecessary();
			
			newOrg = OrganisationFacade.getInstance().update( updatedOrg );
			
			if ( newOrg != null ) {
				
				fireChanged( newOrg );
			
			} 
			
		}
		
		return newOrg;
		
	}
	
	public Organisation delete( Organisation deletedOrg ) {
		
		Organisation org = null;

		
		// Update DB
		if ( allowsToEdit() && deletedOrg != null ) {

			org = OrganisationFacade.getInstance().delete( deletedOrg );
			
			if ( org != null ) {
				
				fireDeleted( org );
			
			} 
			
		}
		
		return org;
		
	}

	
	public void setOrgCode( Organisation org ) {
		
		// Code will be set by UsersFacade
		OrganisationFacade.getInstance().setUniqueCode( org );
		
	}

	public Collection<OrgUser> getUsers() {
		
		return UsersFacade.getInstance().list( getSelectedOrg());
	}


	private boolean serviceOwnerSelectable = true;
	public boolean ownerCanBeSelected() {
		
		return serviceOwnerSelectable;
	}
	
	private void checkOwnerSelectable() {

		this.serviceOwnerSelectable = false;
		
		// Employee selection is possible if there is 1 or more NOT DELETED employees
		if ( this.selectedOrg != null && this.selectedOrg.getEmployees() != null
			&&
			 this.selectedOrg.getEmployees().size() > 0 ) {
			
			for ( OrgUser user : this.selectedOrg.getEmployees().values()) {
				
				if ( !user.isDeleted()) {
					this.serviceOwnerSelectable = true;
					break;
				}
			}
			
		}
		
		
	}


}
