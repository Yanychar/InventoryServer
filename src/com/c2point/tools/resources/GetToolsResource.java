package com.c2point.tools.resources;

import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.DataFacade;
import com.c2point.tools.datalayer.ToolsAndItemsFacade;
import com.c2point.tools.entity.authentication.Account;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Category;
import com.c2point.tools.entity.tool.identity.ToolIdentity;
import com.c2point.tools.entity.tool.identity.ToolIdentityType;
import com.c2point.tools.resources.stubs.ToolItemsListStub;

@Path("/gettools")
public class GetToolsResource extends BaseResource {
	private static Logger logger = LogManager.getLogger( GetToolsResource.class.getName());
	
	@GET
	@Produces( MediaType.APPLICATION_JSON )
	public ToolItemsListStub get(
			@DefaultValue("NOT_SPECIFIED") @QueryParam("sessionid") String sessionId, 
			@DefaultValue( "-1" ) @QueryParam("categoryid") long categoryId, 
			@DefaultValue( "-1" ) @QueryParam("toolid") long toolId, 
			@DefaultValue( "-1" ) @QueryParam("userid") long userId, 
			@DefaultValue( "" ) @QueryParam("barcode") String barcode,
			@DefaultValue( "true" ) @QueryParam("barcode") boolean showPublicOnly
		) {

		if ( logger.isDebugEnabled()) {
			logger.debug( "Start GetToolsResource.getJSON()..." );
			// Show received parameters
			logger.debug( "  Request parameters: " 
							+ "sessionId='" + sessionId + "', "
							+ "category id='" + categoryId + "' "
							+ "user id='" + userId + "' "
							+ "tool id='" + toolId + "' "
							+ "barcode='" + barcode + "' "
							+ "hide Personal Tools = " + showPublicOnly + "' "
			);
			
		}

		Account account = findAccount( sessionId );

		if ( account == null ) {
			if ( logger.isDebugEnabled()) {
				logger.debug( "  FAILED because account not found");
				logger.debug( "... end GetCategoriesResource.getJSON()");
			}
			throw new WebApplicationException( Response.Status.NOT_FOUND );
		}
		if ( logger.isDebugEnabled()) logger.debug( "  Account found" );
		
		/*
		 * if toolid != null than priority to fetch tool and check category if specified
		 * otherwise fetch tools by category
		 */

		Collection<ToolItem> tList = null;

		/* New selection of fetch */
		if ( toolId >= 0 ) {

			ToolItem item = DataFacade.getInstance().find( ToolItem.class, toolId );
			if ( item != null ) {
				if ( logger.isDebugEnabled()) logger.debug( "  Tool found. Will be added to resulted list!" );
				tList = new ArrayList<ToolItem>();
				tList.add( item );
			}
			
		} else if ( barcode != null && barcode.length() > 0 ) {

			ToolIdentity identity = new ToolIdentity( ToolIdentityType.BARCODE, barcode ); 
			tList = ToolsAndItemsFacade.getInstance().getItems( account.getUser().getOrganisation(), identity );
			
		} else if ( categoryId >= 0 ) {
			// Items belonged to category and subcategories will be fetched
			
			Category category = DataFacade.getInstance().find( Category.class, categoryId );
			if ( category != null ) {
				if ( logger.isDebugEnabled()) logger.debug( "  Category found" );
			
				if ( userId >= 0 ) {
					// ... belonged to particular user (personal and non personal)
					if ( logger.isDebugEnabled()) logger.debug( "  Search All tools belonged to User!" );
					tList = ToolsAndItemsFacade.getInstance().getItems( category, account.getUser());
					
				} else if ( showPublicOnly ) {
					// ... without Tools with PersonalFlag
					if ( logger.isDebugEnabled()) logger.debug( "  Search All tools with Personal Tools excluded!" );
					tList = ToolsAndItemsFacade.getInstance().getItemsPublic( category, account.getUser().getOrganisation());
					
				} else {
					// .. without restrictions. All belonged to specified Category 
					if ( logger.isDebugEnabled()) logger.debug( "  Category Id only specified. Will be search all tools belonged to category" );
					tList = ToolsAndItemsFacade.getInstance().getItems( category, account.getUser().getOrganisation());
					
				}
			}
			
		} else {
			// All items
			if ( userId >= 0 ) {
				// ... belonged to particular user (personal and non personal)
				if ( logger.isDebugEnabled()) logger.debug( "  Search All tools belonged to User!" );
				tList = ToolsAndItemsFacade.getInstance().getItems( account.getUser());
				
			} else if ( showPublicOnly ) {
				// ... without Tools with PersonalFlag
				if ( logger.isDebugEnabled()) logger.debug( "  Search All tools with Personal Tools excluded!" );
				tList = ToolsAndItemsFacade.getInstance().getItemsPublic( account.getUser().getOrganisation());
				
			} else {
				// .. without restrictions
				if ( logger.isDebugEnabled()) logger.debug( "  Search All tools without any restrictions will be returned" );
				tList = ToolsAndItemsFacade.getInstance().getItems( account.getUser().getOrganisation());
				
			}
			
			
		}
		
		
		
		
		
		
		/* ... end of New selection of fetch */
		
/*		
		if ( toolId >= 0 ) {
			// fetch exact tool
			if ( logger.isDebugEnabled()) logger.debug( "  toolId specified. Search it..." );
			ToolItem item = DataFacade.getInstance().find( ToolItem.class, toolId );

			// Validate that item belongs to specified category if any
			if ( item != null && categoryId >= 0 ) {
				if ( logger.isDebugEnabled()) logger.debug( "  Tool found. categoryId specified. Check Tool belongs to Category ..." );
				
				Category parentOrItself = DataFacade.getInstance().find( Category.class, categoryId );
				if ( parentOrItself != null && !item.getTool().getCategory().belongedTo( parentOrItself )) {

					// Found tool does not belong to specified category or its childs
					if ( logger.isDebugEnabled()) logger.debug( "  Tool found but does not belong to specified Category ==>> NOT FOUND" );
					
					item = null;
				}

			}
			
			if ( item != null ) {
				if ( logger.isDebugEnabled()) logger.debug( "  Tool found. Will be added to resulted list!" );
				tList = new ArrayList<ToolItem>();
				tList.add( item );
			}
			
		} else if ( categoryId >= 0 ) {
			// Fetch tools belonged to specified category
			if ( logger.isDebugEnabled()) logger.debug( "  Category Id only specified. Will be search all tools belonged to category" );
			Category category = DataFacade.getInstance().find( Category.class, categoryId );
			
			if ( category != null ) {
				if ( logger.isDebugEnabled()) logger.debug( "  Category found" );
				tList = ToolsAndItemsFacade.getInstance().getItems( account.getUser().getOrganisation(), category );
			}
		} else if ( barcode != null && barcode.length() > 0 ) { 
			// Search the Tool by BarCode specified
			ToolIdentity identity = new ToolIdentity( ToolIdentityType.BARCODE, barcode ); 
			tList = ToolsAndItemsFacade.getInstance().getItems( account.getUser().getOrganisation(), identity );
			
		} else {
			// Fetch all tools
			if ( logger.isDebugEnabled()) logger.debug( "  toolId == null and categoryId == null ==>> All tools will be returned" );
			tList = ToolsAndItemsFacade.getInstance().getItems( account.getUser().getOrganisation());
		}
*/		
		if ( logger.isDebugEnabled()) {
			if ( tList != null ) {
				for ( ToolItem member : tList ) {
					logger.debug( member.toString());
				}
			} else {
				logger.debug( "Nothing was found");
			}
		}
		
		if ( tList == null || tList.size() <= 0 ) {
			// Nothing was found

			if ( logger.isDebugEnabled()) logger.debug( "Nothing was found" );
			logger.debug( "... end GetToolsResource.getJSON() with result NOT FOUND");
			throw new WebApplicationException( Response.Status.NOT_FOUND );
		}
		
		ToolItemsListStub stub;
		stub = new ToolItemsListStub( tList );
		
		if ( logger.isDebugEnabled()) {
			logger.debug( "***** Response: succeeded!" );
			logger.debug( stub.toString());
			logger.debug( "... end GetToolsResource.getJSON()");
		}
		
		return stub;
	
	}
	
}
