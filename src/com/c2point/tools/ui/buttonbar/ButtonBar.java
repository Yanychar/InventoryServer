package com.c2point.tools.ui.buttonbar;

import com.c2point.tools.ui.changescollecor.ChangesListener;
import com.c2point.tools.ui.changescollecor.FieldsChangeCollector;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class ButtonBar extends HorizontalLayout implements ChangesListener {
	private static final long serialVersionUID = 1L;
	
	private Button			okButton;
	private Button			cancelButton;

	private FieldsChangeCollector	changesCollector = null;
	
	private ButtonPressListener		buttonsListener = null;

	
	public ButtonBar() { 
		this( null );
	}
	
	public ButtonBar( ButtonPressListener buttonsListener ) { 

		this.buttonsListener = buttonsListener;
		
		okButton = new Button( "OK" );
		cancelButton = new Button( "Cancel" );

		okButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				if ( ButtonBar.this.buttonsListener != null ) ButtonBar.this.buttonsListener.okPressed();
			}
			
			
		});
		
		cancelButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				if ( ButtonBar.this.buttonsListener != null ) ButtonBar.this.buttonsListener.cancelPressed();
			
			}
			
			
		});
		
		addComponent( okButton );
		addComponent( cancelButton );
		
		updateButtons();
		
	}

	public void addButtonsListener( ButtonPressListener buttonsListener ) { this.buttonsListener = buttonsListener; }
	
	public Button getOk() { return okButton; }
	public Button getCancel() { return cancelButton; }
	
	public void updateButtons() {
		
		getOk().setEnabled(  changesCollector == null || changesCollector.wasItChanged());
		getCancel().setEnabled( true );
		
	}
	
	public void setChangesCollector( FieldsChangeCollector collector ) {
		
		this.changesCollector = collector;
		
		changesCollector.addChangesListener( this );
		
		updateButtons();
	}

	@Override
	public void fieldWasChanged() {

		updateButtons();
		
	}
	
	
}
