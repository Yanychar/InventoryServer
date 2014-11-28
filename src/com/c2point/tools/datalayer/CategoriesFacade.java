package com.c2point.tools.datalayer;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.tool.Category;

public class CategoriesFacade {
	
	private static Logger logger = LogManager.getLogger( CategoriesFacade.class.getName()); 

	private static int					MAX_INSTANCE_NUMBER = 2;
	private static CategoriesFacade []	instances;
	private static int					next_instance_number;
	
	public static CategoriesFacade getInstance() {
		
		if ( instances == null ) {
			instances = new CategoriesFacade[ MAX_INSTANCE_NUMBER ];
			for ( int i = 0; i < MAX_INSTANCE_NUMBER; i++ ) {
				instances[ i ] = new CategoriesFacade();  
			}
			next_instance_number = 0;
			
		}
		
		CategoriesFacade ret = instances[ next_instance_number ];
		if ( logger.isDebugEnabled()) logger.debug( "CategoriesFacade instance number retirned is " + next_instance_number + " from " + MAX_INSTANCE_NUMBER + " available!" );
		
		next_instance_number = ++next_instance_number % MAX_INSTANCE_NUMBER ;
		
		return ret;
	}
	
	public List<Category> listTop( Organisation org ) {
			
		return listTop( org, true ); 
	}

	public List<Category> listTop( Organisation org, boolean showEvenEmpty ) {
		
		if ( org == null ) {
			throw new IllegalArgumentException( "Valid Organisation should be specified!" );

		}

		EntityManager em = DataFacade.getInstance().createEntityManager();
		TypedQuery<Category> query = null;
		List<Category> results = null;
		
		try {
			
			if ( showEvenEmpty ) {
				query = em.createNamedQuery( "listTop", Category.class )
								.setParameter( "org", org );
			} else {
				query = em.createNamedQuery( "listTopNotEmpty", Category.class )
						.setParameter( "org", org );
				
			}

			results = query.getResultList();
			if ( logger.isDebugEnabled()) logger.debug( "**** Fetched list of Top Categories. Size = " + results.size());
			
		} catch ( NoResultException e ) {
			if ( logger.isDebugEnabled()) logger.debug( "No TopCategories found!" );
		} catch ( Exception e ) {
			results = null;
			logger.error( e );
		} finally {
			em.close();
		}
		
		return results;
		
	}

	public Category add( Category category ) {

		Category newCategory = null;
		
		if ( category == null )
			throw new IllegalArgumentException( "Valid category cannot be null!" );
		
		try {
			newCategory = DataFacade.getInstance().insert( category );
		} catch ( Exception e ) {
			logger.error( "Failed to add Category: " + category );
			logger.error( e );
			return null;
		}
		

		if ( logger.isDebugEnabled() && newCategory != null ) 
			logger.debug( "Category has been added: " + newCategory );
		
		
		return newCategory;
	}
	
}
