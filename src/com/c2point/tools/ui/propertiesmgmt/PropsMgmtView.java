package com.c2point.tools.ui.propertiesmgmt;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.AccessRightsFacade;
import com.c2point.tools.entity.access.AccessRight;
import com.c2point.tools.entity.access.AccessRightsCollector;
import com.c2point.tools.entity.access.FunctionalityType;
import com.c2point.tools.entity.access.OwnershipType;
import com.c2point.tools.entity.access.PermissionType;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.ui.ChangesCollector;
import com.c2point.tools.ui.accountmgmt.AccountView;
import com.c2point.tools.ui.personnelmgmt.StuffListModel;
import com.eijsink.vaadin.components.formcheckbox.FormCheckBox;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
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
	private TextField			lastUsedCode;
	
	private ChangesCollector	changesCollector = new ChangesCollector();
	
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
		content.addComponent( getControlBar() );

		
		setContent( content );
		
		updateFields();
		
	}

	private void updateFields() {
		updateUserCodeFields();
	}
	
	private Component getUserCodeSettings() {

		FormLayout table = new FormLayout();
		table.setSpacing( true );
		table.setMargin( true );
		table.setSpacing( true );
		table.setWidth( "100%" );
		
		allowUserCode 	= new FormCheckBox( "User Code is used" + ":" );
		userCodeLength 	= new TextField( "Length of User Code" + ":" );
		lastUsedCode	= new TextField( "Latest User Code" + ":" );
		
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
		table.addComponent( lastUsedCode );
				
		changesCollector.listenForChanges( allowUserCode );
		changesCollector.listenForChanges( userCodeLength ); 
		changesCollector.listenForChanges( lastUsedCode );
		
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
			lastUsedCode.setEnabled( true );
		} else {
			userCodeLength.clear(); 
			lastUsedCode.clear();
			userCodeLength.setEnabled( false ); 
			lastUsedCode.setEnabled( false );
		}
		
	}
	
	private boolean validateUserCodeFields() {

		return userCodeLength.isValid() && lastUsedCode.isValid();
		
	}
	
	private void viewToModelUserCode() {

	}
	
	private void modelToViewUserCode() {
		
	}
	
	private Component getControlBar() {
		
		HorizontalLayout bar = new HorizontalLayout();
		
		Button okButton = new Button( "OK" );
		Button cancelButton = new Button( "Cancel" );
		
		okButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				if ( wereChanged() ) {
					
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
	
	private boolean wereChanged() {

		boolean bRes = changesCollector.wasItChanged();
		
		logger.debug( "Content changed? " + bRes );
		return bRes;
	}
	
	private boolean validateAndSave() {

		boolean bRes = validate() && save();
		
		return bRes;
	}
	
	private boolean validate() {
		boolean bRes = false;
		
		bRes = validateUserCodeFields();
		
		logger.debug( "Validated? " + bRes );
		return bRes;
	}
	
	private boolean save() {
		boolean bRes = false;
		
		viewToModel();
		
		bRes = model.update();
		
		if ( !bRes ) {

			String template = model.getApp().getResourceStr( "general.error.update.header" );
			Object[] params = { this.shownOrg.getName() };
			template = MessageFormat.format( template, params );

			Notification.show( template, Notification.Type.ERROR_MESSAGE );
			
		}
		
		
		bRes = true;
		
		logger.debug( "Saved? " + bRes );
		return bRes;
	}

	
	private void viewToModel() {

		viewToModelUserCode();
	}
	
	private void modelToView() {
		modelToViewUserCode();
		
	}
	
}
