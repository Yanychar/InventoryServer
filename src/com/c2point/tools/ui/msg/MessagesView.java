package com.c2point.tools.ui.msg;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;

public class MessagesView extends HorizontalLayout {
	
	private static final long serialVersionUID = 1L;

//	private MessageboxesListComponent	messageBoxesComponent;
	private MessageListComponent		messagesComponent;
	
	private MessagingModel			model;

	public MessagesView() {
		super();

		initModel();
		
		initUI();
	}
	
	public void initUI() {
		
		setWidth( "100%" );
		this.setHeight( "100%" );

		Component component;
/*
		component = createMessageboxesComponent();
		this.addComponent( component );
		this.setExpandRatio( component, 0.3f );
*/		
		component = createMessagesListComponent();
		this.addComponent( component );
//		this.setExpandRatio( component, 0.7f );
		this.setExpandRatio( component, 0.1f );
		
//		messageboxesComponent.selectInbox();
	}
/*	
	private Component createMessageboxesComponent() {
		
		messageBoxesComponent = new MessageBoxesListComponent( this.model );

		Panel panel = new Panel();
		panel.setContent( messageBoxesComponent );

		return panel;
	}
*/
	private Component createMessagesListComponent() {
		
		messagesComponent = new MessageListComponent( this.model );

		Panel panel = new Panel( "List of Messages" );
		panel.setContent( messagesComponent );
		
		return panel;
	}

	
	
	
	
	
	
	private void initModel() {
		
		this.model = new MessagingModel();
	
	}
	
	
	
}
