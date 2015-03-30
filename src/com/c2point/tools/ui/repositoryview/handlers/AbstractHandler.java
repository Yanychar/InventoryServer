package com.c2point.tools.ui.repositoryview.handlers;

import com.c2point.tools.ui.repositoryview.NewToolsListModel;

public abstract class AbstractHandler implements CommandListener {

	
	private NewToolsListModel	model;
	
	public AbstractHandler( NewToolsListModel model ) {
		
		setModel( model );
		
	}
	
	public void setModel( NewToolsListModel model ) { this.model = model; }
	public NewToolsListModel getModel() { return model; }
	
}
