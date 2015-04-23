package com.c2point.tools.datalayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.InventoryUI;
import com.c2point.tools.entity.access.AccessGroups;
import com.c2point.tools.entity.access.AccessRightsCollector;
import com.c2point.tools.entity.access.AccessRight;
import com.c2point.tools.entity.access.FunctionalityType;
import com.c2point.tools.entity.access.OwnershipType;
import com.c2point.tools.entity.access.PermissionType;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.transactions.TransactionOperation;
import com.vaadin.ui.UI;

public class AccessRightsFacade {

	private static Logger logger = LogManager.getLogger( AccessRightsFacade.class.getName()); 

	private static int						MAX_INSTANCE_NUMBER = 4;
	private static AccessRightsFacade []	instances;
	private static int						next_instance_number;

	public static AccessRightsFacade getInstance() {
		
		if ( instances == null ) {
			instances = new AccessRightsFacade[ MAX_INSTANCE_NUMBER ];
			for ( int i = 0; i < MAX_INSTANCE_NUMBER; i++ ) {
				instances[ i ] = new AccessRightsFacade();  
			}
			next_instance_number = 0;

			initDefMap();			
		}
		
		AccessRightsFacade ret = instances[ next_instance_number ];
		if ( logger.isDebugEnabled()) 
			logger.debug( "AuthenticationFacade instance number retirned is " + next_instance_number + " from " + MAX_INSTANCE_NUMBER + " available!" );
		
		next_instance_number = ++next_instance_number % MAX_INSTANCE_NUMBER ;
		
		return ret;
	}

	public List<AccessRight> getAccessRights( OrgUser user ) {
	
		List<AccessRight> list = null;
		
		EntityManager em = DataFacade.getInstance().createEntityManager();
		try {
			// Fetched Account with specify UserName. Should be one account only!!!  
			TypedQuery<AccessRight> q = em.createNamedQuery( "findAccessRecords", AccessRight.class )
					.setParameter( "user", user );
			list = q.getResultList();
		} catch ( NoResultException e ) {
			if ( logger.isDebugEnabled())
				logger.debug( "No Access Records were found for user: '" + user.getFirstAndLastNames() + "'" );
		} catch ( Exception e ) {
			logger.error( e );
		} finally {
			em.close();
		}
		
		// Add missing rights if necessary
		AccessRightsCollector tmpMap = new AccessRightsCollector( list );
		
		for ( FunctionalityType func : FunctionalityType.values()) {
			
			for ( OwnershipType ownership : OwnershipType.values()) {
				
				if ( !tmpMap.exists( func, ownership )) {
					
					// Access Rights record is missing. Create new one and ...
					AccessRight record = new AccessRight( user, func, ownership, getDefaultRight( user, func, ownership ));
					
					// ... add Access Record to DB ...
					AccessRight newRecord = addAccessRights( record ); 
					
					if ( newRecord == null ) {
						// Cannot store record but it must be used anyway
						logger.error( "Cannot add Access Record for the user: " + user.getFirstAndLastNames());
						
						newRecord = record;
					}
					
					// ... and also add new record to the list
					list.add( newRecord );
					tmpMap.addEntry( newRecord );
					
				}
			}
			
		}
		
		return list;
	}

	public AccessRight addAccessRights( AccessRight record ) {
		
		if ( record == null )
			throw new IllegalArgumentException ( "AccessRight record cannot be null or emptyl!" );
		
		if ( record.getFunction() == null || record.getType() == null || record.getPermission() == null )
			throw new IllegalArgumentException ( "AccessRight record PARAMETERS cannot be null or emptyl!" );
		
		AccessRight newRecord = null;
		
		try {
			newRecord = DataFacade.getInstance().insert( record );
			
			TransactionsFacade.getInstance().writeAccessRights( 
					(( InventoryUI )UI.getCurrent()).getSessionOwner(), 
					newRecord.getUser(), 
					TransactionOperation.ADD );
			
		} catch ( Exception e) {
			logger.error( "Cannot insert AccessRights record into DB\n" + e );
		}
		
		if ( logger.isDebugEnabled())
				logger.debug( "New AccessRights record was added: " + newRecord );
		
		return newRecord;
	}
	
	public AccessRight updateAccessRights( AccessRight record ) {

		AccessRight newRecord = null;
		
		try {
			
			newRecord = DataFacade.getInstance().find( AccessRight.class, record.getId());
			
			if ( newRecord == null ) {
				logger.error( "Old Access Record was not found in the DB. Cannot update!!!" );
				
				return null;
			}

//			newRecord.setFunction( record.getFunction());
//			newRecord.setType( record.getType());
			newRecord.setPermission( record.getPermission());
			
			newRecord = DataFacade.getInstance().merge( newRecord );
			
			if ( newRecord != null ) {
				TransactionsFacade.getInstance().writeAccessRights( 
						(( InventoryUI )UI.getCurrent()).getSessionOwner(), 
						newRecord.getUser(), 
						TransactionOperation.EDIT );
			}
			
		} catch ( Exception e) {
			logger.error( "Cannot update AccessRights record into DB\n" + e );
		}
		
		if ( logger.isDebugEnabled())
				logger.debug( "AccessRights record was updated: " + newRecord );
		
		return newRecord;
	}

	private PermissionType getDefaultRight( OrgUser  user, FunctionalityType func, OwnershipType ownership ) {

		PermissionType permission = PermissionType.NO;
	
		// First one for superuser from Uisko only
		if ( user.isSuperUserFlag() && user.getOrganisation().isServiceOwner()) {
			permission = PermissionType.RW;
		} else {
			
			permission = getDefPermissionFromDefMap( user, func, ownership );		
		}
/*		
		else if ( func == FunctionalityType.MESSAGING	&& ownership != OwnershipType.ANY ) permission = PermissionType.RW;
		else if ( func == FunctionalityType.BORROW 		&& ownership == OwnershipType.OWN ) permission = PermissionType.R;
		else if ( func == FunctionalityType.BORROW 		&& ownership == OwnershipType.COMPANY ) permission = PermissionType.RW;
		else if ( func == FunctionalityType.CHANGESTATUS	&& ownership == OwnershipType.OWN ) permission = PermissionType.RW;
		else if ( func == FunctionalityType.CHANGESTATUS	&& ownership == OwnershipType.COMPANY ) permission = PermissionType.R;
*/	
		
//		permission = defaultMap.get( ) 
		
		
		return permission;
	}

//	public List<AccessGroup> 
	/*
	 *   Below is a default table to set up access rights per Access Group
	 *   
	 *   Following fields are there:
	 *   AccessGroup, Function, Own Rights, Company Rights, Service Wide Rights        
	 * 
	 */
	private static String [][] defaultRightsArray = { 
			{ "VIEWER", "BORROW",		"R",	"NO",	"NO" },
			{ "VIEWER", "CHANGESTATUS",	"R",	"NO",	"NO" },
			{ "VIEWER", "MESSAGING",	"R",	"NO",	"NO" },
			{ "VIEWER", "USERS_MGMT",	"R",	"NO",	"NO" },
			{ "VIEWER", "TOOLS_MGMT",	"R",	"NO",	"NO" },
			{ "VIEWER", "ORGS_MGMT",	"R",	"NO",	"NO" },
			{ "VIEWER", "TRN_MGMT",		"R",	"NO",	"NO" },
			
			{ "USER", "BORROW",			"R",	"RW",	"NO" },
			{ "USER", "CHANGESTATUS",	"RW",	"R",	"NO" },
			{ "USER", "MESSAGING",		"RW",	"R",	"NO" },
			{ "USER", "USERS_MGMT",		"RW",	"NO",	"NO" },
			{ "USER", "TOOLS_MGMT",		"NO",	"NO",	"NO" },
			{ "USER", "ORGS_MGMT",		"NO",	"NO",	"NO" },
			{ "USER", "TRN_MGMT",		"R",	"NO",	"NO" },
			
			{ "FOREMAN", "BORROW",		"R",	"RW",	"NO" },
			{ "FOREMAN", "CHANGESTATUS","RW",	"RW",	"NO" },
			{ "FOREMAN", "MESSAGING",	"RW",	"RW",	"NO" },
			{ "FOREMAN", "USERS_MGMT",	"RW",	"RW",	"NO" },
			{ "FOREMAN", "TOOLS_MGMT",	"RW",	"RW",	"NO" },
			{ "FOREMAN", "ORGS_MGMT",	"NO",	"NO",	"NO" },
			{ "FOREMAN", "TRN_MGMT",	"R",	"NO",	"NO" },
			
			{ "BOSS", "BORROW",			"R",	"RW",	"NO" },
			{ "BOSS", "CHANGESTATUS",	"RW",	"RW",	"NO" },
			{ "BOSS", "MESSAGING",		"RW",	"RW",	"NO" },
			{ "BOSS", "USERS_MGMT",		"RW",	"RW",	"NO" },
			{ "BOSS", "TOOLS_MGMT",		"RW",	"RW",	"NO" },
			{ "BOSS", "ORGS_MGMT",		"RW",	"RW",	"NO" },
			{ "BOSS", "TRN_MGMT",		"R",	"R",	"NO" },
			
			{ "ADMIN", "BORROW",		"R",	"R",	"NO" },
			{ "ADMIN", "CHANGESTATUS",	"R",	"R",	"NO" },
			{ "ADMIN", "MESSAGING",		"RW",	"RW",	"NO" },
			{ "ADMIN", "USERS_MGMT",	"RW",	"RW",	"NO" },
			{ "ADMIN", "TOOLS_MGMT",	"RW",	"RW",	"NO" },
			{ "ADMIN", "ORGS_MGMT",		"RW",	"RW",	"NO" },
			{ "ADMIN", "TRN_MGMT",		"RW",	"RW",	"NO" },
			
	};
	
	private static Map<Long, PermissionType> defaultMap = new HashMap<Long, PermissionType>(); 
			
			
	private static void initDefMap() {
		
		for ( String [] row : defaultRightsArray ) {
			
			// Handle one row 
			defaultMap.put( getDefMapKey( AccessGroups.valueOf( row[0] ), FunctionalityType.valueOf( row[1] ), OwnershipType.OWN ), PermissionType.valueOf( row[2] ));
			defaultMap.put( getDefMapKey( AccessGroups.valueOf( row[0] ), FunctionalityType.valueOf( row[1] ), OwnershipType.COMPANY ), PermissionType.valueOf( row[3] ));
			defaultMap.put( getDefMapKey( AccessGroups.valueOf( row[0] ), FunctionalityType.valueOf( row[1] ), OwnershipType.ANY ), PermissionType.valueOf( row[4] ));
		}
	}
	
	private static PermissionType getDefPermissionFromDefMap( OrgUser user, FunctionalityType func, OwnershipType ownership ) { 
		
		PermissionType permission = PermissionType.NO;

		long key = getDefMapKey( user.getAccessGroup(), func, ownership );
		if ( key >= 0 && defaultMap.containsKey(key)) {
		
			permission = defaultMap.get( key ); 
		}
		
		return permission;
	}
	
	private static long getDefMapKey( AccessGroups group, FunctionalityType func, OwnershipType ownership ) {
		
		return group.ordinal()*100 + +func.ordinal()*10 + ownership.ordinal();
		
	}
	
	
}

