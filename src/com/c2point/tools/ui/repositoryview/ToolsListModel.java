package com.c2point.tools.ui.repositoryview;

import java.util.Collection;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.InventoryUI;
import com.c2point.tools.datalayer.CategoriesFacade;
import com.c2point.tools.datalayer.ItemsFacade;
import com.c2point.tools.datalayer.UsersFacade;
import com.c2point.tools.entity.access.AccessGroups;
import com.c2point.tools.entity.access.FunctionalityType;
import com.c2point.tools.entity.access.SecurityContext;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ItemStatus;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Category;
import com.c2point.tools.entity.tool.Manufacturer;
import com.c2point.tools.entity.tool.Tool;
import com.c2point.tools.ui.AbstractModel;
import com.c2point.tools.ui.listeners.CategoryChangedListener;
import com.c2point.tools.ui.listeners.ToolItemChangedListener;
import com.vaadin.ui.UI;

public class ToolsListModel extends AbstractModel {

	private static Logger logger = LogManager.getLogger( ToolsListModel.class.getName());

	private Organisation 		org;

	private Filter itemsFilter = new Filter();

	private ToolItem 			selectedItem;

	
	
	public ToolsListModel() {
		
		this( null );
		
	}
	
	public ToolsListModel( Organisation org ) {
		super();
		
		setOrg( org != null ? org : getApp().getSessionData().getOrg());
		
	}
	
	public void init() {
		
		// Initial model initialization here if necesary
		
		fireListChanged();		
		
	}

	public Organisation getOrg() { return org; }
	public void setOrg( Organisation org ) { this.org = org; }

	public Collection<Category> getCategories() {
		
		return CategoriesFacade.getInstance().listTop( this.getApp().getSessionData().getOrg());
		
	}

	public void addChangedListener( CategoryChangedListener listener ) {
		listenerList.add( CategoryChangedListener.class, listener);
	}
	
	protected void fireCategoryListChanged() {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == CategoryChangedListener.class) {
	    		(( CategoryChangedListener )listeners[ i + 1 ] ).listWasChanged();
	         }
	     }
	 }

	protected void fireCategoryAdded( Category category ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == CategoryChangedListener.class) {
	    		(( CategoryChangedListener )listeners[ i + 1 ] ).wasAdded( category );
	         }
	     }
	 }
	
	protected void fireCategoryChanged( Category category ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == CategoryChangedListener.class) {
	    		(( CategoryChangedListener )listeners[ i + 1 ] ).wasChanged( category );
	         }
	     }
	 }
	
	protected void fireCategoryDeleted( Category category ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == CategoryChangedListener.class) {
	    		(( CategoryChangedListener )listeners[ i + 1 ] ).wasDeleted( category );
	         }
	     }
	 }

	protected void fireCategorySelected( Category category ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == CategoryChangedListener.class) {
	    		(( CategoryChangedListener )listeners[ i + 1 ] ).selected( category );
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

	public void addChangedListener( ToolItemChangedListener listener ) {
		listenerList.add( ToolItemChangedListener.class, listener);
	}
	
	public void fireChanged( ToolItem item ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ToolItemChangedListener.class) {
	    		(( ToolItemChangedListener )listeners[ i + 1 ] ).wasChanged( item );
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

	
	/*
	 *  Implementation of repositoryItem-s search
	 */

	public Collection<ToolItem> getItems() {
		
		Collection<ToolItem> list;

		SecurityContext context = getApp().getSessionData().getContext();
		
		if ( context.hasViewPermissionMgmt( FunctionalityType.BORROW )) {

			list = ItemsFacade.getInstance().getItems( getOrg());
			
		} else if ( context.hasViewPermissionOwn( FunctionalityType.CHANGESTATUS )) {

			list = ItemsFacade.getInstance().getItems( getApp().getSessionOwner());
			
		} else {
			
			list = null;
		}
			
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
		fireListChanged();		
	}
	
	public void setStatusFilter( ItemStatus status ) {

		itemsFilter.setStatus( status );
		fireListChanged();		
	}

	public void setManufFilter( Manufacturer manuf ) {

		itemsFilter.setManuf(manuf);
		fireListChanged();		
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
