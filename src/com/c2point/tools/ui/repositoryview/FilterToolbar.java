package com.c2point.tools.ui.repositoryview;

import java.util.Arrays;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.InventoryUI;
import com.c2point.tools.entity.repository.ItemStatus;
import com.c2point.tools.ui.listeners.FilterListener;
import com.c2point.tools.ui.listeners.PrintNowListener;
import com.c2point.tools.ui.util.AbstractModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.BaseTheme;

public class FilterToolbar extends HorizontalLayout {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( FilterToolbar.class.getName());

	private FilterListener		filterListener;
	private TextField			searchText;

	private ComboBox			statusFilter;
	private Button				printButton;
	private PrintNowListener 	printButtonListener;
	

	public FilterToolbar( FilterListener filterListener, AbstractModel model ) {
		this( filterListener, model, null );
	}
	public FilterToolbar( FilterListener filterListener, AbstractModel model, PrintNowListener printButtonListener ) {
		super();
	
		this.filterListener = filterListener;
		this.printButtonListener = printButtonListener;
		
		initUI();
	}
	
	private void initUI() {
	
		this.setWidth( "100%");
		this.setMargin( new MarginInfo( false, true, false, true ));
		this.setSpacing( true );

		Label searchIcon = new Label();
		searchIcon.setIcon(new ThemeResource("icons/16/search.png"));
		searchIcon.setWidth( "2em" );
		
		searchText = new TextField();
		searchText.setWidth("30ex");
		searchText.setNullSettingAllowed(true);
		searchText.setInputPrompt( "Search ...");
		searchText.setImmediate( true );
		
		searchText.addTextChangeListener( new TextChangeListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void textChange( TextChangeEvent event ) {
				
				prepareEndSendFilteringEvent( event.getText());
				
			}
			
		});

		
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
					prepareEndSendFilteringEvent( null );
					
				}
				
			}
			
		});
		
		

		Label statusFilterLabel = new Label( (( InventoryUI )UI.getCurrent()).getResourceStr( "repositorymgmt.filter.label.status" ));
		statusFilterLabel.setWidth( null );

		statusFilter = new ComboBox();
		statusFilter.setFilteringMode( FilteringMode.CONTAINS );
		statusFilter.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		statusFilter.setNullSelectionAllowed( true );
		statusFilter.setInputPrompt( (( InventoryUI )UI.getCurrent()).getResourceStr( "repositorymgmt.filter.prompt.status" ));
		statusFilter.setInvalidAllowed( false );
		statusFilter.setImmediate(true);

		initStatusFilter();

		// Print button
        printButton = new Button();
    	printButton.setCaption( (( InventoryUI )UI.getCurrent()).getResourceStr( "general.button.print" ));
//    	printButton.addStyleName( Runo.BUTTON_BIG );
//    	printButton.addStyleName( Runo.BUTTON_DEFAULT );
		
    	printButton.addClickListener( new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				if ( printButtonListener != null )
					printButtonListener.printNow();
			}
    		
    	});
    	
		
		
		addComponent( searchIcon );
		addComponent( searchText );
		addComponent( deleteIcon );

		
		
		this.addComponent( statusFilterLabel );
		this.addComponent( statusFilter );
		
		Label glue = new Label( "" );
		addComponent( glue );
		
		if ( printButtonListener != null ) {
			addComponent( printButton );
		}
		
		setExpandRatio( glue,  1.0f );

	}
	

  	public ComboBox getStatusFilter() { return statusFilter; }
 
	private void initStatusFilter() {

		statusFilter.addItem( ItemStatus.FREE );
		statusFilter.addItem( ItemStatus.INUSE );
		statusFilter.addItem( ItemStatus.BROKEN );
		statusFilter.addItem( ItemStatus.REPAIRING );
		statusFilter.addItem( ItemStatus.STOLEN );
		statusFilter.addItem( ItemStatus.RESERVED );

		statusFilter.setItemCaption( ItemStatus.FREE, ItemStatus.FREE.toString((( InventoryUI )UI.getCurrent()).getSessionData().getBundle()));
		statusFilter.setItemCaption( ItemStatus.INUSE, ItemStatus.INUSE.toString((( InventoryUI )UI.getCurrent()).getSessionData().getBundle()));
		statusFilter.setItemCaption( ItemStatus.BROKEN, ItemStatus.BROKEN.toString((( InventoryUI )UI.getCurrent()).getSessionData().getBundle()));
		statusFilter.setItemCaption( ItemStatus.REPAIRING, ItemStatus.REPAIRING.toString((( InventoryUI )UI.getCurrent()).getSessionData().getBundle()));
		statusFilter.setItemCaption( ItemStatus.STOLEN, ItemStatus.STOLEN.toString((( InventoryUI )UI.getCurrent()).getSessionData().getBundle()));
		statusFilter.setItemCaption( ItemStatus.RESERVED, ItemStatus.RESERVED.toString((( InventoryUI )UI.getCurrent()).getSessionData().getBundle()));
		
		statusFilter.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
	
			@Override
			public void valueChange( ValueChangeEvent event ) {
				
//				model.setStatusFilter(( ItemStatus )statusFilter.getValue());
				prepareEndSendFilteringEvent();
				
			}
			
		});
		
		
		
	}

	private void prepareEndSendFilteringEvent() {
		
		prepareEndSendFilteringEvent( searchText.getValue());

	}
	private void prepareEndSendFilteringEvent( String filterStr ) {
		
		Collection<String> strArray = null;
		
		if ( filterStr != null && filterStr.trim().length() > 0 ) {
			
			strArray = Arrays.asList( filterStr.trim().split( " " ));
			
		}

		filterListener.filterWasChanged( strArray, ( ItemStatus )statusFilter.getValue() );
	}

}
