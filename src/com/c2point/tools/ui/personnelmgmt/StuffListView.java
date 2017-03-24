package com.c2point.tools.ui.personnelmgmt;

import java.text.MessageFormat;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;

import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.ui.listeners.StuffChangedListener;
import com.c2point.tools.ui.util.AbstractModel.EditModeType;
import com.c2point.tools.ui.util.BoldLabel;
import com.c2point.tools.ui.util.ListWithSearchComponent;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.BaseTheme;

public class StuffListView extends ListWithSearchComponent implements StuffChangedListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( StuffListView.class.getName());

	private static int BUTTON_WIDTH = 25;
	
	private StuffListModel	model;
	private Table			usersTable;

	protected HorizontalLayout	statusBarLayout;
	private Label				counterLabel;
	
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
		
		usersTable.addContainerProperty( "name", String.class, null );
		usersTable.addContainerProperty( "phone", String.class, null );
		usersTable.addContainerProperty( "email", String.class, null );
		usersTable.addContainerProperty( "buttons", HorizontalLayout.class, null );
		usersTable.addContainerProperty( "data", OrgUser.class, null );

		usersTable.setVisibleColumns( new Object [] { "name", "phone", "email", "buttons" } );
		
//		usersTable.setColumnWidth( "code", -1 );
//		usersTable.setColumnExpandRatio( "name", 2f );
		
		usersTable.setColumnHeaders( new String[] { 
				model.getApp().getResourceStr( "general.table.header.employee" ), 
				model.getApp().getResourceStr( "general.caption.phone" ), 
				model.getApp().getResourceStr( "general.caption.email" ), 
				""
		});

		usersTable.setColumnWidth( "buttons", BUTTON_WIDTH * 2 );

		// New User has been selected. Send event to model
		usersTable.addValueChangeListener( new  ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			public void valueChange( ValueChangeEvent event) {

				try {
					if ( logger.isDebugEnabled()) {
						

						if ( usersTable.getItem( usersTable.getValue()) != null ) {
							OrgUser user = ( OrgUser ) usersTable.getItem( usersTable.getValue()).getItemProperty( "data" ).getValue();
							logger.debug( "  User was selected: " + "Id=" + user.getId() + ", " + user );
						}
						
					}
					
					Item item = usersTable.getItem( usersTable.getValue());
					model.setSelectedUser( ( OrgUser ) item.getItemProperty( "data" ).getValue());
					
					
				} catch ( Exception e ) {
					logger.debug( "No selection. OrgUser cannot be fetched from StuffList " );
					model.setSelectedUser( null );
				}
			}
		});
		
		usersTable.addItemClickListener( new ItemClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void itemClick(ItemClickEvent event) {
				
				if ( toProcess( event ) && event.getItem().getItemProperty( "data" ).getValue() instanceof OrgUser ) {
					if ( logger.isDebugEnabled()) logger.debug( "Click shall be processed! Table item clicked: " + event.getItem().getItemProperty( "name" ).getValue());
					
					handleActions(( OrgUser )event.getItem().getItemProperty( "data" ).getValue());
					
				} else {
					if ( logger.isDebugEnabled()) logger.debug( "Click done. No processing" );
				}
				
			}
			
			
			
		});
		
		

		this.addComponent( getSearchBar());
		this.addComponent( usersTable );
		this.addComponent( getStatusbar());
	
		this.setExpandRatio( usersTable, 1.0f );
		
	}
	
	private Object clickedId = null;
	private int clickedCounter = 0;
	
	private void endClickHandlingProcess() {
		clickedCounter = 0;
	}
	private boolean toProcess( ItemClickEvent event ) {

		boolean bRes = false;
		
		if ( event.isDoubleClick()) {
			// We pass doubleclick completely!
			return bRes;
		}
		if ( clickedId != event.getItemId()) {

			// New item clicked. Select it but do nothing
			clickedCounter = 1;
			clickedId = event.getItemId();
			
		} else {
			// Item has been selected already

			if ( clickedCounter < 1 ) {
				clickedCounter = 1;
			}
			
			if ( clickedCounter == 1 ) {
				// Second click opens dialog or starts other actions
				bRes = true;
				
			} else {
				// This is more than 2nd click. Do nothing 
			}
			clickedCounter++;
			
		}
		
		return bRes;
	}
	
	@Override
	public void wasAdded( OrgUser user ) {

		logger.debug( "StuffList receives notification: User was Added!" );
		
		// Find correct Item. Start from selected one
		// update row with data 
		addOrUpdateItem( user );
		
		// set correct selection
		usersTable.setValue( user.getId());
		
		updateCounter();
		
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
			
		updateCounter();

	}

	@Override
	public void wholeListChanged() {
		
		if ( logger.isDebugEnabled()) logger.debug( "StuffList received WhleListChanged event!" );
		
		dataFromModel();
		
		updateCounter();

	}

	@Override
	public void currentWasSet(OrgUser user) {
		
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

		item.getItemProperty( "name" ).setValue( "\t" + user.getLastAndFirstNames());
		item.getItemProperty( "phone" ).setValue( StringUtils.defaultString( user.getPhoneNumber()));
		item.getItemProperty( "email" ).setValue( StringUtils.defaultString( user.getEmail()));
		item.getItemProperty( "data" ).setValue( user );
		
	}
	
	protected Component getStatusbar() {
		
		// Add search field
		if ( statusBarLayout == null ) {

			statusBarLayout = new HorizontalLayout();
			
			statusBarLayout.setWidth( "100%");
			statusBarLayout.setMargin( new MarginInfo( false, false, false, false ));
	
			counterLabel = new BoldLabel();
			counterLabel.setWidth( null );

				
			
			Label glue = new Label( "" );
			statusBarLayout.addComponent( glue );
			statusBarLayout.setExpandRatio( glue,  1.0f );
			statusBarLayout.addComponent( counterLabel );

		}
		
		return statusBarLayout;
	}

	private void updateCounter() {
		
		int counter = usersTable.size();
		counterLabel.setValue( Integer.toString( counter ));
	}
	
	@Override
	protected void addButtonHandler() {
		
		logger.debug( "add button was pressed to add new Personnel" );
/*		
		OrgUser newUser = new OrgUser();
		newUser.setOrganisation( model.getSelectedOrg());

		model.setSelectedUser( newUser );
*/
		addButtonPressed();
		
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
	
	private void addButtonPressed() {
		
		logger.debug( "Add button was pressed to add new OrgUser" );
		
		OrgUser newUser = new OrgUser();
		newUser.setOrganisation( model.getSelectedOrg());
		// Set tool, user as session owner, status, personal flag
		// Done in constructor
		
//		model.setSelectedItem( newItem );
		
		editUser( newUser, EditModeType.ADD );
			
	}
	
	
	private void editButtonPressed( final OrgUser user ) {
		logger.debug( "Edit button was pressed to edit User: " + user.getFirstAndLastNames());

		usersTable.setValue( user.getId());
		
		editUser( EditModeType.EDIT );
		
	}
	
	private void deleteButtonPressed( final OrgUser user ) {
		logger.debug( "Delete button was pressed to delete existing User: " + user.getFirstAndLastNames());

		usersTable.setValue( user.getId());

		// Confirm removal
		String template = model.getApp().getResourceStr( "confirm.personnel.delete" );
		Object[] params = { user.getLastAndFirstNames() };
		template = MessageFormat.format( template, params );
		
		ConfirmDialog.show( model.getApp(),
				model.getApp().getResourceStr( "confirm.general.header" ),
				template,
				model.getApp().getResourceStr( "general.button.ok" ),
				model.getApp().getResourceStr( "general.button.cancel" ),
				new ConfirmDialog.Listener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClose( ConfirmDialog dialog ) {
						if ( dialog.isConfirmed()) {
							
							OrgUser delUser = model.delete( user );
							if ( delUser != null) {

								String template = model.getApp().getResourceStr( "notify.personnel.delete" );
								Object[] params = { delUser.getLastAndFirstNames() };
								template = MessageFormat.format( template, params );

								Notification.show( template );

							} else {
								// Failed to delete
								String template = model.getApp().getResourceStr( "personnel.errors.item.delete" );
								Object[] params = { user.getLastAndFirstNames() };
								template = MessageFormat.format( template, params );

								Notification.show( template, Notification.Type.ERROR_MESSAGE );
								
							}


						}
					}

		});
		
	}

	private void handleActions( OrgUser user ) {

		editUser( user, EditModeType.VIEW );
		
	}
	
	private void editUser( EditModeType editMode ) {
		
		editUser( model.getSelectedUser(), editMode );
	}
	private void editUser( OrgUser user, EditModeType editMode ) {

		StuffEditDlg editDlg = new StuffEditDlg( model, user, editMode ) {
			private static final long serialVersionUID = 1L;

			@Override
			public void dlgClosed() {

				logger.debug( "UserEditDlg Dialog has been closed!" );
				endClickHandlingProcess();
				
			}
		};

		UI.getCurrent().addWindow( editDlg );
		
	}
	
}
