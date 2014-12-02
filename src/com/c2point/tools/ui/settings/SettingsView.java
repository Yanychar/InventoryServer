package com.c2point.tools.ui.settings;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.InventoryUI;
import com.c2point.tools.ui.personnelmgmt.StuffManagementView;
import com.c2point.tools.ui.toolsmgmt.ToolsManagementView;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

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
		
		tabsheet.addTab( new VerticalLayout(), app.getResourceStr( "settings.tabname.organisation" ));
		tabsheet.addTab( new StuffManagementView(), app.getResourceStr( "settings.tabname.personnel" ));
		tabsheet.addTab( new ToolsManagementView(), app.getResourceStr( "settings.tabname.tools" ));
		
				
		this.addComponent( tabsheet );
		
	}

	private void initModel() {
		
	}
	
	
}
