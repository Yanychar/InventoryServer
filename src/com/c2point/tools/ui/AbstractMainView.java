package com.c2point.tools.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.InventoryUI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public abstract class AbstractMainView extends VerticalLayout {
	
	private static Logger logger = LogManager.getLogger( AbstractMainView.class.getName());
	
	protected InventoryUI	ui;

	private enum InitType { NOT_STARTED, NOT_INIT, INIT } ;
	private InitType state;

	public AbstractMainView() {
		this( null );
	}
	
	public AbstractMainView( InventoryUI ui ) {
		
		logger.debug( "AbstractMainView created" );
		
		this.ui = ui;

		state = InitType.NOT_STARTED;
		
		initUI();
	}

	protected abstract void initUI();
	
	public final void initData( ) {
		if ( state == InitType.NOT_STARTED ) {
			state = InitType.NOT_INIT;
		} // else 
		if ( state == InitType.NOT_INIT ) {
			logger.debug( "First time initialization..." );
			initDataAtStart();
			state = InitType.INIT;
		} else if ( state == InitType.INIT ) {
			logger.debug( "Repeate initialization..." );
			initDataReturn();
		}
	}
	
	protected abstract void initDataAtStart();
	protected abstract void initDataReturn();
	
	public InventoryUI getInventoryUI() { return ui; }
}
