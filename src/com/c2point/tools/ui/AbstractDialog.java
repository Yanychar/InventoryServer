package com.c2point.tools.ui;

import com.c2point.tools.ui.buttonbar.ButtonBar;
import com.c2point.tools.ui.buttonbar.ButtonPressListener;
import com.c2point.tools.ui.changescollecor.FieldsChangeCollector;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public abstract class AbstractDialog extends Window implements ButtonPressListener {
	private static final long serialVersionUID = 1L;

	private ButtonBar 				buttonBar = null;
	private FieldsChangeCollector	changesCollector = null;
	
	public AbstractDialog() {
		super();
		
		addCloseListener( new CloseListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void windowClose(CloseEvent e) {
			
				// Actions to take after dialog has been closed
				dlgClosed();
				
			}
			
		});
		
	}

	protected ButtonBar getButtonBar() {
		if ( buttonBar == null ) {
			buttonBar = new ButtonBar();
			buttonBar.setMargin( true );
			buttonBar.setSpacing( true );
			buttonBar.addButtonsListener( this );

			buttonBar.setChangesCollector( getChangesCollector());
			
		}

		return buttonBar;
	}

	protected FieldsChangeCollector getChangesCollector() {
		if ( changesCollector == null ) {
			changesCollector = new FieldsChangeCollector();
			
		}

		return changesCollector;
	}
	
	public abstract void okPressed();
	public abstract void cancelPressed();

	public abstract void dlgClosed();

	
	protected Component getSeparator() {
		
		Label separator = new Label( "<hr/>", ContentMode.HTML );
		separator.setWidth( "100%" );
		
		return separator;
	}
	
}
