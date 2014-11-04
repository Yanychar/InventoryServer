package com.c2point.tools.entity.location;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum LocationStatus {

	UNKNOWN( 0 );
	
	
	private static Logger logger = LogManager.getLogger( LocationStatus.class.getName());
	
	private final int value;  

	private LocationStatus( int value ) {  
		this.value = value;
	}  
	public int value() {  
		return value;  
	}  

    public static LocationStatus fromValue( int value ) {
        for( LocationStatus item : values())   {
            if ( value == item.value )
                return item;
        }
        
                
        return null;
    }

    
    public static LocationStatus fromValue( String strValue ) {
    	try {
    		int intValue = Integer.parseInt( strValue );
    		return fromValue( intValue );
    	} catch ( Exception e ) {
			logger.error( "Passed string: '" + strValue + "' cannot be converted into integer!" );
			return LocationStatus.UNKNOWN;
    	}

    }
	
}
