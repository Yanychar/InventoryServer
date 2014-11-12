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
import com.c2point.tools.entity.tool.Producer;
import com.c2point.tools.entity.tool.Tool;
import com.c2point.tools.entity.tool.identity.ToolIdentity;
import com.c2point.tools.entity.tool.identity.ToolIdentityType;

public class ToolsFacade extends DataFacade {

	private static Logger logger = LogManager.getLogger( ToolsFacade.class.getName()); 

	private static int						MAX_INSTANCE_NUMBER = 4;
	private static ToolsFacade []	instances;
	private static int						next_instance_number;
	
	public static ToolsFacade getInstance() {
		
		if ( instances == null ) {
			instances = new ToolsFacade[ MAX_INSTANCE_NUMBER ];
			for ( int i = 0; i < MAX_INSTANCE_NUMBER; i++ ) {
				instances[ i ] = new ToolsFacade();  
			}
			next_instance_number = 0;
			
		}
		
		ToolsFacade ret = instances[ next_instance_number ];
		if ( logger.isDebugEnabled()) logger.debug( "ToolsFacade instance number retirned is " + next_instance_number + " from " + MAX_INSTANCE_NUMBER + " available!" );
		
		next_instance_number = ++next_instance_number % MAX_INSTANCE_NUMBER ;
		
		return ret;
	}
	
	private ToolsFacade() {
		super();
	}
	
	/*
	 * Below methods for Tool objects manipulation
	 */
	public Tool getTool( Organisation org, Tool searchTool ) {
		
		Tool resTool = null;
		
		Collection<Tool> existingToolList = getTools( org, searchTool.getCategory());
		
		for ( Tool tool : existingToolList ) {
			
			if ( tool != null && tool.getName() != null 
					&& tool.getCode().trim().compareToIgnoreCase( searchTool.getCode().trim()) == 0
					&& tool.getName().trim().compareToIgnoreCase( searchTool.getName().trim()) == 0
					&& tool.getDescription().trim().compareToIgnoreCase( searchTool.getDescription().trim()) == 0
					&& tool.getProducer().getId() == searchTool.getProducer().getId()
			){
				resTool = tool;
				break;
			}
		}
		
		
		return resTool;
	}

	
	private Collection<Tool> getTools( Organisation org, Category category ) {
		
		if ( org == null && category == null )
			throw new IllegalArgumentException( "Valid Organisation and/or Category cannot be null when add Tool!" );

		EntityManager em = DataFacade.getInstance().createEntityManager();
		
		TypedQuery<Tool> query = null;
		List<Tool> results = new ArrayList<Tool>();

		try {
			
			query = em.createNamedQuery( "listCategoryTools", Tool.class )
				.setParameter( "org", org )
				.setParameter( "category", category );
	
			results = query.getResultList();
			
			
		} catch ( IllegalArgumentException e ) {
			logger.error( e );
		} finally {
			em.close();
		}
			
		return results;
		
	}

	public Tool add( Tool tool ) {

		Tool newTool = null;
		
		if ( tool == null )
			throw new IllegalArgumentException( "Valid Tool cannot be null!" );
		
		try {
			newTool = DataFacade.getInstance().insert( tool );
		} catch ( Exception e ) {
			logger.error( "Failed to add Tool: " + tool );
			logger.error( e );
			return null;
		}
		

		if ( logger.isDebugEnabled() && newTool != null ) 
			logger.debug( "Tool has been added: " + newTool );
		
		
		return newTool;
		
	}

	
}
