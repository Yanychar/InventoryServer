package com.c2point.tools.ui.toolsmgmt;

import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.dialogs.ConfirmDialog;

import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ItemStatus;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Category;
import com.c2point.tools.entity.tool.Manufacturer;
import com.c2point.tools.entity.tool.Tool;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public class ToolItemView extends FormLayout implements ToolItemChangedListener, EditInitiationListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( ToolItemView.class.getName());

	private ToolsListModel	model;
	
	private ComboBox		tool;
	private TextField 		toolText;
	
	private TextField		code;
	private TextArea 		description;
	
	private ComboBox		category;
	private TextField 		categoryText;
	
	private ComboBox		manufacturer;
	private TextField 		manufacturerText;
	
	private ComboBox		currentUser;
	private TextField 		currentUserText;

	private CheckBox		personalFlag;

	private ComboBox		status;
	
	private ComboBox		reservedBy;
	private TextField 		reservedByText;
	
	private TextField		serialNumber;
	private TextField		barcode;

	
	private Button		editcloseButton;
	private Button		deleteButton;
	
	private boolean		editedFlag;
	private ToolItem	shownItem;

/*
 	code;
	name;
	description;
	
	category;
	manufacturer;
	
	currentUser;
	personalFlag;
	status;
	
	reservedBy;
	
	serialNumber;
	barcode;

 */
	
	public ToolItemView( ToolsListModel model ) {
		super();
		
		setModel( model );
		
		initView();
		
		model.addChangedListener(( ToolItemChangedListener ) this );
		model.addChangedListener(( EditInitiationListener ) this );
		
	}

	private void initView() {

		this.setSpacing( true );
		this.setMargin( true );
//		this.setSizeFull();

		tool = new ComboBox( model.getApp().getResourceStr( "toolsmgmt.view.label.tool" ));
		tool.setInputPrompt( model.getApp().getResourceStr( "toolsmgmt.text.select.tool" ));
		tool.setFilteringMode( FilteringMode.CONTAINS );
		tool.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		tool.setNullSelectionAllowed( false );
		tool.setInvalidAllowed( false );
		tool.setImmediate( true );
		tool.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange( ValueChangeEvent event ) {
				
				Tool tool = ( Tool )event.getProperty().getValue();
				
				ToolItemView.this.toolText.setValue( tool.getName());
				ToolItemView.this.code.setValue( tool.getCode());
				ToolItemView.this.description.setValue( tool.getDescription());
				ToolItemView.this.category.setValue( tool.getCategory());
				ToolItemView.this.categoryText.setValue( tool.getCategory().getName());
				ToolItemView.this.manufacturer.setValue( tool.getManufacturer());
				ToolItemView.this.manufacturerText.setValue( tool.getManufacturer().getName());
				
			}
			
		});
		

		toolText = new TextField( model.getApp().getResourceStr( "toolsmgmt.view.label.tool" ));
		toolText.setNullRepresentation( "" );
		toolText.setImmediate( true );
		
		code = new TextField( model.getApp().getResourceStr( "toolsmgmt.view.label.code" ));
		code.setRequired(true);
		code.setRequiredError( model.getApp().getResourceStr( "toolsmgmt.view.error.fieldempty" ));
		code.setNullRepresentation( "" );
		code.setImmediate( true );

		description = new TextArea( model.getApp().getResourceStr( "toolsmgmt.view.label.descr" ));
		description.setNullRepresentation( "" );
		description.setRows( 3 );
		description.setImmediate( true );
		
		category = new ComboBox( model.getApp().getResourceStr( "toolsmgmt.view.label.category" ));
		category.setInputPrompt( model.getApp().getResourceStr( "toolsmgmt.text.select.category" ));
		category.setFilteringMode( FilteringMode.CONTAINS );
		category.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		category.setNullSelectionAllowed( false );
		category.setInvalidAllowed( false );
		category.setImmediate( true );
		
		categoryText = new TextField( model.getApp().getResourceStr( "toolsmgmt.view.label.category" ));
		categoryText.setNullRepresentation( "" );
		categoryText.setImmediate( true );
		
		manufacturer = new ComboBox( model.getApp().getResourceStr( "toolsmgmt.view.label.manufacturer" ));
		manufacturer.setInputPrompt( model.getApp().getResourceStr( "toolsmgmt.text.select.manufacturer" ));
		manufacturer.setFilteringMode( FilteringMode.CONTAINS );
		manufacturer.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		manufacturer.setNullSelectionAllowed( false );
		manufacturer.setInvalidAllowed( false );
		manufacturer.setImmediate( true );

		manufacturerText = new TextField( model.getApp().getResourceStr( "toolsmgmt.view.label.manufacturer" ));
		manufacturerText.setNullRepresentation( "" );
		manufacturerText.setImmediate( true );
		
		currentUser = new ComboBox( model.getApp().getResourceStr( "toolsmgmt.view.label.user" ));
		currentUser.setInputPrompt( model.getApp().getResourceStr( "toolsmgmt.text.select.user" ));
		currentUser.setFilteringMode( FilteringMode.CONTAINS );
		currentUser.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		currentUser.setNullSelectionAllowed( false );
		currentUser.setInvalidAllowed( false );
		currentUser.setImmediate( true );

		currentUserText = new TextField( model.getApp().getResourceStr( "toolsmgmt.view.label.user" ));
		currentUserText.setNullRepresentation( "" );
		currentUserText.setImmediate( true );
		
		personalFlag = new CheckBox( model.getApp().getResourceStr( "toolsmgmt.view.label.personalflag" ));

		status = new ComboBox( model.getApp().getResourceStr( "toolsmgmt.view.label.status" ));
		status.setInputPrompt( model.getApp().getResourceStr( "toolsmgmt.text.select.status" ));
		status.setFilteringMode( FilteringMode.CONTAINS );
		status.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		status.setNullSelectionAllowed( false );
		status.setInvalidAllowed( false );
		status.setImmediate( true );
		
		reservedBy = new ComboBox( model.getApp().getResourceStr( "toolsmgmt.view.label.reservedby" ));
		reservedBy.setInputPrompt( model.getApp().getResourceStr( "toolsmgmt.text.select.reservedby" ));
		reservedBy.setFilteringMode( FilteringMode.CONTAINS );
		reservedBy.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		reservedBy.setNullSelectionAllowed( true );
		reservedBy.setInvalidAllowed( false );
		reservedBy.setImmediate( true );

		reservedByText = new TextField( model.getApp().getResourceStr( "toolsmgmt.view.label.reservedby" ));
		reservedByText.setNullRepresentation( "" );
		reservedByText.setImmediate( true );
		
		serialNumber = new TextField( model.getApp().getResourceStr( "toolsmgmt.view.label.sn" ));
		serialNumber.setNullRepresentation( "" );
		serialNumber.setImmediate( true );

		barcode = new TextField( model.getApp().getResourceStr( "toolsmgmt.view.label.barcode" ));
		barcode.setNullRepresentation( "" );
		barcode.setImmediate( true );

		Label separator = new Label( "<hr/>", ContentMode.HTML );
		separator.setWidth( "100%" );
		
		
		addComponent( tool );
		addComponent( toolText );
		addComponent( code );
		addComponent( description );
		addComponent( category );
		addComponent( categoryText );
		addComponent( manufacturer );
		addComponent( manufacturerText );
		addComponent( separator );
		addComponent( personalFlag );
		addComponent( currentUser );
		addComponent( currentUserText );
		addComponent( status );
		addComponent( reservedBy );
		addComponent( reservedByText );
		addComponent( serialNumber );
		addComponent( barcode );
		
		addComponent( getButtonsBar());
		
		updateButtons();
		updateFields();
		
	}
	
	public ToolsListModel getModel() { return model; }
	public void setModel( ToolsListModel model ) { this.model = model; }

		
	public boolean isEditedFlag() { return editedFlag;}
	public void setEditedFlag(boolean editedFlag) {this.editedFlag = editedFlag; }
	
	public Component getButtonsBar() {
		
		HorizontalLayout toolBarLayout = new HorizontalLayout();
		
		toolBarLayout.setWidth( "100%");
		toolBarLayout.setMargin( new MarginInfo( false, true, false, true ));

		editcloseButton = new Button();
		
		editcloseButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick( ClickEvent event) {
					
				editSavePressed();
				
			}
		});
		
		
		deleteButton = new Button();
		deleteButton.setIcon( new ThemeResource("icons/16/reject.png"));
		
		deleteButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick( ClickEvent event) {
				if ( ToolItemView.this.shownItem != null ) {
					
					deleteCancelPressed();
				}
			}
		});

		
		toolBarLayout.addComponent( editcloseButton);
		toolBarLayout.addComponent( deleteButton);
		
		return toolBarLayout;
	}

	public void addPressed() {

		initToolsComboBox( this.shownItem.getTool());
		initCategoryComboBox( this.shownItem.getTool().getCategory());
		initManufacturerComboBox(this.shownItem.getTool().getManufacturer());

		initUserComboBox( this.shownItem.getCurrentUser());
		initReservedComboBox( this.shownItem.getReservedBy());
		
	}
	
	private void editSavePressed() {

		switch ( model.getMode()) {
			case ADD:

				viewToData( true );
				
				model.setViewMode();
				break;
			case EDIT:
				
				viewToData( true );
				
				ToolItem updatedItem = model.update( this.shownItem );
				
				if ( updatedItem == null ) {
					// Failed to update
					String template = model.getApp().getResourceStr( "general.errors.update.header" );
					Object[] params = { this.shownItem.getTool().getName() };
					template = MessageFormat.format( template, params );

					Notification.show( template, Notification.Type.ERROR_MESSAGE );
					
				} else {

//					currentWasSet( null );
					
				}
				
				model.setViewMode();
				break;
			case VIEW:

				initUserComboBox( this.shownItem.getCurrentUser());
				initReservedComboBox( this.shownItem.getReservedBy());

				model.setEditMode();
				break;
		}

		updateButtons();
		updateFields();
		
	}
	
	private void deleteCancelPressed() {

		switch ( model.getMode()) {
			case ADD:
			case EDIT:
				model.setViewMode();
				break;
			case VIEW:

				deleteItem();
				
//				model.setViewMode();
				break;
		}

		updateButtons();
		updateFields();
		
	}
		
	private void updateButtons() {

		switch ( model.getMode()) {
			case ADD:
			case EDIT:
				editcloseButton.setCaption( model.getApp().getResourceStr( "general.button.ok" ));
				editcloseButton.setIcon( new ThemeResource("icons/16/approve.png"));

				deleteButton.setCaption( model.getApp().getResourceStr( "general.button.cancel" ));
//				deleteButton.setVisible( false );
				break;
			case VIEW:
				editcloseButton.setCaption( model.getApp().getResourceStr( "general.button.edit" ));
				editcloseButton.setIcon( new ThemeResource("icons/16/edit.png"));
				
				deleteButton.setCaption( model.getApp().getResourceStr( "general.button.delete" ));
//				deleteButton.setVisible( true );
				break;
		}
			
	}

	private void updateFields() {

		switch ( model.getMode()) {
			case ADD:
				tool.setVisible( true );
				toolText.setVisible( false );
//				toolText.setEnabled( false );
				
				code.setEnabled( true );
				description.setEnabled( true );
				
				category.setVisible( false );
				categoryText.setVisible( true );
				categoryText.setEnabled( false );

				manufacturer.setVisible( false );
				manufacturerText.setVisible( true );
				manufacturerText.setEnabled( false );
				
				currentUser.setVisible( true );
				currentUserText.setVisible( false );

				personalFlag.setEnabled( true );
				status.setEnabled( true );
				
				reservedBy.setVisible( true );
				reservedByText.setVisible( false );
				reservedByText.setEnabled( false );
				
				serialNumber.setEnabled( true );
				barcode.setEnabled( true );
				break;
			case EDIT:
				tool.setVisible( false );
				toolText.setVisible( true );
				toolText.setEnabled( false );
				
				code.setEnabled( false);
				description.setEnabled( true );
				
				category.setVisible( false );
				categoryText.setVisible( true );
				categoryText.setEnabled( false );

				manufacturer.setVisible( false );
				manufacturerText.setVisible( true );
				manufacturerText.setEnabled( false );
				
				currentUser.setVisible( true );
				currentUserText.setVisible( false );

				personalFlag.setEnabled( true );
				status.setEnabled( true );
				
				reservedBy.setVisible( true );
				reservedByText.setVisible( false );
				reservedByText.setEnabled( false );
				
				serialNumber.setEnabled( true );
				barcode.setEnabled( true );
				break;
			case VIEW:
				tool.setVisible( false );
				toolText.setVisible( true );
				toolText.setEnabled( false );
				
				code.setEnabled( false);
				description.setEnabled( false );
				
				category.setVisible( false );
				categoryText.setVisible( true );
				categoryText.setEnabled( false );

				manufacturer.setVisible( false );
				manufacturerText.setVisible( true );
				manufacturerText.setEnabled( false );
				
				currentUser.setVisible( false );
				currentUserText.setVisible( true );
				currentUserText.setEnabled( false );

				personalFlag.setEnabled( false );
				status.setEnabled( false );
				
				reservedBy.setVisible( false );
				reservedByText.setVisible( true );
				reservedByText.setEnabled( false );
				
				serialNumber.setEnabled( false );
				barcode.setEnabled( false );
				break;
		}
	}
	
	
	@Override
	public void wasAdded(ToolItem item) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void wasChanged(ToolItem item) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void wasDeleted(ToolItem item) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void wholeListChanged() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void currentWasSet( ToolItem item ) {

		if ( logger.isDebugEnabled()) logger.debug( "ToolItemView received event about user selection. Ready to show:" + item );

		this.shownItem = item;
		setVisible( item != null );
		dataToView();

		switch ( model.getMode()) {
			case ADD:
			case EDIT:
				model.setViewMode();
				break;
			case VIEW:
				break;
			
		}
		
		updateButtons();
		updateFields();
		
	}

	private void dataToView() {

		if ( this.shownItem != null ) {
			
			if ( this.shownItem.getTool() != null ) {
				
				code.setValue( this.shownItem.getTool().getCode());
				toolText.setValue( this.shownItem.getTool().getName());
				description.setValue( this.shownItem.getTool().getDescription());
				
				categoryText.setValue( shownItem.getTool().getCategory() != null ? shownItem.getTool().getCategory().getName() : null );
				manufacturerText.setValue( shownItem.getTool().getManufacturer() != null ? shownItem.getTool().getManufacturer().getName() : null );
				
			}
			
			currentUserText.setValue( shownItem.getCurrentUser() != null ? shownItem.getCurrentUser().getLastAndFirstNames() : null );
			personalFlag.setValue( shownItem.isPersonalFlag());

			initStatusComboBox( shownItem.getStatus());
			
			reservedBy.setValue( shownItem.getReservedBy() != null ? shownItem.getReservedBy().getLastAndFirstNames() : null );
			
			serialNumber.setValue( shownItem.getSerialNumber());
			barcode.setValue( shownItem.getBarcode());

		}
		
	}

	private void viewToData( boolean justEdit ) {

		if ( this.shownItem != null ) {
			
			if ( justEdit ) {

				shownItem.setCurrentUser(( OrgUser )currentUser.getValue());
				shownItem.setStatus(( ItemStatus ) status.getValue());
				shownItem.setReservedBy(( OrgUser )reservedBy.getValue());
				
				shownItem.setSerialNumber( serialNumber.getValue());
				shownItem.setBarcode( barcode.getValue());
				
				shownItem.setPersonalFlag( personalFlag.getValue());
				shownItem.getTool().setDescription( description.getValue());
				
				
			}

		}
		
	}

	
	
	private boolean categoryCBinited = false;
	private  void initCategoryComboBox( Category selectedCat ) {
		
		if ( !categoryCBinited ) {
			for ( Category cat : model.getCategories()) {
				
				addCategory( cat, category, 1 );
				
			}
	
			category.addValueChangeListener( new ValueChangeListener() {
				private static final long serialVersionUID = 1L;
	
				@Override
				public void valueChange( ValueChangeEvent event ) {
					
					
				}
				
			});
		
			categoryCBinited = true;
		}
		
		category.setValue( selectedCat );
	}
	private void addCategory( Category cat, ComboBox combo, int level ) {

		String caption = "";
		
		if ( cat != null && combo != null ) {
			
			category.addItem( cat );
			
			switch ( level ) {
				case 1:
					caption = ""; 
					break;
				case 2:
					caption = "\u2503 \u2523"; 
					break;
				case 3:
					caption = "\u2503   \u2523"; 
					break;
				default:
					caption = "\u2503     \u2523"; 
					break;
			}
			caption = caption + cat.getName(); 
 
			category.setItemCaption( cat, caption );
			
			if ( cat.getChilds() != null && cat.getChilds().size() > 0 ) {
				
				int newLevel = level + 1;
				
				for ( Category catChild : cat.getChilds()) {
					
					addCategory( catChild, combo, newLevel );
					
				}
			}
			
		}
	}

	private boolean manufacturerCBinited = false;
	private  void initManufacturerComboBox( Manufacturer selectedMan ) {

		if ( manufacturerCBinited ) {
			for ( Manufacturer m : model.getManufacturers()) {
				
				manufacturer.addItem( m );
				manufacturer.setItemCaption( m, m.getName());
				
			}
	
			manufacturer.addValueChangeListener( new ValueChangeListener() {
				private static final long serialVersionUID = 1L;
	
				@Override
				public void valueChange( ValueChangeEvent event ) {
					
					
				}
				
			});
			
			manufacturerCBinited = true;
		}
		
		manufacturer.setValue( selectedMan );
		
	}

	private boolean userCBinited = false;
	private  void initUserComboBox( OrgUser selectedUser ) {

		if ( !userCBinited ) {
			for ( OrgUser user : model.getUsers()) {
				
				currentUser.addItem( user );
				currentUser.setItemCaption( user, user.getLastAndFirstNames());
				
			}
		
			currentUser.addValueChangeListener( new ValueChangeListener() {
				private static final long serialVersionUID = 1L;
		
				@Override
				public void valueChange( ValueChangeEvent event ) {
					
					
				}
				
			});
			
			userCBinited = true;
		}
		
		currentUser.setValue( selectedUser );
	
	}

	private boolean statusCBinited = false;
	private  void initStatusComboBox( ItemStatus selectedStatus ) {

		if ( !statusCBinited ) {
			status.addItem( ItemStatus.UNKNOWN );
			status.addItem( ItemStatus.FREE );
			status.addItem( ItemStatus.INUSE );
			status.addItem( ItemStatus.BROCKEN );
			status.addItem( ItemStatus.REPAIRING );
			status.addItem( ItemStatus.STOLEN );
			status.addItem( ItemStatus.RESERVED );
	
			status.setItemCaption( ItemStatus.UNKNOWN, ItemStatus.UNKNOWN.toString( model.getApp().getSessionData().getBundle()));
			status.setItemCaption( ItemStatus.FREE, ItemStatus.FREE.toString( model.getApp().getSessionData().getBundle()));
			status.setItemCaption( ItemStatus.INUSE, ItemStatus.INUSE.toString( model.getApp().getSessionData().getBundle()));
			status.setItemCaption( ItemStatus.BROCKEN, ItemStatus.BROCKEN.toString( model.getApp().getSessionData().getBundle()));
			status.setItemCaption( ItemStatus.REPAIRING, ItemStatus.REPAIRING.toString( model.getApp().getSessionData().getBundle()));
			status.setItemCaption( ItemStatus.STOLEN, ItemStatus.STOLEN.toString( model.getApp().getSessionData().getBundle()));
			status.setItemCaption( ItemStatus.RESERVED, ItemStatus.RESERVED.toString( model.getApp().getSessionData().getBundle()));
			
			status.addValueChangeListener( new ValueChangeListener() {
				private static final long serialVersionUID = 1L;
		
				@Override
				public void valueChange( ValueChangeEvent event ) {
					
					
				}
				
			});
			
			statusCBinited = true;
		}
		
		status.setValue( selectedStatus );
	
	}
	
	private boolean reservedCBinited = false;
	private void initReservedComboBox( OrgUser selectedUser ) {

		if ( !reservedCBinited ) {
			for ( OrgUser user : model.getUsers()) {
				
				reservedBy.addItem( user );
				reservedBy.setItemCaption( user, user.getLastAndFirstNames());
				
			}
		
			reservedBy.addValueChangeListener( new ValueChangeListener() {
				private static final long serialVersionUID = 1L;
		
				@Override
				public void valueChange( ValueChangeEvent event ) {
					
					
				}
				
			});
			
			reservedCBinited = true;
			
		}

		reservedBy.setValue( selectedUser );
		
	}
	
	private boolean toolCBinited = false;
	public void initToolsComboBox( Tool selectedTool ) {
		
		if ( !toolCBinited ) {
			for ( Tool t : model.getTools()) {
				
				tool.addItem( t );
				tool.setItemCaption( t, t.getName());
				
			}
		
			tool.addValueChangeListener( new ValueChangeListener() {
				private static final long serialVersionUID = 1L;
		
				@Override
				public void valueChange( ValueChangeEvent event ) {
					
					
				}
				
			});
			
			toolCBinited = true;
		}
		
		tool.setValue( selectedTool );

	}

	private void deleteItem() {

		// Confirm removal
		String template = model.getApp().getResourceStr( "toolsmgmt.confirm.item.delete" );
		Object[] params = { this.shownItem.getTool().getName() };
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

							ToolItem deletedItem = model.delete( ToolItemView.this.shownItem );
							if ( deletedItem != null) {

								String template = model.getApp().getResourceStr( "toolsmgmt.notify.item.delete" );
								Object[] params = { deletedItem.getTool().getName() };
								template = MessageFormat.format( template, params );

								Notification.show( template );

							} else {
								// Failed to delete
								// Failed to update
								String template = model.getApp().getResourceStr( "toolsmgmt.errors.item.delete" );
								Object[] params = { ToolItemView.this.shownItem.getTool().getName() };
								template = MessageFormat.format( template, params );

								Notification.show( template, Notification.Type.ERROR_MESSAGE );
								
							}

						}
					}

		});

	}

	@Override
	public void initiateAdd() {

		this.initToolsComboBox( null );
		this.initCategoryComboBox( model.getSelectedCategory());
		this.initManufacturerComboBox( null );
		initUserComboBox( this.shownItem.getCurrentUser());
		initReservedComboBox( this.shownItem.getReservedBy());

		model.setAddMode();

		updateButtons();
		updateFields();
		
	}

	@Override
	public void initiateEdit() {
		
	}

	
}
