package com.c2point.tools.entity.access;

public enum OwnershipType {

	OWN( 1 ), 
	COMPANY( 2 ), 
	ANY( 3 );
	
	private int value = 0;
	
	OwnershipType( int value ) {
		this.value = value;
		
	}
	
	public int value() {
		return this.value;
	}

	public static OwnershipType fromValue( int i ) {
		for ( OwnershipType type : OwnershipType.values()) {
            if ( type.value == i ) {
                return type;
            }
        }
        throw new IllegalArgumentException( Integer.toString( i ));
	}
	
	public static OwnershipType fromValue( String str ) {
		
		return valueOf( str );
	}
	
}
