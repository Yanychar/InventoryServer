package com.c2point.tools.ui.personnelmgmt;

import java.text.MessageFormat;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDate;
import org.vaadin.dialogs.ConfirmDialog;

import com.c2point.tools.entity.access.AccessGroups;
import com.c2point.tools.entity.access.FunctionalityType;
import com.c2point.tools.entity.person.Address;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.ui.AbstractModel.EditModeType;
import com.c2point.tools.ui.ChangesCollector;
import com.c2point.tools.ui.accountmgmt.AccountView;
import com.c2point.tools.ui.listeners.EditInitiationListener;
import com.c2point.tools.utils.lang.Locales;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;

public class DetailsView extends VerticalLayout implements StuffChangedListener, EditInitiationListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( DetailsView.class.getName());


	private StuffListModel	model;
	private ChangesCollector	changesCollector = new ChangesCollector();

//	private TextField	code;
	private TextField 	firstName;
	private TextField 	lastName;
	private DateField 	birthday;

	private TextField	street;
	private TextField	pobox;
	private TextField	index;
	private TextField	city;
	private ComboBox	country;

	private TextField	email;
	private TextField	mobile;

	private ComboBox	accessGroup;
	
	private Label		noAccountMsg;
	private TextField	usrname;
	private Button		accountButton;
	
	private Button		editCloseButton;
	private Button		deleteButton;

	private OrgUser		shownUser;

	public DetailsView( StuffListModel model ) {
		super();

		setModel( model );

		initView();

		model.addListener(( StuffChangedListener ) this );
		model.addListener(( EditInitiationListener ) this );

		model.clearEditMode();
	}

	private void initView() {

		setSpacing( true );
		setMargin( true );
//		setSizeUndefined();

//		code = new TextField( model.getApp().getResourceStr( "general.caption.code" ) + ":" );
//		code.setRequired(true);
//		code.setRequiredError("The Field may not be empty.");
//		code.setNullRepresentation( "" );
//		code.setImmediate( true );

		firstName = new TextField( model.getApp().getResourceStr( "personnel.caption.firstname" ) + ":" );
		firstName.setNullRepresentation( "" );
		firstName.setImmediate( true );

		lastName = new TextField( model.getApp().getResourceStr( "personnel.caption.lastname" ) + ":" );
		lastName.setRequired( true );
		lastName.setRequiredError( "The Field may not be empty." );
		lastName.setNullRepresentation( "" );
		lastName.setImmediate( true );

		birthday = new DateField( "Birthday:");
		birthday.setLocale( new Locale("fi", "FI"));
		birthday.setDateFormat( "dd.MM.yyyy" );
		birthday.setResolution( Resolution.DAY );
		birthday.setImmediate(true);

		street = new TextField( "Street:" );
		street.setNullRepresentation( "" );
		street.setImmediate(true);

		pobox = new TextField( "Pobox:" );
		pobox.setNullRepresentation( "" );
		pobox.setImmediate(true);

		index = new TextField( "Postcode:" );
		index.setNullRepresentation( "" );
		index.setImmediate(true);

		city = new TextField( "City:" );
		city.setNullRepresentation( "" );
		city.setImmediate(true);

		country = new ComboBox( "Country code:", Locales.getISO3166Container());
		country.setInputPrompt( "No country selected" );

		country.setItemCaptionPropertyId( Locales.iso3166_PROPERTY_NAME);
		country.setItemCaptionMode( ItemCaptionMode.PROPERTY);
		country.setItemIconPropertyId( Locales.iso3166_PROPERTY_FLAG);
		country.setFilteringMode( FilteringMode.CONTAINS );

		country.setImmediate( true );

		email = new TextField( model.getApp().getResourceStr( "general.caption.email" ) + ":" );
		email.setRequired(true);
		email.setNullRepresentation( "" );
		email.setImmediate(true);

		mobile = new TextField( model.getApp().getResourceStr( "general.caption.phone" ) + ":" );
		mobile.setRequired(true);
		mobile.setNullRepresentation( "" );
		mobile.setImmediate(true);

		accessGroup = new ComboBox( "User Group:", Locales.getISO3166Container());

		accessGroup.setFilteringMode( FilteringMode.OFF );
		accessGroup.setInputPrompt( "No group selected" );
		accessGroup.setInvalidAllowed( false );
		accessGroup.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		accessGroup.setNullSelectionAllowed(false);
		accessGroup.setNullSelectionAllowed( false );
		accessGroup.setRequired( true );
		accessGroup.setRequiredError(model.getApp().getResourceStr( "general.error.field.empty" ));
		accessGroup.setValidationVisible( true );
		accessGroup.setImmediate( true );
		
/////
		
		noAccountMsg = new Label ( "<B>No account. Shall be created</B>", ContentMode.HTML );
		
		usrname = new TextField( "Username" + ":" );
		usrname.setWidth( "20em" );
		usrname.setEnabled( false );
		usrname.setImmediate( true );
		
		accountButton = new Button( "Change to 'Open Account'" );

		accountButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick( ClickEvent event) {

				accountMgmt();

			}
		});

		
		FormLayout fl_1 = new FormLayout();
		fl_1.setSpacing( true );
		Label separator_4 = new Label( " " );
		
//		fl_1.addComponent( code );
		fl_1.addComponent( firstName );
		fl_1.addComponent( lastName );
		fl_1.addComponent( birthday );
		fl_1.addComponent( street );
		fl_1.addComponent( pobox );
		fl_1.addComponent( index );
		fl_1.addComponent( city );
		fl_1.addComponent( country );
		fl_1.addComponent( email );
		fl_1.addComponent( mobile );
		fl_1.addComponent( separator_4 );
		fl_1.addComponent( accessGroup );



		FormLayout fl_2 = new FormLayout();
		fl_2.setSpacing( true );
		
		fl_2.addComponent( noAccountMsg );
		fl_2.addComponent( usrname );
		fl_2.addComponent( accountButton );
		
		Label separator_1 = new Label( "<hr/>", ContentMode.HTML );
		separator_1.setWidth( "100%" );
		Label separator_2 = new Label( "<hr/>", ContentMode.HTML );
		separator_2.setWidth( "100%" );
		Label separator_3 = new Label( "<hr/>", ContentMode.HTML );
		separator_3.setWidth( null );
		

		addComponent( fl_1 );
		addComponent( separator_1 );
		addComponent( fl_2 );
		addComponent( separator_2 );
		addComponent( separator_3 );
	
		addComponent( getButtonsBar());

		changesCollector.listenForChanges( firstName );
		changesCollector.listenForChanges( lastName );
		changesCollector.listenForChanges( birthday );
		changesCollector.listenForChanges( street );
		changesCollector.listenForChanges( pobox );
		changesCollector.listenForChanges( index );
		changesCollector.listenForChanges( city );
		changesCollector.listenForChanges( country );
		changesCollector.listenForChanges( email );
		changesCollector.listenForChanges( mobile );
		changesCollector.listenForChanges( accessGroup );
		
		updateButtons();
		updateFields();
		
	}


	public StuffListModel getModel() { return model; }
	public void setModel( StuffListModel model ) { this.model = model; }

	private void dataToView() {

		if ( this.shownUser != null ) {
			firstName.setValue( this.shownUser.getFirstName());
			lastName.setValue( this.shownUser.getLastName());

			if ( this.shownUser.getBirthday() != null )
				birthday.setValue( this.shownUser.getBirthday().toDate());
			else
				birthday.setValue( null );

			street.setValue( this.shownUser.getAddress() != null ? this.shownUser.getAddress().getStreet() : null );
			pobox.setValue( this.shownUser.getAddress() != null ? this.shownUser.getAddress().getPoBox() : null );
			index.setValue( this.shownUser.getAddress() != null ? this.shownUser.getAddress().getIndex() : null );
			city.setValue( this.shownUser.getAddress() != null ? this.shownUser.getAddress().getCity() : null );
			country.setValue( this.shownUser.getAddress() != null ? this.shownUser.getAddress().getCountryCode() : null );
			accessGroup.setValue( this.shownUser.getAccessGroup() != null ? this.shownUser.getAccessGroup() : null );
			
			email.setValue( this.shownUser.getEmail());
			mobile.setValue( this.shownUser.getPhoneNumber());

			// If this is a new user than it is necessary to set up Code.
			// user can change code if he wants
//			if ( this.shownUser.getId() <= 0 ) {
//				model.setUserCode( this.shownUser );
//			}
//			code.setValue( this.shownUser.getCode());
			
			initAccessGroupCombo( this.shownUser );
			
		}

	}

	private void viewToData() {

		if ( this.shownUser != null ) {
//			this.shownUser.setCode( code.getValue());
			this.shownUser.setFirstName( firstName.getValue());
			this.shownUser.setLastName( lastName.getValue());

			this.shownUser.setBirthday( birthday.getValue() != null ? new LocalDate( birthday.getValue()) : null );

			if ( this.shownUser.getAddress() == null ) {
				this.shownUser.setAddress( new Address());
			}

			this.shownUser.getAddress().setStreet( street.getValue());
			this.shownUser.getAddress().setPoBox( pobox.getValue());
			this.shownUser.getAddress().setIndex( index.getValue());
			this.shownUser.getAddress().setCity( city.getValue());
			this.shownUser.getAddress().setCountryCode(( String )country.getValue());
			this.shownUser.setAccessGroup(( AccessGroups )accessGroup.getValue());

			this.shownUser.setEmail( email.getValue());
			this.shownUser.setPhoneNumber( mobile.getValue());

		}

	}



	@Override
	public void currentWasSet( OrgUser user ) {

		if ( logger.isDebugEnabled()) logger.debug( "StuffView received event about user selection. Ready to show:" + user );

		this.shownUser = user;
		setVisible( user != null );
		dataToView();

		if ( model.isEditMode()) {
			
			model.clearEditMode();
			
		}

		updateButtons();
		updateFields();

		
		if ( user != null && user.getId() <= 0 ) {

			logger.debug( "New OrgUser created. Need to be edited and saved!" );

			editSavePressed();

		}

	}


	@Override
	public void wasAdded(OrgUser user) {}
	@Override
	public void wasChanged(OrgUser user) {}
	@Override
	public void wasDeleted(OrgUser user) {}
	@Override
	public void wholeListChanged() {}

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


		deleteButton = new Button();
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

	private void deleteUser( final OrgUser user ) {

		// Confirm removal
		String template = model.getApp().getResourceStr( "confirm.personnel.delete" );
		Object[] params = { user.getFirstAndLastNames() };
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

							OrgUser deletedUser = model.delete( user );
							if ( deletedUser != null) {

								String template = model.getApp().getResourceStr( "notify.personnel.delete" );
								Object[] params = { deletedUser.getFirstAndLastNames() };
								template = MessageFormat.format( template, params );

								Notification.show( template );

							} else {
								// Failed to delete
								// Failed to update
								String template = model.getApp().getResourceStr( "personnel.errors.item.delete" );
								Object[] params = { user.getFirstAndLastNames() };
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

		boolean enable = model.isEditMode();
		
//		code.setEnabled( enable );
		firstName.setEnabled( enable );
		lastName.setEnabled( enable );
		birthday.setEnabled( enable );

		street.setEnabled( enable );
		pobox.setEnabled( enable );
		index.setEnabled( enable );
		city.setEnabled( enable );
		country.setEnabled( enable );
		accessGroup.setEnabled( enable );

		email.setEnabled( enable );
		mobile.setEnabled( enable );
		
		updateAccountFields();

		if ( enable )
			changesCollector.startToListen();
		else
			changesCollector.stopToListen();
		
		
		
	}

	private void updateAccountFields() {
		
		if ( this.shownUser != null ) {

			if ( this.shownUser.getAccount() != null ) {

				noAccountMsg.setVisible( false );
				
				usrname.setVisible( model.getSecurityContext().hasViewPermission( 
						FunctionalityType.ACCOUNTS_MGMT, model.getSessionOwner(), model.getSelectedOrg()));
				usrname.setValue( this.shownUser.getAccount().getUsrName());
//				usrname.setEnabled( model.isEditMode());
				
				accountButton.setCaption( "Edit Account" ); //model.getApp().getResourceStr( "account.button.edit" ));
				
			} else {

				noAccountMsg.setVisible( true );

				usrname.setVisible( false );
				usrname.setValue( "" );

				accountButton.setCaption( "Create Account" ); //model.getApp().getResourceStr( "account.button.create" ));
				
			}

			accountButton.setVisible( 
					model.isEditMode()
					&& model.getSecurityContext().hasViewPermission( 
							FunctionalityType.ACCOUNTS_MGMT, model.getSessionOwner(), model.getSelectedOrg())
			);
			
		} else {
			
			// Nothing to show
			
			noAccountMsg.setVisible( false );
			usrname.setVisible( false );
			accountButton.setVisible( false );
			
		}
			
				
		
	}
	
	
	private void editSavePressed() {

		logger.debug( "EditClose button has been pressed!  EditMode was " + model.getEditMode());

		switch ( model.getEditMode()) {
			case ADD:
	
				viewToData();
				
				addUser( shownUser );
	
				
				break;
	
			case EDIT:
				
				updateUser( shownUser );
	
				break;
			case VIEW:
	
				model.setEditMode( this.shownUser.getId() > 0 ? EditModeType.EDIT : EditModeType.ADD );
				
				break;
			default:
				break;
		}

		updateButtons();
		updateFields();
		
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

				deleteUser( shownUser );
				
				break;
			default:
				break;
		}

		updateButtons();
		updateFields();
		
	}

	
	// This method call AccountView dialog
	private void accountMgmt() {
	
		AccountView view = new AccountView( model ); 
		model.getApp().addWindow( view );
		
		view.addCloseListener( new CloseListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void windowClose(CloseEvent e) {
				
				if ( model.accountWasChanged()) {
					changesCollector.changed();
				}
				
				logger.debug( "AccountView has been closed" );
				updateAccountFields();				
			}
			
		});
		
	}

	private boolean validate() {

		if ( !lastName.isValid() || lastName.getValue() == null || lastName.getValue().trim().length() == 0 ) {   
				
			Notification.show( "Error", "Field cannot be empty!", Type.ERROR_MESSAGE );

			lastName.selectAll();
			lastName.focus();

			return false;
		}
		
		if ( !birthday.isValid()) {   
			
			Notification.show( "Error", "Field cannot be empty!", Type.ERROR_MESSAGE );

			birthday.focus();

			return false;
		}

		if ( !mobile.isValid() || mobile.getValue() == null || mobile.getValue().trim().length() == 0 ) {
			
			mobile.selectAll();
			mobile.focus();

			if ( !email.isValid() || email.getValue() == null || email.getValue().trim().length() == 0 ) {
				
				Notification.show( "Error", "Field cannot be empty!", Type.ERROR_MESSAGE );

				mobile.selectAll();
				mobile.focus();
//				email.selectAll();
//				email.focus();

				return false;
			}
		}
				
		return true;
	}
	
	private  void initAccessGroupCombo( OrgUser selectedUser ) {

		accessGroup.removeAllItems();
		
		for ( AccessGroups group : AccessGroups.values()) {
				
			accessGroup.addItem( group );
			accessGroup.setItemCaption( group, model.getApp().getResourceStr( "accessrights.group.name." + group.name().toLowerCase())); // group.name());
			
			if ( logger.isDebugEnabled()) {
				logger.debug( "Resource name: '" + "accessrights.group.name." + group.name().toLowerCase() + "'" );
			}
				
		}

		accessGroup.setValue( selectedUser.getAccessGroup());
	
	}

	@Override
	public void initiateAdd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initiateCopy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initiateEdit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initiateDelete() {

		model.setViewMode();
		deleteCancelPressed();
		
	}

	// For now just to save code
	private OrgUser addUser( OrgUser user ) {

		OrgUser newUser = null;
		
		if ( changesCollector.wasItChanged()) {

			if ( validate()) {
				// Changes must be stored
				viewToData();

				if ( this.shownUser.getId() <= 0 ) {

					// This is new record. It must be added
					
					newUser = model.add( this.shownUser );
					
					if ( newUser != null ) {

						model.setViewMode();
						currentWasSet( newUser );
						
					} else {

						String template = model.getApp().getResourceStr( "general.error.add.header" );
						Object[] params = { this.shownUser.getFirstAndLastNames() };
						template = MessageFormat.format( template, params );

						Notification.show( template, Notification.Type.ERROR_MESSAGE );

					}

				}
			} 
		
		}
		
		
		return newUser;
	}
	
	private OrgUser updateUser( OrgUser user ) {

		OrgUser updatedUser = null;
		
		if ( changesCollector.wasItChanged()) {

			if ( validate()) {
				// Changes must be stored
				viewToData();

				if ( this.shownUser.getId() > 0 ) {

					// This is existing record update
					
					updatedUser = model.update( user );
					
					if ( updatedUser != null ) {

						model.setViewMode();
						currentWasSet( updatedUser );

					} else {

						String template = model.getApp().getResourceStr( "general.error.update.header" );
						Object[] params = { this.shownUser.getFirstAndLastNames() };
						template = MessageFormat.format( template, params );

						Notification.show( template, Notification.Type.ERROR_MESSAGE );
						
					}
				}		
			} 
		
		} else {
			model.setViewMode();
		}
		
		
		return updatedUser;
	}
	
	
}
