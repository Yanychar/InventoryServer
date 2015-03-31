package com.c2point.tools.ui.repositoryview;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.ui.AbstractMainView;
import com.vaadin.ui.HorizontalSplitPanel;

public class RepositoryManagementView extends AbstractMainView {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( RepositoryManagementView.class.getName());

	
	private ToolsListModel	model;

	private ToolsListView	toolsList;
	private ToolItemView		toolItemView;
	
	public RepositoryManagementView() {
		super();

	}
	
	@Override
	protected void initUI() {

		this.setSizeFull();
		this.setSpacing( true );

		this.model = new ToolsListModel();
		
		initToolItemsListView();
		initItemView();
		
		HorizontalSplitPanel hzSplit = new HorizontalSplitPanel();
		

		hzSplit.addComponent( toolsList );
		hzSplit.addComponent( toolItemView );

		hzSplit.setSplitPosition( 65, Unit.PERCENTAGE );
		
/*		
		vtSplit.addComponent( toolbar );
		vtSplit.addComponent( hzSplit );
		
		vtSplit.setHeight( "100%" );
		vtSplit.setExpandRatio( hzSplit, 1f );
*/		
		this.addComponent( hzSplit );
		
		this.model.init();
		
	}

	@Override
	protected void initDataAtStart() {

		
	}

	@Override
	protected void initDataReturn() {

		
	}

	private void initToolItemsListView() {

		if ( logger.isDebugEnabled()) logger.debug( "Data from model will be read!" );
		
		toolsList = new ToolsListView( this.model );
		
		
	}
	
	private void initItemView() {

		toolItemView = new ToolItemView( this.model );
		
		
	}
	
}
