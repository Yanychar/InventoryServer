package com.c2point.tools.ui.toolsmgmt;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.ToolsAndItemsFacade;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.ui.AbstractModel;

public class ToolsManagementModel extends AbstractModel {
	private static Logger logger = LogManager.getLogger( ToolsManagementModel.class.getName());

	private Organisation 		org;
	private ToolItem 			selectedItem;
	

	public ToolsManagementModel() {
		this( null );

		
	}
	
	public ToolsManagementModel( Organisation org ) {
		super();
		
		setOrg( 
				org != null ? 
					org 
				: 
					getApp().getSessionData().getOrg()
		);
	}
	
	public void initModel() {
		
		// Initial model initialization here if necesary
		
		fireListChanged();
	}
	
	public void addChangedListener( ToolItemChangedListener listener ) {
		listenerList.add( ToolItemChangedListener.class, listener);
	}
	
	protected void fireAdded( ToolItem item ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ToolItemChangedListener.class) {
	    		(( ToolItemChangedListener )listeners[ i + 1 ] ).wasAdded( item );
	         }
	     }
	 }
	
	protected void fireChanged( ToolItem item ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ToolItemChangedListener.class) {
	    		(( ToolItemChangedListener )listeners[ i + 1 ] ).wasChanged( item );
	         }
	     }
	 }
	
	protected void fireDeleted( ToolItem item ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ToolItemChangedListener.class) {
	    		(( ToolItemChangedListener )listeners[ i + 1 ] ).wasDeleted( item );
	         }
	     }
	 }
	
	protected void fireListChanged() {
		Object[] listeners = listenerList.getListenerList();
		
		if ( logger.isDebugEnabled()) logger.debug( "ToolsManagementModel issued WholeListChanged event!" );

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ToolItemChangedListener.class) {
	    		(( ToolItemChangedListener )listeners[ i + 1 ] ).wholeListChanged();
	         }
	     }
	 }

	protected void fireSelected( ToolItem item ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ToolItemChangedListener.class) {
	    		(( ToolItemChangedListener )listeners[ i + 1 ] ).currentWasSet( item );
	         }
	     }
	 }

	public ToolItem getSelectedItem() { return selectedItem; }
	public void setSelectedItem( ToolItem selectedItem ) { 
		this.selectedItem = selectedItem; 
		fireSelected( selectedItem );
	}

	public Organisation getOrg() { return org; }
	public void setOrg( Organisation org ) { this.org = org; }

	public Collection<ToolItem> getItems() {
	
		return ToolsAndItemsFacade.getInstance().getItems( org );
		
	}

	public boolean isSuperUser() {
		
		return getApp().getSessionData().getOrgUser().isSuperUserFlag();
		
	}

	
}
