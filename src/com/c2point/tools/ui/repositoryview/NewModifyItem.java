package com.c2point.tools.ui.repositoryview;

import java.util.EventListener;

import javax.swing.event.EventListenerList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.InventoryUI;
import com.c2point.tools.datalayer.SettingsFacade;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.repository.ItemStatus;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.ui.repositoryview.handlers.CommandListener;
import com.c2point.tools.ui.repositoryview.handlers.SetToolUserHandler;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class NewModifyItem extends Window {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( NewModifyItem.class.getName());

	private NewToolsListModel	model;
	private ToolItem 			item;
	
	public interface StatusSelectorListener extends EventListener {
		
		public void statusChanged( ItemStatus newStatus );
	}
	
	private EventListenerList	listenerList = new EventListenerList(); 
	
	private Button		ownership;
	private ComboBox	status;
	
	private OptionGroup single;
	
	public NewModifyItem( NewToolsListModel model, ToolItem item ) {
		super();
		
		this.model = model;
		this.item = item;

		initUI();

		if ( item != null ) 
			selectStatus( item.getStatus());

	}

	

	private void initUI() {
		
		
		VerticalLayout subContent = new VerticalLayout();
		subContent.setMargin( true );
		setContent( subContent );
		setModal( true );
		setClosable( true );
		setResizable( false );
	
//		center();
		
		boolean freeAllowed = Boolean.parseBoolean(
				SettingsFacade.getInstance().getProperty( model.getOrg(), "FreeStatusAllowed", "false" ));
		
		ownership = new Button( "Take Ownership ..." );
		ownership.setImmediate( true );
		ownership.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
/*
//				SetToolUserHandler handler = new SetToolUserHandler( model );

				CommandListener.ExitStatus exitStatus = new SetToolUserHandler( model ).handleCommand( model.getSelectedItem());
				if ( logger.isDebugEnabled()) logger.debug( "Command button has been clicked" );
				
				if ( exitStatus != CommandListener.ExitStatus.NONE 
					&& exitStatus != CommandListener.ExitStatus.STATUS_CHANGED )
					showNotification( exitStatus, model.getSelectedItem(), null );
*/				
			}
			
			
		});
		
		
		status = new ComboBox( "New status:" );
		status.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		status.setNullSelectionAllowed(false);
		status.setImmediate(true);

		status.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange( ValueChangeEvent event ) {
			}
		});
		
/*		
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
*/		

		subContent.addComponent( ownership );
		subContent.addComponent( status );
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
