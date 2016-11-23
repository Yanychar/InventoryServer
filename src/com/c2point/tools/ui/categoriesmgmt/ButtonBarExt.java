package com.c2point.tools.ui.categoriesmgmt;

import com.c2point.tools.ui.changescollecor.ChangesListener;
import com.c2point.tools.ui.changescollecor.FieldsChangeCollector;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class ButtonBarExt extends HorizontalLayout implements ChangesListener {
	private static final long serialVersionUID = 1L;

	enum ButtonType { OK, CANCEL, SELECT, ADD, EDIT, DEL, SEARCH };
	
	private Button			okButton;
	private Button			cancelButton;
	private Button			selectButton;

	private Button			addButton;
	private Button			editButton;
	private Button			delButton;
	
	protected TextField		searchText;
	
	private ButtonPressListenerExt		buttonsListener = null;

	
	public ButtonBarExt() { 
		this( null );
	}
	
	public ButtonBarExt( ButtonPressListenerExt buttonsListener ) { 

		this.buttonsListener = buttonsListener;
		
		okButton = new Button( "OK" );
		cancelButton = new Button( "Cancel" );

		okButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				if ( ButtonBarExt.this.buttonsListener != null ) ButtonBarExt.this.buttonsListener.okPressed();
			}
			
			
		});
		
		cancelButton.addClickListener( new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				if ( ButtonBarExt.this.buttonsListener != null ) ButtonBarExt.this.buttonsListener.cancelPressed();
			
			}
			
			
		});
		
		addComponent( okButton );
		addComponent( cancelButton );
		
		updateButtons();
		
	}

	public void addButtonsListener( ButtonPressListenerExt buttonsListener ) { this.buttonsListener = buttonsListener; }
	
	public void addOk() {
		
	}
	
	private void addButton( ButtonType type ) {
		
//		Component comp = getByType( type)
	
		
	}
	
	
	private Component getByType( ButtonType type ) { return getByType( type, false ); }
	private Component getByType( ButtonType type, boolean canCreate ) {
		
		Component ret = null;
		
		switch ( type ) {
		
			case ADD:
				if ( addButton == null && canCreate ) {
					addButton = new Button( "Add" ); 
				}
				ret = addButton;
				break;
			case CANCEL:
				if ( cancelButton == null && canCreate ) {
					cancelButton = new Button( "Cancel" ); 
				}
				ret = cancelButton;
				break;
			case DEL:
				if ( delButton == null && canCreate ) {
					delButton = new Button( "Delete" ); 
				}
				ret = delButton;
				break;
			case EDIT:
				if ( editButton == null && canCreate ) {
					editButton = new Button( "Edit" ); 
				}
				ret = editButton;
				break;
			case OK:
				if ( okButton == null && canCreate ) {
					okButton = new Button( "Ok" ); 
				}
				ret = okButton;
				break;
			case SEARCH:
				if ( searchText == null && canCreate ) {
					searchText = new TextField(); 
				}
				ret = searchText;
				break;
			case SELECT:
				if ( selectButton == null && canCreate ) {
					selectButton = new Button( "Select" ); 
				}
				ret = selectButton;
				break;
			default:
				break;
			
			
		}
		
		return ret;
	}
	
	public Button getOk() { return okButton; }
	public Button getCancel() { return cancelButton; }
	public Button getSelect() { return selectButton; }

	public Button getAdd() { return addButton; }
	public Button getEdit() { return editButton; }
	public Button getDel() { return delButton; }

	public TextField getSearch() { return searchText; }
	
	public void enableOk( boolean enabled ) { getOk().setEnabled( enabled ); }
	public void enableCancel( boolean enabled ) { getCancel().setEnabled( enabled ); }
	public void enableSelect( boolean enabled ) { getCancel().setEnabled( enabled ); }

	public void enableAdd( boolean enabled ) { getAdd().setEnabled( enabled ); }
	public void enableEdit( boolean enabled ) { getEdit().setEnabled( enabled ); }
	public void enableDel( boolean enabled ) { getDel().setEnabled( enabled ); }
	
	public void enableSearch( boolean enabled ) { getSearch().setEnabled( enabled ); }

	@Override
	public void fieldWasChanged() {
		// TODO Auto-generated method stub
		
	}
	
	private void updateButtons() {
		// TODO Auto-generated method stub
		
	}

	
}
