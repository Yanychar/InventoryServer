package com.c2point.tools.ui.transactions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

import com.c2point.tools.entity.transactions.BaseTransaction;
import com.c2point.tools.ui.AbstractMainView;
import com.vaadin.ui.HorizontalSplitPanel;

public class TransactionsManagementView extends AbstractMainView implements TransactionsModelListener {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( TransactionsManagementView.class.getName());

	private TransactionsListModel	model;
	
	private FilterComponent			filterComp;
	private TrnsListComponent		trnsListComp ;
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

		filterComp = new FilterComponent( this.model ); 
		detailsComp = new DetailsComponent( this.model );
		
		trnsListComp = new TrnsListComponent( this.model );

		
		trnsSplit  = new HorizontalSplitPanel();
		trnsSplit.setSplitPosition( 60, Unit.PERCENTAGE );
		trnsSplit.setSizeFull();
		trnsSplit.setLocked( false );

		trnsSplit.addComponent( trnsListComp );
		trnsSplit.addComponent( detailsComp );
		
		this.addComponent( filterComp );
		this.addComponent( trnsSplit );

		this.setExpandRatio( trnsSplit, 1f );
		
//		model.addChangedListener( this );
		
		
	}
	
	@Override
	protected void initDataAtStart() {
		
		model.readData();
		
	}
	
	@Override
	protected void initDataReturn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void listUpdated(Collection<BaseTransaction> list) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void transactionSelected(BaseTransaction trn) {
		// TODO Auto-generated method stub
		
	}

}
