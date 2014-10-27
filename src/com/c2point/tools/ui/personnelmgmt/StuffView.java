package com.c2point.tools.ui.personnelmgmt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.person.OrgUser;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;

public class StuffView extends GridLayout implements StuffChangedListener {
	private static Logger logger = LogManager.getLogger( StuffView.class.getName());

	
	private StuffListModel	model;
	
	private TextField	code;
	private TextField 	firstName;
	private TextField 	lastName;
	private DateField 	birthday;

	private TextField	street;
	private TextField	pobox;
	private TextField	index;
	private TextField	city;
	private TextField	countryCode;

	private TextField	description;
	
	private TextField	email;
	private TextField	mobile;
	
	private boolean		editMode;	 
	
	public StuffView( StuffListModel model ) {
	
		setModel( model );
		
		model.addChangedListener( this );
		
		setEditMode( false );
	}
	
	public StuffListModel getModel() { return model; }
	public void setModel( StuffListModel model ) { this.model = model; }

	public boolean isEditMode() { return editMode; }
	public void setEditMode( boolean editMode ) { this.editMode = editMode; }

	
	private void showUser() {
		
	}
	



	@Override
	public void currentWasSet( OrgUser user ) {
		
		if ( !isEditMode()) {
			showUser();
		}
		
	}
	
	
	@Override
	public void wasAdded(OrgUser user) {}
	@Override
	public void wasChanged(OrgUser user) {}
	@Override
	public void wasDeleted(OrgUser user) {}
	@Override
	public void wholeListChanged() {}


}
