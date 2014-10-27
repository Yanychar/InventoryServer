package com.c2point.tools.ui;

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
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ListWithSearchComponent extends VerticalLayout {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger( ListWithSearchComponent.class.getName());
	
	private static String [] searchFields = {
			"name",
			"code",
	};
	
	protected Container			dataSource;

	protected HorizontalLayout	searchLayout;
	protected TextField			searchText;
	protected AbstractSelect	listComponent;
	
	protected ListWithSearchComponent() {
		super();
		
		this.dataSource = null;
	}
	
	protected ListWithSearchComponent( Container dataSource ) {
		this();
		
		setContainerForSearch( dataSource );
	}

	protected String [] getFieldsForSearch() {
		
		return searchFields;
		
	}
	
	
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
		if ( searchLayout == null ) {

			searchLayout = new HorizontalLayout();
			
			searchLayout.setWidth( "100%");
			searchLayout.setMargin( new MarginInfo( false, true, false, true ));
	
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
			
	
			searchLayout.addComponent( searchIcon );
			searchLayout.addComponent( searchText );
			searchLayout.addComponent( deleteIcon );
			Label glue = new Label( "" );
			searchLayout.addComponent( glue );
			searchLayout.setExpandRatio( glue,  1.0f );
		}
		
		return searchLayout;
	}

	private boolean searchFieldUpdated( String searchStr ) {
		
		boolean found = false;

		if ( dataSource != null ) {
		
			(( Container.Filterable )dataSource ).removeAllContainerFilters();
		
			if ( searchStr != null && searchStr.length() > 0 ) {
				Filter filter = new Or(
						new SimpleStringFilter( getFieldsForSearch()[0],	searchStr, true, false ),
						new SimpleStringFilter( getFieldsForSearch()[1],	searchStr, true, false )
						);
				
				(( Container.Filterable )dataSource ).addContainerFilter( filter );
				
				
			}
			
			found = dataSource.size() > 0;
			
			if ( dataSource instanceof Container.Ordered && this.listComponent != null ) {
				this.listComponent.setValue( found ? (( Container.Ordered )dataSource ).firstItemId() : null );
			}
			
		}
		
		
		return found;
	}
}
