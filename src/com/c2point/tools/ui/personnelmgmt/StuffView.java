package com.c2point.tools.ui.personnelmgmt;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDate;
import org.vaadin.dialogs.ConfirmDialog;

import com.c2point.tools.entity.person.Address;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.ui.AbstractModel;
import com.c2point.tools.utils.lang.Locales;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class StuffView extends FormLayout implements StuffChangedListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( StuffView.class.getName());


	private StuffListModel	model;

	private TextField	code;
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

	private Button		editcloseButton;
	private Button		deleteButton;

	private boolean		editedFlag;
	private OrgUser		shownUser;

	public StuffView( StuffListModel model ) {
		super();

		setModel( model );

		initView();

		model.addChangedListener( this );

		model.clearEditMode();
	}

	private void initView() {

		setSpacing( true );
		this.setMargin( true );
		setSizeUndefined();

		code = new TextField( "Code:" );
		code.setRequired(true);
		code.setRequiredError("The Field may not be empty.");
		code.setNullRepresentation( "" );
		code.setImmediate( true );

		firstName = new TextField( "First name:" );
		firstName.setNullRepresentation( "" );
		firstName.setImmediate( true );

		lastName = new TextField( "Last name:" );
		lastName.setRequired(true);
		lastName.setRequiredError("The Field may not be empty.");
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

		email = new TextField( "Email:" );
		email.setRequired(true);
		email.setNullRepresentation( "" );
		email.setImmediate(true);

		mobile = new TextField( "Mobile:" );
		mobile.setRequired(true);
		mobile.setNullRepresentation( "" );
		mobile.setImmediate(true);

		addComponent( code );
		addComponent( firstName );
		addComponent( lastName );
		addComponent( birthday );
		addComponent( street );
		addComponent( pobox );
		addComponent( index );
		addComponent( city );
		addComponent( country );
		addComponent( email );
		addComponent( mobile );

		addComponent( getButtonsBar());

		updateButtons();
		enableFields();
	}


	public StuffListModel getModel() { return model; }
	public void setModel( StuffListModel model ) { this.model = model; }

	public boolean isEditedFlag() { return editedFlag;}
	public void setEditedFlag(boolean editedFlag) {this.editedFlag = editedFlag; }

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

			email.setValue( this.shownUser.getEmail());
			mobile.setValue( this.shownUser.getPhoneNumber());

			// If this is a new user than it is necessary to set up Code.
			// user can change code if he wants
			if ( this.shownUser.getId() <= 0 ) {
				model.setUserCode( this.shownUser );
			}
			code.setValue( this.shownUser.getCode());

		}

	}

	private void viewToData() {

		if ( this.shownUser != null ) {
			this.shownUser.setCode( code.getValue());
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
			
			updateButtons();
			enableFields();
		}

		if ( user != null && user.getId() <= 0 ) {

			logger.debug( "New OrgUser created. Need to be edited and saved!" );

			editClose();

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

		editcloseButton = new Button();

		editcloseButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick( ClickEvent event) {

				editClose();

			}
		});


		deleteButton = new Button( "Delete" );
		deleteButton.setIcon( new ThemeResource("icons/16/reject.png"));

		deleteButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick( ClickEvent event) {
				if ( !model.isEditMode() && StuffView.this.shownUser != null ) {

					deletePerson();
				}
			}
		});


		toolBarLayout.addComponent( editcloseButton);
		toolBarLayout.addComponent( deleteButton);

		return toolBarLayout;
	}

	private void deletePerson() {
		// Confirm removal
		String template = model.getApp().getResourceStr( "confirm.personnel.delete" );
		Object[] params = { this.shownUser.getFirstAndLastNames() };
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

							OrgUser deletedUser = model.delete( StuffView.this.shownUser );
							if ( deletedUser != null) {

								String template = model.getApp().getResourceStr( "notify.personnel.delete" );
								Object[] params = { deletedUser.getFirstAndLastNames() };
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

		code.setEnabled( model.isEditMode() );
		firstName.setEnabled( model.isEditMode() );
		lastName.setEnabled( model.isEditMode() );
		birthday.setEnabled( model.isEditMode() );

		street.setEnabled( model.isEditMode() );
		pobox.setEnabled( model.isEditMode() );
		index.setEnabled( model.isEditMode() );
		city.setEnabled( model.isEditMode() );
		country.setEnabled( model.isEditMode() );

		email.setEnabled( model.isEditMode() );
		mobile.setEnabled( model.isEditMode() );

	}

	private void editClose() {

		logger.debug( "EditClose button has been pressed!" );

		model.swipeEditMode();

		if ( model.isEditMode()) {

			setEditedFlag( false );

			listenForChanges( code );
			listenForChanges( firstName );
			listenForChanges( lastName );
			listenForChanges( birthday );
			listenForChanges( street );
			listenForChanges( pobox );
			listenForChanges( index );
			listenForChanges( city );
			listenForChanges( country );
			listenForChanges( email );
			listenForChanges( mobile );

		} else {
			if ( isEditedFlag()) {

				// Changes must be stored
				viewToData();

				if ( this.shownUser.getId() > 0 ) {
					// This is existing record update
					OrgUser newUser = model.update( this.shownUser );
					if ( newUser == null ) {

						String template = model.getApp().getResourceStr( "general.errors.update.header" );
						Object[] params = { this.shownUser.getFirstAndLastNames() };
						template = MessageFormat.format( template, params );

						Notification.show( template, Notification.Type.ERROR_MESSAGE );

					} else {
						currentWasSet( null );
					}
				} else {
					// This is new record. It must be added
					OrgUser newUser = model.add( this.shownUser );
					if ( newUser == null ) {

						String template = model.getApp().getResourceStr( "general.errors.add.header" );
						Object[] params = { this.shownUser.getFirstAndLastNames() };
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
