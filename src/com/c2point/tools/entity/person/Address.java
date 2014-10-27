package com.c2point.tools.entity.person;

import javax.persistence.Embeddable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;


@Embeddable
public class Address {

	private String street;
	private String poBox;
	private String index;
	private String city;
	private String countryCode;
	
	private String description;
	
	public Address() {
		
		setCountryCode( "FI" );
		
	}

	public Address( String oneLineAddress ) {
		
		setDescription( oneLineAddress );
		
	}

	public Address( String street, String postalCode, String city, String countryCode ) {
		
		this( street, null, postalCode, city, countryCode );

	}

	public Address( String street, String poBox, String index, String city,
			String countryCode ) {
		super();
		setStreet( street );
		setPoBox( poBox );
		setIndex( index );
		setCity( city );
		setCountryCode( countryCode );
	}


	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getPoBox() {
		return poBox;
	}

	public void setPoBox(String poBox) {
		this.poBox = poBox;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex( String index ) {
		this.index = index;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getDescription() { return description; }
	public void setDescription(String description) { this.description = description; }


	/*
	 *  Business Methods 
	 * 
	 */
	public String getOneLine() {
		
		String resStr = "";
				
		if ( !StringUtils.isBlank( getStreet())) {
			resStr = resStr.concat( WordUtils.capitalizeFully( StringUtils.trim( getStreet()))) + ", ";
		}
		if ( !StringUtils.isBlank( getIndex())) {
			resStr = resStr.concat( WordUtils.capitalizeFully( StringUtils.trim( getIndex()))) + " ";
		}
		if ( !StringUtils.isBlank( getCity())) {
			resStr = resStr.concat( WordUtils.capitalizeFully( StringUtils.trim( getCity()))) + ", ";
		}
		if ( !StringUtils.isBlank( getCountryCode())) {
			resStr = resStr.concat( StringUtils.trim( getCountryCode()));
		}
				
		return resStr;
		
	}
	
	
}
