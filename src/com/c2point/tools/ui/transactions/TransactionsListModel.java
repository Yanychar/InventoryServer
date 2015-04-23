package com.c2point.tools.ui.transactions;

import java.util.Collection;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.c2point.tools.datalayer.TransactionsFacade;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.transactions.BaseTransaction;
import com.c2point.tools.ui.AbstractModel;

public class TransactionsListModel extends AbstractModel {
	private static Logger logger = LogManager.getLogger( TransactionsListModel.class.getName());

	private Organisation 	org;
	
	private BaseTransaction		selectedTransaction;

	private TrnsFilter		filter;
	
	public TransactionsListModel() {
		this( null );
		
	}
	
	public TransactionsListModel( Organisation org ) {
		super();
		
		setOrg( org != null ? org : getApp().getSessionData().getOrg());
		
		filter = new TrnsFilter( org );
	
	}
	
	public void addChangedListener( TransactionsModelListener listener ) {
		listenerList.add( TransactionsModelListener.class, listener );
	}
	
	protected void fireDataRead( Collection<BaseTransaction> list ) {
		
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == TransactionsModelListener.class) {
	    		(( TransactionsModelListener )listeners[ i + 1 ] ).listUpdated( list );	    		
	    	}
		}
	}
	
	protected void fireTransactionSelected( BaseTransaction trn ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == TransactionsModelListener.class) {
	    		(( TransactionsModelListener )listeners[ i + 1 ] ).transactionSelected( trn );
	    	}
	    }
	}

	
	public Organisation getOrg() { return org; }
	public void setOrg( Organisation org ) { this.org = org; }
	
	public void	selectTransaction( BaseTransaction trn ) {
		if ( logger.isDebugEnabled()) logger.debug( "Fire transactionSelected with trn = " + trn );
		fireTransactionSelected( trn );
	}

	public void setSelectedOrg( Organisation org ) {
		
		if ( getOrg() != org ) {
			
			setOrg( org );
			
			readData();			
		}
	}
	
	
	/*
	 * Read data from DB to (re-)initialize the model
	 */
	public Collection<BaseTransaction> readData() {

		logger.debug( "Start readData..." );

		// Read transaction 
		TransactionsFacade tf = TransactionsFacade.getInstance();

		Collection<BaseTransaction> trnsList = tf
					.getTransactions( org, filter.getDateStart(), new Date( filter.getDateEnd().getTime() + 1000 * 60*60*24 ));
		
		if ( trnsList != null && trnsList.size() > 0 ) {
			// Create the set of ToolItems and related Transactions from read above
			
			logger.debug( "  Number of transactions read: " + trnsList.size());
			
		} else {
			logger.debug( "  Number of transactions read: 0. No transactions were read" );
		}
	
		// Send event to clear the list of transactions
		fireTransactionSelected( null );
		// Fire model changed event
		fireDataRead( trnsList );
			
		return trnsList;
	}
	
	public Date getDateStart() { return filter.getDateStart(); }
	public void setDateStart( Date dateStart ) { 
		setDateStart( dateStart, true );
	}
	public void setDateStart( Date dateStart, boolean readNecessary ) { 
	
		if ( filter.getDateStart() != dateStart ) {
			filter.setDateStart( dateStart );

			if ( readNecessary )
				readData();
		}

	}

	public Date getDateEnd() { return filter.getDateEnd(); }
	public void setDateEnd( Date dateEnd ) { 
		setDateEnd( dateEnd, true );
	}
	public void setDateEnd( Date dateEnd, boolean readNecessary ) {
		if ( filter.getDateEnd() != dateEnd ) {
			filter.setDateEnd( dateEnd ); 

			if ( readNecessary )
				readData();
		}

	}
	

		
	
}
