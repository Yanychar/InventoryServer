package com.c2point.tools.ui.transactions;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.OrganisationFacade;
import com.c2point.tools.entity.access.FunctionalityType;
import com.c2point.tools.entity.access.SecurityContext;
import com.c2point.tools.entity.organisation.Organisation;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;

public class FilterComponent extends Panel {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( FilterComponent.class.getName());

	private TransactionsListModel	model;
	
	private ComboBox 				orgSelector;
	private DateField 				startDF;
	private DateField 				endDF;

	private CheckBox 				loginCB;		// LOGIN
	private CheckBox				userCB;		// ACCOUNT, USER
	private CheckBox				adminCB;	// ORGANISATION, ACCESSRIGHTS
	private CheckBox				toolCB;		// CATEGORY, TOOL, TOOLITEM
	private CheckBox				otherCB;		// Unknown yet events

	private Button 					allBT;
	private Button 					noneBT;
	
	public FilterComponent( TransactionsListModel model ) {

		super();
		
		this.model = model;
		
		initUI();
	}

	private void initUI() {
		
		VerticalLayout content = new VerticalLayout();
		this.setContent( content );
		
		content.setWidth( "100%" );
		content.setSpacing( true );
		content.setMargin( true );

		// Add Organisation Selector if necessary
		if ( setOrgSelector() ) {
			content.addComponent( orgSelector );
		}
		
		content.addComponent( getDateSelector());

		content.addComponent( getTrnsSelector());

		dateToView();
		
		if ( model.allowsOtherCompanies()) {
			orgSelector.addValueChangeListener( new ValueChangeListener() {
				private static final long serialVersionUID = 1L;
	
				@Override
				public void valueChange( ValueChangeEvent event ) {
	
					model.setSelectedOrg(( Organisation ) event.getProperty().getValue());
					
				}
				
			});
		}
		
		startDF.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			public void valueChange( ValueChangeEvent event ) {

				model.setDateStart(( Date )event.getProperty().getValue());
			}
		});
		
		endDF.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			public void valueChange( ValueChangeEvent event ) {

				model.setDateEnd(( Date )event.getProperty().getValue());
				
			}
		});
		
		allBT.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

//				model.selectAllFilterFlags();
				
//				flagsToView();
				
				checkAllCheckBoxes();

				viewToFlags();
				model.filterFlagChanged();
				
			}
			
		});
		
		noneBT.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

//				model.clearAllFilterFlags();

//				flagsToView();
				
				clearAllCheckBoxes();

				viewToFlags();
				model.filterFlagChanged();
				
			}
			
		});

		ValueChangeListener forCheckButtons = new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {

				if ( !postponeUpdate ) {
					viewToFlags();
					model.filterFlagChanged();
				}
				
			}
			
		};
		
		loginCB.addValueChangeListener( forCheckButtons );
		userCB.addValueChangeListener( forCheckButtons );
		adminCB.addValueChangeListener( forCheckButtons );
		toolCB.addValueChangeListener( forCheckButtons );
		otherCB.addValueChangeListener( forCheckButtons );
		
	}

boolean postponeUpdate;	
	
	/*
	 * Set content of Organisations List ComboBox
	 */
	private boolean setOrgSelector() {
		
		boolean bRet = false;

		if ( model.allowsOtherCompanies()) {
		
			orgSelector = new ComboBox( model.getApp().getResourceStr( "trnsmgmt.label.select.org", "Select Company" ) + ": " );
			
			// Read Organisations list or just own Org
			SecurityContext context = model.getApp().getSessionData().getContext();
	
			if ( context.hasViewPermissionAll( FunctionalityType.TRN_MGMT )) {
				// User can see Transactions for all companies
				
				orgSelector.setWidth( "40ex" );
				orgSelector.setFilteringMode( FilteringMode.CONTAINS );
				orgSelector.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
				orgSelector.setNullSelectionAllowed( false );
				orgSelector.setInvalidAllowed( false );
				orgSelector.setImmediate( true );
	
				Collection<Organisation> orgsList = 
						OrganisationFacade.getInstance().getOrganisations();
				
				if ( orgsList != null && orgsList.size() > 0 ) {
	
					for ( Organisation org : orgsList ) {
						if ( org != null ) {
	
							orgSelector.addItem( org );
							orgSelector.setItemCaption( org, org.getName());
						}
					}
					orgSelector.select( model.getSelectedOrg());
					
					bRet = true;
					
				}
				
			}
		}
		
		return bRet;
		
	}

	private Component getDateSelector() {
		
		SimpleDateFormat dFormat = new SimpleDateFormat( "dd.MM.yyyy" );
		
		startDF = new DateField();
		startDF.setLocale( model.getApp().getSessionData().getLocale());
		startDF.setDateFormat( dFormat.toPattern());
		startDF.setResolution( Resolution.DAY );
		startDF.setImmediate(true);
		
		endDF = new DateField();
		endDF.setLocale( model.getApp().getSessionData().getLocale());
		endDF.setDateFormat( dFormat.toPattern());
		endDF.setResolution( Resolution.DAY );
		endDF.setImmediate(true);
	
		HorizontalLayout dateLine = new HorizontalLayout();
		dateLine.setSpacing( true ); // and spacing between components		
		
		dateLine.addComponent( new Label( model.getApp().getResourceStr( "trnsmgmt.label.from")));
		dateLine.addComponent( startDF );
		dateLine.addComponent( new Label( "  " + model.getApp().getResourceStr( "trnsmgmt.label.to")));
		dateLine.addComponent( endDF );
	
		return dateLine; 
	}

	private Component getTrnsSelector() {

		
		GridLayout layout = new GridLayout( 4, 3 );
		layout.setMargin( true );
		layout.setSpacing( true );
		
		loginCB = new CheckBox( "Authentication");		// LOGIN
		userCB = new CheckBox( "User Management");		// ACCOUNT, USER
		adminCB = new CheckBox( "Administration" );	// ORGANISATION, ACCESSRIGHTS
		toolCB = new CheckBox( "Tools Management" );		// CATEGORY, TOOL, TOOLITEM
		otherCB = new CheckBox( "All other" );		// Unknown yet events
		
		layout.addComponent( loginCB,  0,  0 );
		layout.addComponent( userCB,   1,  0 );
		layout.addComponent( adminCB,  2,  0 );
		layout.addComponent( toolCB,   0,  1 );
		layout.addComponent( otherCB,  1,  1 );
		
		allBT = new Button( "Select All" );
		noneBT = new Button( "Clear All" );
		
		layout.addComponent( allBT,    3,  0 );
		layout.addComponent( noneBT,  3,  1 );
		
		return layout; 
	}

	private void dateToView() {

		// Set dates
		startDF.setValue( model.getDateStart());
		endDF.setValue( model.getDateEnd());
		
		flagsToView();
	}

	private void flagsToView() {
		
		loginCB.setValue( model.getFilter().isLoginFlag());
		userCB.setValue( model.getFilter().isUserFlag());
		adminCB.setValue( model.getFilter().isAdminFlag());
		toolCB.setValue( model.getFilter().isToolFlag());
		otherCB.setValue( model.getFilter().isOtherFlag());

	}
	
	private void viewToFlags() {
		
		model.getFilter().setLoginFlag( loginCB.getValue());
		model.getFilter().setUserFlag( userCB.getValue());
		model.getFilter().setAdminFlag( adminCB.getValue());
		model.getFilter().setToolFlag( toolCB.getValue());
		model.getFilter().setOtherFlag( otherCB.getValue());

	}

	private void clearAllCheckBoxes() {
		
		postponeUpdate = true;
	
		loginCB.setValue( false );
		userCB.setValue( false );
		adminCB.setValue( false );
		toolCB.setValue( false );
		otherCB.setValue( false );
		
		postponeUpdate = false;
		
	}
	
	private void checkAllCheckBoxes() {
		
		postponeUpdate = true;
	
		loginCB.setValue( true );
		userCB.setValue( true );
		adminCB.setValue( true );
		toolCB.setValue( true );
		otherCB.setValue( true );
		
		postponeUpdate = false;
		
	}
	
}
