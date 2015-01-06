package com.c2point.tools.ui.transactions;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.person.OrgUser;
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

	private TransactionsListModel		model;
	
	private OptionGroup					viewSelector;
	private ComboBox					itemSelection;

	private DateField 					startDF;
	private DateField 					endDF;
	
//	private Button						searchButton;
	
	public ViewSelectorComponent( TransactionsListModel model ) {
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
		
		viewSelector = new OptionGroup( model.getApp().getResourceStr( "trnsmgmt.label.viewtype" ));
		viewSelector.addStyleName( "horizontalgroup" );
		viewSelector.setWidth( "100%" );
		viewSelector.setImmediate(true); 
		
		viewSelector.addItem( TransactionsListModel.ViewMode.PERSONNEL );
		viewSelector.setItemCaption( TransactionsListModel.ViewMode.PERSONNEL, 
										model.getApp().getResourceStr( "trnsmgmt.label.personnel" ));
		
		viewSelector.addItem( TransactionsListModel.ViewMode.TOOLS );
		viewSelector.setItemCaption( TransactionsListModel.ViewMode.TOOLS, 
										model.getApp().getResourceStr( "trnsmgmt.label.tools" ));
		
		itemSelection = new ComboBox( "Select item: " );
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
		startDF.setEnabled( false );
		
		endDF = new DateField();
		endDF.setLocale( model.getApp().getSessionData().getLocale());
		endDF.setDateFormat( dFormat.toPattern());
		endDF.setResolution( Resolution.DAY );
		endDF.setImmediate(true);
		endDF.setEnabled( false );

		HorizontalLayout dateLine = new HorizontalLayout();
		dateLine.setMargin( true ); // we want a margin
		dateLine.setSpacing( true ); // and spacing between components		
		dateLine.setWidth( "100%" );
		
		Label glue = new Label( " " );
		glue.setHeight("100%");

		dateLine.addComponent( new Label( model.getApp().getResourceStr( "trnsmgmt.label.from")));
		dateLine.addComponent( startDF );
		dateLine.addComponent( new Label( "  " + model.getApp().getResourceStr( "trnsmgmt.label.to")));
		dateLine.addComponent( endDF );
		dateLine.addComponent( glue );

		dateLine .setExpandRatio( glue, 1.0f );
		
		content.addComponent( viewSelector );
		content.addComponent( itemSelection );
		content.addComponent( dateLine );
		
		dateToView();
		
		viewSelector.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange( ValueChangeEvent event) {
				
				model.setViewMode( event.getProperty().getValue());
				
//				updateUI();
			}
			
		});
		
		itemSelection.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange( ValueChangeEvent event ) {

				model.setSelectedUser( event.getProperty().getValue());
				
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

	private void dateToView() {
		
		// Update View selector if necessary
//		if ( viewSelector.getValue() != model.getViewMode()) {
			viewSelector.setValue( model.getViewMode());
//		}
		
		// Update itemSelection caption
		setSelectionItemCaption();
		
		// Update itemSelection content
		setSelectionItemContent();
		
		// Set dates
		startDF.setEnabled( true );
		endDF.setEnabled( true );
		
		startDF.setValue( model.getDateStart());
		endDF.setValue( model.getDateEnd());
		
	}

	private void setSelectionItemCaption() {
		
		itemSelection.setEnabled( true );
		
		String str = "Select item: ";
		
		switch ( model.getViewMode()) {
			case PERSONNEL:
				str = model.getApp().getResourceStr( "trnsmgmt.label.select.personnel");
				break;
			case TOOLS:
				str = model.getApp().getResourceStr( "trnsmgmt.label.select.tool");
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
		
		// Fill Personnel ComboBox if it was not read yet 
		if ( itemSelection.size() == 0 ) {
			logger.debug( "List of personnel is empty. Will be read initially" );

			// Remove old content
			itemSelection.removeAllItems();
			// Read user list
			Collection<OrgUser> usersList = model.getUsers();
		
			if ( usersList != null ) {
				// Fill new ComboBox content
				for ( OrgUser user : usersList ) {
					if ( user != null ) {
	
						itemSelection.addItem( user );
						itemSelection.setItemCaption( user, user.getLastAndFirstNames());
					}
				}
				
				model.setSelectedUser( model.getApp().getSessionOwner());
				
			}
		} else {
			logger.debug( "List of personnel is NOT empty. Not necessary to read again" );
		}
		
		// If model has user selected than try to select it
		if ( itemSelection.getValue() != model.getSelectedUser()) {
			logger.debug( "User selection in model different. Will be set in view" );

			
//			if ( model.getSelectedUser() != null ) {
				try {
					itemSelection.setValue( model.getSelectedUser());
//					itemSelection.setValue( itemSelection.getItemIds().iterator().next());
				} catch ( Exception e ) {
					logger.debug( "Cannot select proper user" );
				}
//			}
			
		} else if ( model.getSelectedUser() == null ) {
			logger.debug( "User selection in model == null. Initial selection will be set to session owner" );
//			model.setSelectedUser( model.getApp().getSessionOwner());
//			itemSelection.setValue( model.getApp().getSessionOwner());
		}
		
		
	}

	private void setProjectContent() {
		
		itemSelection.removeAllItems();
		
//		Collection<Tool> usersList = model.getUsers();
		
	
	}
	
}
