package com.c2point.tools.ui.tools.history;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Tool;
import com.c2point.tools.entity.transactions.BaseTransaction;
import com.c2point.tools.ui.ListWithSearchComponent;
import com.c2point.tools.ui.tools.history.ToolsHistoryListModel.ViewMode;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Table;

public class UsersListComponent extends ListWithSearchComponent implements ToolsHistoryModelListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( UsersListComponent.class.getName());
	
	private ToolsHistoryListModel		model;

	private Table						usersTable;
	
	public UsersListComponent( ToolsHistoryListModel model ) {
		super();
		
		this.model = model;
		
		initUI();
		
		model.addChangedListener( this );
	}

	private static String [] searchFields = {
		"name"
	};
	
	protected String [] getFieldsForSearch() {
		
		return searchFields;
		
	}
	
	
	private void initUI() {

		setSizeFull();

		setMargin( true );
		
		usersTable = new Table();
		
		setContainerForSearch( usersTable );
		
		// Configure table
		usersTable.setSelectable( true );
		usersTable.setNullSelectionAllowed( false );
		usersTable.setMultiSelect( false );
		usersTable.setColumnCollapsingAllowed( false );
		usersTable.setColumnReorderingAllowed( false );
		usersTable.setImmediate( true );
		usersTable.setSizeFull();
		
		usersTable.addContainerProperty( "name", 	String.class, null );
		usersTable.addContainerProperty( "data", 	ToolItem.class, null );

		usersTable.setVisibleColumns( new Object [] { "name" } );
		
		usersTable.setColumnHeaders( new String[] { 
				model.getApp().getResourceStr( "repositorymgmt.list.header.user" ),
		
		});

		// New User has been selected. Send event to model
		usersTable.addValueChangeListener( new  ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			public void valueChange( ValueChangeEvent event) {
				
				if ( logger.isDebugEnabled()) logger.debug( "Property.valueChanged!" );
				
				try {

					model.setSelectedToolItem( 
							( ToolItem )usersTable.getItem( usersTable.getValue()).getItemProperty( "data" ).getValue()
					);
					
					
				} catch ( Exception e ) {
					logger.debug( "No selection. OrgUser cannot be fetched from usersTable" );
				}
			}
		});

		this.addComponent( getSearchBar());
		this.addComponent( usersTable );
		
		this.setExpandRatio( usersTable, 1.0f );

//		dataFromModel();
		
	}

	@Override
	public void modelWasRead() {

		if ( model.getViewMode() == ViewMode.TOOLS ) {
			if ( logger.isDebugEnabled()) logger.debug( "modelWasRead events received. UsersList will be updated!" );
			
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
		Long selectedId = ( Long )usersTable.getValue();
		Long newSelectedId = null;
		boolean selected = ( selectedId != null );
		
		// remove old content
		usersTable.removeAllItems();

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
		
		usersTable.setSortContainerPropertyId( "name" );

		usersTable.sort();
		
		if ( newSelectedId != null ) {
			usersTable.setValue( newSelectedId );
		} else {
			usersTable.setValue( usersTable.firstItemId());
		}
		
	}

	@SuppressWarnings("unchecked")
	private void addOrUpdateItem( ToolItem toolItem ) {
		
		Item item = usersTable.getItem( toolItem.getId());
		
		if ( item == null ) {

//			if ( logger.isDebugEnabled()) logger.debug( "Tool Item will be added: " + toolItem );
			item = usersTable.addItem( toolItem.getId());

		}

		item.getItemProperty( "name" ).setValue( toolItem.getCurrentUser().getLastAndFirstNames());
		item.getItemProperty( "data" ).setValue( toolItem );
		
	}
	
	
	
}
