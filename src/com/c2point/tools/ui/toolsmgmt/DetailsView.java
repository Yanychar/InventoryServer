package com.c2point.tools.ui.toolsmgmt;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.ui.listeners.ToolItemChangedListener;
import com.c2point.tools.ui.util.BoldLabel;
import com.c2point.tools.ui.util.CustomGridLayout;

public class DetailsView extends CustomGridLayout implements ToolItemChangedListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( DetailsView.class.getName());

	private ToolsListModel	model;
	private ToolItem		shownItem;
	
	private BoldLabel		manufacturer;
	private BoldLabel		toolModel;
	private BoldLabel 		toolText;
	private BoldLabel		category;
	
	private BoldLabel		personalFlag;
	private BoldLabel		currentUser;

	private BoldLabel		status;
	
	private BoldLabel		serialNumber;
	private BoldLabel		barcode;
	private BoldLabel		comments;
	
	private BoldLabel		buyDate;
	private BoldLabel		nextMaintenance;
	private BoldLabel		price;
	private BoldLabel		takuu;
	
	
	
	public DetailsView( ToolsListModel model ) {
		super();
		
		setModel( model );
		
		
		initView();
		
		model.addListener(( ToolItemChangedListener ) this );
		
	}

	private void initView() {

		this.setSpacing( true );
		this.setMargin( true );

		manufacturer = new BoldLabel();
		manufacturer.setImmediate( true );

		toolModel = new BoldLabel();
		toolModel.setImmediate( true );
		
		toolText = new BoldLabel();
		toolText.setImmediate( true );
		
		category = new BoldLabel();
		category.setImmediate( true );

		personalFlag = new BoldLabel();
		personalFlag.setImmediate( true );
		
		currentUser = new BoldLabel();
		currentUser.setImmediate( true );

		status = new BoldLabel();
		status.setImmediate( true );
		
		serialNumber = new BoldLabel();
		serialNumber.setImmediate( true );
		
		barcode = new BoldLabel();
		barcode.setImmediate( true );
		
		comments = new BoldLabel();
		comments.setImmediate( true );
		
		buyDate = new BoldLabel();
		buyDate.setImmediate( true );
		
		nextMaintenance = new BoldLabel();
		nextMaintenance.setImmediate( true );
		
		price = new BoldLabel();
		price.setImmediate( true );
		
		takuu = new BoldLabel();
		takuu.setImmediate( true );
		
		
		
		
		manufacturer = addLabelValueComponent( model.getApp().getResourceStr( "toolsmgmt.view.label.manufacturer" ));
		toolModel = addLabelValueComponent( model.getApp().getResourceStr( "toolsmgmt.view.label.model" ));
		toolText = addLabelValueComponent( model.getApp().getResourceStr( "toolsmgmt.view.label.tool" ));
		category = addLabelValueComponent( model.getApp().getResourceStr( "toolsmgmt.view.label.category" ));
		addSeparator();
		
		status = addLabelValueComponent( model.getApp().getResourceStr( "toolsmgmt.view.label.status" ));
		currentUser = addLabelValueComponent( model.getApp().getResourceStr( "toolsmgmt.view.label.user" ));
		barcode = addLabelValueComponent( model.getApp().getResourceStr( "toolsmgmt.view.label.barcode" ));
		addSeparator();

		personalFlag = addLabelValueComponent( model.getApp().getResourceStr( "toolsmgmt.view.label.personalflag" ));
		serialNumber = addLabelValueComponent( model.getApp().getResourceStr( "toolsmgmt.view.label.sn" ));
		buyDate = addLabelValueComponent( "Bought" + ":" );
		nextMaintenance = addLabelValueComponent( "Next Maintenance" + ":" );
		price = addLabelValueComponent( "Price" + ":" );
		takuu = addLabelValueComponent( "Guarantee (months)" + ":" );
		comments = addLabelValueComponent( model.getApp().getResourceStr( "toolsmgmt.view.label.iteminfo" ));
		
		addSeparator();
		
	}
	
	private BoldLabel addLabelValueComponent( String labelCaption ) {
		
		BoldLabel resComp = new BoldLabel();

		addField( labelCaption, resComp );
		
		return resComp;
	}

	public ToolsListModel getModel() { return model; }
	public void setModel( ToolsListModel model ) { this.model = model; }

	
	@Override
	public void wasAdded(ToolItem item) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void wasChanged(ToolItem item) {

		if ( logger.isDebugEnabled()) logger.debug( "ToolItem DetailsView received event 'ToolItem was edited': " + item );

		this.shownItem = item;
		
		dataToView();
		
	}
	@Override
	public void wasDeleted(ToolItem item) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void wholeListChanged() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void currentWasSet( ToolItem item ) {

		if ( logger.isDebugEnabled()) logger.debug( "ToolItem DetailsView received event 'ToolItem was selected': " + item );

		this.shownItem = item;
		
		dataToView();

	}

	private void dataToView() {

		setVisible( this.shownItem != null );
	
		if ( this.shownItem != null ) {
			
			if ( this.shownItem.getTool() != null ) {
				
				try { manufacturer.setValue( StringUtils.defaultString( this.shownItem.getTool().getManufacturer().getName())); } 
				catch( Exception e ) { manufacturer.setValue( "" ); }

				try { toolModel.setValue( StringUtils.defaultString( this.shownItem.getTool().getModel())); }
				catch( Exception e ) { toolModel.setValue( "" ); }

				toolText.setValue( StringUtils.defaultString( this.shownItem.getTool().getName())
						+ "\n"
						+ StringUtils.defaultString( this.shownItem.getTool().getToolInfo())
					  );
				
				try { category.setValue( this.shownItem.getTool().getCategory().getName()); }
				catch( Exception e ) { category.setValue( "" ); }
				
				personalFlag.setValue( shownItem.isPersonalFlag() ? "X" : "-" );
		
				try { currentUser.setValue( this.shownItem.getCurrentUser().getFirstAndLastNames()); }
				catch( Exception e ) { currentUser.setValue( "" ); }

				status.setValue( shownItem.getStatus().toString( model.getApp().getSessionData().getBundle()));
				
				serialNumber.setValue( StringUtils.defaultString( shownItem.getSerialNumber()));
				barcode.setValue( StringUtils.defaultString( shownItem.getBarcode()));
				comments.setValue( StringUtils.defaultString( this.shownItem.getComments()));
				
				buyDate.setValue( shownItem.getBuyTime() != null ? shownItem.getBuyTime().toString( "dd.MM.yyyy" ) : "" );
				nextMaintenance.setValue( shownItem.getMaintenance() != null ? shownItem.getMaintenance().toString( "MM.yyyy" ) : "" );

				try { price.setValue( shownItem.getPrice().toString()); }
				catch( Exception e ) { price.setValue( "" ); }

				try { takuu.setValue( shownItem.getTakuu().toString()); }
				catch( Exception e ) { takuu.setValue( "" ); }
				
			}

			

		} else {
			if ( logger.isDebugEnabled()) logger.debug( "No selection. Dataview shall be cleared" );

			toolText.setValue( "" );
			manufacturer.setValue( null );
			toolModel.setValue( "" );
			category.setValue( null );

			personalFlag.setValue( "" );
			currentUser.setValue( "" );
			status.setValue( "" );
			serialNumber.setValue( "" );
			barcode.setValue( "" );
			comments.setValue( "" );
			buyDate.setValue( "" );
			nextMaintenance.setValue( "" );
			price.setValue( "" );
			takuu.setValue( "" );

		}
		
	}

}
