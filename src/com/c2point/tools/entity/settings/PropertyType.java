package com.c2point.tools.entity.settings;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum PropertyType {

	UNKNOWN,
	BOOLEAN,
	INT,
	LONG,
	STRING;
	
	private static Logger logger = LogManager.getLogger( PropertyType.class.getName()); 
	
	
	public static PropertyType getType( Object value ) {
		
		PropertyType type = PropertyType.UNKNOWN;
		
		if ( value instanceof Boolean ) {
			type = PropertyType.BOOLEAN;
		} else if ( value instanceof Integer ) {
			type = PropertyType.INT;
		} else if ( value instanceof Long ) {
			type = PropertyType.LONG;
		} else if ( value instanceof String ) {
			type = PropertyType.STRING;
		} else {
			logger.error( "Unsupported property type of value: '" + value.getClass().getSimpleName() + "'");
		}
		
		return type;
		
	}
}
