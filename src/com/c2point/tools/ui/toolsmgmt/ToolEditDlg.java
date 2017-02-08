package com.c2point.tools.ui.toolsmgmt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.InventoryUI;
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
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.VerticalLayout;

public class ToolEditDlg extends AbstractDialog {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( ToolEditDlg.class.getName());

	private Tool 				tool;
	private ToolsListModel		model;
	private EditModeType		editModeType;
	
	private TextField		name;
	private TextField		code;
	private ComboBox		manuf;
	private TextField		toolModel;
	
	private ComboBox		category;
	
	private TextArea 		description;
	
	public ToolEditDlg( ToolsListModel model, EditModeType editModeType ) {
		super();
		
		this.tool = model.getSelectedItem().getTool();
		this.model = model;
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
		
		code = new TextField();
		code.setImmediate( true );
		
 		name = new TextField();
		name.setImmediate( true );

		manuf = new ComboBox();
		manuf.setNewItemsAllowed( true );
		manuf.setNewItemHandler( new NewItemHandler() {
		    @Override
		    public void addNewItem( String newName ) {
		        // Create a new bean - can't set all properties
		        Manufacturer newManuf = new Manufacturer( newName );
		    	addManufacturerToCombo( newManuf );
		        manuf.select( newManuf );

		        logger.debug( "New manufacturer was added: " + newManuf );
		    }
		});
		manuf.setImmediate( true );
		
		
		toolModel = new TextField();
		toolModel.setImmediate( true );
		
		category = new ComboBox();
		category.setNewItemsAllowed( true );
		category.setNewItemHandler( new NewItemHandler() {
		    @Override
		    public void addNewItem( String newName ) {
		        // Create a new bean - can't set all properties
		    	Category newCat = new Category( newName );
		    	addCategoryToCombo( newCat, 1 );
		    	category.select( newCat );

		        logger.debug( "New category was added: " + newCat );
		    }
		});
		category.setImmediate( true );
		
		description = new TextArea();
		description.setNullRepresentation( "" );
		description.setRows( 3 );
//		description.setReadOnly( true );
		description.setImmediate( true );
		
		if ( SettingsFacade.getInstance().getBoolean( model.getSelectedOrg(), "allowToolCode", false )) {
			subContent.addField( model.getApp().getResourceStr( "toolsmgmt.view.label.code" ), code );
		}
		subContent.addField( model.getApp().getResourceStr( "toolsmgmt.view.label.category" ), category );
		subContent.addSeparator();
		subContent.addField( model.getApp().getResourceStr( "toolsmgmt.view.label.manufacturer" ), manuf );
		subContent.addField( model.getApp().getResourceStr( "toolsmgmt.view.label.model" ), toolModel );
		subContent.addField( model.getApp().getResourceStr( "toolsmgmt.view.label.tool" ), name );
		subContent.addField( model.getApp().getResourceStr( "toolsmgmt.view.label.toolinfo" ), description );
		
		subContent.addSeparator();
		

		VerticalLayout vl = new VerticalLayout();
		setContent( vl );
		
		vl.addComponent( subContent );
		vl.addComponent( getButtonBar());
		
		dataToView();
		

		getChangesCollector().addField( name );
		getChangesCollector().addField( code );
		getChangesCollector().addField( manuf );
		getChangesCollector().addField( toolModel );
		getChangesCollector().addField( category );
		getChangesCollector().addField( description );
		
//		updateFields();
		
	}

	private void dataToView() {

//		updateFields( true );
		
//		setVisible( this.item != null );

		initManufacturerComboBox();
		initCategoryComboBox();
		
		if ( this.tool != null ) {
			

		}
		
	}

	
	@Override
	public void okPressed() {

		close();
	}
	
	@Override
	public void cancelPressed() {

		close();
		
	}

	@Override
	public void dlgClosed() {

		logger.debug( "ToolsEdit Dialog has been closed!" );
		
	}

	private void initManufacturerComboBox() {

		for ( Manufacturer m : model.getManufacturers()) {
				
			addManufacturerToCombo( m );
				
		}
/*	
			mnftrSelect.addValueChangeListener( new ValueChangeListener() {
				private static final long serialVersionUID = 1L;
	
				@Override
				public void valueChange( ValueChangeEvent event ) {
					
	               logger.debug( "Manufacturer combo value has been changed! Manufacturer: '" + (( Manufacturer )event.getProperty().getValue()).getName()+ "'" );
					
				}
				
			});

			manufacturerCBinited = true;
		}
	*/	
		
	}
	private void addManufacturerToCombo( Manufacturer m ) {
		
		manuf.addItem( m );
		manuf.setItemCaption( m, m.getName());
		
	}

	private  void initCategoryComboBox() {
		
		for ( Category cat : model.getCategories()) {
			
			addCategoryToCombo( cat, 1 );
			
		}
/*	
		category.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange( ValueChangeEvent event ) {
				
				
			}
			
		});
	
		categoryCBinited = true;
	}
*/		
	}
	private void addCategoryToCombo( Category cat, int level ) {

		String caption = "";
		
		if ( cat != null ) {
			
			if ( category.getItem( cat ) == null ) {
				category.addItem( cat );
				caption = caption + cat.getName(); 
	 
				category.setItemCaption( cat, caption );
			}
			
			if ( cat.getChilds() != null && cat.getChilds().size() > 0 ) {
				
				int newLevel = level + 1;
				
				for ( Category catChild : cat.getChilds()) {
					
					addCategoryToCombo( catChild, newLevel );
					
				}
			}
			
		}
	}

	private String getHeader() {
		
		String str;
		
		switch ( editModeType ) {
			case ADD:
				str = "New";
				break;
			case EDIT:
				str = "Edit";
				break;
			default:
				str = "";
				logger.error( "Wrong editModeType" );
				break;
		}

		return str;
	}

	
}
