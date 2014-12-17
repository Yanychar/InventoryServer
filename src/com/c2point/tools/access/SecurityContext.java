package com.c2point.tools.access;

import java.util.HashMap;
import java.util.Map;

import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ToolItem;

public class SecurityContext {

	private Map<Long, PermissionType>	accessMap;
	
	public SecurityContext( OrgUser user ) {
		
		if ( user != null ) {
			accessMap = new HashMap<Long, PermissionType>();
			fillHashMap();
		} else {
			accessMap = null;
		}
	}
	
	public PermissionType getPermission( FunctionalityType func, OwnershipType oType ) {
		
		return accessMap.get( getKey( func, oType )); 
	}
	
	public PermissionType getPermission( FunctionalityType func, boolean owner ) {
		
		return accessMap.get( getKey( func, ( owner ? OwnershipType.OWN : OwnershipType.COMPANY ))); 
	}
	
	public PermissionType getPermission( FunctionalityType func, ToolItem item, OwnershipType oType ) {
		
		return accessMap.get( getKey( func, oType )); 
	}

	private void fillHashMap() {
		
		addEntry( FunctionalityType.BORROW, 		PermissionType.R,  PermissionType.RW, PermissionType.NO );
		addEntry( FunctionalityType.CHANGESTATUS, 	PermissionType.RW, PermissionType.R,  PermissionType.NO );
		addEntry( FunctionalityType.MESSAGING,		PermissionType.NO, PermissionType.RW, PermissionType.NO );
	}

	private void addEntry( FunctionalityType func, 
						   PermissionType forOwn, PermissionType forCompany, PermissionType forAdmin ) {
		
		accessMap.put( getKey( func, OwnershipType.OWN ), 		forOwn ); 
		accessMap.put( getKey( func, OwnershipType.COMPANY ), 	forCompany ); 
		accessMap.put( getKey( func, OwnershipType.ANY ), 		forAdmin ); 
	}

	private Long getKey( FunctionalityType func, OwnershipType oType ) {
		
		return new Long( func.value() * 10 + oType.value());
		
	}
}
