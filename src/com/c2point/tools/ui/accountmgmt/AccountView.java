package com.c2point.tools.ui.accountmgmt;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.AuthenticationFacade;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.tool.Manufacturer;
import com.c2point.tools.ui.personnelmgmt.StuffListModel;
import com.c2point.tools.ui.util.AbstractModel;
import com.c2point.tools.ui.util.ChangesCollector;
import com.c2point.tools.utils.PasswordGenerator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class AccountView extends Window {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( AccountView.class.getName());

	private StuffListModel		model;
	private ChangesCollector	changesCollector = new ChangesCollector();
	
	private TextField		usrname;
	private TextField		password;
//	private TextField		openpassword;

//	private CheckBox		showPassword;
	private Button			generateButton;
	private Button			okButton;
	private Button			cancelButton;
	
	public AccountView( StuffListModel model ) {
		
		super();
		this.model = model;
		
		init();
		
	}
	private void init() {
		
		setModal( true );
		setCaption( getCaption( model.getEditMode()));
		center();
		
		VerticalLayout content = new VerticalLayout();
		content.setMargin( true );
		
		usrname = new TextField( "Username" + ":" );
		usrname.setWidth( "20em" );
		usrname.setRequired( true );
		usrname.setRequiredError( "The Field may not be empty." );		
		usrname.setImmediate( true );
/*
		String pwdStr = "";
		// Wrap it in a property data source
		final ObjectProperty<String> property =
		new ObjectProperty<String>(pwdStr);		
*/		
		
		password = new TextField( "Password" + ":" );
		password.setWidth( "20em" );
		password.setRequired( true );
		password.setRequiredError( "The Field may not be empty." );		
		password.setImmediate( true );
		
/*
		openpassword = new TextField( "Password" + ":" );
		openpassword.setPropertyDataSource( property );
		openpassword.setWidth( "20em" );
		openpassword.setRequired( true );
		openpassword.setRequiredError( "The Field may not be empty." );		
		openpassword.setImmediate( true );

		openpassword.setImmediate(true);		
		
		showPassword = new CheckBox( "Show password? ");
		showPassword.addValueChangeListener( new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				
				password.setVisible( showPassword.getValue());
				openpassword.setVisible( !showPassword.getValue());
		
			}
		});
*/		
		generateButton = new Button( "Generate new password" );
		generateButton.addStyleName( "small" );
		generateButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				generatePassword();
				
			}
			
			
		});
		
		
		FormLayout fl = new FormLayout();
		fl.setSizeUndefined();		
		
		fl.addComponent( usrname );
		fl.addComponent( password );
//		fl.addComponent( openpassword );
//		fl.addComponent( showPassword );
		fl.addComponent( generateButton );
		
		
		
		content.addComponent( fl );
		content.addComponent( getButtonBar());

		setContent( content );
		
		
		changesCollector.listenForChanges( usrname );
		changesCollector.listenForChanges( password );

		dataToView();
		
	}

	
	private String getCaption( AbstractModel.EditModeType type ) {
		
		String caption; 
		
		switch ( type ) {
			case ADD:
				caption = "Add Account";
				break;
			case EDIT:
				caption = "Edit Account";
				break;
			case VIEW:
			default:
				caption = "View Account";
				break;
		}
		
		return caption;
	}
	
	private Component getButtonBar() {
		
		HorizontalLayout bar = new HorizontalLayout();
		
		okButton = new Button( "OK" );
		cancelButton = new Button( "Cancel" );
		
		okButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				if ( changesCollector.wasItChanged()) {
					// Name or Password were changed. Try to validate and save
					
					if ( validateName()) {
						if ( validatePwd()) {
							if ( model.saveAccount( usrname.getValue(), password.getValue())) {
								// Name and password are valid. Try to save
								AccountView.this.close();
							} else {
								
								Notification.show( "Error", "Cannot save account", Type.ERROR_MESSAGE );
								
							}
						}
					}
						
				} else {
					// Nothing was changed. Close and exit
					AccountView.this.close();
				}
				
			}
			
			
		});
		
		cancelButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				AccountView.this.close();
				
			}
			
			
		});
		
		bar.addComponent( okButton );
		bar.addComponent( cancelButton );
		
		return bar;
	}

	private boolean validateName() {

		boolean bRes = false;
		
		// Check that username != null and length is suitable
		bRes = 
				usrname.isValid()
			&&	
				usrname.getValue().trim().length() >= 1;
				
		if ( bRes ) {
			// Check that there is no duplicated name
			bRes = model.checkName( usrname.getValue().trim());
			
			if ( !bRes ) {

				Notification.show( "Error", "Username exists already. Select other name", Type.ERROR_MESSAGE );
				usrname.selectAll();
				usrname.focus();
				
			}
		} else {

			Notification.show( "Error", "Invalid Username. Length of Username shall be more than 6 characters", Type.ERROR_MESSAGE );
			usrname.selectAll();
			usrname.focus();
			
		}
		
		return bRes;
	}
	
	private boolean validatePwd() {
		boolean bRes = false;

		bRes = 
				password.isValid()
			&&	
				password.getValue().trim().length() >= 8;

		if ( bRes ) {
			// Check that there is no duplicated name
//					bRes = model.checkPassword( password.getValue().trim());	
			
			if ( !bRes ) {

//						Notification.show( "Error", "Username exists already. Select other name", Type.ERROR_MESSAGE );
				
			}
		} else {

			Notification.show( "Error", "Invalid Password. Length of Password shall be more than 8 characters", Type.ERROR_MESSAGE );
			password.selectAll();
			password.focus();
			
		}
		
		return bRes;
	}

	private void generateUserName( OrgUser user ) {
		
		int DEFAULT_NAME_LENGTH = 8;  // Will be 8 or 9
		
		if ( user != null ) {
			
			String newName =  
			    StringUtils.defaultString( user.getLastName()).trim().toLowerCase()
			  + StringUtils.defaultString( user.getFirstName()).trim().toLowerCase();
			
			// Make username length 8 or 9
			int size = newName.length();
			
			if ( size == DEFAULT_NAME_LENGTH - 1 ) {
				newName = newName.concat( ".x" );
			} else if ( size < DEFAULT_NAME_LENGTH ) {
				newName = newName.concat( "." ).concat( StringUtils.repeat( 'x', DEFAULT_NAME_LENGTH - size - 1 ));
			} else {  // In this case lenght >= DEFAULT_NAME_LENGTH
				newName = newName.toLowerCase().substring( 0,  8 );   
			}
			logger.debug( "Normalized name (before checking for existance): " + newName );
			
			int count = 1;
			String ext = "";
			while( AuthenticationFacade.getInstance().findByUserName( newName.concat( ext )) != null ) {

				ext = "." + Integer.toString( count++ );
				
			};
			
			if ( count > 1 ) {
				// Means name shall be changed by adding ext
				newName = newName.concat( ext );
			}
			logger.debug( "Normalized name (AFTER checking for existance): " + newName );
			
			usrname.setValue( newName );
			
		}
		
	}
	
	private void generatePassword() {
		
		password.setValue( PasswordGenerator.getNewPassword());
		password.selectAll();
		password.focus();
		
	}
	
	private void dataToView() {

		OrgUser user = model.getSelectedUser();
		
		if ( user != null && user.getAccount() != null ) {
				
			usrname.setValue( user.getAccount().getUsrName());
			password.setValue( user.getAccount().getPwd());

			changesCollector.clearChanges();
			
		} else {
			
			usrname.setValue( "" );
			password.setValue( "" );

			generateUserName( user );
			generatePassword();
		}

	}
	
}
