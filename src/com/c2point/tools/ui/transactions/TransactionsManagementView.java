package com.c2point.tools.ui.transactions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Tool;
import com.c2point.tools.entity.transactions.BaseTransaction;
import com.c2point.tools.ui.AbstractMainView;
import com.c2point.tools.ui.transactions.TransactionsListModel.ViewMode;
import com.vaadin.ui.HorizontalSplitPanel;

public class TransactionsManagementView extends AbstractMainView implements TransactionModelListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( TransactionsManagementView.class.getName());

	
	private TransactionsListModel		model;
	
	private ViewSelectorComponent	viewSelector; 
	private ToolsListComponent		toolsListComp; 
	private UsersListComponent		usersListComp; 
	private TrnsListComponent		trnsListComp; 
	private DetailsComponent		detailsComp; 

	private HorizontalSplitPanel	trnsSplit;
	
	public TransactionsManagementView() {
		super();

	}

	
	@Override
	protected void initUI() {

		this.setSizeFull();
		this.setSpacing( true );
		this.setWidth( "100%" );

		this.model = new TransactionsListModel();

		this.model.initModel();
		
		viewSelector = new ViewSelectorComponent( this.model ); 
		detailsComp = new DetailsComponent( this.model );
		
		toolsListComp = new ToolsListComponent( this.model );
		usersListComp = new UsersListComponent( this.model );
		
		trnsListComp = new TrnsListComponent( this.model );

		
		trnsSplit  = new HorizontalSplitPanel();
		trnsSplit.setSplitPosition( 40, Unit.PERCENTAGE );
		trnsSplit.setSizeFull();
		trnsSplit.setLocked( false );

		this.addComponent( viewSelector );
		this.addComponent( trnsSplit );
		this.addComponent( detailsComp );

		this.setExpandRatio( trnsSplit, 1f );
		
		model.addChangedListener( this );
		
		viewSelector.selectViewMode( ViewMode.PERSONNEL );
//		viewTypeChanged( model.getViewMode());
		
		
	}

	@Override
	protected void initDataAtStart() {
	}

	@Override
	protected void initDataReturn() {
	}


	@Override
	public void viewTypeChanged( ViewMode mode ) {

		if ( trnsSplit.getFirstComponent() != null ) {
			trnsSplit.removeComponent( trnsSplit.getFirstComponent());
		}
		if ( trnsSplit.getSecondComponent() == null ) {
			trnsSplit.setSecondComponent( trnsListComp );
		}
		
		if ( mode == ViewMode.PERSONNEL ) {

			trnsSplit.setFirstComponent( toolsListComp );
//			trnsSplit.setSecondComponent( trnsListComp );
			
		} else if ( mode == ViewMode.TOOLS ) {

			trnsSplit.setFirstComponent( usersListComp );
//			trnsSplit.setSecondComponent( trnsListComp );
			
		} else {
			logger.error( "Wrong ViewMode selected" );
		}
 
		
	}


	@Override
	public void modelWasRead() {}
	@Override
	public void toolSelected( Tool tool ) {}
	@Override
	public void toolItemSelected( ToolItem toolItem ) {}
	@Override
	public void userSelected(OrgUser user) {}
	@Override
	public void transactionSelected(BaseTransaction trn) {}

}
