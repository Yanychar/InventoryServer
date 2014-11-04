package com.c2point.tools.entity.location;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import com.c2point.tools.entity.SimplePojo;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ToolItem;

@Entity
public class LocationRecord extends SimplePojo {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( LocationRecord.class.getName());

	private ToolItem		item;
	
//	@Temporal(TemporalType.DATE)
	@Temporal(TemporalType.TIMESTAMP)
    private Date			dateForDB;
	
	private OrgUser			user;
	private GeoLocation		location;

	@Enumerated( EnumType.ORDINAL )
	private LocationStatus	status;

	public LocationRecord() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LocationRecord(ToolItem item, DateTime date, OrgUser user,
			GeoLocation location, LocationStatus status) {
		super();
		
		setItem( item );
		setDate( date );
		setUser( user );
		setLocation( location );
		setStatus( status );
	}

	
	public ToolItem getItem() { return item; }
	public void setItem(ToolItem item) { this.item = item; }

	public DateTime getDate() { return new DateTime( getDateForDB()); }
	public void setDate( DateTime date ) { setDateForDB( date.toDate()); }

	protected Date getDateForDB() { return this.dateForDB; }
	protected void setDateForDB( Date dateForDB ) { this.dateForDB = dateForDB; }	

	public OrgUser getUser() { return user; }
	public void setUser(OrgUser user) { this.user = user; }

	public GeoLocation getLocation() { return location; }
	public void setLocation(GeoLocation location) { this.location = location; }

	public LocationStatus getStatus() { return status; }
	public void setStatus(LocationStatus status) { this.status = status; }

	@Override
	public String toString() {
		return "LocationRecord [item=" + item 
				+ ", date=" + getDate().toString( DateTimeFormat.forPattern( "dd-MM-yyyy HH:mm" )) 
				+ ", user="+ user 
				+ ", location=" + location + ", status=" + status + "]";
	}
	
	
	
	
	
}
