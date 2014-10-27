package com.c2point.tools.ui.repositoryview;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.CategoriesFacade;
import com.c2point.tools.datalayer.MsgFacade;
import com.c2point.tools.datalayer.ToolsAndItemsFacade;
import com.c2point.tools.entity.msg.Message;
import com.c2point.tools.entity.msg.MessageType;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Category;
import com.c2point.tools.entity.tool.Tool;
import com.c2point.tools.ui.AbstractModel;
import com.c2point.tools.ui.category.CategoryModelListener;

public class ToolsListModel extends AbstractModel {

	private static Logger logger = LogManager.getLogger( ToolsListModel.class.getName());
	
	public ToolsListModel() {
		super();
		
	}
	
	public void init() {
		
		// Initial model initialization here if necesary
		
		
		fireCategoryListChanged();
	}
	
	public Collection<Category> getTopCategories() {
		
		Collection<Category> retList = null;
		
		retList = CategoriesFacade.getInstance().listTop();
		
		// Read data from DB here 
		
		return retList;
	}

	public void addChangedListener( CategoryModelListener listener ) {
		listenerList.add( CategoryModelListener.class, listener);
	}
	
	
	protected void fireCategoryListChanged() {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == CategoryModelListener.class) {
	    		(( CategoryModelListener )listeners[ i + 1 ] ).listWasChanged();
	         }
	     }
	 }

	protected void fireCategoryAdded( Category category ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == CategoryModelListener.class) {
	    		(( CategoryModelListener )listeners[ i + 1 ] ).wasAdded( category );
	         }
	     }
	 }
	
	protected void fireCategoryChanged( Category category ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == CategoryModelListener.class) {
	    		(( CategoryModelListener )listeners[ i + 1 ] ).wasChanged( category );
	         }
	     }
	 }
	
	protected void fireCategoryDeleted( Category category ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == CategoryModelListener.class) {
	    		(( CategoryModelListener )listeners[ i + 1 ] ).wasDeleted( category );
	         }
	     }
	 }

	protected void fireCategorySelected( Category category ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == CategoryModelListener.class) {
	    		(( CategoryModelListener )listeners[ i + 1 ] ).selected( category );
	         }
	     }
	 }

	public void categorySelected( Category category ) {
		logger.debug( "Category has been selected: " + ( category != null ? category.getName() : "NULL" ));

		fireCategorySelected( category );
		
	}

	/*
	 * * Tool selection notification
	 */
	public void toolSelected( ToolItem repItem ) {
		
		fireToolSelected( repItem );
	}

	public void toolSelected( Object obj ) {
		
		if ( obj instanceof ToolItem ) {
			
			toolSelected(( ToolItem )obj );
			
		}
		
	}
	
	
	public void addChangedListener( ToolsModelListener listener ) {
		listenerList.add( ToolsModelListener.class, listener);
	}
	
	
	protected void fireToolListChanged() {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ToolsModelListener.class) {
	    		(( ToolsModelListener )listeners[ i + 1 ] ).listWasChanged();
	         }
	     }
	 }

	protected void fireToolAdded( ToolItem repItem ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ToolsModelListener.class) {
	    		(( ToolsModelListener )listeners[ i + 1 ] ).wasAdded( repItem );
	         }
	     }
	 }
	
	protected void fireCategoryChanged( ToolItem repItem ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ToolsModelListener.class) {
	    		(( ToolsModelListener )listeners[ i + 1 ] ).wasChanged( repItem );
	         }
	     }
	 }
	
	protected void fireCategoryDeleted( ToolItem repItem ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ToolsModelListener.class) {
	    		(( ToolsModelListener )listeners[ i + 1 ] ).wasDeleted( repItem );
	         }
	     }
	 }

	protected void fireToolSelected( ToolItem repItem ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ToolsModelListener.class) {
	    		(( ToolsModelListener )listeners[ i + 1 ] ).selected( repItem );
	         }
	     }
	 }

	
	
	
	/*
	 *  Implementation of repositoryItem-s search
	 */

	public Collection<ToolItem> getItems() {
		
		Collection<ToolItem> list = 
			ToolsAndItemsFacade.getInstance().getItems( getOrg());
			
		return list;
	}

	public Collection<ToolItem> getItems( Category category ) {

		// If category is not specified or it is Top Category "All Items" than show all items
		if ( category == null ||
			 category != null && category.isTopCategoryFlag()) {
			
			return getItems();
		}
		
		return ToolsAndItemsFacade.getInstance().getItems( category, getOrg());
		
	}

	public Collection<ToolItem> getItems( Tool tool ) {

		// If tool is not specified than show all items
		if ( tool == null ) {
			
			return getItems();
		}
		
		return null;
	}

	
}
