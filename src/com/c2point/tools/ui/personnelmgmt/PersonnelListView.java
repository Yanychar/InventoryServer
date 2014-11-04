package com.c2point.tools.ui.personnelmgmt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.ui.AbstractMainView;
import com.c2point.tools.ui.upload.ImportComponent;
import com.c2point.tools.ui.upload.PersonnelImportProcessor;
import com.c2point.tools.ui.upload.UploadComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class PersonnelListView extends AbstractMainView {
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( PersonnelListView.class.getName());

	
	private StuffListModel		model;

	private ComboBox			orgSelector;
	
	private HorizontalLayout	toolbar;
	private ComboBox			filter;
	private UploadComponent		importButton;
	private Button				exportButton;
	
	private StuffListView 		stuffList;

	private StuffView			stuffView;
	
	public PersonnelListView() {
		super();

	}

	@Override
	protected void initUI() {
		
		this.setSizeFull();
		this.setSpacing( true );

		this.model = new StuffListModel();
		
		initToolbar();
		initStuffListView();
		initStuffView();
		
		VerticalLayout vtSplit = new VerticalLayout(); 
		HorizontalSplitPanel hzSplit = new HorizontalSplitPanel();
		

		hzSplit.addComponent( stuffList );
		hzSplit.addComponent( stuffView );
		
		if ( model.isSuperUser()) {
		
			initOrgSelector();
			vtSplit.addComponent( orgSelector );
			
		}
		
		vtSplit.addComponent( toolbar );
		vtSplit.addComponent( hzSplit );
		
		
		this.addComponent( vtSplit );
		this.setExpandRatio( vtSplit, 1f );
		
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
		
	}
	
	private void initToolbar() {
		
		toolbar = new HorizontalLayout();
		toolbar.setWidth( "100%" );
		toolbar.setSpacing( true );
		toolbar.setMargin( true );
		
		Label filterLabel = new Label( "Filter:" );
		filter = new ComboBox();
		
		importButton = new UploadComponent( this.model.getApp().getResourceStr( "general.button.import" ));
		PersonnelImportProcessor processor = new PersonnelImportProcessor( model, importButton.getUploadFile());
		
		ImportComponent importComponent = new ImportComponent( processor );

		importButton.addStartedListener( importComponent );
		importButton.addSucceededListener( importComponent );
		importButton.addFailedListener( importComponent );
		importButton.addProgressListener( importComponent );

		exportButton = new Button( "Export" );
		exportButton.setEnabled( false );
		
		Label glue = new Label( " " );
		glue.setHeight("100%");

		
		toolbar.addComponent( filterLabel );
		toolbar.addComponent( filter );
		toolbar.addComponent( glue );
		toolbar.addComponent( importButton );
		toolbar.addComponent( exportButton );
		
		toolbar .setExpandRatio( glue, 1.0f );
		
		
	}
	
	private void initStuffListView() {

		if ( logger.isDebugEnabled()) logger.debug( "Data from model will be read!" );
		
		stuffList = new StuffListView( this.model );
		
		
	}
	
	private void initStuffView() {

		stuffView = new StuffView( this.model );
		
		
	}

}
