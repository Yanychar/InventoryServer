package com.c2point.tools.datalayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import com.c2point.tools.entity.tool.Tool;
import com.c2point.tools.entity.tool.identity.ToolIdentity;
import com.c2point.tools.entity.tool.identity.ToolIdentityType;

public class ToolsAndItemsFacade extends DataFacade {

	private static Logger logger = LogManager.getLogger( ToolsAndItemsFacade.class.getName()); 

	private static int						MAX_INSTANCE_NUMBER = 4;
	private static ToolsAndItemsFacade []	instances;
	private static int						next_instance_number;
	
	public static ToolsAndItemsFacade getInstance() {
		
		if ( instances == null ) {
			instances = new ToolsAndItemsFacade[ MAX_INSTANCE_NUMBER ];
			for ( int i = 0; i < MAX_INSTANCE_NUMBER; i++ ) {
				instances[ i ] = new ToolsAndItemsFacade();  
			}
			next_instance_number = 0;
			
		}
		
		ToolsAndItemsFacade ret = instances[ next_instance_number ];
		if ( logger.isDebugEnabled()) logger.debug( "ToolsAndItemsFacade instance number retirned is " + next_instance_number + " from " + MAX_INSTANCE_NUMBER + " available!" );
		
		next_instance_number = ++next_instance_number % MAX_INSTANCE_NUMBER ;
		
		return ret;
	}
	
	private ToolsAndItemsFacade() {
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

	
	public ToolItem updateTakeOwer( ToolItem item, OrgUser newUser ) {
		
		item.setReservedBy( null );
		return update( item, -1, newUser, null, ItemStatus.INUSE, null, null  );
	}
	
	public ToolItem updateStatus( ToolItem item, ItemStatus status ) {
		
		return update( item, -1, null, null, status, null, null  );
	}
	
	public ToolItem updateLocation( ToolItem item, GeoLocation location ) {
		
		return update( item, -1, null, null, null, location, null  );
	}
	
	private  ToolItem update( ToolItem item, 
								int quantity,
								OrgUser currentUser,
								OrgUser reservedBy,
								ItemStatus status,
								GeoLocation lastKnownLocation,
								String barcode
								
								) {

		ToolItem newItem = null;
		boolean wasChanged = false;
		
		if ( quantity >= 0 && quantity != item.getQuantity() ) { 
			item.setQuantity( quantity ); 
			wasChanged = true; 
		}
		
		if ( currentUser != null && currentUser != item.getCurrentUser()) { 
			item.setCurrentUser( currentUser ); 
			wasChanged = true; 
		}
		
		if ( reservedBy != null && reservedBy != item.getReservedBy()) { 
			item.setReservedBy( reservedBy ); 
			wasChanged = true; 
		}
		if ( status != null && status != item.getStatus()) { 
			item.setStatus( status ); 
			wasChanged = true; 
		}
		if ( lastKnownLocation != null && lastKnownLocation != item.getLastKnownLocation()) { 
			item.setLastKnownLocation( lastKnownLocation ); 
			wasChanged = true; 
		}
		if ( barcode != null && barcode != item.getBarcode()) { 
			item.setBarcode( barcode ); 
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
	
}
