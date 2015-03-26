package com.c2point.tools.ui.listeners;

import java.util.EventListener;

import com.c2point.tools.entity.repository.ToolItem;

public interface ToolItemChangedListener extends EventListener {

	public void wasAdded( ToolItem item );
	public void wasChanged( ToolItem item );
	public void wasDeleted( ToolItem item );
	public void wholeListChanged();					//  List was re-read fully 
	public void currentWasSet( ToolItem item );  // CurrentItem was set

}
