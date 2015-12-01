package com.c2point.tools.entity.repository;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDate;

import com.c2point.tools.datalayer.SettingsFacade;
import com.c2point.tools.entity.SimplePojo;
import com.c2point.tools.entity.location.GeoLocation;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.tool.Tool;


@Entity
@NamedQueries({
	@NamedQuery( name = "listAllPublic", 
			query = "SELECT item FROM ToolItem item " +
				"WHERE " 
				+ "item.deleted = false AND " 
				+ "item.tool.org = :org AND "
				+ "item.personalFlag = false "
				+ "ORDER BY item.tool.name ASC"
	),
	@NamedQuery( name = "listAll", 
			query = "SELECT item FROM ToolItem item " +
				"WHERE " 
				+ "item.deleted = false AND " 
				+ "item.tool.org = :org "
				+ "ORDER BY item.tool.name ASC"
	),
	@NamedQuery( name = "listAllBelongTo", 
		query = "SELECT item FROM ToolItem item " +
			"WHERE " 
			+ "item.deleted = false AND "
			+ "item.currentUser = :user "
			+ "ORDER BY item.tool.name ASC"
	),
	@NamedQuery( name = "listCategoryPublic", 
		query = "SELECT item FROM ToolItem item " +
			"WHERE " 
			+ "item.deleted = false AND "
			+ "item.tool.org = :org AND "
			+ "item.tool.category = :category AND "
			+ "item.personalFlag = false "
			+ "ORDER BY item.tool.name ASC"
	),
	@NamedQuery( name = "listCategory", 
			query = "SELECT item FROM ToolItem item " +
				"WHERE " 
				+ "item.deleted = false AND "
				+ "item.tool.org = :org AND "
				+ "item.tool.category = :category "
				+ "ORDER BY item.tool.name ASC"
	),
	@NamedQuery( name = "listCategoryBelongTo", 
		query = "SELECT item FROM ToolItem item " +
			"WHERE " 
			+ "item.deleted = false AND "
			+ "item.tool.org = :org AND "
			+ "item.tool.category = :category AND "
			+ "item.currentUser = :user "
			+ "ORDER BY item.tool.name ASC"
	),
	@NamedQuery( name = "listTool", 
			query = "SELECT item FROM ToolItem item " +
				"WHERE "
				+ "item.deleted = false AND "
				+ "item.tool.org = :org AND "
	 			+ "item.tool = :tool "
				+ "ORDER BY item.tool.name ASC"
	),
	@NamedQuery( name = "listToolBarcode", 
			query = "SELECT item FROM ToolItem item " +
				"WHERE "
				+ "item.deleted = false AND "
				+ "item.tool.org = :org AND "
				+ "item.barcode = :barcode "
				+ "ORDER BY item.tool.name ASC"
	),
	@NamedQuery( name = "listSearchSubstr", 
		query = "SELECT item FROM ToolItem item " +
			"WHERE "
			+ "item.deleted = false AND "
			+ "item.tool.org = :org AND "
			+ "( LOCATE( :searchStr, UPPER( item.tool.name )) > 0 OR "
			+ "  LOCATE( :searchStr, UPPER( item.tool.toolInfo )) > 0 OR "
			+ "  LOCATE( :searchStr, UPPER( item.tool.manufacturer.name )) > 0 OR "
			+ "  LOCATE( :searchStr, UPPER( item.tool.model )) > 0 OR "
			+ "  LOCATE( :searchStr, UPPER( item.serialNumber )) > 0 OR"
			+ "  LOCATE( :searchStr, UPPER( item.barcode )) > 0 ) "
			+ "ORDER BY item.tool.name ASC"

			
			),
})
public class ToolItem extends SimplePojo {

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( ToolItem.class.getName());
	
//	private Organisation	org;
	@ManyToOne( cascade = { CascadeType.MERGE }, fetch=FetchType.EAGER, optional=false )
	private Tool			tool;
	
	private	int				quantity;
	
	private OrgUser			responsible;
	private OrgUser			currentUser;
	
	private OrgUser			reservedBy;
	
	@Enumerated( EnumType.ORDINAL )
	private ItemStatus		status;
	
	private GeoLocation		lastKnownLocation;
	
	private String			serialNumber;
	private String			barcode;
	
	private boolean 		personalFlag;

	@Temporal(TemporalType.DATE)
	@Column(name="buytime")
    private Date			buyTimeForDB;
	
	@Temporal(TemporalType.DATE)
	@Column(name="lastmaintenance")
    private Date			maintenanceForDB;
	
	private Double			price;
	private Integer				takuu;
	
	@Column(name="description")
	private String 			comments;
	
	public ToolItem() {
		this( null, null, null );
	}
	
	public ToolItem( Tool tool, OrgUser responsible, OrgUser currentUser ) {
		
		setTool( tool );
		setResponsible( responsible );
		setCurrentUser( currentUser );
		
		setQuantity( 1 );

// Set to FREE if FREE status available in this organisation. Othervise INUSE
		if ( tool != null ) {
			
			boolean freeAllowed = SettingsFacade.getInstance().getBoolean( tool.getOrg(), "freeStatusAllowed", false );
			
			setStatus( freeAllowed ? ItemStatus.FREE : ItemStatus.INUSE );
	    	
		}

		
		setPersonalFlag( false );
	}


//	public Organisation getOrg() { return org; }
//	public void setOrg( Organisation org ) { this.org = org; }

	public Tool getTool() { return tool; }
	public void setTool( Tool tool ) { this.tool = tool; }

	public OrgUser getResponsible() { return responsible; }
	public void setResponsible( OrgUser responsible ) { this.responsible = responsible; }

	public OrgUser getCurrentUser() { return currentUser; }
	public void setCurrentUser( OrgUser currentUser ) { this.currentUser = currentUser; }

	public OrgUser getReservedBy() { return reservedBy; }
	public void setReservedBy( OrgUser reservedBy ) { this.reservedBy = reservedBy; }

	public GeoLocation getLastKnownLocation() { return lastKnownLocation; }
	public void setLastKnownLocation( GeoLocation lastKnownLocation ) { this.lastKnownLocation = lastKnownLocation; }

	public ItemStatus getStatus() { return status; }
	public void setStatus( ItemStatus status ) { this.status = status; }
	
	public int getQuantity() { return quantity; }
	public void setQuantity( int quantity ) { this.quantity = quantity; }

	public String getBarcode() { return barcode; }
	public void setBarcode( String barcode ) { this.barcode = barcode; }

	public String getSerialNumber() { return serialNumber; }
	public void setSerialNumber( String serialNumber ) { this.serialNumber = serialNumber; }
	
	public boolean isPersonalFlag() { return personalFlag; }
	public void setPersonalFlag( boolean personalFlag ) { this.personalFlag = personalFlag; }

	protected Date getBuyTimeForDB() { return this.buyTimeForDB; }
	protected void setBuyTimeForDB( Date buyTimeForDB ) { this.buyTimeForDB = buyTimeForDB; }	
	
	protected Date getMaintenanceForDB() { return this.maintenanceForDB; }
	protected void setMaintenanceForDB( Date maintenanceForDB ) { this.maintenanceForDB = maintenanceForDB; }	
	
	public LocalDate getBuyTime() { 
		try {
			return LocalDate.fromDateFields( getBuyTimeForDB()); 
		} catch( Exception e ) {}
		
		return null;
	}
	public void setBuyTime( LocalDate date ) { setBuyTime( date != null ? date.toDate() : null ); }
	public void setBuyTime( Date date ) { setBuyTimeForDB( date ); }
	
	public LocalDate getMaintenance() {
		try {
			return LocalDate.fromDateFields( getMaintenanceForDB());
		} catch( Exception e ) {}
		
		return null;
	}
	public void setMaintenance( LocalDate date ) { setMaintenance( date != null ? date.toDate() : null ); }
	public void setMaintenance( Date date ) { setMaintenanceForDB( date ); }

	public Double getPrice() { return price; }
	public void setPrice( Double price ) { this.price = price; }

	public Integer getTakuu() { return takuu; }
	public void setTakuu( Integer takuu ) { this.takuu = takuu; }

	public String getComments() { return comments; }
	public void setComments( String comments ) { this.comments = comments; }

	
	public String getFullName() { 
		
		return ( getTool() != null ? getTool().getFullName() : "" );
	}
		
}
