package com.c2point.tools.ui.transactions;

import java.util.Collection;
import java.util.EventListener;

import com.c2point.tools.entity.transactions.BaseTransaction;

public interface TransactionsModelListener extends EventListener {

	public void listUpdated( Collection<BaseTransaction> list );
	public void transactionSelected( BaseTransaction trn );

}
