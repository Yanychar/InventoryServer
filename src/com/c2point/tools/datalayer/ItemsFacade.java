package com.c2point.tools.datalayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.InventoryUI;
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
import com.c2point.tools.entity.transactions.TransactionOperation;
import com.vaadin.ui.UI;

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
/*
	public ToolItem updateTakeOwer( ToolItem item, OrgUser newUser ) {
		return updateTakeOwer( null, item, newUser );
	}
	
	public ToolItem updateTakeOwer( OrgUser whoDid, ToolItem item, OrgUser newUser ) {
		
		item.setReservedBy( null );
		item.setCurrentUser( newUser ); 
		return update( whoDid, item );
	}
*/	
	public ToolItem updateStatus( ToolItem item, ItemStatus status ) {
		return updateStatus( null, item, status );
	}
	
	public ToolItem updateStatus( OrgUser whoDid, ToolItem item, ItemStatus status ) {
	
		item.setStatus( status );
		return update( whoDid, item );

	}
	
	public ToolItem updateLocation( ToolItem item, GeoLocation location ) {
		return updateLocation( null, item, location );
	}	
	public ToolItem updateLocation( OrgUser whoDid, ToolItem item, GeoLocation location ) {

		item.setLastKnownLocation( location );
		return update( whoDid, item );
		
	}

	public ToolItem updateUser( ToolItem item, OrgUser newUser ) {
		return updateUser( null, item, newUser );
	}
		
	public ToolItem updateUser( OrgUser whoDid, ToolItem item, OrgUser newUser ) {
		
		item.setReservedBy( null );
//		item.setStatus( ItemStatus.INUSE );
		item.setCurrentUser( newUser ); 
		
		return update( whoDid, item );
		
	}

	public ToolItem update( ToolItem item ) { 
		return update( null, item ); 
	}
		
	public ToolItem update( OrgUser whoDid, ToolItem item ) { 

		if ( whoDid == null ) {
			
			try {
				whoDid = (( InventoryUI )UI.getCurrent()).getSessionOwner();
			} catch ( Exception e ) {
				logger.error( "Cannot identify who are doing update");
			}
		}
		
		
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
		
		boolean userUpdatedFlag = false;
		OrgUser oldUser = newItem.getCurrentUser();
		boolean statusUpdatedFlag = false;
		ItemStatus oldStatus = newItem.getStatus();
		
		newItem.setTool( item.getTool());
		newItem.setQuantity( item.getQuantity());
		
		newItem.setResponsible( item.getResponsible());
		
		if ( oldUser != null && item.getCurrentUser() == null
			||
			oldUser == null && item.getCurrentUser() != null
			|| 
			oldUser != null && item.getCurrentUser() != null 
			  && oldUser.getId() != item.getCurrentUser().getId()	
		) {
			newItem.setCurrentUser( item.getCurrentUser());
			userUpdatedFlag = true;
		}
		
		newItem.setReservedBy( item.getReservedBy());
		
		if ( oldStatus != item.getStatus()) {
			newItem.setStatus( item.getStatus());
			statusUpdatedFlag = true;
		}
		
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
			
			if ( whoDid != null ) {
				TransactionsFacade.getInstance().writeToolItem( whoDid, newItem, TransactionOperation.EDIT );
	
				if ( userUpdatedFlag == true ) {
					TransactionsFacade.getInstance().writeToolItemUserChanged( 	whoDid, newItem, 
																				oldUser, newItem.getCurrentUser()); 
							
				}
	
				if ( statusUpdatedFlag == true ) {
					TransactionsFacade.getInstance().writeToolItemStatus( whoDid, newItem ); 
							
				}
			}
			
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

			try {
				OrgUser whoDid = (( InventoryUI )UI.getCurrent()).getSessionOwner();
				TransactionsFacade.getInstance().writeToolItem( whoDid, newItem, TransactionOperation.DELETE );
				
			} catch ( Exception e ) {
				logger.error( "Cannot identify who delete ToolItem");
			}
			
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
			
			try {
				OrgUser whoDid = (( InventoryUI )UI.getCurrent()).getSessionOwner();
				TransactionsFacade.getInstance().writeToolItem( whoDid, newItem, TransactionOperation.ADD );
				
			} catch ( Exception e ) {
				logger.error( "Cannot identify who add ToolItem");
			}
			
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
