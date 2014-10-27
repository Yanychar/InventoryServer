package com.c2point.tools.entity.msg;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDate;

import com.c2point.tools.entity.SimplePojo;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ToolItem;

@Entity
// @Access(AccessType.FIELD)
@NamedQueries({
	@NamedQuery( name = "listAllMsgForUser", 
			query = "SELECT msg FROM Message msg " +
				"WHERE " + //item.org = :org AND " +
				"msg.deleted = false AND " +
	 			"msg.to = :user " +
				"ORDER BY msg.dateForDB ASC"
	),
	@NamedQuery( name = "listMsgWithStatus", 
			query = "SELECT msg FROM Message msg " +
				"WHERE " + //item.org = :org AND " +
				"msg.deleted = false AND " +
	 			"msg.to = :user AND " +
	 			"msg.status = :status " +
				"ORDER BY msg.dateForDB ASC"
	),
	@NamedQuery( name = "listLatestMsg", 
			query = "SELECT msg FROM Message msg " +
				"WHERE " + //item.org = :org AND " +
				"msg.deleted = false AND " +
	 			"msg.to = :user AND " +
	 			"( msg.dateForDB > :date  OR msg.status = com.c2point.tools.entity.msg.MessageStatus.UNREAD ) " +
				"ORDER BY msg.dateForDB ASC"
	),
})

public class Message extends SimplePojo {

	private static Logger logger = LogManager.getLogger( Message.class.getName()); 

    @Enumerated( EnumType.ORDINAL )
	private MessageType		type;

	@Temporal(TemporalType.DATE)
    private Date			dateForDB;
    
	private OrgUser			from;
	private OrgUser			to;
	
	@OneToOne( cascade={CascadeType.MERGE, CascadeType.REFRESH}, fetch=FetchType.EAGER )
	private ToolItem	item;
	
	private String			text;
	
	@Enumerated( EnumType.ORDINAL )
	private MessageStatus	status;

	public Message() {
		this( null, null, null, null, null );
	}
	
	// Request and Info messages
	public Message( MessageType type, OrgUser from, OrgUser to, ToolItem item ) {
		
		this( type, from, to, item, null );
		
	}
	
	// Simple text message 
	public Message( OrgUser from, OrgUser to, String text ) {
		
		this( MessageType.TEXT, from, to, null, text );
		
	}
	
	public Message( MessageType type, OrgUser from, OrgUser to, ToolItem item, String text ) {
		
		setType( type );
		setFrom( from );
		setTo( to );
		setItem( item );
		setText( text );
		setStatus( MessageStatus.CREATED );
		setDate( LocalDate.now());
		
	}
	
	public void update( Message msg ) {
		
		setType( msg.getType());
		setFrom( msg.getFrom());
		setTo( msg.getTo());
		setItem( msg.getItem());
		setText( msg.getText());
		setStatus( msg.getStatus());
		setDate( msg.getDate());
	}
	
    public MessageType getType() { return type; }
	public void setType( MessageType type ) { this.type = type; }

	public OrgUser getFrom() { return from; }
	public void setFrom( OrgUser from ) { this.from = from; }

	public OrgUser getTo() { return to; }
	public void setTo( OrgUser to ) { this.to = to; }

	public ToolItem getItem() { return item; }
	public void setItem( ToolItem item ) { this.item = item; }

	public String getText() { return text; }
	public void setText(String text) { this.text = text; }

	public MessageStatus getStatus() { return status; }
	public void setStatus(MessageStatus status) { this.status = status; }

	public LocalDate getDate() { return LocalDate.fromDateFields( getDateForDB()); }
	public void setDate( LocalDate date ) { setDateForDB( date.toDate()); }

	//@Column(name="date")
	//@Temporal(TemporalType.DATE)
	//@Access(AccessType.PROPERTY)
	protected Date getDateForDB() { return this.dateForDB; }
	protected void setDateForDB( Date dateForDB ) { this.dateForDB = dateForDB; }	
    
	public Message createReply( MessageType type ) {
		
		Message newMsg = createReply();
		newMsg.setType( type );
		
		return newMsg;
	}

	public Message createReply() {
		
		Message newMsg = new Message( getType(), getTo(), getFrom(), getItem(), getText());
		
		switch ( getType()) {
			case REQUEST:
				newMsg.setType( MessageType.AGREEMENT );
				break;
			case AGREEMENT:
				newMsg.setType( MessageType.CONFIRMATION );
				break;
			case REJECTION:
				newMsg.setType( MessageType.CONFIRMATION );
				break;
			case INFO:
				// Nothing to send
				newMsg = null;
				break;
			case TEXT:
				// Just reply. nothing to change
				break;
			default:
				newMsg.setType( null );
				logger.error( "Message with id: " + getId() + " has wrong MessageType!" );
				break;
		
		}
		
		this.setStatus( MessageStatus.RESPONDED );
		this.setText( "" );
		
		return newMsg;
	}
	
}
