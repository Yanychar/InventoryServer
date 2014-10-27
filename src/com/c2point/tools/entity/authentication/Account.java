package com.c2point.tools.entity.authentication;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
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
	@NamedQuery(name = "findAccountByUsrId", query = 
			"SELECT account FROM Account account " +
				"WHERE account.user.id = :userId"),
})
public class Account extends SimplePojo {
	
	private static Logger logger = LogManager.getLogger( Account.class.getName()); 

	public enum AccountStateType { Active, Blocked, MustBeChanged };
	
	
	private static final int 		PSW_LENGTH = 10;
	private static final char [] 	CHAR_TO_DELETE = { '0', 'o', 'O', 'l', 'I', '1' };
	
	private String 					usrName;
	private String 					pwd;
	
	private OrgUser					user;
	
	@Enumerated( EnumType.ORDINAL )
	private AccountStateType 		state;

    private String 				uniqueSessionID;

	@Temporal(TemporalType.TIMESTAMP)
    private Date			dateStartedForDB;
	@Temporal(TemporalType.TIMESTAMP)
    private Date			dateTouchedForDB;
	@Temporal(TemporalType.TIMESTAMP)
    private Date			dateEndedForDB;
	
	
	
	
	
//	@OneToOne(optional=false, fetch=FetchType.LAZY)

	public Account( String usrName, String pwd, OrgUser user ) {
		super();
		
		setUsrName( usrName );
		setPwd( pwd );
		setUser( user );
		setState( AccountStateType.Active );

		this.uniqueSessionID = null;
		this.dateStartedForDB = null;
		this.dateTouchedForDB  = null;
		this.dateEndedForDB  = null;
		
	}
	public Account() {
		this( "", "", null );
	}

	public String getUsrName() { return usrName; }
	public void setUsrName( String usrName ) {
		this.usrName = ( usrName != null ? usrName : "" );
	}
	
	public String getPwd() { return pwd; }
	public void setPwd( String pwd ) {
		this.pwd = ( pwd != null ? pwd : "" );
	}

	public OrgUser getUser() { return user; }
	public void setUser( OrgUser user ) { this.user = user; }
	
	public AccountStateType getState() { return state; }
	public void setState( AccountStateType state ) { this.state = state; }
	
	public static String generateNewPassword() {
		
		String password = "";
		boolean generate = true;
		
		while( generate ) {
			password = RandomStringUtils.randomAlphanumeric( PSW_LENGTH * 2 );
			
			// what shall be removed: 0oOlI1
			if ( StringUtils.containsAny( password, CHAR_TO_DELETE )) {
				// Delete chars
				for ( char c : CHAR_TO_DELETE ) {
					password = StringUtils.remove( password, c );
				}
			}
			
			// Check that length is required. Cut or select next passord
			if ( password.length() >= PSW_LENGTH ) {
				password = StringUtils.left( password, PSW_LENGTH );
				
				generate = false;
			}

			logger.debug( "Generated password: '" + password + "'" );
			
		}
		
		
		
		return password;
	}

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
