package com.c2point.tools.ui.repositoryview;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.ui.listeners.ToolItemChangedListener;
import com.c2point.tools.ui.util.DoubleField;
import com.c2point.tools.ui.util.IntegerField;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public class DetailsView extends FormLayout implements ToolItemChangedListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( DetailsView.class.getName());

	private ToolsListModel	model;
	
	private TextField 		toolText;
	private TextField		code;
	private TextArea 		description;
	private TextField		category;
	private TextField		manufacturer;
	private TextField		toolModel;
	
	private CheckBox		personalFlag;
	private TextField		currentUser;

	private TextField		reservedBy;
	private TextField		status;
	
	private TextField		serialNumber;
	private TextField		barcode;

	private PopupDateField	buyDate;
	private PopupDateField	nextMaintenance;
	private DoubleField		price;
	private IntegerField	takuu;
	
	private ToolItem	shownItem;

	public DetailsView( ToolsListModel model ) {
		super();
		
		setModel( model );
		
		initView();
		
		model.addChangedListener(( ToolItemChangedListener ) this );
		
	}

	private void initView() {

		this.setSpacing( true );
		this.setMargin( true );

		toolText = new TextField( model.getApp().getResourceStr( "toolsmgmt.view.label.tool" ));
		toolText.setNullRepresentation( "" );
		toolText.setImmediate( true );
		
		code = new TextField( model.getApp().getResourceStr( "toolsmgmt.view.label.code" ));
		code.setRequired(true);
		code.setRequiredError( model.getApp().getResourceStr( "toolsmgmt.view.error.fieldempty" ));
		code.setNullRepresentation( "" );
		code.setImmediate( true );

		description = new TextArea( model.getApp().getResourceStr( "toolsmgmt.view.label.descr" ));
		description.setNullRepresentation( "" );
		description.setRows( 3 );
		description.setImmediate( true );
		
		category = new TextField( model.getApp().getResourceStr( "toolsmgmt.view.label.category" ));
		category.setNullRepresentation( "" );
		category.setImmediate( true );

		manufacturer = new TextField( model.getApp().getResourceStr( "toolsmgmt.view.label.manufacturer" ));
		manufacturer.setNullRepresentation( "" );
		manufacturer.setImmediate( true );

		toolModel = new TextField( model.getApp().getResourceStr( "toolsmgmt.view.label.model" ));
		toolModel.setNullRepresentation( "" );
		toolModel.setImmediate( true );
		
		currentUser = new TextField( model.getApp().getResourceStr( "toolsmgmt.view.label.user" ));
		currentUser.setNullRepresentation( "" );
		currentUser.setImmediate( true );

		personalFlag = new CheckBox( model.getApp().getResourceStr( "toolsmgmt.view.label.personalflag" ));

		status = new TextField( model.getApp().getResourceStr( "toolsmgmt.view.label.status" ));
		status.setNullRepresentation( "" );
		status.setImmediate( true );
		
		reservedBy = new TextField( model.getApp().getResourceStr( "toolsmgmt.view.label.reservedby" ));
		reservedBy.setNullRepresentation( "" );
		reservedBy.setImmediate( true );

		serialNumber = new TextField( model.getApp().getResourceStr( "toolsmgmt.view.label.sn" ));
		serialNumber.setNullRepresentation( "" );
		serialNumber.setImmediate( true );

		barcode = new TextField( model.getApp().getResourceStr( "toolsmgmt.view.label.barcode" ));
		barcode.setNullRepresentation( "" );
		barcode.setImmediate( true );

		buyDate = new PopupDateField( "Bought" + ":" );
//		buyDate.setValue( new Date());
		buyDate.setDateFormat( "dd.MM.yyyy" );
		
		nextMaintenance = new PopupDateField( "Next Maintenance" + ":" );
		nextMaintenance.setDateFormat( "MM.yyyy" );
		nextMaintenance.setResolution( Resolution.MONTH);
		
		price = new DoubleField( "Price" + ":" );
		price.setLocale( model.getApp().getSessionData().getLocale());
		price.setMinValue( 0. );		
		
		takuu = new IntegerField( "Guarantee (months)" + ":" );
		takuu.setupMaxValue( 120 );
		
		addComponent( toolText );
		addComponent( manufacturer );
		addComponent( toolModel );
//		addComponent( code );
		addComponent( description );
		addComponent( category );
		addComponent( getSeparator());
		addComponent( personalFlag );
		addComponent( getSeparator());
		addComponent( currentUser );
		addComponent( status );
		addComponent( reservedBy );
		addComponent( serialNumber );
		addComponent( barcode );
		addComponent( getSeparator());
		
		addComponent( buyDate );
		addComponent( price );
		addComponent( takuu );
		addComponent( nextMaintenance );
		
		disallowToUpdate();
		
	}
	
	public ToolsListModel getModel() { return model; }
	public void setModel( ToolsListModel model ) { this.model = model; }

	private void allowToUpdate() {
		allowToUpdate( true );
	}
	private void disallowToUpdate() {
		allowToUpdate( false );
	}
	private void allowToUpdate( boolean allow ) {

		toolText.setReadOnly( !allow );
		code.setReadOnly( !allow );
		description.setReadOnly( !allow );
		category.setReadOnly( !allow );
		manufacturer.setReadOnly( !allow );
		toolModel.setReadOnly( !allow );
		
		personalFlag.setReadOnly( !allow );
		currentUser.setReadOnly( !allow );
		status.setReadOnly( !allow );
		reservedBy.setReadOnly( !allow );
		serialNumber.setReadOnly( !allow );
		barcode.setReadOnly( !allow );

		buyDate.setReadOnly( !allow );
		price.setReadOnly( !allow );
		takuu.setReadOnly( !allow );
		nextMaintenance.setReadOnly( !allow );
		
	}

	
	@Override
	public void wasAdded(ToolItem item) {}
	@Override
	public void wasChanged(ToolItem item) {

		if ( logger.isDebugEnabled()) logger.debug( "ToolItemView received event 'Item was changed'. Item:" + item );
		
		dataToView();
		
	}
	@Override
	public void wasDeleted(ToolItem item) {}
	@Override
	public void wholeListChanged() {}
	
	@Override
	public void currentWasSet( ToolItem item ) {

		if ( logger.isDebugEnabled()) logger.debug( "ToolItemView received event 'Item Selected'. Item:" + item );

		this.shownItem = item;
//		setVisible( item != null );
		
		dataToView();

	}

	private void dataToView() {

		allowToUpdate();		
		
		try {
			if ( this.shownItem != null ) {

				this.setVisible( true );
				
				if ( this.shownItem.getTool() != null ) {
					
					toolText.setValue( this.shownItem.getTool().getName());
					code.setValue( this.shownItem.getTool().getCode());
					description.setValue( this.shownItem.getTool().getToolInfo());
	
					category.setValue( shownItem.getTool().getCategory() != null 
							? shownItem.getTool().getCategory().getName() 
							: null );
					
					manufacturer.setValue( shownItem.getTool().getManufacturer() != null
							? shownItem.getTool().getManufacturer().getName()
							: null );
	
					toolModel.setValue( this.shownItem.getTool().getModel());
					
				}
	
				personalFlag.setValue( shownItem.isPersonalFlag());
				
				currentUser.setValue( shownItem.getCurrentUser() != null
						? shownItem.getCurrentUser().getLastAndFirstNames()
						: null );
	
				status.setValue( shownItem.getStatus().toString( model.getApp().getSessionData().getBundle()));
				
				reservedBy.setValue( shownItem.getReservedBy() != null
						? shownItem.getReservedBy().getLastAndFirstNames()
						: null );
				
				serialNumber.setValue( shownItem.getSerialNumber());
				barcode.setValue( shownItem.getBarcode());

				buyDate.setValue( shownItem.getBuyTime() != null ? shownItem.getBuyTime().toDate() : null );
				nextMaintenance.setValue( shownItem.getMaintenance() != null ? shownItem.getMaintenance().toDate() : null );
				price.setValue( shownItem.getPrice());
				takuu.setValue( shownItem.getTakuu());
				
			} else {
				// No ToolItem selected and/or Category selected

				this.setVisible( false );
			}
			
		} catch ( Exception e ) {
			logger.error( "Cannot update view: " + e.getMessage());
		}
		
		disallowToUpdate();
	}

	private Component getSeparator() {
		
		Label separator = new Label( "<hr/>", ContentMode.HTML );
		separator.setWidth( "100%" );
		
		return separator;
	}

}
