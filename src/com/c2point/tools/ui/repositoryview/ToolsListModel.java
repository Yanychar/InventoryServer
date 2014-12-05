package com.c2point.tools.ui.repositoryview;

import java.util.Collection;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.CategoriesFacade;
import com.c2point.tools.datalayer.ItemsFacade;
import com.c2point.tools.datalayer.UsersFacade;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ItemStatus;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Category;
import com.c2point.tools.entity.tool.Manufacturer;
import com.c2point.tools.entity.tool.Tool;
import com.c2point.tools.ui.AbstractModel;
import com.c2point.tools.ui.category.CategoryModelListener;

public class ToolsListModel extends AbstractModel {

	private static Logger logger = LogManager.getLogger( ToolsListModel.class.getName());

	private Organisation 		org;

	private Filter itemsFilter = new Filter();
	
	public ToolsListModel() {
		
		this( null );
		
	}
	
	public ToolsListModel( Organisation org ) {
		super();
		
		setOrg( org != null ? org : getApp().getSessionData().getOrg());
		
	}
	
	public void init() {
		
		// Initial model initialization here if necesary
		
		
		fireCategoryListChanged();
	}

	public Organisation getOrg() { return org; }
	public void setOrg( Organisation org ) { this.org = org; }
	
	public Collection<Category> getTopCategories() {
		
		Collection<Category> retList = null;
		
		retList = CategoriesFacade.getInstance().listTop( this.getApp().getSessionData().getOrg());
		
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

	private Category	selectedCategory = null;
	
	
	public void categorySelected( Category category ) {
		logger.debug( "Category has been selected: " + ( category != null ? category.getName() : "NULL" ));
		
		setSelectedCategory( category );

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

	protected void fireToolChanged( ToolItem repItem ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ToolsModelListener.class) {
	    		(( ToolsModelListener )listeners[ i + 1 ] ).wasChanged( repItem );
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
			ItemsFacade.getInstance().getItems( getOrg());
			
		return itemsFilter.getFilteredItems( list );
		
	}

	public Collection<ToolItem> getItems( Category category ) {

		// If category is not specified or it is Top Category "All Items" than show all items
		if ( category == null ||
			 category != null && category.isTopCategoryFlag()) {
			
			return getItems();
		}
		
		return itemsFilter.getFilteredItems( ItemsFacade.getInstance().getItems( category, getOrg()));
		
	}

	public Collection<ToolItem> getItems( Tool tool ) {

		// If tool is not specified than show all items
		if ( tool == null ) {
			
			return getItems();
		}
		
		return null;
	}

	public Collection<OrgUser> getUsers() {
		
		return UsersFacade.getInstance().list( org );
	}

	public Collection<Manufacturer> getManufacturers() {
		
		return ItemsFacade.getInstance().getManufacturers(); // org );
	}

	public void setUserFilter( OrgUser user ) {

		itemsFilter.setUser(user );
		fireToolListChanged();		
	}
	
	public void setStatusFilter( ItemStatus status ) {

		itemsFilter.setStatus( status );
		fireToolListChanged();		
	}

	public void setManufFilter( Manufacturer manuf ) {

		itemsFilter.setManuf(manuf);
		fireToolListChanged();		
	}
	
	
	public Category getSelectedCategory() { return selectedCategory; }
	public void setSelectedCategory(Category selectedCategory) { this.selectedCategory = selectedCategory; }

	class Filter {
		OrgUser user;
		ItemStatus status;
		Manufacturer manuf;
		
		public OrgUser getUser() { return user; }
		public void setUser(OrgUser user) { 
			this.user = user; 
			logger.debug( this );
		}
		
		public ItemStatus getStatus() { return status; }
		public void setStatus(ItemStatus status) { 
			this.status = status;
			logger.debug( this );
		}
		
		public Manufacturer getManuf() { return manuf; }
		public void setManuf(Manufacturer manuf) { 
			this.manuf = manuf;
			logger.debug( this );
		}
		

		Collection<ToolItem> getFilteredItems( Collection<ToolItem> initialCollection ) {
			
			if ( initialCollection != null && ( user != null || status != null || manuf != null )) {
				
				for ( Iterator<ToolItem> iterator = initialCollection.iterator(); iterator.hasNext();) {
					ToolItem item = iterator.next();
				    if ( filteredOut( item )) {
				        // Remove the current element from the iterator and the list.
				        iterator.remove();
				    }
				}
			
			}
			
			return initialCollection;
		}
		
		boolean filteredOut( ToolItem item ) {

			if ( this.user != null && 
				 ( item.getCurrentUser() == null ||
				   item.getCurrentUser().getId() != this.user.getId()
				 )
			) {
				return true;
			}
			
			if ( this.status != null && 
					 ( item.getStatus() == null ||
					   item.getStatus() != this.status
					 )
			) {
					return true;
			}

			if ( this.manuf != null && 
					 ( item.getTool().getManufacturer() == null ||
					   item.getTool().getManufacturer().getId() != this.manuf.getId()
					 )
				) {
					return true;
				}
				
			
			return false;

		}
		
		public String toString() {
			
			return "Filter[ "
					+ "user: " + ( this.user != null ? this.user.getFirstAndLastNames() : "null" )
					+ " status: " + ( this.status != null ? this.status.toString() : "null" )
					+ " manuf.: " + ( this.manuf != null ? this.manuf.getName() : "null" )
					;
		}
	}
}
