package com.c2point.tools.entity.person;

public class Identity {

	private IDType	type;
	private String	value;
	private String	country;
	
	protected Identity() {
		
	}
	
	public Identity( IDType type, String value ) {
		
		this( type, value, null );
		
	}
	
	public Identity( IDType type, String value, String country ) {

		setType( type );
		setValue( value );
		setCountry( country );
		
	}

	public IDType getType() { return type; }
	public void setType( IDType type ) { this.type = type; }

	public String getValue() {return value;}
	public void setValue( String value ) { this.value = value; }

	public String getCountry() { return country; }
	public void setCountry( String country ) { this.country = country; }
	
	


}
