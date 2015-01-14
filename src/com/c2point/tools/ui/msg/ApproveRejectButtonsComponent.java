package com.c2point.tools.ui.msg;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.InventoryUI;
import com.c2point.tools.entity.msg.Message;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;

@SuppressWarnings("serial")
public class ApproveRejectButtonsComponent extends CustomComponent {

	@SuppressWarnings("unused")
	private static Logger 	logger = LogManager.getLogger( ApproveRejectButtonsComponent.class.getName());
	
	private InventoryUI app;
	
	private Message 	msg;

	private Button 		approveButton;
	private Button 		rejectButton;
	
	public ApproveRejectButtonsComponent( InventoryUI app, Message msg ) {
		super();
		
		this.app = app;
		
		initView();

		setMsg( msg );
		
//		updateButtonView( report );
	}

	
	private void initView() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing( true );
		setCompositionRoot( layout );
	
		approveButton = new Button();
		approveButton.addStyleName( "smallroundicon" );
		approveButton.setIcon( new ThemeResource( "icons/16/approve.png" ));
		approveButton.setImmediate( true );
		
		rejectButton = new Button();
		rejectButton.addStyleName( "smallroundicon" );
		rejectButton.setIcon( new ThemeResource( "icons/16/reject.png" ));
		rejectButton.setImmediate( true );
		
		configureButtons( layout );
		
	}

	public Message getMsg() { return this.msg; }
	public void setMsg( Message msg ) {
		this.msg = msg;
		
		configureButtons(( HorizontalLayout )this.getCompositionRoot());
	}
	
	public void addApproveListener( Button.ClickListener listener ) {
		approveButton.addClickListener( listener );
	}
	
	public void addRejectListener( Button.ClickListener listener ) {
		rejectButton.addClickListener( listener );
	}

	private void configureButtons( HorizontalLayout layout ) {

		try {
			layout.removeComponent( approveButton );
		} catch ( Exception e ) {}
		try {
			layout.removeComponent( rejectButton );
		} catch ( Exception e ) {}
		
		
		if ( getMsg() != null ) {
			switch (msg.getType()) {
				case REQUEST:

					approveButton.setCaption( "Approve" );
					rejectButton.setCaption( "Reject" );
					
					layout.addComponent( approveButton );
					layout.addComponent( rejectButton );

					break;
				case AGREEMENT:
					
					approveButton.setCaption( "Got It!" );
					rejectButton.setCaption( "Cancel" );
					
					layout.addComponent( approveButton );
					layout.addComponent( rejectButton );

					break;
				case REJECTION:
					break;
				case CONFIRMATION:

					approveButton.setCaption( "Ok" );
					
					layout.addComponent( approveButton );

					break;
				case INFO:

					break;
				case TEXT:
					break;
				default:
					break;
				
			}
		}
	}
	
}
