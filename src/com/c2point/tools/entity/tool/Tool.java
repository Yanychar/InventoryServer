package com.c2point.tools.entity.tool;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.SimplePojo;
import com.c2point.tools.entity.organisation.Organisation;

@Entity
@NamedQueries({
	@NamedQuery( name = "listTools", 
			query = "SELECT tool FROM Tool tool " +
				"WHERE " 
				+ "tool.deleted = false AND "
				+ "tool.org = :org " 
				+ "ORDER BY tool.name ASC"
	),
	@NamedQuery( name = "listCategoryTools", 
			query = "SELECT tool FROM Tool tool " +
				"WHERE " 
				+ "tool.deleted = false AND "
				+ "tool.org = :org AND " 
				+ "tool.category = :category "
				+ "ORDER BY tool.name ASC"
	),
	@NamedQuery( name = "countAllOrgTools", 
			query = "SELECT COUNT( tool.id ) FROM Tool tool " +
				"WHERE tool.org = :org"
	),
	@NamedQuery( name = "listToolsByManufacturer", 
			query = "SELECT tool FROM Tool tool " +
					"WHERE " 
					+ "tool.deleted = false AND "
					+ "tool.org = :org AND " 
					+ "tool.manufacturer.name = :name "
					+ "ORDER BY tool.name ASC"
	),
})
public class Tool extends SimplePojo {

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( Tool.class.getName());
	
//	private String		code;
	private String		name;

	@Column(name="description")
	private String 		toolInfo;
	
	private Category 	category;
	
	private Manufacturer	manufacturer;

	private String 		model;
	
	@ManyToOne
	private Organisation org;

	protected Tool() {
		
	}

	public Tool( Organisation org ) {
		this();
		
		setOrg( org );
		
	}

	public boolean contentDifferent( Tool tool ) {
		
		boolean res = true;

//		if ( StringUtils.equalsIgnoreCase( this.getCode(), tool.getCode()))
			if ( StringUtils.equalsIgnoreCase( this.getName(), tool.getName()))
				if ( StringUtils.equalsIgnoreCase( this.getToolInfo(), tool.getToolInfo()))
					if ( StringUtils.equalsIgnoreCase( this.getCategory().getName(), tool.getCategory().getName()))
						if ( StringUtils.equalsIgnoreCase( this.getManufacturer().getName(), tool.getManufacturer().getName()))
								if ( StringUtils.equalsIgnoreCase( this.getModel(), tool.getModel()))	
									res = false;
		return res;
	}
	
	public Tool copy() {
		
		Tool newTool = new Tool( this.getOrg());

//		newTool.setCode( this.getCode());
		newTool.setManufacturer( this.getManufacturer());
		newTool.setModel( this.getModel());
		newTool.setName( this.getName());
		newTool.setToolInfo( this.getToolInfo());
		newTool.setCategory( this.getCategory());
		
		return newTool;
	}
	
/*	
	private String getCode() { return code; }
	private void setCode( String code ) { this.code = code; }
*/
	
	public String getName() { return name; }
	public void setName( String name ) { this.name = name; }

	public String getToolInfo() { return toolInfo; }
	public void setToolInfo( String toolInfo ) { this.toolInfo = toolInfo; }

	public Category getCategory() { return category; }
	public void setCategory( Category category ) { this.category = category; }

	public Manufacturer getManufacturer() { return manufacturer; }
	public void setManufacturer( Manufacturer manufacturer ) { this.manufacturer = manufacturer; }
	
	public Organisation getOrg() { return org; }
	public void setOrg( Organisation org ) { this.org = org; }

	public String getModel() { return model; }
	public void setModel(String model) { this.model = model; }

	public String getFullName() { 

		String str = 
				  StringUtils.defaultString( getName()) + " "
				+ ( getManufacturer() != null ? StringUtils.defaultString( getManufacturer().getName()) + " " : "" ) 
				+ StringUtils.defaultString( getModel())
		;
		
		return str;
	}
	
	@Override
	public String toString() {
		return "Tool [id=" + id + ", name=" + name + ", description="
				+ toolInfo + ", category=" + category + ", manufacturer="
				+ manufacturer + ", model=" + model + "]";
	}

	
}
