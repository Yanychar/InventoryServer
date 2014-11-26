package com.c2point.tools.ui.repositoryview;

import java.util.Collection;

import com.c2point.tools.entity.repository.ItemStatus;
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

public class ToolsListComponent extends VerticalLayout implements CategoryModelListener {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger( ToolsListComponent.class.getName());
	
	private ToolsListModel		model; 
	private Table 				table;
	private ItemInfoComponent	infoComp = new ItemInfoComponent();
	
//	private Panel			infoPanel;
	
	private boolean 			editMode;
	
	public ToolsListComponent( ToolsListModel model ) {
		
		this( model, false );
		
	}
	
	public ToolsListComponent( ToolsListModel model, boolean editMode ) {
		super();
		
		initUI();
		
		initModel( model );
		
		setEditMode( editMode );
		
	}
	
	public void setEditMode() {
		
		setEditMode( true );
	}
	
	private void setEditMode( boolean editMode ) {
		
		this.editMode = editMode;
		updateUI();
	}
	
	public void stopEditMode() {
		
		setEditMode( false );
		
	}

	private void initUI() {

		setSizeFull();

		setMargin( true );
		setSpacing( true );

		
		table = new Table();
		initTable();
	
		Panel infoPanel = new Panel();
		infoPanel.addStyleName( "light" );
		
		infoComp = new ItemInfoComponent();
		infoPanel.setContent( infoComp );

//		this.addComponent( getSearchBar());
		this.addComponent( table );
		this.addComponent( infoPanel );
		
//		this.setExpandRatio( table, 1.0f );
//		this.setExpandRatio( infoPanel, 0.20f );
		
	}
	
	private void initTable() {

		// Configure table
		table.setSelectable( true );
		table.setMultiSelect( false );
//		table.setNullSelectionAllowed( false );
		table.setColumnCollapsingAllowed( false );
		table.setColumnReorderingAllowed( false );
//		table.setColumnHeaderMode( Table.ColumnHeaderMode.HIDDEN );
//		table.setSortEnabled( false );
		table.setImmediate( true );
		table.setSizeFull();
		
//		categoriesTree.addContainerProperty( "code",		String.class, 	null );
		table.addContainerProperty( "photo",		Embedded.class, null );
		table.addContainerProperty( "tool", 		Label.class, 	null );
		table.addContainerProperty( "manufacturer", 	String.class, 	"" );
		table.addContainerProperty( "status", 		String.class, 	"" );
		table.addContainerProperty( "user", 		String.class, 	"" );

		
		// New User has been selected. Send event to model
		table.addValueChangeListener( new ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			public void valueChange( ValueChangeEvent event) {
				if ( logger.isDebugEnabled()) logger.debug( "List of Tools selection were changed" );

				model.toolSelected( table.getValue());
				
			}
		});
		
	}
	
	private void updateUI() {
		
	}

	private void initModel( ToolsListModel model ) {
		
		this.model = model; 
		model.addChangedListener( this );		
		model.addChangedListener( infoComp );
		
	}

	
	private void dataFromModel( Category category ) {

		Collection<ToolItem> itemList = 
							model.getItems( category );
		
		
		table.removeAllItems();
		
		if ( itemList != null && itemList.size() > 0 ) {
			for ( ToolItem repItem : itemList ) {
				if ( repItem  != null ) {
					addItem( repItem );
				}
			}
		}
		
		table.setSortContainerPropertyId( "tool" );

		table.sort();
		
	}
	
	private void addItem( ToolItem repItem ) {

		Item item = table.addItem( repItem );

		if ( logger.isDebugEnabled()) logger.debug( "Item will be added. Repository Item id: " + repItem.getId());

		updateItem( item, repItem );
			
	}

	int num = 1;

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
		
		item.getItemProperty( "status" ).setValue( getStatusString( repItem.getStatus()));
		
	
		
	}
	
	
	@Override
	public void wasAdded( Category category ) {}
	@Override
	public void wasChanged(Category category) {}
	@Override
	public void wasDeleted(Category category) {}
	@Override
	public void listWasChanged() {}

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

	private String getStatusString( ItemStatus status ) {
		
		switch ( status ) {
			case RESERVED:
				return "Reserved";
			case BROCKEN:
				return "Brocken";
			case FREE:
				return "Available";
			case REPAIRING:
				return "Under repairing";
			case INUSE:
				return "In use";
			case STOLEN:
				return "Stolen";
			default:
				return "Unknown";
		}
		
	}
	
}
