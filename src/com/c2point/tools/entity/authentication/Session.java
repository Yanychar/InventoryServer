package com.c2point.tools.entity.authentication;

import java.util.Date;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.person.OrgUser;

public class Session {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( Session.class.getName());

	private String	uniqueSessionID;
//	private Account	account;
	private OrgUser	user;
    private Date	lastAccessed;   // time in millisec from 
	
	
	public Session() {
	}

	public Session( OrgUser user ) {
		
		setUniqueSessionID();
//		setAccount( account );
		setUser( user );
		setLastAccessed();
		
	}


	public String getUniqueSessionID() { return uniqueSessionID; }
	public void setUniqueSessionID(String uniqueSessionID) { this.uniqueSessionID = uniqueSessionID; }
	public void setUniqueSessionID() { setUniqueSessionID( UUID.randomUUID().toString()); }

//	public Account getAccount() { return account; }
//	public void setAccount( Account account ) { this.account = account; }

	public OrgUser getUser() { return user; }
	public void setUser( OrgUser user ) { this.user = user; }

	public Date getLastAccessed() { return lastAccessed; }
	public void setLastAccessed(Date lastAccessed) { this.lastAccessed = lastAccessed; }
	public void setLastAccessed() { setLastAccessed( new Date());
	}
	
	
}
