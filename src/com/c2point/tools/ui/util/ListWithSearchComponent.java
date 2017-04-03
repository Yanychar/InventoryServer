package com.c2point.tools.ui.util;

import com.c2point.tools.InventoryUI;
import com.vaadin.data.Container;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ListWithSearchComponent extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger( ListWithSearchComponent.class.getName());
	
	protected Container			dataSource;

	protected HorizontalLayout	toolBarLayout;
	protected TextField			searchText;
	protected AbstractSelect	listComponent;
	
	protected Button			addButton;
	protected boolean			addNecessary = false;		
	
	protected ListWithSearchComponent() {
		this( false );
		
		this.dataSource = null;
	}
	
	protected ListWithSearchComponent( boolean withAddButton ) {
		super();
		
		this.dataSource = null;
		
		this.addNecessary = withAddButton;
		
	}
	
	private String [] searchFields = null;
	protected String [] getFieldsForSearch() {
		
		if ( searchFields == null ) { 
			searchFields = new String [] { "name", "code" };
		}
		
		return searchFields;
	}
	
/*	
	protected ListWithSearchComponent( Container dataSource ) {
		this();
		
		setContainerForSearch( dataSource );
	}
*/
	protected void setContainerForSearch( Container dataSource ) {

		if ( dataSource instanceof Container.Filterable ) {
			this.dataSource = ( Container.Filterable )dataSource;
		} else {
			logger.error( "Suplied container does not have Container.Filterable interface" );
			this.dataSource = null;
		}
		
	}


	protected void setContainerForSearch( AbstractSelect selectionList ) {

		setContainerForSearch( selectionList.getContainerDataSource());
		this.listComponent = selectionList; 
		
	}
	
	protected Component getSearchBar() {
		
		// Add search field
		if ( toolBarLayout == null ) {

			toolBarLayout = new HorizontalLayout();
			
			toolBarLayout.setWidth( "100%");
			toolBarLayout.setMargin( new MarginInfo( false, true, false, true ));
	
			Label searchIcon = new Label();
			searchIcon.setIcon(new ThemeResource("icons/16/search.png"));
			searchIcon.setWidth( "2em" );
	
			Button deleteIcon = new Button();
			deleteIcon.setStyleName( BaseTheme.BUTTON_LINK );
			deleteIcon.setIcon( new ThemeResource("icons/16/reject.png"));
			
			deleteIcon.addClickListener( new ClickListener() {
	
				private static final long serialVersionUID = 1L;
	
				@Override
				public void buttonClick( ClickEvent event) {
	
					if ( logger.isDebugEnabled()) logger.debug( "DeleteIcon image had been pressed" );
					
					if ( searchText != null && searchText.getValue() != null && searchText.getValue().length() > 0 ) {
	
						if ( logger.isDebugEnabled()) logger.debug( "Search text shall be set to empty string" );
						
						searchText.setValue( "" );
						searchFieldUpdated( null );
						
					}
					
				}
				
			});
			
			searchText = new TextField();
			searchText.setWidth("30ex");
			searchText.setNullSettingAllowed(true);
			searchText.setInputPrompt( "Search ...");
			searchText.setImmediate( true );
			
			searchText.addTextChangeListener( new TextChangeListener() {
	
				private static final long serialVersionUID = 1L;
	
				@Override
				public void textChange( TextChangeEvent event ) {
					
					searchFieldUpdated( event.getText());
					
				}
				
			});
			
			if ( this.addNecessary ) {
				
				addButton = new Button((( InventoryUI )UI.getCurrent()).getResourceStr( "personnel.caption.add" ));
				addButton.addClickListener( new ClickListener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void buttonClick(ClickEvent event) {

						addButtonHandler();
						
					}
					
				});
				
			}
	
			
			
			
			toolBarLayout.addComponent( searchIcon );
			toolBarLayout.addComponent( searchText );
			toolBarLayout.addComponent( deleteIcon );
			Label glue = new Label( "" );
			toolBarLayout.addComponent( glue );
			toolBarLayout.setExpandRatio( glue,  1.0f );

			if ( this.addNecessary ) {
				toolBarLayout.addComponent( addButton );
			}
		}
		
		return toolBarLayout;
	}

	protected boolean searchFieldUpdated( String searchStr ) {
		
		boolean found = false;

		if ( dataSource != null ) {
		
			(( Container.Filterable )dataSource ).removeAllContainerFilters();
		
			if ( searchStr != null && searchStr.length() > 0 ) {
				
				(( Container.Filterable )dataSource ).addContainerFilter( concatenateFilters( searchStr ));
				
				
			}
			
			found = dataSource.size() > 0;
			
			if ( dataSource instanceof Container.Ordered && this.listComponent != null ) {
				this.listComponent.setValue( found ? (( Container.Ordered )dataSource ).firstItemId() : null );
			}
			
		}
		
		
		return found;
	}

	protected void addButtonHandler() {
		
	}

	private Filter concatenateFilters( String searchStr ) {
		
		Filter filter = null;
		
		if ( getFieldsForSearch().length > 0 ) {
			
			filter = new SimpleStringFilter( getFieldsForSearch()[0],	searchStr, true, false );
			
			for ( int i = 1; i < getFieldsForSearch().length; i++ ) {

				filter = new Or(
								filter,
								new SimpleStringFilter( getFieldsForSearch()[i],	searchStr, true, false )
								);
			}
			
		}

		return filter;
	}
	
}
