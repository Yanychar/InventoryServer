package com.c2point.tools.entity.repository;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	
	public ToolItem() {
		this( null, null, null );
	}
	
	public ToolItem( Tool tool, OrgUser responsible, OrgUser currentUser ) {
		
		setTool( tool );
		setResponsible( responsible );
		setCurrentUser( currentUser );
		
		setQuantity( 1 );

		setStatus( ItemStatus.FREE );

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

}
