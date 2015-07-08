package com.c2point.tools.ui.listeners;

import java.util.EventListener;

import com.c2point.tools.entity.person.OrgUser;

public interface StuffChangedListener extends EventListener {

	public void wasAdded( OrgUser user );
	public void wasChanged( OrgUser user );
	public void wasDeleted( OrgUser user );
	public void wholeListChanged();					//  List was re-read fully 
	public void currentWasSet( OrgUser user );  // CurrentItem was set

}
