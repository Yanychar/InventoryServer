package com.c2point.tools.entity.tool;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

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
})
public class Tool extends SimplePojo {

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( Tool.class.getName());
	
	private String		code;
	private String		name;

	private String 		description;
	
	private Category 	category;
	
	private Manufacturer	manufacturer;

	@ManyToOne
	private Organisation org;

	public Tool() {
		
	}

	public String getCode() { return code; }
	public void setCode( String code ) { this.code = code; }

	
	public String getName() { return name; }
	public void setName( String name ) { this.name = name; }

	public String getDescription() { return description; }
	public void setDescription( String description ) { this.description = description; }

	public Category getCategory() { return category; }
	public void setCategory( Category category ) { this.category = category; }

	public Manufacturer getManufacturer() { return manufacturer; }
	public void setManufacturer( Manufacturer manufacturer ) { this.manufacturer = manufacturer; }
	
	public Organisation getOrg() { return org; }
	public void setOrg( Organisation org ) { this.org = org; }

	@Override
	public String toString() {
		return "Tool [code=" + code + ", name=" + name + ", description="
				+ description + ", category=" + category + ", manufacturer="
				+ manufacturer + "]";
	}

	
}
