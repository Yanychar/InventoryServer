package com.c2point.tools.ui.personnelmgmt;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.OrganisationFacade;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.ui.AbstractMainView;
import com.c2point.tools.ui.upload.ImportComponent;
import com.c2point.tools.ui.upload.UploadComponent;
import com.c2point.tools.ui.upload.personnel.PersonnelImportProcessor;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;

public class StuffManagementView extends AbstractMainView {

	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( StuffManagementView.class.getName());

	private StuffListModel		model;

	private ComboBox			orgSelector;

	private Panel				toolbar;
	private UploadComponent		importButton;
	private Button				exportButton;

	private StuffListView 		stuffList;
	private DetailsView			stuffView;

	public StuffManagementView() {
		super();

	}

	@Override
	protected void initUI() {

		this.setSizeFull();
		this.setSpacing( true );
		this.setWidth( "100%" );

		this.model = new StuffListModel();

		initToolbar();
		initStuffListView();
		initStuffView();

		HorizontalSplitPanel hzSplit = new HorizontalSplitPanel();
		hzSplit.setSplitPosition( 65, Unit.PERCENTAGE );
		hzSplit.setSizeFull();
		hzSplit.setLocked( false );

		hzSplit.addComponent( stuffList );
		hzSplit.addComponent( stuffView );

		this.addComponent( toolbar );
		this.addComponent( hzSplit );
		
		this.setExpandRatio( hzSplit, 1f );
		
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

		toolbar = new Panel();
		
		HorizontalLayout content = new HorizontalLayout();
		toolbar.setContent( content );
		
		content.setWidth( "100%" );
		content.setSpacing( true );
		content.setMargin( true );

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

		// Add Organisation Selector if necessary
		if ( model.allowsOtherCompanies()) {
			content.addComponent( orgSelector );
		}

		content.addComponent( glue );
	
		content.addComponent( importButton );
		content.addComponent( exportButton );
		
		content .setExpandRatio( glue, 1.0f );
		
		importButton.setEnabled( model.allowsToEdit());
		exportButton.setEnabled( model.allowsToEdit());

	}

	private void initStuffListView() {

		if ( logger.isDebugEnabled()) logger.debug( "Data from model will be read!" );

		stuffList = new StuffListView( this.model );


	}

	private void initStuffView() {

		stuffView = new DetailsView( this.model );


	}

}
