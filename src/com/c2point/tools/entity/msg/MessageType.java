package com.c2point.tools.entity.msg;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum(String.class)
public enum MessageType { 
	UNKNOWN( 0 ),
	TEXT( 1 ), 
	REQUEST( 2 ), 
	AGREEMENT( 3 ),
	REJECTION( 4 ),
	CONFIRMATION( 5 ),
	INFO( 6 );
	
	private int value = 0;
	
	MessageType( int value ) {
		this.value = value;
		
	}
	
	public int value() {
		return this.value;
	}

	public static MessageType fromValue( int i ) {
		for ( MessageType type : MessageType.values()) {
            if ( type.value == i ) {
                return type;
            }
        }
        throw new IllegalArgumentException( Integer.toString( i ));
	}
	
	public static MessageType fromValue( String str ) {
		
		return valueOf( str );
	}
	
	

};
