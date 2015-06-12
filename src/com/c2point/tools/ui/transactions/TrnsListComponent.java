package com.c2point.tools.ui.transactions;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.format.DateTimeFormat;

import com.c2point.tools.entity.transactions.BaseTransaction;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class TrnsListComponent extends VerticalLayout implements TransactionsModelListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( TrnsListComponent.class.getName());
	
	private TransactionsListModel		model;

	private Table						trnsTable;
	
	public TrnsListComponent( TransactionsListModel model ) {
		super();
		
		this.model = model;
		
		initUI();

		model.addChangedListener( this );
	}
	
	private void initUI() {

		setSizeFull();

		setMargin( true );
		
		trnsTable = new Table();
		
//		setContainerForSearch( trnsTable );
		
		// Configure table
		trnsTable.setSelectable( true );
		trnsTable.setNullSelectionAllowed( false );
		trnsTable.setMultiSelect( false );
		trnsTable.setColumnCollapsingAllowed( false );
		trnsTable.setColumnReorderingAllowed( false );
		trnsTable.setImmediate( true );
		trnsTable.setSizeFull();
		
		trnsTable.addContainerProperty( "date", 	String.class, null );
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

Object obj;
obj = trnsTable.getItem( id );
obj = trnsTable.getItem( id ).getItemProperty( "data" );
obj = trnsTable.getItem( id ).getItemProperty( "data" ).getValue();

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

	private void dataFromModel( Collection<BaseTransaction>trnsList ) {

		if ( logger.isDebugEnabled()) logger.debug( "Data from model will be read!" );
		
		// Store selection for recovery at the end of this method
		Long selectedId = ( Long )trnsTable.getValue();
		Long newSelectedId = null;
		boolean selected = ( selectedId != null );
		
		if ( trnsList != null ) {

			// remove old content
			trnsTable.removeAllItems();
			
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

//			if ( logger.isDebugEnabled()) logger.debug( "Tool Item will be added: " + toolItem );
			item = trnsTable.addItem( trn.getId());

		} else {
			if ( logger.isDebugEnabled()) logger.debug( "Transaction exists already. Will be modified: " + trn );
		}

		item.getItemProperty( "date" ).setValue( DateTimeFormat.forPattern("dd.MM.yyyy HH:MM").print( trn.getDate()));
		item.getItemProperty( "content" ).setValue( createTextContent( trn ));
		item.getItemProperty( "user" ).setValue( trn.getUser().getFirstAndLastNames());
		item.getItemProperty( "data" ).setValue( trn );
		
		
	}
	
	private String createTextContent( BaseTransaction trn ) {
		
//		return getTextTrnType( trn ) + getTextTrnOperation( trn );
		
		if ( trn != null ) {

			return trn.getTrnType().toString( model.getApp().getSessionData().getBundle()) 
					+ ". "
					+ trn.getTrnOperation().toString( model.getApp().getSessionData().getBundle());
					
		}
		
		return "???";
		
	}
	
/*	
	private String getTextTrnOperation( BaseTransaction trn ) {
		
		switch ( trn.getTrnOperation()) {
			case ADD:
//				str = "Added by "
//						+ trn.getUser().getFirstAndLastNames();
				str = model.getApp().getResourceStr( "transaction.operation.add" );
				break;
			case DELETE:
//				str = "Deleted by "
//						+ trn.getUser().getFirstAndLastNames();
				str = model.getApp().getResourceStr( "transaction.operation.delete" );
				break;
			case EDIT:
//				str = "Edited by "
//						+ trn.getUser().getFirstAndLastNames();
				str = model.getApp().getResourceStr( "transaction.operation.edit" );
				break;
			case NEWSTATUS:
//				str = "New status: " 
//					+ trn.getNewStatus().toString( model.getApp().getSessionData().getBundle())
//					+ " was given by "
//					+ trn.getUser().getFirstAndLastNames();
				str = model.getApp().getResourceStr( "transaction.operation.newstatus" ) + ": "
						+ trn.getNewStatus().toString( model.getApp().getSessionData().getBundle());
				break;
			case USERCHANGED:
//				str = "Transferred from " 
//					+ trn.getSourceUser().getFirstAndLastNames() 
//					+ " to "
//					+ trn.getDestUser().getFirstAndLastNames();
				str = model.getApp().getResourceStr( "transaction.operation.userchanged" );
				break;
			case OFF:
//				str = "Wrong operation made by " + trn.getUser().getFirstAndLastNames();
				str = model.getApp().getResourceStr( "transaction.operation.wrong" );
				break;
			case ON:
//				str = "Wrong operation made by " + trn.getUser().getFirstAndLastNames();
				str = model.getApp().getResourceStr( "transaction.operation.wrong" );
				break;
			default:
//				str = "Unknown operation made by " + trn.getUser().getFirstAndLastNames();
				str = model.getApp().getResourceStr( "transaction.operation.unknown" );
				break;
		}
		
		return str;
	}
*/	
	@Override
	public void transactionSelected(BaseTransaction user) { }

	@Override
	public void listUpdated(Collection<BaseTransaction> list) {
		
		dataFromModel( list );
		
		applyFilter();
	}

	private void applyFilter() {

		if ( trnsTable.getContainerDataSource() != null ) {
			
			(( Filterable )trnsTable.getContainerDataSource()).removeAllContainerFilters();
		
			Filter filter = model.getFilter();
				
			(( Filterable )trnsTable.getContainerDataSource()).addContainerFilter( filter );
				
			
			if ( trnsTable != null && trnsTable.getContainerDataSource() instanceof Container.Ordered ) {
				
				trnsTable.setValue( 
						trnsTable.getContainerDataSource().size() > 0 ? trnsTable.firstItemId() : null 
				);
			}
			
		}
		
	}

	
}
