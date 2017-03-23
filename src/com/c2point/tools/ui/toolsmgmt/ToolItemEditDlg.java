package com.c2point.tools.ui.toolsmgmt;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.SettingsFacade;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ItemStatus;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Category;
import com.c2point.tools.entity.tool.Manufacturer;
import com.c2point.tools.entity.tool.Tool;
import com.c2point.tools.ui.util.AbstractDialog;
import com.c2point.tools.ui.util.CustomGridLayout;
import com.c2point.tools.ui.util.DoubleField;
import com.c2point.tools.ui.util.IntegerField;
import com.c2point.tools.ui.util.AbstractModel.EditModeType;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.AbstractSelect.NewItemHandler;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.VerticalLayout;

public class ToolItemEditDlg extends AbstractDialog {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( ToolItemEditDlg.class.getName());

	private ToolsListModel	model;
	
	private Tool			editedTool = null;
	private ToolItem		editedItem = null;
	
	/* New variant of Tool selection */
	private	CheckBox		editToolFlag;
	/* Tool fields	*/
	private ComboBox		manufSelect;
	private ComboBox		modelSelect;
	private TextArea		nameText;
	private ComboBox		catSelect;

	/*	ToolItem fields	*/
	private CheckBox		personalFlag;
	private ComboBox		currentUser;
	private ComboBox		statusBox;

	private TextField		barcode;
	private TextField		serialNumber;
	
	private PopupDateField	buyDate;
	private PopupDateField	nextMaintenance;
	private DoubleField		price;
	private IntegerField	takuu;

	private TextArea 		comments;
	
	public ToolItemEditDlg( ToolsListModel model, ToolItem item, EditModeType editModeType ) {
		super();
		
		this.model = model;
		this.model.setEditMode( editModeType );
			
		this.editedItem = item;
		if ( item != null &&item.getTool() != null ) {
			this.editedTool = item.getTool().copy();
		}
		
		initUI();
	}

	private void initUI() {
		
		if ( this.editedItem == null ) {
			logger.error( "No ToolItem selected!" );
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
		modelSelect.setNullSelectionAllowed( true );
		modelSelect.setInvalidAllowed( false );
		modelSelect.setTextInputAllowed( true );
		modelSelect.setNewItemsAllowed( true );
		modelSelect.setImmediate( true );
		
		nameText = new TextArea();
		nameText.setInputPrompt( "Write name of the Tool..." );
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

		personalFlag = new CheckBox();
		personalFlag.setImmediate( true );

		currentUser = new ComboBox();
		currentUser.setInputPrompt( "Select user..." );
		currentUser.setFilteringMode( FilteringMode.CONTAINS );
		currentUser.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		currentUser.setNullSelectionAllowed( false );
		currentUser.setInvalidAllowed( false );
		currentUser.setImmediate( true );

		statusBox = new ComboBox();
		statusBox.setInputPrompt( "Select Status..." );
		statusBox.setFilteringMode( FilteringMode.CONTAINS );
		statusBox.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		statusBox.setNullSelectionAllowed( false );
		statusBox.setInvalidAllowed( false );
		statusBox.setImmediate( true );
		
		barcode = new TextField();
		barcode.setInputPrompt( "Set barcode..." );
		barcode.setNullRepresentation( "" );
		barcode.setImmediate( true );

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
		
		
		
		
		
		if ( /*model.getEditMode() == EditModeType.ADD || */model.getEditMode() == EditModeType.EDIT ) {
			subContent.addField( "Edit Tool model:", editToolFlag );
		}
		subContent.addField( "Manufacturer:", manufSelect );
		subContent.addField( "Model:", modelSelect );
		subContent.addField( "Name:", nameText );
		subContent.addField( "Category:", catSelect );
		subContent.addSeparator();

		subContent.addField( "Status:", statusBox );
		subContent.addField( "User:", currentUser );
		subContent.addField( "Barcode:", barcode );
		subContent.addSeparator();

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
		
		getChangesCollector( 1 ).addField( manufSelect );
		getChangesCollector( 1 ).addField( modelSelect );
		getChangesCollector( 1 ).addField( nameText );
		getChangesCollector( 1 ).addField( catSelect );

		getChangesCollector().addField( manufSelect );
		getChangesCollector().addField( modelSelect );
		getChangesCollector().addField( nameText );
		getChangesCollector().addField( catSelect );

		getChangesCollector().addField( statusBox );
		getChangesCollector().addField( currentUser );
		getChangesCollector().addField( barcode );
		getChangesCollector().addField( personalFlag );
		getChangesCollector().addField( serialNumber );
		getChangesCollector().addField( buyDate );
		getChangesCollector().addField( price );
		getChangesCollector().addField( takuu );
		getChangesCollector().addField( nextMaintenance );
		getChangesCollector().addField( comments );
		
		/*	Update Tools editable flag	*/
		updateFields();
		
		/*	Add field changes listener	*/
		
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
			private static final long serialVersionUID = 1L;

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
						logger.debug( "Manufacturer selected: " + ( Manufacturer )selectedValue );
						
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
				
				newManName = StringUtils.capitalize( newManName );
				/*	New Manufacturer shall be added	*/
				Manufacturer newMan =  new Manufacturer( newManName );
				
				if ( newMan != null && addManufacturer( newMan )) {
					/*	Create new Tool for new Manufacturer	*/
					editedTool = getNewTool();
					editedTool.setManufacturer( newMan );

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
	
				addModel( newTool );
				
			}
			
		});
				
		nameText.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {

				Object selectedValue = nameText.getValue();
				if ( selectedValue != null && selectedValue instanceof String ) {
					logger.debug( "New Tool Name entered." );
					
				if ( editedTool != null ) {
						editedTool.setName(( String )selectedValue );
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
						editedTool.setCategory(( Category )selectedValue );
					} else if ( selectedValue instanceof String ) {
						logger.debug( "New Category entered. Need to add '" + selectedValue + "' Category" );

						editedTool.setCategory(( Category )selectedValue );
						
						
					} else {
						logger.error( "Value returned by selection is wrong. Type: " + selectedValue.getClass().getSimpleName() );
					}
				}
				
			}
			
		});
		
	}
	
	private void dataToView() {

		if ( editedItem != null ) {
			
			editToolFlag.setValue( model.getEditMode() == EditModeType.ADD );
			
			initManufacturers( editedTool );
			initModels( editedTool );
			initCategories( editedTool );
			
			if ( editedTool != null ) {
				
				nameText.setValue( 
						StringUtils.defaultString( editedTool.getName())
					+ "\n"
					+ StringUtils.defaultString( editedTool.getToolInfo())
				);
			}

			/*	ToolItem attributes	*/
			if ( editedItem != null ) {
				barcode.setValue( StringUtils.defaultString( editedItem.getBarcode()));
				initStatuses( editedItem.getStatus());
				initUsers( editedItem.getCurrentUser());
				personalFlag.setValue( editedItem.isPersonalFlag());
				serialNumber.setValue( StringUtils.defaultString( editedItem.getSerialNumber()));
				buyDate.setValue( editedItem.getBuyTime() != null ? 
									editedItem.getBuyTime().toDate() : null );
				price.setValue( editedItem.getPrice());
				takuu.setValue( editedItem.getTakuu());
				nextMaintenance.setValue( editedItem.getMaintenance() != null ? model.getSelectedItem().getMaintenance().toDate() : null );
				comments.setValue( StringUtils.defaultString( editedItem.getComments()));
			}
			
		}
	}


	private boolean viewToData() {

		boolean res = false;
		if ( getChangesCollector( 1 ).wasItChanged()) {
			
			if ( editedItem.getTool() == null || editedTool.getId() <=0 ) {
				/*	This is new Tool. Can be added */
				editedItem.setTool( editedTool );
			} else if ( editedTool.getId() != editedItem.getTool().getId()){
				/*	Other existing Tool was been selected	*/
				editedItem.setTool( editedTool );
			} else {
				/*	Tool was changed (Name, Cat or similar)	*/
				editedItem.getTool().setManufacturer( editedTool.getManufacturer());
				editedItem.getTool().setModel( editedTool.getModel());
				editedItem.getTool().setName( editedTool.getName());
				editedItem.getTool().setToolInfo( editedTool.getToolInfo());
				editedItem.getTool().setCategory( editedTool.getCategory());
			}
		}


		editedItem.setBarcode( barcode.getValue());
		editedItem.setStatus(( ItemStatus ) statusBox.getValue());
		editedItem.setCurrentUser(( OrgUser )currentUser.getValue());
		
		editedItem.setPersonalFlag( personalFlag.getValue());
		editedItem.setSerialNumber( serialNumber.getValue());
		editedItem.setBuyTime( buyDate.getValue());
		editedItem.setMaintenance( nextMaintenance.getValue());
		editedItem.setPrice( price.getDoubleValueNoException());
		editedItem.setTakuu( takuu.getIntegerValueNoException());
		editedItem.setComments( comments.getValue());
		
		res = true;
		

		return res;
	}

	private void updateFields() {
		
		boolean disallowEdit = ( model.getEditMode() == EditModeType.COPY || !editToolFlag.getValue());
		
		manufSelect.setReadOnly( disallowEdit );
		modelSelect.setReadOnly( disallowEdit );
		nameText.setReadOnly( disallowEdit );
		catSelect.setReadOnly( disallowEdit );
		
		disallowEdit = ( model.getEditMode() == EditModeType.VIEW );
		
		personalFlag.setReadOnly( disallowEdit );
		currentUser.setReadOnly( disallowEdit );
		statusBox.setReadOnly( disallowEdit );
		barcode.setReadOnly( disallowEdit );
		serialNumber.setReadOnly( disallowEdit );
		buyDate.setReadOnly( disallowEdit );
		nextMaintenance.setReadOnly( disallowEdit );
		price.setReadOnly( disallowEdit );
		takuu.setReadOnly( disallowEdit );
		comments.setReadOnly( disallowEdit );
		
	}
	
	
	private void initManufacturers( Tool tool ) {
		
		Manufacturer manufacturer = ( tool != null ? tool.getManufacturer() : null );
		
		manufSelect.removeAllItems();
		
		switch ( model.getEditMode()) {
			case ADD:
			case COPY:
			case EDIT:
				// Add manufacturers and select specified if possible

				Collection<Manufacturer> mansList = model.getManufacturers();

				if ( mansList != null && mansList.size() > 0 ) {
				
					BeanItemContainer<Manufacturer> mansContainer = new BeanItemContainer<>( Manufacturer.class );
					
					mansContainer.addAll( mansList );
					mansContainer.sort(new Object[] { "name" }, new boolean[] { true });
					manufSelect.setContainerDataSource( mansContainer );
					manufSelect.setItemCaptionPropertyId( "name" );
					
				}
				
				// Select Manufacturer if it is not null
				if ( manufacturer != null ) {
					manufSelect.setValue( manufacturer );
				}
				break;

			case VIEW:
			default:
	
				// Show and Select Tool Manufacturer
				if ( manufacturer != null ) {
					manufSelect.addItem( manufacturer );
					manufSelect.setItemCaption( manufacturer, StringUtils.defaultString( manufacturer.getName()));
	
					manufSelect.setValue( manufacturer );
				}
				
				break;
		}
		
	}

	private boolean addManufacturer( Manufacturer man ) {
		
		boolean res = false;
		
		if ( man != null ) { 
			// Add Manufacturer to the ComboBox
			@SuppressWarnings("unchecked")
			BeanItemContainer<Manufacturer> mansContainer = ( BeanItemContainer<Manufacturer> ) manufSelect.getContainerDataSource();
			
			mansContainer.addBean( man );

			mansContainer.sort(new Object[] { "name" }, new boolean[] { true });
			// Select it
			manufSelect.setValue( man );
			
			logger.debug( "Manufacturer '" + man + "' was added" );

			res = true;
		}
		
		return res;
	}

	private void manufacturerChanged( Manufacturer manuf ) {
		
		if ( manuf != null ) {

			logger.debug( "Manufacturer was changed to '" + manuf + "'" );
			
			nameText.setValue( "" );
			modelSelect.setValue( null );
			catSelect.setValue( null );
		
			initModels( manuf );
				
		}
	}
	
	
	private void initModels( Tool tool ) {
		
		BeanItemContainer<Tool> modelContainer = new BeanItemContainer<>( Tool.class );
		modelSelect.setContainerDataSource( modelContainer );
		modelSelect.setItemCaptionPropertyId( "model" );

		if ( tool != null ) {

			switch ( model.getEditMode()) {
				case ADD:
				case COPY:
				case EDIT:

					// Add Tools for specified manufacturer and select specified if possible
					initModels( tool.getManufacturer());
					modelSelect.setValue( tool );
					
					break;
				case VIEW:
				default:
					// Show and select Tool
					
					addModel( tool );

					break;
			}
		} else {
			
		}
	}
	private void initModels( Manufacturer manuf ) {
		
		List<Tool> toolsList;
		
		modelSelect.removeAllItems();
		if ( manuf != null ) {


			if ( manuf.getId() > 0 ) {
				// Manufacturer exists in DB already ==>> Possible to read models
				toolsList = model.getTools( manuf );
	
				if ( toolsList != null && toolsList.size() > 0 ) {
					@SuppressWarnings("unchecked")
					BeanItemContainer<Tool> modelContainer = ( BeanItemContainer<Tool> ) modelSelect.getContainerDataSource();
				
					modelContainer.addAll( toolsList );
					modelContainer.sort(new Object[] { "model" }, new boolean[] { true });
					
				}				
			} else {

				//modelSelect.setValue( null );
				
			}
			
		}
		
	}

	private void addModel( Tool tool ) {

		@SuppressWarnings("unchecked")
		BeanItemContainer<Tool> modelContainer = ( BeanItemContainer<Tool> ) modelSelect.getContainerDataSource();
		
		modelContainer.addBean( tool );
		modelContainer.sort(new Object[] { "model" }, new boolean[] { true });	
		
		if ( tool != null ) 
			modelSelect.setValue( tool );
		
	}

	
	private void modelChanged( Tool tool ) {

		editedTool = tool;
		nameText.setValue( StringUtils.defaultString( editedTool.getName()));
		catSelect.setValue( editedTool.getCategory());
		
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

		if ( cat != null ) {
			
			if ( catSelect.getItem( cat ) == null ) {
				
				catSelect.addItem( cat );
				catSelect.setItemCaption( cat, cat.getName());
				
			}
			
		}
		
	}

	private void initStatuses( ItemStatus status ) {
		boolean freeAllowed = SettingsFacade.getInstance().getBoolean( model.getSelectedOrg(), "FreeStatusAllowed", false );
		
		for ( ItemStatus tmpStatus: ItemStatus.values()) {

			if ( !freeAllowed && tmpStatus == ItemStatus.FREE ) {
				// Do not add if free status is not allowed
				continue;
			}
			statusBox.addItem( tmpStatus );
			
			statusBox.setItemCaption( tmpStatus, tmpStatus.toString( model.getApp().getSessionData().getBundle()));

		}
		
		// Select if specified
		if ( status != null ) {
			statusBox.setValue( status );
		}
		
	}
	@SuppressWarnings("unchecked")
	private void initUsers( OrgUser user ) {
/*		
		for ( OrgUser u : model.getUsers()) {
			
			addOrUpdateUser( u );

		}
*/
		Collection<OrgUser> usersList;
		currentUser.removeAllItems();
		
		usersList = model.getUsers();

		if ( usersList != null && usersList.size() > 0 ) {
		
			IndexedContainer usersContainer = new IndexedContainer();
//			usersContainer.addAll( usersList );
			usersContainer.addContainerProperty( "name", String.class, null);
			for ( OrgUser u : model.getUsers()) {
				
				usersContainer.addItem( u ).getItemProperty( "name" ).setValue( u.getLastAndFirstNames());;

			}
			
			
			usersContainer.sort(new Object[] { "name" }, new boolean[] { true });
			currentUser.setContainerDataSource( usersContainer );
			currentUser.setItemCaptionPropertyId( "name" );
			
		}
		
		if ( user != null ) {
			currentUser.setValue( user );
		}
		
		
		
		
	}

	@Override
	public void okPressed() {

		switch ( model.getEditMode()) {
			case ADD:

				if ( viewToData()) {
				
					if ( model.addToolAndItem( editedItem ) != null ) {
						logger.debug( "Tool And Item were added" );
						close();
					} else {
						String template = model.getApp().getResourceStr( "toolsmgmt.errors.item.add" );
						Object[] params = { editedItem.getFullName() };
						template = MessageFormat.format( template, params );
						
						new Notification( 
								model.getApp().getResourceStr( "general.error.header" ),
								template,
								Notification.Type.ERROR_MESSAGE, 
								true 
						).show( Page.getCurrent());
					}
				}
				
				break;
				
			case COPY:
				if ( viewToData()) {
				
					if ( model.addItem( editedItem ) != null ) {
						logger.debug( "Item was added to existing Tool" );
						close();
					} else {
						new Notification( 
								model.getApp().getResourceStr( "general.error.header" ),
								model.getApp().getResourceStr( "toolsmgmt.errors.item.add" ),
								Notification.Type.ERROR_MESSAGE, 
								true 
						).show( Page.getCurrent());
					}
				}
			
				break;
			case EDIT:
				
				if ( viewToData()) {
				
					if ( model.updateItem( editedItem ) != null ) {
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

	/*
	 * If Manufacturer or Model were added (not existed before) than new Tool shall be created 
	 */
	private Tool getNewTool() {
		
		Tool newTool = editedTool;
		
		if ( editedTool == null || editedTool.getId() > 0 ) {
			// New Tool shall be created
			newTool = new Tool( model.getSelectedOrg());
			
		}
		
		return newTool;
	}
}
