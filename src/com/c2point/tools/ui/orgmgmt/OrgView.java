package com.c2point.tools.ui.orgmgmt;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;

import com.c2point.tools.access.FunctionalityType;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.person.Address;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.ui.util.UIhelper;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractField;
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
import com.vaadin.ui.VerticalLayout;

public class OrgView extends VerticalLayout implements OrgChangedListener {
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
	private ComboBox	country;

    private TextField 	email;
    private TextField 	phone;

//    private TextArea	info;
	
	private Component	soSelector = null;
	private Component	soNew = null;
	private boolean 	isSoSelector = false;
	
	private ComboBox	serviceOwner;

	private TextField	soFirstName;
    private TextField	soLastName;
    private TextField	soEmail;
    private TextField	soPhone;

	private Button		editcloseButton;
	private Button		deleteButton;

	private boolean		editedFlag;
	private Organisation shownOrg;

	public OrgView( OrgListModel model ) {
		super();
		
		setModel( model );

		initView();

		model.addChangedListener( this );

		model.clearEditMode();
		
	}

	private void initView() {

		GridLayout nameLayout = new GridLayout( 4, 2 );
 		
		nameLayout.setSpacing( true );
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
        
		Label separator = new Label( "<hr/>", ContentMode.HTML );
		separator.setWidth( "100%" );
		
		addComponent( separator );

		
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
		
	    phone = new TextField();
	    phone.setTabIndex( 7 );
	    phone.setWidth( "15em" );
	    phone.setNullRepresentation( "" );
	    phone.setRequired( false );
	    phone.setValidationVisible( true );
	    phone.setImmediate(true);
	    
	    email = new TextField();
	    email.setTabIndex( 8 );
	    email.setWidth("100%");
	    email.setNullRepresentation( "" );
	    email.setRequired( false );
	    email.setValidationVisible( true );
	    email.setImmediate(true);
    	
    	
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
        
		Label separator2 = new Label( "<hr/>", ContentMode.HTML );
		separator.setWidth( "100%" );
		
		addComponent( separator2 );
        
		addServiceOwnerSelector();
		
		addComponent( getButtonsBar());

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

			OrgUser tmpUser = this.shownOrg.getResponsible();

			initUserComboBox( tmpUser );
			if ( tmpUser != null ) {

				soFirstName.setValue( tmpUser.getFirstName());
			    soLastName.setValue( tmpUser.getLastName());
			    soEmail.setValue( tmpUser.getEmail());
			    soPhone.setValue( tmpUser.getPhoneNumber());
				
			} else  {
				
				soFirstName.setValue( null );
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
			
			
			if ( isSoSelector ) {
				
				this.shownOrg.setResponsible(( OrgUser )serviceOwner.getValue());
				
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
				
			}
			
		}

	}



	@Override
	public void currentWasSet( Organisation org ) {

		if ( logger.isDebugEnabled()) logger.debug( "OrganisationView received event about Org selection. Ready to show:" + org );

		this.shownOrg = org;
		setVisible( org != null );
		
		// Call method to set proper component visible
		setSelectOrEnterResponsiblePerson();
		
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

		street.setEnabled( model.isEditMode());
		index.setEnabled( model.isEditMode());
		city.setEnabled( model.isEditMode());
		country.setEnabled( model.isEditMode());

		phone.setEnabled( model.isEditMode());
		email.setEnabled( model.isEditMode());

		serviceOwner.setEnabled( model.isEditMode());

		soFirstName.setEnabled( model.isEditMode());
	    soLastName.setEnabled( model.isEditMode());
	    soEmail.setEnabled( model.isEditMode());
	    soPhone.setEnabled( model.isEditMode());
		
	}

	private void editClose() {

		logger.debug( "EditClose button has been pressed!" );

		if ( !model.isEditMode()) {
			
			model.setEditMode();

			initUserComboBox();
			
			setEditedFlag( false );

			listenForChanges( code );
			listenForChanges( name );
			listenForChanges( tunnus );

			listenForChanges( street );
//			listenForChanges( poBox );
			listenForChanges( index );
			listenForChanges( city );
			listenForChanges( country );

			listenForChanges( phone );
			listenForChanges( email );
		    
//			listenForChanges( info );
			listenForChanges( serviceOwner );

			listenForChanges( soFirstName );
			listenForChanges( soLastName );
			listenForChanges( soEmail );
			listenForChanges( soPhone );

		} else {

			if ( isEditedFlag()) {
				
				if ( valid()) {

					// Changes must be stored
					viewToData();
	
					if ( this.shownOrg.getId() > 0 ) {
						// This is existing record update
						Organisation newOrg = model.update( this.shownOrg );
						if ( newOrg == null ) {
	
							String template = model.getApp().getResourceStr( "general.error.update.header" );
							Object[] params = { this.shownOrg.getName() };
							template = MessageFormat.format( template, params );
	
							Notification.show( template, Notification.Type.ERROR_MESSAGE );
	
						} else {
							currentWasSet( newOrg );
						}
					} else {
						// This is new record. It must be added
						Organisation newOrg = model.add( this.shownOrg );
						if ( newOrg == null ) {
	
							String template = model.getApp().getResourceStr( "general.error.add.header" );
							Object[] params = { this.shownOrg.getName() };
							template = MessageFormat.format( template, params );
	
							Notification.show( template, Notification.Type.ERROR_MESSAGE );
	
						} else {
							currentWasSet( newOrg );
						}
	
					}
				} else {
					// Fields are invalid. Check, edit them and continue

					Notification.show( model.getApp().getResourceStr( "general.error.field.empty" ), Notification.Type.ERROR_MESSAGE );
					
				}
			} else {

				// Nothing was changed
				model.clearEditMode();
				stopListeningForChanges();
				
			}

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

	private void addServiceOwnerSelector() {
		

		// Create selection component. Existing person can be selected
		soSelector = getServiceOwnerSelector();
		addComponent( soSelector );
		
		// Create  assignment component. No person exists to set uop. new one shall be created
		soNew = getNewServiceOwner();
		addComponent( soNew );
		
		// Call method to set proper component visible
		setSelectOrEnterResponsiblePerson();
		
		
	}

	private void chooseVisibilityFlag() {
		
		isSoSelector = false;
		
		// Employee selection is possible if there is 1 or more NOT DELETED employees
		
		if ( this.shownOrg != null 
			&&
			 this.shownOrg.getEmployees() != null
			&&
			this.shownOrg.getEmployees().size() > 0 ) {
			
			for ( OrgUser user : this.shownOrg.getEmployees().values()) {
				
				if ( !user.isDeleted()) {
					isSoSelector = true;
					break;
				}
			}
			
		}
		
	}
	
	private void setSelectOrEnterResponsiblePerson() {

		// Determine what component from above shall be visible
		chooseVisibilityFlag();
		
		soSelector.setVisible( isSoSelector );
		soNew.setVisible( !isSoSelector );
		
	}
	
	private Component getServiceOwnerSelector() {

		GridLayout layout = new GridLayout( 2, 1 );
		layout.setSpacing( true );
		layout.setMargin( true );
		
		serviceOwner = new ComboBox();
		serviceOwner.setInputPrompt( model.getApp().getResourceStr( "toolsmgmt.text.select.user" ));
		serviceOwner.setFilteringMode( FilteringMode.CONTAINS );
		serviceOwner.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		serviceOwner.setNullSelectionAllowed( false );
		serviceOwner.setInvalidAllowed( false );
		serviceOwner.setRequired( true );
		serviceOwner.setRequiredError(model.getApp().getResourceStr( "general.error.field.empty" ));
		serviceOwner.setValidationVisible( true );
		serviceOwner.setImmediate( true );
		
		layout.addComponent( new Label( "Service Owner: " ), 0, 0 );
		layout.addComponent( serviceOwner, 1, 0 );

    	layout.getComponent( 0, 0 ).setWidth( "6em" );
		
		return layout;
	}
	
	private Component getNewServiceOwner() {

		GridLayout layout = new GridLayout( 4, 4 );
 		
		layout.setSpacing( true );
		layout.setMargin( true );
//		layout.setSizeUndefined();
		layout.setWidth( "100%" );

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
    	
    	
        layout.addComponent( new Label( model.getApp().getResourceStr( "personnel.caption.firstname" ) + ":" ),	0,  0 );
        layout.addComponent( new Label( model.getApp().getResourceStr( "personnel.caption.lastname" ) + ":" ),	0,  1 );
        layout.addComponent( new Label( model.getApp().getResourceStr( "general.caption.email" ) + ":" ),		0,  2 );
        layout.addComponent( new Label( model.getApp().getResourceStr( "general.caption.phone" ) + ":" ),		0,  3 );
		
        layout.addComponent( soFirstName,	1,  0,  2,  0 );
        layout.addComponent( soLastName,	1,  1,  3,  1 );
        layout.addComponent( soEmail,		1,  2,  3,  2 );
        layout.addComponent( soPhone,		1,  3,  2,  3 );

	    // Align and size the labels in 1st column
	    for ( int row=0; row < layout.getRows(); row++ ) {
	    	layout.getComponent( 0, row ).setWidth( "6em" );
	    }
        layout.setColumnExpandRatio( 3, 5 );
		
		return layout;
	}

	private  void initUserComboBox() {
		
		OrgUser selectedUser = ( OrgUser )serviceOwner.getValue();
		
		initUserComboBox( selectedUser, true );
	}
	private  void initUserComboBox( OrgUser selectedUser ) {
		initUserComboBox( selectedUser, false );
	}
	private  void initUserComboBox( OrgUser selectedUser, boolean full ) {

		serviceOwner.removeAllItems();
		
		if ( full ) { 
		
			for ( OrgUser user : model.getUsers()) {
				
				serviceOwner.addItem( user );
				serviceOwner.setItemCaption( user, user.getLastAndFirstNames());
				
			}
			
		} else {
		
			if ( selectedUser != null ) {
				serviceOwner.addItem( selectedUser );
				serviceOwner.setItemCaption( selectedUser, selectedUser.getLastAndFirstNames());
			}
		
		}
/*			
		serviceOwner.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
	
			@Override
			public void valueChange( ValueChangeEvent event ) {
				
				
			}
			
		});
*/			
		serviceOwner.setValue( selectedUser );
	
	}

	private boolean valid() {

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
		
		
		if ( isSoSelector ) {
		
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

/*
	private void setNormalizedValue( AbstractField field, Object value ) {
		
		if ( value != null ) field.setValue( value );
	}
*/
}
