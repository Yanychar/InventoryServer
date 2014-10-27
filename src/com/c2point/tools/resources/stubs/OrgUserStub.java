package com.c2point.tools.resources.stubs;

import javax.xml.bind.annotation.XmlType;

import com.c2point.tools.entity.person.OrgUser;

@XmlType(propOrder = { "id", "firstName", "lastname" })
public class OrgUserStub {

	private long	id;

	private String	firstName;
	private String	lastName;
	
	public OrgUserStub() {}
	
	public OrgUserStub( OrgUser user ) {
		this();
		if ( user != null ) {
			setId( user.getId());
	
			setFirstName( user.getFirstName());
			setLastName( user.getLastName());
		}
	}

	public long getId() { return id; }
	public void setId(long id) { this.id = id; }

	public String getFirstName() { return firstName; }
	public void setFirstName( String firstName ) { this.firstName = firstName; }
	
	public String getLastName() { return lastName; }
	public void setLastName( String lastName ) { this.lastName = lastName; }

	public String toString() {
		
		String output = 
				"OrgUserStub[" + getId() + ", '" + getFirstName() + "', " + getLastName() + "]";
		
		return output;
	}
	
	
}
