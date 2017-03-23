package com.c2point.tools.ui.repositoryview;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.InventoryUI;
import com.c2point.tools.datalayer.ItemsFacade;
import com.c2point.tools.datalayer.SettingsFacade;
import com.c2point.tools.entity.access.FunctionalityType;
import com.c2point.tools.entity.repository.ItemStatus;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.ui.buttonbar.ButtonBar;
import com.c2point.tools.ui.buttonbar.ButtonPressListener;
import com.c2point.tools.ui.changescollecor.FieldsChangeCollector;
import com.c2point.tools.ui.util.AbstractDialog;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class ChangeItemAttributesDlg extends AbstractDialog {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( ChangeItemAttributesDlg.class.getName());

	private ToolsListModel		model;
	private ToolItem 			item;

	private Label		currentOwnership;
	private Button		getOwnership;
	private ComboBox	status;
	private TextArea 	comments;

	private boolean specialTakeoverFlag = false;
	
	
	public ChangeItemAttributesDlg( ToolsListModel model, ToolItem item ) {
		super();
		
		this.model = model;
		this.item = item;

		if ( item == null ) {
			item = model.getSelectedItem();
		}
			
		initUI();

	}

	

	private void initUI() {
		
		setCaption( "Edit Tool" );
		setModal( true );
		setClosable( true );
//		setResizable( false );

		VerticalLayout subContent = new VerticalLayout();
		subContent.setMargin( true );
		subContent.setSpacing( true );

		setContent( subContent );
		
//		center();
		
		currentOwnership = new Label( "", ContentMode.HTML);
		currentOwnership.setImmediate( true );
		
		getOwnership = new Button( "Take Ownership ..." );
		getOwnership.setImmediate( true );
		getOwnership.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				changeUser();				
			}
			
			
		});
		
		
		status = new ComboBox( "New status:" );
		status.setItemCaptionMode( ItemCaptionMode.EXPLICIT );
		status.setNullSelectionAllowed(false);
		status.setImmediate(true);

		fillStatusField();

		comments = new TextArea( model.getApp().getResourceStr( "toolsmgmt.view.label.iteminfo" ));
		comments.setNullRepresentation( "" );
		comments.setRows( 3 );
		comments.setColumns( 60 );
		comments.setNullSettingAllowed( true );
		comments.setNullRepresentation( "" );
		comments.setImmediate( true );
		
		
		enableProperFields();

		subContent.addComponent( currentOwnership );
		subContent.addComponent( getOwnership );
		subContent.addComponent( status );
		subContent.addComponent( comments );
		
		
		getChangesCollector().addField( currentOwnership );
		getChangesCollector().addField( status );
		getChangesCollector().addField( comments );

		dataToView();
		
		subContent.addComponent( getButtonBar());
	}

	private void fillStatusField() {

		boolean freeAllowed = SettingsFacade.getInstance().getBoolean( model.getOrg(), "freeStatusAllowed", false );
		
		for ( ItemStatus oneStatus : ItemStatus.values()) {
			if ( oneStatus != ItemStatus.UNKNOWN 
				 &&
				 ( freeAllowed || !freeAllowed && oneStatus != ItemStatus.FREE )
			) {
				
				status.addItem( oneStatus );
				status.setItemCaption( oneStatus, oneStatus.toString((( InventoryUI )UI.getCurrent()).getSessionData().getBundle()));
			}
		}
		
	}
	
	private void enableProperFields() {

		if ( item != null ) {
		
			boolean takeOverPerm;   
			boolean changeStatusPerm;   
			
			if ( specialTakeoverFlag ) {
				// If Takeover was done than values to set are known: no more takeover and possible to edit own data
				takeOverPerm = false;   
				changeStatusPerm = true;
				
			} else {
				takeOverPerm = model.getSecurityContext().canChangeToolItemIfNotOwn( FunctionalityType.BORROW, item );   
				changeStatusPerm = model.getSecurityContext().canChangeToolItemIfOwn( FunctionalityType.CHANGESTATUS, item );
			}
			
			getOwnership.setEnabled( takeOverPerm );
			status.setEnabled( changeStatusPerm );
			comments.setEnabled( changeStatusPerm );
		} else {

			getOwnership.setEnabled( false );
			status.setEnabled( false );
			comments.setEnabled( false );
			
		}
		
	}



	private void changeUser() {

		specialTakeoverFlag = true;
		
		this.currentOwnership.setValue( "Current user:<br/>"
				+ "<b>" + model.getSessionOwner().getFirstAndLastNames() + "</b>"
		);

		enableProperFields();		
	}
	
	private void dataToView() {
	
		if ( item != null  ) {
			
			this.currentOwnership.setValue( "Current user:<br/>"
											+ "<b>" + item.getCurrentUser().getFirstAndLastNames() + "</b>"
										  );
			
			this.specialTakeoverFlag = false;

			this.status.setValue( item.getStatus());
			this.comments.setValue( item.getComments());
			
		} else {
			
			this.status.setValue( null );
			this.comments.setValue( null );
	
		}
		
		getChangesCollector().clearChanges();
	}

	private void viewToDate() {

		if ( item != null  ) {

			if ( specialTakeoverFlag ) {

				item.setReservedBy( null );
				item.setCurrentUser( model.getSessionOwner());
				
			}

			item.setStatus(( ItemStatus )this.status.getValue());
			item.setComments( this.comments.getValue());
			
		}
		
	}

	

	@Override
	public void okPressed() {

		viewToDate();
		
		ToolItem updatedItem = ItemsFacade.getInstance().update( item );
		
		if ( updatedItem != null ) {
			if ( logger.isDebugEnabled()) logger.debug( "Specified Tool Item with Id=" + item.getId() + " has been updated." );
			
			new Notification( 
					model.getApp().getResourceStr( "general.notify.header" ),
					model.getApp().getResourceStr( "repositorymgmt.notify.changed" ),
					Notification.Type.HUMANIZED_MESSAGE, 
					true 
			).show( Page.getCurrent());
			
			close();
			
			model.fireChanged( updatedItem );
			
		} else {

			new Notification( 
					model.getApp().getResourceStr( "general.error.header" ),
					model.getApp().getResourceStr( "repositorymgmt.error.changed" ),
					Notification.Type.ERROR_MESSAGE, 
					true 
			).show( Page.getCurrent());
		}
		
	}



	@Override
	public void cancelPressed() {

		close();
		
	}



	@Override
	public void dlgClosed() {
		// TODO Auto-generated method stub
		
	}


}
