package com.c2point.tools.datalayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.InventoryUI;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.tool.Category;
import com.c2point.tools.entity.tool.Tool;
import com.c2point.tools.entity.transactions.TransactionOperation;
import com.vaadin.ui.UI;

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
	public Tool searchTool( Organisation org, Tool searchTool ) {
		
		Tool resTool = null;
		
		Collection<Tool> existingToolList = searchTools( org, searchTool.getCategory());
		
		for ( Tool tool : existingToolList ) {
			
			boolean checkCode = searchTool.getCode() != null && searchTool.getCode().length() > 0; 
			
			if ( tool != null && tool.getName() != null 

//					&& tool.getCode().trim().compareToIgnoreCase( searchTool.getCode().trim()) == 0
					
					&& ( checkCode ? tool.getCode().trim().compareToIgnoreCase( searchTool.getCode().trim()) == 0 : true )
					
					
					
					&& tool.getName().trim().compareToIgnoreCase( searchTool.getName().trim()) == 0
//					&& tool.getDescription().trim().compareToIgnoreCase( searchTool.getDescription().trim()) == 0
					&& tool.getManufacturer().getId() == searchTool.getManufacturer().getId()
					&& tool.getModel().trim().compareToIgnoreCase( searchTool.getModel().trim()) == 0
			){
				resTool = tool;
				break;
			}
		}
		
		
		return resTool;
	}

	
	public Collection<Tool> getTools( Organisation org ) {
		
		if ( org == null )
			throw new IllegalArgumentException( "Valid Organisation cannot be null when add Tool!" );

		EntityManager em = DataFacade.getInstance().createEntityManager();
		
		TypedQuery<Tool> query = null;
		List<Tool> results = new ArrayList<Tool>();

		try {
			
			query = em.createNamedQuery( "listTools", Tool.class )
				.setParameter( "org", org );
	
			results = query.getResultList();
			
			
		} catch ( IllegalArgumentException e ) {
			logger.error( e );
		} finally {
			em.close();
		}
			
		return results;
		
	}

	public Collection<Tool> searchTools( Organisation org, Category category ) {
		
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
		
		try {
			OrgUser whoDid = (( InventoryUI )UI.getCurrent()).getSessionOwner();
			TransactionsFacade.getInstance().writeTool( whoDid, newTool, TransactionOperation.ADD );
			
		} catch ( Exception e ) {
			logger.error( "Cannot identify who add Tool");
		}

		if ( logger.isDebugEnabled() && newTool != null ) 
			logger.debug( "Tool has been added: " + newTool );
		
		
		return newTool;
		
	}

	public long count( Organisation org ) {
		
		long result = 0;

		if ( org == null )
			throw new IllegalArgumentException( "Valid Organisation cannot be null!" );
		
		EntityManager em = DataFacade.getInstance().createEntityManager();
		TypedQuery<Long> query = null;
		
		try {
			query = em.createNamedQuery( "countAllOrgTools", Long.class )
					.setParameter( "org", org );
			
			result = query.getSingleResult();
			
			if ( logger.isDebugEnabled()) logger.debug( "**** Number of all Tool Types in Org = " + result );
			
		} catch ( NoResultException e ) {
			if ( logger.isDebugEnabled()) logger.debug( "No users found!" );
		} catch ( Exception e ) {
			logger.error( e );
		} finally {
			em.close();
		}
		
		return result;
		
	}

	public void setUniqueCode( Tool tool, Organisation org ) {

		boolean useToolCode = SettingsFacade.getInstance().getBoolean( org, "useToolCode", false );
		
		if ( useToolCode ) {

			long lastUniqueCode = SettingsFacade.getInstance().getLong( org, "lastToolCode" );
			
			if ( lastUniqueCode <= 0 && org != null ) {
				
				lastUniqueCode = this.count( org );
	
			}
	
			int codeLength = SettingsFacade.getInstance().getInteger( org, "toolCodeLength", 6 );
			
			lastUniqueCode++;
			
			String newCode = StringUtils.leftPad(
					Long.toString( lastUniqueCode ),
					codeLength,	
					'0'
			);
	
			// Store new lastUniqueCode
			SettingsFacade.getInstance().set( org, 
											  "lastToolCode", 
											  lastUniqueCode );
			// set up Tool code
			tool.setCode( newCode );
		}
	}
	
}


