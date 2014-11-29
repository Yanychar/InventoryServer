package com.c2point.tools.ui.toolsmgmt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.repository.ToolItem;
import com.vaadin.ui.FormLayout;

public class ToolItemView extends FormLayout implements ToolItemChangedListener {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( ToolItemView.class.getName());

	private ToolsListModel	model;
	
	
	
	private boolean		editMode;	 
	private boolean		editedFlag;
	
	public ToolItemView( ToolsListModel model ) {
		super();
		
		setModel( model );
		
		initView();
		
		model.addChangedListener( this );
		
		setEditMode( false );
	}

	private void initView() {

		setSpacing( true );
		this.setMargin( true );
		setSizeUndefined();
		
		
	}
	
	public ToolsListModel getModel() { return model; }
	public void setModel( ToolsListModel model ) { this.model = model; }

	public boolean isEditMode() { return this.editMode; }
	public void setEditMode( boolean editMode ) { this.editMode = editMode; }
	public void swipeEditMode() { setEditMode( !isEditMode()); }
		
	public boolean isEditedFlag() { return editedFlag;}
	public void setEditedFlag(boolean editedFlag) {this.editedFlag = editedFlag; }
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public void wasAdded(ToolItem item) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void wasChanged(ToolItem item) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void wasDeleted(ToolItem item) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void wholeListChanged() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void currentWasSet(ToolItem item) {
		// TODO Auto-generated method stub
		
	}

}
