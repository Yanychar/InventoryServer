package com.c2point.tools.ui.repositoryview.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.ItemsFacade;
import com.c2point.tools.datalayer.MsgFacade;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ItemStatus;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.ui.repositoryview.ToolsListModel;

public class SetToolUserHandler extends AbstractHandler {

	private static Logger logger = LogManager.getLogger( SetToolUserHandler.class.getName());
	
	public SetToolUserHandler( ToolsListModel model ) {
		super( model );
	}
	
	@Override
	public ExitStatus handleCommand( ToolItem item ) {

		CommandListener.ExitStatus exitStatus = CommandListener.ExitStatus.UNKNOWN;
		
		logger.debug( "Handler 'Set Tool User' has been started. Tool: '" + item.getTool().getName() + "'" );
		
		// Set new user and change status
		OrgUser oldUser = item.getCurrentUser();
		
		ToolItem updatedItem = ItemsFacade.getInstance().updateUser( item, this.getModel().getSessionOwner());
		
		if ( updatedItem != null ) {
			
			if ( logger.isDebugEnabled()) logger.debug( "Specified Tool Item was updated: " + updatedItem );
			
			exitStatus = CommandListener.ExitStatus.ITEM_TOOKOVER;

			getModel().fireChanged( updatedItem );
			
		} else {
			
			logger.error( "Failed to update ToolItem: " + item );
			exitStatus = CommandListener.ExitStatus.FAILED_TOOKOVER;
			
		}
		
		// Save Info message
		
		if ( oldUser != null && exitStatus == CommandListener.ExitStatus.ITEM_TOOKOVER ) {
			
			if ( MsgFacade.getInstance().addToolBorrowedInfo( this.getModel().getSessionOwner(), oldUser, updatedItem )) {

				if ( logger.isDebugEnabled()) logger.debug( "Message 'Tool Borrowed' was sent" );
			
			}
		
		}
		
		return exitStatus;
	}

}
