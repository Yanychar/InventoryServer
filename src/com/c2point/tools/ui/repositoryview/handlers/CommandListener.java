package com.c2point.tools.ui.repositoryview.handlers;

import com.c2point.tools.entity.repository.ToolItem;

public interface CommandListener {

	public enum ExitStatus {
		OK,
		SENT_TO_USER,
		SENT_TO_OWNER,
		WRONG_ITEM,
		WRONG_USER,
		FAILED_UNKNOWN
		
		
	};
	
	public ExitStatus handleCommand( ToolItem item );
	
}
