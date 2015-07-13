package com.c2point.tools.entity.settings;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.organisation.Organisation;

public class OrgProperties {
	private static Logger logger = LogManager.getLogger( OrgProperties.class.getName()); 

	private Organisation 	org;
	
	private Map<String, Object>	listOfProperties;
	private Stack<Property>		listToUpdate;
	

	public OrgProperties( Organisation org ) {
		
		this.org = org;
		
		this.listOfProperties = new HashMap<String, Object>();
		
		this.listToUpdate = new Stack<Property>();
		
	}

	public Organisation getOrg() { return this.org; }
	
	public void set( String name, Object value ) {
	
		Object oldValue = this.listOfProperties.get( name );
		
		if ( oldValue == null ) {
			// new value will be stored
			
			// 1. Store in hashMap
			this.listOfProperties.put( name, value );
			
		} else {
			
			// Param exist. If is was changed than
			if ( oldValue != value ) {
			
				// 1. Store in hashMap
				this.listOfProperties.put( name, value );
				
			} else {
				// Nothing to do. Exit from here
				return;
			}
			
		}
		
		// 2. Create Param and put into the list to persist in DB
		addToUpdate( name, value );
	}

	// Add property to hashmap after reading from DB. Does not require DB update after that
	public void set( Property prop ) {
		
		this.listOfProperties.put( prop.getName(), prop.convertValue());
			
	}
	
	
	@SuppressWarnings("unchecked")
	public <T> T get( Class<T> c, String name ) {
		
		try {
			
			Object value = this.listOfProperties.get( name );
			if ( value != null )
				return ( T )value;
			else
				logger.debug( "Parameter '" + name + "' was not found in the Map Of Properties. " );
			
		} catch( Exception e ) {
			
			logger.error( "Parameter '" + name + "' stored in Map Of Properties has other type than expected!" );
		}

		return null;
		
	}

	public Boolean getBoolean( String name ) {
		
		return get( Boolean.class, name );
		
	}

	public Integer getInteger( String name ) {
		
		return get( Integer.class, name );
		
	}

	public Long getLong( String name ) {
		
		return get( Long.class, name );
		
	}

	public String getString( String name ) {
		
		return get( String.class, name );
		
	}

	
	private void addToUpdate( String name, Object value ) {

		PropertyType type = PropertyType.getType( value ); 
		
		if ( type != PropertyType.UNKNOWN ) {

			this.listToUpdate.push( new Property( this.org, name, type, value.toString()));
		} else {
			logger.error( "Unsupported property type of property '" + name + "' was trying to store");
		}
				
	}

	public void clearUpdateCash() {
		this.listToUpdate.clear();
	}
	
	public Property getNextToUpdate() {
		
		Property prop = null;
		
		if ( hasToUpdate()) {
			
			prop = this.listToUpdate.pop();
		}
		
		return prop;

	}

	public boolean hasToUpdate() {
		
		return !this.listToUpdate.empty();
	}

	public String toString() {
		
		String str = "";
		
		
		
		for (Map.Entry<String, Object> entry : listOfProperties.entrySet()) {
			
			str = str.concat( entry.getKey() + " (" + entry.getValue().getClass().getSimpleName() + ") = " + entry.getValue() + "\n" );
		
		}
		
		return str;
	}
}
