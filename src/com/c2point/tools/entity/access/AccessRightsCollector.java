package com.c2point.tools.entity.access;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AccessRightsCollector extends HashMap<Long, AccessRight> {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( AccessRightsCollector.class.getName()); 
	
	public AccessRightsCollector() {
		
		super( FunctionalityType.values().length * OwnershipType.values().length );
		
//		setDefaultContext();
	}

	public AccessRightsCollector( List<AccessRight> list ) {
		
		this();
		
		if ( list != null && list.size() > 0 ) {
			
			addEntries( list ); 
			
		}
	}

	public void addEntry( AccessRight record ) {

		if ( record != null ) {
			this.put( getKey( record.getFunction(), record.getType()), record );
		}
		
	}

	public void addEntries( List<AccessRight> list ) {
		
		if ( list != null && list.size() > 0 ) {
			
			for ( AccessRight record : list ) {
				
				addEntry( record ); 
				
			}
			
			
		}
	}

	public void clearEntries() {
	
		this.clear();
	}
	
	public boolean exists( FunctionalityType func, OwnershipType ownership ) {
		
		return this.containsKey( getKey( func, ownership ));
	}
	
	public PermissionType getPermissionInt( FunctionalityType func, OwnershipType ownership ) {
		
		PermissionType retType = PermissionType.NO;
		
		if ( this.containsKey( getKey( func, ownership ))) {
			
			retType = this.get( getKey( func, ownership )).getPermission();
			
		}
		
		return retType; 
	}

	
	
	
	private Long getKey( FunctionalityType func, OwnershipType oType ) {
		
		return new Long( func.ordinal() * 10 + oType.value());
		
	}

}
