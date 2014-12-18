package com.c2point.tools.entity.person;

import java.util.Date;
import java.util.Map;

import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDate;

import com.c2point.tools.entity.SimplePojo;

@MappedSuperclass
//@Converter(name = "localDateConverter", converterClass = LocalDateConverterForJPA.class)  
public class Person extends SimplePojo {

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( Person.class.getName());
	
	private String		code;

	private String		firstName;
	private String		lastName;
	
	@Temporal(TemporalType.DATE)
	private Date		birthdayForDB;
	
	private Address		address;

	private String		phoneNumber;
	private String		email;
	
	private Map<IDType, Identity>	identities;
	
	private boolean		superUserFlag;
	
	public Person() {
		
		setIdentities( null );
		
	}
	
	public Person( String firstName, String lastName, String phoneNumber, String email ) {
		this();
		
		setFirstName( firstName );
		setLastName( lastName );
		setPhoneNumber( phoneNumber );
		setEmail( email );
		
	}

	public Person( String firstName, String lastName ) {
		this( firstName, lastName, null, null ); 

	}

	public String getCode() { return this.code; }
	public void setCode( String code ) { this.code = code; }

	public String getFirstName() { return firstName; }
	public void setFirstName( String firstName ) { this.firstName = firstName; }
	
	public String getLastName() { return lastName; }
	public void setLastName( String lastName ) { this.lastName = lastName; }

	public LocalDate getBirthday() { return ( getBirthdayForDB() != null ? new LocalDate( getBirthdayForDB()) : null ); }
	public void setBirthday( LocalDate birthday ) { setBirthdayForDB( birthday != null ? birthday.toDate() : null ); }

	protected Date getBirthdayForDB() { return this.birthdayForDB; }
	protected void setBirthdayForDB( Date birthdayForDB ) { this.birthdayForDB = birthdayForDB; }	
	
	public Address getAddress() { return address; }
	public void setAddress( Address address ) { this.address = address; }

	public String getPhoneNumber() { return phoneNumber; }
	public void setPhoneNumber( String phoneNumber ) { this.phoneNumber = phoneNumber; }

	public String getEmail() { return email; }
	public void setEmail( String email ) { this.email = email; }

	public Map<IDType, Identity> getIdentities() { return identities; }
	public void setIdentities( Map<IDType, Identity> identities ) { this.identities = identities; }	

	public boolean isSuperUserFlag() { return superUserFlag; }
	public void setSuperUserFlag( boolean superUserFlag ) { this.superUserFlag = superUserFlag; }

	
	/*
	 * Business Methods
	 */

	public String getFirstAndLastNames() {
		
		return WordUtils.capitalizeFully( StringUtils.trim( getFirstName() + " " + getLastName() ));
	}
	
	public String getLastAndFirstNames() {
		
		return WordUtils.capitalizeFully( StringUtils.trim( getLastName() + " " + getFirstName() ));
		
	}

}
