package com.c2point.tools.entity.msg;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum(String.class)
public enum MessageStatus { 
	
	UNKNOWN( 0 ),
	CREATED( 1 ), 
	SENT( 2 ), 
	UNREAD( 3 ), 
	READ( 4 ),
	RESPONDED( 5 );
	
	private int value = 0;
	
	MessageStatus( int value ) {
		this.value = value;
		
	}
	
	public int value() {
		return this.value;
	}

	public static MessageStatus fromValue( int i ) {
		for ( MessageStatus type : MessageStatus.values()) {
            if ( type.value == i ) {
                return type;
            }
        }
        throw new IllegalArgumentException( Integer.toString( i ));
	}
	
	public static MessageStatus fromValue( String str ) {
		
		return valueOf( str );
	}
	
	public boolean isLowerThan( MessageStatus status ) {
	
		return ( this.value() < status.value());
	}
	
};
