package com.c2point.tools.ui.orgmgmt;

import java.util.EventListener;

import com.c2point.tools.entity.organisation.Organisation;

public interface OrgChangedListener extends EventListener {

	public void wasAdded( Organisation org );
	public void wasChanged( Organisation org );
	public void wasDeleted( Organisation org );
	public void wholeListChanged();					//  List was re-read fully 
	public void currentWasSet( Organisation org );  // CurrentItem was set

}
