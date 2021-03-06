package com.c2point.tools.ui.listeners;

import java.util.EventListener;

import com.c2point.tools.entity.tool.Category;

public interface CategoryChangedListener extends EventListener {

	public void wasAdded( Category category );
	public void wasChanged( Category category );
	public void wasDeleted( Category category );
	public void listWasChanged();
	public void selected( Category category );
	
}
