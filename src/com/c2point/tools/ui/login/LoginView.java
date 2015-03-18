package com.c2point.tools.ui.login;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.InventoryUI;
import com.c2point.tools.datalayer.AuthenticationFacade;
import com.c2point.tools.datalayer.TransactionsFacade;
import com.c2point.tools.entity.authentication.Account;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.ui.AbstractMainView;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class LoginView  extends AbstractMainView {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( LoginView.class.getName());
	
	private LoginComponent			loginComponent;
	private SelectAccountComponent	selectorComponent;
	
	private final ArrayList<LoginListener> listeners;
	
	public LoginView( InventoryUI ui ) {
		super( ui );
		
		listeners = new ArrayList<LoginListener>();
	}
	
	public void initUI() {

		this.setSizeFull();
		this.setSpacing( true );
		
		enterCredentials();
		
	}

	private Account login() {
		
		Account account = null;

		if ( logger.isDebugEnabled()) logger.debug( "Started login..." );

		// Login
		account = AuthenticationFacade.getInstance()
					.authenticateUser( loginComponent.getName(), loginComponent.getPwd());
		
		if ( logger.isDebugEnabled()) logger.debug( "... login end. Succeeded? " + ( account != null ));
		
		return account;
	}

	private void fireLoginEvent( OrgUser user ) {
		for ( LoginListener l : listeners ) {
			l.newUserLogged( user );
		}
	}

	public void addLoginListener( LoginListener l ) {
		listeners.add( l );
	}

	public void removeLoginListener( LoginListener l ) {
		listeners.remove( l );
	}

	public interface LoginListener {
		public void newUserLogged( OrgUser user );
	}

	@Override
	protected void initDataAtStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initDataReturn() {
		// TODO Auto-generated method stub
		
	}

	private void processLogIn( OrgUser user ) {
		
		// This account belongs to one User only. Proceed as loggen in!
		if ( logger.isDebugEnabled()) logger.debug( "  Logged-In (web): " + user.getFirstAndLastNames());
		
		// Store credentials as cookies
		getInventoryUI().storeInCookies( 
				loginComponent.getName(),
				loginComponent.getPwd(),
				loginComponent.toRemember(),
				loginComponent.getLanguage()
		);		
		
		getInventoryUI().getSessionData().setOrgUser( user );

		TransactionsFacade.getInstance().writeLogin( user );
		
		fireLoginEvent( user );

	}

	private void enterCredentials() {

		try {
			removeComponent( selectorComponent );
		} catch ( Exception e ) {
			
		}
		
		if ( loginComponent == null ) {
			loginComponent = new LoginComponent( this.getInventoryUI());
		}
		
		addComponent(loginComponent);
		setComponentAlignment( loginComponent, Alignment.MIDDLE_CENTER );

		loginComponent.addLoginButtonListener( new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				Account loggedAccount = login();
				
				if ( loggedAccount != null ) {
					
					OrgUser user = loggedAccount.getUser();
					
					LoginSelectedUserHandler handler = new LoginSelectedUserHandler();
					
					if ( user != null ) {
						
						// There is one user only for this account
						handler.userSelected( user );
						
					} else {
						
						// There are more than one user for this account. Selection necessary
						selectOneUser( loggedAccount, handler );
						
					}
					
				} else /* if ( loggedAccount == null ) */ {

					// Login Failed!!!
					getInventoryUI().deleteCookies();

					loginComponent.invalid();
					
				}
			}
		});
		
		loginComponent.addForgotButtonListener( new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 2L;

			@Override
			public void buttonClick( ClickEvent event ) {

				// User forgot username/password
				forgotPassword();
			}
		});
		
		
	}
	
	interface UserSelectionIf {
		void userSelected( OrgUser user );
		void noSelection();
	}
	
	private void selectOneUser( Account account, final UserSelectionIf proceed ) {

		// More than one "user" for this account. Select one
		try {
			removeComponent( loginComponent );
		} catch ( Exception e ) {
			
		}
		
		
		if ( selectorComponent == null ) {
			selectorComponent = new SelectAccountComponent( this.getInventoryUI());
		}

		selectorComponent.setSelectingAccounts( account.getUsers());

		addComponent( selectorComponent );
		setComponentAlignment( selectorComponent, Alignment.MIDDLE_CENTER );

		selectorComponent.addLoginButtonListener( new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				
				OrgUser selectedUser = selectorComponent.getSelected();
			
				if ( selectedUser != null ) {

					// There is one user only for this account
					proceed.userSelected( selectedUser );
					
				} else {
					
					// Nothing has been selected. Return back to Login screen
					proceed.noSelection();
				}
				
				
			}
		});
		
		selectorComponent.addCancelButtonListener( new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				
				// Cancel has been pressed. Return back to Login screen
				proceed.noSelection();
			}
		});
		
		
		
	}
	
	private void forgotPassword() {
		
		String usrname = loginComponent.getName();
		
		logger.debug( "Username '" + usrname + "' pressed Forgot Password button" );
		
		// Username must be entered firstly
		if ( usrname != null && usrname.trim().length() > 0 ) {
			// Find account and appropriate user
			Account account = AuthenticationFacade.getInstance().findByUserName( usrname.trim());
			if ( account != null ) {
				
				// Get all User-Organisations for this account
				//  For all of them ...
				boolean bRes = false;
				for ( OrgUser user : account.getUsers()) {
					
					// ... send password
					bRes = sentCredentialsByEmail( account, user ); 
					if ( !bRes ) {
						
						// Was not possible to send credentials to the user directly
						// Credentials will be sent to organisation's responsible (for the service) person
						OrgUser respPerson = user.getOrganisation().getResponsible();
						
						bRes = sentCredentialsByEmail( account, user, respPerson );

						
					}
					
				}
				
				if ( bRes ) {

					// TODO: Show Notification that credentials were sent
					
				} else {

					// TODO: Show ERROR Notification that credentials were NOT sent
					
				}
				
				
				
				
			} else {

				// Username was not found!
				logger.debug( "Specified user '" + usrname + "' was not found!" );
				// TODO show Error/Warning dialog
				
			}
			
			
		} else {
			// Username is not entered. Nothing to do. Ask to enter
			logger.debug( "Username must be entered" );
			Notification.show( 
					this.getInventoryUI().getResourceStr( "general.warning.header" ),
					this.getInventoryUI().getResourceStr( "login.warning.no.usrname" ),
					Notification.Type.WARNING_MESSAGE
			);		
		}
	}

	class LoginSelectedUserHandler implements UserSelectionIf {

		@Override
		public void userSelected( OrgUser selectedUser ) {

			processLogIn( selectedUser );
			
		}

		@Override
		public void noSelection() {

			enterCredentials();
			
		}
		
	}
	
	class GetPasswordSelectedUserHandler implements UserSelectionIf {

		@Override
		public void userSelected( OrgUser selectedUser ) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void noSelection() {
			// TODO Auto-generated method stub
			
		}
		
	}

	private boolean sentCredentialsByEmail( Account account, OrgUser user ) {
		return sentCredentialsByEmail( account, user, user );
	}
	
	private boolean emailIsValid( String email ) {
		
		boolean bRes = ( email != null && email.trim().length() > 4 );
		
		if ( bRes ) {
			
			try {
				new EmailValidator( "" ).validate( email );
				bRes = true;
			} catch ( InvalidValueException e ) {
				
			}
		
		}
		
		return bRes;
		
	}
	
	private boolean sentCredentialsByEmail( Account account, OrgUser user, OrgUser receiver ) {
		
		boolean bRes = false;
		
		if ( receiver != null && emailIsValid( receiver.getEmail())) {
			logger.debug( "Receiver: " + receiver.getFirstAndLastNames() + ". "
						+ "InventTori Service password for user " + user.getFirstAndLastNames() + " was sent!"
			);

			
			
		} else {
			logger.debug( "FAILED to send credentials of " + user.getFirstAndLastNames() + " to " + receiver.getFirstAndLastNames());
			
		}
						
				
		
		return bRes;
	}

}
