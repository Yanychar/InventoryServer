package com.c2point.tools.entity.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.organisation.Organisation;

public class OrgProperties extends HashMap<String, Object>{
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( OrgProperties.class.getName()); 

	private Organisation 	org;
	
	private List<Property>	listToUpdate;
	

	public OrgProperties( Organisation org ) {
		
		this.org = org;
		
		this.listToUpdate = new ArrayList<Property>(10);
		
	}

	public Organisation getOrg() { return this.org; }
	
	public void set( String name, Object value ) {
	
		Object oldValue = this.get( name );
		
		if ( oldValue == null ) {
			// new value will be stored
			
			// 1. Store in hashMap
			put( name, value );
			
		} else {
			
			// Param exist. If is was changed than
			if ( oldValue != value ) {
			
				// 1. Store in hashMap
				put( name, value );
				
			} else {
				// Nothing to do. Exit from here
				return;
			}
			
		}
		
		// 2. Create Param and put into the list to persist in DB
		addToUpdate( name, value );
	}
	
	
	public Boolean getBoolean( String name ) {
		
		try {
			
			Object value = this.get( name );
			if ( value != null )
				return ( Boolean )value;
			else
				logger.debug( "Parameter '" + name + "' was not found in the Map Of Properties. " );
			
		} catch( Exception e ) {
			
			logger.error( "Parameter '" + name + "' stored in Map Of Properties has other type than expected!" );
		}

		return null;
		
	}

	public Integer getInteger( String name ) {
		
		try {
			
			Object value = this.get( name );
			if ( value != null )
				return ( Integer )value;
			else
				logger.debug( "Parameter '" + name + "' was not found in the Map Of Properties. " );
			
		} catch( Exception e ) {
			
			logger.error( "Parameter '" + name + "' stored in Map Of Properties has other type than expected!" );
		}

		return null;
		
	}

	public Long getLong( String name ) {
		
		try {
			
			Object value = this.get( name );
			if ( value != null )
				return ( Long )value;
			else
				logger.debug( "Parameter '" + name + "' was not found in the Map Of Properties. " );
			
		} catch( Exception e ) {
			
			logger.error( "Parameter '" + name + "' stored in Map Of Properties has other type than expected!" );
		}

		return null;
		
	}

	public String getString( String name ) {
		
		try {
			
			Object value = this.get( name );
			if ( value != null )
				return ( String )value;
			else
				logger.debug( "Parameter '" + name + "' was not found in the Map Of Properties. " );
			
		} catch( Exception e ) {
			
			logger.error( "Parameter '" + name + "' stored in Map Of Properties has other type than expected!" );
		}

		return null;
		
	}

	
	private void addToUpdate( String name, Object value ) {

		PropertyType type = PropertyType.getType( value ); 
		
		if ( type != PropertyType.UNKNOWN ) {

			this.listToUpdate.add( new Property( this.org, name, type, value.toString()));
		} else {
			logger.error( "Unsupported property type of property '" + name + "' was trying to store");
		}
				
	}

	public void clearUpdateCash() {
		this.listToUpdate.clear();
	}

}
