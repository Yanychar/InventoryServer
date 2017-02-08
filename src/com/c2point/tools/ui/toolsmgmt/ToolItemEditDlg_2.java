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

public class ToolItemEditDlg_2 extends AbstractDialog {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( ToolItemEditDlg_2.class.getName());

	private ToolsListModel		model;
	
	/* New variant of Tool selection */
	private	CheckBox		editToolFlag;
	// Tool fields
	private ComboBox		manufSelect;
	private ComboBox		modelSelect;
	private TextArea		nameText;
	private ComboBox		catSelect;

	public ToolItemEditDlg_2( ToolsListModel model, EditModeType editModeType ) {
		super();
		
		this.model = model;
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
		
		subContent.addField( "Edit Tool model:", editToolFlag );
		subContent.addField( "Manufacturer:", manufSelect );
		subContent.addField( "Model:", modelSelect );
		subContent.addField( "Name:", nameText );
		subContent.addSeparator();
	
		subContent.addField( "Category:", catSelect );
		
		subContent.addSeparator();

		
		
		
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
		
		// Update Tools editable flag
		canToolBeEdited( model.getEditMode() == EditModeType.ADD );
		
		// Add field changes listener
		
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
				
				canToolBeEdited();
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

		initManufacturers( tool );
		initModels( tool );
		initCategories( tool );
		
	}


	private boolean viewToData() {

		boolean res = false;

		return res;
	}

	private void canToolBeEdited() {
	
		// Swith editable flag. Not initial state
		canToolBeEdited( editToolFlag.getValue(), false );
	}
	private void canToolBeEdited( boolean canBeEdited ) {
		
		// Initially set up flag
		canToolBeEdited( canBeEdited, true );
	}
	private void canToolBeEdited( boolean canBeEdited, boolean initial ) {
		
		if ( initial ) {
			// If initially allowed to edit than cannot be changed
			editToolFlag.setValue( canBeEdited );
			editToolFlag.setEnabled( !canBeEdited );
			
			
		} 

		manufSelect.setEnabled( canBeEdited );
		modelSelect.setEnabled( canBeEdited );
		nameText.setEnabled( canBeEdited );
		catSelect.setEnabled( canBeEdited );
		
	}

	private void initManufacturers( Tool tool ) {
		
		Manufacturer manufacturer = ( tool != null ? tool.getManufacturer() : null );
		
		boolean selectMan = model.getEditMode() != EditModeType.ADD;
		switch ( model.getEditMode()) {
			case ADD:
			case COPY:
			case EDIT:
				// Add manufacturers and select specified if possible
				for ( Manufacturer tmpMan : model.getManufacturers()) {
					
					addOrUpdateManufacturer( tmpMan, manufacturer != null && selectMan && tmpMan.getId() == manufacturer.getId());
				}
				
				// Select Tool Manufacturer
//				if ( manufacturer != null ) manufSelect.setValue( manufacturer );
				break;
			case VIEW:
			default:
				// Select Tool Manufacturer
				if ( manufacturer != null ) {
					addOrUpdateManufacturer( manufacturer, true );
				}
				
				// Disable editing
				manufSelect.setReadOnly( true );
				break;
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

			initModels( tool.getManufacturer());
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
