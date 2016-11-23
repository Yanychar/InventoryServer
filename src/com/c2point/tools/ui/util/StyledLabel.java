package com.c2point.tools.ui.util;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

public class StyledLabel extends Label {
	private static final long serialVersionUID = 1L;

	private String style;
	private String value;
	
	public StyledLabel( String style ) {
		this( null, style );
	}

	public StyledLabel( String str, String style ) {
		super();
		
		if ( !StringUtils.isBlank( style ) ) {
			
			setStyle( style );
		}
		setValue( str );
	}

	public String getValue() { return this.value; }
	public void setValue( String str ) {

		this.value = str;
		if ( !StringUtils.isBlank( this.value )) {
			super.setValue( 
				( style != null ? "<" + getStyle() + ">" : "" ) 
				+ str 
				+ ( style != null ? "</" + getStyle() + ">" : "" ) 
			);
		} else {
			super.setValue( "" );
		}
	}

	public String getStyle() { return style; }
	public void setStyle( String style ) { 
	
		if ( style != getStyle()) {
			this.style = style;
			setValue( this.value );
			
			if ( !StringUtils.isBlank( this.style )) {
				setContentMode( ContentMode.HTML );
			} else {
				setContentMode( ContentMode.TEXT );
			}
		}
	}
	
	
}
