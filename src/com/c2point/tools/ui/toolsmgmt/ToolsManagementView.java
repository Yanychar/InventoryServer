package com.c2point.tools.ui.toolsmgmt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.ui.AbstractMainView;
import com.c2point.tools.ui.upload.ImportComponent;
import com.c2point.tools.ui.upload.UploadComponent;
import com.c2point.tools.ui.upload.tools.ToolItemsImportProcessor;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class ToolsManagementView extends AbstractMainView {
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( ToolsManagementView.class.getName());

	
	private ToolsListModel		model;

	private ComboBox			orgSelector;
	
	private HorizontalLayout	toolbar;
	private UploadComponent		importButton;
	private Button				exportButton;
	
	private ToolsListView		toolsList;
	private ToolItemView		toolItemView;


	
	public ToolsManagementView() {
		super();

	}

	@Override
	protected void initUI() {
		
		this.setSizeFull();
		this.setSpacing( true );

		this.model = new ToolsListModel();
		
		initToolbar();
		initToolItemsListView();
		initItemView();
		
		VerticalLayout vtSplit = new VerticalLayout(); 
		HorizontalSplitPanel hzSplit = new HorizontalSplitPanel();
		

		hzSplit.addComponent( toolsList );
		hzSplit.addComponent( toolItemView );

		hzSplit.setSplitPosition( 65, Unit.PERCENTAGE );
		
		if ( model.isSuperUser()) {
		
			initOrgSelector();
			vtSplit.addComponent( orgSelector );
			
		}
		
		vtSplit.addComponent( toolbar );
		vtSplit.addComponent( hzSplit );
		
		vtSplit.setHeight( "100%" );
		vtSplit.setExpandRatio( hzSplit, 1f );
		
		this.addComponent( vtSplit );
		
	}

	@Override
	protected void initDataAtStart() {

		
		this.model.initModel();
		
	}

	@Override
	protected void initDataReturn() {
		// TODO Auto-generated method stub
		
	}

	private void initOrgSelector() {
		
		orgSelector = new ComboBox();
		
		// TODO
		// 1. Fill combo box
		// Select activi item: model.getsession.getOrg()
		
		// For now
		orgSelector.setVisible( false );
		
	}
	
	private void initToolbar() {
		
		toolbar = new HorizontalLayout();
		toolbar.setWidth( "100%" );
		toolbar.setSpacing( true );
		toolbar.setMargin( true );
		
		importButton = new UploadComponent( this.model.getApp().getResourceStr( "toolsmgmt.button.import" ));
		ToolItemsImportProcessor processor = new ToolItemsImportProcessor( model, importButton.getUploadFile());
		
		ImportComponent importComponent = new ImportComponent( processor );

		importButton.addStartedListener( importComponent );
		importButton.addSucceededListener( importComponent );
		importButton.addFailedListener( importComponent );
		importButton.addProgressListener( importComponent );

		exportButton = new Button( this.model.getApp().getResourceStr( "toolsmgmt.button.export" ));
		exportButton.setEnabled( false );
		
		Label glue = new Label( " " );
		glue.setHeight("100%");

		
		toolbar.addComponent( glue );
		
		toolbar.addComponent( importButton );
		toolbar.addComponent( exportButton );
		
		toolbar .setExpandRatio( glue, 1.0f );
		
		
	}
	
	private void initToolItemsListView() {

		if ( logger.isDebugEnabled()) logger.debug( "Data from model will be read!" );
		
		toolsList = new ToolsListView( this.model );
		
		
	}
	
	private void initItemView() {

		toolItemView = new ToolItemView( this.model );
		
		
	}


	
}
