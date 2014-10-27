package com.c2point.tools.ui.repositoryview.handlers;

import com.c2point.tools.ui.repositoryview.ToolsListModel;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;

public abstract class AbstractHandler implements CommandListener {

	
	private ToolsListModel	model;
	
	public AbstractHandler( ToolsListModel model ) {
		
		setModel( model );
		
	}
	
	public void setModel( ToolsListModel model ) { this.model = model; }
	public ToolsListModel getModel() { return model; }
	
}
