package com.c2point.tools.ui.util;

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
	
	public static int 					MAX_CHANGE_COLLECTORS = 5;
	private FieldsChangeCollector []	changesCollector = new FieldsChangeCollector [ MAX_CHANGE_COLLECTORS ];
	
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

		return getChangesCollector( 0 );
	}
	
	protected FieldsChangeCollector getChangesCollector( int i ) {
		
		FieldsChangeCollector res = null;
		
		if ( i < MAX_CHANGE_COLLECTORS && i >= 0 ) {
			
			// Create collector if it was not created
			if ( changesCollector[ i ] == null ) {
				changesCollector[ i ] = new FieldsChangeCollector();
			}
			// Return collector
			res = changesCollector[ i ];
		}

		return res;
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
