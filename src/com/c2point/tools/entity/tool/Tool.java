package com.c2point.tools.entity.tool;

import javax.persistence.Entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.SimplePojo;

@Entity
public class Tool extends SimplePojo {

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( Tool.class.getName());
	
	private String		code;
	private String		name;

	private String 		description;
	
	private Category 	category;
	
	private Producer	producer;

	private boolean 	personalFlag;
	
	public Tool() {
		
		setPersonalFlag( false );
	}

	public String getCode() { return code; }
	public void setCode( String code ) { this.code = code; }

	
	public String getName() { return name; }
	public void setName( String name ) { this.name = name; }

	public String getDescription() { return description; }
	public void setDescription( String description ) { this.description = description; }

	public Category getCategory() { return category; }
	public void setCategory( Category category ) { this.category = category; }

	public Producer getProducer() { return producer; }
	public void setProducer( Producer producer ) { this.producer = producer; }
	
	public boolean isPersonalFlag() { return personalFlag; }
	public void setPersonalFlag( boolean personalFlag ) { this.personalFlag = personalFlag; }

	
}
