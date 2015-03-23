package com.c2point.tools.ui;

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
	
	public boolean wasItChanged() { return this.wasChangedFlag;}
	public void clearChanges() { changed( false ); }
	
	private void changed() { changed( true ); }
	private void changed( boolean wasChangedFlag ) {this.wasChangedFlag = wasChangedFlag; }

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
	
	public void stopListeningForChanges() {

		for( Pair pair : listenersList ) {
			pair.field.removeValueChangeListener( pair.listener );
		}

	}

}