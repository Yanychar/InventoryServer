package com.c2point.tools.ui.tools.history;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Tool;
import com.c2point.tools.entity.transactions.BaseTransaction;
import com.c2point.tools.ui.tools.history.ToolsHistoryListModel.ViewMode;
import com.c2point.tools.ui.util.ListWithSearchComponent;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Table;

public class ToolsListComponent extends ListWithSearchComponent implements ToolsHistoryModelListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( ToolsListComponent.class.getName());
	
	private ToolsHistoryListModel		model;

	private Table						toolsTable;
	
	public ToolsListComponent( ToolsHistoryListModel model ) {
		super();
		
		this.model = model;
		
		initUI();
		
		model.addChangedListener( this );
	}

	private String [] searchFields = null;
	protected String [] getFieldsForSearch() {
		
		if ( searchFields == null ) { 
			searchFields = new String [] { "name", "status" };
		}
		
		return searchFields;
	}
	
	private void initUI() {

		setSizeFull();

		setMargin( true );
		
		toolsTable = new Table();
		
		setContainerForSearch( toolsTable );
		
		// Configure table
		toolsTable.setSelectable( true );
		toolsTable.setNullSelectionAllowed( false );
		toolsTable.setMultiSelect( false );
		toolsTable.setColumnCollapsingAllowed( false );
		toolsTable.setColumnReorderingAllowed( false );
		toolsTable.setImmediate( true );
		toolsTable.setSizeFull();
		
		toolsTable.addContainerProperty( "name", 	String.class, null );
		toolsTable.addContainerProperty( "status", 	String.class, null );
		toolsTable.addContainerProperty( "data", 	ToolItem.class, null );

		toolsTable.setVisibleColumns( new Object [] { "name", "status" } );
		
		toolsTable.setColumnHeaders( new String[] { 
				model.getApp().getResourceStr( "toolsmgmt.list.header.tool" ),
				model.getApp().getResourceStr( "toolsmgmt.list.header.status" ),
		
		});

		// New User has been selected. Send event to model
		toolsTable.addValueChangeListener( new  ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			public void valueChange( ValueChangeEvent event) {
				
				if ( logger.isDebugEnabled()) logger.debug( "Property.valueChanged!" );
				
				try {

					model.setSelectedToolItem( 
							(ToolItem) toolsTable.getItem( toolsTable.getValue()).getItemProperty( "data" ).getValue()
					);
					
					
				} catch ( Exception e ) {
					logger.debug( "No selection. ToolItem cannot be fetched from toolsTable" );
				}
			}
		});

		this.addComponent( getSearchBar());
		this.addComponent( toolsTable );
		
		this.setExpandRatio( toolsTable, 1.0f );

//		dataFromModel();
		
	}

	@Override
	public void modelWasRead() {

		if ( model.getViewMode() == ViewMode.PERSONNEL ) {
			if ( logger.isDebugEnabled()) logger.debug( "modelWasRead events received. ToolsList will be updated!" );
			
			dataFromModel();
		}
	}

	@Override
	public void viewTypeChanged(ViewMode mode) { }
	@Override
	public void toolSelected( Tool tool ) { }
	@Override
	public void toolItemSelected( ToolItem toolItem ) { }
	@Override
	public void userSelected(OrgUser user) { }
	@Override
	public void transactionSelected(BaseTransaction user) { }

	private void dataFromModel() {

		if ( logger.isDebugEnabled()) logger.debug( "Data from model will be read!" );
		
		// Store selection for recovery at the end of this method
		Long selectedId = ( Long )toolsTable.getValue();
		Long newSelectedId = null;
		boolean selected = ( selectedId != null );
		
		// remove old content
		toolsTable.removeAllItems();

		Collection<ToolItem> itemsList = model.getPreparedToolItems();
		
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
		
		toolsTable.setSortContainerPropertyId( "name" );

		toolsTable.sort();
		
		if ( newSelectedId != null ) {
			toolsTable.setValue( newSelectedId );
		} else {
			toolsTable.setValue( toolsTable.firstItemId());
		}
		
	}

	@SuppressWarnings("unchecked")
	private void addOrUpdateItem( ToolItem toolItem ) {
		
		Item item = toolsTable.getItem( toolItem.getId());
		
		if ( item == null ) {

//			if ( logger.isDebugEnabled()) logger.debug( "Tool Item will be added: " + toolItem );
			item = toolsTable.addItem( toolItem.getId());

		}
		
		item.getItemProperty( "name" ).setValue( toolItem.getTool().getFullName());
		item.getItemProperty( "status" ).setValue( toolItem.getStatus().toString( model.getApp().getSessionData().getBundle()));
		item.getItemProperty( "data" ).setValue( toolItem );
		
		
	}
	
	
	
}
