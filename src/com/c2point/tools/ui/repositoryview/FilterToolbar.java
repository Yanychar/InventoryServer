package com.c2point.tools.ui.repositoryview;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ItemStatus;
import com.c2point.tools.entity.tool.Manufacturer;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class FilterToolbar extends HorizontalLayout {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( FilterToolbar.class.getName());

	private ToolsListModel		model; 
	
	private ComboBox			userFilter;
	private ComboBox			statusFilter;
	private ComboBox			manufFilter;

	public FilterToolbar( ToolsListModel model ) {
		super();
	
		this.model = model;
		
		initUI();
	}
	
	private void initUI() {
	
		this.setWidth( "100%");
		this.setMargin( new MarginInfo( false, true, false, true ));
		this.setSpacing( true );

		Label userFilterLabel = new Label( this.model.getApp().getResourceStr( "repositorymgmt.filter.label.user" ));
		userFilterLabel.setWidth( null );

		userFilter = new ComboBox();
		userFilter.setFilteringMode( FilteringMode.CONTAINS );
		userFilter.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		userFilter.setNullSelectionAllowed( true );
		userFilter.setInputPrompt( this.model.getApp().getResourceStr( "repositorymgmt.filter.prompt.user" ));
		userFilter.setInvalidAllowed( false );
		userFilter.setImmediate(true);
		

		Label statusFilterLabel = new Label( this.model.getApp().getResourceStr( "repositorymgmt.filter.label.status" ));
		statusFilterLabel.setWidth( null );

		statusFilter = new ComboBox();
		statusFilter.setFilteringMode( FilteringMode.CONTAINS );
		statusFilter.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		statusFilter.setNullSelectionAllowed( true );
		statusFilter.setInputPrompt( this.model.getApp().getResourceStr( "repositorymgmt.filter.prompt.status" ));
		statusFilter.setInvalidAllowed( false );
		statusFilter.setImmediate(true);

		Label manufFilterLabel = new Label( this.model.getApp().getResourceStr( "repositorymgmt.filter.label.manufacturer" ));
		manufFilterLabel.setWidth( null );

		manufFilter = new ComboBox();
		manufFilter.setFilteringMode( FilteringMode.CONTAINS );
		manufFilter.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		manufFilter.setNullSelectionAllowed( true );
		manufFilter.setInputPrompt( this.model.getApp().getResourceStr( "repositorymgmt.filter.prompt.manufacturer" ));
		manufFilter.setInvalidAllowed( false );
		manufFilter.setImmediate(true);
		
		
		initUserFilter();
		initStatusFilter();
		initManufFilter();

		this.addComponent( userFilterLabel );
		this.addComponent( userFilter );
		this.addComponent( statusFilterLabel );
		this.addComponent( statusFilter );
		this.addComponent( manufFilterLabel );
		this.addComponent( manufFilter );
		
		Label glue = new Label( "" );
		this.addComponent( glue );
		this.setExpandRatio( glue,  1.0f );

	}
	
	public ComboBox getUserFilter() { return userFilter; }
	public ComboBox getStatusFilter() { return statusFilter; }
	public ComboBox getManufFilter() { return manufFilter; }

	private void initUserFilter() {
	
		for ( OrgUser user : model.getUsers()) {
			
			userFilter.addItem( user );
			userFilter.setItemCaption( user, user.getLastAndFirstNames());
			
		}
	
		userFilter.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
	
			@Override
			public void valueChange( ValueChangeEvent event ) {
				
				model.setUserFilter(( OrgUser ) userFilter.getValue());
			}
			
		});
		
		userFilter.setValue( null );
		
	}
	
	private void initStatusFilter() {

		statusFilter.addItem( ItemStatus.FREE );
		statusFilter.addItem( ItemStatus.INUSE );
		statusFilter.addItem( ItemStatus.BROKEN );
		statusFilter.addItem( ItemStatus.REPAIRING );
		statusFilter.addItem( ItemStatus.STOLEN );
		statusFilter.addItem( ItemStatus.RESERVED );

		statusFilter.setItemCaption( ItemStatus.FREE, ItemStatus.FREE.toString( model.getApp().getSessionData().getBundle()));
		statusFilter.setItemCaption( ItemStatus.INUSE, ItemStatus.INUSE.toString( model.getApp().getSessionData().getBundle()));
		statusFilter.setItemCaption( ItemStatus.BROKEN, ItemStatus.BROKEN.toString( model.getApp().getSessionData().getBundle()));
		statusFilter.setItemCaption( ItemStatus.REPAIRING, ItemStatus.REPAIRING.toString( model.getApp().getSessionData().getBundle()));
		statusFilter.setItemCaption( ItemStatus.STOLEN, ItemStatus.STOLEN.toString( model.getApp().getSessionData().getBundle()));
		statusFilter.setItemCaption( ItemStatus.RESERVED, ItemStatus.RESERVED.toString( model.getApp().getSessionData().getBundle()));
		
		statusFilter.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
	
			@Override
			public void valueChange( ValueChangeEvent event ) {
				
				model.setStatusFilter(( ItemStatus )statusFilter.getValue());
				
			}
			
		});
		
	}
	
	private void initManufFilter() {
		
		for ( Manufacturer manuf : model.getManufacturers()) {
			
			manufFilter.addItem( manuf );
			manufFilter.setItemCaption( manuf, manuf.getName());
			
		}
	
		manufFilter.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;
	
			@Override
			public void valueChange( ValueChangeEvent event ) {
				
				model.setManufFilter(( Manufacturer ) manufFilter.getValue());
			}
			
		});
		
		userFilter.setValue( null );
		
	}
	

}
