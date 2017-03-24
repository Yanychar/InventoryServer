package com.c2point.tools.ui.toolsmgmt;

import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.CategoriesFacade;
import com.c2point.tools.datalayer.ItemsFacade;
import com.c2point.tools.datalayer.ToolsFacade;
import com.c2point.tools.datalayer.UsersFacade;
import com.c2point.tools.entity.access.FunctionalityType;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Category;
import com.c2point.tools.entity.tool.Manufacturer;
import com.c2point.tools.entity.tool.Tool;
import com.c2point.tools.ui.listeners.ToolItemChangedListener;
import com.c2point.tools.ui.util.AbstractModel;

public class ToolsListModel extends AbstractModel {
	private static Logger logger = LogManager.getLogger( ToolsListModel.class.getName());

	private Organisation 		selectedOrg;
	private ToolItem 			selectedItem;
	private Category 			selectedCategory = null;	

	public ToolsListModel() {
		this( null );

		
	}
	
	public ToolsListModel( Organisation org ) {
		super();
		
		this.selectedOrg = ( org != null ? org : getApp().getSessionData().getOrg());
		setViewMode();

		setupAccess( FunctionalityType.TOOLS_MGMT, this.selectedOrg );
		
	}
	
	public void initModel() {
		
		// Initial model initialization here if necesary
		
		fireListChanged();
	}
	
	public void addListener( ToolItemChangedListener listener ) {
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

	public Organisation getSelectedOrg() { return selectedOrg; }
	public void setSelectedOrg( Organisation selectedOrg ) {
		
		if ( getSelectedOrg() != selectedOrg ) {
			
			this.selectedOrg = selectedOrg;

			setupAccess( FunctionalityType.TOOLS_MGMT, this.selectedOrg );
			
			fireListChanged();
		}
	}
	

	public Collection<ToolItem> getItems() {

		if ( selectedCategory == null ) {
			
			if ( logger.isDebugEnabled()) logger.debug( "Read all ToolItems has been call!" );
			
			return ItemsFacade.getInstance().getItems( getSelectedOrg());
			
		}
		
		if ( logger.isDebugEnabled()) logger.debug( "Read Category related ToolItems has been call!" );

		return ItemsFacade.getInstance().getItems( selectedCategory, getSelectedOrg());
		
	}

	public boolean isSuperUser() {
		
		return getApp().getSessionData().getOrgUser().isSuperUserFlag();
		
	}

	public Collection<Category> getCategories() {
		
		return CategoriesFacade.getInstance().listTop( getSelectedOrg());
	}

	private Category topCat = null; 
	public Category getTopCategory() { 
	
		if ( this.topCat == null ) {
			topCat = new Category( "", getApp().getResourceStr( "category.top.caption" ));
			
		}
	
		return topCat;
	}
	
	public Category getSelectedCategory() { return selectedCategory; }
	public void setSelectedCategory( Category category ) {

		if ( category == this.topCat ) {
			this.selectedCategory = null;
		} else {
			this.selectedCategory = category;
		}
		
		initModel();
		
	}

	public Collection<Manufacturer> getManufacturers() {
		
		return ItemsFacade.getInstance().getManufacturers();
	}

	public Collection<OrgUser> getUsers() {
		
		return UsersFacade.getInstance().list( getSelectedOrg());
	}

	public Collection<Tool> getTools() {
		
		if ( this.selectedCategory != null ) {

			return ToolsFacade.getInstance().searchTools( getSelectedOrg(), this.selectedCategory );
		}
		
				
		return ToolsFacade.getInstance().getTools( getSelectedOrg());
	}

	public ToolItem addItem() {
		return addItem( this.selectedItem );
	}
	
	public ToolItem addItem( ToolItem addedItem ) {
		
		ToolItem newItem = null;
		
		// Add to DB
		if ( addedItem != null ) {

			newItem = ItemsFacade.getInstance().add( addedItem );
			
			if ( newItem != null ) {
				
				fireAdded( newItem );
			
			} 
			
		}
		
		return newItem;
		
	}
	
	public ToolItem addToolAndItem( ) {
		return addToolAndItem( this.selectedItem );
	}
	public ToolItem addToolAndItem( ToolItem item  ) {
		
		Tool 		newTool = null;
		ToolItem 	newItem = null;			
		
		// Add to DB
		if (  item != null && item.getTool() != null ) {

			// If New Tool than add Tool firstly
			if ( item.getTool().getId() <=0 ) {

				newTool = ToolsFacade.getInstance().add( item.getTool());
				
				if ( newTool != null ) {
				
					item.setTool( newTool );
				} else {
					logger.error( "Couldn't add new Tool: " + item.getTool());
					return null;
				}
			}
			
			// Now Add ToolItem
			newItem = addItem( item );			
			
			if ( newItem != null ) {
				
				fireAdded( newItem );
			
			}
			
			
		} else {
			// Missing Tool and/or ToolItem. Nothing to add/edit 
			logger.error( "Cannot add Tool or ToolItem. Something is NULL!" );
		}
		
		
		return newItem;

	}
	
	public ToolItem updateItem() {
		return updateItem( this.selectedItem );
	}
	public ToolItem updateItem( ToolItem item ) {
		
		ToolItem newItem = null;
		
		// Update DB
		if ( item != null ) {

			newItem = ItemsFacade.getInstance().update( item );
			
			if ( newItem != null ) {
				
				fireChanged( newItem );
			
			} 
			
		}
		
		return newItem;
		
	}
	
	public ToolItem delete( ToolItem deletedItem ) {
		
		ToolItem newItem = null;

		// Mark item as deleted in  DB
		if ( deletedItem != null ) {

			newItem = ItemsFacade.getInstance().delete( deletedItem );
			
			if ( newItem != null ) {
				
				fireDeleted( deletedItem );
			
			} 
			
		}
		
		return newItem;
		
	}

	public List<Tool> getTools( Manufacturer manuf ) {

		List<Tool> result = ToolsFacade.getInstance().searchTools( selectedOrg, manuf );

		return result;
	}

/*
	public ToolItem copyItem() {
		
		ToolItem 
		
		return newItem( null );
	}

	public ToolItem copyTool( ) {
		
		return newItem( null );
	}

	public ToolItem newTool() {
		
		return newItem( null );
	}
*/	
}
