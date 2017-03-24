package com.c2point.tools.ui.personnelmgmt;

import java.text.MessageFormat;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDate;
import org.vaadin.dialogs.ConfirmDialog;

import com.c2point.tools.entity.access.AccessGroups;
import com.c2point.tools.entity.access.FunctionalityType;
import com.c2point.tools.entity.person.Address;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.ui.accessrightsmgmt.AccessMgmtView;
import com.c2point.tools.ui.accountmgmt.AccountView;
import com.c2point.tools.ui.listeners.EditInitiationListener;
import com.c2point.tools.ui.listeners.StuffChangedListener;
import com.c2point.tools.ui.util.BoldLabel;
import com.c2point.tools.ui.util.ChangesCollector;
import com.c2point.tools.ui.util.AbstractModel.EditModeType;
import com.c2point.tools.ui.util.CustomGridLayout;
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

public class DetailsView extends CustomGridLayout implements StuffChangedListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( DetailsView.class.getName());


	private StuffListModel	model;
	private OrgUser			shownUser;

	private BoldLabel	firstName;
	private BoldLabel	lastName;
	private BoldLabel	birthday;

	private BoldLabel	street;
	private BoldLabel	pobox;
	private BoldLabel	index;
	private BoldLabel	city;
	private BoldLabel	country;

	private BoldLabel	email;
	private BoldLabel	mobile;

	private BoldLabel	accessGroup;
	
	private BoldLabel	usrname;
	


	public DetailsView( StuffListModel model ) {
		super();

		setModel( model );

		initView();

		model.addListener( this );
		
	}

	private void initView() {

		setSpacing( true );
		setMargin( true );
//		setSizeUndefined();

		firstName = new BoldLabel();
		firstName.setImmediate( true );

		lastName = new BoldLabel();
		lastName.setImmediate( true );

		birthday = new BoldLabel();
		birthday.setImmediate(true);

		street = new BoldLabel();
		street.setImmediate(true);

		pobox = new BoldLabel();
		pobox.setImmediate(true);

		index = new BoldLabel();
		index.setImmediate(true);

		city = new BoldLabel();
		city.setImmediate(true);

		country = new BoldLabel();
		country.setImmediate( true );

		email = new BoldLabel();
		email.setImmediate(true);

		mobile = new BoldLabel();
		mobile.setImmediate(true);

		accessGroup = new BoldLabel();

		usrname = new BoldLabel();
		usrname.setImmediate( true );
		
		firstName = addLabelValueComponent( model.getApp().getResourceStr( "personnel.caption.firstname" ) + ":" );
		lastName = addLabelValueComponent( model.getApp().getResourceStr( "personnel.caption.lastname" ) + ":" );
		birthday = addLabelValueComponent( model.getApp().getResourceStr( "personnel.caption.birthday" ) + ":" );
		addSeparator();

		street = addLabelValueComponent( model.getApp().getResourceStr( "personnel.caption.street" ) + ":" );
		pobox = addLabelValueComponent( model.getApp().getResourceStr( "personnel.caption.pobox" ) + ":" );
		index = addLabelValueComponent( model.getApp().getResourceStr( "personnel.caption.postcode" ) + ":" );
		city = addLabelValueComponent( model.getApp().getResourceStr( "personnel.caption.city" ) + ":" );
		country = addLabelValueComponent( model.getApp().getResourceStr( "personnel.caption.country" ) + ":" );
		addSeparator();

		email = addLabelValueComponent( model.getApp().getResourceStr( "general.caption.email" ) + ":" );
		mobile = addLabelValueComponent( model.getApp().getResourceStr( "general.caption.phone" ) + ":" );
		addSeparator();

		accessGroup = addLabelValueComponent( model.getApp().getResourceStr( "personnel.caption.group" ) + ":" );
		usrname = addLabelValueComponent( model.getApp().getResourceStr( "login.username" ) + ":" );
		addSeparator();
		
	}

	public StuffListModel getModel() { return model; }
	public void setModel( StuffListModel model ) { this.model = model; }

	@Override
	public void currentWasSet( OrgUser user ) {

		if ( logger.isDebugEnabled()) logger.debug( "StuffView received event about user selection. Ready to show:" + user );

		this.shownUser = user;

		dataToView();

	}


	@Override
	public void wasAdded(OrgUser user) {}
	@Override
	public void wasChanged(OrgUser user) {
		
		if ( logger.isDebugEnabled()) logger.debug( "StuffView received event 'Personnel was edited': " + user );

		this.shownUser = user;

		dataToView();
		
	}
	@Override
	public void wasDeleted(OrgUser user) {}
	@Override
	public void wholeListChanged() {}

	private void dataToView() {

		setVisible( this.shownUser != null );
		
		if ( this.shownUser != null ) {
			
			firstName.setValue( StringUtils.defaultString( this.shownUser.getFirstName()));
			lastName.setValue( StringUtils.defaultString( this.shownUser.getLastName()));

			birthday.setValue( shownUser.getBirthday() != null ? shownUser.getBirthday().toString( "dd.MM.yyyy" ) : "" );

			if ( this.shownUser.getAddress() != null ) {
				street.setValue( StringUtils.defaultString( this.shownUser.getAddress().getStreet()));
				pobox.setValue( StringUtils.defaultString( this.shownUser.getAddress().getPoBox()));
				index.setValue( StringUtils.defaultString( this.shownUser.getAddress().getIndex()));
				city.setValue( StringUtils.defaultString( this.shownUser.getAddress().getCity()));
				country.setValue( StringUtils.defaultString( this.shownUser.getAddress().getCountryCode()));
			} else {
				street.setValue( "" );
				pobox.setValue( "" );
				index.setValue( "" );
				city.setValue( "" );
				country.setValue( "" );
			}

			email.setValue( StringUtils.defaultString( this.shownUser.getEmail()));
			mobile.setValue( StringUtils.defaultString( this.shownUser.getPhoneNumber()));
			
			
			if ( this.shownUser.getAccessGroup() != null ) {
				accessGroup.setValue( 
					StringUtils.defaultString( 
							model.getApp().getResourceStr( "accessrights.group.name." + this.shownUser.getAccessGroup().name().toLowerCase())));
			} else {
				accessGroup.setValue( "" );
			}
			
			if ( this.shownUser.getAccount() != null ) {
				usrname.setValue( 
					StringUtils.defaultString( 
						this.shownUser.getAccount().getUsrName()));
			} else {
				usrname.setValue( "" );
			}

		}

	}

	
	
}
