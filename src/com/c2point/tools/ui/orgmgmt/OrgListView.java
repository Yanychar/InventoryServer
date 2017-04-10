package com.c2point.tools.ui.orgmgmt;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.ui.listeners.OrgChangedListener;
import com.c2point.tools.ui.util.ListWithSearchComponent;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;

public class OrgListView extends ListWithSearchComponent implements OrgChangedListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( OrgListView.class.getName());

	private OrgListModel	model;

	private Table			orgsTable;
	
	public OrgListView( OrgListModel model ) {
		super( true );
		this.model = model;

		initView();

		model.addChangedListener( this );
		
	}

	private void initView() {

		setSizeFull();

		setMargin( true );
//		setSpacing( true );

		orgsTable = new Table();
		
		setContainerForSearch( orgsTable );

		// Configure table
		orgsTable.setSelectable( true );
		orgsTable.setNullSelectionAllowed( false );
		orgsTable.setMultiSelect( false );
		orgsTable.setColumnCollapsingAllowed( false );
		orgsTable.setColumnReorderingAllowed( false );
		orgsTable.setImmediate( true );
		orgsTable.setSizeFull();
		
		orgsTable.addContainerProperty( "code", String.class, null );
		orgsTable.addContainerProperty( "name", String.class, null );
		orgsTable.addContainerProperty( "data", Organisation.class, null );

		orgsTable.setVisibleColumns( new Object [] { "code", "name" } );
		
		orgsTable.setColumnHeaders( new String[] { 
				model.getApp().getResourceStr( "general.table.header.code" ), 
				model.getApp().getResourceStr( "general.table.header.organisation" ), 
		
		});

	
		// New User has been selected. Send event to model
		orgsTable.addValueChangeListener( new  ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			public void valueChange( ValueChangeEvent event) {
				if ( logger.isDebugEnabled()) logger.debug( "Property.valueChanged!" );
				
				try {
					if ( logger.isDebugEnabled()) {
						
						logger.debug( "Table item selected. Item Id = " + orgsTable.getValue());
						logger.debug( "  Item = " + orgsTable.getItem( orgsTable.getValue()));

						if ( orgsTable.getItem( orgsTable.getValue()) != null )
							logger.debug( "  Organisation has been selected: " + ( Organisation ) orgsTable.getItem( orgsTable.getValue()).getItemProperty( "data" ).getValue());
						
					}
					
					Item item = orgsTable.getItem( orgsTable.getValue());
					model.setSelectedOrg(( Organisation ) item.getItemProperty( "data" ).getValue());
					
					
				} catch ( Exception e ) {
					logger.debug( "No selection. Organisation cannot be fetched from OrgList " );
					model.setSelectedOrg( null );
				}
			}
		});

		this.addComponent( getSearchBar());
		this.addComponent( orgsTable );
		this.addComponent( getStatusbar());
		
		this.setExpandRatio( orgsTable, 1.0f );
		
	}

	@Override
	public void wasAdded( Organisation org ) {

		logger.debug( "OrganisationList receives notification: Organisation was Added!" );
		
		// Find correct Item. Start from selected one
		// update row with data 
		addOrUpdateItem( org );
		
		// set correct selection
		orgsTable.setValue( org.getId());

		updateCounter();
		
	}

	@Override
	public void wasChanged( Organisation org ) {
		
		logger.debug( "OrganisationList receives notification: Organisation was Changed!" );
		
		// Find correct Item. Start from selected one
		// update row with data 
		addOrUpdateItem( org );
		
		// set correct selection
		orgsTable.setValue( org.getId());
		
	}

	@Override
	public void wasDeleted( Organisation org ) {

		Object futureId;
		try {
			futureId = orgsTable.prevItemId( orgsTable.getValue());
		} catch ( Exception e ) {
			futureId = null;
		}
		
		orgsTable.removeItem( org.getId());
		
		if ( futureId != null ) 
			orgsTable.setValue( futureId );
		else
			orgsTable.setValue( orgsTable.firstItemId());

		updateCounter();
		
	}

	@Override
	public void wholeListChanged() {
		
		if ( logger.isDebugEnabled()) logger.debug( "OrganisationList received WholeListChanged event!" );
		
		dataFromModel();

		updateCounter();
		
	}

	@Override
	public void currentWasSet( Organisation org ) {
		// TODO Auto-generated method stub
		
	}

	private void dataFromModel() {

		if ( logger.isDebugEnabled()) logger.debug( "Data from model will be read!" );
		

		// Store selection for recovery at the end of this method
		Long selectedId = ( Long )orgsTable.getValue();
		Long newSelectedId = null;
		boolean selected = ( selectedId != null );
		
		// remove old content
		orgsTable.removeAllItems();
		
		// Fill the Table
		Collection<Organisation> orgsList = model.getOrganisations();
		
		if ( orgsList != null ) {
			for ( Organisation org : orgsList ) {
				if ( org != null ) {
					addOrUpdateItem( org );
					
					// Check that selection can be restored
					if ( selected && org.getId() == selectedId ) {
						newSelectedId = org.getId();
						selected = false;
					}
				}
			}
		}
		
		// Sort by Name
		orgsTable.setSortContainerPropertyId( "name" );
		orgsTable.sort();
		
		// Restore previous selection if possible or set first item as selected
		if ( newSelectedId != null ) {
			orgsTable.setValue( newSelectedId );
		} else {
			orgsTable.setValue( orgsTable.firstItemId());
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void addOrUpdateItem( Organisation org ) {
		
		Item item = orgsTable.getItem( org.getId());
		
		if ( item == null ) {

			if ( logger.isDebugEnabled()) logger.debug( "Item will be added: " + org );
			item = orgsTable.addItem( org.getId());
			
		} else {
			if ( logger.isDebugEnabled()) logger.debug( "Item exists already. Will be modified: " + org );
		}

		item.getItemProperty( "code" ).setValue( org.getCode());
		item.getItemProperty( "name" ).setValue( org.getName());
		item.getItemProperty( "data" ).setValue( org );
		
	}
	
	@Override
	protected void addButtonHandler() {
		
		logger.debug( "add button was pressed to add new Personnel" );
		
		Organisation newOrg = new Organisation();
		model.setSelectedOrg( newOrg );
		
	}
	
	
}
