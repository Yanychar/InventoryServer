package com.c2point.tools.ui.orgmgmt;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.access.FunctionalityType;
import com.c2point.tools.datalayer.DataFacade;
import com.c2point.tools.datalayer.OrganisationFacade;
import com.c2point.tools.datalayer.PresenceFilterType;
import com.c2point.tools.datalayer.UsersFacade;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.ui.AbstractModel;

public class OrgListModel extends AbstractModel {
		private static Logger logger = LogManager.getLogger( OrgListModel.class.getName());

	private PresenceFilterType	presenceFilter = PresenceFilterType.CURRENT;
	
	private Organisation 		selectedOrganisation;
	
	private	boolean				orgsListSupported;

	public OrgListModel() {
		
		this( null );
		
	}
	
	public OrgListModel( Organisation org ) {
		super();

		checkListSupport();

//		if ( !isOrgListSupported()) {

			setSelectedOrg( org != null ? org : getApp().getSessionData().getOrg());		
			
		
	}
	
	public void initModel() {
		
		// Initial model initialization here if necesary
		
		fireListChanged();
	}
	
	public boolean isOrgListSupported() { return this.orgsListSupported; }
	public void checkListSupport() {
		
		orgsListSupported = getSecurityContext().hasViewPermissionAll( FunctionalityType.ORGS_MGMT ); 
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

	public PresenceFilterType getPresenceFilter() { return presenceFilter; }
	public void setPresenceFilter( PresenceFilterType presenceFilter ) { this.presenceFilter = presenceFilter; }

	public Organisation getSelectedOrg() { return selectedOrganisation; }
	public void setSelectedOrg( Organisation selectedOrganisation ) { 
		this.selectedOrganisation = selectedOrganisation; 
		fireSelected( selectedOrganisation );
	}

	public Collection<Organisation> getOrganisations() {
		
		if ( isOrgListSupported()) {
			return DataFacade.getInstance().list( Organisation.class );
		}
		
		return null;
/*		
		Collection<Organisation> list = new ArrayList<Organisation>();
		
		if ( getSelectedOrg() != null ) 
			list.add( getSelectedOrg());
		
		return list;
*/		
		
	}

	public Organisation add( Organisation addedOrg ) {
	
		Organisation newOrg = null;

		// Add to DB
		if ( isOrgListSupported() && addedOrg != null ) {
			
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
		if ( updatedOrg != null ) {

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
		if ( isOrgListSupported() && deletedOrg != null ) {

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
		
		return UsersFacade.getInstance().list( selectedOrganisation );
	}

/*
	// Check that responsible person exists already and add if necessary
	private OrgUser addUpdateResponsibleIfNecessary() {
		return addUpdateResponsibleIfNecessary( null );
	}
	private OrgUser addUpdateResponsibleIfNecessary( Organisation updatedOrg ) {

		OrgUser newUser = null;
		
		if ( updatedOrg == null ) 
			updatedOrg = this.selectedOrganisation;
		
		OrgUser responsibleUser = updatedOrg.getResponsible();
		
		if ( responsibleUser != null && responsibleUser.getId() < 1 ) {
			
			responsibleUser.setOrganisation( updatedOrg );
			
			UsersFacade.getInstance().setUniqueCode( responsibleUser );
//			responsibleUser.setCode( "" );			

			newUser = UsersFacade.getInstance().add( responsibleUser );
			
			if ( newUser != null ) {
				
				updatedOrg.addUser( newUser );
			}
		
		}
		
		return newUser;
		
	}
*/
}
