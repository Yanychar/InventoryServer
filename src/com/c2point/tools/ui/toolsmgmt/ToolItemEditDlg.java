package com.c2point.tools.ui.toolsmgmt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.SettingsFacade;
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
	private ToolItem 			item;
	private EditModeType		editModeType;
	
/*	
	private TextField 		toolName;
	
	private ComboBox		mnftrSelect;
	private TextField 		mnftrName;
	
	private TextField		toolModel;
	private TextArea 		toolInfo;
	
	private ComboBox		categorySelect;
	private TextField 		categoryName;

*/
	/* New variant of Tool selection */
	private StyledLabel		code;
	private ComboBox		toolSelect;
	private StyledLabel		toolName;
	private StyledLabel		categoryName;
	private TextArea 		descr;
	private Button			modTool;
	
	
	/* ... end of New variant of Tool selection */
	
	// Tool Item info

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
	
	
	public ToolItemEditDlg( ToolsListModel model, EditModeType editModeType ) {
		super();
		
		this.model = model;
		this.item = model.getSelectedItem();
		this.editModeType = editModeType;
			
		initUI();
	}

	private void initUI() {
		
		setCaption( getHeader());
		setModal( true );
		setClosable( true );

		CustomGridLayout subContent = new CustomGridLayout();
		subContent.setMargin( true );
		subContent.setSpacing( true );

		center();
		
		code = new StyledLabel( "b" );
		code.setImmediate( true );
		
		toolSelect = new ComboBox();
		toolSelect.setInputPrompt( "Select Tool" );
		toolSelect.setFilteringMode( FilteringMode.CONTAINS );
		toolSelect.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		toolSelect.setNullSelectionAllowed( true );
		toolSelect.setInvalidAllowed( false );
		toolSelect.setNewItemsAllowed( false );
		toolSelect.setImmediate( true );

 		toolName = new StyledLabel( "h2" );
		toolName.setImmediate( true );
 		

 		
		categoryName = new StyledLabel( "b" );
		
		categoryName.setImmediate( true );
		
		modTool = new Button( "Modify" );
		modTool.setImmediate( true );
		
		modTool.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				handleModifyTool();
			}
			
		});

		descr = new TextArea();
		descr.setNullRepresentation( "" );
		descr.setRows( 3 );
		descr.setReadOnly( true );
		descr.setImmediate( true );
		
		

/*		
		
		
		private Button			;
		private Button			editTool;
*/		
		
		
// Tool Item info
		
		personalFlag = new CheckBox();

		status = new ComboBox();
		status.setInputPrompt( model.getApp().getResourceStr( "toolsmgmt.text.select.status" ));
		status.setFilteringMode( FilteringMode.CONTAINS );
		status.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		status.setNullSelectionAllowed( false );
		status.setInvalidAllowed( false );
		status.setImmediate( true );
		
		currentUser = new ComboBox();
		currentUser.setInputPrompt( model.getApp().getResourceStr( "toolsmgmt.text.select.user" ));
		currentUser.setFilteringMode( FilteringMode.CONTAINS );
		currentUser.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		currentUser.setNullSelectionAllowed( false );
		currentUser.setInvalidAllowed( false );
		currentUser.setImmediate( true );

		reservedBy = new ComboBox();
		reservedBy.setInputPrompt( model.getApp().getResourceStr( "toolsmgmt.text.select.reservedby" ));
		reservedBy.setFilteringMode( FilteringMode.CONTAINS );
		reservedBy.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		reservedBy.setNullSelectionAllowed( true );
		reservedBy.setInvalidAllowed( false );
		reservedBy.setImmediate( true );

		serialNumber = new TextField();
		serialNumber.setNullRepresentation( "" );
		serialNumber.setImmediate( true );

		barcode = new TextField();
		barcode.setNullRepresentation( "" );
		barcode.setImmediate( true );

		comments = new TextArea();
		comments.setNullRepresentation( "" );
		comments.setRows( 3 );
		comments.setImmediate( true );
		
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

		HorizontalLayout hl = new HorizontalLayout(); 
		hl.setSpacing( true );
		if ( model.getEditMode() == EditModeType.ADD ) {
			hl.addComponent( toolSelect );
		} else {
			hl.addComponent( toolName );
			hl.setComponentAlignment( toolName, Alignment.TOP_LEFT);
		}
		hl.setSpacing( true );
		hl.addComponent( modTool );

		
		subContent.addField( model.getApp().getResourceStr( "toolsmgmt.view.label.code" ), code );
		subContent.addField( model.getApp().getResourceStr( "toolsmgmt.view.label.tool" ), hl );
		subContent.addField( model.getApp().getResourceStr( "toolsmgmt.view.label.category" ), categoryName );
		subContent.addField( model.getApp().getResourceStr( "toolsmgmt.view.label.toolinfo" ), descr );
		
		subContent.addSeparator();
		
		subContent.addField( model.getApp().getResourceStr( "toolsmgmt.view.label.personalflag" ), personalFlag );
		subContent.addField( model.getApp().getResourceStr( "toolsmgmt.view.label.status" ), status );
		subContent.addField( model.getApp().getResourceStr( "toolsmgmt.view.label.user" ), currentUser );
		subContent.addField( model.getApp().getResourceStr( "toolsmgmt.view.label.reservedby" ), reservedBy );
		
		subContent.addSeparator();
		
		subContent.addField( model.getApp().getResourceStr( "toolsmgmt.view.label.sn" ), serialNumber );
		subContent.addField( model.getApp().getResourceStr( "toolsmgmt.view.label.barcode" ) +":", barcode);
		subContent.addField( model.getApp().getResourceStr( "toolsmgmt.view.label.iteminfo" ), comments );

		subContent.addSeparator();
		
		subContent.addField( "Bought" + ":", buyDate );
		subContent.addField( "Price" + ":", price );
		subContent.addField( "Guarantee (months)" + ":", takuu );
		subContent.addField( "Next Maintenance" + ":", nextMaintenance );

		subContent.addSeparator();

		VerticalLayout vl = new VerticalLayout();
//		vl.setSizeFull();
		
		setContent( vl );
		
		vl.addComponent( subContent );
//		subContent.setSizeFull();
		
		vl.addComponent( getButtonBar());
		
		dataToView();
		

//		getChangesCollector().addField( code );
		getChangesCollector().addField( toolSelect );
//		getChangesCollector().addField( categoryName );
//		getChangesCollector().addField( descr );
		getChangesCollector().addField( personalFlag );
		getChangesCollector().addField( status );
		getChangesCollector().addField( currentUser );
		getChangesCollector().addField( reservedBy );
		getChangesCollector().addField( serialNumber );
		getChangesCollector().addField( barcode );
		getChangesCollector().addField( comments );
		getChangesCollector().addField( buyDate );
		getChangesCollector().addField( price );
		getChangesCollector().addField( takuu );
		getChangesCollector().addField( nextMaintenance );
		getChangesCollector().addField( buyDate );
		getChangesCollector().addField( price );
		getChangesCollector().addField( takuu );
		getChangesCollector().addField( nextMaintenance );
		
		updateFields();
		
	}

	private void dataToView() {

//		updateFields( true );
		
		setVisible( this.item != null );
	
		if ( this.item != null ) {
			
			Tool tool = this.item.getTool();
			if ( tool != null ) {
	
				if ( model.getEditMode() == EditModeType.ADD ) {
					initToolSelectComboBox( tool );
					
				} else {
					showToolInfo( tool );
				}
				
			}

			personalFlag.setValue( item.isPersonalFlag());
			
			initStatusComboBox( item.getStatus());
			
			initAllUserComboBoxes();

			currentUser.setValue(  item.getCurrentUser());
			reservedBy.setValue(  item.getReservedBy());

			
			serialNumber.setValue( item.getSerialNumber());
			barcode.setValue( item.getBarcode());
			comments.setValue( this.item.getComments());
			
			buyDate.setValue( item.getBuyTime() != null ? item.getBuyTime().toDate() : null );
			nextMaintenance.setValue( item.getMaintenance() != null ? item.getMaintenance().toDate() : null );
			price.setValue( item.getPrice());
			takuu.setValue( item.getTakuu());
			

		} else {
			if ( logger.isDebugEnabled()) logger.debug( "No selection. Dataview shall be cleared" );

			initToolSelectComboBox( null );
			showToolInfo( null );

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

	private boolean viewToData() {

		boolean res = false;
		
		if ( this.item != null ) {
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

	private boolean validateAll() {
		
		boolean res = 
				validate( buyDate )
				&& validate( nextMaintenance )
				&& validate( price )
				&& validate( takuu );
		
		return res;
	}
	
	private boolean validate( @SuppressWarnings("rawtypes") AbstractField field ) {
		boolean res = false;
		
		field.setValidationVisible( false );
		try {
			field.validate();
			res = true;
		} catch (InvalidValueException e) {
			field.setValidationVisible(true);
			if ( field instanceof AbstractTextField )
				(( AbstractTextField )field ).selectAll();
			else 
				field.focus();
		}			
		
		return res;
		
	}
	
	
	
	@Override
	public void okPressed() {

		switch ( model.getEditMode()) {
			case ADD:

				if ( viewToData()) {
				
					if ( model.addToolAndItem( item ) != null ) {
						logger.debug( "Tool And Item were added" );
						close();
					}
				}
				
				break;
			case COPY:
				if ( viewToData()) {
				
					if ( model.addItem( item ) != null ) {
						logger.debug( "Item was added to existing Tool" );
						close();
					}
				}
			
				break;
			case EDIT:
				
				if ( viewToData()) {
				
					if ( model.updateItem( item ) != null ) {
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

	private  void initToolSelectComboBox( Tool tool ) {
		
		switch ( editModeType ) {
			case ADD:
				break;
			case COPY:
			case EDIT:
			case VIEW:
				// Add one tool 
				// Select it
				// Set combo read only
				toolSelect.addItem( tool );
				toolSelect.setItemCaption( tool, tool.getFullName());
				toolSelect.setValue( tool );
				toolSelect.setReadOnly( true );
				
				break;
			default:
				break;
		}
		
		
	}
	
/*	
	private boolean manufacturerCBinited = false;
	private  void initManufacturerComboBox( Manufacturer selected ) {

		if ( !manufacturerCBinited ) {
			
			for ( Manufacturer m : model.getManufacturers()) {
				
				addManufacturerToCombo( m );
				
			}
	
			mnftrSelect.addValueChangeListener( new ValueChangeListener() {
				private static final long serialVersionUID = 1L;
	
				@Override
				public void valueChange( ValueChangeEvent event ) {
					
	               logger.debug( "Manufacturer combo value has been changed! Manufacturer: '" + (( Manufacturer )event.getProperty().getValue()).getName()+ "'" );
					
				}
				
			});

			manufacturerCBinited = true;
		}
		
		mnftrSelect.setValue( selected );
		
	}
	private void addManufacturerToCombo( Manufacturer m ) {
		
		mnftrSelect.addItem( m );
		mnftrSelect.setItemCaption( m, m.getName());
		
	}

	private boolean categoryCBinited = false;
	private  void initCategoryComboBox( Category selected ) {

		if ( !categoryCBinited ) {
			
			for ( Category m : model.getCategories()) {
				
				addCategoryToCombo( m );
				
			}
	
			categorySelect.addValueChangeListener( new ValueChangeListener() {
				private static final long serialVersionUID = 1L;
	
				@Override
				public void valueChange( ValueChangeEvent event ) {
					
	               logger.debug( "Category combo value has been changed! Category: '" + (( Category )event.getProperty().getValue()).getName()+ "'" );
					
				}
				
			});

			categoryCBinited = true;
		}
		
		categorySelect.setValue( selected );
		
	}
	private void addCategoryToCombo( Category m ) {
		
		categorySelect.addItem( m );
		categorySelect.setItemCaption( m, m.getName());
		
	}
*/
	private boolean statusCBinited = false;
	private  void initStatusComboBox( ItemStatus selectedStatus ) {

		if ( !statusCBinited ) {
			status.addItem( ItemStatus.UNKNOWN );
			
			boolean freeAllowed = SettingsFacade.getInstance().getBoolean( model.getSelectedOrg(), "FreeStatusAllowed", false );
			if ( freeAllowed ) {
				status.addItem( ItemStatus.FREE );
			}
			
			status.addItem( ItemStatus.INUSE );
			status.addItem( ItemStatus.BROKEN );
			status.addItem( ItemStatus.REPAIRING );
			status.addItem( ItemStatus.STOLEN );
			status.addItem( ItemStatus.RESERVED );
	
			status.setItemCaption( ItemStatus.UNKNOWN, ItemStatus.UNKNOWN.toString( model.getApp().getSessionData().getBundle()));
			if ( freeAllowed ) {
				status.setItemCaption( ItemStatus.FREE, ItemStatus.FREE.toString( model.getApp().getSessionData().getBundle()));
			}
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
	
	private boolean initedUserCombos = false;
	private  void initAllUserComboBoxes() {

		if ( !initedUserCombos ) {
			for ( OrgUser user : model.getUsers()) {
				
				currentUser.addItem( user );
				currentUser.setItemCaption( user, user.getLastAndFirstNames());
				
				reservedBy.addItem( user );
				reservedBy.setItemCaption( user, user.getLastAndFirstNames());
				
				
			}
		}
		
		initedUserCombos = true;
	
	}

	private void updateFields() {

			descr.setReadOnly( true );
			

			// Tool Item info
			personalFlag.setReadOnly( editModeType == EditModeType.VIEW );
			currentUser.setReadOnly( editModeType == EditModeType.VIEW );

			reservedBy.setReadOnly( editModeType == EditModeType.VIEW );
			status.setReadOnly( editModeType == EditModeType.VIEW );
			
			serialNumber.setReadOnly( editModeType == EditModeType.VIEW );
			barcode.setReadOnly( editModeType == EditModeType.VIEW );
			comments.setReadOnly( editModeType == EditModeType.VIEW );
			
			buyDate.setReadOnly( editModeType == EditModeType.VIEW );
			nextMaintenance.setReadOnly( editModeType == EditModeType.VIEW );
			price.setReadOnly( editModeType == EditModeType.VIEW );
			takuu.setReadOnly( editModeType == EditModeType.VIEW );
		
	}

	private String getHeader() {
		
		String str;
		
		switch ( editModeType ) {
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
				str = "View Tool";
				break;
			default:
				str = "Tool";
				break;
		}

		return str;
	}

	private void handleModifyTool() {

		Tool tool = ( Tool )toolSelect.getValue();

		switch ( editModeType ) {
			case ADD:
				// TODO: addTool();
				break;
			case COPY:
			case EDIT:
				// TODO: editTool();
				break;
		}
		
		ToolEditDlg editDlg = new ToolEditDlg( model, EditModeType.ADD );
		
		UI.getCurrent().addWindow( editDlg );
			
	}

	private void showToolInfo( Tool tool ) {

		categoryName.setReadOnly( false );
		descr.setReadOnly( false );
		
		if ( tool != null ) {

			code.setValue( tool.getCode());
			
			toolName.setValue( tool.getFullName());
			
			if ( tool.getCategory() != null )
				categoryName.setValue( tool.getCategory().getName());
			else 
				categoryName.setValue( "" );

			descr.setValue( tool.getToolInfo());
		} else {
			toolName.setValue( "" );
			code.setValue( "" );
			categoryName.setValue( "" );

			descr.setValue( "" );
		}

		code.setReadOnly( true );
		toolName.setReadOnly( true );
		categoryName.setReadOnly( true );
		descr.setReadOnly( true );
	
	}
	
}
