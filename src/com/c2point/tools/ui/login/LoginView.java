package com.c2point.tools.ui.login;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.InventoryUI;
import com.c2point.tools.datalayer.AuthenticationFacade;
import com.c2point.tools.entity.authentication.Account;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.ui.AbstractMainView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class LoginView  extends AbstractMainView {

	private static Logger logger = LogManager.getLogger( LoginView.class.getName());

	LoginComponent loginComponent;
	
	private final ArrayList<LoginListener> listeners;
	
	public LoginView( InventoryUI ui ) {
		super( ui );
		
		listeners = new ArrayList<LoginListener>();
	}
	
	public void initUI() {

		this.setSizeFull();
		this.setSpacing( true );
		
		loginComponent = new LoginComponent( this.getInventoryUI());
		loginComponent.setHeight( "400px" );
		loginComponent.setHeight( "400px" );
		
		loginComponent.setName( getInventoryUI().getNameFromCookies());
		loginComponent.setPwd( getInventoryUI().getPwdFromCookies());
		loginComponent.setRemember( getInventoryUI().getRememberFlagFromCookies());
		loginComponent.setLanguage( getInventoryUI().getLanguageFromCookies());
		
		addComponent(loginComponent);
		setComponentAlignment( loginComponent, Alignment.MIDDLE_CENTER );

		loginComponent.addLoginButtonListener( new ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				if ( login()) {
					// Store credentials as cookies
					getInventoryUI().storeInCookies( 
							loginComponent.getName(),
							loginComponent.getPwd(),
							loginComponent.toRemember(),
							loginComponent.getLanguage()
					);
				} else {
					getInventoryUI().deleteCookies();
				}
				
			}
		}); 
	}

	private boolean login() {
		boolean bRes = false;
		if ( logger.isDebugEnabled()) logger.debug( "Started login..." );

		Account account = null;
		// Login
		account = AuthenticationFacade.getInstance()
					.authenticateUser( loginComponent.getName(), loginComponent.getPwd());
		if ( account != null && account.getUser() != null ) {
			if ( logger.isDebugEnabled()) logger.debug( "  Logged-In (web): " + account.getUser().getFirstAndLastNames());
			
			getInventoryUI().getSessionData().setOrgUser( account.getUser());

			fireLoginEvent( account.getUser());

			bRes = true;
			
		} else {
			loginComponent.invalid();
		}
		
		if ( logger.isDebugEnabled()) logger.debug( "... login end. Succeeded? " + bRes );
		return bRes;
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

}
