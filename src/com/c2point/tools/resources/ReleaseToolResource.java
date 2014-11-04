package com.c2point.tools.resources;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.DataFacade;
import com.c2point.tools.datalayer.MsgFacade;
import com.c2point.tools.datalayer.ToolsAndItemsFacade;
import com.c2point.tools.entity.authentication.Account;
import com.c2point.tools.entity.msg.MessageType;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ItemStatus;
import com.c2point.tools.entity.repository.ToolItem;

@Path("/release")
public class ReleaseToolResource extends BaseResource {
	private static Logger logger = LogManager.getLogger( ReleaseToolResource.class.getName());
	
	@GET
//	@Produces( MediaType.APPLICATION_JSON )
	public Response get(
			@DefaultValue("NOT_SPECIFIED") @QueryParam("sessionid") String sessionId, 
			@DefaultValue( "-1" ) @QueryParam("toolid") long toolId 
		) {

		if ( logger.isDebugEnabled()) {
			logger.debug( "Start ReleaseToolResource.get()..." );
			// Show received parameters
			logger.debug( "  Request parameters: " 
					+ "sessionid='" + sessionId + "' "
					+ "toolid='" + toolId + "' "
			);
			
		}

		Account account = findAccount( sessionId );

		if ( account == null ) {
			if ( logger.isDebugEnabled()) {
				logger.debug( "  FAILED because account not found");
				logger.debug( "... end ReleaseToolResource.get()");
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

			logger.error( "Wrong parameters specified in ReleaseToolResource.get( toolId): " 
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
		
		// Set new user and change status
		OrgUser oldUser = item.getCurrentUser();
		
		ToolItem updatedItem = ToolsAndItemsFacade.getInstance().updateStatus( item, ItemStatus.FREE );
		
		if ( updatedItem != null ) {
			if ( logger.isDebugEnabled()) logger.debug( "Specified Tool Item with Id=" + item.getId() + " has been updated" );
		} else {
			throw new WebApplicationException( Response.Status.INTERNAL_SERVER_ERROR );
		}
		
		return Response.ok().build();
	
	}
	
}
