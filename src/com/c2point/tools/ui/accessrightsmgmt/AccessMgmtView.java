package com.c2point.tools.ui.accessrightsmgmt;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.AccessRightsFacade;
import com.c2point.tools.entity.access.AccessRight;
import com.c2point.tools.entity.access.AccessRightsCollector;
import com.c2point.tools.entity.access.FunctionalityType;
import com.c2point.tools.entity.access.OwnershipType;
import com.c2point.tools.entity.access.PermissionType;
import com.c2point.tools.ui.personnelmgmt.StuffListModel;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;

public class AccessMgmtView extends Window {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( AccessMgmtView.class.getName()); 

	private StuffListModel		model;
	
	private HashMap<AccCombo,Integer> changedMap;
	

	public AccessMgmtView( StuffListModel model ) {
		
		super();
		
		this.model = model;
		this.changedMap = new HashMap<AccCombo,Integer>();
		
		init();
		
	}
	private void init() {
		
		setModal( true );
		setCaption( "Access Rights" );
		center();
		this.setWidth( "40em" );
//		this.setHeight( "80ex" );
		
		VerticalLayout content = new VerticalLayout();
		content.setMargin( true );

		Label userName = new Label( model.getSelectedUser().getFirstAndLastNames());
		userName.addStyleName( "h2" );
		
		GridLayout addData = new GridLayout( 2, 2 );
		addData.setSpacing( true );
		
		addData.addComponent( new Label( "Company: " ), 	0, 0 );
		addData.addComponent( new Label( "Access Group: " ),0, 1 );

		addData.addComponent( new Label( "<b>" + model.getSelectedUser().getOrganisation().getName() + "</b>", ContentMode.HTML ), 1, 0 );
		addData.addComponent( new Label( "<b>" + model.getApp().getResourceStr( "accessrights.group.name." + model.getSelectedUser().getAccessGroup().name().toLowerCase()) + "</b>", ContentMode.HTML ), 1, 1 );
		
		Component arTable = createAccessRightsComponent();
		
		Component controlBar = getControlBar();
		
		content.addComponent( userName );
		content.addComponent( addData );
		content.addComponent( arTable );
		content.addComponent( controlBar );
		
		setContent( content );
		
		
	}

	private Component createAccessRightsComponent() {
		
		GridLayout table = new GridLayout( 4, FunctionalityType.values().length + 1 );
		table.setMargin( true );
		table.setSpacing( true );
		table.setWidth( "100%" );
		
		table.addComponent( new Label( "Own" ),		1, 0 );
		table.addComponent( new Label( "Company" ),	2, 0 );
		table.addComponent( new Label( "All" ),		3, 0 );

		// Fetch AccessRights for selected user
		List<AccessRight> list = AccessRightsFacade.getInstance().getAccessRights( model.getSelectedUser());
		AccessRightsCollector acCollector = new AccessRightsCollector();
		acCollector.addEntries( list );

		// Fill grod with Access Rights
		if ( model.getSecurityContext().hasViewPermission( FunctionalityType.ACCOUNTS_MGMT, model.getSelectedOrg())) {
		
			int row = 1;
			for ( FunctionalityType func : FunctionalityType.values()) {
	
				table.addComponent( new Label( func.name() ), 0, row );
	
				table.addComponent( getAccCombo( acCollector.getEntry( func, OwnershipType.OWN )),		1, row );
				
				table.addComponent( getAccCombo( acCollector.getEntry( func, OwnershipType.COMPANY )),2, row );
				
				if ( model.getSessionOwner().getOrganisation().isServiceOwner() || model.getSessionOwner().isSuperUserFlag()) {
					table.addComponent( getAccCombo( acCollector.getEntry( func, OwnershipType.ANY )),		3, row );
				}
				
				
				row++;
				
			}
			
			table.setColumnExpandRatio( 0, 1 );
		}
		
		return table;
	}

	private AccCombo getAccCombo( AccessRight acRecord ) {
		
		final AccCombo newCombo = new AccCombo( acRecord );  
		
		newCombo.addValueChangeListener( new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				
				changedMap.put( newCombo,null );
				
			}
			
		});
		
		if ( !model.getSecurityContext().hasEditPermission( FunctionalityType.ACCOUNTS_MGMT, model.getSelectedOrg())) {
			
			newCombo.setEnabled( false );
			
		}

		
		
		return newCombo;
	}
	
	private Component getControlBar() {
		
		HorizontalLayout bar = new HorizontalLayout();
		
		Button okButton = new Button( "OK" );
		Button cancelButton = new Button( "Cancel" );
		
		okButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				if ( changedMap.size() > 0 ) {
					
					Iterator<AccCombo> iterator = changedMap.keySet().iterator();
					AccessRight record;
					AccessRight defRecord;
					boolean res = true;
					
					AccessRightsFacade af = AccessRightsFacade.getInstance();
					
					while (iterator.hasNext()) {
						
						record = iterator.next().getArRecord();
						defRecord = af.getDefaultRight( record );
						
						if (  defRecord == null
							|| defRecord.getPermission() != record.getPermission()) {
						
							res = res &&
									AccessRightsFacade.getInstance().saveAccessRights( record ) != null;
							logger.debug( "Save accessRights" + record );
						} else if ( true ) {
							res = res &&
									AccessRightsFacade.getInstance().deleteAccessRight( record );
							logger.debug( "Save accessRights" + record );
						}
					}

					if ( res ) {
						AccessMgmtView.this.close();
					} else {
						
						Notification.show( "Error", "Cannot save Access Rights", Type.ERROR_MESSAGE );
						
					}
						
				} else {
					// Nothing was changed. Close and exit
					AccessMgmtView.this.close();
				}
				
			}
			
			
		});
		
		
		cancelButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				AccessMgmtView.this.close();
				
			}
			
			
		});

		bar.addComponent( okButton );
		bar.addComponent( cancelButton );
		
		return bar;
	}
	
	
	class AccCombo extends NativeSelect {
		private static final long serialVersionUID = 1L;
		
		AccessRight	arRecord;
				
		AccCombo( AccessRight arRecord ) {
			super();
			
			setNullSelectionAllowed( false );
	        setImmediate( true );

	        setArRecord( arRecord );
	        
			initValues();
			
			setValue( arRecord.getPermission());
			
		}
		
		private void initValues() {
			
			for ( PermissionType type : PermissionType.values()) {
	            
				addItem( type );
	            setItemCaption( type, type.name());
	        }
			
			
		}

		public AccessRight getArRecord() { return arRecord; }
		public void setArRecord( AccessRight arRecord ) { this.arRecord = arRecord; }
		
		
		protected void fireValueChange(boolean repaintIsNotNeeded) {
			
			super.fireValueChange( repaintIsNotNeeded );
			
			if ( getArRecord().getPermission() != this.getValue()) {
				
				getArRecord().setPermission(( PermissionType )this.getValue());
			}
//			logger.debug( "AccCombo was changed. Was " + getArRecord().getPermission() + ". Now - " + this.getValue() );
		}
		
	}

}
