package com.c2point.tools.ui.repositoryview.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.ui.repositoryview.ToolsListModel;

public class SendMessageHandler extends AbstractHandler {

	private static Logger logger = LogManager.getLogger( SendMessageHandler.class.getName());
	
	public SendMessageHandler( ToolsListModel model ) {
		super( model );
	}
	
	@Override
	public ExitStatus handleCommand( ToolItem item ) {
		
		CommandListener.ExitStatus exitStatus = CommandListener.ExitStatus.FAILED_UNKNOWN;
		
		logger.debug( "Handler 'Send Message' has been started." );
		// TODO Auto-generated method stub
		
		return exitStatus;
	}

}
