package com.c2point.tools.entity.access;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.AuthenticationFacade;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ToolItem;

public class SecurityContext {
	private static Logger logger = LogManager.getLogger( SecurityContext.class.getName()); 
	
	private OrgUser user;

	// TODO. Temporal implementation. Must be changed
	private Map<Long, PermissionType>	accessMap;
	
	private boolean initialized = false;

	public SecurityContext( OrgUser user ) {

		this.user = user;
		
		if ( user != null ) {
			
			accessMap = new HashMap<Long, PermissionType>();
			
			setDefaultContext();
			
		} else {
			accessMap = null;
		}
	}

	/*
	 * Adapters to base method
	 */
	
	// True if has R or RW for OWN data
	public boolean hasViewPermissionOwn( FunctionalityType func ) { return getPermission( func ).getFor( OwnershipType.OWN ) != PermissionType.NO; }
	
	public boolean hasViewPermissionMgmt( FunctionalityType func ) {

		PermissionsResp resp = getPermission( func );
		
		return resp.getFor( OwnershipType.COMPANY ) != PermissionType.NO || resp.getFor( OwnershipType.ANY ) != PermissionType.NO; 
		
	}

	public boolean hasViewPermissionAll( FunctionalityType func ) { return getPermission( func ).getFor( OwnershipType.ANY ) != PermissionType.NO; 	}

	public boolean hasEditPermissionMgmt( FunctionalityType func ) {

		PermissionsResp resp = getPermission( func );
		
		return resp.getFor( OwnershipType.COMPANY ) == PermissionType.RW || resp.getFor( OwnershipType.ANY ) == PermissionType.RW; 
		
	}

	public boolean hasEditPermissionAll( FunctionalityType func ) { return getPermission( func ).getFor( OwnershipType.ANY ) == PermissionType.RW; 	}
	
	// Can current user change item? It depends does he own item or not
	public boolean canChangeToolItem( FunctionalityType func, ToolItem item ) {
		
		boolean itemOwned = item.getCurrentUser().getId() == this.user.getId();
		
		return ( getPermission( func ).getFor( itemOwned ? OwnershipType.OWN : OwnershipType.COMPANY ) == PermissionType.RW ); 

		
	}
	/*
	 * Base method (detailed) to get all permissions for particular finction
	 */
	private PermissionsResp getPermission( FunctionalityType func ) {

		if ( !initialized) {
			logger.debug( "Security Context was not initialized yet. Will be done!" );

			initialized = fillContext();
			
			if ( !initialized) {
				logger.error( "Failed to initialize Security Context. Default AccessRights will be used!" );
			}

		}
		
		PermissionsResp resp = new PermissionsResp(
				accessMap.get( getKey( func, OwnershipType.OWN )),
				accessMap.get( getKey( func, OwnershipType.COMPANY )),
				// OwnershipType.ANY  permissions can be for Service Company members only!!!
				( this.user.getOrganisation().isServiceOwner() ? accessMap.get( getKey( func, OwnershipType.ANY )) : PermissionType.NO ) 
		);
		
		return resp; 
	}
	

	
	private Long getKey( FunctionalityType func, OwnershipType oType ) {
		
		return new Long( func.value() * 10 + oType.value());
		
	}


	private boolean fillContext() {
		
		boolean res = false;
		
		List<AccessRightsRecord> list = AuthenticationFacade.getInstance().getAccessRights( this.user );
		
		if ( list != null ) {
			
			for ( AccessRightsRecord record : list ) {
			
				addEntry( record.getFunction(), record.getType(), record.getPermission());
				
			}
			
			res = true;
		}

		// Check that all AccessRights exists in DB
		if ( list == null
			||
			 list.size() != accessMap.size()
			||
			 list.size() != accessMap.size()
		 ) {
				
			for ( FunctionalityType func : FunctionalityType.values()) {
				
				AuthenticationFacade.getInstance().updateAccessRights( this.user, func, OwnershipType.OWN, accessMap.get( getKey( func, OwnershipType.OWN )));
				
			}
			
			
			
		}

		
		return res;
	}

	private void setDefaultContext() {

		for ( FunctionalityType func : FunctionalityType.values()) {
			
			addEntry( func, PermissionType.NO,  PermissionType.NO, PermissionType.NO );
			
		}
		
		addEntry( FunctionalityType.BORROW, 		PermissionType.R,  PermissionType.RW, PermissionType.NO );
		addEntry( FunctionalityType.CHANGESTATUS, 	PermissionType.RW, PermissionType.R,  PermissionType.NO );
		addEntry( FunctionalityType.MESSAGING,		PermissionType.RW, PermissionType.RW, PermissionType.NO );
	}

	private void addEntry( FunctionalityType func, 
						   PermissionType forOwn, PermissionType forCompany, PermissionType forAdmin ) {
		
		addEntry( func, OwnershipType.OWN, 		forOwn ); 
		addEntry( func, OwnershipType.COMPANY, 	forCompany ); 
		addEntry( func, OwnershipType.ANY, 		forAdmin ); 
	}

	private void addEntry( FunctionalityType func, OwnershipType type,  PermissionType permission ) {
		
		accessMap.put( getKey( func, type ), permission ); 
		
	}


	private class PermissionsResp {
		
		private Map<OwnershipType,PermissionType> perms = new HashMap<OwnershipType,PermissionType>();
		
		public PermissionsResp( PermissionType forOwn, PermissionType forCompany, PermissionType forAll ) {
			super();
			
			perms.put( OwnershipType.OWN, forOwn );
			perms.put( OwnershipType.COMPANY, forCompany );
			perms.put( OwnershipType.ANY, forAll );
		}
		
		public PermissionType getFor( OwnershipType ownershipType ) { return perms.get( ownershipType ); } 
	}

	/*
	 * Temporal implementation of security credentials storage
	 */
/*
	private void fillHashMap() {
		
		addEntry( FunctionalityType.BORROW, 		PermissionType.R,  PermissionType.RW, PermissionType.NO );
		addEntry( FunctionalityType.CHANGESTATUS, 	PermissionType.RW, PermissionType.R,  PermissionType.NO );
		addEntry( FunctionalityType.MESSAGING,		PermissionType.NO, PermissionType.RW, PermissionType.NO );
		addEntry( FunctionalityType.USERS_MGMT,		PermissionType.RW, PermissionType.RW, PermissionType.RW );
		addEntry( FunctionalityType.TOOLS_MGMT,		PermissionType.RW, PermissionType.RW, PermissionType.RW );
		addEntry( FunctionalityType.ORGS_MGMT,		PermissionType.RW, PermissionType.RW, PermissionType.RW );
	}
*/
	
	
}

