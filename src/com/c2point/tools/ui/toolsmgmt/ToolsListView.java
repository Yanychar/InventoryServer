package com.c2point.tools.ui.toolsmgmt;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Category;
import com.c2point.tools.entity.tool.Tool;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

public class ToolsListView extends VerticalLayout implements ToolItemChangedListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( ToolsListView.class.getName());

	
	protected HorizontalLayout	toolBarLayout;
	private ComboBox			categoryFilter;
	private TextField			searchText;

	protected Button			addButton;
	
	private ToolsListModel		model;
	private Table				itemsTable;
	
	
	
	public ToolsListView( ToolsListModel model ) {
		super();
		this.model = model;

		initView();

		model.addChangedListener( this );
		
	}

	private void initView() {

		setSizeFull();

		setMargin( true );
//		setSpacing( true );

		itemsTable = new Table();
		
		// Configure table
		itemsTable.setSelectable( true );
		itemsTable.setNullSelectionAllowed( false );
		itemsTable.setMultiSelect( false );
		itemsTable.setColumnCollapsingAllowed( false );
		itemsTable.setColumnReorderingAllowed( false );
		itemsTable.setImmediate( true );
		itemsTable.setSizeFull();

		itemsTable.addContainerProperty( "category",	String.class, null );
		itemsTable.addContainerProperty( "name", 		String.class, null );
		itemsTable.addContainerProperty( "status", 		String.class, null );
		itemsTable.addContainerProperty( "user", 		String.class, null );
		itemsTable.addContainerProperty( "data", 		ToolItem.class, null );

		itemsTable.setVisibleColumns( new Object [] { "category", "name", "status", "user" } );
		
		itemsTable.setColumnHeaders( new String[] { 
				model.getApp().getResourceStr( "toolsmgmt.list.header.category" ),
				model.getApp().getResourceStr( "toolsmgmt.list.header.tool" ),
				model.getApp().getResourceStr( "toolsmgmt.list.header.Status" ),
				model.getApp().getResourceStr( "toolsmgmt.list.header.user" )
		
		});
		
		itemsTable.addValueChangeListener( new  ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			public void valueChange( ValueChangeEvent event) {
				if ( logger.isDebugEnabled()) logger.debug( "Property.valueChanged!" );
				
				try {
					if ( logger.isDebugEnabled()) {
						
						logger.debug( "Table item selected. Item Id = " + itemsTable.getValue());
						logger.debug( "  Item = " + itemsTable.getItem( itemsTable.getValue()));

						if ( itemsTable.getItem( itemsTable.getValue()) != null )
							logger.debug( "  Tool Item was selected: " + ( ToolItem ) itemsTable.getItem( itemsTable.getValue()).getItemProperty( "data" ).getValue());
						
					}
					
					Item item = itemsTable.getItem( itemsTable.getValue());
					model.setSelectedItem(( ToolItem ) item.getItemProperty( "data" ).getValue());
					
					
				} catch ( Exception e ) {
					logger.debug( "No selection. Tool Item cannot be fetched from itemsList " );
					model.setSelectedItem( null );
				}
			}
		});
		
		this.addComponent( getToolbar());
		this.addComponent( itemsTable );
		
		this.setExpandRatio( itemsTable, 1.0f );
	}

	
	@Override
	public void wasAdded(ToolItem item) {

		logger.debug( "ToolItems List receives notification: Tool Item was added!" );
		
		// Find correct Item. Start from selected one
		// update row with data 
		addOrUpdateItem( item );
		
		// set correct selection
		itemsTable.setValue( item.getId());
		
	}

	@Override
	public void wasChanged(ToolItem item) {

		logger.debug( "Tool Items List receives notification: Tool Item was Changed!" );
		
		// Find correct Item. Start from selected one
		// update row with data 
		addOrUpdateItem( item );
		
		// set correct selection
		itemsTable.setValue( item.getId());
		
	}

	@Override
	public void wasDeleted(ToolItem item) {

		if ( logger.isDebugEnabled()) logger.debug( "Tool Items List received notification: Tool Item was Deleted!" );
		
		Object futureId;
		try {
			futureId = itemsTable.prevItemId( itemsTable.getValue());
		} catch ( Exception e ) {
			futureId = null;
		}
		
		itemsTable.removeItem( item.getId());
		
		if ( futureId != null ) 
			itemsTable.setValue( futureId );
		else
			itemsTable.setValue( itemsTable.firstItemId());
		
	}

	@Override
	public void wholeListChanged() {
		
		if ( logger.isDebugEnabled()) logger.debug( "Tool Items List received WhleListChanged event!" );
		
		dataFromModel();
		
	}

	@Override
	public void currentWasSet(ToolItem item) {
		// TODO Auto-generated method stub
		
	}

	private void dataFromModel() {

		if ( logger.isDebugEnabled()) logger.debug( "Data from model will be read!" );
		
		// Store selection for recovery at the end of this method
		Long selectedId = ( Long )itemsTable.getValue();
		Long newSelectedId = null;
		boolean selected = ( selectedId != null );
		
		// remove old content
		itemsTable.removeAllItems();

		Collection<ToolItem> itemsList = model.getItems();
		
		if ( itemsList != null ) {
			for ( ToolItem toolItem : itemsList ) {
				if ( toolItem != null ) {
					addOrUpdateItem( toolItem );
					
					// Check that selection can be restored
					if ( selected && toolItem.getId() == selectedId ) {
						newSelectedId = toolItem.getId();
						selected = false;
					}
				}
			}
		}
		
		itemsTable.setSortContainerPropertyId( "name" );

		itemsTable.sort();
		
		if ( newSelectedId != null ) {
			itemsTable.setValue( newSelectedId );
		} else {
			itemsTable.setValue( itemsTable.firstItemId());
		}
		
		
	}
	
	@SuppressWarnings("unchecked")
	private void addOrUpdateItem( ToolItem toolItem ) {
		
		Item item = itemsTable.getItem( toolItem.getId());
		
		if ( item == null ) {

//			if ( logger.isDebugEnabled()) logger.debug( "Tool Item will be added: " + toolItem );
			item = itemsTable.addItem( toolItem.getId());
			
		} else {
			if ( logger.isDebugEnabled()) logger.debug( "Tool Item exists already. Will be modified: " + toolItem );
		}

		item.getItemProperty( "category" ).setValue( getCategoryChain( toolItem.getTool().getCategory()));
		item.getItemProperty( "name" ).setValue( toolItem.getTool().getName());
		item.getItemProperty( "status" ).setValue( toolItem.getStatus().toString( model.getApp().getSessionData().getBundle()));
		item.getItemProperty( "user" ).setValue( toolItem.getCurrentUser().getFirstAndLastNames());
		item.getItemProperty( "data" ).setValue( toolItem );
		
	}
	
	
	protected Component getToolbar() {
		
		// Add search field
		if ( toolBarLayout == null ) {

			toolBarLayout = new HorizontalLayout();
			
			toolBarLayout.setWidth( "100%");
			toolBarLayout.setMargin( new MarginInfo( false, true, false, true ));
	
			Label catFilterLabel = new Label( this.model.getApp().getResourceStr( "toolsmgmt.label.filter.category" ));
			catFilterLabel.setWidth( null );

			categoryFilter = new ComboBox();
			categoryFilter.setFilteringMode( FilteringMode.CONTAINS );
			categoryFilter.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
			categoryFilter.setNullSelectionAllowed( false );
			categoryFilter.setInputPrompt( "Search" );
			categoryFilter.setInvalidAllowed( false );
			categoryFilter.setImmediate(true);
			
			initCategoryFilter();

			Label searchIcon = new Label();
			searchIcon.setIcon(new ThemeResource("icons/16/search.png"));
			searchIcon.setWidth( "2em" );
	
			Button deleteIcon = new Button();
			deleteIcon.setStyleName( BaseTheme.BUTTON_LINK );
			deleteIcon.setIcon( new ThemeResource("icons/16/reject.png"));
			
			deleteIcon.addClickListener( new ClickListener() {
	
				private static final long serialVersionUID = 1L;
	
				@Override
				public void buttonClick( ClickEvent event) {
	
					if ( logger.isDebugEnabled()) logger.debug( "DeleteIcon image had been pressed" );
					
					if ( searchText != null && searchText.getValue() != null && searchText.getValue().length() > 0 ) {
	
						if ( logger.isDebugEnabled()) logger.debug( "Search text shall be set to empty string" );
						
						searchText.setValue( "" );
						searchFieldUpdated( null );
						
					}
					
				}
				
			});
			
			searchText = new TextField();
			searchText.setWidth("30ex");
			searchText.setNullSettingAllowed(true);
			searchText.setInputPrompt( "Search ...");
			searchText.setImmediate( true );
			
			searchText.addTextChangeListener( new TextChangeListener() {
	
				private static final long serialVersionUID = 1L;
	
				@Override
				public void textChange( TextChangeEvent event ) {
					
					searchFieldUpdated( event.getText());
					
				}
				
			});
			
			
			addButton = new Button( model.getApp().getResourceStr( "personnel.add.caption" ));
			addButton.addClickListener( new ClickListener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void buttonClick( ClickEvent event) {

					addButtonHandler();
					
				}
				
			});
				
			
			toolBarLayout.addComponent( catFilterLabel );
			toolBarLayout.addComponent( categoryFilter );
			toolBarLayout.addComponent( searchIcon );
			toolBarLayout.addComponent( searchText );
			toolBarLayout.addComponent( deleteIcon );
			Label glue = new Label( "" );
			toolBarLayout.addComponent( glue );
			toolBarLayout.setExpandRatio( glue,  1.0f );

			toolBarLayout.addComponent( addButton );
		}
		
		return toolBarLayout;
	}

	private void addButtonHandler() {
		
	}
	
	private boolean searchFieldUpdated( String searchStr ) {
		
		boolean found = false;

		if ( itemsTable.getContainerDataSource() != null ) {
		
			((Filterable) itemsTable.getContainerDataSource()).removeAllContainerFilters();
		
			if ( searchStr != null && searchStr.length() > 0 ) {
				Filter filter = new ToolsViewFilter( searchStr );
				
				((Filterable) itemsTable.getContainerDataSource()).addContainerFilter( filter );
				
				
			}
			
			found = itemsTable.getContainerDataSource().size() > 0;
			
			if ( itemsTable != null && itemsTable.getContainerDataSource() instanceof Container.Ordered ) {
				itemsTable.setValue( found ? itemsTable.firstItemId() : null );
			}
			
		}
		
		if ( logger.isDebugEnabled()) logger.debug( "Search: str = '" + searchStr + "'. Found? " + found );
		
		return found;
	}

	private String getCategoryChain( Category category ) {
		
		String chain;
		Category tmpCategory = category;
		
		if ( tmpCategory != null ) {
			
			chain = tmpCategory.getName();
			while ( tmpCategory.getParent() != null ) {
				
				tmpCategory = tmpCategory.getParent();
				
				if ( tmpCategory.getName() != null ) {
					
					chain = tmpCategory.getName() + "\u25BA" + chain; 
							
				} else {
					chain = " ? " + "\u25BA" + chain; 
				}
				
			}
			
		} else {
			
			chain = "???";
			logger.error( "Could not create the String with chain of categories");
		}
		
		return chain;
	}

	private void initCategoryFilter() {

		// Add Category ALL
		Category topCat = model.getTopCategory(); 
		
		categoryFilter.addItem( topCat );
		categoryFilter.setItemCaption( topCat, topCat.getName());

		for ( Category cat : model.getCategories()) {
			
			addCategory( cat, categoryFilter, 1 );
			
		}

		categoryFilter.setValue( topCat );
		
		categoryFilter.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange( ValueChangeEvent event ) {
				
//				reReadToolsList();
				model.setSelectedCategory(( Category )categoryFilter.getValue());
				
			}
			
		});
		
		
	}

	private void addCategory( Category cat, ComboBox combo, int level ) {

		String caption = "";
		
		if ( cat != null && combo != null ) {
			
			categoryFilter.addItem( cat );
			
			switch ( level ) {
				case 1:
					caption = "\u2523"; 
					break;
				case 2:
					caption = "\u2503 \u2523"; 
					break;
				case 3:
					caption = "\u2503   \u2523"; 
					break;
				default:
					caption = "\u2503     \u2523"; 
					break;
			}
			caption = caption + cat.getName(); 
 
			categoryFilter.setItemCaption( cat, caption );
			
			if ( cat.getChilds() != null && cat.getChilds().size() > 0 ) {
				
				int newLevel = level + 1;
				
				for ( Category catChild : cat.getChilds()) {
					
					addCategory( catChild, combo, newLevel );
					
				}
			}
			
		}
	}

	class ToolsViewFilter implements Container.Filter {

		private static final long serialVersionUID = 1L;
		private String searchString;
		
		public ToolsViewFilter( String searchString ) {
		
			this.searchString  = searchString; 
		}
		
		@Override
		public boolean passesFilter(Object itemId, Item item)
				throws UnsupportedOperationException {
			
			if ( searchString == null || searchString.length() == 0 ) return true;
						
			ToolItem toolItem;
			try {
				toolItem = ( ToolItem ) item.getItemProperty( "data" ).getValue();
			} catch ( Exception e ) {
				return false;
			}
			
			if ( toolItem == null ) return false;
			
			try {
				Tool tool = toolItem.getTool();
				
				if ( tool.getName() != null &&
					 tool.getName().toLowerCase().indexOf( searchString.toLowerCase()) != -1 ) return true;
				
				if ( tool.getDescription() != null &&
					 tool.getDescription().toLowerCase().indexOf( searchString.toLowerCase()) != -1 ) return true;
				
				if ( toolItem.getBarcode() != null &&
					 toolItem.getBarcode().toLowerCase().indexOf( searchString.toLowerCase()) != -1 ) return true;
				
				if ( toolItem.getSerialNumber() != null &&
					 toolItem.getSerialNumber().toLowerCase().indexOf( searchString.toLowerCase()) != -1 ) return true;
				
				if ( toolItem.getCurrentUser() != null &&
					 toolItem.getCurrentUser().getFirstAndLastNames().toLowerCase().indexOf( searchString.toLowerCase()) != -1 ) return true;
				
			} catch ( Exception e ) {
				return false;
			}
			
			
			return false;
		}

		@Override
		public boolean appliesToProperty( Object propertyId ) {
			
			return ( propertyId != null && propertyId.equals( "data" )); 
			
		}

	}	
	
}
