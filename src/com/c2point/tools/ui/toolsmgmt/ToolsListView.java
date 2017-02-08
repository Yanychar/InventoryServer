package com.c2point.tools.ui.toolsmgmt;

import java.util.Arrays;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Category;
import com.c2point.tools.entity.tool.Tool;
import com.c2point.tools.ui.AbstractModel.EditModeType;
import com.c2point.tools.ui.listeners.ToolItemChangedListener;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
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
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

public class ToolsListView extends VerticalLayout implements ToolItemChangedListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( ToolsListView.class.getName());

	private static int BUTTON_WIDTH = 25;
	
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

		model.addListener( this );
		
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
		itemsTable.addContainerProperty( "buttons", HorizontalLayout.class, null );
		itemsTable.addContainerProperty( "data", 		ToolItem.class, null );

		itemsTable.setVisibleColumns( new Object [] { "category", "name", "status", "user", "buttons" } );
		
		itemsTable.setColumnHeaders( new String[] { 
				model.getApp().getResourceStr( "toolsmgmt.list.header.category" ),
				model.getApp().getResourceStr( "toolsmgmt.list.header.tool" ),
				model.getApp().getResourceStr( "toolsmgmt.list.header.status" ),
				model.getApp().getResourceStr( "toolsmgmt.list.header.user" ),
				""
		});

		itemsTable.setColumnWidth( "buttons", BUTTON_WIDTH * 3 );
		
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
		
		itemsTable.addItemClickListener( new ItemClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void itemClick(ItemClickEvent event) {
				
				if ( toProcess( event ) && event.getItem().getItemProperty( "data" ).getValue() instanceof ToolItem ) {
					if ( logger.isDebugEnabled()) logger.debug( "Click shall be processed! Table item clicked: " + event.getItem().getItemProperty( "name" ).getValue());
					
					handleActions(( ToolItem )event.getItem().getItemProperty( "data" ).getValue());
					
				} else {
					if ( logger.isDebugEnabled()) logger.debug( "Click done. No processing" );
				}
				
			}
			
			
			
		});
		
		this.addComponent( getToolbar());
		this.addComponent( itemsTable );
		
		this.setExpandRatio( itemsTable, 1.0f );
	}

	
	private Object clickedId = null;
	private int clickedCounter = 0;
	
	private void endClickHandlingProcess() {
		clickedCounter = 0;
	}
	private boolean toProcess( ItemClickEvent event ) {

		boolean bRes = false;
		
		if ( event.isDoubleClick()) {
			// We pass doubleclick completely!
			return bRes;
		}
		if ( clickedId != event.getItemId()) {

			// New item clicked. Select it but do nothing
			clickedCounter = 1;
			clickedId = event.getItemId();
			
		} else {
			// Item has been selected already

			if ( clickedCounter < 1 ) {
				clickedCounter = 1;
			}
			
			if ( clickedCounter == 1 ) {
				// Second click opens dialog or starts other actions
				bRes = true;
				
			} else {
				// This is more than 2nd click. Do nothing 
			}
			clickedCounter++;
			
		}
		
		return bRes;
	}
	private void handleActions( ToolItem item ) {

		editTool( EditModeType.EDIT );
		
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

		initCategoryFilter();
		
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
		
		addButton.setEnabled( model.allowsToEdit());
		
	}
	
	@SuppressWarnings("unchecked")
	private void addOrUpdateItem( ToolItem toolItem ) {
		
		Item item = itemsTable.getItem( toolItem.getId());
		
		if ( item == null ) {

			if ( logger.isDebugEnabled()) logger.debug( "Tool Item will be added: " + toolItem );
			item = itemsTable.addItem( toolItem.getId());

	        item.getItemProperty( "buttons" ).setValue( getButtonSet( item ));
			
		} else {
			if ( logger.isDebugEnabled()) logger.debug( "Tool Item exists already. Will be modified: " + toolItem );
		}

		item.getItemProperty( "category" ).setValue( getCategoryChain( toolItem.getTool().getCategory()));
		item.getItemProperty( "name" ).setValue( toolItem.getTool().getFullName());
		item.getItemProperty( "status" ).setValue( toolItem.getStatus().toString( model.getApp().getSessionData().getBundle()));
		try {
			item.getItemProperty( "user" ).setValue( toolItem.getCurrentUser().getLastAndFirstNames());
		} catch ( Exception e ) {
			item.getItemProperty( "user" ).setValue( "No user" );
		}
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
			categoryFilter.setInputPrompt( this.model.getApp().getResourceStr( "toolsmgmt.text.search" ));
			categoryFilter.setInvalidAllowed( false );
			categoryFilter.setImmediate(true);
			
//			initCategoryFilter();

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
			
			
			addButton = new Button( model.getApp().getResourceStr( "personnel.caption.add" ));
			addButton.addClickListener( new ClickListener() {
				private static final long serialVersionUID = 1L;
				@Override
				public void buttonClick( ClickEvent event) {
					addButtonPressed();
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

		deleteCatFilterChangedListener();
		
		categoryFilter.removeAllItems();
		
		// Add Category ALL
		Category topCat = model.getTopCategory(); 
		
		categoryFilter.addItem( topCat );
		categoryFilter.setItemCaption( topCat, topCat.getName());

		for ( Category cat : model.getCategories()) {
			
			addCategory( cat, categoryFilter, 1 );
			
		}

		categoryFilter.setValue( topCat );
		
		addCatFilterChangedListener();		
		
	}
	
	private ValueChangeListener catFilterChangedListener = null;
	private ValueChangeListener addCatFilterChangedListener() {
		
		if ( catFilterChangedListener == null ) {

			catFilterChangedListener = new ValueChangeListener() {
				private static final long serialVersionUID = 1L;

				@Override
				public void valueChange( ValueChangeEvent event ) {
					
					model.setSelectedCategory(( Category )categoryFilter.getValue());
					
				}
				
			};

			categoryFilter.addValueChangeListener( catFilterChangedListener );
			
		}
		
		return catFilterChangedListener;

	}
	private void deleteCatFilterChangedListener() {

		if ( catFilterChangedListener != null ) {
			categoryFilter.removeValueChangeListener( catFilterChangedListener );
			catFilterChangedListener = null;
		}
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

    private Component getButtonSet( Item item ) {
	
        HorizontalLayout buttonsSet = new HorizontalLayout();

        buttonsSet.setSpacing( true );

		if ( model.allowsToEdit()) {
        
			final NativeButton copyButton = 	createButton( "icons/16/copy.png", "toolsmgmt.copy.tooltip", item ); 
			final NativeButton editButton = 	createButton( "icons/16/edit.png", "toolsmgmt.edit.tooltip", item );
			final NativeButton deleteButton = createButton( "icons/16/delete.png", "toolsmgmt.delete.tooltip", item );
	        
	        copyButton.addClickListener( new ClickListener() {
				private static final long serialVersionUID = 1L;
				@Override
				public void buttonClick( ClickEvent event ) {
					// Button data is Item. Item's data property is ToolItem
					copyButtonPressed( (ToolItem) ((Item) copyButton.getData()).getItemProperty( "data" ).getValue());
				}
	        });
	        editButton.addClickListener( new ClickListener() {
				private static final long serialVersionUID = 1L;
				@Override
				public void buttonClick( ClickEvent event ) {
					// Button data is Item. Item's data property is ToolItem
					editButtonPressed( (ToolItem) ((Item) editButton.getData()).getItemProperty( "data" ).getValue());
				}
	        });
	        deleteButton.addClickListener( new ClickListener() {
				private static final long serialVersionUID = 1L;
				@Override
				public void buttonClick( ClickEvent event ) {
					// Button data is Item. Item's data property is ToolItem
					deleteButtonPressed( (ToolItem) ((Item) deleteButton.getData()).getItemProperty( "data" ).getValue());
				}
	        });
	        
	        buttonsSet.addComponent( copyButton );
	        buttonsSet.addComponent( editButton );
	        buttonsSet.addComponent( deleteButton );
		}
    	
    	return buttonsSet;
    }

	private NativeButton createButton( String iconPath, String tooltipKey, Item item ) {
	
		NativeButton button = new NativeButton(); 
		
		button.setIcon( new ThemeResource( iconPath ));
		button.setDescription( model.getApp().getResourceStr( tooltipKey ));

		button.setHeight( Integer.toString( BUTTON_WIDTH ) + "px" );
//		button.setStyleName( "v-nativebutton-deleteButton" );
//		button.addStyleName( "v-nativebutton-link" );
		button.setStyleName( BaseTheme.BUTTON_LINK );

		button.setData( item );
		button.setImmediate( true );
		
		return button;
		
	}
	
	class ToolsViewFilter implements Container.Filter {

		private static final long serialVersionUID = 1L;

		private Collection<String>	searchStringArray;
		
		public ToolsViewFilter( String searchString ) {
		
			setSearchString( searchString );
		}
		
		public void setSearchString( String searchString ) {

			if ( searchString != null && searchString.trim().length() > 0 ) {
				
					searchStringArray = Arrays.asList( searchString.trim().split( " " ));
					
			} else {
				this.searchStringArray = null;
			}
		}

		
		@Override
		public boolean passesFilter(Object itemId, Item item)
				throws UnsupportedOperationException {
			
			boolean bRes = true;

			ToolItem toolItem;
			try {
				toolItem = ( ToolItem ) item.getItemProperty( "data" ).getValue();
			} catch ( Exception e ) {
				return false;
			}
			
			if ( toolItem == null ) return false;
			
			if ( this.searchStringArray != null && this.searchStringArray.size() > 0 ) {

				Tool tool = toolItem.getTool();
				
				for ( String searchString : this.searchStringArray ) {
							
					
					try {

						bRes = bRes
								&& 
							  ( checkToolName( tool, searchString ) || checkToolItem( toolItem, searchString ));
								
					} catch ( Exception e ) {
					}
					
					if ( !bRes )
						break;
					
				}
			}
			
			return bRes;
		}

		@Override
		public boolean appliesToProperty( Object propertyId ) {
			
			return ( propertyId != null && propertyId.equals( "data" )); 
			
		}

		private boolean checkToolName( Tool tool, String searchString ) {
			
			if ( tool == null ) return false;

			try {
				if ( tool.getName() != null &&
					 tool.getName().toLowerCase().indexOf( searchString.toLowerCase()) != -1 ) return true;
				
				if ( tool.getToolInfo() != null &&
					 tool.getToolInfo().toLowerCase().indexOf( searchString.toLowerCase()) != -1 ) return true;

				if ( tool.getManufacturer() != null && tool.getManufacturer().getName() != null 
						&& tool.getManufacturer().getName().toLowerCase().indexOf( searchString.toLowerCase()) != -1 ) return true;
					
				if ( tool.getModel() != null &&
						 tool.getModel().toLowerCase().indexOf( searchString.toLowerCase()) != -1 ) return true;

			} catch ( Exception e ) {
			}
			
			return false;
		}
		
		private boolean checkToolItem( ToolItem toolItem, String searchString ) {
			
			if ( toolItem == null ) return false;
			
			try {
				
				if ( toolItem.getBarcode() != null &&
					 toolItem.getBarcode().toLowerCase().indexOf( searchString.toLowerCase()) != -1 ) return true;
				
				if ( toolItem.getSerialNumber() != null &&
					 toolItem.getSerialNumber().toLowerCase().indexOf( searchString.toLowerCase()) != -1 ) return true;

				if ( toolItem.getCurrentUser() != null &&
						 toolItem.getCurrentUser().getFirstAndLastNames().toLowerCase().indexOf( searchString.toLowerCase()) != -1 ) return true;
				
			} catch ( Exception e ) {
			}
			
			return false;
		}
		
	}	

	private void editButtonPressed( ToolItem item ) {
		logger.debug( "Edit button was pressed to add new Tool/ToolItem. Edit Tool: " + item.getTool().getName() );

		itemsTable.setValue( item.getId());
		
		editTool( EditModeType.EDIT );
	}
	
	private void editTool( EditModeType editMode) {

		ToolItemEditDlg_2 editDlg = new ToolItemEditDlg_2( model, editMode ) {
			private static final long serialVersionUID = 1L;

			@Override
			public void dlgClosed() {

				logger.debug( "ToolsEditDlg Dialog has been closed!" );
				endClickHandlingProcess();
				
			}
		};

		UI.getCurrent().addWindow( editDlg );
		
	}
	
	private void addButtonPressed() {
		
		logger.debug( "Add button was pressed to add new Tool/ToolItem" );
		
		Tool	 newTool = new Tool( model.getSelectedOrg());
		//	Set code, category, org
		newTool.setCategory( model.getSelectedCategory());

		ToolItem newItem = new ToolItem( newTool, model.getSessionOwner(), model.getSessionOwner());
		// Set tool, user as session owner, status, personal flag
		// Done in constructor
		
		model.setSelectedItem( newItem );
		
		editTool( EditModeType.ADD );
			
	}
	
	private void copyButtonPressed( ToolItem item ) {
		logger.debug( "Copybutton was pressed to add new Tool/ToolItem. Copy Tool: " + item.getTool().getName() );

		itemsTable.setValue( item.getId());

		ToolItem newItem = new ToolItem( item.getTool(), model.getSessionOwner(), model.getSessionOwner());
		// Set tool, user as session owner, status, personal flag
		// Done in constructor
		newItem.setPersonalFlag( item.isPersonalFlag());
		newItem.setPrice( item.getPrice());
		newItem.setTakuu( item.getTakuu());
		
		model.setSelectedItem( newItem );
		
		editTool( EditModeType.COPY );
		
	}
	
	private void deleteButtonPressed( final ToolItem item ) {
		logger.debug( "Deletebutton was pressed to add new Tool/ToolItem. Delete Tool/ToolItem: " + item.getTool().getName() );

		itemsTable.setValue( item.getId());
		
		model.initiateDelete();
		
	}


	
}
