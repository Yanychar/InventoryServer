package com.c2point.tools.ui.transactions;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.format.DateTimeFormat;

import com.c2point.tools.entity.transactions.BaseTransaction;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Container.Filterable;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.BaseTheme;

public class TrnsListComponent extends VerticalLayout implements TransactionsModelListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( TrnsListComponent.class.getName());

	protected HorizontalLayout			toolBarLayout;
	private TextField					searchText;
	
	private TransactionsListModel		model;
	private Table						trnsTable;
	
	public TrnsListComponent( TransactionsListModel model ) {
		super();
		
		this.model = model;
		
		initView();

		model.addChangedListener( this );
	}
	
	private void initView() {

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

		this.addComponent( getToolbar());
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
		item.getItemProperty( "content" ).setValue( trn.toTableItem( model.getApp().getSessionData().getBundle()));
		item.getItemProperty( "user" ).setValue( trn.getUser().getFirstAndLastNames());
		item.getItemProperty( "data" ).setValue( trn );
		
		
	}

	protected Component getToolbar() {
		
		// Add search field
		if ( toolBarLayout == null ) {

			toolBarLayout = new HorizontalLayout();
			
			toolBarLayout.setWidth( "100%");
			toolBarLayout.setMargin( new MarginInfo( false, true, false, true ));
	
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
						applyFilter( null );
						
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
					
					applyFilter( event.getText());
					
				}
				
			});
			
			
			toolBarLayout.addComponent( searchIcon );
			toolBarLayout.addComponent( searchText );
			toolBarLayout.addComponent( deleteIcon );
			Label glue = new Label( "" );
			toolBarLayout.addComponent( glue );
			toolBarLayout.setExpandRatio( glue,  1.0f );

		}
		
		return toolBarLayout;
	}

	@Override
	public void transactionSelected(BaseTransaction user) { }

	@Override
	public void listUpdated(Collection<BaseTransaction> list) {
		
		dataFromModel( list );
		
		applyFilter( searchText.getValue() );
	}

//	private void applyFilter() { applyFilter( null ); }
	
	private void applyFilter( String searchString ) { 

		if ( trnsTable.getContainerDataSource() != null ) {
			
			(( Filterable )trnsTable.getContainerDataSource()).removeAllContainerFilters();
		
			TrnsFilter filter = model.getFilter();
			filter.setSearchString( searchString );
				
			(( Filterable )trnsTable.getContainerDataSource()).addContainerFilter( filter );
				
			
			if ( trnsTable != null && trnsTable.getContainerDataSource() instanceof Container.Ordered ) {
				
				trnsTable.setValue( 
						trnsTable.getContainerDataSource().size() > 0 ? trnsTable.firstItemId() : null 
				);
			}
			
		}
		
	}

	
}
