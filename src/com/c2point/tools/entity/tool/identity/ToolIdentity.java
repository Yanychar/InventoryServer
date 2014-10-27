package com.c2point.tools.entity.tool.identity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ToolIdentity {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( ToolIdentity.class.getName());
	
	private ToolIdentityType	type;
	private String 				stringValue;
	
	public ToolIdentity() {
		super();
	}
	
	
	
	public ToolIdentity( ToolIdentityType type, String stringValue) {
		super();
		
		this.type = type;
		this.stringValue = stringValue;
	}

	public ToolIdentityType getType() { return type; }
	public void setType(ToolIdentityType type) { this.type = type; }
	
	public String getStringValue() { return stringValue; }
	public void setStringValue(String stringValue) { this.stringValue = stringValue; }

	/*
	 * Business methods
	 */
	
	public static ToolIdentity createBarcodelIdentity( String barcode ) {
		
		return new ToolIdentity( ToolIdentityType.BARCODE, barcode );
	}
	
	public String getBarCode() { return getStringValue(); }
	
	
	@Override
	public String toString() {
		return "ToolIdentity [type=" + type + ", stringValue=" + stringValue
				+ "]";
	}
	
}
