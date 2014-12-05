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
		
		setType( type );
		setStringValue( stringValue );
	}

	public ToolIdentityType getType() { return type; }
	public void setType(ToolIdentityType type) { this.type = type; }
	
	public String getStringValue() { return stringValue; }
	public void setStringValue(String stringValue) { 
		this.stringValue = ( stringValue != null ? stringValue.trim().toUpperCase() : "" ); 
	}

	/*
	 * Business methods
	 */
	
	public static ToolIdentity createBarcodelIdentity( String barcode ) {
		
		return new ToolIdentity( ToolIdentityType.BARCODE, barcode );
	}
	
	public static ToolIdentity createSearchStringIdentity( String str ) {
		
		return new ToolIdentity( ToolIdentityType.SEARCHSTRING, str );
	}
	
	public String getBarCode() { return getStringValue(); }
	public String getSearchString() { return getStringValue(); }
	
	
	@Override
	public String toString() {
		return "ToolIdentity [type=" + type + ", stringValue=" + stringValue
				+ "]";
	}
	
}
