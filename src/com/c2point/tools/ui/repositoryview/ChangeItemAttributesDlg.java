package com.c2point.tools.ui.repositoryview;

import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.InventoryUI;
import com.c2point.tools.datalayer.ItemsFacade;
import com.c2point.tools.datalayer.MsgFacade;
import com.c2point.tools.datalayer.SettingsFacade;
import com.c2point.tools.entity.access.FunctionalityType;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ItemStatus;
import com.c2point.tools.entity.repository.ToolItem;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class ChangeItemAttributesDlg extends Window {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( ChangeItemAttributesDlg.class.getName());

	private ToolsListModel	model;
	private ToolItem 			item;

/*
	public interface StatusSelectorListener extends EventListener {
		
		public void statusChanged( ItemStatus newStatus );
	}
	
	private EventListenerList	listenerList = new EventListenerList(); 
*/	
	private Button		ownership;
	private ComboBox	status;
	
//	private OptionGroup single;
	
	public ChangeItemAttributesDlg( ToolsListModel model, ToolItem item ) {
		super();
		
		this.model = model;
		this.item = item;

		if ( item == null ) {
			item = model.getSelectedItem();
		}
			
		initUI();

	}

	

	private void initUI() {
		
		setCaption( "Actions" );
		setModal( true );
		setClosable( true );
		setResizable( false );

		VerticalLayout subContent = new VerticalLayout();
		subContent.setMargin( true );
		subContent.setSpacing( true );

		setContent( subContent );
		
//		center();
		
		ownership = new Button( "Take Ownership ..." );
		ownership.setImmediate( true );
		ownership.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				ToolItem updatedItem = changeUser( item );
				
				if ( updatedItem != null ) {

					String template = model.getApp().getResourceStr( "repositorymgmt.notify.takeover" );
					Object[] params = { item.getTool().getFullName() };
					template = MessageFormat.format( template, params );
					
					new Notification( 
							model.getApp().getResourceStr( "general.notify.header" ),
							template,
							Notification.Type.HUMANIZED_MESSAGE, 
							true 
					).show( Page.getCurrent());
					
					close();
					
					model.fireChanged( updatedItem );
					
				} else {
					
					String template = model.getApp().getResourceStr( "repositorymgmt.error.takeover" );
					Object[] params = { item.getTool().getFullName() };
					template = MessageFormat.format( template, params );
					
					new Notification( 
							model.getApp().getResourceStr( "general.error.header" ),
							template,
							Notification.Type.ERROR_MESSAGE, 
							true 
					).show( Page.getCurrent());
				}
				
			}
			
			
		});
		
		
		status = new ComboBox( "New status:" );
		status.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		status.setNullSelectionAllowed(false);
		status.setImmediate(true);

		fillStatusField();

		enableProperFields();

		status.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange( ValueChangeEvent event ) {

				ItemStatus newStatus = ( ItemStatus )event.getProperty().getValue();
					
				ToolItem updatedItem = ItemsFacade.getInstance().updateStatus( item, newStatus );
					
				if ( updatedItem != null ) {
					if ( logger.isDebugEnabled()) logger.debug( "Specified Tool Item with Id=" + item.getId() + " has been updated."
																+ " New status: " + newStatus );
					
					new Notification( 
							model.getApp().getResourceStr( "general.notify.header" ),
							model.getApp().getResourceStr( "repositorymgmt.notify.statuschanged" ),
							Notification.Type.HUMANIZED_MESSAGE, 
							true 
					).show( Page.getCurrent());
					
					close();
					
					model.fireChanged( updatedItem );
					
				} else {

					new Notification( 
							model.getApp().getResourceStr( "general.error.header" ),
							model.getApp().getResourceStr( "repositorymgmt.error.statuschanged" ),
							Notification.Type.ERROR_MESSAGE, 
							true 
					).show( Page.getCurrent());
				}
					
				
			}
		});
		

		subContent.addComponent( ownership );
		subContent.addComponent( status );
	}

	private void fillStatusField() {

		boolean freeAllowed = Boolean.parseBoolean(
				SettingsFacade.getInstance().getProperty( model.getOrg(), "FreeStatusAllowed", "false" ));
		
		for ( ItemStatus oneStatus : ItemStatus.values()) {
			if ( oneStatus != ItemStatus.UNKNOWN 
				 &&
				 ( freeAllowed || !freeAllowed && oneStatus != ItemStatus.FREE )
			) {
				
				status.addItem( oneStatus );
				status.setItemCaption( oneStatus, oneStatus.toString((( InventoryUI )UI.getCurrent()).getSessionData().getBundle()));
			}
		}
		
		if ( item != null && item.getStatus() != null )
			this.status.setValue( item.getStatus());
		
	}
	
/*	
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
*/
	private void enableProperFields() {

		if ( item != null ) {
		
			boolean takeOverPerm = model.getSecurityContext().canChangeToolItem( FunctionalityType.BORROW, item );   
			boolean changeStatusPerm = model.getSecurityContext().canChangeToolItem( FunctionalityType.CHANGESTATUS, item );   
			
			ownership.setEnabled( takeOverPerm );
			status.setEnabled( changeStatusPerm );
		} else {

			ownership.setEnabled( false );
			status.setEnabled( false );
			
		}
		
	}


	private ToolItem changeUser( ToolItem item ) {

		// Set new user and change status
		OrgUser oldUser = item.getCurrentUser();
		
		ToolItem updatedItem = ItemsFacade.getInstance().updateUser( item, model.getSessionOwner());
		
		if ( updatedItem != null ) {
			
			if ( logger.isDebugEnabled()) logger.debug( "Specified Tool Item was updated: " + updatedItem );
			
			// Save Info message
			if ( MsgFacade.getInstance().addToolBorrowedInfo( model.getSessionOwner(), oldUser, updatedItem )) {

				if ( logger.isDebugEnabled()) logger.debug( "Message 'Tool Borrowed' was sent" );
			
			}
			
		} else {
			
			logger.error( "Failed to update ToolItem: " + item );
			
		}
		
		
		return updatedItem;
	}
	
}
