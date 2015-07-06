package com.c2point.tools.entity.access;

public enum FunctionalityType {

	BORROW,
	CHANGESTATUS,
	MESSAGING,
	USERS_MGMT,
	TOOLS_MGMT,
	ORGS_MGMT,
	TRN_MGMT,
	ACCOUNTS_MGMT;
	
/*	
	public static FunctionalityType fromValue( int i ) {
		
		FunctionalityType.
		
		
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
	
*/	
	
}
