package com.c2point.tools.ui.tools.history;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDate;

import com.c2point.tools.datalayer.SettingsFacade;
import com.c2point.tools.datalayer.ToolsFacade;
import com.c2point.tools.datalayer.TransactionsFacade;
import com.c2point.tools.datalayer.UsersFacade;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Tool;
import com.c2point.tools.entity.transactions.BaseTransaction;
import com.c2point.tools.ui.AbstractModel;

public class ToolsHistoryListModel extends AbstractModel {
	private static Logger logger = LogManager.getLogger( ToolsHistoryListModel.class.getName());

	public enum ViewMode { PERSONNEL, TOOLS };
	private ViewMode		mode;

	private Organisation 	org;
	
	private Date		dateStart;
	private Date		dateEnd;
	
	private OrgUser		selectedUser;

	private Tool		selectedTool;

	private ToolHoldersMap	toolsHolder;
	
	
	public ToolsHistoryListModel() {
		this( null );

		
	}
	
	public ToolsHistoryListModel( Organisation org ) {
		super();
		
		setOrg( org != null ? org : getApp().getSessionData().getOrg());
	
	}
	
	public void initModel() {
		
		
		// Read default value of time period in months
		int lengthInMonths = SettingsFacade.getInstance().getInteger( org, "periodInMonths", 6 );
		
		setDateStart( new LocalDate().minusMonths( lengthInMonths ).toDate(), false );
		setDateEnd( new Date(), false );

//		reReadData();

		// Initial model initialization here if necesary
		setViewMode( ViewMode.PERSONNEL, false );
		
	}
	
	public void addChangedListener( ToolsHistoryModelListener listener ) {
		listenerList.add( ToolsHistoryModelListener.class, listener );
	}
	
	protected void fireViewModeChanged() {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ToolsHistoryModelListener.class) {
	    		(( ToolsHistoryModelListener )listeners[ i + 1 ] ).viewTypeChanged( this.mode );
	    	}
		}
	}

	protected void fireDataRead() {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ToolsHistoryModelListener.class) {
	    		(( ToolsHistoryModelListener )listeners[ i + 1 ] ).modelWasRead();
	    	}
		}
	}
	
	protected void fireToolItemSelected( ToolItem selectedTool ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ToolsHistoryModelListener.class) {
	    		(( ToolsHistoryModelListener )listeners[ i + 1 ] ).toolItemSelected( selectedTool );
	        }
	    }
	}

	protected void fireTransactionSelected( BaseTransaction trn ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ToolsHistoryModelListener.class) {
	    		(( ToolsHistoryModelListener )listeners[ i + 1 ] ).transactionSelected( trn );
	    	}
	    }
	}

	
	public Organisation getOrg() { return org; }
	public void setOrg( Organisation org ) { this.org = org; }
	
	public ViewMode getViewMode() { return this.mode; }
	public void setViewMode( ViewMode mode, boolean reRead ) {
		
		logger.debug( "ViewMode has been changed to: " + mode );
		this.mode = mode;
		fireViewModeChanged();
		
		if ( reRead )
			readData();
	}
	public void setViewMode( Object mode, boolean reRead ) {
		
		if ( mode instanceof ViewMode ) {
			setViewMode(( ViewMode )mode, reRead );
		} else {
			logger.error( "Object of wrong class passed" );
		}
	}
	
	public Date getDateStart() { return dateStart; }
	public void setDateStart( Date dateStart ) { 
		setDateStart( dateStart, true );
	}
	public void setDateStart( Date dateStart, boolean readNecessary ) { 
	
		if ( this.dateStart != dateStart ) {
			this.dateStart = dateStart;

			if ( readNecessary )
				readData();
		}

	}

	public Date getDateEnd() { return dateEnd; }
	public void setDateEnd( Date dateEnd ) { 
		setDateEnd( dateEnd, true );
	}
	public void setDateEnd( Date dateEnd, boolean readNecessary ) {
		if ( this.dateEnd != dateEnd ) {
			this.dateEnd = dateEnd; 

			if ( readNecessary )
				readData();
		}

	}
	
	public OrgUser getSelectedUser() { return selectedUser; }

	/*
	 * Set new selected user and re-read data if necessary
	 */
	public void setSelectedUser( OrgUser selectedUser ) {
		
		if ( mode == ViewMode.PERSONNEL ) {
	
//			if ( this.selectedUser != selectedUser ) {
			
				this.selectedUser = selectedUser; 
				
				readData();
//			}
		} else if ( mode == ViewMode.TOOLS ) {
	
		}
	}
	
	public void setSelectedUser( Object user ) { 
		if ( user instanceof OrgUser ) {
			setSelectedUser(( OrgUser )user );
		} else {
			logger.error( "Object of wrong class passed" );
		}
	}
	
	public Tool	getSelectedTool() { return this.selectedTool; }
	
	public void	setSelectedTool( Tool selectedTool ) {
		
		if ( mode == ViewMode.TOOLS ) {
			
			this.selectedTool = selectedTool;

			readData();

		}
	}
	public void	setSelectedTool( Object selectedTool ) {
		
		if ( selectedTool instanceof Tool ) {
			
			setSelectedTool(( Tool )selectedTool );
			
		} else {
			
			logger.error( "Wrong data passed to model: " + selectedTool.getClass().getSimpleName());
		}
		
	}

	public void	setSelectedToolItem( ToolItem selectedToolItem ) {
		
		fireToolItemSelected( selectedToolItem );

	}
	
	
	/*
	 * Read data from DB to (re-)initialize the model
	 */
	public void readData() {

		logger.debug( "Start reReadData..." );

		// If Personnel based view has been selected
		if ( mode == ViewMode.PERSONNEL ) {
			
			readPersonnelBasedData();
			
		} else if ( mode == ViewMode.TOOLS ) {
			
			readToolsBasedData();
			
		} else {
			logger.error( "Wrong ViewMode stored!!!" );
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void	selectTransaction( BaseTransaction trn ) {
		if ( logger.isDebugEnabled()) logger.debug( "Fire transactionSelected with trn = " + trn );
		fireTransactionSelected( trn );
	}
	
	public Collection<OrgUser> getUsers() {
		
		return UsersFacade.getInstance().list( getOrg());
		
	}

	public Collection<Tool> getTools() {
		
		return ToolsFacade.getInstance().getTools( getOrg());
	}

	private Collection<ToolTransactionsHolder> getToolTransactionsHolders() {
		
		if ( toolsHolder != null ) {

			return toolsHolder.getToolTransactionHolders();
		}

		return null;
	}
/*	
	private Collection<BaseTransaction> getTransactions() {
		
		if ( mode == ViewMode.PERSONNEL ) {
			
			return TransactionsFacade.getInstance().getTransactions( selectedUser, dateStart, dateEnd );
			
		} else if ( mode == ViewMode.TOOLS ) {
			
		} else {
			
		}
		
		return null;
		
	}
*/
	/*
	 *  Returns the list of ToolItems were related to the transactions found earlier 
	 */
	public Collection<ToolItem> getPreparedToolItems() {
		
		Collection<ToolItem> list = new ArrayList<ToolItem>();
		
		if ( getToolTransactionsHolders() != null ) {
			
			// Fetch ToolItems from TransactionHolders found earlier
			for( ToolTransactionsHolder holder : getToolTransactionsHolders()) {
				
				if ( holder != null && holder.getTool() != null ) {
					list.add( holder.getTool());
				}
				
			}
		}
		
		return list;
		
	}
	
	/*
	 * Returns all Transactions about ToolItem have been hold by particular ToolTransactionsHolder 
	 */
	public Collection<BaseTransaction> getTransactions( ToolTransactionsHolder holder ) {
		
		Collection<BaseTransaction> list = new ArrayList<BaseTransaction>();
		
		if ( holder != null ) {
			list.addAll( holder.getTransactions());
		}
		
		return list;
		
	}
	
	/*
	 * Returns all Transactions about ToolItem specified. 
	 *  
	 */
	public Collection<BaseTransaction> getTransactions( ToolItem item ) {

		Collection<BaseTransaction> list = new ArrayList<BaseTransaction>();
		
		if ( this.toolsHolder != null && item != null ) {
			list.addAll( this.toolsHolder.getTransactions( item ));
		}
		
		return list;
		
	}
	
	/*
	 * Returns all Transactions for the selected ToolItem (this.selectedTool) 
	 *  
	 */
/*	
	public Collection<BaseTransaction> getTransactions() {

		
		return getTransactions( this.selectedTool );
		
	}
*/
	private void readPersonnelBasedData() {
		
		// Read transaction if the user selected already
		if ( selectedUser != null ) {
			logger.debug( "  Selected user != null. Data will be read" );

			selectedTool = null;
			toolsHolder = new ToolHoldersMap();
			
			// Read all transactions for particular user in specified time period
			TransactionsFacade tf = TransactionsFacade.getInstance();
//			Collection<BaseTransaction> trnsList = TransactionsFacade.getInstance()
			Collection<BaseTransaction> trnsList = tf
					.getTransactions( selectedUser, dateStart, new Date( dateEnd.getTime() + 1000 * 60*60*24 ));
		
			if ( trnsList != null && trnsList.size() > 0 ) {
				// Create the set of ToolItems and related Transactions from read above
				
				for( BaseTransaction trn : trnsList ) {
					
					toolsHolder.addTransaction( trn );
					
				}
				
				logger.debug( "  Number of transactions read: " + trnsList.size());
				
			} else {
				logger.debug( "  Number of transactions read: 0. No transactions were read" );
			}
		
			// Send event to clear the list of transactions
			fireToolItemSelected( null );
			// Fire model changed event
			fireDataRead();
			
			
		} else {
			logger.debug( "  Selected user == null. Data will NOT be read" );
		}
	
	}
	
	private void readToolsBasedData() {

		// Read transaction if Tool selected already
		if ( this.selectedTool != null ) {
			logger.debug( "  Selected Tool != null. Data will be read" );

			selectedUser = null;
			toolsHolder = new ToolHoldersMap();
			
			// Read all transactions for particular user in specified time period
			Collection<BaseTransaction> trnsList = TransactionsFacade.getInstance()
					.getTransactions( selectedTool, dateStart, new Date( dateEnd.getTime() + 1000 * 60*60*24 ));
		
			if ( trnsList != null && trnsList.size() > 0 ) {
				// Create the set of ToolItems and related Transactions from read above
				
				for( BaseTransaction trn : trnsList ) {
					
					toolsHolder.addTransaction( trn );
					
				}
				
				logger.debug( "  Number of transactions read: " + trnsList.size());
				
			} else {
				logger.debug( "  Number of transactions read: 0. No transactions were read" );
			}
		
			// Send event to clear the list of transactions
			fireToolItemSelected( null );
			// Fire model changed event
			fireDataRead();
			
			
		} else {
			logger.debug( "  Selected user == null. Data will NOT be read" );
		}
		
	}
	
}
