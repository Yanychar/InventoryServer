package com.c2point.tools.ui.upload;

public enum ProcessedStatus {

	// Correct statuses
	COMMENT( 1 ), 
	VALIDATED( 2 ),
	PROCESSED( 3 ),
	
	// Failure statuses
	EXIST( -1 ),
	WRONG_FIELDS_COUNT( -2 ),
	WRONG_FIELD_FORMAT( -3 ),
	NO_CAT_SPECIFIED( -4 ),
	VALIDATION_FAILED( -5 ),
	PERSON_NOT_FOUND( -6 ),
	TOOL_ITEM_EXIST( -7 ),
	FAILED( -8 );
	
	private int value;
	
	private ProcessedStatus( int i ) {
		
		this.value = i;
		
	}
	
	public int getValue() { return value; }
	
	
	public boolean isValid() { return this.getValue() > 0; }
	
	public boolean isFailure() { return this.getValue() < 0; }

}
