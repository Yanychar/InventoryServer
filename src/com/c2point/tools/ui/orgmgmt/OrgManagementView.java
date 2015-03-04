package com.c2point.tools.ui.orgmgmt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.ui.AbstractMainView;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class OrgManagementView extends AbstractMainView {

	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( OrgManagementView.class.getName());


	private OrgListModel		model;

	private HorizontalLayout	toolbar;
	private ComboBox			filter;

	private OrgListView 		stuffList;

	private OrgView			stuffView;

	public OrgManagementView() {
		super();

	}

	@Override
	protected void initUI() {

		this.setSizeFull();
		this.setSpacing( true );

		this.model = new OrgListModel();

		initStuffView();
		
		if ( model.isOrgListSupported()) {

			initToolbar();
			initStuffListView();

			VerticalLayout vtSplit = new VerticalLayout();
			HorizontalSplitPanel hzSplit = new HorizontalSplitPanel();


			hzSplit.addComponent( stuffList );
			hzSplit.addComponent( stuffView );

			hzSplit.setSplitPosition( 65, Unit.PERCENTAGE );

			vtSplit.addComponent( toolbar );
			vtSplit.addComponent( hzSplit );

			vtSplit.setHeight( "100%" );
			vtSplit.setExpandRatio( hzSplit, 1f );

			this.addComponent( vtSplit );
			
		} else {

			stuffView.setSizeFull();

			this.addComponent( stuffView );
			
		}
		
		

	}

	@Override
	protected void initDataAtStart() {

		logger.debug( "Initial entrance into the " + this.getClass().getSimpleName());
		
		this.model.initModel();

	}

	@Override
	protected void initDataReturn() {

		logger.debug( "Return into the " + this.getClass().getSimpleName());
		
		this.model.initModel();

	}

	private void initToolbar() {

		toolbar = new HorizontalLayout();
		toolbar.setWidth( "100%" );
		toolbar.setSpacing( true );
		toolbar.setMargin( true );

		Label filterLabel = new Label( "Filter:" );
		filter = new ComboBox();

		Label glue = new Label( " " );
		glue.setHeight("100%");


		toolbar.addComponent( filterLabel );
		toolbar.addComponent( filter );
		toolbar.addComponent( glue );

		toolbar .setExpandRatio( glue, 1.0f );


	}

	private void initStuffListView() {

		if ( logger.isDebugEnabled()) logger.debug( "Data from model will be read!" );

		stuffList = new OrgListView( this.model );


	}

	private void initStuffView() {

		stuffView = new OrgView( this.model );


	}

}
