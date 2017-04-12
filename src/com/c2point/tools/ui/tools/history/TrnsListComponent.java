package com.c2point.tools.ui.tools.history;

import java.util.Collection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Tool;
import com.c2point.tools.entity.transactions.BaseTransaction;
import com.c2point.tools.ui.tools.history.ToolsHistoryListModel.ViewMode;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class TrnsListComponent extends VerticalLayout implements ToolsHistoryModelListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( TrnsListComponent.class.getName());
	
	private ToolsHistoryListModel		model;

	private Table						trnsTable;
	
	public TrnsListComponent( ToolsHistoryListModel model ) {
		super();
		
		this.model = model;
		
		initUI();

		model.addChangedListener( this );
	}
	
	private void initUI() {

		setSizeFull();

		setMargin( true );
		
		trnsTable = new Table() {
			private static final long serialVersionUID = 1L;

			DateTimeFormatter fmt = DateTimeFormat.forPattern( "dd.MM.yyyy" );
						
			@Override
			protected String formatPropertyValue(
								Object rowId,
								Object colId, 
								Property<?> property ) { 
				
				// Format by property type
				if ( property.getType() == DateTime.class ) {

					DateTime dd = ( DateTime )property.getValue();
					return ( dd != null ? fmt.print( dd ) : "" );
				}	
				
				return super.formatPropertyValue(rowId, colId, property);
							
			}
		};
		
//		setContainerForSearch( trnsTable );
		
		// Configure table
		trnsTable.setSelectable( true );
		trnsTable.setNullSelectionAllowed( false );
		trnsTable.setMultiSelect( false );
		trnsTable.setColumnCollapsingAllowed( false );
		trnsTable.setColumnReorderingAllowed( false );
		trnsTable.setImmediate( true );
		trnsTable.setSizeFull();
		
		trnsTable.addContainerProperty( "date", 	DateTime.class, null );
		trnsTable.addContainerProperty( "content", 	String.class, null );
		trnsTable.addContainerProperty( "user", 	String.class, null );
		trnsTable.addContainerProperty( "data", 	BaseTransaction.class, null );

		trnsTable.setVisibleColumns( new Object [] { "date", "content", "user" } );
		
		trnsTable.setColumnHeaders( new String[] { 
				model.getApp().getResourceStr( "trnsmgmt.list.header.date" ),
				model.getApp().getResourceStr( "trnsmgmt.list.header.content" ),
				model.getApp().getResourceStr( "trnsmgmt.list.header.user" ),
		});
		
		trnsTable.setColumnExpandRatio( "date",	   -1 );
		trnsTable.setColumnExpandRatio( "content",	2 );
		trnsTable.setColumnExpandRatio( "user",		1 );

	
		// New User has been selected. Send event to model
		trnsTable.addValueChangeListener( new  ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			public void valueChange( ValueChangeEvent event) {
				
				if ( logger.isDebugEnabled()) logger.debug( "Transaction has been selected!" );
				
				try {
					
					Object id = trnsTable.getValue();
					
					if ( id != null && trnsTable.getItem( id ) != null ) {
						if ( logger.isDebugEnabled()) logger.debug( "Transaction has been selected and found!" );

						model.selectTransaction(  
								( BaseTransaction ) trnsTable.getItem( id ).getItemProperty( "data" ).getValue());
						
					} else {
						if ( logger.isDebugEnabled()) logger.debug( "Transaction == null. Id = " + id );
						
					}

					
					
				} catch ( Exception e ) {
					logger.debug( "No selection. OrgUser cannot be fetched from StuffList " );
				}
			}
		});

		this.addComponent( trnsTable );
		
		this.setExpandRatio( trnsTable, 1.0f );

		
	}

	private void dataFromModel( ToolItem item ) {

		if ( logger.isDebugEnabled()) logger.debug( "Data from model will be read!" );
		
		// Store selection for recovery at the end of this method
		Long selectedId = ( Long )trnsTable.getValue();
		Long newSelectedId = null;
		boolean selected = ( selectedId != null );
		
		// remove old content
		trnsTable.removeAllItems();

		Collection<BaseTransaction> trnsList = model.getTransactions( item );
		
		if ( trnsList != null ) {
			for ( BaseTransaction trn : trnsList ) {
				if ( trn != null ) {
					addOrUpdateItem( trn );
					
					// Check that selection can be restored
					if ( selected && trn.getId() == selectedId ) {
						newSelectedId = trn.getId();
						selected = false;
					}
				}
			}
		}
		
		trnsTable.setSortContainerPropertyId( "name" );

		trnsTable.sort();
		
		if ( newSelectedId != null ) {
			trnsTable.setValue( newSelectedId );
		} else {
			trnsTable.setValue( trnsTable.firstItemId());
		}
		
	}

	@SuppressWarnings("unchecked")
	private void addOrUpdateItem( BaseTransaction trn ) {
		
		Item item = trnsTable.getItem( trn.getId());
		
		if ( item == null ) {

			item = trnsTable.addItem( trn.getId());

		} else {
			if ( logger.isDebugEnabled()) logger.debug( "Transaction exists already. Will be modified: " + trn );
		}

		item.getItemProperty( "date" ).setValue( trn.getDate());
		item.getItemProperty( "content" ).setValue( trn.toTableItem( model.getApp().getSessionData().getBundle()));
		item.getItemProperty( "user" ).setValue( trn.getUser().getFirstAndLastNames());
		item.getItemProperty( "data" ).setValue( trn );
		
		
	}
	
	@Override
	public void toolItemSelected( ToolItem toolItem ) {

		if ( logger.isDebugEnabled()) logger.debug( "toolsSelected events received. ToolsList will be updated!" );
		
		if ( toolItem != null ) {
			dataFromModel( toolItem );
			
			trnsTable.setSortContainerPropertyId( "date" );
			trnsTable.setSortAscending( false );
			trnsTable.sort();
			
		} else {
			trnsTable.removeAllItems();
		}
		
	}

	@Override
	public void viewTypeChanged(ViewMode mode) { }
	@Override
	public void modelWasRead() { }
	@Override
	public void userSelected(OrgUser user) { }
	@Override
	public void transactionSelected(BaseTransaction user) { }
	@Override
	public void toolSelected(Tool tool) { }

}
