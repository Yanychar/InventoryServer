package com.c2point.tools.resources;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.DataFacade;
import com.c2point.tools.datalayer.ItemsFacade;
import com.c2point.tools.entity.authentication.Account;
import com.c2point.tools.entity.repository.ItemStatus;
import com.c2point.tools.entity.repository.ToolItem;

@Path("/updatestatus")
public class UpdateStatusResource extends BaseResource {
	private static Logger logger = LogManager.getLogger( UpdateStatusResource.class.getName());
	
	@GET
//	@Produces( MediaType.APPLICATION_JSON )
	public Response get(
			@DefaultValue("NOT_SPECIFIED") @QueryParam("sessionid") String sessionId, 
			@DefaultValue( "-1" ) @QueryParam("toolid") long toolId,
			@DefaultValue( "UNKNOWN" ) @QueryParam("status") ItemStatus newStatus 
		) {

		if ( logger.isDebugEnabled()) {
			logger.debug( "Start UpdateStatusResource.get()..." );
			// Show received parameters
			logger.debug( "  Request parameters: " 
					+ "sessionid='" + sessionId + "' "
					+ "toolid='" + toolId + "', "
					+ "new status='" + newStatus + "' "
			);
			
		}

		Account account = findAccount( sessionId );

		if ( account == null ) {
			if ( logger.isDebugEnabled()) {
				logger.debug( "  FAILED because account not found");
				logger.debug( "... end UpdateStatusResource.get()");
			}
			
			throw new WebApplicationException( Response.Status.NOT_FOUND );
		}
		if ( logger.isDebugEnabled()) logger.debug( "  Account found" );
		
		/*
		 * Necessary to:
		 *   find ToolItem
		 *   Set correct status and store
		 */
		
		if ( toolId <= 0 ) {

			logger.error( "Wrong parameters specified in UpdateStatusResource.get( toolId): " 
								+ toolId
			);

			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}
		
		// Determine Tool
		ToolItem item = DataFacade.getInstance().find( ToolItem.class, toolId );
		
		if ( item == null ) {
			logger.error( "Specified ToolItem with Id=" + toolId + " not found!" );
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}
		
		ToolItem updatedItem = ItemsFacade.getInstance().updateStatus( item, newStatus );
		
		if ( updatedItem != null ) {
			if ( logger.isDebugEnabled()) logger.debug( "Specified Tool Item with Id=" + item.getId() + " has been updated."
														+ " New status: " + newStatus );
		} else {
			throw new WebApplicationException( Response.Status.INTERNAL_SERVER_ERROR );
		}
		
		return Response.ok().build();
	
	}
	
}
