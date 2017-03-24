package com.c2point.tools.ui.personnelmgmt;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.SettingsFacade;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ItemStatus;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Category;
import com.c2point.tools.entity.tool.Manufacturer;
import com.c2point.tools.entity.tool.Tool;
import com.c2point.tools.ui.util.AbstractDialog;
import com.c2point.tools.ui.util.CustomGridLayout;
import com.c2point.tools.ui.util.DoubleField;
import com.c2point.tools.ui.util.IntegerField;
import com.c2point.tools.ui.util.AbstractModel.EditModeType;
import com.c2point.tools.utils.lang.Locales;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.AbstractSelect.NewItemHandler;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
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
	private TextField		birthday;

	/*	Address fields	*/
	private TextField		street;
	private TextField		pobox;
	private TextField		index;
	private TextField		city;
	private ComboBox		country;

	private TextField		email;
	private TextField		phone;
	
	private ComboBox		accessGroup;
	
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

		CustomGridLayout subContent = new CustomGridLayout();
		subContent.setMargin( true );
		subContent.setSpacing( true );

		center();
		
		firstName = new TextField();
		firstName.setNullRepresentation( "" );
		firstName.setImmediate( true );

		lastName = new TextField();
		lastName.setRequired( true );
		lastName.setRequiredError( "The Field may not be empty." );
		lastName.setNullRepresentation( "" );
		lastName.setImmediate( true );

		birthday = new TextField();
		birthday.setRequired( false );
//		birthday.set.setDateFormat( "dd.MM.yyyy" );
		birthday.setNullRepresentation( "" );
		birthday.setImmediate(true);

		street = new TextField();
		street.setNullRepresentation( "" );
		street.setImmediate(true);

		pobox = new TextField();
		pobox.setNullRepresentation( "" );
		pobox.setImmediate(true);

		index = new TextField();
		index.setNullRepresentation( "" );
		index.setImmediate(true);

		city = new TextField();
		city.setNullRepresentation( "" );
		city.setImmediate(true);
		
		country = new ComboBox( "", Locales.getISO3166Container());
		country.setInputPrompt( "No country selected" );
		country.setItemCaptionPropertyId( Locales.iso3166_PROPERTY_NAME);
		country.setItemCaptionMode( ItemCaptionMode.PROPERTY);
		country.setItemIconPropertyId( Locales.iso3166_PROPERTY_FLAG);
		country.setFilteringMode( FilteringMode.CONTAINS );
		country.setImmediate( true );

		email = new TextField();
		email.setRequired(true);
		email.setNullRepresentation( "" );
		email.setImmediate(true);

		phone = new TextField();
		phone.setRequired(true);
		phone.setNullRepresentation( "" );
		phone.setImmediate(true);

		accessGroup = new ComboBox( "", Locales.getISO3166Container());

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

		
		
		
		subContent.addField( model.getApp().getResourceStr( "personnel.caption.firstname" ) + ":", firstName );
		subContent.addField( model.getApp().getResourceStr( "personnel.caption.lastname" ) + ":", lastName );
		subContent.addField( model.getApp().getResourceStr( "personnel.caption.birthday" ) + ":", birthday );
		subContent.addSeparator();

		/*	Address fields	*/
		subContent.addField( model.getApp().getResourceStr( "personnel.caption.street" ) + ":", street );
		subContent.addField( model.getApp().getResourceStr( "personnel.caption.pobox" ) + ":", pobox );
		subContent.addField( model.getApp().getResourceStr( "personnel.caption.index" ) + ":", index );
		subContent.addField( model.getApp().getResourceStr( "personnel.caption.city" ) + ":", city );
		subContent.addField( model.getApp().getResourceStr( "personnel.caption.country" ) + ":", country );
		subContent.addSeparator();

		subContent.addField( model.getApp().getResourceStr( "general.caption.email" ) + ":", email );
		subContent.addField( model.getApp().getResourceStr( "general.caption.phone" ) + ":", phone );
		subContent.addSeparator();
		subContent.addField( model.getApp().getResourceStr( "personnel.caption.group" ) + ":", accessGroup );
		
		
		VerticalLayout vl = new VerticalLayout();
		
		setContent( vl );
		
		vl.addComponent( subContent );
		subContent.addSeparator();
		

	
		vl.addComponent( getButtonBar());
		
		dataToView();
		
		getChangesCollector().addField( country );
		
		updateFields();
		
		addUsersFieldsListeners();
		
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

	private void addUsersFieldsListeners() {
		
		
	}
	
	private void dataToView() {

		if ( editedUser != null ) {
			

	/*			
			initManufacturers( editedTool );
			initModels( editedTool );
			initCategories( editedTool );
*/			
			firstName.setValue( StringUtils.defaultString( editedUser.getFirstName()));
			lastName.setValue( StringUtils.defaultString( editedUser.getLastName()));
			birthday.setValue( editedUser.getBirthday() != null ? editedUser.getBirthday().toString( "dd.MM.yyyy" ) : "" );

			if ( editedUser.getAddress() != null ) {
				street.setValue( StringUtils.defaultString( editedUser.getAddress().getStreet()));
				pobox.setValue( StringUtils.defaultString( editedUser.getAddress().getPoBox()));
				index.setValue( StringUtils.defaultString( editedUser.getAddress().getIndex()));
				city.setValue( StringUtils.defaultString( editedUser.getAddress().getCity()));
				country.setValue( StringUtils.defaultString( editedUser.getAddress().getCountryCode()));
			}

			email.setValue( StringUtils.defaultString( editedUser.getEmail()));
			phone.setValue( StringUtils.defaultString( editedUser.getPhoneNumber()));
			
			accessGroup.setValue( editedUser.getAccessGroup());

		}
	}


	private boolean viewToData() {

		boolean res = false;
		
		res = true;
		

		return res;
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
		
	}
/*	
	private void initStatuses( ItemStatus status ) {
		boolean freeAllowed = SettingsFacade.getInstance().getBoolean( model.getSelectedOrg(), "FreeStatusAllowed", false );
		
		for ( ItemStatus tmpStatus: ItemStatus.values()) {

			if ( !freeAllowed && tmpStatus == ItemStatus.FREE ) {
				// Do not add if free status is not allowed
				continue;
			}
			statusBox.addItem( tmpStatus );
			
			statusBox.setItemCaption( tmpStatus, tmpStatus.toString( model.getApp().getSessionData().getBundle()));

		}
		
		// Select if specified
		if ( status != null ) {
			statusBox.setValue( status );
		}
		
	}
*/	

	@Override
	public void okPressed() {
/*
		switch ( model.getEditMode()) {
			case ADD:

				if ( viewToData()) {
				
					if ( model.addToolAndItem( editedItem ) != null ) {
						logger.debug( "Tool And Item were added" );
						close();
					} else {
						String template = model.getApp().getResourceStr( "toolsmgmt.errors.item.add" );
						Object[] params = { editedItem.getFullName() };
						template = MessageFormat.format( template, params );
						
						new Notification( 
								model.getApp().getResourceStr( "general.error.header" ),
								template,
								Notification.Type.ERROR_MESSAGE, 
								true 
						).show( Page.getCurrent());
					}
				}
				
				break;
				
			case COPY:
				if ( viewToData()) {
				
					if ( model.addItem( editedItem ) != null ) {
						logger.debug( "Item was added to existing Tool" );
						close();
					} else {
						new Notification( 
								model.getApp().getResourceStr( "general.error.header" ),
								model.getApp().getResourceStr( "toolsmgmt.errors.item.add" ),
								Notification.Type.ERROR_MESSAGE, 
								true 
						).show( Page.getCurrent());
					}
				}
			
				break;
			case EDIT:
				
				if ( viewToData()) {
				
					if ( model.updateItem( editedItem ) != null ) {
						logger.debug( "Item was edited" );
						close();
					}
	
				}
				
				break;
			default:
				break;
		}

*/		
	}

	@Override
	public void cancelPressed() {

		close();
		
	}

	@Override
	public void dlgClosed() {

		logger.debug( "ToolsEdit Dialog has been closed!" );
		
	}

	/*
	 * If Manufacturer or Model were added (not existed before) than new Tool shall be created 
	 */
/*
	private Tool getNewTool() {
		
		Tool newTool = editedTool;
		
		if ( editedTool == null || editedTool.getId() > 0 ) {
			// New Tool shall be created
			newTool = new Tool( model.getSelectedOrg());
			
		}
		
		return newTool;
	}
*/	
}
