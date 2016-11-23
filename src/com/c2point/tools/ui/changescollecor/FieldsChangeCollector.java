package com.c2point.tools.ui.changescollecor;

import javax.swing.event.EventListenerList;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Label;

public class FieldsChangeCollector implements ValueChangeListener, TextChangeListener {
	private static final long serialVersionUID = 1L;

	
	private boolean 	wasChanged;
	
	private boolean 	suspended = false;

	private EventListenerList listenerList = new EventListenerList();
	
	
	public boolean wasItChanged() { return this.wasChanged;}
	public void clearChanges() { changed( false ); }
	public void setChanges() { changed( true ); }
	
	public void suspend() { this.suspended = true; }
	public void resume() { this.suspended = false; }
	
	@SuppressWarnings("rawtypes")
	public void addField( final AbstractField field ) { 
		field.addValueChangeListener( this );
		
		if ( field instanceof AbstractTextField ) {
			
			(( AbstractTextField )field ).addTextChangeListener( this );
		}
	}
	public void addField( Label field ) {
		field.addValueChangeListener( this );
	}

	@SuppressWarnings("rawtypes")
	public void removeField( final AbstractField field ) { field.removeValueChangeListener( this ); }

	public void addChangesListener( ChangesListener listener ) {
		
		listenerList.add( ChangesListener.class, listener );
	}
	
	public void removeChangesListener( ChangesListener listener ) {
		
		listenerList.remove( ChangesListener.class, listener );
	}
	
		
	private void changed( boolean wasChanged ) { 
		if ( !suspended || !wasChanged ) {
			this.wasChanged = wasChanged;
			
			if ( this.wasChanged ) {
				fireChanges();
			}
		}
	}

	private void fireChanges() {

		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ChangesListener.class) {
	    		(( ChangesListener )listeners[ i + 1 ] ).fieldWasChanged();
	         }
	     }
		
	}
	
	// ValueChangeListener method implementation
	@Override
	public void valueChange( ValueChangeEvent event) {

		changed( true );
		
	}
	@Override
	// TextChangeListener method implementation
	public void textChange(TextChangeEvent event) {

		changed( true );
		
	}
	
}
