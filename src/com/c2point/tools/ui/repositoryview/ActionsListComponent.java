package com.c2point.tools.ui.repositoryview;

import java.text.MessageFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.access.FunctionalityType;
import com.c2point.tools.access.PermissionType;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.ui.repositoryview.handlers.ChangeStatusHandler;
import com.c2point.tools.ui.repositoryview.handlers.CommandListener;
import com.c2point.tools.ui.repositoryview.handlers.RequestToolHandler;
import com.c2point.tools.ui.repositoryview.handlers.SendMessageHandler;
import com.c2point.tools.ui.repositoryview.handlers.SetToolUserHandler;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

public class ActionsListComponent extends VerticalLayout implements ToolsModelListener {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger( ActionsListComponent.class.getName());
	
	private ToolsListModel	model;
	
	private Button			requestButton;
	private Button			takeOverButton;
	private Button			changeStatusButton;
	private Button			sendMsgButton;
	
	public ActionsListComponent( ToolsListModel model ) {
		
		this( model, false );
		
	}
	
	public ActionsListComponent( ToolsListModel model, boolean editMode ) {
		super();
		
		this.model = model;
		
		initUI();
		
		model.addChangedListener( this );
		
	}
	
	private void initUI() {

		setSizeFull();

		setMargin( true );
//		setSpacing( true );

		Label glue = new Label( "" );
		Label separator = new Label( "<hr/>", ContentMode.HTML );
		separator.setWidth( "100%" );
		
		requestButton = addCommandButton( "request", new RequestToolHandler( model ));
		takeOverButton = addCommandButton( "takeower", new SetToolUserHandler( model ));
		changeStatusButton = addCommandButton( "changestatus", new ChangeStatusHandler( model ));
		addComponent( separator );
		sendMsgButton = addCommandButton( "sendmessage", new SendMessageHandler( model ));
		addComponent( glue );
		
		setExpandRatio( glue, 1.0f );
		
	}	

	private Button addCommandButton( String resourceSuffix, final CommandListener listener ) {

		String resourceCore = "repositorymgmt.actions.label.";
		
		Button commandButton = new Button( this.model.getApp().getResourceStr( resourceCore + resourceSuffix ));
		commandButton.addStyleName( "link" );

		commandButton.addClickListener( new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				
				CommandListener.ExitStatus exitStatus = listener.handleCommand( model.getSelectedItem());
				if ( logger.isDebugEnabled()) logger.debug( "Command button has been clicked" );
				
				if ( exitStatus != CommandListener.ExitStatus.NONE 
					&& exitStatus != CommandListener.ExitStatus.STATUS_CHANGED )
					showNotification( exitStatus, model.getSelectedItem(), null );
				
			}
		});
		
		addComponent( commandButton );
		
		return commandButton;
	}

	@Override
	public void wasChanged(ToolItem repItem) {

		updateUI();
		
	}

	@Override
	public void listWasChanged() {

		updateUI();
		
	}

	@Override
	public void selected( ToolItem repItem ) {

		logger.debug( "ActionListComponent received Tools Selection Changed event from ToolsModel" );

		updateUI();
		
	}

	private void showNotification( CommandListener.ExitStatus exitStatus, ToolItem item, OrgUser user ) {
		
		Notification.Type type = Notification.Type.ERROR_MESSAGE;
		String message = "";
		String template ;
		
		switch ( exitStatus ) {
			case ITEM_TOOKOVER:

				type = Notification.Type.HUMANIZED_MESSAGE;
				
				template = model.getApp().getResourceStr( "repositorymgmt.notify.takeover" );
				Object[] params1 = { item.getTool().getFullName() };
				message = MessageFormat.format( template, params1 );

				break;
			case FAILED_TOOKOVER:

				type = Notification.Type.ERROR_MESSAGE;
				
				template = model.getApp().getResourceStr( "repositorymgmt.error.takeover" );
				Object[] params2 = { item.getTool().getFullName() };
				message = MessageFormat.format( template, params2 );
				
				break;
			
			case MSG_SENT:
				break;
			case REQUEST_ACCEPTED:
				break;
			case REQUEST_REJECTED:
				break;
			case REQUEST_SENT:
				break;
			case WRONG_ITEM:

				type = Notification.Type.ERROR_MESSAGE;
				
				template = model.getApp().getResourceStr( "repositorymgmt.error.item" );
				Object[] params3 = { item.getTool().getFullName() };
				message = MessageFormat.format( template, params3 );
				
				break;
			
			case WRONG_USER:

				type = Notification.Type.ERROR_MESSAGE;
				
				template = model.getApp().getResourceStr( "repositorymgmt.error.user" );
				Object[] params4 = { ( user != null ? user.getFirstAndLastNames() : "" ) };
				message = MessageFormat.format( template, params4 );
				
				break;
			
			case UNKNOWN:
			default:
				type = Notification.Type.ERROR_MESSAGE;
				message = model.getApp().getResourceStr( "general.errors.unknown" );
				break;
			}
		
		showNotification( type, message ); 
		
	}
	
	private void showNotification( Notification.Type type, String content ) {

		String header = "";
		
		switch ( type ) {
			case ERROR_MESSAGE:
				header = this.model.getApp().getResourceStr( "general.error.header" );
				break;
			case ASSISTIVE_NOTIFICATION:
			case HUMANIZED_MESSAGE:
			case TRAY_NOTIFICATION:
				type = Notification.Type.HUMANIZED_MESSAGE;
				header = this.model.getApp().getResourceStr( "general.notify.header" );
				break;
			case WARNING_MESSAGE:
				header = this.model.getApp().getResourceStr( "general.warning.header" );
				break;
			default:
				break;
		}
		
		new Notification( header, content, type, true ).show( Page.getCurrent());
		
	}

	private void updateUI() {

		boolean itemSelected = model.getSelectedItem() != null;
		boolean itemOwned = itemSelected 
						 && model.getSelectedItem().getCurrentUser().getId() == model.getSessionOwner().getId();
		
		PermissionType takeOverPerm = model.getSecurityContext().getPermission( FunctionalityType.BORROW, itemOwned );   
		PermissionType changeStatusPerm = model.getSecurityContext().getPermission( FunctionalityType.CHANGESTATUS, itemOwned );   
		PermissionType msgPerm = model.getSecurityContext().getPermission( FunctionalityType.MESSAGING, itemOwned );   
		
		requestButton.setEnabled( itemSelected && false );
		takeOverButton.setEnabled( itemSelected && takeOverPerm == PermissionType.RW );
		changeStatusButton.setEnabled( itemSelected && changeStatusPerm == PermissionType.RW );
		sendMsgButton.setEnabled( itemSelected && msgPerm == PermissionType.RW && false ); 
		
	}
	
}
