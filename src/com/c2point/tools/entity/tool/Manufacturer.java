package com.c2point.tools.entity.tool;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.SimplePojo;

@Entity
//@Table( name="producer" )
@NamedQueries({
	@NamedQuery( name = "listActive", 
	query = "SELECT manufacturer FROM Manufacturer manufacturer "
				+ "WHERE manufacturer.deleted = false "
				+ "ORDER BY manufacturer.name ASC"
			
	),
})
public class Manufacturer extends SimplePojo {

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( Manufacturer.class.getName());
	
	private String	name;

	private String 	description;
	
	public Manufacturer() {}
	
	public Manufacturer( String name ) {
		
		this();
		setName( name );
	}

	public String getName() { return name; }
	public void setName( String name ) { this.name = name; }

	public String getDescription() { return description; }
	public void setDescription( String description ) { this.description = description; }
	
}
