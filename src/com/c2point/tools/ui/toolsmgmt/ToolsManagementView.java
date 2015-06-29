package com.c2point.tools.ui.toolsmgmt;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.OrganisationFacade;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.ui.AbstractMainView;
import com.c2point.tools.ui.upload.ImportComponent;
import com.c2point.tools.ui.upload.UploadComponent;
import com.c2point.tools.ui.upload.tools.ToolItemsImportProcessor;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;

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
		vtSplit.setWidth( "100%" );
		vtSplit.setSpacing( true );
		vtSplit.setMargin( true );
		
		
		HorizontalSplitPanel hzSplit = new HorizontalSplitPanel();
		

		hzSplit.addComponent( toolsList );
		hzSplit.addComponent( toolItemView );

		hzSplit.setSplitPosition( 65, Unit.PERCENTAGE );
		
		vtSplit.addComponent( toolbar );
		vtSplit.addComponent( hzSplit );
		
		vtSplit.setHeight( "100%" );
		vtSplit.setExpandRatio( hzSplit, 1f );
		
		this.addComponent( vtSplit );
		
		if ( model.allowsOtherCompanies()) { 
			orgSelector.addValueChangeListener( new ValueChangeListener() {
				private static final long serialVersionUID = 1L;
	
				@Override
				public void valueChange( ValueChangeEvent event ) {
	
					model.setSelectedOrg(( Organisation ) event.getProperty().getValue());
					
					importButton.setEnabled( model.allowsToEdit());
					exportButton.setEnabled( model.allowsToEdit());
					
				}
				
			});
		}
	}

	@Override
	protected void initDataAtStart() {

		
		this.model.initModel();
		
	}

	@Override
	protected void initDataReturn() {
		// TODO Auto-generated method stub
		
	}

	private void initToolbar() {

		toolbar = new HorizontalLayout();
		toolbar.setWidth( "100%" );
		toolbar.setSpacing( true );
		toolbar.setMargin( true );

		if ( model.allowsOtherCompanies()) {
		
			orgSelector = new ComboBox( model.getApp().getResourceStr( "trnsmgmt.label.select.org", "Select Company" ) + ": " );
			
			// User can see Transactions for all companies
			
			orgSelector.setWidth( "40ex" );
			orgSelector.setFilteringMode( FilteringMode.CONTAINS );
			orgSelector.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
			orgSelector.setNullSelectionAllowed( false );
			orgSelector.setInvalidAllowed( false );
			orgSelector.setImmediate( true );

			Collection<Organisation> orgsList = 
					OrganisationFacade.getInstance().getOrganisations();
			
			if ( orgsList != null && orgsList.size() > 0 ) {

				for ( Organisation org : orgsList ) {
					if ( org != null ) {

						orgSelector.addItem( org );
						orgSelector.setItemCaption( org, org.getName());
					}
				}
				orgSelector.select( model.getSelectedOrg());
				
				
			}
				
		}
		
		
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

		
		// Add Organisation Selector if necessary
		if ( model.allowsOtherCompanies()) {
			toolbar.addComponent( orgSelector );
		}

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
