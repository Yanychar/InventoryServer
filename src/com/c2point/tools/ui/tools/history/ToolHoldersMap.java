/*
 * Class keeps the map of ToolTransactionsHolder 
 *   All transactions separated by ToolItem they related to
 *   All one ToolItem related Transactions have been hold by ToolTransactionsHolder object
 * 
 * 
 */
package com.c2point.tools.ui.tools.history;

import java.util.Collection;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.transactions.BaseTransaction;


public class ToolHoldersMap  extends HashMap<Long, ToolTransactionsHolder> {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( ToolHoldersMap.class.getName());

	public ToolHoldersMap() {
		super();
	}
	
	public void addTransaction( BaseTransaction trn ) {
		
		ToolItem item = trn.getToolItem();
		
		if ( item != null ) {
			
			ToolTransactionsHolder holder = this.get( item.getId());
			if ( holder == null ) {
				// New holder shall be created
				holder = new ToolTransactionsHolder( item );
				this.put( item.getId(), holder );
			}
			// Now add transaction to the holder
			holder.addTransaction( trn );
			
		} else {
			logger.error( "Transaction (id=" + trn.getId() + ") does not have ToolItem assigned but was passed here" );
		}
	}

	public Collection<BaseTransaction> getTransactions( ToolItem item ) {
		return getTransactions( item.getId());
	}
	/*
	 * Returns the list of transactions related to one ToolItem identified by ToolItem.Id
	 */
	public Collection<BaseTransaction> getTransactions( long id ) {
		
		return this.get( id ).getTransactions();
	}
	
	public Collection<ToolTransactionsHolder> getToolTransactionHolders() {
		
		return this.values();
	}
	
}
