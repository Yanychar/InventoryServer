package com.c2point.tools.entity.person;

public enum IDType {
	Tunnus( 1 ), TaxNumber( 2 ), Unknown( -1 );
	
	private int value = -1;
	
	IDType( int value ) {
		this.value = value;
		
	}
	
	public int value() {
		return this.value;
	}

	public static IDType fromValue( int i ) {
		for ( IDType type : IDType.values()) {
            if ( type.value == i ) {
                return type;
            }
        }
        throw new IllegalArgumentException( Integer.toString( i ));
	}
};
