package com.c2point.tools.ui.repositoryview.handlers;

import com.c2point.tools.entity.repository.ToolItem;

public interface CommandListener {

	public enum ExitStatus {
		NONE,
		REQUEST_SENT,
		REQUEST_ACCEPTED,
		REQUEST_REJECTED,
		ITEM_TOOKOVER,
		STATUS_CHANGED,
		FAILED_TOOKOVER,
		FAILED_CHANGE,
		WRONG_ITEM,
		WRONG_USER,
		MSG_SENT,
		UNKNOWN
		
	};
	
	public ExitStatus handleCommand( ToolItem item );
	
}
