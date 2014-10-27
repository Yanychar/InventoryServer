package com.c2point.tools.resources.stubs;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.c2point.tools.entity.authentication.Account;


@XmlRootElement(name = "authenticated")
@XmlType(propOrder = { "name", "sessionId", "date" })
public class AuthenticationStub {
	
	private String 	name;
	private String 	sessionId = "";
	private String 	date;
	
	protected AuthenticationStub() {
		
	}
	
	public AuthenticationStub( Account account, DateTime date ) {
		this.name = account.getUser().getFirstAndLastNames();
		this.sessionId = account.getUniqueSessionID();
		this.date = date.toString( DateTimeFormat.forPattern( "ddMMyyyyHHmm" ));
	}

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	@XmlElement( name="sessionid" )
	public String getSessionId() { return sessionId; }
	public void setSessionId( String sessionId ) { this.sessionId = sessionId; }

	public String getDate() { return date; }
	public void setDate( String date ) { this.date = date; }

	@Override
	public String toString() {
		return "AuthenticationStub ["
				+ (name != null ? "name=" + name + ", " : "")
				+ (sessionId != null ? "sessionId=" + sessionId + ", " : "")
				+ (date != null ? "date=" + date : "") + "]";
	}
	
	

}
