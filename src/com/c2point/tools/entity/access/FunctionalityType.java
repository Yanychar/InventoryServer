package com.c2point.tools.entity.access;

public enum FunctionalityType {

	BORROW( 1 ),
	CHANGESTATUS( 2 ),
	MESSAGING( 3 ),
	USERS_MGMT( 4 ),
	TOOLS_MGMT( 5 ),
	ORGS_MGMT( 6 );
	
	private int value = 0;
	
	FunctionalityType( int value ) {
		this.value = value;
		
	}
	
	public int value() {
		return this.value;
	}

	public static FunctionalityType fromValue( int i ) {
		for ( FunctionalityType type : FunctionalityType.values()) {
            if ( type.value == i ) {
                return type;
            }
        }
        throw new IllegalArgumentException( Integer.toString( i ));
	}
	
	public static FunctionalityType fromValue( String str ) {
		
		return valueOf( str );
	}
	
}
