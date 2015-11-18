package com.c2point.tools.ui.propertiesmgmt;

import java.text.MessageFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.SettingsFacade;
import com.c2point.tools.entity.organisation.Organisation;
import com.eijsink.vaadin.components.formcheckbox.FormCheckBox;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;

public class PropsMgmtView extends Window {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( PropsMgmtView.class.getName()); 

	private PropsMgmtModel		model;

	private FormCheckBox 		allowUserCode;
	private TextField			userCodeLength;
	private TextField			lastUsedUserCode;

	private FormCheckBox 		allowToolCode;
	private TextField			toolCodeLength;
	private TextField			lastUsedToolCode;
	
	public PropsMgmtView( PropsMgmtModel model ) {
		
		super();
		
		this.model = model;
		
		init();
		
	}
	private void init() {
		
		setModal( true );
		setCaption( "Properties" );
		center();
		this.setWidth( "40em" );
//		this.setHeight( "80ex" );
		
		VerticalLayout content = new VerticalLayout();
		content.setMargin( true );

		content.addComponent( getUserCodeSettings());
		content.addComponent( getToolCodeSettings());
		content.addComponent( getControlBar() );

		
		setContent( content );
		
		modelToView();
		
		updateFields();
		
	}

	private void updateFields() {
		updateUserCodeFields();
		updateToolCodeFields();
	}
	
	/* User Code management */
	private Component getUserCodeSettings() {

		FormLayout table = new FormLayout();
		table.setSpacing( true );
		table.setMargin( true );
		table.setSpacing( true );
		table.setWidth( "100%" );
		
		allowUserCode 	= new FormCheckBox( "User Code is used" + ":" );
		userCodeLength 	= new TextField( "Length of User Code" + ":" );
		lastUsedUserCode	= new TextField( "Latest User Code" + ":" );
		
//		userCodeLength.addValidator( new IntegerRangeValidator( "Code shall be between 4 and 8 digits", 4, 8 ));
//		lastUsedCode.addValidator( new RegexpValidator( "[0-9]{4,8}", "Wrong code specified" ));
				
		
//		final ObjectProperty<Integer> property = new ObjectProperty<Integer>(42);
		// Create a TextField, which edits Strings
		// Use a converter between String and Integer
//		userCodeLength.setConverter(new StringToIntegerConverter());
		// And bind the field
//		userCodeLength.setPropertyDataSource(property);
//		userCodeLength.addValidator( new RegexpValidator( "[0-9]", "Code shall be between 4 and 8 digits" ));
		
		
		
		table.addComponent( allowUserCode );
		table.addComponent( userCodeLength );
		table.addComponent( lastUsedUserCode );
				
		model.getChangesCollector().listenForChanges( allowUserCode );
		model.getChangesCollector().listenForChanges( userCodeLength ); 
		model.getChangesCollector().listenForChanges( lastUsedUserCode );
		
		allowUserCode.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange( ValueChangeEvent event ) {
				
				updateUserCodeFields();
				
			}
			
		});
		
		
				
		return table;
	}

	private void updateUserCodeFields() {

		if ( allowUserCode.getValue()) {
			userCodeLength.setEnabled( true ); 
			lastUsedUserCode.setEnabled( true );
		} else {
			userCodeLength.clear(); 
			lastUsedUserCode.clear();
			userCodeLength.setEnabled( false ); 
			lastUsedUserCode.setEnabled( false );
		}
		
	}
	
	private boolean validateUserCodeFields() {

		return userCodeLength.isValid() && lastUsedUserCode.isValid();
		
	}
	
	private void viewToModelUserCode() {

		SettingsFacade sf = SettingsFacade.getInstance();
		Organisation org = model.getOrg();
				
		sf.set( org, "allowUserCode", allowUserCode.getValue(), false );
		sf.setInteger( org, "userCodeLength", userCodeLength.getValue(), false );
		sf.set( org, "lastUsedCode", lastUsedUserCode.getValue(), false );
		
	}
	
	private void modelToViewUserCode() {

		SettingsFacade sf = SettingsFacade.getInstance();
		Organisation org = model.getOrg();
				
		allowUserCode.setValue(	sf.getBoolean( org, "allowUserCode", true ));
		userCodeLength.setValue(sf.getInteger( org, "userCodeLength", 4 ).toString());
		lastUsedUserCode.setValue(	sf.getString( org, "lastUsedCode", "0001" ));
		
	}
	/* ... end of User Code management */

	/* Tool Code management */

	private Component getToolCodeSettings() {

		FormLayout table = new FormLayout();
		table.setSpacing( true );
		table.setMargin( true );
		table.setSpacing( true );
		table.setWidth( "100%" );
		
		allowToolCode 	= new FormCheckBox( "Tool Code is used" + ":" );
		toolCodeLength 	= new TextField( "Length of Tool Code" + ":" );
		lastUsedToolCode	= new TextField( "Latest Tool Code" + ":" );
		
//		userCodeLength.addValidator( new IntegerRangeValidator( "Code shall be between 4 and 8 digits", 4, 8 ));
//		lastUsedCode.addValidator( new RegexpValidator( "[0-9]{4,8}", "Wrong code specified" ));
				
		
//		final ObjectProperty<Integer> property = new ObjectProperty<Integer>(42);
		// Create a TextField, which edits Strings
		// Use a converter between String and Integer
//		userCodeLength.setConverter(new StringToIntegerConverter());
		// And bind the field
//		userCodeLength.setPropertyDataSource(property);
//		userCodeLength.addValidator( new RegexpValidator( "[0-9]", "Code shall be between 4 and 8 digits" ));
		
		
		
		table.addComponent( allowToolCode );
		table.addComponent( toolCodeLength );
		table.addComponent( lastUsedToolCode );
				
		model.getChangesCollector().listenForChanges( allowToolCode );
		model.getChangesCollector().listenForChanges( toolCodeLength ); 
		model.getChangesCollector().listenForChanges( lastUsedToolCode );
		
		allowToolCode.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange( ValueChangeEvent event ) {
				
				updateToolCodeFields();
				
			}
			
		});
		
		
				
		return table;
	}

	private void updateToolCodeFields() {

		if ( allowToolCode.getValue()) {
			toolCodeLength.setEnabled( true ); 
			lastUsedToolCode.setEnabled( true );
		} else {
			toolCodeLength.clear(); 
			lastUsedToolCode.clear();
			toolCodeLength.setEnabled( false ); 
			lastUsedToolCode.setEnabled( false );
		}
		
	}
	
	private boolean validateToolCodeFields() {

		return toolCodeLength.isValid() && lastUsedToolCode.isValid();
		
	}
	
	private void viewToModelToolCode() {

		SettingsFacade sf = SettingsFacade.getInstance();
		Organisation org = model.getOrg();
				
		sf.set( org, "allowToolCode", allowToolCode.getValue(), false );
		sf.setInteger( org, "toolCodeLength", toolCodeLength.getValue(), false );
		sf.set( org, "lastUsedToolCode", lastUsedToolCode.getValue(), false );
		
	}
	
	private void modelToViewToolCode() {

		SettingsFacade sf = SettingsFacade.getInstance();
		Organisation org = model.getOrg();
				
		allowToolCode.setValue(	sf.getBoolean( org, "allowToolCode", false ));
		toolCodeLength.setValue(sf.getInteger( org, "toolCodeLength", 4 ).toString());
		lastUsedToolCode.setValue(	sf.getString( org, "lastUsedToolCode", "0001" ));
		
	}
	
	/* ... end of Tool Code management */
	
	private Component getControlBar() {
		
		HorizontalLayout bar = new HorizontalLayout();
		
		Button okButton = new Button( "OK" );
		Button cancelButton = new Button( "Cancel" );
		
		okButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				if ( model.getChangesCollector().wasItChanged()) {
					
					if ( validateAndSave()) {
						
						Notification.show( "", "Settings were stored", Type.HUMANIZED_MESSAGE );

						PropsMgmtView.this.close();
					} else {
						
						Notification.show( "Error", "Cannot store Settings", Type.ERROR_MESSAGE );
						
					}
						
				} else {
					// Nothing was changed. Close and exit
					Notification.show( "", "Nothing was changed", Type.HUMANIZED_MESSAGE );
					
					
					PropsMgmtView.this.close();
				}
				
			}
			
			
		});
		
		
		cancelButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				PropsMgmtView.this.close();
				
			}
			
			
		});

		bar.addComponent( okButton );
		bar.addComponent( cancelButton );
		
		return bar;
	}
	
		
	private boolean validateAndSave() {

		boolean bRes = validate() && save();
		
		return bRes;
	}
	
	private boolean validate() {
		boolean bRes = false;
		
		bRes = validateUserCodeFields();

		bRes = bRes && validateToolCodeFields();
		
		logger.debug( "Validated? " + bRes );
		return bRes;
	}
	
	private boolean save() {
		boolean bRes = false;
		
		viewToModel();
		
		bRes = model.update();
		
		if ( !bRes ) {

			String template = model.getApp().getResourceStr( "general.error.update.header" );
			Object[] params = { model.getOrg().getName() };
			template = MessageFormat.format( template, params );

			Notification.show( template, Notification.Type.ERROR_MESSAGE );
			
		}
		
		
		bRes = true;
		
		logger.debug( "Saved? " + bRes );
		return bRes;
	}

	
	private void viewToModel() {

		viewToModelUserCode();
		viewToModelToolCode();
	}
	
	private void modelToView() {
		modelToViewUserCode();
		modelToViewToolCode();
		
	}
	
}
