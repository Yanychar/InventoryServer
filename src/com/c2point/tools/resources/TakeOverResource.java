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
import com.c2point.tools.datalayer.MsgFacade;
import com.c2point.tools.datalayer.ItemsFacade;
import com.c2point.tools.entity.authentication.Session;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ToolItem;

@Path("/takeover")
public class TakeOverResource extends BaseResource {
	private static Logger logger = LogManager.getLogger( TakeOverResource.class.getName());
	
	@GET
//	@Produces( MediaType.APPLICATION_JSON )
	public Response get(
			@DefaultValue("NOT_SPECIFIED") @QueryParam("sessionid") String sessionId, 
			@DefaultValue( "-1" ) @QueryParam("toolid") long toolId 
		) {

		if ( logger.isDebugEnabled()) {
			logger.debug( "Start TakeOverResource.get()..." );
			// Show received parameters
			logger.debug( "  Request parameters: " 
					+ "sessionid='" + sessionId + "' "
					+ "toolid='" + toolId + "' "
			);
			
		}

		Session session = findSession( sessionId );

		if ( session == null ) {
			if ( logger.isDebugEnabled()) {
				logger.debug( "  FAILED because session not found");
				logger.debug( "... end TakeOverResource.get()");
			}
			
			throw new WebApplicationException( Response.Status.UNAUTHORIZED );
		}
		if ( logger.isDebugEnabled()) logger.debug( "  Session was found" );
		
		/*
		 * Necessary to:
		 *   find ToolItem
		 *   Set new user and change status
		 *   after above create and store Message
		 */
		
		if ( toolId <= 0 ) {

			logger.error( "Wrong parameters specified in TakeOverResource.get( toolId): " 
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
		
		ToolItem updatedItem = ItemsFacade.getInstance().updateUser( session.getUser(), item, session.getUser());
		
		if ( updatedItem != null ) {
			if ( logger.isDebugEnabled()) logger.debug( "Specified Tool Item with Id=" + item.getId() + " has been updated" );
		} else {
			throw new WebApplicationException( Response.Status.INTERNAL_SERVER_ERROR );
		}
		
		// Save Info message
		
		if ( oldUser != null )
			if ( MsgFacade.getInstance().addToolBorrowedInfo( session.getUser(), oldUser, updatedItem )) {
				if ( logger.isDebugEnabled()) logger.debug( "ToolItem " + item + " was borrowed" );
			} else {
				throw new WebApplicationException( Response.Status.INTERNAL_SERVER_ERROR );
			}

		return Response.ok().build();
	
	}
	
}
