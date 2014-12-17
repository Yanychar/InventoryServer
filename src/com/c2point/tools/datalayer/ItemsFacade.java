package com.c2point.tools.datalayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.location.GeoLocation;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ItemStatus;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Category;
import com.c2point.tools.entity.tool.Manufacturer;
import com.c2point.tools.entity.tool.Tool;
import com.c2point.tools.entity.tool.identity.ToolIdentity;
import com.c2point.tools.entity.tool.identity.ToolIdentityType;

public class ItemsFacade extends DataFacade {

	private static Logger logger = LogManager.getLogger( ItemsFacade.class.getName()); 

	private static int						MAX_INSTANCE_NUMBER = 4;
	private static ItemsFacade []	instances;
	private static int						next_instance_number;
	
	public static ItemsFacade getInstance() {
		
		if ( instances == null ) {
			instances = new ItemsFacade[ MAX_INSTANCE_NUMBER ];
			for ( int i = 0; i < MAX_INSTANCE_NUMBER; i++ ) {
				instances[ i ] = new ItemsFacade();  
			}
			next_instance_number = 0;
			
		}
		
		ItemsFacade ret = instances[ next_instance_number ];
		if ( logger.isDebugEnabled()) logger.debug( "ItemsFacade instance number retirned is " + next_instance_number + " from " + MAX_INSTANCE_NUMBER + " available!" );
		
		next_instance_number = ++next_instance_number % MAX_INSTANCE_NUMBER ;
		
		return ret;
	}
	
	private ItemsFacade() {
		super();
	}
	
	/*
	 * Below methods for Tool objects manipulation
	 */

	/*
	 * Below methods for RepositoryItem objects manipulation
	 */
	
	public Collection<ToolItem> getItems( OrgUser user ) {
		return getItems( null, user, false );
	}
	public Collection<ToolItem> getItems( Organisation org ) {
		return getItems( org, null, false );
	}
	public Collection<ToolItem> getItemsPublic( Organisation org ) {
		return getItems( org, null, true );
	}
	private Collection<ToolItem> getItems( Organisation org, OrgUser user, boolean publicOnly ) {

		if ( org == null && user == null )
			throw new IllegalArgumentException( "Valid Organisation and/or OrgUser cannot be null!" );

		EntityManager em = DataFacade.getInstance().createEntityManager();
		TypedQuery<ToolItem> query = null;
		List<ToolItem> results = null;
		
		try {
			if ( user != null ) {
				
				query = em.createNamedQuery( "listAllBelongTo", ToolItem.class )
				.setParameter( "user", user );
				
			} else if ( publicOnly ) {
	
				query = em.createNamedQuery( "listAllPublic", ToolItem.class )
				.setParameter( "org", org );
				
			} else {
				
				query = em.createNamedQuery( "listAll", ToolItem.class )
				.setParameter( "org", org );
	
			}

			results = query.getResultList();
			if ( logger.isDebugEnabled()) logger.debug( "**** Fetched list of all RepositoryItem-s. Size = " + results.size());
			
		} catch ( NoResultException e ) {
			if ( logger.isDebugEnabled()) logger.debug( "No RepositoryItem-s found!" );
		} catch ( Exception e ) {
			results = null;
			logger.error( e );
		} finally {
			em.close();
		}
		
		return results;
		
	}

	public Collection<ToolItem> getItems( Category category, OrgUser user ) {
		return getItems( null, category, user, false );
	}
	public Collection<ToolItem> getItems( Category category, Organisation org ) {
		return getItems( org, category, null, false );
	}
	public Collection<ToolItem> getItemsPublic( Category category, Organisation org ) {
		return getItems( org, category, null, true );
	}
	public Collection<ToolItem> getItems( Organisation org, Tool tool ) {
		
		if ( org == null )
			throw new IllegalArgumentException( "Valid Organisation cannot be null!" );

		EntityManager em = DataFacade.getInstance().createEntityManager();
		TypedQuery<ToolItem> query = null;
		List<ToolItem> results = null;
		
		try {
			query = em.createNamedQuery( "listTool", ToolItem.class )
							.setParameter( "org", org )
							.setParameter( "tool", tool );

			results = query.getResultList();
			if ( logger.isDebugEnabled()) logger.debug( "**** Fetched list of RepositoryItem-s for Tool. Size = " + results.size());
			
		} catch ( NoResultException e ) {
			if ( logger.isDebugEnabled()) logger.debug( "No RepositoryItem-s found!" );
		} catch ( Exception e ) {
			results = null;
			logger.error( e );
		} finally {
			em.close();
		}
		
		return results;
		
	}

	public Collection<ToolItem> getItems( Organisation org, ToolIdentity identity ) {
		
		if ( org == null )
			throw new IllegalArgumentException( "Valid Organisation cannot be null!" );

		EntityManager em = DataFacade.getInstance().createEntityManager();
		TypedQuery<ToolItem> query = null;
		List<ToolItem> results = null;
		
		try {
			
			if ( identity.getType() == ToolIdentityType.BARCODE ) {
				query = em.createNamedQuery( "listToolBarcode", ToolItem.class )
								.setParameter( "org", org )
								.setParameter( "barcode", identity.getBarCode());
			} else if ( identity.getType() == ToolIdentityType.SEARCHSTRING ) {
				query = em.createNamedQuery( "listSearchSubstr", ToolItem.class )
						.setParameter( "org", org )
						.setParameter( "searchStr", identity.getSearchString());

			} else {
				logger.error( "Unsupported ToolIdentityType had been used!" );
				return null;
			}
			
			results = query.getResultList();
			if ( logger.isDebugEnabled()) logger.debug( "**** Fetched list of RepositoryItem-s for Tool. Size = " + results.size());
			
		} catch ( NoResultException e ) {
			if ( logger.isDebugEnabled()) logger.debug( "No RepositoryItem-s found!" );
		} catch ( Exception e ) {
			results = null;
			logger.error( e );
		} finally {
			em.close();
		}
		
		return results;
		
	}

	public Collection<Manufacturer> getManufacturers() {
		
		Collection<Manufacturer> results = null;
		
		EntityManager em = DataFacade.getInstance().createEntityManager();

		TypedQuery<Manufacturer> query;

		try {
			query = em.createNamedQuery( "listActive", Manufacturer.class );

			results = query.getResultList();

			if ( logger.isDebugEnabled()) logger.debug( "**** Fetched list of Manufacturers. Size = " + results.size());
			
		} catch ( NoResultException e ) {
			if ( logger.isDebugEnabled()) logger.debug( "No Manufacturers found!" );
			
		} catch ( Exception e ) {
			logger.error( e );
		} finally {
			em.close();
		}
		
		return results;
	}

	
	public ToolItem updateTakeOwer( ToolItem item, OrgUser newUser ) {
		
		item.setReservedBy( null );
		item.setCurrentUser( newUser ); 
		return update( item );
/*
		item.setReservedBy( null );
		return update( item, -1, newUser, null, ItemStatus.INUSE, null, null  );
 */
	}
	
	public ToolItem updateStatus( ToolItem item, ItemStatus status ) {
	
		item.setStatus( status );
		return update( item );
		
/*
 * 		return update( item, -1, null, null, status, null, null  );
 */
	}
	
	
	public ToolItem updateLocation( ToolItem item, GeoLocation location ) {

		item.setLastKnownLocation( location );
		return update( item );
		
/*
 * 		return update( item, -1, null, null, null, location, null  );
*/
	}

/*	
	private  ToolItem update( ToolItem oldItem, 
								int quantity,
								OrgUser currentUser,
								OrgUser reservedBy,
								ItemStatus status,
								GeoLocation lastKnownLocation,
								String barcode
								
								) {

		ToolItem newItem = null;
		boolean wasChanged = false;
		
		if ( oldItem == null )
			throw new IllegalArgumentException( "Valid ToolItem cannot be null!" );
		
		try {
			
			newItem = DataFacade.getInstance().find( ToolItem.class, oldItem.getId());
			
		} catch ( Exception e ) {
			logger.error( "Failed to update ToolItem: " + oldItem );
			logger.error( e );
			return null;
		}
		
		if ( quantity >= 0 && quantity != newItem.getQuantity() ) { 
			newItem.setQuantity( quantity ); 
			wasChanged = true; 
		}
		
		if ( currentUser != null && currentUser != newItem.getCurrentUser()) { 
			newItem.setCurrentUser( currentUser ); 
			wasChanged = true; 
		}
		
		if ( reservedBy != null && reservedBy != newItem.getReservedBy()) { 
			newItem.setReservedBy( reservedBy ); 
			wasChanged = true; 
		}
		if ( status != null && status != newItem.getStatus()) { 
			newItem.setStatus( status ); 
			wasChanged = true; 
		}
		if ( lastKnownLocation != null && lastKnownLocation != newItem.getLastKnownLocation()) { 
			newItem.setLastKnownLocation( lastKnownLocation ); 
			wasChanged = true; 
		}
		if ( barcode != null && barcode != newItem.getBarcode()) { 
			newItem.setBarcode( barcode ); 
			wasChanged = true; 
		}
		
		
		if( wasChanged ) {
			
			newItem = DataFacade.getInstance().merge( item );

		} else {
			// Noting to update 
			newItem = item;
		}
		

		
		return newItem;
	}
*/
	public ToolItem update( ToolItem item ) { 

		ToolItem newItem = null;

		if ( item == null )
			throw new IllegalArgumentException( "Valid ToolItem cannot be null!" );
		
		try {
			
			newItem = DataFacade.getInstance().find( ToolItem.class, item.getId());
			
		} catch ( Exception e ) {
			logger.error( "Failed to update ToolItem: " + item );
			logger.error( e );
			return null;
		}
		
		newItem.setTool( item.getTool());
		newItem.setQuantity( item.getQuantity());
		
		newItem.setResponsible( item.getResponsible());
		newItem.setCurrentUser( item.getCurrentUser());
		newItem.setReservedBy( item.getReservedBy());
		
		newItem.setStatus( item.getStatus());

		newItem.setLastKnownLocation( item.getLastKnownLocation());
		
		newItem.setSerialNumber( item.getSerialNumber());
		newItem.setBarcode( item.getBarcode());
		
		newItem.setPersonalFlag( item.isPersonalFlag());

		newItem.setBuyTime( item.getBuyTime());
		newItem.setMaintenance( item.getMaintenance());
		newItem.setPrice( item.getPrice());
		newItem.setTakuu( item.getTakuu());
		
		try {
			newItem = DataFacade.getInstance().merge( newItem );
		} catch ( Exception e ) {
			logger.error( "Failed to update ToolItem: " + item );
			logger.error( e );
			return null;
		}

		if ( logger.isDebugEnabled() && newItem != null ) 
			logger.debug( "ToolItem has been updated: " + newItem );

		return newItem;
	}
	
	public ToolItem delete( ToolItem item ) { 

		ToolItem newItem = null;

		if ( item == null )
			throw new IllegalArgumentException( "Valid ToolItem cannot be null!" );
		
		try {
			
			newItem = DataFacade.getInstance().find( ToolItem.class, item.getId());
			
		} catch ( Exception e ) {
			logger.error( "Failed to delete ToolItem: " + item );
			logger.error( e );
			return null;
		}
		
		newItem.setDeleted( true );
		
		try {
			newItem = DataFacade.getInstance().merge( newItem );
		} catch ( Exception e ) {
			logger.error( "Failed to delete ToolItem: " + item );
			logger.error( e );
			return null;
		}

		if ( logger.isDebugEnabled() && newItem != null ) 
			logger.debug( "ToolItem has been deleted: " + newItem );

		return newItem;
	}
	
	private Collection<ToolItem> getItems( Organisation org, Category category, OrgUser user, boolean publicOnly ) {
		
		if ( org == null && user == null )
			throw new IllegalArgumentException( "Valid Organisation and/or OrgUser cannot be null!" );

		EntityManager em = DataFacade.getInstance().createEntityManager();
		TypedQuery<ToolItem> query = null;
		List<ToolItem> results = new ArrayList<ToolItem>();

		try {
			
			if ( user != null ) {
				
				query = em.createNamedQuery( "listCategoryBelongTo", ToolItem.class )
				.setParameter( "user", user );
				
			} else if ( publicOnly ) {
	
				query = em.createNamedQuery( "listCategoryPublic", ToolItem.class )
				.setParameter( "org", org );
				
			} else {
				
				query = em.createNamedQuery( "listCategory", ToolItem.class )
				.setParameter( "org", org );
	
			}
			
			query.setParameter( "category", category );
			
		} catch ( IllegalArgumentException e ) {
			logger.error( e );
			return null;
		} finally {
		}
			
		readItemsForOneCategory( query, category, results );
		
		em.close();
		
		return results;
		
	}

	private void readItemsForOneCategory( TypedQuery<ToolItem> query, Category category, List<ToolItem> allResults ) {

		List<ToolItem> thisResults = null;
		
		if ( category != null && allResults != null ) {
			try {
				query.setParameter( "category", category );
	
				thisResults = query.getResultList();
				
				if ( logger.isDebugEnabled()) logger.debug( "**** Fetched list of RepositoryItem-s for one category. Size = " + thisResults.size());
				
				if ( thisResults != null && thisResults.size() > 0 ) {
					// There are something to add to results!
					
					allResults.addAll( thisResults );
				}
				
				// Check child categories
				if ( category.hasChilds()) {
					for ( Category childCategory : category.getChilds()) {
						readItemsForOneCategory( query, childCategory, allResults );
					}
				}
				
			} catch ( NoResultException e ) {
				if ( logger.isDebugEnabled()) logger.debug( "No RepositoryItem-s found!" );
			} catch ( Exception e ) {
				logger.error( e );
			} finally {
			}
		} else {
			logger.error( "Category == null or allResults == null." );
		}
		
	}

	
	public ToolItem add( ToolItem item ) {

		ToolItem newItem = null;
		
		if ( item == null )
			throw new IllegalArgumentException( "Valid ToolItem to add cannot be null!" );
		
		try {
			newItem = DataFacade.getInstance().insert( item );
		} catch ( Exception e ) {
			logger.error( "Failed to add ToolItem: " + item );
			logger.error( e );
			return null;
		}
		

		if ( logger.isDebugEnabled() && newItem != null ) 
			logger.debug( "ToolItem has been added: " + newItem );
		
		
		return newItem;
		
	}

	
}
