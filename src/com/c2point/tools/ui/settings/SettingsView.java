package com.c2point.tools.ui.settings;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.InventoryUI;
import com.c2point.tools.entity.access.FunctionalityType;
import com.c2point.tools.entity.access.SecurityContext;
import com.c2point.tools.ui.orgmgmt.OrgManagementView;
import com.c2point.tools.ui.personnelmgmt.StuffManagementView;
import com.c2point.tools.ui.toolsmgmt.ToolsManagementView;
import com.c2point.tools.ui.transactions.TransactionsManagementView;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;

public class SettingsView extends HorizontalLayout {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( SettingsView.class.getName());

	

	public SettingsView() {
		super();

		initModel();
		
		initUI();
	}
	
	public void initUI() {
	
		setWidth( "100%" );
		this.setHeight( "100%" );
		this.setSizeFull();

		// Setup menu on top
/*		
		MenuBar menubar = new MenuBar();
		this.addComponent(menubar);
		
		MenuItem personnelItem = menubar.addItem( "Personnel",  null,  null ); 
*/
		
		TabSheet tabsheet = new SettingsTabSheet();
		InventoryUI app = ( InventoryUI )UI.getCurrent();

		SecurityContext context = app.getSessionData().getContext();
		
		if ( context.hasViewPermissionMgmt( FunctionalityType.ORGS_MGMT )) {
				
			tabsheet.addTab( new OrgManagementView(), app.getResourceStr( "settings.tabname.organisation" ));
		}
		
		if ( context.hasViewPermissionMgmt( FunctionalityType.USERS_MGMT )) {
			tabsheet.addTab( new StuffManagementView(), app.getResourceStr( "settings.tabname.personnel" ));
		}
		
		if ( context.hasViewPermissionMgmt( FunctionalityType.TOOLS_MGMT )) {
			tabsheet.addTab( new ToolsManagementView(), app.getResourceStr( "settings.tabname.tools", "Tools" ));
		}
		
		if ( context.hasViewPermissionMgmt( FunctionalityType.TRN_MGMT )) {
			tabsheet.addTab( new TransactionsManagementView(), app.getResourceStr( "settings.tabname.transactions", "Transactions" ));
		}
		
				
		this.addComponent( tabsheet );
		
	}

	private void initModel() {
		
	}
	
	
}
