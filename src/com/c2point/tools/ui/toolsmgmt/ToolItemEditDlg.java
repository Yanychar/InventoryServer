package com.c2point.tools.ui.toolsmgmt;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.ManyToOne;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.SettingsFacade;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ItemStatus;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Category;
import com.c2point.tools.entity.tool.Manufacturer;
import com.c2point.tools.entity.tool.Tool;
import com.c2point.tools.ui.AbstractDialog;
import com.c2point.tools.ui.CustomGridLayout;
import com.c2point.tools.ui.AbstractModel.EditModeType;
import com.c2point.tools.ui.util.CaptionedHLabel;
import com.c2point.tools.ui.util.DoubleField;
import com.c2point.tools.ui.util.IntegerField;
import com.c2point.tools.ui.util.StyledLabel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect.NewItemHandler;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.VerticalLayout;

public class ToolItemEditDlg extends AbstractDialog {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( ToolItemEditDlg.class.getName());

	private ToolsListModel		model;
//	private ToolItem 			item;
	
	/* New variant of Tool selection */
	private	CheckBox		editToolFlag;
	// Tool fields
	private ComboBox		manufSelect;
	private ComboBox		modelSelect;
	private TextArea		nameText;
	private ComboBox		catSelect;

	// ToolItem fields
	private	IntegerField	quantity;

	private TextField		barcode;
	private ComboBox		statusBox;
	private ComboBox		currentUser;
	private CheckBox		personalFlag;

	private TextField		serialNumber;
	
	private PopupDateField	buyDate;
	private PopupDateField	nextMaintenance;
	private DoubleField		price;
	private IntegerField	takuu;

	private TextArea 		comments;
	
	public ToolItemEditDlg( ToolsListModel model, EditModeType editModeType ) {
		super();
		
		this.model = model;
//		this.item = model.getSelectedItem();
		this.model.setEditMode( editModeType );
			
		initUI();
	}

	private void initUI() {
		
		if ( model.getSelectedItem() == null ) {
			logger.debug( "No ToolItem selected. ADD has been chosen!" );
		}

		setCaption( getHeader());
		setModal( true );
		setClosable( true );

		CustomGridLayout subContent = new CustomGridLayout();
		subContent.setMargin( true );
		subContent.setSpacing( true );

		center();
		
		editToolFlag = new CheckBox( "" );
		editToolFlag.setImmediate( true );

		manufSelect = new ComboBox();
		manufSelect.setInputPrompt( "Select manufacturer" );
		manufSelect.setFilteringMode( FilteringMode.CONTAINS );
		manufSelect.setNullSelectionAllowed( false );
		manufSelect.setInvalidAllowed( false );
		manufSelect.setTextInputAllowed( true );
		manufSelect.setNewItemsAllowed( true );
		manufSelect.setImmediate( true );
		
		modelSelect = new ComboBox();
		modelSelect.setInputPrompt( "Select model" );
		modelSelect.setFilteringMode( FilteringMode.CONTAINS );
		modelSelect.setNullSelectionAllowed( false );
		modelSelect.setInvalidAllowed( false );
		modelSelect.setTextInputAllowed( true );
		modelSelect.setNewItemsAllowed( true );
		modelSelect.setImmediate( true );
		
		nameText = new TextArea();
		nameText.setNullRepresentation( "" );
		nameText.setRows( 3 );
		nameText.setImmediate( true );

		
		catSelect = new ComboBox();
		catSelect.setInputPrompt( "Select Category" );
		catSelect.setFilteringMode( FilteringMode.CONTAINS );
		catSelect.setNullSelectionAllowed( false );
		catSelect.setInvalidAllowed( false );
		catSelect.setTextInputAllowed( true );
		catSelect.setNewItemsAllowed( false );
		catSelect.setImmediate( true );
		
		quantity = new IntegerField();
		quantity.setNullSettingAllowed( false );
		quantity.setNullRepresentation( "1" );
		quantity.setWidth( "3em" );
		
		barcode = new TextField();
		barcode.setInputPrompt( "Set barcode..." );
		barcode.setNullRepresentation( "" );
		barcode.setImmediate( true );

		statusBox = new ComboBox();
		statusBox.setInputPrompt( "Select Status..." );
		statusBox.setFilteringMode( FilteringMode.CONTAINS );
		statusBox.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		statusBox.setNullSelectionAllowed( false );
		statusBox.setInvalidAllowed( false );
		statusBox.setImmediate( true );
		
		currentUser = new ComboBox();
		currentUser.setInputPrompt( "Select user..." );
		currentUser.setFilteringMode( FilteringMode.CONTAINS );
		currentUser.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		currentUser.setNullSelectionAllowed( false );
		currentUser.setInvalidAllowed( false );
		currentUser.setImmediate( true );

		personalFlag = new CheckBox();
		personalFlag.setImmediate( true );
		
		serialNumber = new TextField();
		serialNumber.setInputPrompt( "Set serial number ..." );
		serialNumber.setNullRepresentation( "" );
		serialNumber.setImmediate( true );
		
		buyDate = new PopupDateField();
		buyDate.setDateFormat( "dd.MM.yyyy" );
		
		nextMaintenance = new PopupDateField();
		nextMaintenance.setDateFormat( "MM.yyyy" );
		nextMaintenance.setResolution( Resolution.MONTH);
		
		price = new DoubleField();
		price.setLocale( model.getApp().getSessionData().getLocale());
		price.setMinValue( 0. );		
		
		takuu = new IntegerField();
		takuu.setupMaxValue( 120 );

		comments = new TextArea();
		comments.setNullRepresentation( "" );
		comments.setRows( 3 );
		comments.setImmediate( true );
		
		subContent.addField( "Edit Tool model:", editToolFlag );
		subContent.addField( "Manufacturer:", manufSelect );
		subContent.addField( "Model:", modelSelect );
		subContent.addField( "Name:", nameText );
		subContent.addSeparator();
	
		subContent.addField( "Category:", catSelect );
		
		subContent.addSeparator();

		subContent.addField( "Quantity:", quantity );
		subContent.addField( "Barcode:", barcode );
		subContent.addField( "Status:", statusBox );
		subContent.addField( "User:", currentUser );
		subContent.addField( "Personal tool?", personalFlag );
		subContent.addField( "Serial number:", serialNumber );
		subContent.addField( "Bought:", buyDate );
		subContent.addField( "Price:", price );
		subContent.addField( "Takuu (mm):", takuu );
		subContent.addField( "Next Maintenance:", nextMaintenance );
		subContent.addField( "Comment:", comments );
		
		
		
		VerticalLayout vl = new VerticalLayout();
		
		setContent( vl );
		
		vl.addComponent( subContent );
		
		subContent.addSeparator();

	
		vl.addComponent( getButtonBar());
		
		dataToView();
		
		getChangesCollector().addField( manufSelect );
		getChangesCollector().addField( modelSelect );
		getChangesCollector().addField( nameText );
		getChangesCollector().addField( catSelect );
		getChangesCollector().addField( quantity );
		getChangesCollector().addField( barcode );
		getChangesCollector().addField( statusBox );
		getChangesCollector().addField( currentUser );
		getChangesCollector().addField( personalFlag );
		getChangesCollector().addField( serialNumber );
		getChangesCollector().addField( buyDate );
		getChangesCollector().addField( price );
		getChangesCollector().addField( takuu );
		getChangesCollector().addField( nextMaintenance );
		getChangesCollector().addField( comments );
		
		updateFields();
		
//		updateFields( true );

		addToolFieldsListeners();
		
	}

	private String getHeader() {
		
		String str;
		
		switch ( model.getEditMode()) {
			case ADD:
				str = "Add Tool";
				break;
			case COPY:
				str = "Copy Tool";
				break;
			case EDIT:
				str = "Edit Tool";
				break;
			case VIEW:
			default:
				str = "View Tool";
				break;
		}

		return str;
	}

	private void addToolFieldsListeners() {
		
		editToolFlag.addValueChangeListener( new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				
				updateFields();
			}
			
		});
		manufSelect.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {

				Object selectedValue = manufSelect.getValue();
				
				if ( selectedValue != null ) {
					if ( selectedValue instanceof Manufacturer ) {
						logger.debug( "Manufacturer selected: " + selectedValue );
						
						manufacturerChanged(( Manufacturer ) selectedValue );
						
					} else if ( selectedValue instanceof String ) {
						logger.debug( "New Manufacturer entered. Need to add '" + selectedValue + "' manufacturer" );
						
					} else {
						logger.error( "Value returned by selection is wrong. Type: " + selectedValue.getClass().getSimpleName() );
					}
					
					
				}

				
			}
			
		});
		
		manufSelect.setNewItemHandler( new NewItemHandler() {
			private static final long serialVersionUID = 1L;

			@Override
			public void addNewItem( String newManName ) {

				// New Manufacturer shall be added
				Manufacturer newMan =  model.addManufacturer( newManName );
				
				if ( newMan != null ) {
					addOrUpdateManufacturer( newMan, true );
				}
				
			}
			
		});
		
		modelSelect.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {

				Object selectedValue = modelSelect.getValue();
				if ( selectedValue != null ) {
					
					if ( selectedValue instanceof Tool ) {
						logger.debug( "Model selected: " + selectedValue );

						modelChanged(( Tool ) selectedValue );
						
					} else if ( selectedValue instanceof String ) {
						logger.debug( "New Model entered. Need to add '" + selectedValue + "' this model" );
						
					} else {
						logger.error( "Value returned by selection is wrong. Type: " + selectedValue.getClass().getSimpleName() );
					}
					
					
				}

			}
			
		});
		

		modelSelect.setNewItemHandler( new NewItemHandler() {
			private static final long serialVersionUID = 1L;

			@Override
			public void addNewItem( String newModel ) {
				// New Manufacturer shall be added
				Tool newTool = new Tool( model.getSelectedOrg());
				newTool.setManufacturer(( Manufacturer )manufSelect.getValue());
				newTool.setModel( newModel );
				
				modelSelect.addItem( newTool );
				modelSelect.setItemCaption( newTool, newTool.getModel());
				modelSelect.setValue( newTool );
				
			}
			
		});
				
		nameText.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {

				Object selectedValue = nameText.getValue();
				if ( selectedValue != null && selectedValue instanceof String ) {
					logger.debug( "New Tool Name entered." );
					
					Tool tool = ( Tool )modelSelect.getValue();
					if ( tool != null ) {
						tool.setName(( String )selectedValue );
					}
						
				}

			}
			
		});
		
		
		catSelect.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {

				Object selectedValue = catSelect.getValue();
				
				if ( selectedValue != null ) {
					if ( selectedValue instanceof Category ) {
						logger.debug( "Category selected: " + selectedValue ); 
					} else if ( selectedValue instanceof String ) {
						logger.debug( "New Category entered. Need to add '" + selectedValue + "' Category" );
						
					} else {
						logger.error( "Value returned by selection is wrong. Type: " + selectedValue.getClass().getSimpleName() );
					}
				}
				
			}
			
		});
		
	}
	
	private void dataToView() {

		Tool tool = null;
		
		if ( model.getSelectedItem() != null ) {
			tool = model.getSelectedItem().getTool();
			
		}
/*
		if ( tool == null ) {
			logger.error( "TOOL is null for ToolItem passed to ToolItemDlg!" );
			return;
		}
*/
		initManufacturers( tool );
		initModels( tool );
		initCategories( tool );
		initUsers();
		initStatuses();
		
		if ( model.getSelectedItem() != null ) {
			quantity.setValue( model.getSelectedItem().getQuantity());
			barcode.setValue( model.getSelectedItem().getBarcode());
			statusBox.setValue( model.getSelectedItem().getStatus());

			currentUser.setValue( model.getSelectedItem().getCurrentUser());
			personalFlag.setValue( model.getSelectedItem().isPersonalFlag());
			serialNumber.setValue( model.getSelectedItem().getSerialNumber());
			buyDate.setValue( model.getSelectedItem().getBuyTime() != null ? 
								model.getSelectedItem().getBuyTime().toDate() : null );
			price.setValue( model.getSelectedItem().getPrice());
			takuu.setValue( model.getSelectedItem().getTakuu());
			nextMaintenance.setValue( model.getSelectedItem().getMaintenance() != null ? model.getSelectedItem().getMaintenance().toDate() : null );
			comments.setValue( model.getSelectedItem().getSerialNumber());
			
		}
		
		
			
	}


	private boolean viewToData() {

		boolean res = false;
		
		if ( model.getSelectedItem() != null ) {
/*			
			if ( validateAll()) {
				if ( model.getEditMode() == ToolsListModel.EditModeType.ADD ) {
					
					item.getTool().setCode( code.getValue());
					item.getTool().setName( toolName.getValue());
					item.getTool().setManufacturer(( Manufacturer ) mnftrSelect.getValue() );
					item.getTool().setModel( toolModel.getValue());
					item.getTool().setToolInfo( toolInfo.getValue());
					item.getTool().setCategory(( Category ) categorySelect.getValue());
					
				} else if ( model.getEditMode() == ToolsListModel.EditModeType.EDIT ) {
					
					item.getTool().setToolInfo( toolInfo.getValue());
					item.getTool().setCategory(( Category ) categorySelect.getValue());
					
				}
	
				item.setPersonalFlag( personalFlag.getValue());
				
				item.setCurrentUser(( OrgUser )currentUser.getValue());
				item.setStatus(( ItemStatus ) status.getValue());
				item.setReservedBy(( OrgUser )reservedBy.getValue());
				
				item.setSerialNumber( serialNumber.getValue());
				item.setBarcode( barcode.getValue());
				item.setComments( comments.getValue());
				
				item.setBuyTime( buyDate.getValue());
				item.setMaintenance( nextMaintenance.getValue());
				item.setPrice( price.getDoubleValueNoException());
				item.setTakuu( takuu.getIntegerValueNoException());

				
				res = true;
			}
*/			
		}

		return res;
	}

	private void updateFields() {
		updateFields( false );
	}
	
	private void updateFields( boolean initial ) {
		
		boolean editable = false;
		if ( initial ) {
			editable = model.getEditMode() == EditModeType.ADD;
			editToolFlag.setValue( editable );
			editToolFlag.setEnabled( !editable );
			
			
		} else {
			// Update fields status during interactions
			editable = editToolFlag.getValue();
			
		}

		manufSelect.setEnabled( editable );
		modelSelect.setEnabled( editable );
		nameText.setEnabled( editable );
		catSelect.setEnabled( editable );
		
	}

	private void initManufacturers( Tool tool ) {
		
		Manufacturer manufacturer = ( tool != null ? tool.getManufacturer() : null );
		
		if ( model.getEditMode() == EditModeType.ADD ) {
			
			for ( Manufacturer tmpMan : model.getManufacturers()) {
				
				addOrUpdateManufacturer( tmpMan );
			}
			
			
		} else if ( model.getEditMode() == EditModeType.EDIT ) {
 
			for ( Manufacturer tmpMan : model.getManufacturers()) {
				
				addOrUpdateManufacturer( tmpMan );
			}
			// Select Tool Manufacturer
			if ( manufacturer != null ) manufSelect.setValue( manufacturer );
		
		} else {
			
			if ( manufacturer != null ) { 
				addOrUpdateManufacturer( manufacturer, true );
				manufSelect.setReadOnly( true );
			}

		}
	}
	
	private void addOrUpdateManufacturer( Manufacturer man ) {
		addOrUpdateManufacturer( man, false );
	}
	private void addOrUpdateManufacturer( Manufacturer man, boolean selected ) {

		Item item = manufSelect.getItem( man );
		
		if ( item == null ) {
			// Item NOT found. shall be added
			manufSelect.addItem( man );
		}
		manufSelect.setItemCaption( man, man.getName());
		
		if ( selected ) 
			manufSelect.setValue( man );
		
	}

	private void manufacturerChanged( Manufacturer manuf ) {
		
		if ( manuf != null ) {

			modelSelect.removeAllItems();
			nameText.setValue( "" );
			catSelect.removeAllItems();
		
			initModels( manuf );
				
		}
	}
	
	
	private void initModels( Tool tool ) {
		
		if ( tool != null ) {

			initModels( tool != null ? tool.getManufacturer() : null );
			modelSelect.setValue( tool );

		}
	}
	private void initModels( Manufacturer manuf ) {
		
		List<Tool> toolsList;
		
		if ( manuf != null ) {

			toolsList = model.getTools( manuf );

			modelSelect.removeAllItems();
			
			if ( toolsList != null && toolsList.size() > 0 ) {
			
				for ( Tool tmpTool : toolsList ) {
					
					// Update Tools Combo
					modelSelect.addItem( tmpTool );
					modelSelect.setItemCaption( tmpTool, tmpTool.getModel());
				
/*					
					// Update Category combo
					if ( catSelect.getItem( tmpTool.getCategory()) == null ) {
						catSelect.addItem( tmpTool.getCategory() );
						catSelect.setItemCaption( tmpTool.getCategory(), tmpTool.getCategory().getName());
					}
*/					
					
				}
				
//				modelSelect.setValue( modelSelect.getItemIds().iterator().next());
		
//				catSelect.setValue( catSelect.getItemIds().iterator().next());
				
			}
			
		}

	}

	private void modelChanged( Tool tool ) {

		nameText.setValue( StringUtils.defaultString( tool.getName()));
		
	}
	
	
	private void initCategories( Tool tool ) {
		
		for ( Category cat : model.getCategories()) {
			
			addOrUpdateCategory( cat );
			
		}
		if ( tool != null && tool.getCategory() != null ) {
		
			catSelect.setValue( tool.getCategory());
			
		}
		
	}
	
	private void addOrUpdateCategory( Category cat ) {
		addOrUpdateCategory( cat, false );
	}
	private void addOrUpdateCategory( Category cat, boolean topCategory ) {

		String caption = "";
		
		if ( cat != null ) {
			
			if ( catSelect.getItem( cat ) == null ) {
				
				catSelect.addItem( cat );
				catSelect.setItemCaption( cat, cat.getName());
				
			}
			
		}
		
	}

	private void initUsers() {
		
		for ( OrgUser u : model.getUsers()) {
			
			addOrUpdateUser( u );

		}
		
	}

	private void addOrUpdateUser( OrgUser user ) {

		Item item = currentUser.getItem( user );
		
		if ( item == null ) {
			// Item NOT found. shall be added
			currentUser.addItem( user );
		}
		
		currentUser.setItemCaption( user, user.getLastAndFirstNames());
		
	}

	private void initStatuses() {

		boolean freeAllowed = SettingsFacade.getInstance().getBoolean( model.getSelectedOrg(), "FreeStatusAllowed", false );
		
		for ( ItemStatus status: ItemStatus.values()) {

			if ( !freeAllowed && status != ItemStatus.FREE ) {
				// Do not add if free status is not allowed
				continue;
			}
			statusBox.addItem( status );
			// TODO  take from resources
			statusBox.setItemCaption( status, status.toString( model.getApp().getSessionData().getBundle()));

		}
		
	}
	
	@Override
	public void okPressed() {

		switch ( model.getEditMode()) {
			case ADD:

				if ( viewToData()) {
				
					if ( model.addToolAndItem() != null ) {
						logger.debug( "Tool And Item were added" );
						close();
					}
				}
				
				break;
			case COPY:
				if ( viewToData()) {
				
					if ( model.addItem() != null ) {
						logger.debug( "Item was added to existing Tool" );
						close();
					}
				}
			
				break;
			case EDIT:
				
				if ( viewToData()) {
				
					if ( model.updateItem() != null ) {
						logger.debug( "Item was edited" );
						close();
					}
	
				}
				
				break;
			default:
				break;
		}

		
	}

	@Override
	public void cancelPressed() {

		close();
		
	}

	@Override
	public void dlgClosed() {

		logger.debug( "ToolsEdit Dialog has been closed!" );
		
	}

	private void setTool( Tool tool ) {

		if ( tool != null ) {
			// Update selected item
			model.getSelectedItem().setTool( tool );
			logger.debug( "New Tool was set for Item: " + tool );
			// Show Tool data
			dataToViewTool();
		}
		
	}

	private void dataToViewTool() {
/*
		private Manufacturer	manufacturer;
		private String 		model;
		private String		name;
		
		private Category 	category;
*/		
	}
	
}
