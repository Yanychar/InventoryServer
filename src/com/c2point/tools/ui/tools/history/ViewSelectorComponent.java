package com.c2point.tools.ui.tools.history;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.tool.Tool;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;

public class ViewSelectorComponent extends Panel {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( ViewSelectorComponent.class.getName());

	private ToolsHistoryListModel		model;
	
	private OptionGroup					viewSelector;
	private ComboBox					itemSelection;

	private DateField 					startDF;
	private DateField 					endDF;
	
//	private Button						searchButton;
	
	public ViewSelectorComponent( ToolsHistoryListModel model ) {
		super();
		
		this.model = model;
		
		initUI();
	}
	
	private void initUI() {
		
		VerticalLayout content = new VerticalLayout();
		this.setContent( content );
		
		content.setWidth( "100%" );
		content.setSpacing( true );
		content.setMargin( true );
		
		viewSelector = new OptionGroup( model.getApp().getResourceStr( "trnsmgmt.label.viewtype" ) + ":" );
		viewSelector.addStyleName( "horizontalgroup" );
		viewSelector.setWidth( "100%" );
		viewSelector.setImmediate(true); 
		
		viewSelector.addItem( ToolsHistoryListModel.ViewMode.PERSONNEL );
		viewSelector.setItemCaption( ToolsHistoryListModel.ViewMode.PERSONNEL, 
										model.getApp().getResourceStr( "trnsmgmt.label.personnel" ));
		
		viewSelector.addItem( ToolsHistoryListModel.ViewMode.TOOLS );
		viewSelector.setItemCaption( ToolsHistoryListModel.ViewMode.TOOLS, 
										model.getApp().getResourceStr( "trnsmgmt.label.tools" ));
		
		itemSelection = new ComboBox( "Select item: " );
		itemSelection.setWidth( "40ex" );
		itemSelection.setFilteringMode( FilteringMode.CONTAINS );
		itemSelection.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		itemSelection.setNullSelectionAllowed( false );
		itemSelection.setInvalidAllowed( false );
		itemSelection.setImmediate( true );

		itemSelection.setEnabled( false );
		
		SimpleDateFormat dFormat = new SimpleDateFormat( "dd.MM.yyyy" );
		
		startDF = new DateField();
		startDF.setLocale( model.getApp().getSessionData().getLocale());
		startDF.setDateFormat( dFormat.toPattern());
		startDF.setResolution( Resolution.DAY );
		startDF.setImmediate(true);
		
		endDF = new DateField();
		endDF.setLocale( model.getApp().getSessionData().getLocale());
		endDF.setDateFormat( dFormat.toPattern());
		endDF.setResolution( Resolution.DAY );
		endDF.setImmediate(true);

		HorizontalLayout dateLine = new HorizontalLayout();
		dateLine.setSpacing( true ); // and spacing between components		

		dateLine.addComponent( new Label( model.getApp().getResourceStr( "trnsmgmt.label.from" ) + ":" ));
		dateLine.addComponent( startDF );
		dateLine.addComponent( new Label( "  " + model.getApp().getResourceStr( "trnsmgmt.label.to" ) + ":" ));
		dateLine.addComponent( endDF );
		
		content.addComponent( viewSelector );
		content.addComponent( itemSelection );
		content.addComponent( dateLine );
		
		initValues();
		
		viewSelector.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange( ValueChangeEvent event ) {
				
				// View Type was changed. The whole TransactionManagementView will be updated
				viewTypeWasChanged(( ToolsHistoryListModel.ViewMode )event.getProperty().getValue());
				
			}
			
		});
		
		itemSelection.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange( ValueChangeEvent event ) {

				if ( model.getViewMode() == ToolsHistoryListModel.ViewMode.PERSONNEL ) {
					model.setSelectedUser( event.getProperty().getValue());
				} else if ( model.getViewMode() == ToolsHistoryListModel.ViewMode.TOOLS ) {
					model.setSelectedTool( event.getProperty().getValue());
				}
				
			}
			
		});

		startDF.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			public void valueChange( ValueChangeEvent event ) {

				model.setDateStart(( Date )event.getProperty().getValue());
			}
		});
		
		endDF.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			public void valueChange( ValueChangeEvent event ) {

				model.setDateEnd(( Date )event.getProperty().getValue());
				
			}
		});

	}
	
	private void initValues() {

		// Set dates
		startDF.setValue( model.getDateStart());
		endDF.setValue( model.getDateEnd());
		
	}

	private void setSelectionItemCaption() {
		
		itemSelection.setEnabled( true );
		
		String str = "Select item: ";
		
		switch ( model.getViewMode()) {
			case PERSONNEL:
				str = model.getApp().getResourceStr( "trnsmgmt.label.select.personnel" ) + ":";
				break;
			case TOOLS:
				str = model.getApp().getResourceStr( "trnsmgmt.label.select.tool" ) + ":";
				break;
		}
		
		itemSelection.setCaption( str );
	}
	
	private void setSelectionItemContent() {
		
		switch ( model.getViewMode()) {
			case PERSONNEL:
				setPersonnelContent();
				break;
			case TOOLS:
				setProjectContent();
				break;
		}
	}

	/*
	 * Set content of User List ComboBox and select user from model
	 */
	private void setPersonnelContent() {
		
		// Remove old content
		try {
			itemSelection.removeAllItems();
		} catch ( Exception e ) {
			
		}

		// Fill Personnel ComboBox 
		logger.debug( "List of personnel is empty. Will be read" );

		// Read user list
		Collection<OrgUser> usersList = model.getUsers();
		OrgUser tmpSelectedUser = null;
		
		if ( usersList != null ) {
			// Fill new ComboBox content
			for ( OrgUser user : usersList ) {
				if ( user != null ) {

					itemSelection.addItem( user );
					itemSelection.setItemCaption( user, user.getLastAndFirstNames());
				}
			}
			
			// Initial selection will be set to session owner
			tmpSelectedUser = model.getApp().getSessionOwner();
			
		}

		
		// If model has user selected than try to select it

		try {
			itemSelection.setValue( tmpSelectedUser );
		} catch ( Exception e ) {
			logger.debug( "Cannot select proper user" );
		}
			
		
	}

	private void setProjectContent() {
		
		// Remove old content
		try {
			itemSelection.removeAllItems();
		} catch ( Exception e ) {
			
		}

		// Fill Tools ComboBox 
		logger.debug( "List of Tools is empty. Will be read" );

		// Read tools list
		Collection<Tool> toolsList = model.getTools();
		Tool tmpSelectedTool = null;
		
		if ( toolsList != null ) {
			// Fill new ComboBox content
			for ( Tool tool : toolsList ) {
				if ( tool != null ) {

					itemSelection.addItem( tool );
					itemSelection.setItemCaption( tool, tool.getFullName());
				}
			}
			
			// Initial selection will be set to first item
			tmpSelectedTool = ( Tool ) itemSelection.getItemIds().iterator().next();
			
		}
		
		// If model has user selected than try to select it
		try {
			itemSelection.setValue( tmpSelectedTool );
		} catch ( Exception e ) {
			logger.debug( "Cannot select proper Tool" );
		}
			
	}

	private void viewTypeWasChanged( ToolsHistoryListModel.ViewMode mode ) {
	
		model.setViewMode( mode, false );

		// Update itemSelection caption
		setSelectionItemCaption();
		
		// Update itemSelection content
		setSelectionItemContent();
		
	}

	public void selectViewMode( ToolsHistoryListModel.ViewMode mode ) {
		
		viewSelector.setValue( mode );
	}
}
