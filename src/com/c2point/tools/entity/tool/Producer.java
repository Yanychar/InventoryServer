package com.c2point.tools.entity.tool;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.SimplePojo;

@Entity
@NamedQueries({
	@NamedQuery( name = "listActive", 
	query = "SELECT producer FROM Producer producer "
				+ "WHERE producer.deleted = false"
	),
})
public class Producer extends SimplePojo {

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( Producer.class.getName());
	
	private String	name;

	private String 	description;
	
	public Producer() {}
	
	public Producer( String name ) {
		
		this();
		setName( name );
	}

	public String getName() { return name; }
	public void setName( String name ) { this.name = name; }

	public String getDescription() { return description; }
	public void setDescription( String description ) { this.description = description; }
	
}
