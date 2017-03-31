package com.c2point.tools.ui.accountmgmt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.authentication.Account;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.ui.changescollecor.FieldsChangeCollector;
import com.c2point.tools.ui.personnelmgmt.StuffListModel;
import com.c2point.tools.ui.util.AbstractDialog;
import com.c2point.tools.ui.util.AbstractModel;
import com.c2point.tools.utils.PasswordGenerator;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class AccountEditDlg extends AbstractDialog {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( AccountEditDlg.class.getName());

	private StuffListModel		model;
	private OrgUser				editedUser;
	
	private TextField		usrname;
	private TextField		password;

	private Button			generateButton;
	
	public AccountEditDlg( StuffListModel model, OrgUser user ) {
		
		super();
		this.model = model;
		this.editedUser = user;
		
		initUI();
		
	}
	private void initUI() {
		
		setCaption( getCaption( model.getEditMode()));
		setModal( true );
		setClosable( false );
		center();
		
		VerticalLayout content = new VerticalLayout();
		content.setMargin( true );
		
		usrname = new TextField( model.getApp().getResourceStr( "login.username" ) + ":" );
		usrname.setWidth( "20em" );
		usrname.setRequired( true );
		usrname.setRequiredError( "The Field may not be empty." );		
		usrname.setImmediate( true );
		
		password = new TextField( model.getApp().getResourceStr( "login.password" ) + ":" );
		password.setWidth( "20em" );
		password.setRequired( true );
		password.setRequiredError( "The Field may not be empty." );		
		password.setImmediate( true );
		
		generateButton = new Button( "Generate new password" );
		generateButton.addStyleName( "small" );
		generateButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				password.setValue( PasswordGenerator.generatePassword());
				
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

		dataToView();
		
		getChangesCollector().addField( usrname );
		getChangesCollector().addField( password );

		
	}

	public FieldsChangeCollector getChangesCollector() {
		return super.getChangesCollector();
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
	
	public void okPressed() {

		// Name or Password were changed. Try to validate and save
			
		if ( validate()) {
				
			//				model.saveAccount( usrname.getValue(), password.getValue());
/*				
					// Name and password are valid. Try to save
					AccountView.this.close();
				} else {
					Notification.show( "Error", "Cannot save account", Type.ERROR_MESSAGE );
						
				}
*/
			if ( editedUser.getAccount() == null ) {
				
				editedUser.addAccount( new Account());
				
				
			}
			editedUser.getAccount().setUsrName( usrname.getValue());
			editedUser.getAccount().setPwd( password.getValue());

			model.setAccountChanged();
			AccountEditDlg.this.close();
			
		} else {
			model.clearAccountChanged();
		}
				
	}				
		
	public void cancelPressed() {

			AccountEditDlg.this.close();
			
	}

	private boolean validateName() {

		boolean bRes = false;
		
		String name = usrname.getValue();
		
		// Check that username != null and length is suitable
		bRes = PasswordGenerator.validateUsrName( name );
				
		if ( bRes ) {
			// Check the status of such usrname in Accounts DB
			switch ( model.checkName( name, editedUser )) {
				case DUPLICATE:
					// Show Notification, update usrname and stay in dialog
					usrname.setValue( model.updateUserName( name ));

					Notification.show( "Error", "Username exists already. Select other name", Type.ERROR_MESSAGE );
					usrname.selectAll();
					usrname.focus();
					
					bRes = false;
					break;
				case EXIST:
					bRes = true;
					break;
				case NO:
					bRes = true;
					break;
					
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
		
		// Check that pwd != null and length is suitable
		if ( PasswordGenerator.validatePassword( password.getValue())) {
			
			bRes = true;
			
		} else {

			Notification.show( 
					"Error", 
					"Invalid Password. Length of Password shall be more than " 
						+ PasswordGenerator.PASSWORD_LENGTH
						+ " characters",
					Type.ERROR_MESSAGE );
			
			password.selectAll();
			password.focus();
			
		}
		
		return bRes;
	}
	private void dataToView() {

		if ( this.editedUser != null && this.editedUser.getAccount() != null ) {
				
			usrname.setValue( this.editedUser.getAccount().getUsrName());
			password.setValue( this.editedUser.getAccount().getPwd());

		} else {
			
			usrname.setValue( PasswordGenerator.generateUserName(
					editedUser.getLastName(), 
					editedUser.getFirstName()
			));
			password.setValue( PasswordGenerator.generatePassword());
		}

	}

	private boolean validate() {
		
		return validateName() && validatePwd();
	}
	
	

	
	@Override
	public void dlgClosed() {

		logger.debug( "AccountEditDlg has been closed!" );
		
	}

}
