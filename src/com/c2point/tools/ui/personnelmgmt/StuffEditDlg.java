package com.c2point.tools.ui.personnelmgmt;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDate;

import com.c2point.tools.entity.access.AccessGroups;
import com.c2point.tools.entity.access.FunctionalityType;
import com.c2point.tools.entity.person.Address;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.ui.accessrightsmgmt.AccessMgmtView;
import com.c2point.tools.ui.accountmgmt.AccountEditDlg;
import com.c2point.tools.ui.util.AbstractDialog;
import com.c2point.tools.ui.util.CustomGridLayout;
import com.c2point.tools.ui.util.AbstractModel.EditModeType;
import com.c2point.tools.utils.lang.Locales;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractFocusable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.VerticalLayout;

public class StuffEditDlg extends AbstractDialog {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( StuffEditDlg.class.getName());

	private StuffListModel	model;
	
	private OrgUser			editedUser = null;
	
	// User data
	private TextField		firstName;
	private TextField		lastName;
	private DateField		birthday;

	/*	Address fields	*/
	private TextField		street;
	private TextField		pobox;
	private TextField		index;
	private TextField		city;
	private ComboBox		country;

	private TextField		email;
	private TextField		phone;
	
	private ComboBox		accessGroup;
	private Button			accessButon;

	private TextField		usrName;
	private Button			accountButon;
	
	public StuffEditDlg( StuffListModel model, OrgUser user, EditModeType editModeType ) {
		super();
		
		this.model = model;
		this.model.setEditMode( editModeType );
			
		this.editedUser = user;
		
		initUI();
	}

	private void initUI() {
		
		if ( this.editedUser == null ) {
			logger.error( "No User selected!" );
		}

		setCaption( getHeader());
		setModal( true );
		setClosable( true );
//		setWidth( "35%" );

		CustomGridLayout subContent = new CustomGridLayout();
		subContent.setMargin( true );
		subContent.setSpacing( true );
		subContent.setWidthUndefined();

		center();
		
		firstName = new TextField();
		firstName.setNullRepresentation( "" );
		firstName.setImmediate( true );

		lastName = new TextField();
		lastName.setRequired( true );
		lastName.setRequiredError( "The Field may not be empty." );
		lastName.setNullRepresentation( "" );
		lastName.setImmediate( true );

		birthday = new DateField();
		birthday.setRequired( false );
		birthday.setDateFormat( "dd.MM.yyyy" );
//		birthday.setTextFieldEnabled( true );
		birthday.setWidth( "15ex" );
		birthday.setImmediate(true);

		street = new TextField();
		street.setNullRepresentation( "" );
		street.setImmediate(true);

		pobox = new TextField();
		pobox.setNullRepresentation( "" );
		pobox.setWidth( "8ex" );
		pobox.setImmediate(true);

		index = new TextField();
		index.setNullRepresentation( "" );
		index.setWidth( "8ex" );
		index.setImmediate(true);

		city = new TextField();
		city.setNullRepresentation( "" );
		city.setImmediate(true);
		
		country = new ComboBox();
		country.setContainerDataSource( Locales.getISO3166Container());		
		country.setInputPrompt( "No country selected" );
		country.setItemCaptionPropertyId( Locales.iso3166_PROPERTY_NAME);
		country.setItemCaptionMode( ItemCaptionMode.PROPERTY);
//		country.setItemIconPropertyId( Locales.iso3166_PROPERTY_FLAG);
		country.setFilteringMode( FilteringMode.CONTAINS );
		country.setImmediate( true );
		country.setWidth( "30ex" );

		email = new TextField();
		email.setRequired(true);
		email.setRequiredError(model.getApp().getResourceStr( "general.error.field.empty" ));
		email.setNullRepresentation( "" );
		email.setImmediate(true);

		phone = new TextField();
		phone.setRequired(true);
		phone.setRequiredError(model.getApp().getResourceStr( "general.error.field.empty" ));
		phone.setNullRepresentation( "" );
		phone.setImmediate(true);

		accessGroup = new ComboBox();

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

		accessButon = new Button( model.getApp().getResourceStr( "personnel.caption.access.change" ));
		accessButon.setImmediate(true);
		
		usrName = new TextField();
		phone.setImmediate(true);
		
		accountButon = new Button( model.getApp().getResourceStr( "personnel.caption.account.change" ));
		accountButon.setImmediate(true);
		
		
		
		subContent.addField( model.getApp().getResourceStr( "personnel.caption.firstname" ) + ":", firstName );
		subContent.addField( model.getApp().getResourceStr( "personnel.caption.lastname" ) + ":", lastName );
		subContent.addField( model.getApp().getResourceStr( "personnel.caption.birthday" ) + ":", birthday );
		subContent.addSeparator();

		/*	Address fields	*/
		subContent.addField( model.getApp().getResourceStr( "personnel.caption.street" ) + ":", street );
		subContent.addField( model.getApp().getResourceStr( "personnel.caption.pobox" ) + ":", pobox );
		subContent.addField( model.getApp().getResourceStr( "personnel.caption.postcode" ) + ":", index );
		subContent.addField( model.getApp().getResourceStr( "personnel.caption.city" ) + ":", city );
		subContent.addField( model.getApp().getResourceStr( "personnel.caption.country" ) + ":", country );
		subContent.addSeparator();

		subContent.addField( model.getApp().getResourceStr( "general.caption.email" ) + ":", email );
		subContent.addField( model.getApp().getResourceStr( "general.caption.phone" ) + ":", phone );
		subContent.addSeparator();
		subContent.addField( model.getApp().getResourceStr( "personnel.caption.group" ) + ":", accessGroup );

		if ( model.isEditMode() 
				&& 
			 model.getSecurityContext().hasEditPermission( FunctionalityType.ACCOUNTS_MGMT, model.getSelectedOrg())) {
			subContent.addLastInLineField( accessButon );
		}
		
		subContent.addSeparator();
		
		subContent.addField( model.getApp().getResourceStr( "login.username" ) + ":", usrName );
		
		if ( model.isEditMode() 
				&& 
			 model.getSecurityContext().hasEditPermission( FunctionalityType.ACCOUNTS_MGMT, model.getSelectedOrg())) {
			subContent.addLastInLineField( accountButon );
		}
		
		VerticalLayout vl = new VerticalLayout();
		
		setContent( vl );
		
		vl.addComponent( subContent );
		subContent.addSeparator();
		
		
		
		
		vl.addComponent( getButtonBar());
		
		dataToView();
		
		getChangesCollector().addField( firstName );
		getChangesCollector().addField( lastName );
		getChangesCollector().addField( birthday );
		getChangesCollector().addField( street );
		getChangesCollector().addField( pobox );
		getChangesCollector().addField( index );
		getChangesCollector().addField( city );
		getChangesCollector().addField( country );
		getChangesCollector().addField( email );
		getChangesCollector().addField( phone );
		getChangesCollector().addField( accessGroup );
		getChangesCollector().addField( country );
		
		updateFields();
		
		addListeners();
		
	}

	private String getHeader() {
		
		String str;
		
		switch ( model.getEditMode()) {
			case ADD:
				str = model.getApp().getResourceStr( "personnel.caption.add" );
				break;
			case EDIT:
				str = model.getApp().getResourceStr( "personnel.caption.edit" );
				break;
			case VIEW:
			default:
				str = model.getApp().getResourceStr( "personnel.caption.view" );
				break;
		}

		return str;
	}

	private void addListeners() {
		
		accessButon.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				AccessMgmtView view = new AccessMgmtView( model ); 
				model.getApp().addWindow( view );
				
				view.addCloseListener( new CloseListener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void windowClose(CloseEvent e) {
						
						getChangesCollector().setChanges();
						
						logger.debug( "AccessMgmtView has been closed" );
					}
					
				});
			}
			
		});
		
		
		accountButon.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				StuffEditDlg.this.editedUser.setFirstName( firstName.getValue());
				StuffEditDlg.this.editedUser.setLastName( lastName.getValue());
				
				AccountEditDlg view = new AccountEditDlg( model, editedUser ); 
				model.getApp().addWindow( view );
				
				
				view.addCloseListener( new CloseListener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void windowClose(CloseEvent e) {

						logger.debug( "AccountView has been closed" );
						
						if ( model.wasAccountChanged()) {
							// Enable "OK" button if account was changed
							getButtonBar().getOk().setEnabled( true );
							
							// Update 'Usrname' field if account was changed
							if ( editedUser.getAccount() != null ) {
								usrName.setReadOnly( false );
								usrName.setValue( StringUtils.defaultString( editedUser.getAccount().getUsrName()));
								usrName.setReadOnly( true );
							}


						}
					}
					
				});
			}
			
		});
		
	}
	
	private void dataToView() {

		if ( editedUser != null ) {
			
			firstName.setValue( StringUtils.defaultString( editedUser.getFirstName()));
			lastName.setValue( StringUtils.defaultString( editedUser.getLastName()));
			birthday.setValue( editedUser.getBirthday() != null ? editedUser.getBirthday().toDate() : null );

			if ( editedUser.getAddress() != null ) {
				street.setValue( StringUtils.defaultString( editedUser.getAddress().getStreet()));
				pobox.setValue( StringUtils.defaultString( editedUser.getAddress().getPoBox()));
				index.setValue( StringUtils.defaultString( editedUser.getAddress().getIndex()));
				city.setValue( StringUtils.defaultString( editedUser.getAddress().getCity()));
				country.setValue( StringUtils.defaultString( editedUser.getAddress().getCountryCode()));
			}

			email.setValue( StringUtils.defaultString( editedUser.getEmail()));
			phone.setValue( StringUtils.defaultString( editedUser.getPhoneNumber()));
			
			initGroups( editedUser );
			
			if ( editedUser.getAccount() != null ) {
				usrName.setValue( StringUtils.defaultString( editedUser.getAccount().getUsrName()));
			}

		}
	}


	private boolean viewToData() {

		boolean res = false;
		
		if ( this.editedUser != null && validate()) {

			this.editedUser.setFirstName( WordUtils.capitalizeFully( firstName.getValue()));
			this.editedUser.setLastName( WordUtils.capitalizeFully( lastName.getValue()));
			this.editedUser.setBirthday( birthday.getValue() != null ? new LocalDate( birthday.getValue()) : null );
			
			if ( this.editedUser.getAddress() == null ) {
				this.editedUser.setAddress( new Address());
			}

			this.editedUser.getAddress().setStreet( street.getValue());
			this.editedUser.getAddress().setPoBox( pobox.getValue());
			this.editedUser.getAddress().setIndex( index.getValue());
			this.editedUser.getAddress().setCity( city.getValue());
			this.editedUser.getAddress().setCountryCode(( String )country.getValue());
			
			this.editedUser.setEmail( email.getValue());
			this.editedUser.setPhoneNumber( phone.getValue());

			this.editedUser.setAccessGroup(( AccessGroups )accessGroup.getValue());
			
			
			res = true;
				
			
		}

		return res;
	}
	
	private boolean validate() {
		
		boolean bRes = false;
		
		AbstractComponent fieldToSelect = null;
		
		if ( !lastName.isValid()) {
			fieldToSelect = lastName;
		} else if ( email.isEmpty() && phone.isEmpty()) {
			fieldToSelect = email;
		} else if ( !accessGroup.isValid()) {
			fieldToSelect = accessGroup;
		} else {
			// Othervise data are OK to store
			bRes = true;
		}
		
		// First wrong field to set focus and to select content iif possible
		if ( fieldToSelect != null ) {
			if ( fieldToSelect instanceof AbstractFocusable )
				(( AbstractFocusable )fieldToSelect ).focus();
			if ( fieldToSelect instanceof TextField )
				(( TextField )fieldToSelect ).selectAll();
		}
		
		return bRes;
	}

	private void updateFields() {
		
		boolean disallowEdit = ( model.getEditMode() == EditModeType.VIEW );
		
		firstName.setReadOnly( disallowEdit );
		lastName.setReadOnly( disallowEdit );
		birthday.setReadOnly( disallowEdit );
		street.setReadOnly( disallowEdit );
		pobox.setReadOnly( disallowEdit );
		index.setReadOnly( disallowEdit );
		city.setReadOnly( disallowEdit );
		country.setReadOnly( disallowEdit );
		email.setReadOnly( disallowEdit );
		phone.setReadOnly( disallowEdit );
		accessGroup.setReadOnly( disallowEdit );
		usrName.setReadOnly( true );
		
	}

	private  void initGroups( OrgUser selectedUser ) {

		accessGroup.removeAllItems();
		
		for ( AccessGroups group : AccessGroups.values()) {
				
			accessGroup.addItem( group );
			accessGroup.setItemCaption( group, model.getApp().getResourceStr( "accessrights.group.name." + group.name().toLowerCase())); // group.name());
			
			if ( logger.isDebugEnabled()) {
				logger.debug( "Resource name: '" + "accessrights.group.name." + group.name().toLowerCase() + "'" );
			}
				
		}

		if ( selectedUser != null ) {
			accessGroup.setValue( selectedUser.getAccessGroup());
		}
	
	}


	@Override
	public void okPressed() {
		
		OrgUser user;
		
		if ( viewToData()) {
		
			switch ( model.getEditMode()) {
				case ADD:
					user = model.add( editedUser );
					if ( user != null ) {
						logger.debug( "New User was added" );
						
						// Now Account must be added or user added to account
						
						
						
						
						
						
						close();
					} else {
						String template = model.getApp().getResourceStr( "personnel.errors.add" );
						Object[] params = { editedUser.getLastAndFirstNames() };
						template = MessageFormat.format( template, params );
						
						new Notification( 
								model.getApp().getResourceStr( "general.error.header" ),
								template,
								Notification.Type.ERROR_MESSAGE, 
								true 
						).show( Page.getCurrent());
					}
					
					break;
					
				case EDIT:
					
					
						if ( model.update( editedUser ) != null ) {
							logger.debug( "Item was edited" );
							close();
						} else {
							String template = model.getApp().getResourceStr( "personnel.errors.add" );
							Object[] params = { editedUser.getLastAndFirstNames() };
							template = MessageFormat.format( template, params );
							
							new Notification( 
									model.getApp().getResourceStr( "general.error.header" ),
									template,
									Notification.Type.ERROR_MESSAGE, 
									true 
							).show( Page.getCurrent());
						}
		
					
					break;
				default:
					break;
			}
		}
		
	}

	@Override
	public void cancelPressed() {

		close();
		
	}

	@Override
	public void dlgClosed() {

		logger.debug( "ToolsEdit Dialog has been closed!" );
		
	}

}
