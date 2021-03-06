package com.c2point.tools.resources.stubs;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.c2point.tools.entity.repository.ItemStatus;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Category;

@XmlType //(propOrder = { "id", "code", "name", "quantity", "responsible", "currentUser", "reservedBy" })
public class ToolItemStub {
	
	private long	id;
//	private String	code;
	private String	name;
	private String 	description;
	private String 	manufacturer;
	private String 	model;

	private	int			quantity;
	
	private OrgUserStub	responsible;
	private OrgUserStub	currentUser;
	private OrgUserStub	reservedBy;
	
	private ItemStatus	status;
	
	private String		barcode;
	private String		serialNumber;
	private boolean		personalFlag;
	// Flag that identifies if short borrowing process is used in particular company for particular tool
	// In ver. 1.0.3 till 1.0.x short process only is valid ==>> shortWayOnlyFlag == TRUE 
	private boolean		shortWayOnlyFlag;

	private GeoLocationStub		lastKnownLocation;
	
	private ArrayList<String>	categoriesTree;		
	
	
	protected ToolItemStub() {}

	public ToolItemStub( ToolItem item ) {
		
		setId( item.getId());
//		setCode( item.getTool().getCode());
		setName( item.getTool().getName());
		setDescription( item.getTool().getToolInfo());
		try {
			setManufacturer( item.getTool().getManufacturer().getName());
		} catch( Exception e ) {
		}
		setModel( item.getTool().getModel());
		
		setQuantity( item.getQuantity());

		if ( item.getResponsible() != null ) setResponsible( new OrgUserStub( item.getResponsible()));
		if ( item.getCurrentUser() != null ) setCurrentUser( new OrgUserStub( item.getCurrentUser()));
		if ( item.getReservedBy() != null ) setReservedBy( new OrgUserStub( item.getReservedBy()));

		setStatus( item.getStatus());
		
		setBarcode( item.getBarcode());
		setSerialNumber( item.getSerialNumber());
		setPersonalFlag( item.isPersonalFlag());
		setShortWayOnlyFlag( true );

		setupCategoriesTree( item );
	
		if ( item.getLastKnownLocation() != null ) 
			setLastKnownLocation( new GeoLocationStub( item.getLastKnownLocation()));

		
	}

	public long getId() { return id; }
	public void setId(long id) { this.id = id; }
/*
	public String getCode() { return code;}
	public void setCode(String code) { this.code = code; }
*/
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public String getDescription() { return description; }
	public void setDescription( String description ) { this.description = description; }

	public String getManufacturer() { return manufacturer; }
	public void setManufacturer( String manufacturer ) { this.manufacturer = manufacturer; }

	public String getModel() { return model; }
	public void setModel(String model) { this.model = model; }

	public int getQuantity() { return quantity; }
	public void setQuantity( int quantity ) { this.quantity = quantity; }
	
	public OrgUserStub getResponsible() { return responsible; }
	public void setResponsible( OrgUserStub responsible ) { this.responsible = responsible; }

	public OrgUserStub getCurrentUser() { return currentUser; }
	public void setCurrentUser( OrgUserStub currentUser ) { this.currentUser = currentUser; }

	public OrgUserStub getReservedBy() { return reservedBy; }
	public void setReservedBy( OrgUserStub reservedBy ) { this.reservedBy = reservedBy; }

	public ItemStatus getStatus() { return status; }
	public void setStatus( ItemStatus status ) { this.status = status; }

	public String getBarcode() { return barcode; }
	public void setBarcode( String barcode ) { this.barcode = barcode; }

	public String getSerialNumber() { return serialNumber; }
	public void setSerialNumber( String serialNumber ) { this.serialNumber = serialNumber; }
	
	public boolean isPersonalFlag() { return personalFlag; }
	public void setPersonalFlag( boolean personalFlag ) { this.personalFlag = personalFlag; }

	public boolean isShortWayOnlyFlag() { return shortWayOnlyFlag; }
	public void setShortWayOnlyFlag( boolean shortWayOnlyFlag ) { this.shortWayOnlyFlag = shortWayOnlyFlag; }

	@XmlElement( name="location" )
	public GeoLocationStub getLastKnownLocation() { return lastKnownLocation; }
	public void setLastKnownLocation( GeoLocationStub lastKnownLocation ) { this.lastKnownLocation = lastKnownLocation; }
	
	public ArrayList<String> getCategoriesTree() { return categoriesTree; }
	public void setCategoriesTree( ArrayList<String> categoriesTree ) { this.categoriesTree = categoriesTree; }

	private void setupCategoriesTree( ToolItem item ) {
		
		categoriesTree = new ArrayList<String>();
		Category tmpCategory = item.getTool().getCategory();

		do {
		
			if ( tmpCategory != null ) {
				
				categoriesTree.add( 0, tmpCategory.getName());
				tmpCategory = tmpCategory.getParent();
				
			}
		
		} while ( tmpCategory != null );
		
	}

	private String categoriesTreeToString() {
		
		String str = "";
		
		for( String name : this.getCategoriesTree()) {
			
			str = str.concat( name + " > " );
			
		}
		
		return str;
	}
	
	public String toString() {
		
		String output = 
				"ToolItemStub[" + getId() + /*", '" + getCode() + */"', " + getName() + ", quantity="+getQuantity()+"]"
				+ ( getDescription() != null ? "\n  Description: "+getDescription() : "" )
				+ ( getManufacturer() != null ? "\n Manufacturer: " + getManufacturer() : "" )
				+ ( getModel() != null ? "\n Model: " + getModel() : "" )
				+ ( getResponsible() != null ? "\n  Responsible: "+getResponsible() : "" )
				+ ( getCurrentUser() != null ? "\n  Current User:"+getCurrentUser() : "" )
				+ ( getReservedBy() != null ? "\n  Reserved By:"+getReservedBy() : "" )
				+ ( getStatus() != null ? "\n  Status:"+getStatus() : "" )
				+ ( getBarcode() != null ? "\n  Barcode:"+getBarcode() : "" )
				+ ( getSerialNumber() != null ? "\n  SN:"+getSerialNumber() : "" )
				+ "\n  Is it personal Tool?: "+isPersonalFlag()
				+ ( getCategoriesTree() != null ? "\n Category: " + categoriesTreeToString() : "" )
				+ ( getLastKnownLocation() != null ? "\n Location: [" 
						+ getLastKnownLocation().getLatitude() + ", " 
						+ getLastKnownLocation().getLongitude() + ", " 
						+ getLastKnownLocation().getAccuracy() + "]" 
						: "" )
				;
		
		return output;
	}
	
	
	
}
