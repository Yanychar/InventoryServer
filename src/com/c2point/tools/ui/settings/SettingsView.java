package com.c2point.tools.ui.settings;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.ui.personnelmgmt.PersonnelListView;
import com.c2point.tools.ui.toolsmgmt.ToolsListView;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TabSheet;
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

		// Setup menu on top
/*		
		MenuBar menubar = new MenuBar();
		this.addComponent(menubar);
		
		MenuItem personnelItem = menubar.addItem( "Personnel",  null,  null ); 
*/
		TabSheet tabsheet = new SettingsTabSheet();		
		tabsheet.addTab( new VerticalLayout(), "Organisations" );
		tabsheet.addTab( new PersonnelListView(), "Personnel" );
		tabsheet.addTab( new ToolsListView(), "Tools" );
		
				
		this.addComponent( tabsheet );
		
	}

	private void initModel() {
		
	}
	
	
}
