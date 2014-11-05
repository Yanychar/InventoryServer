package com.c2point.tools.ui.toolsmgmt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.ui.ListWithSearchComponent;
import com.vaadin.ui.Table;

public class CatAndToolItemsListView extends ListWithSearchComponent implements ToolItemChangedListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( CatAndToolItemsListView.class.getName());

	private ToolsManagementModel	model;

	private Table			itemsTable;
	
	public CatAndToolItemsListView( ToolsManagementModel model ) {
		super( true );
		this.model = model;

		initView();

		model.addChangedListener( this );
		
	}

	private void initView() {

		setSizeFull();

		setMargin( true );
//		setSpacing( true );
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
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
