package com.c2point.tools.entity.transactions;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;

import com.c2point.tools.entity.SimplePojo;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ItemStatus;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Tool;

@Entity
@Table( name = "transactions")
@NamedQueries({
	@NamedQuery( name = "listTransactionsForUser", 
			query = "SELECT trn FROM BaseTransaction trn " +
				"WHERE " 
				+ "trn.org = :org AND "
				+ "( trn.trnType = com.c2point.tools.entity.transactions.TransactionType.TOOL OR "
				+ "  trn.trnType = com.c2point.tools.entity.transactions.TransactionType.TOOLITEM ) AND " 
				+ "( trn.sourceUser = :user OR trn.destUser = :user or trn.user = :user ) AND "
				+ "trn.dateForDb >= :startDate AND trn.dateForDb <= :endDate " 
				+ "ORDER BY trn.tool.name ASC"
	),
})

public class BaseTransaction extends SimplePojo {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( BaseTransaction.class.getName()); 

	/*
	 * Organisation this transzction referred to 
	 */
	private Organisation	org;
	
	
	/**
	 * Date and time of transaction
	 */
	@Temporal( TemporalType.TIMESTAMP )
    private Date		dateForDb;

	/**
	 * User who initiated transaction (can NOT be null )
	 */
	private OrgUser 	user;
	
	/**
	 * The User who is the source of transaction (can be null ) 
	 */
	private OrgUser 	sourceUser;
	
	/**
	 * The User who is the destination source of transaction (can be null ) 
	 */
	private OrgUser 	destUser;
	
	/**
	 * The Tool affected by this transaction(can be null ) 
	 */
	private Tool		tool;

	/**
	 * The ToolItem affected by this transaction(can be null ) 
	 */
	private ToolItem	toolItem;
	
	/**
	 * Message field can be used for additional descriptions or messages (lentth < 255)
	 * Can be null 
	 */
	private String		message;

	private TransactionType			trnType;
	private TransactionOperation	trnOperation;
	
	private ItemStatus	newStatus;
	
	public BaseTransaction() {
		this( null, null, null );
		
	}
	
	public BaseTransaction( OrgUser user, TransactionType type, TransactionOperation op ) {
		this( new DateTime(), user, type, op );
		
	}
	
	public BaseTransaction( DateTime date, OrgUser user, TransactionType type, TransactionOperation op ) {
		super();

		setOrg( user != null ? user.getOrganisation() : null );
		setDate( date );
		setUser( user );
		setTrnType( type );
		setTrnOperation( op );		
	}
	
	public Organisation getOrg() { return org; }
	public void setOrg(Organisation org) { this.org = org; }

	/**
	 * Used to store/retrieve Java date. Internal use
	 */
	protected Date getDateForDb() { return dateForDb; }
	protected void setDateForDb( Date dateForDb ) { this.dateForDb = dateForDb; }

	/**
	 * Used to store/retrieve Transaction date
	 */
	
	public DateTime  getDate() { return ( getDateForDb() != null ? new DateTime( getDateForDb()) : null ); }
	public void setDate( DateTime  date ) { setDateForDb( date != null ? date.toDate() : null ); }

	/**
	 * Used to read/save the OrgUser who initiates transaction
	 */
	public OrgUser getUser() { return user; }
	public void setUser( OrgUser user ) { this.user = user; }

	/**
	 * Used to read/save the OrgUser who is the
	 * potential source of items/messages/etc. moving
	 */
	public OrgUser getSourceUser() { return sourceUser; }
	public void setSourceUser( OrgUser sourceUser ) { 
		this.sourceUser = sourceUser;
		this.org = sourceUser.getOrganisation();
	}
	
	/**
	 * Used to read/save the OrgUser who is the
	 * potential destination of items/messages/etc. moving
	 */
	public OrgUser getDestUser() { return destUser; }
	public void setDestUser( OrgUser destUser ) { this.destUser = destUser; }

	/**
	 * Used to read/save the Tool if it is participating in transaction
	 */
	public Tool getTool() { return tool; }
	public void setTool( Tool tool ) { 
		this.tool = tool;
		setOrg( tool.getOrg());
	}

	/**
	 * Used to read/save the ToolItem if it is participating in transaction
	 */
	public ToolItem getToolItem() { return toolItem; }
	public void setToolItem( ToolItem toolItem ) { 
		this.toolItem = toolItem;
		setTool( toolItem.getTool());
	}
	
	/**
	 * Used to get/set text data (length < 255)
	 */
	public String getMessage() { return message; }
	public void setMessage( String message ) {
		
		if ( message != null && message.length() > 255 ) {
			
			message = StringUtils.substring( StringUtils.trimToEmpty( message ), 0, 254 );
		}
		
		this.message = message;
	}

	public TransactionType getTrnType() { return trnType; }
	public void setTrnType(TransactionType type) { this.trnType = type; }

	public TransactionOperation getTrnOperation() { return trnOperation; }
	public void setTrnOperation( TransactionOperation operation ) { this.trnOperation = operation; }

	public ItemStatus getNewStatus() { return newStatus; }
	public void setNewStatus(ItemStatus newStatus) { this.newStatus = newStatus; }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return  this.getClass().getSimpleName() + "[ "
				+ "Date:" + getDate() + ", " + " User: '" + (user != null ? user.getFirstAndLastNames() + "'" : "NULL" ) + "\n"
				+ "TransactionType: '" + getTrnType() + "', Operation: '" + getTrnOperation() + "'\n"
				+ "sourceUsr: '" + ( sourceUser != null ? sourceUser.getFirstAndLastNames() + "'" : "NULL") + ", "
				+ "destUsr: '" + ( destUser != null ? destUser.getFirstAndLastNames() + "'" : "NULL") + ", \n"
				+ "Tool: '" + ( tool != null ? tool.getFullName() + "'" : "NULL") + ", \n"
				+ "ToolItem: '" + ( toolItem.getTool().getFullName() != null ? toolItem.getTool().getFullName() + "'" : "NULL") + ", \n"
				+ "New Item Status: '" + ( getNewStatus() != null ? getNewStatus() + "'" : "NULL") + ", \n"
				+ "Message: '" + ( message != null ? message : "NULL") + "]";
	}
	
	public String toStringShort() {
		return  this.getClass().getSimpleName() + "[ "
				+ "Date:" + getDate() + ", " + " user: '" + (user != null ? user.getFirstAndLastNames() + "'" : "NULL" ) + " ]";
	}
	
	
}
