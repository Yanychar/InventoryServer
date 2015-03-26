package com.c2point.tools.ui.repositoryview.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.ItemsFacade;
import com.c2point.tools.entity.repository.ItemStatus;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.ui.repositoryview.StatusSelector;
import com.c2point.tools.ui.repositoryview.ToolsListModel;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

public class ChangeStatusHandler extends AbstractHandler {

	private static Logger logger = LogManager.getLogger( ChangeStatusHandler.class.getName());
	
	public ChangeStatusHandler( ToolsListModel model ) {
		super( model );
	}
	
	@Override
	public ExitStatus handleCommand( final ToolItem item ) {

		CommandListener.ExitStatus exitStatus = CommandListener.ExitStatus.UNKNOWN;

		StatusSelector selector = new StatusSelector( getModel().getOrg());
		
		selector.selectStatus( item.getStatus());
		
		selector.addStatusChangedListener( new StatusSelector.StatusSelectorListener() {
			
			@Override
			public void statusChanged( ItemStatus newStatus ) {

				setNewStatus( item, newStatus );
			}
		});
		
		UI.getCurrent().addWindow( selector );
		

		exitStatus = CommandListener.ExitStatus.NONE;
		
		return exitStatus;
	}

	private CommandListener.ExitStatus setNewStatus( ToolItem item, ItemStatus newStatus ) {
		
		CommandListener.ExitStatus exitStatus = CommandListener.ExitStatus.FAILED_CHANGE;
		
		ToolItem updatedItem = ItemsFacade.getInstance().updateStatus( item, newStatus );
		
		if ( updatedItem != null ) {
			if ( logger.isDebugEnabled()) logger.debug( "Specified Tool Item with Id=" + item.getId() + " has been updated."
														+ " New status: " + newStatus );
			
			exitStatus = CommandListener.ExitStatus.STATUS_CHANGED;

			new Notification( 
					getModel().getApp().getResourceStr( "general.notify.header" ),
					getModel().getApp().getResourceStr( "repositorymgmt.notify.statuschanged" ),
					Notification.Type.HUMANIZED_MESSAGE, 
					true 
			).show( Page.getCurrent());
			
			getModel().fireChanged( updatedItem );
			
		} else {

			new Notification( 
					getModel().getApp().getResourceStr( "general.error.header" ),
					getModel().getApp().getResourceStr( "repositorymgmt.error.statuschanged" ),
					Notification.Type.ERROR_MESSAGE, 
					true 
			).show( Page.getCurrent());
		}
		
		return exitStatus;
		
	}

	
}
