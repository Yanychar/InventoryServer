package com.c2point.tools.utils;

import java.util.HashMap;

public class MapForCountedCaptions extends HashMap<String, IncrementalInt> {
	private static final long serialVersionUID = 1L;

	public String getUnicCaption( String caption ) {
		
		IncrementalInt counter = this.get( caption );
		
		if ( caption != null ) {
			if ( counter == null ) {
				put( caption, new IncrementalInt());
			} else {
				caption = caption.concat( "   (" + counter + ")" );
				counter.increment();
			}
		} else {
			return "";
		}
		
		return caption;
	}

}
