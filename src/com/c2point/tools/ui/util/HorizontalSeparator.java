package com.c2point.tools.ui.util;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

public class HorizontalSeparator extends Label {
	private static final long serialVersionUID = 1L;

	public HorizontalSeparator() {
			
		super("<hr/>", ContentMode.HTML);
		
		setWidth( "100%" );
		
		setHeight( "10px");
		
		setImmediate( true );

	}
		
}
