package com.c2point.tools.ui.repositoryview;

import java.util.Collection;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Category;
import com.c2point.tools.ui.category.CategoryModelListener;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ToolsListView extends VerticalLayout implements CategoryModelListener, ToolsModelListener {
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
		model.addChangedListener(( ToolsModelListener ) this );		
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
		itemsTable.addContainerProperty( "manufacturer", 	String.class, 	"" );
		itemsTable.addContainerProperty( "status", 		String.class, 	"" );
		itemsTable.addContainerProperty( "user", 		String.class, 	"" );

		
		// New User has been selected. Send event to model
		itemsTable.addValueChangeListener( new ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			public void valueChange( ValueChangeEvent event) {
				if ( logger.isDebugEnabled()) logger.debug( "List of Tools selection were changed" );

				model.toolSelected( itemsTable.getValue());
				
			}
		});
		
	}
	
	private FilterToolbar getFilterToolBar() {
		
		if ( toolBarLayout == null ) {
			toolBarLayout = new FilterToolbar( model );
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
					addItem( repItem );
				}
			}
		}
		
		itemsTable.setSortContainerPropertyId( "tool" );

		itemsTable.sort();
		
	}
	
	private void addItem( ToolItem repItem ) {

		Item item = itemsTable.addItem( repItem );

		if ( logger.isDebugEnabled()) logger.debug( "Item will be added. Repository Item id: " + repItem.getId());

		updateItem( item, repItem );
			
	}

	int num = 1;

	@SuppressWarnings("unchecked")
	private void updateItem( Item item, ToolItem repItem ) {
		
		// Item Photo column 
		item.getItemProperty( "photo" ).setValue( getItemPhoto( repItem ));
		
		// Item Name column 
		Label toolLabel = ( Label )item.getItemProperty( "tool" ).getValue();
		if ( toolLabel == null ) {
			toolLabel = new Label( "", ContentMode.HTML );
		}

		String nameStr;
		try {
			nameStr = "<b>" + repItem.getTool().getName() + "</b>"
					+ "<br>"
					+ ( repItem.getTool().getDescription() != null ? repItem.getTool().getDescription() : "" );
			
			toolLabel.setValue( nameStr);
		} catch ( Exception e ) {
			logger.error( "  Tool name are missing for Repository Item" );
			toolLabel.setValue( "Noname" );
		}
		item.getItemProperty( "tool" ).setValue( toolLabel );
		
		try {
			item.getItemProperty( "manufacturer" ).setValue( repItem.getTool().getManufacturer().getName());
		} catch ( Exception e ) {
			item.getItemProperty( "manufacturer" ).setValue( "" );
		}
		
		try {
			item.getItemProperty( "user" ).setValue( repItem.getCurrentUser().getLastAndFirstNames());
		} catch ( Exception e ) {
			item.getItemProperty( "user" ).setValue( "No user" );
		}
		
		item.getItemProperty( "status" ).setValue( repItem.getStatus().toString( model.getApp().getSessionData().getBundle()));
		
	
		
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
	public void wasChanged( ToolItem repItem ) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void selected(ToolItem repItem) {
		// TODO Auto-generated method stub
		
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

}
