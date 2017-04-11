package com.c2point.tools.ui.login;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.InventoryUI;
import com.c2point.tools.ui.util.Lang;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

public class LoginComponent extends CustomComponent {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( LoginComponent.class.getName());

	private InventoryUI	app;
	
	private TextField 		user;
	private PasswordField 	pwd;
	private ComboBox		languageSelect;
	private CheckBox 		rememberMeButton;
	private Button 			okButton;
	private Button 			forgotButton;
	
	
	public LoginComponent( InventoryUI app ) {
		super();
		
		this.app = app;
		initView();

		fillContent();
	}

	public void addLoginButtonListener( Button.ClickListener listener ) {
		okButton.addClickListener( listener );
	}
	
	public void addForgotButtonListener( Button.ClickListener listener ) {
		forgotButton.addClickListener( listener );
	}
	
	private void initView() {

		Panel panel = new Panel();
		setCompositionRoot( panel );
//		setWidth(  "500px" );
//		setHeight(  "240px" );
//		setWidth(  "40em" );
		setWidthUndefined();
		setHeight(  "20ex" );
		
		user = new TextField();
		pwd = new PasswordField();
//		user.setWidth("200px");
//		pwd.setWidth("200px");
		user.setWidth( "20em" );
		pwd.setWidth( "20em" );

		languageSelect = new ComboBox( null, Lang.getAvailableLocales());
		languageSelect.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		languageSelect.setNullSelectionAllowed(false);
		languageSelect.setImmediate(true);

		languageSelect.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange( ValueChangeEvent event ) {
				Locale locale = ( Locale )languageSelect.getValue();
				
				ResourceBundle bundle = Lang.loadBundle( "com.c2point.tools.ui.resources.WebResources", locale );
				if ( bundle == null ) {
					locale = Lang.DEFAULT_LOCALE;
					bundle = Lang.loadBundle( "com.c2point.tools.ui.resources.WebResources", locale );
					
				} 
				
				app.getSessionData().setBundle( bundle );
				app.getSessionData().setLocale( locale );
					
				if ( bundle != null ) {
				
					refreshCaptions();
					
				}
				
				
				
			}
		});

		
		
		
		rememberMeButton = new CheckBox();
		
		okButton = new Button();
		Label glue = new Label( " " );
//		glue.setWidth( "100%" );
		glue.setWidth( "10em" );
		forgotButton = new Button();
		forgotButton.addStyleName( BaseTheme.BUTTON_LINK );

		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth( "100%" );
		hl.addComponent( okButton );
		hl.addComponent( glue );
//		hl.addComponent( forgotButton );
		
		
		VerticalLayout vl = new VerticalLayout();
		
		vl.addComponent( user );
		vl.addComponent( pwd );
		vl.addComponent( languageSelect );
		vl.addComponent( rememberMeButton );
		vl.addComponent( hl );
		
		// Set the size as undefined at all levels
		vl.setSpacing( true );
		vl.setMargin( true );
		
		panel.setContent( vl );
		
	}
	
	public String getName() {
		return user.getValue().toString();
	}
	public void setName( String nameStr ) {
		if ( nameStr != null ) this.user.setValue( nameStr );
	}
	
	public String getPwd() {
		return pwd.getValue().toString();
	}
	public void setPwd( String pwdStr ) {
		if ( pwdStr != null ) this.pwd.setValue( pwdStr );
	}
	
	public boolean toRemember() {
		return ( Boolean )rememberMeButton.getValue();
	}
	public void setRemember( boolean toRemember ) {
		this.rememberMeButton.setValue( toRemember );
	}
	
	public String getLanguage() {
		return this.languageSelect.getValue().toString();
	}
	
	public void setLanguage( String langStr ) {
		if ( langStr != null ) {
			for ( Locale l : Lang.getAvailableLocales()) {
				if ( langStr.equals( l.toString())) {
					languageSelect.setValue( l );
					break;
				}
			}
		} else {
			languageSelect.setValue( Lang.DEFAULT_LOCALE );
		}
//		refreshCaptions();
	}
	
	public void invalid() {
		
		// Show message
		Notification.show( 
				app.getResourceStr( "login.errors.credentials.header" ),
				app.getResourceStr( "login.errors.credentials.body" ),
				Notification.Type.WARNING_MESSAGE
		);		
		// Clear password
		pwd.setValue( "" );
		// Select name and set focus
		user.selectAll();
	}

	private void refreshCaptions() {
		getCompositionRoot().setCaption( app.getResourceStr( "login.caption" ));

		user.setCaption( app.getResourceStr( "login.username" ) + ":" );
		pwd.setCaption( app.getResourceStr( "login.password" ) + ":" );
		languageSelect.setCaption( app.getResourceStr( "login.language" ) + ":" );
		rememberMeButton.setCaption( app.getResourceStr( "login.rememberme" ));
		okButton.setCaption( app.getResourceStr( "login.login" ));
		forgotButton.setCaption( "Forgot username or password?" );

		languageSelect.setItemCaption( Lang.LOCALE_EN, app.getResourceStr( "language.en" ));
		languageSelect.setItemCaption( Lang.LOCALE_FI, app.getResourceStr( "language.fi" ));
		languageSelect.setItemCaption( Lang.LOCALE_ET, app.getResourceStr( "language.es" ));
		languageSelect.setItemCaption( Lang.LOCALE_RU, app.getResourceStr( "language.ru" ));
		
		languageSelect.setValue( app.getSessionData().getLocale());
//		languageSelect.requestRepaint();

		if ( app != null && app.getPage() != null ) {
			app.getPage().setTitle( app.getSessionData().getBundle().getString( "mainWindow.title" ));
		}
		
	}

	private void fillContent() {
		
		setName( app.getNameFromCookies());
		setPwd( app.getPwdFromCookies());
		setRemember( app.getRememberFlagFromCookies());
		setLanguage( app.getLanguageFromCookies());
		
		
	}
	
}
