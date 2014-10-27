package com.c2point.tools.ui.msg;

import java.util.EventObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.msg.Message;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Category;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Field;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

public class MessageInfoComponent extends GridLayout implements ValueChangeListener {
	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger( MessageInfoComponent.class.getName());


	public MessageInfoComponent() {
		
		super( 4, 6 );
		
//		this.repItem = repItem;
		
		init();
	}
	
	private void init() {
	
		
		
	}
	
	public void showItem( Message msg ) {
		
		
	}

	public void clearItem() {
		
	}
	
	

	@Override
	public void valueChange( ValueChangeEvent event ) {

		logger.debug( "infoComponent received valueChanged event from Repository Items List" );
		
		if ( event instanceof EventObject && ((EventObject) event).getSource() instanceof Table ) {
			
			Table table =  ( Table )((EventObject) event).getSource();
			
			if ( table.getValue() != null ) {
				logger.debug( "  There are selection in the Messages List" );

				showItem(( Message )event.getProperty().getValue());
				
			} else {
				logger.debug( "  There is NO selection in the Messages List" );

				clearItem();
				
			}
		}
		
/*
		try {
			if ( event.getProperty().getValue() instanceof RepositoryItem ) {
			
				showItem(( RepositoryItem )event.getProperty().getValue());
			}
		}
		catch( Exception e ) {
			
			clearItem();
			
		}
*/		
	}
	

}
