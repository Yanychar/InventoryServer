package com.c2point.tools.ui.repositoryview.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.MsgFacade;
import com.c2point.tools.entity.msg.Message;
import com.c2point.tools.entity.msg.MessageType;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.ui.repositoryview.ToolsListModel;

public class RequestToolHandler extends AbstractHandler {

	private static Logger logger = LogManager.getLogger( RequestToolHandler.class.getName());

	public RequestToolHandler( ToolsListModel model ) {
		super( model );
	}
	
	@Override
	public ExitStatus handleCommand( ToolItem item ) {

		CommandListener.ExitStatus exitStatus = CommandListener.ExitStatus.FAILED_UNKNOWN;
		
		if ( logger.isDebugEnabled()) {
			if ( item != null && item.getTool() != null && item.getTool().getName() != null )
				logger.debug( "Handler 'Request Tool' has been started. Tool: '" + item.getTool().getName() + "'" );
			else
				try {
					logger.debug( "Item = " + item );
					logger.debug( "Tool = " + item.getTool());
					logger.debug( "Tool name = " + item.getTool().getName());
				} catch ( Exception e ) {
					
				}
		}

		// 1. validate Item and Tool
		if ( validated( item )) {
		// If validated

			if( MsgFacade.getInstance().addToolRequest( item, getModel().getApp().getSessionData().getOrgUser())) {
				
				// 
				if ( item.getCurrentUser() != null ) {
					exitStatus = CommandListener.ExitStatus.SENT_TO_USER;
				} else if ( item.getResponsible() != null ) {
					exitStatus = CommandListener.ExitStatus.SENT_TO_OWNER;			
				}
			
			}
		
		} else {
		// if NOT validated than notification + return
			logger.debug( "Item cannot be Requested!!!" );
			
			if ( item == null ) {
				exitStatus = CommandListener.ExitStatus.WRONG_ITEM;			
			} else if ( item.getCurrentUser() == null && item.getResponsible() == null ) {
				exitStatus = CommandListener.ExitStatus.WRONG_USER;			
			}
		}
		
		return exitStatus;
	}

	private boolean validated( ToolItem item ) {
		
		boolean bRes = false;
		
		if ( item != null && item.getTool() != null 
			&& ( item.getCurrentUser() != null || item.getResponsible() != null )
		) {
			
			bRes = true;
		}
		return bRes;
	}

	
}
