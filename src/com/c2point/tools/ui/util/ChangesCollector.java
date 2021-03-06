package com.c2point.tools.ui.util;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractField;

public class ChangesCollector {

	@SuppressWarnings("rawtypes")
	class Pair {
		AbstractField field;
		ValueChangeListener listener;

		Pair( AbstractField field, ValueChangeListener listener ) {
			this.field = field;
			this.listener = listener;
		}
	}
	
	private List<Pair>	listenersList = new ArrayList<Pair>();
	private boolean 	wasChangedFlag;
	
	private boolean 	suspendedToListen = false;

	
	
	public boolean wasItChanged() { return this.wasChangedFlag;}
	public void clearChanges() { this.wasChangedFlag = false; }
	
	public void stopToListen() { 
		this.suspendedToListen = true; 
	}
	public void startToListen() { 
		this.suspendedToListen = false;
		clearChanges();		
	}
	
	@SuppressWarnings("rawtypes")
	public void listenForChanges( final AbstractField field ) {

		ValueChangeListener listener = new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {

				changed();

			}

		};

		field.addValueChangeListener( listener );
		listenersList.add( new Pair( field, listener ));

	}
/*
	public void listenForChanges( Button ownership ) {
		
		new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				changed();
				
			}};
		
	}
*/

	public void changed() { changed( true ); }
	public void changed( boolean wasChangedFlag ) { if ( !suspendedToListen ) this.wasChangedFlag = wasChangedFlag; }

	
}
