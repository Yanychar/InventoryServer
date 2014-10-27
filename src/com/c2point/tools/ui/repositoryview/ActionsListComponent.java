package com.c2point.tools.ui.repositoryview;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.msg.Message;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.ui.repositoryview.handlers.AddNewToolHandler;
import com.c2point.tools.ui.repositoryview.handlers.CommandListener;
import com.c2point.tools.ui.repositoryview.handlers.RequestToolHandler;
import com.c2point.tools.ui.repositoryview.handlers.SendMessageHandler;
import com.c2point.tools.ui.repositoryview.handlers.SetToolUserHandler;
import com.vaadin.event.MouseEvents;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

public class ActionsListComponent extends VerticalLayout implements ToolsModelListener {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger( ActionsListComponent.class.getName());
	
	private ToolsListModel	model; 
	
	private ToolItem 	selectedItem;
	
	private boolean 		editMode;
	
	public ActionsListComponent( ToolsListModel model ) {
		
		this( model, false );
		
	}
	
	public ActionsListComponent( ToolsListModel model, boolean editMode ) {
		super();
		
		this.model = model;
		this.selectedItem = null;
		
		initUI();
		
		setEditMode( editMode );
		
		model.addChangedListener( this );
		
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
//		setSpacing( true );

		Label glue = new Label( "" );
		Label separator = new Label( "<hr/>", ContentMode.HTML );
		separator.setWidth( "100%" );
		
		addComponent( createCommandButtonComponent( "Request Tool ...", 	new RequestToolHandler( model )));
		addComponent( createCommandButtonComponent( "Set Tool User ...", 	new SetToolUserHandler( model )));
		addComponent( createCommandButtonComponent( "Add New Tool ...", 	new AddNewToolHandler( model )));
		addComponent( separator );
		addComponent( createCommandButtonComponent( "Send message to ...", 	new SendMessageHandler( model )));
		addComponent( glue );
		
		setExpandRatio( glue, 1.0f );
		
	}	

	private void updateUI() {
		
	}
	
	private Component createCommandButtonComponent( String header, final CommandListener listener ) {

		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth( "100%" );
		
		Button commandButton = new Button( header + " ..." );
		Label glue = new Label( "" );
		Embedded icon = new Embedded( "", new ThemeResource( "icons/16/arrowright.png" ));

		commandButton.addStyleName( "link" );

		hl.addComponent( commandButton );
		hl.addComponent( glue );
		hl.addComponent( icon );

		hl.setComponentAlignment( commandButton, Alignment.BOTTOM_LEFT);
		hl.setComponentAlignment( icon, Alignment.TOP_RIGHT);
		hl.setExpandRatio( glue, 1.0f );

		commandButton.addClickListener( new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				
				CommandListener.ExitStatus exitStatus = listener.handleCommand( ActionsListComponent.this.selectedItem );
				if ( logger.isDebugEnabled()) logger.debug( "Command button has been clicked" );
				
				showNotification( exitStatus, null );
				
				
			}
		});
		
		icon.addClickListener( new MouseEvents.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void click(com.vaadin.event.MouseEvents.ClickEvent event) {

				CommandListener.ExitStatus exitStatus = listener.handleCommand( ActionsListComponent.this.selectedItem );
				if ( logger.isDebugEnabled()) logger.debug( "Command icon has been clicked" );

				showNotification( exitStatus, null );
				
			}

			
		});
		
		
		
		return hl;
	}

	@Override
	public void wasAdded(ToolItem repItem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void wasChanged(ToolItem repItem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void wasDeleted(ToolItem repItem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void listWasChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void selected( ToolItem repItem ) {

		logger.debug( "ActionListComponent received Tools Selection Changed event from ToolsModel" );
		
		this.selectedItem = repItem;

		updateUI();
		
	}

	private void showNotification( CommandListener.ExitStatus exitStatus, Message msg ) {
		
		switch ( exitStatus ) {
			case FAILED_UNKNOWN:
				showNotification( 
						Notification.Type.WARNING_MESSAGE, 
						"<br/>Cannot Request the Tool because unknown reason!" );
			case OK:
				showNotification( 
						Notification.Type.HUMANIZED_MESSAGE, 
						"<br/>Request was sent" );
				break;
			case SENT_TO_OWNER:
				showNotification( 
						Notification.Type.HUMANIZED_MESSAGE, 
						"<br/>Request was sent to Tool Owner" );
				break;
			case SENT_TO_USER:
				showNotification( 
						Notification.Type.HUMANIZED_MESSAGE, 
						"<br/>Request was sent to Tool User" );
				break;
			case WRONG_ITEM:
				showNotification( 
						Notification.Type.WARNING_MESSAGE, 
						"<br/>Tool shall be selected or it is not defined!" );
				break;
			case WRONG_USER:
				showNotification( 
						Notification.Type.ERROR_MESSAGE, 
						"<br/>There is no Tool User and/or Tool Owner to request the Tool!" );
			default:
				break;
			
		}
	}
	
	private void showNotification( Notification.Type type, String content ) {

		String header = "";
		
		switch ( type ) {
			case ASSISTIVE_NOTIFICATION:
				break;
			case ERROR_MESSAGE:
				header = "Error!";
				break;
			case HUMANIZED_MESSAGE:
				header = "Information!";
				break;
			case TRAY_NOTIFICATION:
				break;
			case WARNING_MESSAGE:
				header = "Warning!";
				break;
			default:
				break;
		}
		
		new Notification( header, content, type, true ).show( Page.getCurrent());
		
	}
	
}
