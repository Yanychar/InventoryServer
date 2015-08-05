package com.c2point.tools.ui.repositoryview;

import java.util.EventListener;

import javax.swing.event.EventListenerList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.InventoryUI;
import com.c2point.tools.datalayer.SettingsFacade;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.repository.ItemStatus;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class StatusSelector extends Window {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( StatusSelector.class.getName());

	private Organisation org;
	
	public interface StatusSelectorListener extends EventListener {
		
		public void statusChanged( ItemStatus newStatus );
	}
	
	private EventListenerList	listenerList = new EventListenerList();; 
	
	private OptionGroup single;
	
	public StatusSelector( Organisation org ) {
		super();
		
		this.org = org;

		initUI();
		
	}

	public StatusSelector( Organisation org, ItemStatus status ) {
		this( org );
	
		selectStatus( status );
	}

	private void initUI() {
		
		
		VerticalLayout subContent = new VerticalLayout();
		subContent.setMargin( true );
		setContent( subContent );
		setModal( true );
//		setClosable( false );
		setResizable( false );
	
//		center();
		
		boolean freeAllowed = SettingsFacade.getInstance().getBoolean( org, "freeStatusAllowed", false );
		
		single = new OptionGroup();
		for ( ItemStatus status : ItemStatus.values()) {
			if ( status != ItemStatus.UNKNOWN 
				 &&
				 ( freeAllowed || !freeAllowed && status != ItemStatus.FREE )
			) {
				
				single.addItem( status );
				single.setItemCaption( status, status.toString((( InventoryUI )UI.getCurrent()).getSessionData().getBundle()));
			}
		}
		
		single.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				
				logger.debug( "ItemSelected: " + event.getProperty().getValue());

				close();

				fireStatusChanged(( ItemStatus )event.getProperty().getValue());
				
			}
			
		});
		
		subContent.addComponent( single );
		
	}

	public void selectStatus( ItemStatus status ) {
		
		if ( status != null ) {
			
			single.select( status );
		}
	}
	
	public void addStatusChangedListener( StatusSelectorListener listener ) {
		listenerList.add( StatusSelectorListener.class, listener);
	}
	
	protected void fireStatusChanged( ItemStatus newStatus ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == StatusSelectorListener.class) {
	    		(( StatusSelectorListener )listeners[ i + 1 ] ).statusChanged( newStatus );
	         }
	     }
	 }


	
}
