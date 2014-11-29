package com.c2point.tools.entity.repository;

import java.util.ResourceBundle;

import javax.xml.bind.annotation.XmlEnum;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@XmlEnum(String.class)
public enum ItemStatus {

	UNKNOWN( 0 ),
	FREE( 1 ),		// AVAILABLE
	INUSE( 2 ),		// In Use
	BROCKEN( 3 ),	//
	REPAIRING( 4 ),
	STOLEN( 5 ),
	RESERVED( 6 );
	
	
	private static Logger logger = LogManager.getLogger( ItemStatus.class.getName());
	
	private final int value;  

	private ItemStatus( int value ) {  
		this.value = value;  
	}  
	public int value() {  
		return value;  
	}  

    public static ItemStatus fromValue( int value ) {
        for( ItemStatus item : values())   {
            if ( value == item.value )
                return item;
        }
        return null;
    }

    
    public static ItemStatus fromValue( String strValue ) {
    	try {
    		int intValue = Integer.parseInt( strValue );
    		return fromValue( intValue );
    	} catch ( Exception e ) {
			logger.error( "Passed string: '" + strValue + "' cannot be converted into integer!" );
    	}

    	return null;
    }
	
    public String toString( ResourceBundle bundle ) {
    	
    	try {
    		return bundle.getString( "tool.item.status." + this.toString().toLowerCase());
    	} catch ( Exception e ) {
    		
    	}
    	
    	return this.toString();
    	
    }
	
}
