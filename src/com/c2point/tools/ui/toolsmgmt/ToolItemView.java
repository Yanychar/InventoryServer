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
import com.c2point.tools.ui.toolsmgmt.ToolsListModel.EditMode;
import com.vaadin.data.Item;
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
	
	private TextField 		toolText;
	private TextField		code;
	private TextArea 		description;
	private ComboBox		category;
	private ComboBox		manufacturer;
	private TextField		toolModel;
	
	private CheckBox		personalFlag;
	private ComboBox		currentUser;

	private ComboBox		reservedBy;
	private ComboBox		status;
	
	private TextField		serialNumber;
	private TextField		barcode;

	
	private Button		editcloseButton;
	private Button		deleteButton;
	
	private boolean		editedFlag;
	private ToolItem	shownItem;

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

		manufacturer = new ComboBox( model.getApp().getResourceStr( "toolsmgmt.view.label.manufacturer" ));
		manufacturer.setInputPrompt( model.getApp().getResourceStr( "toolsmgmt.text.select.manufacturer" ));
		manufacturer.setFilteringMode( FilteringMode.CONTAINS );
		manufacturer.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
//		manufacturer.setNullSelectionAllowed( false );
//		manufacturer.setInvalidAllowed( false );
		manufacturer.setImmediate( true );

		toolModel = new TextField( model.getApp().getResourceStr( "toolsmgmt.view.label.model" ));
		toolModel.setNullRepresentation( "" );
		toolModel.setImmediate( true );
		
		currentUser = new ComboBox( model.getApp().getResourceStr( "toolsmgmt.view.label.user" ));
		currentUser.setInputPrompt( model.getApp().getResourceStr( "toolsmgmt.text.select.user" ));
		currentUser.setFilteringMode( FilteringMode.CONTAINS );
		currentUser.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		currentUser.setNullSelectionAllowed( false );
		currentUser.setInvalidAllowed( false );
		currentUser.setImmediate( true );

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

		serialNumber = new TextField( model.getApp().getResourceStr( "toolsmgmt.view.label.sn" ));
		serialNumber.setNullRepresentation( "" );
		serialNumber.setImmediate( true );

		barcode = new TextField( model.getApp().getResourceStr( "toolsmgmt.view.label.barcode" ));
		barcode.setNullRepresentation( "" );
		barcode.setImmediate( true );

		Label separator = new Label( "<hr/>", ContentMode.HTML );
		separator.setWidth( "100%" );
		
		
		addComponent( toolText );
		addComponent( manufacturer );
		addComponent( toolModel );
		addComponent( code );
		addComponent( description );
		addComponent( category );
		addComponent( separator );
		addComponent( personalFlag );
		addComponent( currentUser );
		addComponent( status );
		addComponent( reservedBy );
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

	private void updateButtons() {

		switch ( model.getMode()) {
			case ADD:
			case COPY:
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
			default:
				break;
		}
			
	}
	private void updateFields() {
		updateFields( model.getMode());
	}
	private void updateFields( ToolsListModel.EditMode mode ) {
		
		if ( mode == EditMode.ALLOWED_ALL ) {
			toolText.setReadOnly( false );
			code.setReadOnly( false );
			description.setReadOnly( false );
			category.setReadOnly( false );
			manufacturer.setReadOnly( false );
			toolModel.setReadOnly( false );
			
			personalFlag.setReadOnly( false );
			currentUser.setReadOnly( false );
			status.setReadOnly( false );
			reservedBy.setReadOnly( false );
			serialNumber.setReadOnly( false );
			barcode.setReadOnly( false );
			
		} else {
			toolText.setReadOnly( mode != EditMode.ADD );
			code.setReadOnly( mode != EditMode.ADD ); // ( mode == EditMode.COPY || mode == EditMode.VIEW );
			description.setReadOnly( mode != EditMode.ADD ); //( mode == EditMode.COPY || mode == EditMode.VIEW );
			category.setReadOnly( mode != EditMode.ADD ); 
			manufacturer.setReadOnly( mode != EditMode.ADD);
			toolModel.setReadOnly( mode != EditMode.ADD );

			personalFlag.setReadOnly( mode == EditMode.VIEW );
			currentUser.setReadOnly( mode == EditMode.VIEW );
			status.setReadOnly( mode == EditMode.VIEW );
			reservedBy.setReadOnly( mode == EditMode.VIEW );
			serialNumber.setReadOnly( mode == EditMode.VIEW );
			barcode.setReadOnly( mode == EditMode.VIEW );
		
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
//		setVisible( item != null );
		
		dataToView();

		switch ( model.getMode()) {
			case ADD:
			case COPY:
			case EDIT:
				model.setViewMode();
				break;
			default:
				break;
		}
		
		updateButtons();
		updateFields();
		
	}

	private void dataToView() {

		updateFields( EditMode.ALLOWED_ALL );
		
		
		if ( this.shownItem != null ) {
			
			if ( this.shownItem.getTool() != null ) {
				
				toolText.setValue( this.shownItem.getTool().getName());
				code.setValue( this.shownItem.getTool().getCode());
				description.setValue( this.shownItem.getTool().getDescription());

				Category tmpCat = shownItem.getTool().getCategory();
				if ( tmpCat != null ) {
					if ( logger.isDebugEnabled()) logger.debug( "Tool has Category set up" );
					if ( logger.isDebugEnabled()) logger.debug( "length of Category Combo = " + category.getItemIds().size());
					
					if ( category.getItem( tmpCat ) == null ) {
						if ( logger.isDebugEnabled()) logger.debug( "Category NOT found in the Combo" );
						category.addItem( tmpCat );
						category.setItemCaption( tmpCat, tmpCat.getName());
					} else {
						if ( logger.isDebugEnabled()) logger.debug( "Category was found in the Combo" );
					}
				}
				category.setValue( tmpCat );
				
				Manufacturer tmpMan = shownItem.getTool().getManufacturer();
				if ( tmpMan != null ) {
					if ( manufacturer.getItem( tmpMan ) == null ) {
						manufacturer.addItem( tmpMan );
						manufacturer.setItemCaption( tmpMan, tmpMan.getName());
					}
				}
				manufacturer.setValue( tmpMan );

				toolModel.setValue( this.shownItem.getTool().getModel());
				
			}

			personalFlag.setValue( shownItem.isPersonalFlag());
			
			OrgUser tmpUser = shownItem.getCurrentUser();
			if ( tmpUser != null ) {
				if ( currentUser.getItem( tmpUser ) == null ) {
					currentUser.addItem( tmpUser );
					currentUser.setItemCaption( tmpUser, tmpUser.getLastAndFirstNames());
				}
			}
			currentUser.setValue(  tmpUser );

			ItemStatus tmpStatus = shownItem.getStatus();
			status.addItem( tmpStatus );
			status.setItemCaption( tmpStatus, tmpStatus.toString( model.getApp().getSessionData().getBundle()));
			status.setValue( tmpStatus );
			
			tmpUser = shownItem.getReservedBy();
			if ( tmpUser != null ) {
				if ( reservedBy.getItem( tmpUser ) == null ) {
					reservedBy.addItem( tmpUser );
					reservedBy.setItemCaption( tmpUser, tmpUser.getLastAndFirstNames());
				}
			}
			reservedBy.setValue(  tmpUser );

			
			serialNumber.setValue( shownItem.getSerialNumber());
			barcode.setValue( shownItem.getBarcode());

		}
		
	}

	private void viewToData() {

		if ( this.shownItem != null ) {
			
			if ( model.getMode() == EditMode.ADD ) {
				
				shownItem.getTool().setName( toolText.getValue());
				shownItem.getTool().setCode( code.getValue());
				shownItem.getTool().setDescription( description.getValue());
				shownItem.getTool().setCategory(( Category ) category.getValue());
				shownItem.getTool().setManufacturer(( Manufacturer ) manufacturer.getValue() );
				shownItem.getTool().setModel( toolModel.getValue());
				
				
			} else if ( model.getMode() == EditMode.EDIT ) {
				
				shownItem.getTool().setCode( code.getValue());
				shownItem.getTool().setDescription( description.getValue());
				shownItem.getTool().setCategory(( Category ) category.getValue());
			}

			shownItem.setPersonalFlag( personalFlag.getValue());
			
			shownItem.setCurrentUser(( OrgUser )currentUser.getValue());
			shownItem.setStatus(( ItemStatus ) status.getValue());
			shownItem.setReservedBy(( OrgUser )reservedBy.getValue());
			
			shownItem.setSerialNumber( serialNumber.getValue());
			shownItem.setBarcode( barcode.getValue());
				
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
			
			if ( category.getItem( cat ) == null ) {
				category.addItem( cat );
/*			
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
*/			
				caption = caption + cat.getName(); 
	 
				category.setItemCaption( cat, caption );
			}
			
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

		if ( !manufacturerCBinited ) {
			
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
			status.addItem( ItemStatus.BROKEN );
			status.addItem( ItemStatus.REPAIRING );
			status.addItem( ItemStatus.STOLEN );
			status.addItem( ItemStatus.RESERVED );
	
			status.setItemCaption( ItemStatus.UNKNOWN, ItemStatus.UNKNOWN.toString( model.getApp().getSessionData().getBundle()));
			status.setItemCaption( ItemStatus.FREE, ItemStatus.FREE.toString( model.getApp().getSessionData().getBundle()));
			status.setItemCaption( ItemStatus.INUSE, ItemStatus.INUSE.toString( model.getApp().getSessionData().getBundle()));
			status.setItemCaption( ItemStatus.BROKEN, ItemStatus.BROKEN.toString( model.getApp().getSessionData().getBundle()));
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
	
	@Override
	public void initiateAdd() {

		updateFields( EditMode.ALLOWED_ALL );

		initCategoryComboBox( model.getSelectedCategory());
		initManufacturerComboBox( null );
		initUserComboBox( this.shownItem.getCurrentUser());
		initReservedComboBox( this.shownItem.getReservedBy());

		model.setAddMode();

		updateButtons();
		updateFields();
		
	}

	@Override
	public void initiateCopy() {

		updateFields( EditMode.ALLOWED_ALL );

		initUserComboBox( this.shownItem.getCurrentUser());
		initStatusComboBox( this.shownItem.getStatus());
		initReservedComboBox( this.shownItem.getReservedBy());
		
		model.setCopyMode();

		updateButtons();
		updateFields();
		
	}
	@Override
	public void initiateEdit() {

		updateFields( EditMode.ALLOWED_ALL );

//		initCategoryComboBox( model.getSelectedCategory());
		
		initUserComboBox( this.shownItem.getCurrentUser());
		initStatusComboBox( this.shownItem.getStatus());
		initReservedComboBox( this.shownItem.getReservedBy());

		model.setEditMode();

		updateButtons();
		updateFields();
		
	}

	private void editSavePressed() {

		switch ( model.getMode()) {
			case ADD:

				viewToData();
				
				if ( addToolAndItem( ToolItemView.this.shownItem ) != null ) {

					model.setViewMode();
				}
				
				break;
			case COPY:
				viewToData();
				
				if ( addToolItem( ToolItemView.this.shownItem ) != null ) {

					model.setViewMode();
				}
				
				break;
			case EDIT:
				
				viewToData();
				
				if ( updateToolItem( ToolItemView.this.shownItem ) != null ) {

					model.setViewMode();
				}

				model.setViewMode();
				
				break;
			case VIEW:

				updateFields( EditMode.ALLOWED_ALL );
				
//				initCategoryComboBox( model.getSelectedCategory());
				
				initUserComboBox( this.shownItem.getCurrentUser());
				initStatusComboBox( this.shownItem.getStatus());
				initReservedComboBox( this.shownItem.getReservedBy());

				model.setEditMode();
				break;
			default:
				break;
		}

		updateButtons();
		updateFields();
		
	}
	
	
	@Override
	public void initiateDelete() {

		model.setViewMode();
		deleteCancelPressed();
	
	}

	private void deleteCancelPressed() {

		switch ( model.getMode()) {
			case ADD:
			case COPY:
			case EDIT:
				model.setViewMode();
				dataToView();
				break;
			case VIEW:

				deleteToolItem( ToolItemView.this.shownItem );
				
				break;
			default:
				break;
		}

		updateButtons();
		updateFields();
		
	}
		
	private void deleteToolItem( final ToolItem item ) {

		// Confirm removal
		String template = model.getApp().getResourceStr( "toolsmgmt.confirm.item.delete" );
		Object[] params = { item.getTool().getName() };
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

							ToolItem deletedItem = model.delete( item );
							if ( deletedItem != null) {

								String template = model.getApp().getResourceStr( "toolsmgmt.notify.item.delete" );
								Object[] params = { deletedItem.getTool().getName() };
								template = MessageFormat.format( template, params );

								Notification.show( template );

							} else {
								// Failed to delete
								// Failed to update
								String template = model.getApp().getResourceStr( "toolsmgmt.errors.item.delete" );
								Object[] params = { item.getTool().getName() };
								template = MessageFormat.format( template, params );

								Notification.show( template, Notification.Type.ERROR_MESSAGE );
								
							}

						}
					}

		});

	}

	private ToolItem addToolAndItem( ToolItem item ) {
		
		ToolItem addedItem = model.addToolAndItem( item );
		
		if ( addedItem == null ) {
			// Failed to update
			String template = model.getApp().getResourceStr( "general.errors.add.header" );
			Object[] params = { item.getTool().getName() };
			template = MessageFormat.format( template, params );

			Notification.show( template, Notification.Type.ERROR_MESSAGE );
			
		} else {

//			currentWasSet( null );
			
		}

		return addedItem;
	}
	
	private ToolItem addToolItem( ToolItem item ) {
		
		ToolItem addedItem = model.add( item );
		
		if ( addedItem == null ) {
			// Failed to update
			String template = model.getApp().getResourceStr( "general.errors.add.header" );
			Object[] params = { item.getTool().getName() };
			template = MessageFormat.format( template, params );

			Notification.show( template, Notification.Type.ERROR_MESSAGE );
			
		} else {

//			currentWasSet( null );
			
		}

		return addedItem;
	}
	
	private ToolItem updateToolItem( ToolItem item ) {

		ToolItem updatedItem = model.update( item );
		
		if ( updatedItem == null ) {
			// Failed to update
			String template = model.getApp().getResourceStr( "general.errors.update.header" );
			Object[] params = { item.getTool().getName() };
			template = MessageFormat.format( template, params );

			Notification.show( template, Notification.Type.ERROR_MESSAGE );
			
		} else {

//			currentWasSet( null );
			
		}
		
		return updatedItem;
		
	}
	
}
