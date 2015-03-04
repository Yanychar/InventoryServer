package com.c2point.tools.converter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertyEntity extends AbstractEntity {

	private static Logger logger = LogManager.getLogger( PropertyEntity.class.getName());
	
	private Map<Locale, String> map;  
	
	private PropertyEntity() {
		
		super( EntityType.PROPERTY );
		
	}

	public PropertyEntity( String name ) {
		
		this();
		
		setName( name );
	}
	
	public String getValue() { return null; }
	
	public String getValue( Locale locale ) {
		
		return this.map.get( locale );
		
	}

	public boolean set( Locale locale, String value ) {

		if ( this.map == null ) {
			this.map = new HashMap<Locale, String>();
			
		}
		
		if ( this.map.containsKey( locale )) {
			
			String oldValue = this.map.get( locale );
			
			if ( oldValue == null && value == null 
				|| 
				oldValue != null && oldValue.compareTo( value ) == 0 ) {
				
			} else {
				
				logger.debug( "Value shall be updated. Locale " + locale );
				logger.debug( "  Old: '" + oldValue + "'. New value: '" + value + "'" );
				map.put( locale, value );
			}
		} else {
			map.put( locale, value );
		}
		
		
		return true;
	}


	public String toString() {
		
		String str = getName() + ": ";
		
		Iterator< String > iter = map.values().iterator();
		
		while ( iter.hasNext()) {
			str = str.concat( "\t" + iter.next());
		}
		
		
		return str;
	}

	public String toString( Locale [] localeList ) {

		String str = getName() + ": ";
		
		
		for( Locale locale : localeList ) {
			
			str = str.concat( "\t" + getValue( locale ));
		}
		
		
		return str;
		
	}
	
	
}
