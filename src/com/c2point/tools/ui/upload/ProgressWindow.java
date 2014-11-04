package com.c2point.tools.ui.upload;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ProgressWindow extends Window {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( ProgressWindow.class.getName());

	private TextArea outputScreen = null;
	
	public ProgressWindow( String caption ) {
		
		super( caption );
		center();
		setModal( true );
		setClosable( false );
		setWidth( "50%" );
		setWidth( "50%" );
		
		VerticalLayout content = new VerticalLayout();
		content.setMargin( true );
		content.setSpacing( true );
		setContent(content);

		outputScreen = new TextArea();
		outputScreen.setRows( 20 );
		outputScreen.setImmediate( true );
		outputScreen.setReadOnly( true );
		outputScreen.setSizeFull();

		content.addComponent( outputScreen );
		
		// Trivial logic for closing the sub-window
		Button ok = new Button( "OK" );
		ok.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				close(); // Close the sub-window
			}
		});
		
		
		
		content.addComponent( ok );
		
		logger.debug( "ProgressWindow has been created (not shown yet)" );
	}
	
	public void write( String text ) {

		if ( outputScreen != null ) {
			
			String areaStr = outputScreen.getValue() + "\n" + text;
		
			outputScreen.setReadOnly( false );
			outputScreen.setValue( areaStr );
			outputScreen.setCursorPosition( areaStr.length() - 1 );			
			outputScreen.setReadOnly( true );
			
		}
		
	}
	
}
