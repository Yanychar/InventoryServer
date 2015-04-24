package com.c2point.tools.ui.transactions;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.format.DateTimeFormat;

import com.c2point.tools.entity.transactions.BaseTransaction;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class DetailsComponent extends VerticalLayout implements TransactionsModelListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( DetailsComponent.class.getName());
	
	private TransactionsListModel	model;
	
	private Label					date;
	private Label					who;
	private Label					type;
	private Label					operation;

	public DetailsComponent( TransactionsListModel model ) {
		super();
		
		this.model = model;
		
		initUI();

		model.addChangedListener( this );
		
	}
	
	private void initUI() {

		addComponent( getHeader());
		addComponent( getSeparator());
		addComponent( getUserDate());
		addComponent( getSeparator());
		addComponent( getTransactionType());
		addComponent( getSeparator());
	}		

	private Component getHeader() { 
	
		Label header = new Label( model.getApp().getResourceStr( "trnsmgmt.view.header" ));
		header.addStyleName( "h1" );
		
		
		return header;
	}
	private Component getUserDate() { return new Label(); }
	private Component getTransactionType() { return new Label(); }
	private Label getSeparator() {
		
		Label separator = new Label( "<hr/>", ContentMode.HTML );
		separator.setWidth( "100%" );
		
		return separator;
	}
	
/*	
	content.addComponent( separator, 0, 1, 4, 1 );
		
		
		
		GridLayout content = new GridLayout( 5, 4 );
//		setContent( content );
		
		content.setWidth( "100%" );
		content.setColumnExpandRatio( 0, 0.5f );
		content.setColumnExpandRatio( 1, 1 );
		content.setColumnExpandRatio( 2, 0.5f );
		content.setColumnExpandRatio( 3, 1 );
		content.setColumnExpandRatio( 4, 5 );
		
		content.setWidth( "100%" );
		content.setSpacing( true );
		content.setMargin( true );
		
		content.addComponent( new Label( model.getApp().getResourceStr( "trnsmgmt.label.date" )), 0, 0 );
		content.addComponent( new Label( model.getApp().getResourceStr( "trnsmgmt.label.who" )), 2, 0 );
		
		Label separator = new Label( "<hr/>", ContentMode.HTML );
		separator.setWidth( "100%" );
		content.addComponent( separator, 0, 1, 4, 1 );
		
		content.addComponent( new Label( model.getApp().getResourceStr( "trnsmgmt.label.type" )), 0, 2 );
		content.addComponent( new Label( model.getApp().getResourceStr( "trnsmgmt.label.operation" )), 0, 3 );

		// Now fields with variable content will be added
		date = new Label( "", ContentMode.HTML );
		date.setImmediate( true );
		
		who = new Label( "", ContentMode.HTML );
		who.setImmediate( true );
		
		content.addComponent( date, 		1, 0 );		
		content.addComponent( who, 			3, 0 );		
		
		type = new Label( "", ContentMode.HTML );
		type.setImmediate( true );
		
		operation = new Label( "", ContentMode.HTML );
		operation.setImmediate( true );
		
		content.addComponent( type, 		1, 2 );		
		content.addComponent( operation, 	1, 3 );
		
		fieldName_1 = new Label( "" );
		fieldName_1.setImmediate( true );
		fieldValue_1 = new Label( "", ContentMode.HTML );
		fieldValue_1.setImmediate( true );
		
		fieldName_2 = new Label( "" );
		fieldName_2.setImmediate( true );
		fieldValue_2 = new Label( "", ContentMode.HTML );
		fieldValue_2.setImmediate( true );
		
		content.addComponent( fieldName_1, 		2, 2 );		
		content.addComponent( fieldValue_1, 	3, 2 );
		content.addComponent( fieldName_2, 		2, 3 );		
		content.addComponent( fieldValue_2, 	3, 3 );

		content.setComponentAlignment( content.getComponent( 0, 0 ), Alignment.MIDDLE_LEFT );
		content.setComponentAlignment( content.getComponent( 1, 0 ), Alignment.MIDDLE_LEFT );
		content.setComponentAlignment( content.getComponent( 2, 0 ), Alignment.MIDDLE_LEFT );
		content.setComponentAlignment( content.getComponent( 3, 0 ), Alignment.MIDDLE_LEFT );
		content.setComponentAlignment( content.getComponent( 0, 2 ), Alignment.MIDDLE_LEFT );
		content.setComponentAlignment( content.getComponent( 1, 2 ), Alignment.MIDDLE_LEFT );
		content.setComponentAlignment( content.getComponent( 2, 2 ), Alignment.MIDDLE_LEFT );
		content.setComponentAlignment( content.getComponent( 3, 2 ), Alignment.MIDDLE_LEFT );
		content.setComponentAlignment( content.getComponent( 0, 3 ), Alignment.MIDDLE_LEFT );
		content.setComponentAlignment( content.getComponent( 1, 3 ), Alignment.MIDDLE_LEFT );
		content.setComponentAlignment( content.getComponent( 2, 3 ), Alignment.MIDDLE_LEFT );
		content.setComponentAlignment( content.getComponent( 3, 3 ), Alignment.MIDDLE_LEFT );
		
//		this.getContent().setVisible( false );
		clearContent();			
		
	}
*/
	private void showContent( boolean toShow ) {

		date.setVisible( toShow ); //.setValue( "" );
		who.setVisible( toShow );
		type.setVisible( toShow );
		operation.setVisible( toShow );

	}

	@Override
	public void listUpdated(Collection<BaseTransaction> list) {
		
	}

	@Override
	public void transactionSelected( BaseTransaction trn ) {

		logger.debug( "DetailsComponent received transactionSelected event. Transaction: " 
						+ ( trn != null ? trn.toStringShort() : "null" ));
/*		
		showContent( trn != null );
		
		if ( trn != null ) {

			date.setValue( "<b>" + DateTimeFormat.forPattern("dd.MM.yyyy HH:mm").print( trn.getDate()) + "</b>");
			who.setValue( "<b>" + trn.getUser().getFirstAndLastNames() + "</b>");
			type.setValue( "<b>" + trn.getTrnType().toString( model.getApp().getSessionData().getBundle()) + "</b>");
			operation.setValue( "<b>" + trn.getTrnOperation().toString( model.getApp().getSessionData().getBundle()) + "</b>");
			
		}
*/		
	}


}
