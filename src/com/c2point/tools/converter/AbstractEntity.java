package com.c2point.tools.converter;

import java.util.Locale;

public abstract class AbstractEntity {

	public enum EntityType { COMMENT, EMPTY, PROPERTY }

	private EntityType	type;
	private String	name;
	private String	value;
	
	protected AbstractEntity( EntityType type ) {

		this( type, type.toString());
		
	}
	
	protected AbstractEntity( EntityType type, String name ) {

		this( type, name, null );
		
	}
	
	protected AbstractEntity( EntityType type, String name , String value  ) {
		super();
		
		setType( type );
		setName( name );
		setName( value );
	}
	
	public EntityType getType() { return type; }
	public void setType( EntityType type ) { this.type = type; }
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public String getValue() { return value; }
	public String getValue( Locale locale) { return getValue(); }
	public void setValue(String value) { this.value = value; }
	
	public String toString() {
		
		return getName() + " = '" + getValue() + "'";
	}
	
	public String toString( Locale [] localeList ) {
		
		return toString();
	}

	
}
