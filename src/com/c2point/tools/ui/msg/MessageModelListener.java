package com.c2point.tools.ui.msg;

import java.util.EventListener;

import com.c2point.tools.entity.msg.Message;

public interface MessageModelListener extends EventListener {

	public void wasAdded( Message msg );
	public void wasChanged( Message msg );
	public void wasDeleted( Message msg );
	public void listWasChanged();
	public void selected( Message msg );
	
}
