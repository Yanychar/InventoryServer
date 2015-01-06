package com.c2point.tools.ui.transactions;

import java.util.EventListener;

import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.transactions.BaseTransaction;

public interface TransactionModelListener extends EventListener {

	public void viewTypeChanged( TransactionsListModel.ViewMode mode );
	public void modelWasRead();
	public void toolSelected( ToolItem toolItem );
	public void userSelected( OrgUser user );
	public void transactionSelected( BaseTransaction trn );

}
