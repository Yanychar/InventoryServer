package com.c2point.tools.ui.transactions;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.format.DateTimeFormat;

import com.c2point.tools.entity.transactions.BaseTransaction;
import com.c2point.tools.entity.transactions.TransactionOperation;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class DetailsComponent extends VerticalLayout implements TransactionsModelListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( DetailsComponent.class.getName());
	
	private TransactionsListModel	model;
	
	private GridLayout				gl;

	public DetailsComponent( TransactionsListModel model ) {
		super();
		
		this.model = model;
		
		initUI();

		model.addChangedListener( this );
		
	}
	
	private void initUI() {

		setMargin( true );
		setSpacing( true );
		
		addComponent( getHeader());
		
		gl = new GridLayout( 2, 10 );
		gl.setSpacing( true );
		gl.setMargin( true );
		
		addComponent( gl );

	}		

	private Component getHeader() { 
	
		Label header = new Label( model.getApp().getResourceStr( "trnsmgmt.view.header" ));
		header.addStyleName( "h1" );
		
		
		return header;
	}
	
	@Override
	public void listUpdated(Collection<BaseTransaction> list) {
		
	}

	@Override
	public void transactionSelected( BaseTransaction trn ) {

		logger.debug( "DetailsComponent received transactionSelected event. Transaction: " 
						+ ( trn != null ? trn.toStringShort() : "null" ));
		showContent( trn );
		
	}


	private void showContent( BaseTransaction trn ) {

		boolean trnValid = ( trn != null );
		
		this.setVisible( trnValid );

		if ( trnValid ) {

			// Show Transaction type&operation
			addTrnComponent( trn );
			
			// Show date
			addDateComponent( trn );

			// Show who did transaction
			addInitiatorComponent( trn );
			
			addSeparator( 3 );
			
			// Show transaction description
			switch ( trn.getTrnType()) {
				case ACCESSRIGHTS:
					addARDescr( trn );
					break;
				case ACCOUNT:
					addACDescr( trn );
					break;
				case CATEGORY:
					addCATDescr( trn );
					break;
				case LOGIN:
					addLOGDescr( trn );
					break;
				case ORGANISATION:
					addORGDescr( trn );
					break;
				case TOOL:
					addTOOLDescr( trn );
					break;
				case TOOLITEM:
					addITEMDescr( trn );
					break;
				case USER:
					addUSERDescr( trn );
					break;
				default:
					clearAdditionalFields();
					break;
				
			}

		}
		
	}
	
	private void addTrnComponent( BaseTransaction trn ) {

		Label com = getLabelFromGrid( 0 );
		
		com.setValue( 
			"<b>" + trn.toItemDetails( model.getApp().getSessionData().getBundle()) + "</b>"
		);
		
	}
	
	private void addDateComponent( BaseTransaction trn ) {

		Label com = getLabelFromGrid( 1 );
		
		if ( trn.getDate() != null ) {

			com.setValue( 
				"" 
				+ "<b>"+ DateTimeFormat.forPattern( "dd.MM.yyyy  HH:mm:ss").print( trn.getDate()) + "</b>"
			);
		} else {
			com.setValue( "" );
		}
		
	}
	
	private void addInitiatorComponent( BaseTransaction trn ) {
	
		Label com = getLabelFromGrid( 2 );

		if ( trn.getUser() != null ) {
	
			com.setValue( 
				"Who did: " 
					+ "<b>"+ trn.getUser().getFirstAndLastNames() + "</b>" 
				);

		} else {
			com.setValue( "" );
		}
	}

	private void addSeparator( int row ) {
		
		Label com = getLabelFromGrid( "<hr/>", row );
		com.setWidth( "100%" );
//		com.setHeight( "2px" );
			
		
	}
	
	
	private void addARDescr( BaseTransaction trn ) {
		clearAdditionalFields();

		getLabelFromGrid( "For user:", 0, 4 );
		getLabelFromGrid( "<b>" + trn.getSourceUser().getFirstAndLastNames() + "</b>", 1, 4 );
		
	}
	private void addACDescr( BaseTransaction trn ) {
		clearAdditionalFields();
		
		getLabelFromGrid( "For user:", 0, 4 );
		getLabelFromGrid( "<b>" + trn.getSourceUser().getFirstAndLastNames() + "</b>", 1, 4 );
		
	}
	private void addCATDescr( BaseTransaction trn ) {
		clearAdditionalFields();
		
	}
	private void addLOGDescr( BaseTransaction trn ) {
		clearAdditionalFields();
		
	}
	private void addORGDescr( BaseTransaction trn ) {
		clearAdditionalFields();

		getLabelFromGrid( "Company:", 0, 4 );
		getLabelFromGrid( "<b>" + trn.getOrg().getName() + "</b>", 1, 4 );

		
	}
	private void addUSERDescr( BaseTransaction trn ) {
		clearAdditionalFields();

		getLabelFromGrid( "User:", 0, 4 );
		getLabelFromGrid( "<b>" + trn.getSourceUser().getFirstAndLastNames() + "</b>", 1, 4 );
		
	}
	
	private void addTOOLDescr( BaseTransaction trn ) {
/*		
		String content =
				  "Type: " + "<b>" + trn.getTrnType().toString( model.getApp().getSessionData().getBundle()) + "</b>" + "</br>"
				+ "Operation: " + "<b>" + trn.getTrnOperation().toString( model.getApp().getSessionData().getBundle()) + "</b>" + "</br>"
				+ "Tool: " + "<b>" + trn.getTool().getFullName() + "</b>"
				;
		
		return content;
*/
		addITEMDescr( trn );
		
	}

	private void addITEMDescr( BaseTransaction trn ) {

		
		getLabelFromGrid( "Tool:", 0, 4 );
		getLabelFromGrid( "Code:", 0, 5 );
		getLabelFromGrid( "SN:", 0, 6 );
		getLabelFromGrid( "Barcode:", 0, 7 );
		
		
		getLabelFromGrid( "<b>" + StringUtils.defaultString( trn.getTool().getFullName()) + "</b>", 	  1, 4 );
		getLabelFromGrid( "<b>" + StringUtils.defaultString( trn.getToolItem().getTool().getCode()) + "</b>",	  1, 5 );
		getLabelFromGrid( "<b>" + StringUtils.defaultString( trn.getToolItem().getSerialNumber()) + "</b>", 	  1, 6 );
		getLabelFromGrid( "<b>" + StringUtils.defaultString( trn.getToolItem().getBarcode()) + "</b>", 1, 7 );
		
		Component s = getLabelFromGrid( "<hr/>", 8 );
		s.setWidth( "100%" );
//		s.setHeight( "2px" );
		

		
		gl.removeComponent( 0, 9 );
		gl.removeComponent( 1, 9 );
		if ( trn.getTrnOperation() == TransactionOperation.NEWSTATUS ) {
			
			getLabelFromGrid( "New Status:", 0, 9 );
			getLabelFromGrid( 
					"<b>" + trn.getNewStatus().toString( model.getApp().getSessionData().getBundle()) + "</b>"
					, 1, 9 );
			
		} else if ( trn.getTrnOperation() == TransactionOperation.USERCHANGED ) {

			getLabelFromGrid( 
					"<b>" + StringUtils.defaultString( trn.getSourceUser().getFirstAndLastNames())
					+ "&emsp;>>>>&emsp; "
					+ StringUtils.defaultString( trn.getDestUser().getFirstAndLastNames())
					+ "</b>"
					, 9);
			
		}
		
	}

	private void clearAdditionalFields() {

		getLabelFromGrid( 0, 4 );
		getLabelFromGrid( 0, 5 );
		getLabelFromGrid( 0, 6 );
		getLabelFromGrid( 0, 7 );
		getLabelFromGrid( 8 );
		getLabelFromGrid( 0, 9 );

		getLabelFromGrid( 1, 4 );
		getLabelFromGrid( 1, 5 );
		getLabelFromGrid( 1, 6 );
		getLabelFromGrid( 1, 7 );
		getLabelFromGrid( 1, 9 );
		
	}
	
	
	private Label getLabelFromGrid( int row ) {
		
		return getLabelFromGrid( 0, row, true );
	}

	private Label getLabelFromGrid( int col, int row ) {
		
		return getLabelFromGrid( col, row, false );
		
	}
	
	private Label getLabelFromGrid( int col, int row, boolean wholeWidth ) {
		
		return getLabelFromGrid( "", col, row, wholeWidth ); 
		
	}
	
	private Label getLabelFromGrid( String content, int row ) {
		
		return getLabelFromGrid( content, 0, row, true ); 
		
	}
	
	private Label getLabelFromGrid( String content, int col, int row ) {
		
		return getLabelFromGrid( content, col, row, false ); 
		
	}
	
	private Label getLabelFromGrid( String content, int col, int row, boolean wholeWidth ) { 
	
		Label com = ( Label )gl.getComponent( col, row );
		
		if ( com == null ) {
		
			com = new Label( "", ContentMode.HTML );
			
			if ( wholeWidth )
				gl.addComponent( com, 0, row, gl.getColumns() - 1, row );
			
			else
				gl.addComponent( com, col, row, col, row );
			
		}
		
		com.setValue( content );
		
		return com;
	}
	
}
