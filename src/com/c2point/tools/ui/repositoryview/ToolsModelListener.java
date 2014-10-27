package com.c2point.tools.ui.repositoryview;

import java.util.EventListener;

import com.c2point.tools.entity.repository.ToolItem;

public interface ToolsModelListener extends EventListener {

	public void wasAdded( ToolItem repItem );
	public void wasChanged( ToolItem repItem );
	public void wasDeleted( ToolItem repItem );
	public void listWasChanged();
	public void selected( ToolItem repItem );
	
}
