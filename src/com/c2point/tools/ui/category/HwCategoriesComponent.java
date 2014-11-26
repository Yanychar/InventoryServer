package com.c2point.tools.ui.category;

import java.util.Collection;

import com.c2point.tools.entity.tool.Category;
import com.c2point.tools.ui.ListWithSearchComponent;
import com.c2point.tools.ui.repositoryview.ToolsListModel;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Tree;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HwCategoriesComponent extends ListWithSearchComponent implements CategoryModelListener {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger( HwCategoriesComponent.class.getName());
	
	private ToolsListModel		model; 
	private Tree				categoriesTree;

	private Category			topCategory; 
	
	private boolean 			editMode;

	public HwCategoriesComponent( ToolsListModel model ) {
		
		this( model, false );
		
	}
	
	public HwCategoriesComponent( ToolsListModel model, boolean editMode ) {
		super();
		
		initUI();
		
		initModel( model );
		
		setEditMode( editMode );
		
	}
	
	public void setEditMode() {
		
		setEditMode( true );
	}
	
	private void setEditMode( boolean editMode ) {
		
		this.editMode = editMode;
		updateUI();
	}
	
	public void stopEditMode() {
		
		setEditMode( false );
		
	}

	private void initUI() {

		setSizeFull();

		setMargin( true );
		setSpacing( true );

		
		categoriesTree = new Tree();
	
		// Configure table
		categoriesTree.setSelectable( true );
		categoriesTree.setMultiSelect( false );
		categoriesTree.setNullSelectionAllowed( false );
		categoriesTree.setImmediate( true );
		categoriesTree.setSizeFull();
		
//		categoriesTree.addContainerProperty( "code",		String.class, 	null );
		categoriesTree.addContainerProperty( "name", 		String.class, 	"" );

		categoriesTree.setItemCaptionPropertyId( "name" );
		categoriesTree.setItemCaptionMode( ItemCaptionMode.PROPERTY );		
		
		// New User has been selected. Send event to model
		categoriesTree.addValueChangeListener( new ValueChangeListener() {

			private static final long serialVersionUID = 1L;

			public void valueChange( ValueChangeEvent event) {
				if ( logger.isDebugEnabled()) logger.debug( "CategoriesList selection were changed" );

				model.categorySelected(( Category ) categoriesTree.getValue());
				
			}
		});

		this.addComponent( getSearchBar());
		this.addComponent( categoriesTree );
		
		this.setExpandRatio( categoriesTree, 1.0f );
		
	}
	
	private void updateUI() {
		
	}
	
	private void initModel( ToolsListModel model ) {
		
		this.model = model; 
		model.addChangedListener( this );		
//		model.init();

	}

	@Override
	public void wasAdded(Category category) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void wasChanged(Category category) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void wasDeleted(Category category) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void listWasChanged() {
//		categoriesTree.removeAllItems();
		
		Object selectedItemId = categoriesTree.getValue();

		categoriesTree.removeAllItems();

		topCategory = new Category( "000000", "All Categories", true );
		topCategory.setOrg( model.getOrg());

		addTopCategory( topCategory );
		
		Collection<Category> categoriesList = this.model.getTopCategories();
		
		if ( categoriesList != null && categoriesList.size() > 0 ) {
			
			for ( Category category : categoriesList ) {
				if ( category != null ) {
					addCategory( category, topCategory );
				}
			}
		}

		categoriesTree.expandItem( topCategory );
		categoriesTree.setValue( topCategory );
		
		// TODO:  Find out how to sort Tree
/*		
		categoriesTree.setSortContainerPropertyId( "name" );

		categoriesTree.sort();
*/
		
		// TODO:  Find out how to setup first item selected
/*		
		if ( selectedItemId != null && categoriesTree.containsId( selectedItemId )) {
			categoriesTree.select( selectedItemId );
		} else if ( categoriesTree.firstItemId() != null ) {
			categoriesTree.setValue( categoriesTree.firstItemId());
		} else {
//			model.selectUser( null );
		}
*/		
	}

	public void selectCategory( Category category ) {
		
		categoriesTree.setValue( category );
		
	}
	
	public void selectTopCategory() {
		selectCategory( topCategory );
	}
	
	
	
	@Override
	public void selected( Category category ) {}
	
	private boolean addTopCategory( Category topCategory ) {
	
		boolean ret = addCategory( topCategory, null );

		categoriesTree.setChildrenAllowed( topCategory, true );
		
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	private boolean addCategory( Category addCategory, Category parentCategory ) {
		
		boolean ret = false;

		try {
			// Add this category
			Item item = categoriesTree.addItem( addCategory );

			item.getItemProperty( "name" ).setValue( addCategory.getName());
			
			if ( parentCategory != null ) {
				
				categoriesTree.setParent( addCategory, parentCategory );
				
			}
			
			// Add Child categories if exist
			if ( addCategory.hasChilds()) {
				categoriesTree.setChildrenAllowed( addCategory, true );
				
				for ( Category childCategory : addCategory.getChilds()) {
					if ( childCategory != null ) {
						addCategory( childCategory, addCategory );
					}
				}
				
			} else {
				categoriesTree.setChildrenAllowed( addCategory, false );
			}
			
			
			ret = true;
		} catch ( Exception e ) {
			logger.error( "Failed to add Category: " + addCategory + "\n" + e );
		}
		
		return ret;
	}

	
	
	
}
