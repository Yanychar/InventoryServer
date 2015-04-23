package com.c2point.tools.ui.tools.history;

import java.util.EventListener;

import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Tool;
import com.c2point.tools.entity.transactions.BaseTransaction;

public interface ToolsHistoryModelListener extends EventListener {

	public void viewTypeChanged( ToolsHistoryListModel.ViewMode mode );
	public void modelWasRead();
	public void toolItemSelected( ToolItem toolItem );
	public void toolSelected( Tool tool );
	public void userSelected( OrgUser user );
	public void transactionSelected( BaseTransaction trn );

}
