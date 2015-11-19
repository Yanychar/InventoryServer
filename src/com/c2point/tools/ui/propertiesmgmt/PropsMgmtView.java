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

	// User Code settings
	private FormCheckBox 		allowUserCode;
	private TextField			lengthUserCode;
	private FormCheckBox 		automaticUserCode;
	private TextField			prefixUserCode;

	// Tools Code settings
	private FormCheckBox 		allowToolCode;
	private TextField			lengthToolCode;
	private FormCheckBox 		automaticToolCode;
	private TextField			prefixToolCode;
	
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
		updateUserCodeChanged();
		updateToolCodeChanged();
	}
	
	/* Tool Code management */
	private Component getToolCodeSettings() {

		FormLayout table = new FormLayout();
		table.setSpacing( true );
		table.setMargin( true );
		table.setSpacing( true );
		table.setWidth( "100%" );
		
		allowToolCode 	= new FormCheckBox( "Use Tool ID" + ":" );
		lengthToolCode	= new TextField( "Length of Tool ID" + ":" );
		
		automaticToolCode = new FormCheckBox( "Automatically assigned Tool ID" + ":" );
		prefixToolCode	= new TextField( "Prefix for Tool ID" + ":" );

		table.addComponent( allowToolCode );
		table.addComponent( lengthToolCode );
		table.addComponent( automaticToolCode );
		table.addComponent( prefixToolCode );
		
		model.getChangesCollector().listenForChanges( allowToolCode );
		model.getChangesCollector().listenForChanges( automaticToolCode );
		
		allowToolCode.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange( ValueChangeEvent event ) {
				
				updateToolCodeChanged();
				
			}
			
		});
		
		automaticToolCode.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange( ValueChangeEvent event ) {
				
				updateToolCodeChanged();
				
			}
			
		});
		
		return table;
	}

	private void updateToolCodeChanged() {

		boolean flagAllow = allowToolCode.getValue();
		boolean flagAuto = automaticToolCode.getValue();
		
		lengthToolCode.setEnabled( flagAllow ); 
		automaticToolCode.setEnabled( flagAllow );
		prefixToolCode.setEnabled( flagAllow && flagAuto );
			

	}

	private boolean validateToolCodeFields() {

		return lengthToolCode.isValid() && prefixToolCode.isValid();
		
	}
	
	private void viewToModelToolCode() {

		SettingsFacade sf = SettingsFacade.getInstance();
		Organisation org = model.getOrg();
				
		sf.set( org, "allowToolCode", allowToolCode.getValue(), false );
		sf.setInteger( org, "toolCodeLength", lengthToolCode.getValue(), false );

		sf.set( org, "automaticToolCode", automaticToolCode.getValue(), false );
		sf.set( org, "prefixToolCode", prefixToolCode.getValue(), false );

	}
	
	private void modelToViewToolCode() {

		SettingsFacade sf = SettingsFacade.getInstance();
		Organisation org = model.getOrg();
				
		allowToolCode.setValue(	sf.getBoolean( org, "allowToolCode", false ));
		lengthToolCode.setValue(sf.getPosInteger( org, "toolCodeLength", 4 ).toString());
		automaticToolCode.setValue(	sf.getBoolean( org, "automaticToolCode", true ));
		prefixToolCode.setValue(	sf.getNonEmptyString( org, "prefixToolCode", "" ));

	}
	/* ... end of Tool Code management */

	/* User Code management */
	private Component getUserCodeSettings() {

		FormLayout table = new FormLayout();
		table.setSpacing( true );
		table.setMargin( true );
		table.setSpacing( true );
		table.setWidth( "100%" );
		
		allowUserCode 	= new FormCheckBox( "Use Personnel ID" + ":" );
		lengthUserCode	= new TextField( "Length of Personnel ID" + ":" );
		
		automaticUserCode = new FormCheckBox( "Automatically assigned Personnel ID" + ":" );
		prefixUserCode	= new TextField( "Prefix for Personnel ID" + ":" );

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
		table.addComponent( lengthUserCode );
		table.addComponent( automaticUserCode );
		table.addComponent( prefixUserCode );
		
		model.getChangesCollector().listenForChanges( allowUserCode );
		model.getChangesCollector().listenForChanges( automaticUserCode );
		
		allowUserCode.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange( ValueChangeEvent event ) {
				
				updateUserCodeChanged();
				
			}
			
		});
		
		automaticUserCode.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange( ValueChangeEvent event ) {
				
				updateUserCodeChanged();
				
			}
			
		});
		
		return table;
	}

	private void updateUserCodeChanged() {

		boolean flagAllow = allowUserCode.getValue();
		boolean flagAuto = automaticUserCode.getValue();
		
		lengthUserCode.setEnabled( flagAllow ); 
		automaticUserCode.setEnabled( flagAllow );
		prefixUserCode.setEnabled( flagAllow && flagAuto );
			

	}

	private boolean validateUserCodeFields() {

		return lengthUserCode.isValid() && prefixUserCode.isValid();
		
	}
	
	private void viewToModelUserCode() {

		SettingsFacade sf = SettingsFacade.getInstance();
		Organisation org = model.getOrg();
				
		sf.set( org, "allowUserCode", allowUserCode.getValue(), false );
		sf.setInteger( org, "userCodeLength", lengthUserCode.getValue(), false );

		sf.set( org, "automaticUserCode", automaticUserCode.getValue(), false );
		sf.set( org, "prefixUserCode", prefixUserCode.getValue(), false );

	}
	
	private void modelToViewUserCode() {

		SettingsFacade sf = SettingsFacade.getInstance();
		Organisation org = model.getOrg();
				
		allowUserCode.setValue(	sf.getBoolean( org, "allowUserCode", true ));
		lengthUserCode.setValue(sf.getPosInteger( org, "userCodeLength", 4 ).toString());
		automaticUserCode.setValue(	sf.getBoolean( org, "automaticUserCode", true ));
		prefixUserCode.setValue(	sf.getNonEmptyString( org, "prefixUserCode", "" ));

	}
	/* ... end of User Code management */
	
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
