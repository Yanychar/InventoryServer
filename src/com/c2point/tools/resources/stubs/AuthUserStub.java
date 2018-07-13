package com.c2point.tools.resources.stubs;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;


@XmlRootElement(name = "authenticated")
@XmlType(propOrder = { "name", "sessionId", "date" })
public class AuthUserStub {
	
	private OrgUserStub 	user;
	private String 			sessionId = "";
	private String 			date;
	
	protected AuthUserStub() {
		
	}
	
	public AuthUserStub( OrgUserStub user, String sessionId, DateTime date ) {
		this.user = user;
		this.sessionId = ( sessionId != null ? sessionId : "" );
		this.date = ( date != null ? date.toString( DateTimeFormat.forPattern( "ddMMyyyyHHmm" )) : "" );
	}

	public OrgUserStub getUser() { return user; }
	public void setUser( OrgUserStub user ) { this.user = user; }

	@XmlElement( name="sessionid" )
	public String getSessionId() { return sessionId; }
	public void setSessionId( String sessionId ) { this.sessionId = sessionId; }

	public String getDate() { return date; }
	public void setDate( String date ) { this.date = date; }

	@Override
	public String toString() {
		return "AuthenticationStub ["
				+ (user != null ? "user=" + user + ", " : "")
				+ (sessionId != null ? "sessionId=" + sessionId + ", " : "")
				+ (date != null ? "date=" + date : "") + "]";
	}
	
	

}
