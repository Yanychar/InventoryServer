package com.c2point.tools.ui.personnelmgmt;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.ui.listeners.StuffChangedListener;
import com.c2point.tools.ui.util.ListWithSearchComponent;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Table;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.BaseTheme;

public class StuffListView extends ListWithSearchComponent implements StuffChangedListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( StuffListView.class.getName());

	private static int BUTTON_WIDTH = 25;
	
	private StuffListModel	model;

	private Table			usersTable;
	
	public StuffListView( StuffListModel model ) {
		super( true );
		this.model = model;

		initView();

		model.addListener( this );
		
	}

	private void initView() {

		setSizeFull();

		setMargin( true );
//		setSpacing( true );

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
		
		usersTable.addContainerProperty( "code", String.class, null );
		usersTable.addContainerProperty( "name", String.class, null );
		usersTable.addContainerProperty( "buttons", HorizontalLayout.class, null );
		usersTable.addContainerProperty( "data", OrgUser.class, null );

		usersTable.setVisibleColumns( new Object [] { "code", "name", "buttons" } );
		
//		usersTable.setColumnWidth( "code", -1 );
//		usersTable.setColumnExpandRatio( "name", 2f );
		
		usersTable.setColumnHeaders( new String[] { 
				model.getApp().getResourceStr( "general.table.header.code" ), 
				model.getApp().getResourceStr( "general.table.header.employee" ), 
				""
		});

		usersTable.setColumnWidth( "buttons", BUTTON_WIDTH * 3 );

		// New User has been selected. Send event to model
		usersTable.addValueChangeListener( new  ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			public void valueChange( ValueChangeEvent event) {
				if ( logger.isDebugEnabled()) logger.debug( "Property.valueChanged!" );
				
				try {
					if ( logger.isDebugEnabled()) {
						
						logger.debug( "Table item selected. Item Id = " + usersTable.getValue());
						logger.debug( "  Item = " + usersTable.getItem( usersTable.getValue()));

						if ( usersTable.getItem( usersTable.getValue()) != null )
							logger.debug( "  User was selected: " + ( OrgUser ) usersTable.getItem( usersTable.getValue()).getItemProperty( "data" ).getValue());
						
					}
					
					Item item = usersTable.getItem( usersTable.getValue());
					model.setSelectedUser( ( OrgUser ) item.getItemProperty( "data" ).getValue());
					
					
				} catch ( Exception e ) {
					logger.debug( "No selection. OrgUser cannot be fetched from StuffList " );
					model.setSelectedUser( null );
				}
			}
		});

		this.addComponent( getSearchBar());
		this.addComponent( usersTable );
		
		this.setExpandRatio( usersTable, 1.0f );
		
	}

	@Override
	public void wasAdded( OrgUser user ) {

		logger.debug( "StuffList receives notification: User was Added!" );
		
		// Find correct Item. Start from selected one
		// update row with data 
		addOrUpdateItem( user );
		
		// set correct selection
		usersTable.setValue( user.getId());
		
	}

	@Override
	public void wasChanged( OrgUser user ) {
		
		logger.debug( "StuffList receives notification: User was Changed!" );
		
		// Find correct Item. Start from selected one
		// update row with data 
		addOrUpdateItem( user );
		
		// set correct selection
		usersTable.setValue( user.getId());
		
	}

	@Override
	public void wasDeleted( OrgUser user ) {

		Object futureId;
		try {
			futureId = usersTable.prevItemId( usersTable.getValue());
		} catch ( Exception e ) {
			futureId = null;
		}
		
		usersTable.removeItem( user.getId());
		
		if ( futureId != null ) 
			usersTable.setValue( futureId );
		else
			usersTable.setValue( usersTable.firstItemId());
			
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
		Long selectedId = ( Long )usersTable.getValue();
		Long newSelectedId = null;
		boolean selected = ( selectedId != null );
		
		// remove old content
		usersTable.removeAllItems();

		Collection<OrgUser> usersList = model.getUsers();
		
		if ( usersList != null ) {
			for ( OrgUser user : usersList ) {
				if ( user != null ) {
					addOrUpdateItem( user );
					
					// Check that selection can be restored
					if ( selected && user.getId() == selectedId ) {
						newSelectedId = user.getId();
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
		
		addButton.setEnabled( model.allowsToEdit());
		
	}
	
	@SuppressWarnings("unchecked")
	private void addOrUpdateItem( OrgUser user ) {
		
		Item item = usersTable.getItem( user.getId());
		
		if ( item == null ) {

			if ( logger.isDebugEnabled()) logger.debug( "Item will be added: " + user );
			item = usersTable.addItem( user.getId());
			
	        item.getItemProperty( "buttons" ).setValue( getButtonSet( item ));
			
		} else {
			if ( logger.isDebugEnabled()) logger.debug( "Item exists already. Will be modified: " + user );
		}

		item.getItemProperty( "code" ).setValue( user.getCode());
		item.getItemProperty( "name" ).setValue( user.getLastAndFirstNames());
		item.getItemProperty( "data" ).setValue( user );
		
	}
	
	@Override
	protected void addButtonHandler() {
		
		logger.debug( "add button was pressed to add new Personnel" );
		
		OrgUser newUser = new OrgUser();
		newUser.setOrganisation( model.getSelectedOrg());

		model.setSelectedUser( newUser );
		
	}
	
    private Component getButtonSet( Item item ) {
    	
        HorizontalLayout buttonsSet = new HorizontalLayout();

        buttonsSet.setSpacing( true );

		if ( model.allowsToEdit()) {
        
			final NativeButton editButton = 	createButton( "icons/16/edit.png", "toolsmgmt.edit.tooltip", item );
			final NativeButton deleteButton = createButton( "icons/16/delete.png", "toolsmgmt.delete.tooltip", item );
	        
	        editButton.addClickListener( new ClickListener() {
				private static final long serialVersionUID = 1L;
				@Override
				public void buttonClick( ClickEvent event ) {
					// Button data is Item. Item's data property is ToolItem
					editButtonPressed(( OrgUser ) ((Item) editButton.getData()).getItemProperty( "data" ).getValue());
				}
	        });
	        deleteButton.addClickListener( new ClickListener() {
				private static final long serialVersionUID = 1L;
				@Override
				public void buttonClick( ClickEvent event ) {
					// Button data is Item. Item's data property is ToolItem
					deleteButtonPressed(( OrgUser ) ((Item) deleteButton.getData()).getItemProperty( "data" ).getValue());
				}
	        });
	        
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
	
	private void editButtonPressed( final OrgUser user ) {
		logger.debug( "Edit button was pressed to add new User: " + user.getFirstAndLastNames());

		usersTable.setValue( user.getId());
		
		model.initiateEdit();
		
	}
	
	private void deleteButtonPressed( final OrgUser user ) {
		logger.debug( "Delete button was pressed to delete existing User: " + user.getFirstAndLastNames());

		usersTable.setValue( user.getId());
		
		model.initiateDelete();
		
	}
	
	
}
