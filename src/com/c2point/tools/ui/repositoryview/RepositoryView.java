package com.c2point.tools.ui.repositoryview;

import com.c2point.tools.ui.category.HwCategoriesComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;

public class RepositoryView extends HorizontalLayout {

	private static final long serialVersionUID = 1L;

	private HwCategoriesComponent	categoriesComponent;
	private ToolsListComponent		toolsListComponent;
	private ActionsListComponent	actionsListComponent;
	
	private ToolsListModel			model;

	public RepositoryView() {
		super();

		initModel();
		
		initUI();
	}
	
	public void initUI() {
	
		setWidth( "100%" );
		this.setHeight( "100%" );

		Component component;
		
		component = createCategoryComponent();
		this.addComponent( component );
		this.setExpandRatio( component, 0.2f );
		
		component = createToolsListComponent();
		this.addComponent( component );
		this.setExpandRatio( component, 0.6f );

		component = createActionsComponent();
		this.addComponent( component );
		this.setExpandRatio( component, 0.2f );
		
		categoriesComponent.selectTopCategory();
		model.init();

	}

	private Component createCategoryComponent() {
		
		categoriesComponent = new HwCategoriesComponent( this.model );

		Panel panel = new Panel();
		panel.setContent( categoriesComponent );

		return panel;
	}

	private Component createToolsListComponent() {
		
		toolsListComponent = new ToolsListComponent( this.model );

		Panel panel = new Panel( "List of Tools" );
		panel.setContent( toolsListComponent );
		
		return panel;
	}

	private Component createActionsComponent() {
		
		actionsListComponent = new ActionsListComponent( this.model );

		Panel panel = new Panel( "Operations" );
		panel.setContent( actionsListComponent );
		
		panel.setHeight( "100%" );
		
		return panel;
	}
	
	private void initModel() {
		
		this.model = new ToolsListModel();
	
	}
	
}
