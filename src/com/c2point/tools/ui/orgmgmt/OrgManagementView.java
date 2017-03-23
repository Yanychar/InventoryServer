package com.c2point.tools.ui.orgmgmt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.ui.util.AbstractMainView;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

public class OrgManagementView extends AbstractMainView {

	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( OrgManagementView.class.getName());

	private OrgListModel		model;

	private Panel				toolbar;

	private OrgListView 		orgsList;

	private DetailsView			orgView;

	public OrgManagementView() {
		super();

	}

	@Override
	protected void initUI() {

		this.setSizeFull();
		this.setSpacing( true );
		this.setWidth( "100%" );

		this.model = new OrgListModel();

		initToolbar();
		initStuffListView();
		initDetailsView();
		
		HorizontalSplitPanel hzSplit = new HorizontalSplitPanel();
		hzSplit.setSplitPosition( 65, Unit.PERCENTAGE );
		hzSplit.setSizeFull();
		hzSplit.setLocked( false );

		hzSplit.addComponent( orgsList );
		hzSplit.addComponent( orgView );

		this.addComponent( toolbar );
		this.addComponent( hzSplit );

		this.setExpandRatio( hzSplit, 1f );

	}

	@Override
	protected void initDataAtStart() {

		this.model.initModel();

	}

	@Override
	protected void initDataReturn() {

		logger.debug( "Return into the " + this.getClass().getSimpleName());
		
//		this.model.initModel();

	}

	private void initToolbar() {

		toolbar = new Panel();
		
		HorizontalLayout content = new HorizontalLayout();
		toolbar.setContent( content );
		
		content.setWidth( "100%" );
		content.setSpacing( true );
		content.setMargin( true );

		Label glue = new Label( " " );
		glue.setHeight("100%");

		content.addComponent( glue );
		
		content .setExpandRatio( glue, 1.0f );
		
	}

	private void initStuffListView() {

		if ( logger.isDebugEnabled()) logger.debug( "Data from model will be read!" );

		orgsList = new OrgListView( this.model );


	}

	private void initDetailsView() {

		orgView = new DetailsView( this.model );


	}

}
