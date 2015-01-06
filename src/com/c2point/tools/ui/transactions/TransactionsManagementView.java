package com.c2point.tools.ui.transactions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.ui.AbstractMainView;
import com.vaadin.ui.HorizontalSplitPanel;

public class TransactionsManagementView extends AbstractMainView {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( TransactionsManagementView.class.getName());

	
	private TransactionsListModel		model;
	
	private ViewSelectorComponent		viewSelector; 
	private ToolsListComponent	toolsListComp; 
	private TrnsListComponent	trnsListComp; 
	private DetailsComponent	detailsComp; 

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
		trnsListComp = new TrnsListComponent( this.model );
		
		HorizontalSplitPanel trnsSplit  = new HorizontalSplitPanel();
		trnsSplit.setSplitPosition( 40, Unit.PERCENTAGE );
		trnsSplit.setSizeFull();
		trnsSplit.setLocked( false );

		trnsSplit.setFirstComponent( toolsListComp );
		trnsSplit.setSecondComponent( trnsListComp );
		
		this.addComponent( viewSelector );
		this.addComponent( trnsSplit );
		this.addComponent( detailsComp );

		this.setExpandRatio( trnsSplit, 1f );
		
	}

	@Override
	protected void initDataAtStart() {

		
	}

	@Override
	protected void initDataReturn() {
		// TODO Auto-generated method stub
		
	}

}
