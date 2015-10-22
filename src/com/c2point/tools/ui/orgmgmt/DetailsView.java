package com.c2point.tools.ui.orgmgmt;

import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;

import com.c2point.tools.entity.access.AccessGroups;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.person.Address;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.ui.AbstractModel.EditModeType;
import com.c2point.tools.ui.ChangesCollector;
import com.c2point.tools.ui.listeners.OrgChangedListener;
import com.c2point.tools.ui.propertiesmgmt.PropsMgmtModel;
import com.c2point.tools.ui.propertiesmgmt.PropsMgmtView;
import com.c2point.tools.ui.util.UIhelper;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.VerticalLayout;

public class DetailsView extends VerticalLayout implements OrgChangedListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( DetailsView.class.getName());


	private OrgListModel	model;
	private ChangesCollector	changesCollector = new ChangesCollector();

	private TextField	code;
	private TextField 	name;
    private TextField	tunnus;

    // Address fields
	private TextField	street;
	private TextField	index;
	private TextField	city;
	private ComboBox	country;

    private TextField 	email;
    private TextField 	phone;

	private Button		settingsButton;

	private ComboBox	serviceOwner;

	private TextField	soFirstName;
    private TextField	soLastName;
    private TextField	soEmail;
    private TextField	soPhone;

	private Button		editCloseButton;
	private Button		deleteButton;

	private Organisation	shownOrg;

	public DetailsView( OrgListModel model ) {
		super();
		
		setModel( model );

		initView();

		model.addChangedListener( this );

		model.clearEditMode();
		
	}

	private void initView() {

		this.setSpacing( true );
		this.setMargin( true );

		Label header = new Label( "Company Data", ContentMode.HTML );
		header.addStyleName( "h2" );
		
		addComponent( header );
		
		GridLayout nameLayout = new GridLayout( 4, 2 );
//		nameLayout.setSpacing( true );
		nameLayout.setMargin( true );
//		nameLayout.setSizeUndefined();
		
		code = new TextField();
		code.setWidth( "6em" );
		code.setNullRepresentation( "" );
		code.setImmediate( true );
		
        name = new TextField();
        name.setTabIndex( 1 );
        name.setWidth( "100%" );
//        name.setWidth( "15em" );
        name.setNullRepresentation( "" );
        name.setRequired( true );
        name.setRequiredError( model.getApp().getResourceStr( "general.error.field.empty" ));
        name.setValidationVisible( true );
		name.setImmediate( true );
		
        tunnus = new TextField();
        tunnus.setTabIndex( 2 );
        tunnus.setWidth( "6em" );
        tunnus.setNullRepresentation( "" );
        tunnus.setRequired( true );
        tunnus.setRequiredError(model.getApp().getResourceStr( "general.error.field.empty" ));
        tunnus.setValidationVisible( true );
        tunnus.setImmediate(true);
		
        nameLayout.addComponent( new Label( model.getApp().getResourceStr( "general.caption.code" ) + ":" ),	0,  0 ); 
        nameLayout.addComponent( new Label( model.getApp().getResourceStr( "general.caption.name" ) + ":" ),	0,  1 );
        nameLayout.addComponent( new Label( model.getApp().getResourceStr( "organisation.caption.tunnus" ) + ":" ),	2,  1 );
		
        nameLayout.addComponent( code,		1,  0 );
        nameLayout.addComponent( name,		1,  1 );
        nameLayout.addComponent( tunnus,	3,  1 );

		addComponent( nameLayout );
        
//		addComponent( getSeparator());

		GridLayout addrLayout = new GridLayout( 4, 6 );
 		
		addrLayout.setSpacing( true );
		addrLayout.setMargin( true );
//		addrLayout.setSizeUndefined();
		addrLayout.setWidth( "100%" );
		
        street = new TextField();
        street.setTabIndex( 3 );
        street.setWidth("100%");
        street.setNullRepresentation( "" );
        street.setRequired( false );
        street.setValidationVisible( true );
        street.setImmediate(true);

        index = new TextField();
        index.setTabIndex( 4 );
        index.setWidth( "6em" );
        index.setNullRepresentation( "" );
        index.setRequired( false );
        index.setValidationVisible( true );
        index.setImmediate(true);
		
        city = new TextField();
        city.setTabIndex( 5 );
//        city.setWidth( "15em" );
        city.setWidth( "100%" );
        city.setNullRepresentation( "" );
        city.setRequired( false );
        city.setValidationVisible( true );
        city.setImmediate(true);

    	country = new ComboBox();
    	country.setTabIndex( 6 );
		country.setFilteringMode( FilteringMode.CONTAINS );
		country.setNullSelectionAllowed( true );
		country.setWidth( "10em" );
		country.setImmediate( true );
		UIhelper.fillCountryCombo( country );
		
	    email = new TextField();
	    email.setTabIndex( 7 );
	    email.setWidth( "20em" );
	    email.setNullRepresentation( "" );
	    email.setRequired( false );
	    email.setValidationVisible( true );
	    email.setImmediate(true);
    	
	    phone = new TextField();
	    phone.setTabIndex( 8 );
	    phone.setWidth( "20em" );
	    phone.setNullRepresentation( "" );
	    phone.setRequired( false );
	    phone.setValidationVisible( true );
	    phone.setImmediate(true);
	    
    	
        addrLayout.addComponent( new Label( model.getApp().getResourceStr( "organisation.caption.street" ) + ":" ),		0,  0 );
        addrLayout.addComponent( new Label( model.getApp().getResourceStr( "organisation.caption.index_city" ) + ":" ),	0,  1 );
        addrLayout.addComponent( new Label( model.getApp().getResourceStr( "organisation.caption.country" ) + ":" ),	0,  2 );
        addrLayout.addComponent( new Label( "" ),																		0,  3 );
        addrLayout.addComponent( new Label( model.getApp().getResourceStr( "general.caption.email" ) + ":" ),			0,  4 );
        addrLayout.addComponent( new Label( model.getApp().getResourceStr( "general.caption.phone" ) + ":" ),			0,  5 );
		
        addrLayout.addComponent( street,	1,  0,  3,  0 );
        addrLayout.addComponent( index,		1,  1 );
        addrLayout.addComponent( city,		2,  1,  3,  1 );
        addrLayout.addComponent( country,	1,  2,  2,  2 );
        addrLayout.addComponent( email,		1,  4,  2,  4 );
        addrLayout.addComponent( phone,		1,  5,  3,  5 );

	    // Align and size the labels in 1st column
	    for ( int row=0; row < addrLayout.getRows(); row++ ) {
	    	addrLayout.getComponent( 0, row ).setWidth( "6em" );
	    }
        addrLayout.setColumnExpandRatio( 3, 5 );
                
		addComponent( addrLayout );

		settingsButton = new Button( "Settings" );

		settingsButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick( ClickEvent event) {

				settingsMgmt();

			}
		});

		addComponent( settingsButton );

		addComponent( getSeparator());
        
		Label headerOwner = new Label( "Service Owner Data", ContentMode.HTML );
		headerOwner.addStyleName( "h2" );
		
		addComponent( headerOwner );
		
		addComponent( getServiceOwnerSelector()); 
		addComponent( getNewServiceOwner());
		
		
		
		addComponent( getSeparator());
		
		addComponent( getButtonsBar());

		changesCollector.listenForChanges( code );
		changesCollector.listenForChanges( name );
		changesCollector.listenForChanges( tunnus );

		changesCollector.listenForChanges( street );
//		changesCollector.listenForChanges( poBox );
		changesCollector.listenForChanges( index );
		changesCollector.listenForChanges( city );
		changesCollector.listenForChanges( country );
		changesCollector.listenForChanges( email );
		changesCollector.listenForChanges( phone );
	    
		
		updateButtons();
		updateFields();
	}


	public OrgListModel getModel() { return model; }
	public void setModel( OrgListModel model ) { this.model = model; }

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

			Address adr = this.shownOrg.getAddress();
			if ( adr == null ) {
				adr = new Address();
				this.shownOrg.setAddress( adr );
			}
			
			street.setValue( adr.getStreet());
			index.setValue( adr.getIndex());
			city.setValue( adr.getCity());
			country.setValue( adr.getCountryCode());

			phone.setValue( this.shownOrg.getPhoneNumber());
			email.setValue( this.shownOrg.getEmail());

			if ( model.ownerCanBeSelected() ) {

				initUserComboBox( this.shownOrg.getResponsible());
				
			} else  {
				
				soFirstName.setValue( "" );
			    soLastName.setValue( "" );
			    soEmail.setValue( "" );
			    soPhone.setValue( "" );

			}
			
			
		} else {
			code.setValue( "" );
			name.setValue( "" );
			tunnus.setValue( "" );

			street.setValue( "" );
			index.setValue( "" );
			city.setValue( "" );
			country.setValue( "" );
	
			phone.setValue( "" );
			email.setValue( "" );

			if ( model.ownerCanBeSelected() ) {

				serviceOwner.removeAllItems();
				
			} else {

				soFirstName.setValue( "" );
			    soLastName.setValue( "" );
			    soEmail.setValue( "" );
			    soPhone.setValue( "" );
			}
		    
		}

	}

	private void viewToData() {

		if ( this.shownOrg != null ) {
			this.shownOrg.setCode( code.getValue());
			this.shownOrg.setName( name.getValue());
			this.shownOrg.setTunnus( tunnus.getValue());

			Address adr = this.shownOrg.getAddress();
			if ( adr == null ) {
				adr = new Address();
				this.shownOrg.setAddress( adr );
			}
			
			adr.setStreet( street.getValue( ));
			adr.setIndex( index.getValue( ));
			adr.setCity( city.getValue( ));
			adr.setCountryCode(( String )country.getValue( ));

			this.shownOrg.setPhoneNumber( phone.getValue());
			this.shownOrg.setEmail( email.getValue());
			
			
			if ( model.ownerCanBeSelected() ) {
				
				this.shownOrg.setResponsible(( OrgUser )serviceOwner.getValue());
				this.shownOrg.getResponsible().setAccessGroup( AccessGroups.BOSS );
				
			} else {

				OrgUser responsibleUser = this.shownOrg.getResponsible();
				if ( responsibleUser == null ) {
					
					responsibleUser = new OrgUser();
					
					this.shownOrg.addUser( responsibleUser );
					
					this.shownOrg.setResponsible( responsibleUser );
					
					
				}
				
				responsibleUser.setFirstName( soFirstName.getValue());
				responsibleUser.setLastName( soLastName.getValue());
				responsibleUser.setEmail( soEmail.getValue());
				responsibleUser.setPhoneNumber( soPhone.getValue());
				
				responsibleUser.setAccessGroup( AccessGroups.BOSS );
				
			}
			
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
			
		}

		updateButtons();
		updateFields();
		
		if ( org != null && org.getId() <= 0 ) {

			logger.debug( "New Organisation created. Need to be edited and saved!" );

			editSavePressed();

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
/*		
		logger.debug( "OrgView received 'wholeListChanged' event" );

		dataToView();
		
		updateButtons();
		enableFields();
*/		
	}

	public Component getButtonsBar() {

		HorizontalLayout toolBarLayout = new HorizontalLayout();

		toolBarLayout.setWidth( "100%");
		toolBarLayout.setMargin( new MarginInfo( false, true, false, true ));

		editCloseButton = new Button();

		editCloseButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick( ClickEvent event) {

				editSavePressed();

			}
		});


		deleteButton = new Button( model.getApp().getResourceStr( "general.button.delete" ));
		deleteButton.setIcon( new ThemeResource("icons/16/reject.png"));

		deleteButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick( ClickEvent event) {

				deleteCancelPressed();

			}
		});


		toolBarLayout.addComponent( editCloseButton);
		toolBarLayout.addComponent( deleteButton);

		return toolBarLayout;
	}

	private void deleteOrg( final Organisation org ) {
		// Confirm removal
		String template = model.getApp().getResourceStr( "confirm.organisation.delete" );
		Object[] params = { org.getName() };
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

							Organisation deletedOrg = model.delete( org );
							if ( deletedOrg != null) {

								String template = model.getApp().getResourceStr( "notify.organisation.delete" );
								Object[] params = { deletedOrg.getName() };
								template = MessageFormat.format( template, params );

								Notification.show( template );

							} else {
								// Failed to delete
								// Failed to update
								String template = model.getApp().getResourceStr( "organisation.errors.item.delete" );
								Object[] params = { org.getName() };
								template = MessageFormat.format( template, params );

								Notification.show( template, Notification.Type.ERROR_MESSAGE );
								
							}


						}
					}

		});

	}

	private void updateButtons() {

		editCloseButton.setEnabled( model.allowsToEdit());
		deleteButton.setEnabled( model.allowsToEdit());
			
		switch ( model.getEditMode()) {
			case ADD:
			case EDIT:
				editCloseButton.setCaption( model.getApp().getResourceStr( "general.button.ok" ));
				editCloseButton.setIcon( new ThemeResource("icons/16/approve.png"));

				deleteButton.setCaption( model.getApp().getResourceStr( "general.button.cancel" ));
				break;
			case VIEW:
				editCloseButton.setCaption( model.getApp().getResourceStr( "general.button.edit" ));
				editCloseButton.setIcon( new ThemeResource("icons/16/edit.png"));
				
				deleteButton.setCaption( model.getApp().getResourceStr( "general.button.delete" ));
				break;
			default:
				break;
		}
		
	}

	private void updateFields() {

		boolean enabled = model.isEditMode();
		
		code.setEnabled( false ); // enabled );
		name.setEnabled( enabled );
		tunnus.setEnabled( enabled );

		street.setEnabled( enabled );
		index.setEnabled( enabled );
		city.setEnabled( enabled );
		country.setEnabled( enabled );

		phone.setEnabled( enabled );
		email.setEnabled( enabled );

		serviceOwner.setEnabled( enabled );
		soFirstName.setEnabled( enabled );
	    soLastName.setEnabled( enabled );
	    soEmail.setEnabled( enabled );
	    soPhone.setEnabled( enabled );

		ownerSelector.setVisible( model.ownerCanBeSelected());
		ownerInput.setVisible( !model.ownerCanBeSelected());
		
		
		if ( enabled )
			changesCollector.startToListen();
		else
			changesCollector.stopToListen();
		
	}

	private void editSavePressed() {

		logger.debug( "EditClose button has been pressed!  EditMode was " + model.getEditMode());

		boolean succeeded = false;
		switch ( model.getEditMode()) {
			case ADD:
	
				viewToData();
				
				succeeded = ( this.addOrg( this.shownOrg ) != null );
				
				break;
	
			case EDIT:
				
				succeeded = ( this.updateOrg( this.shownOrg ) != null );
	
				break;
			case VIEW:
	
				model.setEditMode( this.shownOrg.getId() > 0 ? EditModeType.EDIT : EditModeType.ADD );
				succeeded = true;
				
				break;
			default:
				break;
		}

		if ( succeeded ) {
			updateButtons();
			updateFields();
		}
		
		logger.debug( "EditClose button pressing handled!  EditMode now is " + model.getEditMode());
		
	}

	private void deleteCancelPressed() {

		switch ( model.getEditMode()) {
			case ADD:
			case EDIT:
				model.setViewMode();
				dataToView();
				break;
			case VIEW:

				deleteOrg( this.shownOrg );
				
				break;
			default:
				break;
		}

		updateButtons();
		updateFields();
		
	}
/*
	private Component getServiceOwnerComponent() {
		
		Component selector = null;
		
		// If Org not new and employees exist than select from the list
		if ( model.ownerCanBeSelected()) {
			
			selector = getServiceOwnerSelector(); 
		} else {

			selector = getNewServiceOwner();
			
		}
		
		return selector;
		
	}
*/

	private GridLayout ownerSelector = null;
	private Component getServiceOwnerSelector() {

		if ( ownerSelector == null ) {
			
			ownerSelector = new GridLayout( 2, 1 );
			ownerSelector.setSpacing( true );
			ownerSelector.setMargin( true );
			
			serviceOwner = new ComboBox();
			serviceOwner.setInputPrompt( model.getApp().getResourceStr( "toolsmgmt.text.select.user" ));
			serviceOwner.setFilteringMode( FilteringMode.CONTAINS );
			serviceOwner.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
			serviceOwner.setNullSelectionAllowed( true );
			serviceOwner.setInvalidAllowed( false );
			serviceOwner.setRequired( true );
			serviceOwner.setRequiredError(model.getApp().getResourceStr( "general.error.field.empty" ));
			serviceOwner.setValidationVisible( true );
			serviceOwner.setImmediate( true );
			
			ownerSelector.addComponent( new Label( "Service Owner: " ), 0, 0 );
			ownerSelector.addComponent( serviceOwner, 1, 0 );
	
			ownerSelector.getComponent( 0, 0 ).setWidth( "6em" );
			
			changesCollector.listenForChanges( serviceOwner );
		}
		
		ownerSelector.setVisible( false );
		
		return ownerSelector;
	}
	
	private GridLayout ownerInput = null;
	private Component getNewServiceOwner() {

		if ( ownerInput == null ) {
			
			ownerInput = new GridLayout( 4, 4 );
 		
			ownerInput.setSpacing( true );
			ownerInput.setMargin( true );
	//		layout.setSizeUndefined();
			ownerInput.setWidth( "100%" );
	
			soFirstName = new TextField();
		    soFirstName.setTabIndex( 9 );
		    soFirstName.setNullRepresentation( "" );
			soFirstName.setRequired( true );
			soFirstName.setRequiredError(model.getApp().getResourceStr( "general.error.field.empty" ));
			soFirstName.setValidationVisible( true );
			soFirstName.setImmediate( true );
	
			soLastName = new TextField();
		    soLastName.setTabIndex( 10 );
		    soLastName.setNullRepresentation( "" );
			soLastName.setRequired( true );
			soLastName.setRequiredError(model.getApp().getResourceStr( "general.error.field.empty" ));
			soLastName.setValidationVisible( true );
			soLastName.setImmediate( true );
	
			
		    soPhone = new TextField();
		    soPhone.setTabIndex( 11 );
		    soPhone.setWidth( "15em" );
		    soPhone.setNullRepresentation( "" );
		    soPhone.setRequired( true );
		    soPhone.setRequiredError(model.getApp().getResourceStr( "general.error.field.empty" ));
		    soPhone.setValidationVisible( true );
		    soPhone.setImmediate(true);
		    
	
		    soEmail = new TextField();
		    soEmail.setTabIndex( 12 );
		    soEmail.setWidth("100%");
		    soEmail.setNullRepresentation( "" );
		    soEmail.setRequired( true );
		    soEmail.setRequiredError(model.getApp().getResourceStr( "general.error.field.empty" ));
		    soEmail.setValidationVisible( true );
		    soEmail.setImmediate(true);
	    	
	    	
	        ownerInput.addComponent( new Label( model.getApp().getResourceStr( "personnel.caption.firstname" ) + ":" ),	0,  0 );
	        ownerInput.addComponent( new Label( model.getApp().getResourceStr( "personnel.caption.lastname" ) + ":" ),	0,  1 );
	        ownerInput.addComponent( new Label( model.getApp().getResourceStr( "general.caption.email" ) + ":" ),		0,  2 );
	        ownerInput.addComponent( new Label( model.getApp().getResourceStr( "general.caption.phone" ) + ":" ),		0,  3 );
			
	        ownerInput.addComponent( soFirstName,	1,  0,  2,  0 );
	        ownerInput.addComponent( soLastName,	1,  1,  3,  1 );
	        ownerInput.addComponent( soEmail,		1,  2,  3,  2 );
	        ownerInput.addComponent( soPhone,		1,  3,  2,  3 );
	
		    // Align and size the labels in 1st column
		    for ( int row=0; row < ownerInput.getRows(); row++ ) {
		    	ownerInput.getComponent( 0, row ).setWidth( "6em" );
		    }
		    ownerInput.setColumnExpandRatio( 3, 5 );
	
			changesCollector.listenForChanges( soFirstName );
			changesCollector.listenForChanges( soLastName );
			changesCollector.listenForChanges( soEmail );
			changesCollector.listenForChanges( soPhone );
		}

		ownerInput.setVisible( false );
		
		return ownerInput;
	}
 
	private  void initUserComboBox( OrgUser selectedUser ) {

		if ( serviceOwner != null ) {
			
			serviceOwner.removeAllItems();
		
			for ( OrgUser user : model.getUsers()) {
				
				serviceOwner.addItem( user );
				serviceOwner.setItemCaption( user, user.getLastAndFirstNames());
				
			}
			
			serviceOwner.setValue( selectedUser );
			
		}
/*			
		serviceOwner.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
	
			@Override
			public void valueChange( ValueChangeEvent event ) {
				
				
			}
			
		});
*/			
	
	}

	private boolean validate() {

		if ( !name.isValid()) {
		
			name.selectAll(); 
			name.focus();
			return false;
		}
		
		if ( !tunnus.isValid()) {
			
			tunnus.selectAll(); 
			tunnus.focus();
			return false;
		}
		
		
		if ( model.ownerCanBeSelected()) {
		
			if ( !serviceOwner.isValid()) {
				
				serviceOwner.focus();
				return false;
			}
			
		} else {

			if ( !soFirstName.isValid()) {
				
				soFirstName.selectAll(); 
				soFirstName.focus();
				return false;
			}
			
			if ( !soLastName.isValid()) {
				
				soLastName.selectAll(); 
				soLastName.focus();
				return false;
			}
			
			if ( !soEmail.isValid()) {
				
				soEmail.selectAll(); 
				soEmail.focus();
				return false;
			}
			
			if ( !tunnus.isValid()) {
				
				soPhone.selectAll(); 
				soPhone.focus();
				return false;
			}
			
		}
		
		return true;
		
	}

	private void settingsMgmt() {
		
		final PropsMgmtModel propsModel = new PropsMgmtModel( shownOrg ); 
		PropsMgmtView view = new PropsMgmtView( propsModel ); 
		model.getApp().addWindow( view );
		
		view.addCloseListener( new CloseListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void windowClose(CloseEvent e) {
				
				if ( propsModel.wasItChanged()) {
					changesCollector.changed();
				}
				
				logger.debug( "PropertiesMgmtView has been closed" );
			}
			
		});
		
	}

	private Component getSeparator() {

		Label separator = new Label( "<hr/>", ContentMode.HTML );
		separator.setWidth( "100%" );

		return separator;
	}
	
	// For now just to save code
	private Organisation addOrg( Organisation org ) {

		Organisation newOrg = null;
		
		if ( changesCollector.wasItChanged()) {

			if ( validate()) {
				// Changes must be stored
				viewToData();

				if ( this.shownOrg.getId() <= 0 ) {

					// This is new record. It must be added
					
					newOrg = model.add( this.shownOrg );
					
					if ( newOrg != null ) {

						model.setViewMode();
						currentWasSet( newOrg );
						
					} else {

						String template = model.getApp().getResourceStr( "general.error.add.header" );
						Object[] params = { this.shownOrg.getName() };
						template = MessageFormat.format( template, params );

						Notification.show( template, Notification.Type.ERROR_MESSAGE );

					}

				}
			} 
		
		}
		
		
		return newOrg;
	}

	private Organisation updateOrg( Organisation org ) {

		Organisation updatedOrg = null;
		
		if ( changesCollector.wasItChanged()) {

			if ( validate()) {
				// Changes must be stored
				viewToData();

				if ( this.shownOrg.getId() > 0 ) {

					// This is existing record update
					
					updatedOrg = model.update( org );
					
					if ( updatedOrg != null ) {

						model.setViewMode();
						currentWasSet( updatedOrg );

					} else {

						String template = model.getApp().getResourceStr( "general.error.update.header" );
						Object[] params = { this.shownOrg.getName() };
						template = MessageFormat.format( template, params );

						Notification.show( template, Notification.Type.ERROR_MESSAGE );
						
					}
				}		
			} 
		
		} else {
			model.setViewMode();
			updatedOrg = org;
			
		}
		
		
		return updatedOrg;
	}
	

}
