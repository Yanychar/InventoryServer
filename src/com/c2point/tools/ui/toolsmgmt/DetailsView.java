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
import com.c2point.tools.ui.AbstractModel.EditModeType;
import com.c2point.tools.ui.listeners.EditInitiationListener;
import com.c2point.tools.ui.listeners.ToolItemChangedListener;
import com.c2point.tools.ui.util.DoubleField;
import com.c2point.tools.ui.util.IntegerField;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

public class DetailsView extends FormLayout implements ToolItemChangedListener, EditInitiationListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( DetailsView.class.getName());

	private ToolsListModel	model;
	
	private TextField 		toolText;
	private TextArea 		toolInfo;
	private ComboBox		category;
	private ComboBox		manufacturer;
	private TextField		toolModel;
	
	private CheckBox		personalFlag;
	private ComboBox		currentUser;

	private ComboBox		reservedBy;
	private ComboBox		status;
	
	private TextField		serialNumber;
	private TextField		barcode;
	private TextArea 		comments;
	
	private PopupDateField	buyDate;
	private PopupDateField	nextMaintenance;
	private DoubleField		price;
	private IntegerField	takuu;
	
//	private Button			editcloseButton;
//	private Button			deleteButton;
	
	private ToolItem		shownItem;
	
	
	public DetailsView( ToolsListModel model ) {
		super();
		
		setModel( model );
		
		
		initView();
		
		model.addListener(( ToolItemChangedListener ) this );
//		model.addListener(( EditInitiationListener ) this );
		
		model.clearEditMode();
	}

	private void initView() {

		this.setSpacing( true );
		this.setMargin( true );

		toolText = new TextField( model.getApp().getResourceStr( "toolsmgmt.view.label.tool" ));
		toolText.setNullRepresentation( "" );
		toolText.setImmediate( true );
		
		toolInfo = new TextArea( model.getApp().getResourceStr( "toolsmgmt.view.label.toolinfo" ));
		toolInfo.setNullRepresentation( "" );
		toolInfo.setRows( 3 );
		toolInfo.setImmediate( true );
		
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

		barcode = new TextField( model.getApp().getResourceStr( "toolsmgmt.view.label.barcode" ) +":" );
		barcode.setNullRepresentation( "" );
		barcode.setImmediate( true );

		comments = new TextArea( model.getApp().getResourceStr( "toolsmgmt.view.label.iteminfo" ));
		comments.setNullRepresentation( "" );
		comments.setRows( 3 );
		comments.setImmediate( true );
		
		buyDate = new PopupDateField( "Bought" + ":" );
		buyDate.setDateFormat( "dd.MM.yyyy" );
		
		nextMaintenance = new PopupDateField( "Next Maintenance" + ":" );
		nextMaintenance.setDateFormat( "MM.yyyy" );
		nextMaintenance.setResolution( Resolution.MONTH);
		
		price = new DoubleField( "Price" + ":" );
		price.setLocale( model.getApp().getSessionData().getLocale());
		price.setMinValue( 0. );		
		
		takuu = new IntegerField( "Guarantee (months)" + ":" );
		takuu.setupMaxValue( 120 );

		addComponent( manufacturer );
		addComponent( toolModel );
		addComponent( toolText );
		
		addComponent( toolInfo );
		addComponent( category );
		addComponent( getSeparator());
		addComponent( personalFlag );
		addComponent( currentUser );
		addComponent( status );
		addComponent( reservedBy );
		addComponent( getSeparator());
		addComponent( serialNumber );
		addComponent( barcode );
		addComponent( comments );
		addComponent( getSeparator());
		
		addComponent( buyDate );
		addComponent( price );
		addComponent( takuu );
		addComponent( nextMaintenance );
		
		addComponent( getSeparator());
		addComponent( getButtonsBar());
		
		updateButtons();
		updateFields();
		
	}
	
	public ToolsListModel getModel() { return model; }
	public void setModel( ToolsListModel model ) { this.model = model; }

	public Component getButtonsBar() {
		
		HorizontalLayout toolBarLayout = new HorizontalLayout();
		
		toolBarLayout.setWidth( "100%");
		toolBarLayout.setMargin( new MarginInfo( false, true, false, true ));
/*
		editcloseButton = new Button();
		
		editcloseButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick( ClickEvent event) {
					
				editButtonPressed();
				
			}
		});
		
		
		deleteButton = new Button();
		deleteButton.setIcon( new ThemeResource("icons/16/reject.png"));
		
		deleteButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick( ClickEvent event) {
				if ( DetailsView.this.shownItem != null ) {
					
					deleteCancelPressed();
				}
			}
		});

		toolBarLayout.addComponent( editcloseButton);
		toolBarLayout.addComponent( deleteButton);

*/		
		
		
		return toolBarLayout;
	}

	private void updateButtons() {
/*
		editcloseButton.setEnabled( model.allowsToEdit());
		deleteButton.setEnabled( model.allowsToEdit());
			
		switch ( model.getEditMode()) {
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
*/			
	}
	private void updateFields() {
		updateFields( false );
	}
	private void updateFields( boolean allowUpdateAll ) {
		
		if ( allowUpdateAll ) {
			toolText.setReadOnly( false );
			toolInfo.setReadOnly( false );
			category.setReadOnly( false );
			manufacturer.setReadOnly( false );
			toolModel.setReadOnly( false );
			
			personalFlag.setReadOnly( false );
			currentUser.setReadOnly( false );
			status.setReadOnly( false );
			reservedBy.setReadOnly( false );
			serialNumber.setReadOnly( false );
			barcode.setReadOnly( false );
			comments.setReadOnly( false );
			
			buyDate.setReadOnly( false );
			nextMaintenance.setReadOnly( false );
			price.setReadOnly( false );
			takuu.setReadOnly( false );
			
		} else {
			toolText.setReadOnly( model.getEditMode() != ToolsListModel.EditModeType.ADD );
			toolInfo.setReadOnly( model.getEditMode() != ToolsListModel.EditModeType.ADD ); //( mode == EditMode.COPY || mode == EditMode.VIEW );
			category.setReadOnly( model.getEditMode() != ToolsListModel.EditModeType.ADD ); 
			manufacturer.setReadOnly( model.getEditMode() != ToolsListModel.EditModeType.ADD);
			toolModel.setReadOnly( model.getEditMode() != ToolsListModel.EditModeType.ADD );

			personalFlag.setReadOnly( model.getEditMode() == ToolsListModel.EditModeType.VIEW );
			currentUser.setReadOnly( model.getEditMode() == ToolsListModel.EditModeType.VIEW );
			status.setReadOnly( model.getEditMode() == ToolsListModel.EditModeType.VIEW );
			reservedBy.setReadOnly( model.getEditMode() == ToolsListModel.EditModeType.VIEW );
			serialNumber.setReadOnly( model.getEditMode() == ToolsListModel.EditModeType.VIEW );
			barcode.setReadOnly( model.getEditMode() == ToolsListModel.EditModeType.VIEW );
			comments.setReadOnly( model.getEditMode() != ToolsListModel.EditModeType.VIEW );

			buyDate.setReadOnly( model.getEditMode() == ToolsListModel.EditModeType.VIEW );
			nextMaintenance.setReadOnly( model.getEditMode() == ToolsListModel.EditModeType.VIEW );
			price.setReadOnly( model.getEditMode() == ToolsListModel.EditModeType.VIEW );
			takuu.setReadOnly( model.getEditMode() == ToolsListModel.EditModeType.VIEW );
			
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

		switch ( model.getEditMode()) {
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

		updateFields( true );
		
		setVisible( this.shownItem != null );
	
		if ( this.shownItem != null ) {
			
			if ( this.shownItem.getTool() != null ) {
				
				toolText.setValue( this.shownItem.getTool().getName());
				toolInfo.setValue( this.shownItem.getTool().getToolInfo());

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
			comments.setValue( this.shownItem.getComments());
			
			buyDate.setValue( shownItem.getBuyTime() != null ? shownItem.getBuyTime().toDate() : null );
			nextMaintenance.setValue( shownItem.getMaintenance() != null ? shownItem.getMaintenance().toDate() : null );
			price.setValue( shownItem.getPrice());
			takuu.setValue( shownItem.getTakuu());
			

		} else {
			if ( logger.isDebugEnabled()) logger.debug( "No selection. Dataview shall be cleared" );

			toolText.setValue( "" );
			manufacturer.setValue( null );
			toolModel.setValue( "" );
			toolInfo.setValue( "" );
			category.setValue( null );

			personalFlag.setValue( false );
			currentUser.setValue(  null );
			status.setValue( null );
			reservedBy.setValue(  null );
			
			serialNumber.setValue( "" );
			barcode.setValue( "" );
			comments.setValue( "" );

			buyDate.setValue( null );
			nextMaintenance.setValue( null );
			price.setValue( "" );
			takuu.setValue( "" );

		}
		
	}

	@Override
	public void initiateAdd() {
/*
		updateFields( true );

		initCategoryComboBox( model.getSelectedCategory());
		initManufacturerComboBox( null );
		initUserComboBox( this.shownItem.getCurrentUser());
		initReservedComboBox( this.shownItem.getReservedBy());

		model.setAddMode();

		updateButtons();
		updateFields();
*/
		ToolItemEditDlg editDlg = new ToolItemEditDlg( model, EditModeType.ADD );
		
		UI.getCurrent().addWindow( editDlg );
		
	}

	@Override
	public void initiateCopy() {
/*
		updateFields( true );

		initUserComboBox( this.shownItem.getCurrentUser());
		initStatusComboBox( this.shownItem.getStatus());
		initReservedComboBox( this.shownItem.getReservedBy());
		
		model.setCopyMode();

		updateButtons();
		updateFields();
*/
		ToolItemEditDlg editDlg = new ToolItemEditDlg( model, EditModeType.COPY );
		
		UI.getCurrent().addWindow( editDlg );
		
	}
	@Override
	public void initiateEdit() {
/*
		updateFields( true );

//		initCategoryComboBox( model.getSelectedCategory());
		
		initUserComboBox( this.shownItem.getCurrentUser());
		initStatusComboBox( this.shownItem.getStatus());
		initReservedComboBox( this.shownItem.getReservedBy());

		model.setEditMode();

		updateButtons();
		updateFields();
*/
		ToolItemEditDlg editDlg = new ToolItemEditDlg( model, EditModeType.EDIT );
		
		UI.getCurrent().addWindow( editDlg );
	}
	
	@Override
	public void initiateDelete() {

		model.setViewMode();
		deleteCancelPressed();
	
	}

	private void deleteCancelPressed() {

		switch ( model.getEditMode()) {
			case ADD:
			case COPY:
			case EDIT:
				model.setViewMode();
				dataToView();
				break;
			case VIEW:

				deleteToolItem( DetailsView.this.shownItem );
				
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

	private Component getSeparator() {
	
		Label separator = new Label( "<hr/>", ContentMode.HTML );
		separator.setWidth( "100%" );
		
		return separator;
	}

}
