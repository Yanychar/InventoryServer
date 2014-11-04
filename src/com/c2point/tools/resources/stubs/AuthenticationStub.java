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
	
	private OrgUserStub 	user;
	private String 			sessionId = "";
	private String 			date;
	
	protected AuthenticationStub() {
		
	}
	
	public AuthenticationStub( Account account, DateTime date ) {
		this.user = new OrgUserStub( account.getUser());
		this.sessionId = account.getUniqueSessionID();
		this.date = date.toString( DateTimeFormat.forPattern( "ddMMyyyyHHmm" ));
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
