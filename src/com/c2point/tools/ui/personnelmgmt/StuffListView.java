package com.c2point.tools.ui.personnelmgmt;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.ui.ListWithSearchComponent;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Table;

public class StuffListView extends ListWithSearchComponent implements StuffChangedListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( StuffListView.class.getName());

	private StuffListModel	model;

	private Table			usersTable;
	
	public StuffListView( StuffListModel model ) {
		super();
		this.model = model;

		initView();

		model.addChangedListener( this );
		
	}

	private void initView() {

		setSizeFull();

		setMargin( true );
//		setSpacing( true );

		usersTable = new Table();
		
		setContainerForSearch( usersTable );
		
		// Configure table
		usersTable.setSelectable( true );
		usersTable.setNullSelectionAllowed(  false );
		usersTable.setMultiSelect( false );
		usersTable.setColumnCollapsingAllowed( false );
		usersTable.setColumnReorderingAllowed( false );
		usersTable.setImmediate( true );
		usersTable.setSizeFull();
		
		usersTable.addContainerProperty( "code", String.class, null );
		usersTable.addContainerProperty( "fio", String.class, null );
		usersTable.addContainerProperty( "data", OrgUser.class, null );

		usersTable.setVisibleColumns( new Object [] { "code", "fio" } );
		
		usersTable.setColumnHeaders( new String[] { 
				model.getApp().getResourceStr( "general.table.header.code" ), 
				model.getApp().getResourceStr( "general.table.header.employee" ), 
		
		});

	
		// New User has been selected. Send event to model
		usersTable.addValueChangeListener( new  ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			public void valueChange( ValueChangeEvent event) {
				if ( logger.isDebugEnabled()) logger.debug( "Property.valueChanged!" );
				
				try {
					Item item = usersTable.getItem( usersTable.getValue());
					model.setSelectedUser( ( OrgUser ) item.getItemProperty( "data" ).getValue() );
					
					logger.debug( "UserSelected Event. User was selected: " + model.getSelectedUser().getFirstAndLastNames());
					
				} catch ( Exception e ) {
					logger.error( "Could not fetch OrgUser from StuffList table for UserId = " + event.getProperty().getValue());
				}
			}
		});

		this.addComponent( getSearchBar());
		this.addComponent( usersTable );
		
		this.setExpandRatio( usersTable, 1.0f );
		
	}

	@Override
	public void wasAdded(OrgUser user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void wasChanged(OrgUser user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void wasDeleted(OrgUser user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void wholeListChanged() {
		
		if ( logger.isDebugEnabled()) logger.debug( "StuffList received WhleListChanged event!" );
		
		dataFromModel();
		
	}

	@Override
	public void currentWasSet(OrgUser user) {
		// TODO Auto-generated method stub
		
	}

	private void dataFromModel() {

		if ( logger.isDebugEnabled()) logger.debug( "Data from model will be read!" );
		
		// Store selection for recovery at the end of this method
		OrgUser selectedUser = ( OrgUser )usersTable.getValue();
		OrgUser newSelectedUser = null;
		boolean selected = ( selectedUser != null );
		
		// remove old content
		usersTable.removeAllItems();

		Collection<OrgUser> usersList = model.getUsers();
		
		if ( usersList != null ) {
			for ( OrgUser user : usersList ) {
				if ( user != null ) {
					addOrUpdateItem( user );
					
					// Check that selection can be restored
					if ( selected && user.getId() == selectedUser.getId()) {
						newSelectedUser = user;
						selected = false;
					}
				}
			}
		}
		
		usersTable.setSortContainerPropertyId( "fio" );

		usersTable.sort();
		
		if ( newSelectedUser != null ) {
			usersTable.setValue( newSelectedUser );
		} else {
			usersTable.setValue( usersTable.firstItemId());
		}
		
		
	}
	
	private void addOrUpdateItem( OrgUser user ) {
		
		Item item = usersTable.getItem( user.getId() );
		
		if ( item == null ) {

			if ( logger.isDebugEnabled()) logger.debug( "Item will be added: " + user );
			item = usersTable.addItem( user.getId());
			
		} else {
			if ( logger.isDebugEnabled()) logger.debug( "Item exists already. Will be modified: " + user );
		}

		item.getItemProperty( "code" ).setValue( user.getCode());
		item.getItemProperty( "fio" ).setValue( user.getLastAndFirstNames());
		item.getItemProperty( "data" ).setValue( user );
		
	}
	
	
	
}
