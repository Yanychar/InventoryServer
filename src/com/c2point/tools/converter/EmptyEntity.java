package com.c2point.tools.converter;

public class EmptyEntity extends AbstractEntity {

	public EmptyEntity() {
		
		super( EntityType.EMPTY );
		
	}

	public String getName() { return ""; }
	public String getValue() { return ""; }
	
	public String toString() {
		
		return getValue();
	}
	
}
