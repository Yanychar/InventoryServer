package com.c2point.tools.ui;

import javax.swing.event.EventListenerList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.InventoryUI;
import com.c2point.tools.entity.access.FunctionalityType;
import com.c2point.tools.entity.access.SecurityContext;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.person.OrgUser;
import com.vaadin.ui.UI;

public class AbstractModel  {
	
	private static Logger logger = LogManager.getLogger( AbstractModel.class.getName());

	protected InventoryUI 	app;
	
	protected EventListenerList	listenerList; 
	
	public enum EditModeType { VIEW, EDIT, ADD, COPY };
	
	private EditModeType	editMode;
	
	// Allows particular features according to the access rights
	private boolean		allowOtherCompanies = false; 
	private boolean		allowEdit = false; 
	

	public AbstractModel() {
		
		listenerList = new EventListenerList();
		this.app = ( InventoryUI )UI.getCurrent();
		
		setEditMode( EditModeType.VIEW );
	}

	public InventoryUI getApp() {
		
		return app;
		
		
	}

	public void setApp( InventoryUI app ) {
		logger.debug( "setApp( app ). app = " + app  );
		this.app = app;
	}
	
	public OrgUser getSessionOwner() { return app.getSessionData().getOrgUser(); }

/*	
	public Organisation getOrg() { 
	
		if ( app.getSessionData().getOrg() != null ) {
			
			return app.getSessionData().getOrg();
		}

		return getSessionOwner().getOrganisation();
		
	}
*/	
	public SecurityContext getSecurityContext() { return app.getSessionData().getContext(); }

	public EditModeType getEditMode() { return this.editMode; }
	public boolean isEditMode() { return ( this.editMode != EditModeType.VIEW ); }
	public void setEditMode( EditModeType editMode ) { this.editMode = editMode; }
	public void setAddMode() { setEditMode( EditModeType.ADD ); }
	public void setEditMode() { setEditMode( EditModeType.EDIT ); }
	public void setViewMode() { setEditMode( EditModeType.VIEW ); }
	public void setCopyMode() { setEditMode( EditModeType.COPY ); }
	public void clearEditMode() { setEditMode( EditModeType.VIEW ); }

	public void setupAccess( FunctionalityType fType, Organisation org ) {

		this.allowOtherCompanies = getApp().getSessionData().getContext().hasViewPermissionAll( fType );
		this.allowEdit = getApp().getSessionData().getContext().hasEditPermission( 
								FunctionalityType.TOOLS_MGMT, app.getSessionOwner(), org );
		
	}
	
	public boolean allowsOtherCompanies() { return this.allowOtherCompanies; }
	public boolean allowsToEdit() { return this.allowEdit; }
	

	
	
	
}
