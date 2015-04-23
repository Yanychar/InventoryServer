/*
 * Class keeps the list of Transactions concerned one ToolItem   
 * 
 * 
 */

package com.c2point.tools.ui.tools.history;

import java.util.ArrayList;
import java.util.Collection;

import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.transactions.BaseTransaction;

public class ToolTransactionsHolder {

	private ToolItem	toolItem;
	
	private Collection<BaseTransaction> trnsList;
	
	public ToolTransactionsHolder( ToolItem	toolItem ) {
		
		setTool( toolItem );
		
		trnsList = new ArrayList<BaseTransaction>();
	}

	public ToolItem getTool() { return toolItem; }
	public void setTool( ToolItem toolItem ) { this.toolItem =  toolItem; }

	public void addTransaction( BaseTransaction trn ) {
		
		if ( trn != null )
			trnsList.add( trn );
	}
	
	public Collection<BaseTransaction> getTransactions() { return trnsList; }
}
