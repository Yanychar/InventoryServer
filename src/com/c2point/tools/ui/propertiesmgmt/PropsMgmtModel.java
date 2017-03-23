package com.c2point.tools.ui.propertiesmgmt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.SettingsFacade;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.ui.listeners.PropertiesListener;
import com.c2point.tools.ui.util.AbstractModel;
import com.c2point.tools.ui.util.ChangesCollector;

public class PropsMgmtModel extends AbstractModel {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( PropsMgmtModel.class.getName());

	private Organisation 		org;

	private ChangesCollector	changesCollector = new ChangesCollector();
	
	
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

	public boolean update() {
		
		boolean bRes = SettingsFacade.getInstance().persistOrg( this.org );
		
		return bRes;
	}

	public ChangesCollector	getChangesCollector() { return  this.changesCollector; }
	

	public boolean wasItChanged() {
		
		return this.changesCollector.wasItChanged();
	}

}
	