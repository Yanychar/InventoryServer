package com.c2point.tools.entity.organisation;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.SimplePojo;
import com.c2point.tools.entity.person.Address;
import com.c2point.tools.entity.person.OrgUser;

@Entity
public class Organisation extends SimplePojo {

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( Organisation.class.getName());
	
	private String code;
	private String name;
	
	@OneToMany( mappedBy = "organisation", 
			cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH },
			fetch=FetchType.LAZY )
	@MapKey( name = "id" )
	private Map<Long, OrgUser> 
							employees = new HashMap<Long, OrgUser>();

	private Address			address;

	private String			phoneNumber;
	private String			email;

	private String			tunnus;
	private String			info;

	public Organisation() {
		super();
	}
	
	public Organisation( String code, String name ) {
		super();
		
		setCode( code );
		setName( name );
		
	}
	

	
	
	public String getCode() { return code; }
	public void setCode( String code ) { this.code = code; }

	public String getName() { return name; }
	public void setName( String name ) { this.name = name; }

	public Map<Long, OrgUser> getEmployees() { return employees; }
	public void setEmployees( Map< Long, OrgUser > employees ) { this.employees = employees; }
	
	public Address getAddress() { return address; }
	public void setAddress( Address address ) { this.address = address; }

	public String getPhoneNumber() { return phoneNumber; }
	public void setPhoneNumber( String phoneNumber ) { this.phoneNumber = phoneNumber; }

	public String getEmail() { return email; }
	public void setEmail( String email ) { this.email = email; }
	
	public String getTunnus() { return tunnus; }
	public void setTunnus( String tunnus ) { this.tunnus = tunnus; }
	
	public String getInfo() { return info; }
	public void setInfo( String info ) { this.info = info; }
	

}
