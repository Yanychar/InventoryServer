package com.c2point.tools.entity.authentication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import com.c2point.tools.entity.SimplePojo;
import com.c2point.tools.entity.person.OrgUser;

@Entity
@NamedQueries({
	@NamedQuery(name = "findAccountByUsrName", query = 
			"SELECT account FROM Account account " +
				"WHERE account.usrName = :usrName AND account.deleted = false ORDER BY account.usrName ASC"),
	@NamedQuery(name = "findAccountBySessionId", query = 
			"SELECT account FROM Account account " +
				"WHERE account.uniqueSessionID = :sessionId AND account.deleted = false"),
})
public class Account extends SimplePojo {
	
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( Account.class.getName()); 

	public enum AccountStateType { Active, Blocked, MustBeChanged };
	
	private String 					usrName;
	private String 					pwd;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="account")
	private Collection<OrgUser>		users;
	
	@Enumerated( EnumType.ORDINAL )
	private AccountStateType 		state;

    private String 				uniqueSessionID;

	@Temporal(TemporalType.TIMESTAMP)
    private Date			dateStartedForDB;
	@Temporal(TemporalType.TIMESTAMP)
    private Date			dateTouchedForDB;
	@Temporal(TemporalType.TIMESTAMP)
    private Date			dateEndedForDB;
	
	
	public Account( String usrName, String pwd, OrgUser user ) {
		super();
		
		setUsrName( usrName );
		setPwd( pwd );
		
		addUser( user );
		
		setState( AccountStateType.Active );

		this.uniqueSessionID = null;
		this.dateStartedForDB = null;
		this.dateTouchedForDB  = null;
		this.dateEndedForDB  = null;
		
	}
	public Account() {
		this( "", "", null );
	}

	public Account( Account account ) {
		
		this( account.getUsrName(), account.getPwd(), null );
	}

	public String getUsrName() { return usrName; }
	public void setUsrName( String usrName ) {
		this.usrName = ( usrName != null ? usrName : null );
	}
	
	public String getPwd() { return pwd; }
	public void setPwd( String pwd ) {
		this.pwd = ( pwd != null ? pwd : null );
	}

	public Collection<OrgUser> getUsers() { 
	
		return normalize();
		
	}
	protected void setUsers( Collection<OrgUser> users ) { this.users = users; }

	
	/*
	 * Return User if and only if one user found 
	 */
	public OrgUser getUser() { 

		Collection<OrgUser> retList = normalize();
		
		if ( retList != null && retList.size() == 1 ) {
			
			OrgUser user = retList.iterator().next();
			
			if ( user != null && !user.isDeleted()) {
				
				return user;
			}
		}
		
		return null;
	}
	
	/*
	 * Return NON deleted Users list 
	 */
	public Collection<OrgUser> getActiveUsers() { 

		Collection<OrgUser> retList = normalize();
		
		return retList;
	}
	
	public void addUser( OrgUser user ) {
		
		if ( users == null ) {
			users = new ArrayList<OrgUser>();
		}
		
		if ( user != null && !users.contains( user )) { 
				
			users.add( user );
			
			user.setAccount( this );
		}

	}
	
	public AccountStateType getState() { return state; }
	public void setState( AccountStateType state ) { this.state = state; }
	
	public String getUniqueSessionID() { return uniqueSessionID; }
	protected void setUniqueSessionID( String uniqueSessionID ) { this.uniqueSessionID = uniqueSessionID; }
	
	public DateTime getDateSessionStarted() { return new DateTime( getDateStartedForDB()); }
	protected void setDateSessionStarted( DateTime dateSessionStarted ) { 
		setDateStartedForDB( dateSessionStarted != null ? dateSessionStarted.toDate() : null ); 
	}

	public DateTime getDateSessionTouched() { return new DateTime( getDateTouchedForDB()); }
	protected void setDateSessionTouched( DateTime dateSessionTouched ) { 
		setDateTouchedForDB( dateSessionTouched != null ? dateSessionTouched.toDate() : null ); 
	}
		
	public DateTime getDateSessionEnded() { return new DateTime( getDateEndedForDB()); }
	protected void setDateSessionEnded( DateTime dateSessionEnded ) { 
		setDateEndedForDB( dateSessionEnded != null ? dateSessionEnded.toDate() : null ); 
	}
	
	
	protected Date getDateStartedForDB() { return this.dateStartedForDB; }
	protected void setDateStartedForDB( Date dateStartedForDB ) { this.dateStartedForDB = dateStartedForDB; }	
	protected Date getDateTouchedForDB() { return this.dateTouchedForDB; }
	protected void setDateTouchedForDB( Date dateTouchedForDB ) { this.dateTouchedForDB = dateTouchedForDB; }	
	protected Date getDateEndedForDB() { return this.dateEndedForDB; }
	protected void setDateEndedForDB( Date dateEndedForDB ) { this.dateEndedForDB = dateEndedForDB; }	
	

	public String setUniqueSessionID() {

		DateTime date = DateTime.now();
		setDateSessionStarted( date );
		setDateSessionTouched( date );
		setDateSessionEnded( null );

		setUniqueSessionID( UUID.randomUUID().toString());
		
		return uniqueSessionID;

	}
	
	public String closeSession() {

		String oldId = uniqueSessionID;

		DateTime date = DateTime.now();
		setDateSessionEnded( date );

		setUniqueSessionID( null );
		
		return oldId;
	}

	public boolean valid() {
		
		Collection<OrgUser> retList = normalize();
		
		return retList != null && retList.size() > 0 && retList.iterator().next() != null;
	}
	
	private Collection<OrgUser> normalize() {
		
		Collection<OrgUser> retList = null;
		
		if ( users != null ) {
			
			retList = new ArrayList<OrgUser>();
					
			for ( OrgUser user : users ) {
				
				if ( user != null && !user.isDeleted()) {
					
					retList.add( user );
				}
			}
		}
		
		return retList;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Tms Account [usrName=" + usrName + ", pwd=??? ]";
	}
	/**
	 * @return the address
	 */
}
