package com.c2point.tools.entity.access;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.AccessRightsFacade;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ToolItem;

public class SecurityContext {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( SecurityContext.class.getName()); 
	
	private OrgUser user;

	// TODO. Temporal implementation. Must be changed
	private AccessRightsCollector	accessMap;
	
	private boolean initialized = false;

	public SecurityContext( OrgUser user ) {

		this.user = user;
		
		if ( user != null ) {
			
			accessMap = new AccessRightsCollector();
			
			
		} else {
			accessMap = null;
		}
	}

	/*
	 * Adapters to base method
	 */
	
	// True if has R or RW for OWN data
	public boolean hasViewPermissionOwn( FunctionalityType func ) { return getPermission( func, OwnershipType.OWN ) != PermissionType.NO; }
	
	public boolean hasViewPermissionMgmt( FunctionalityType func ) {

		return getPermission( func, OwnershipType.COMPANY ) != PermissionType.NO || getPermission( func, OwnershipType.ANY ) != PermissionType.NO; 
		
	}

	public boolean hasViewPermissionAll( FunctionalityType func ) { return getPermission( func, OwnershipType.ANY ) != PermissionType.NO; 	}

	public boolean hasEditPermissionMgmt( FunctionalityType func ) {

		return getPermission( func, OwnershipType.COMPANY ) == PermissionType.RW || getPermission( func, OwnershipType.ANY ) == PermissionType.RW; 
		
	}

	public boolean hasEditPermissionAll( FunctionalityType func ) { return getPermission( func, OwnershipType.ANY ) == PermissionType.RW; 	}

	
	public boolean hasEditPermission( FunctionalityType func, Organisation org ) {
		
		if ( user.getOrganisation().getId() == org.getId()) {
			
			return getPermission( func, OwnershipType.COMPANY ) == PermissionType.RW;
			
		} else {

			return getPermission( func, OwnershipType.ANY ) == PermissionType.RW;
			
		}
		
	}
	
	public boolean hasViewPermission( FunctionalityType func, Organisation org ) {
		
		if ( user.getOrganisation().getId() == org.getId()) {
			
			return getPermission( func, OwnershipType.COMPANY ) != PermissionType.NO;
			
		} else {

			return getPermission( func, OwnershipType.ANY ) != PermissionType.NO;
			
		}
		
	}
	
	
	// Can current user change item? It depends does he own item or not
	public boolean canChangeToolItem( FunctionalityType func, ToolItem item ) {
		
		boolean itemOwned = item.getCurrentUser().getId() == this.user.getId();
		
		return ( getPermission( func, ( itemOwned ? OwnershipType.OWN : OwnershipType.COMPANY )) == PermissionType.RW ); 

	}

	private PermissionType getPermission( FunctionalityType func, OwnershipType ownership ) {
		
		if ( !initialized ) {
			
			fillContext();
			initialized = true;
		}
		return accessMap.getPermissionInt( func, ownership );
	}
	
	
	private boolean fillContext() {
		
		boolean res = false;
		

		List<AccessRight> list = AccessRightsFacade.getInstance().getAccessRights( this.user );
		
		accessMap.clear();
		accessMap.addEntries( list );
				
		res = ( accessMap.size() > 0 );
		
		return res;
	}

	
}

