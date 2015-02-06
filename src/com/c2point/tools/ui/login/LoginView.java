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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class LoginView  extends AbstractMainView {

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
					
					if ( user != null ) {
						
						// There is one user only for this account
						processLogIn( user );
						
					} else {
						
						// There are more than one user for this account. Selection necessary
						selectOneUser( loggedAccount );
						
					}
					
				} else /* if ( loggedAccount == null ) */ {

					// Login Failed!!!
					processLogInFAILED();
					
				}
			}
		});
		
	}
	
	private void selectOneUser( Account account ) {

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
					processLogIn( selectedUser );
					
				} else {
					
					// Nothing has been selected. Return back to Login screen
					returnBackToLoginScreen();
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
				returnBackToLoginScreen();				
			}
		});
		
		
		
	}
	
	private void returnBackToLoginScreen() {
		enterCredentials();
	}

	private void processLogInFAILED() {

		getInventoryUI().deleteCookies();

		loginComponent.invalid();
		
	}
	
	
}
