package com.c2point.tools;

import javax.servlet.http.Cookie;

import com.c2point.tools.configuration.Configuration;
import com.c2point.tools.configuration.DBupdate;
import com.c2point.tools.entity.SessionData;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.ui.MainView;
import com.c2point.tools.ui.login.LoginView;
import com.c2point.tools.ui.login.LoginView.LoginListener;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletResponse;
import com.vaadin.ui.UI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("serial")
@Theme("inventory")
public class InventoryUI extends UI implements LoginListener {

	private static Logger logger = LogManager.getLogger( InventoryUI.class.getName());
	
	private MainView mainView;
	
	private SessionData sessionData;
	
	@Override
	protected void init(VaadinRequest request) {

		Configuration.readConfiguration(); // this );
//		DBupdate.updateDatabase();
		
		sessionData = new SessionData();
		
		addStyleName( "main" );

/*		
		addListener( new Window.CloseListener() {
			   @Override
			    public void windowClose( CloseEvent e ) {
			       logger.debug( "Closing the application" );
			       getMainWindow().getApplication().close();
			    } 
			});		
*/
		
		// Gets current cookies
		Cookie[] cookies = request.getCookies();
		if ( cookies != null ) {
			getFromCookies( cookies );
		} else {
			if ( logger.isDebugEnabled()) logger.debug( "Cookies were read already!" );
		}
		
		LoginView loginView = new LoginView( this );

//		putView( loginView );
		loginView.addLoginListener( this );

		setContent( loginView );
		
//		loginView.click();

	}

	@Override
	public void close() {
		
		super.close();
		
	}

	public SessionData getSessionData() {
		if ( sessionData == null ) {
			sessionData = new SessionData();
		}

		return sessionData;
	}

	public OrgUser getSessionOwner() {
		
		if ( sessionData != null ) 
			
			return sessionData.getOrgUser();
		
		return null;
		
	}
	
	public void deleteCookies() {
		storeInCookies( null, null, false, "" );
	}
	
	public void storeInCookies( String name, String pwd, boolean toRemember, String language ) {

		Cookie nameCookie = new Cookie( "storedname", name );
		Cookie pwdCookie = new Cookie( "storedpwd", pwd );
		Cookie rememberCookie = new Cookie( "storedrememberflag", Boolean.toString( toRemember ));
		Cookie languageCookie = new Cookie( "storedlanguage", language );
		
		
		nameCookie.setPath( "/" );
		pwdCookie.setPath( "/" );
		rememberCookie.setPath( "/" );
		languageCookie.setPath( "/" );
		
		if ( name != null && toRemember ) {
			// Store cookies
			nameCookie.setMaxAge( 2592000 ); // 30 days
			pwdCookie.setMaxAge( 2592000 ); // 30 days
			rememberCookie.setMaxAge( 2592000 ); // 30 days
			languageCookie.setMaxAge( 2592000 ); // 30 days
			if ( logger.isDebugEnabled()) logger.debug( "Cookies will be stored" );
		} else {
			// Delete cookies
			nameCookie.setMaxAge( 0 );
			pwdCookie.setMaxAge( 0 ); // 30 days
			rememberCookie.setMaxAge( 0 ); // 30 days
			languageCookie.setMaxAge( 0 ); // 30 days
			if ( logger.isDebugEnabled()) logger.debug( "Cookies will be deleted" );
		}

		VaadinServletResponse response = 
				   (VaadinServletResponse) VaadinService.getCurrentResponse();
		
		response.addCookie( nameCookie );
		response.addCookie( pwdCookie );
		response.addCookie( rememberCookie );
		response.addCookie( languageCookie );

		if ( logger.isDebugEnabled()) logger.debug( "Cookies were added to response" );
	}

	private void getFromCookies( Cookie[] cookies ) {
		if ( cookies != null ) {
			String name;
			for ( int i=0; i < cookies.length; i++ ) {
				name = cookies[ i ].getName();
				if ("storedname".equals( name )) {
					// Log the user in automatically
					storedName = cookies[ i ].getValue();
					if ( logger.isDebugEnabled()) logger.debug( "StoredName found and = " + storedName );
				} else if ("storedpwd".equals( name )) {
					storedPwd = cookies[ i ].getValue();
					if ( logger.isDebugEnabled()) logger.debug( "StoredPwd found and = " + storedPwd );
				} else if ("storedrememberflag".equals( name )) {
					String str = cookies[ i ].getValue();
					storedRememberFlag = Boolean.parseBoolean( str );
					if ( logger.isDebugEnabled()) logger.debug( "StoredRememberFlag found and = " + storedRememberFlag );
				} else if ("storedlanguage".equals( name )) {
					storedLanguage = cookies[ i ].getValue();
					if ( logger.isDebugEnabled()) logger.debug( "StoredLanguage found and = " + storedLanguage );
				} else {
					if ( logger.isDebugEnabled()) logger.debug( "Wrong cookies were found!" );
				}
			}
		} else {
			if ( logger.isDebugEnabled()) logger.debug( "There is no cookies stored!" );
		}
	}
	
	private String storedName = null;
	private String storedPwd = null;
	private boolean storedRememberFlag = false;
	private String storedLanguage = null;
	
	public String getNameFromCookies() { return storedName; }
	public String getPwdFromCookies() { return storedPwd; }
	public boolean getRememberFlagFromCookies() { return storedRememberFlag; }
	public String getLanguageFromCookies() { return storedLanguage; }

	public String getResourceStr( String key ) {
		
		return getResourceStr( key, null );
		
	}
	
	public String getResourceStr( String key, String defStr ) {
		
		try {
			return this.getSessionData().getBundle().getString( key );
		} catch (Exception e) {
			logger.error(  "Could not find string resource '" + key + "'. Default will be used" );
			
		}
		return ( defStr != null ? defStr : "" ); 
			
	}

	@Override
	public void newUserLogged( OrgUser user ) {

		if ( mainView == null) {
			mainView = new MainView();
			mainView.initWindow();
		}

		setContent( mainView );
		
	}	
}