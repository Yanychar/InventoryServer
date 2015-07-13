package com.c2point.tools.ui.propertiesmgmt;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.DataFacade;
import com.c2point.tools.datalayer.OrganisationFacade;
import com.c2point.tools.datalayer.PresenceFilterType;
import com.c2point.tools.datalayer.UsersFacade;
import com.c2point.tools.entity.access.FunctionalityType;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.ui.AbstractModel;
import com.c2point.tools.ui.listeners.OrgChangedListener;
import com.c2point.tools.ui.listeners.PropertiesListener;

public class PropsMgmtModel extends AbstractModel {
	private static Logger logger = LogManager.getLogger( PropsMgmtModel.class.getName());

	private Organisation 		org;
	
	public PropsMgmtModel( Organisation org ) {
		super();

		setOrg( org );

	}
	
	public void initModel() {
		
		// Initial model initialization here if necesary
		
	}
	
	public void addChangedListener( PropertiesListener listener ) {
		listenerList.add( PropertiesListener.class, listener);
	}
	
	protected void fireChanged( Organisation org ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == PropertiesListener.class) {
	    		(( PropertiesListener )listeners[ i + 1 ] ).wasChanged( org );
	         }
	     }
	 }
	
	public Organisation getOrg() { return this.org; }
	public void setOrg( Organisation org ) { this.org = org; }

	public boolean changed() {
		
		return true;
	}
	
	public boolean update() {
		
		boolean bRes = false;
		
		return bRes;
	}
	
}
	