package com.c2point.tools.entity.tool;

import javax.persistence.Entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.SimplePojo;

@Entity
public class Producer extends SimplePojo {

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
