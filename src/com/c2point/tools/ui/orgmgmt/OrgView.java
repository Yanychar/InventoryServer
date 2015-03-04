package com.c2point.tools.ui.orgmgmt;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;

import com.c2point.tools.access.FunctionalityType;
import com.c2point.tools.entity.organisation.Organisation;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class OrgView extends GridLayout implements OrgChangedListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( OrgView.class.getName());


	private OrgListModel	model;

	private TextField	code;
	private TextField 	name;
    private TextField	tunnus;

    // Address fields
	private TextField	street;
//	private TextField	poBox;
	private TextField	index;
	private TextField	city;
	private ComboBox	countryCode;

    private TextField 	phone;
    private TextField 	email;

    private TextArea	info;
	
	private Button		editcloseButton;
	private Button		deleteButton;

	private boolean		editedFlag;
	private Organisation shownOrg;

	public OrgView( OrgListModel model ) {
		super( 4, 7 );
		
		setModel( model );

		initView();

		model.addChangedListener( this );

		model.clearEditMode();
		
	}

	private void initView() {

		setSpacing( true );
		this.setMargin( true );
		setSizeUndefined();
		
		code = new TextField();
		code.setRequired( true );
		code.setImmediate( true );
		
        name = new TextField();
        name.setTabIndex( 2 );
        name.setWidth("100%");
        name.setNullRepresentation( "Enter Company Name ..." ); 
        name.setRequired( true );
        name.setValidationVisible( true );
		name.setImmediate( true );

        tunnus = new TextField();
        tunnus.setTabIndex( 3 );
        tunnus.setWidth( "10em" );
        tunnus.setNullRepresentation( "Enter ID ..." ); 
        tunnus.setImmediate(true);
		
        addComponent( new Label( model.getApp().getResourceStr( "general.caption.code" ) + ":" ),	0,  0 ); 
        addComponent( new Label( model.getApp().getResourceStr( "general.caption.name" ) + ":" ),	0,  1 );
        addComponent( new Label( model.getApp().getResourceStr( "organisation.caption.tunnus" ) + ":" ),	2,  1 );
		
		addComponent( code,		1,  0 );
		addComponent( name,		1,  1 );
		addComponent( tunnus,	3,  1 );

		Label separator = new Label( "<hr/>", ContentMode.HTML );
		separator.setWidth( "100%" );
		addComponent( separator, 0, 2 );

        street = new TextField();
        street.setTabIndex( 4 );
        street.setWidth("100%");
        street.setNullRepresentation( "Street address ..." ); 
        street.setRequired( false );
        street.setValidationVisible( true );
        street.setImmediate(true);

        index = new TextField();
        index.setTabIndex( 5 );
        index.setWidth( "10em" );
        index.setNullRepresentation( "Postbox ..." ); 
        index.setRequired( false );
        index.setValidationVisible( true );
        index.setImmediate(true);
		
        city = new TextField();
        city.setTabIndex( 6 );
        city.setWidth( "20em" );
        city.setNullRepresentation( "City ..." ); 
        city.setRequired( false );
        city.setValidationVisible( true );
        city.setImmediate(true);
		
		
        addComponent( new Label( model.getApp().getResourceStr( "organisation.caption.street" ) + ":" ),	0,  3 );
        addComponent( new Label( model.getApp().getResourceStr( "organisation.caption.index" ) + ":" ),	0,  4 );
        addComponent( new Label( model.getApp().getResourceStr( "organisation.caption.city" ) + ":" ),	2,  4 );
		
		addComponent( street,		1,  3,  3,  3 );
		addComponent( index,		1,  4);
		addComponent( city,			3,  4);
		
		
		addComponent( getButtonsBar(), 0, 6, 1, 6 );

		updateButtons();
		enableFields();
	}


	public OrgListModel getModel() { return model; }
	public void setModel( OrgListModel model ) { this.model = model; }

	public boolean isEditedFlag() { return editedFlag;}
	public void setEditedFlag(boolean editedFlag) {this.editedFlag = editedFlag; }

	private void dataToView() {

		if ( this.shownOrg != null ) {
			
			// If this is a new user than it is necessary to set up Code.
			// user can change code if he wants
			if ( this.shownOrg.getId() <= 0 ) {
				model.setOrgCode( this.shownOrg );
			}
			code.setValue( this.shownOrg.getCode());

			name.setValue( this.shownOrg.getName());
			tunnus.setValue( this.shownOrg.getTunnus());
			
			
		}

	}

	private void viewToData() {

		if ( this.shownOrg != null ) {
			this.shownOrg.setCode( code.getValue());
			this.shownOrg.setName( name.getValue());
			this.shownOrg.setTunnus( tunnus.getValue());

		}

	}



	@Override
	public void currentWasSet( Organisation org ) {

		if ( logger.isDebugEnabled()) logger.debug( "OrganisationView received event about Org selection. Ready to show:" + org );

		this.shownOrg = org;
		setVisible( org != null );
		dataToView();

		if ( model.isEditMode()) {
			
			model.clearEditMode();
			
			updateButtons();
			enableFields();
		}

		if ( org != null && org.getId() <= 0 ) {

			logger.debug( "New Organisation created. Need to be edited and saved!" );

			editClose();

		}

	}


	@Override
	public void wasAdded( Organisation org ) {}
	@Override
	public void wasChanged( Organisation org ) {}
	@Override
	public void wasDeleted( Organisation org ) {}
	@Override
	public void wholeListChanged() {
		
		logger.debug( "OrgView received 'wholeListChanged' event" );

		dataToView();
		
		updateButtons();
		enableFields();
		
	}

	public Component getButtonsBar() {

		HorizontalLayout toolBarLayout = new HorizontalLayout();

		toolBarLayout.setWidth( "100%");
		toolBarLayout.setMargin( new MarginInfo( false, true, false, true ));

		editcloseButton = new Button();
		deleteButton = new Button( model.getApp().getResourceStr( "general.button.delete" ));

		if ( model.getSecurityContext().hasEditPermissionMgmt( FunctionalityType.ORGS_MGMT )) { 

			// Edit is possible if RW rights are given for any level or RW given for own company 
			
	
			editcloseButton.addClickListener( new ClickListener() {
				private static final long serialVersionUID = 1L;
	
				@Override
				public void buttonClick( ClickEvent event) {
	
					editClose();
	
				}
			});

			toolBarLayout.addComponent( editcloseButton);
	
			if ( model.getSecurityContext().hasEditPermissionAll( FunctionalityType.ORGS_MGMT )) { 
				
				// Delete is possible if RW rights are given in ANY ORGANISATONS level only! 
				deleteButton.setIcon( new ThemeResource("icons/16/reject.png"));
		
				deleteButton.addClickListener( new ClickListener() {
					private static final long serialVersionUID = 1L;
		
					@Override
					public void buttonClick( ClickEvent event) {
						if ( !model.isEditMode() && OrgView.this.shownOrg != null ) {
		
							deleteOrg();
						}
					}
				});
		
				toolBarLayout.addComponent( deleteButton );
			}
		}

		return toolBarLayout;
	}

	private void deleteOrg() {
		// Confirm removal
		String template = model.getApp().getResourceStr( "confirm.organisation.delete" );
		Object[] params = { this.shownOrg.getName() };
		template = MessageFormat.format( template, params );

		ConfirmDialog.show( model.getApp(),
				model.getApp().getResourceStr( "confirm.general.header" ),
				template,
				model.getApp().getResourceStr( "general.button.ok" ),
				model.getApp().getResourceStr( "general.button.cancel" ),
				new ConfirmDialog.Listener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClose( ConfirmDialog dialog ) {
						if ( dialog.isConfirmed()) {

							Organisation deletedOrg = model.delete( OrgView.this.shownOrg );
							if ( deletedOrg != null) {

								String template = model.getApp().getResourceStr( "notify.organisation.delete" );
								Object[] params = { deletedOrg.getName() };
								template = MessageFormat.format( template, params );

								Notification.show( template );

//								currentWasSet( null );

							}

						}
					}

		});

	}

	private void updateButtons() {

		if ( model.isEditMode() ) {

			editcloseButton.setCaption( model.getApp().getResourceStr( "general.button.close" ) );
			editcloseButton.setIcon( new ThemeResource("icons/16/approve.png"));

			deleteButton.setVisible( false );

		} else {

			editcloseButton.setCaption( model.getApp().getResourceStr( "general.button.edit" ));
			editcloseButton.setIcon( new ThemeResource("icons/16/edit.png"));

			deleteButton.setVisible( true );
		}

	}

	private void enableFields() {

		code.setEnabled( model.isEditMode());
		name.setEnabled( model.isEditMode());
		tunnus.setEnabled( model.isEditMode());



	}

	private void editClose() {

		logger.debug( "EditClose button has been pressed!" );

		model.swipeEditMode();

		if ( model.isEditMode()) {

			setEditedFlag( false );

			listenForChanges( code );
			listenForChanges( name );
			listenForChanges( tunnus );

			listenForChanges( street );
//			listenForChanges( poBox );
			listenForChanges( index );
			listenForChanges( city );
//			listenForChanges( countryCode );

		    
//			listenForChanges( phone );
//			listenForChanges( email );

			listenForChanges( info );
			

		} else {
			if ( isEditedFlag()) {

				// Changes must be stored
				viewToData();

				if ( this.shownOrg.getId() > 0 ) {
					// This is existing record update
					Organisation newOrg = model.update( this.shownOrg );
					if ( newOrg == null ) {

						String template = model.getApp().getResourceStr( "general.errors.update.header" );
						Object[] params = { this.shownOrg.getName() };
						template = MessageFormat.format( template, params );

						Notification.show( template, Notification.Type.ERROR_MESSAGE );

					} else {
						currentWasSet( null );
					}
				} else {
					// This is new record. It must be added
					Organisation newOrg = model.add( this.shownOrg );
					if ( newOrg == null ) {

						String template = model.getApp().getResourceStr( "general.errors.add.header" );
						Object[] params = { this.shownOrg.getName() };
						template = MessageFormat.format( template, params );

						Notification.show( template, Notification.Type.ERROR_MESSAGE );

					} else {
						currentWasSet( null );
					}

				}
			}

			stopListeningForChanges();
		}


		updateButtons();
		enableFields();
	}

	@SuppressWarnings("rawtypes")
	class Pair {
		AbstractField field;
		ValueChangeListener listener;

		Pair( AbstractField field, ValueChangeListener listener ) {
			this.field = field;
			this.listener = listener;
		}
	}

	private List<Pair> listenersList = new ArrayList<Pair>();
	@SuppressWarnings("rawtypes")
	private void listenForChanges( final AbstractField field ) {

		ValueChangeListener listener = new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {

				if ( logger.isDebugEnabled()) logger.debug( "Field '" + field.getClass().getSimpleName() + "' was changed!" );

				setEditedFlag( true );

			}

		};

		field.addValueChangeListener( listener );
		listenersList.add( new Pair( field, listener ));

	}
	private void stopListeningForChanges() {

		for( Pair pair : listenersList ) {
			pair.field.removeValueChangeListener( pair.listener );
		}

	}

}
