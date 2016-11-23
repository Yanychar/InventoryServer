package com.c2point.tools.ui.reports;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.ui.printpdf.AbstractPdfForm;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

public class ReportsView extends VerticalLayout {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( ReportsView.class.getName());
	
	private VerticalLayout 		optPanel = new VerticalLayout();

	private ComboBox 			userSelector;
	private Button				showButton;

	private ReportsViewModel	model;
	private OrgUser 			allUsersFake;
	
	public ReportsView() {
		super();

		initModel();
		allUsersFake = new OrgUser( "", "All people");
		
		initUI();
	}
	
	public void initUI() {
	
		this.setWidth( "100%" );
		this.setHeight( "100%" );
		this.setSizeFull();
		this.setMargin( true );
		this.setSpacing( true );

		addComponent( getReportSelector());
		addComponent( optPanel );
		
		showButton = new Button( "Show" );
		showButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				
				showReport();
				
			}
			
		});
		
		addComponent( showButton );
		
		
	}

	private void initModel() {
	
		model = new ReportsViewModel();
		
	}
	
	private Component getReportSelector() {

		HorizontalLayout component = new HorizontalLayout();

		OptionGroup reportType = new OptionGroup();
		reportType.setNullSelectionAllowed( false ); 
		reportType.addStyleName( "horizontalgroup" );
		reportType.setImmediate( true );		
		
		reportType.addItem( ReportType.OWNERSHIP );		
		reportType.addItem( ReportType.TEST );
		
		reportType.setItemCaption( ReportType.OWNERSHIP, "Ownership" );
		reportType.setItemCaption( ReportType.TEST, "Example" );		
		
		reportType.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange( ValueChangeEvent event ) {
				
				reportTypeChanged( event.getProperty().getValue());
				
				
			}
			
		});
		reportType.select( ReportType.OWNERSHIP );
		
		component.addComponent( reportType );
		
		return component;
	}
	
	private void clearOptionsPanel() {
		
		optPanel.removeAllComponents();
	}
	private void addOwnershipOptions() {

		userSelector = new ComboBox();
		userSelector.setInputPrompt( model.getApp().getResourceStr( "toolsmgmt.text.select.user" ));
		userSelector.setFilteringMode( FilteringMode.CONTAINS );
		userSelector.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		userSelector.setNullSelectionAllowed( false );
		userSelector.setInvalidAllowed( false );
		userSelector.setRequired( true );
		userSelector.setRequiredError( model.getApp().getResourceStr( "general.error.field.empty" ));
		userSelector.setValidationVisible( true );
		userSelector.setImmediate( true );
		
		optPanel.addComponent( userSelector );
		
		fillUserSelector( userSelector );
		
	}
	
	private void addTestOptions() {
		
	}
	
	private void reportTypeChanged( Object objType ) {
		
		if ( objType instanceof ReportType ) {
			ReportType rType = ( ReportType )objType;
			logger.debug( "Report " + rType + " selected!" );
			
			clearOptionsPanel();
			
			switch ( rType ) {
			case OWNERSHIP:
				addOwnershipOptions();
				break;
			case TEST:
				addTestOptions();
				break;
			default:
				logger.error( "Wrong ReportTypepassed: " + rType );
				break;
			
			}
			
		} else {
			logger.error( "Wrong object passed!" );
		}
		
		
	}

	private void fillUserSelector( ComboBox userSelector ) {
		if ( userSelector != null ) {
			
			userSelector.removeAllItems();

			userSelector.addItem( allUsersFake );
			userSelector.setItemCaption( allUsersFake, allUsersFake.getLastAndFirstNames());
			
			for ( OrgUser user : model.getUsers()) {

				userSelector.addItem( user );
				userSelector.setItemCaption( user, user.getLastAndFirstNames());
				
				userSelector.addItem( user );
				userSelector.setItemCaption( user, user.getLastAndFirstNames());
				
			}
			
			userSelector.setValue( allUsersFake );
			
		}
	}

	private void showReport() {
		Window subwindow = new Window(""
		// model.getApp().getResourceStr( "menu.item.report.time" )
		);

		subwindow.setModal(true);
		subwindow.setWidth("80%");
		subwindow.setHeight("90%");
		subwindow.setResizable(true);
		subwindow.center();
/*
		Embedded e = new Embedded();
		e.setType(Embedded.TYPE_BROWSER);
		// Here we create a new StreamResource which downloads our StreamSource,
		// which is our pdf.
		
		StreamResource resource = null;
		
		OrgUser selectedUser = ( OrgUser )userSelector.getValue();
		
		AbstractPdfForm form = null;
		if ( selectedUser == allUsersFake ) {

			form = model.getDocToPrint();
			
		} else {

			form = model.getDocToPrint( selectedUser );
			
		}
		
		if ( form != null ) {
			resource = form.getStream();
			
			resource.setCacheTime( 0 );
			// Set the right mime type
			resource.setMIMEType("application/pdf");
			e.setSource(resource);
			e.setSizeFull();
	
			subwindow.setContent(e);
	
			UI.getCurrent().addWindow(subwindow);
		} else {
			
		}
*/
		
		BrowserFrame browser = new BrowserFrame( "Browser" );
		
		StreamResource resource = null;
		
		OrgUser selectedUser = ( OrgUser )userSelector.getValue();
		
		final AbstractPdfForm form;
		if ( selectedUser == allUsersFake ) {

			form = model.getDocToPrint();
			
		} else {

			form = model.getDocToPrint( selectedUser );
			
		}
		
		if ( form != null ) {
			resource = form.getStream();
			
			resource.setCacheTime( 0 );
			// Set the right mime type
			resource.setMIMEType("application/pdf");
			browser.setSource( resource );
			browser.setSizeFull();
	
			subwindow.setContent( browser );
			subwindow.addCloseListener( new CloseListener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void windowClose(CloseEvent e) {

					form.deleteTmp();
					
				}
				
			});

			
			UI.getCurrent().addWindow( subwindow );
			
		}
	}
}
