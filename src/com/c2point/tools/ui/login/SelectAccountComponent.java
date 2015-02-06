package com.c2point.tools.ui.login;

import java.util.Collection;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.InventoryUI;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.ui.util.Lang;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class SelectAccountComponent extends CustomComponent {
	
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( SelectAccountComponent.class.getName());

	private InventoryUI			app;
	private Collection<OrgUser>	users;
	
	private OptionGroup			accountsHolder;
	private Button 				okButton;
	private Button 				cancelButton;
	
	public SelectAccountComponent( InventoryUI app ) {
		this( app, null );
	}
	
	public SelectAccountComponent( InventoryUI app, Collection<OrgUser> users ) {
		super();
		
		this.app = app;
		this.users = users;
		
		initView();
	}

	public void setSelectingAccounts( Collection<OrgUser> users ) {

		this.users = users;
		fillSelector();
		
	}
	
	public void addLoginButtonListener( Button.ClickListener listener ) {
		okButton.addClickListener( listener );
	}
	
	public void addCancelButtonListener( Button.ClickListener listener ) {
		cancelButton.addClickListener( listener );
	}
	
	private void initView() {
		Panel panel = new Panel();
//		panel.setWidth(  "400px" );
//		panel.setHeight(  "240px" );
		setCompositionRoot( panel );
		setWidth(  "400px" );
		setHeight(  "240px" );

		accountsHolder = new OptionGroup();		
		okButton = new Button();
		cancelButton = new Button();

		HorizontalLayout buttonLayout = new HorizontalLayout();
		
		buttonLayout.addComponent( okButton );
		buttonLayout.addComponent( cancelButton );
		
		
		VerticalLayout vl = new VerticalLayout();
		
		vl.addComponent( accountsHolder );
		vl.addComponent( buttonLayout );
		
		// Set the size as undefined at all levels
		vl.setSpacing( true );
		vl.setMargin( true );
		
		panel.setContent( vl );

		refreshCaptions();

		fillSelector();
		
	}
	
	private void refreshCaptions() {
		getCompositionRoot().setCaption( app.getResourceStr( "login.select.caption" ));

		okButton.setCaption( app.getResourceStr( "login.login" ));
		cancelButton.setCaption( app.getResourceStr( "general.button.cancel" ));
		
	}

	
	private void fillSelector() {
		if ( this.users != null && this.users.size() > 0 ) {
			
			if ( accountsHolder != null )
				accountsHolder.removeAllItems();

			for ( OrgUser user : this.users ) {
				
				accountsHolder.addItem( user );
				accountsHolder.setItemCaption( user, user.getOrganisation().getName() + ". " + user.getLastAndFirstNames());
			}
			
			// Select first item
			accountsHolder.setValue( this.users.iterator().next());
			
		}
	}

	public OrgUser getSelected() {
		
		return ( OrgUser )accountsHolder.getValue();
	}
}
