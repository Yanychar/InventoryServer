package com.c2point.tools.ui.tools.history;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.format.DateTimeFormat;

import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Tool;
import com.c2point.tools.entity.transactions.BaseTransaction;
import com.c2point.tools.entity.transactions.TransactionOperation;
import com.c2point.tools.ui.tools.history.ToolsHistoryListModel.ViewMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

public class DetailsComponent extends Panel implements ToolsHistoryModelListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( DetailsComponent.class.getName());
	
	private ToolsHistoryListModel	model;
	
	private GridLayout 				content;
	
	private Label					date;
	private Label					who;
	private Label					type;
	private Label					operation;

	private Label					fieldName_1;
	private Label					fieldValue_1;
	private Label					fieldName_2;
	private Label					fieldValue_2;
	
	public DetailsComponent( ToolsHistoryListModel model ) {
		super();
		
		this.model = model;
		
		initUI();

		model.addChangedListener( this );
		
	}
	
	private void initUI() {

		content = new GridLayout( 5, 4 );
		setContent( content );
		content.setWidth( "100%" );
		content.setColumnExpandRatio( 0, 0.5f );
		content.setColumnExpandRatio( 1, 1 );
		content.setColumnExpandRatio( 2, 0.5f );
		content.setColumnExpandRatio( 3, 1 );
		content.setColumnExpandRatio( 4, 5 );
		
		content.setWidth( "100%" );
		content.setSpacing( true );
		content.setMargin( true );
		
		content.addComponent( new Label( model.getApp().getResourceStr( "trnsmgmt.label.date" ) + ":" ), 0, 0 );
		content.addComponent( new Label( model.getApp().getResourceStr( "trnsmgmt.label.who" ) + ":" ), 2, 0 );
		
		Label separator = new Label( "<hr/>", ContentMode.HTML );
		separator.setWidth( "100%" );
		content.addComponent( separator, 0, 1, 4, 1 );
		
		content.addComponent( new Label( model.getApp().getResourceStr( "trnsmgmt.label.type" ) + ":" ), 0, 2 );
		content.addComponent( new Label( model.getApp().getResourceStr( "trnsmgmt.label.operation" ) + ":" ), 0, 3 );

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

	@Override
	public void modelWasRead() {
//		clearContent();			
	}

	@Override
	public void userSelected( OrgUser user ) {
		clearContent();			
	}

	@Override
	public void transactionSelected( BaseTransaction trn ) {

		logger.debug( "DetailsComponent received transactionSelected event. Transaction: " 
						+ ( trn != null ? trn.toStringShort() : "null" ));
		
//		this.getContent().setVisible( trn != null );
		
		if ( trn != null ) {
			date.setValue( "<b>" + DateTimeFormat.forPattern("dd.MM.yyyy HH:mm").print( trn.getDate()) + "</b>");
			who.setValue( "<b>" + trn.getUser().getFirstAndLastNames() + "</b>");
			type.setValue( "<b>" + trn.toItemDetails( model.getApp().getSessionData().getBundle()) + "</b>");
//			operation.setValue( "<b>" + trn.getTrnOperation().toString( model.getApp().getSessionData().getBundle()) + "</b>");
			
			if ( trn.getTrnOperation() == TransactionOperation.NEWSTATUS ) {

				fieldName_1.setValue( model.getApp().getResourceStr( "trnsmgmt.label.status" ) + ":" );
				fieldValue_1.setValue( "<b>" + trn.getNewStatus().toString( model.getApp().getSessionData().getBundle()) + "</b>" );
				
				fieldName_2.setValue( "" );
				fieldValue_2.setValue( "" );
				
			} else if ( trn.getTrnOperation() == TransactionOperation.USERCHANGED ) {
				
				fieldName_1.setValue( model.getApp().getResourceStr( "trnsmgmt.label.from" ) + ":" );
				fieldValue_1.setValue( "<b>" + trn.getSourceUser().getFirstAndLastNames() + "</b>" );
				
				fieldName_2.setValue( model.getApp().getResourceStr( "trnsmgmt.label.to" + ":" ));
				fieldValue_2.setValue( "<b>" + trn.getDestUser().getFirstAndLastNames() + "</b>" );
			
			} else {
			
				fieldName_1.setValue( "" );
				fieldValue_1.setValue( "" );
				fieldName_2.setValue( "" );
				fieldValue_2.setValue( "" );
				
			}
		} else {
			clearContent();			
		}
		
	}

	@Override
	public void viewTypeChanged(ViewMode mode) { }
	@Override
	public void toolSelected( Tool tool ) { }
	@Override
	public void toolItemSelected( ToolItem toolItem ) { }
	
	private void clearContent() {

		date.setValue( "" );
		who.setValue( "" );
		type.setValue( "" );
		operation.setValue( "" );

		fieldName_1.setValue( "" );
		fieldValue_1.setValue( "" );
		fieldName_2.setValue( "" );
		fieldValue_2.setValue( "" );
		
	}

}
