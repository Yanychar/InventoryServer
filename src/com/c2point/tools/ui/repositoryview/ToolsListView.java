package com.c2point.tools.ui.repositoryview;

import java.util.Collection;

import com.c2point.tools.entity.repository.ItemStatus;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Category;
import com.c2point.tools.entity.tool.Tool;
import com.c2point.tools.ui.category.CategoryModelListener;
import com.c2point.tools.ui.listeners.FilterListener;
import com.c2point.tools.ui.listeners.ToolItemChangedListener;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ToolsListView extends VerticalLayout implements CategoryModelListener, ToolItemChangedListener, FilterListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( ToolsListView.class.getName());
	
	protected FilterToolbar		toolBarLayout;
	private ItemInfoComponent	infoComp = new ItemInfoComponent();
	
	
	private ToolsListModel		model; 
	private Table 				itemsTable;
	
	public ToolsListView( ToolsListModel model ) {
		super();
		
		this.model = model; 

		initUI();
		
		model.addChangedListener(( CategoryModelListener ) this );		
		model.addChangedListener(( ToolItemChangedListener ) this );		
//		model.addChangedListener( infoComp );
		
	}
	
	private void initUI() {

		setSizeFull();

		setMargin( true );
		setSpacing( true );

		
		itemsTable = new Table();
		initTable();
	
		Panel infoPanel = new Panel();
		infoPanel.addStyleName( "light" );
		
		infoComp = new ItemInfoComponent();
		infoPanel.setContent( infoComp );

		this.addComponent( getFilterToolBar());
		this.addComponent( itemsTable );
		this.addComponent( infoPanel );
		
//		this.setExpandRatio( table, 1.0f );
//		this.setExpandRatio( infoPanel, 0.20f );
		
	}
	
	private void initTable() {

		// Configure table
		itemsTable.setSelectable( true );
		itemsTable.setMultiSelect( false );
//		table.setNullSelectionAllowed( false );
		itemsTable.setColumnCollapsingAllowed( false );
		itemsTable.setColumnReorderingAllowed( false );
//		table.setColumnHeaderMode( Table.ColumnHeaderMode.HIDDEN );
//		table.setSortEnabled( false );
		itemsTable.setImmediate( true );
		itemsTable.setSizeFull();
		
//		categoriesTree.addContainerProperty( "code",		String.class, 	null );
		itemsTable.addContainerProperty( "photo",		Embedded.class, null );
		itemsTable.addContainerProperty( "tool", 		Label.class, 	null );
		itemsTable.addContainerProperty( "status", 		Label.class, 	"" );
		itemsTable.addContainerProperty( "user", 		String.class, 	"" );
		itemsTable.addContainerProperty( "data", 		ToolItem.class, null );

		itemsTable.setVisibleColumns( new Object [] { "photo", "tool", "status", "user" } );
		
		itemsTable.setColumnHeaders( new String[] { 
				model.getApp().getResourceStr( "repositorymgmt.list.header.photo" ),
				model.getApp().getResourceStr( "repositorymgmt.list.header.tool" ),
				model.getApp().getResourceStr( "repositorymgmt.list.header.status" ),
				model.getApp().getResourceStr( "repositorymgmt.list.header.user" )
		});
		
		// New User has been selected. Send event to model
		itemsTable.addValueChangeListener( new ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			public void valueChange( ValueChangeEvent event) {
				if ( logger.isDebugEnabled()) logger.debug( "Selection were changed" );

				try {
					Item item = itemsTable.getItem( itemsTable.getValue());
					model.setSelectedItem(( ToolItem ) item.getItemProperty( "data" ).getValue());
				} catch ( Exception e ) {
					logger.debug( "No selection. Tool Item cannot be fetched from itemsList " );
					model.setSelectedItem( null );
				}
				
			}
		});
		
	}
	
	private FilterToolbar getFilterToolBar() {
		
		if ( toolBarLayout == null ) {
			toolBarLayout = new FilterToolbar( this, model );
		}
		return toolBarLayout;
	}
	private void dataFromModel() {
		dataFromModel( model.getSelectedCategory());
	}
	
	private void dataFromModel( Category category ) {

		Collection<ToolItem> itemList = 
							model.getItems( category );
		
		
		itemsTable.removeAllItems();
		
		if ( itemList != null && itemList.size() > 0 ) {
			for ( ToolItem repItem : itemList ) {
				if ( repItem  != null ) {
					addOrUpdateItem( repItem );
				}
			}
		}
		
		itemsTable.setSortContainerPropertyId( "tool" );

		itemsTable.sort();
		
	}
	
	@SuppressWarnings("unchecked")
	private void addOrUpdateItem( ToolItem toolItem ) {

		Item item = itemsTable.getItem( toolItem.getId());
		
		if ( item == null ) {

//			if ( logger.isDebugEnabled()) logger.debug( "Tool Item will be added: " + toolItem );
			item = itemsTable.addItem( toolItem.getId());
			item.getItemProperty( "tool" ).setValue( new Label( "", ContentMode.HTML ));
			item.getItemProperty( "status" ).setValue( new Label( "", ContentMode.HTML ));

		} else {
			if ( logger.isDebugEnabled()) logger.debug( "Tool Item exists already. Will be modified: " + toolItem );
		}

		
		// Item Photo column 
		item.getItemProperty( "photo" ).setValue( getItemPhoto( toolItem ));
		
		setToolProperty( item, toolItem );
		setStatusProperty( item, toolItem );

		try {
			item.getItemProperty( "user" ).setValue( toolItem.getCurrentUser().getLastAndFirstNames());
		} catch ( Exception e ) {
			item.getItemProperty( "user" ).setValue( "No user" );
		}
		
		
		item.getItemProperty( "data" ).setValue( toolItem );
	
	}
	
	
	@Override
	public void wasAdded( Category category ) {}
	@Override
	public void wasChanged(Category category) {}
	@Override
	public void wasDeleted(Category category) {}
	@Override
	public void listWasChanged() {

		if ( logger.isDebugEnabled()) logger.debug( "Category List was changed event received!" );
		dataFromModel();
		
	}
	
	@Override
	public void selected( Category category ) {

		if ( logger.isDebugEnabled()) logger.debug( "Category selected event received!" );
		
		dataFromModel( category );
		
	}

	private Embedded getItemPhoto( ToolItem repItem ) {

		String iconName = "icons/64/nophoto.png";
		String tooltipStr = "";
			
		Embedded icon = new Embedded( "", new ThemeResource( iconName ));
		icon.setDescription( tooltipStr );
		
		return icon;
		
	}

	@SuppressWarnings("unchecked")
	private void setToolProperty( Item item, ToolItem toolItem ) {

		try {
			Label toolLabel = ( Label )item.getItemProperty( "tool" ).getValue();
			String nameStr;
				nameStr = "<b>" 
						+ toolItem.getTool().getFullName()
						+ "</b><br>"
						+ StringUtils.defaultString( toolItem.getTool().getDescription())
						;
	
			toolLabel.setValue( nameStr );
			
		} catch ( Exception e ) {
			logger.error( "Could not create Tool property because unknown reason. Tool Item: " + toolItem );
			item.getItemProperty( "tool" ).setValue( new Label( "?" ));
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void setStatusProperty( Item item, ToolItem toolItem ) {

		try {
			Label statusLabel = ( Label )item.getItemProperty( "status" ).getValue();
			String nameStr;
				nameStr = "<b "
						+ getColorAttribute( toolItem.getStatus())
						+ ">"
						+ toolItem.getStatus().toString( model.getApp().getSessionData().getBundle())
						+ "</b>" 
						;
	
				statusLabel.setValue( nameStr );
			
		} catch ( Exception e ) {
			logger.error( "Could not create Tool property because unknown reason. Tool Item: " + toolItem );
			item.getItemProperty( "status" ).setValue( new Label( "?" ));
		}
		
	}

	private String getColorAttribute( ItemStatus status ) {
		
		switch( status ) {
			case FREE:
				return "style='color:green'"; 
				
			case RESERVED:
			case BROKEN:
			case REPAIRING:
			case STOLEN:
			case UNKNOWN:
				return "style='color:red'"; 
			case INUSE:
				return "style='color:#FDD835'"; 

		}
		
		return ""; 
		
	}

	class ToolsViewFilter implements Container.Filter {
		private static final long serialVersionUID = 1L;
		
		private Collection<String>	strArray;
		private ItemStatus 			status;
		
		public ToolsViewFilter( Collection<String> strArray, ItemStatus status ) {
		
			this.strArray = strArray;
			this.status = status;
		}
		
		
		
		@Override
		public boolean passesFilter( Object itemId, Item item )
				throws UnsupportedOperationException {
			
			// If no filter defined that any record is OK. Return TRUE
			if (( strArray == null || strArray.size() == 0 ) && status == null ) return true;
						
			ToolItem toolItem;
			try {
				toolItem = ( ToolItem ) item.getItemProperty( "data" ).getValue();
			} catch ( Exception e ) {
				return false;
			}

			if ( toolItem == null ) return false;

			if ( this.status != null && toolItem.getStatus() != this.status ) return false;

			Tool tool = toolItem.getTool();
			
			if (( strArray == null || strArray.size() == 0 )) return true;
			
			for ( String searchStr : strArray ) {
			
				try {
					searchStr = searchStr.toLowerCase().trim();
					
					if ( tool.getName() != null &&
						 tool.getName().toLowerCase().indexOf( searchStr ) != -1 ) return true;
					
					if ( tool.getDescription() != null &&
						 tool.getDescription().toLowerCase().indexOf( searchStr ) != -1 ) return true;
	
					try {
						if ( tool.getManufacturer().getName().toLowerCase().indexOf( searchStr ) != -1 ) return true;
					} catch ( Exception e ) {}
					
					
					if ( tool.getModel() != null &&
							 tool.getModel().toLowerCase().indexOf( searchStr ) != -1 ) return true;
					
					if ( toolItem.getBarcode() != null &&
						 toolItem.getBarcode().toLowerCase().indexOf( searchStr ) != -1 ) return true;
					
					if ( toolItem.getSerialNumber() != null &&
						 toolItem.getSerialNumber().toLowerCase().indexOf( searchStr ) != -1 ) return true;
					
					if ( toolItem.getCurrentUser() != null &&
						 toolItem.getCurrentUser().getFirstAndLastNames().toLowerCase().indexOf( searchStr ) != -1 ) return true;

					
				} catch ( Exception e ) {
					return false;
				}
			}
			
			return false;
		}

		@Override
		public boolean appliesToProperty( Object propertyId ) {
			
			return ( propertyId != null && propertyId.equals( "data" )); 
			
		}

	}	

	@Override
	public boolean filterWasChanged( Collection<String> strArray, ItemStatus status ) {

		boolean found = false;
		
		((Filterable) itemsTable.getContainerDataSource()).removeAllContainerFilters();

		if ( strArray != null && strArray.size() > 0 && itemsTable.getContainerDataSource() != null || status != null ) {

			
			Filter filter = new ToolsViewFilter( strArray, status );
					
			((Filterable) itemsTable.getContainerDataSource()).addContainerFilter( filter );
					
			found = itemsTable.getContainerDataSource().size() > 0;
				
			if ( itemsTable != null && itemsTable.getContainerDataSource() instanceof Container.Ordered ) {
			
				itemsTable.setValue( found ? itemsTable.firstItemId() : null );
			}
				
			if ( logger.isDebugEnabled()) logger.debug( "Search: str = '" + strArray + "'. Found? " + found );
			
		}
		
		return found;
		
	}

	@Override
	public void wasAdded(ToolItem item) {}
	@Override
	public void wasDeleted(ToolItem item) {}

	@Override
	public void wholeListChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void currentWasSet(ToolItem item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void wasChanged( ToolItem item ) {

		logger.debug( "Tool Items List receives notification: Tool Item was Changed!" );
		
		// Find correct Item. Start from selected one
		// update row with data 
		addOrUpdateItem( item );
		
		// set correct selection
		itemsTable.setValue( item.getId());
		
	}

	
	
}
