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
import com.c2point.tools.entity.authentication.Session;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ToolItem;

@Path("/reqborrow")
public class BorrowRequestResource extends BaseResource {
	private static Logger logger = LogManager.getLogger( BorrowRequestResource.class.getName());
	
	@GET
//	@Produces( MediaType.APPLICATION_JSON )
	public Response get(
			@DefaultValue("NOT_SPECIFIED") @QueryParam("sessionid") String sessionId, 
			@DefaultValue( "-1" ) @QueryParam("toolid") long toolId 
		) {

		if ( logger.isDebugEnabled()) {
			logger.debug( "Start BorrowRequestResource.get()..." );
			// Show received parameters
			logger.debug( "  Request parameters: " 
							+ "sessionId='" + sessionId + "' "
			);
			
		}

		Session session = findSession( sessionId );

		if ( session == null ) {
			if ( logger.isDebugEnabled()) {
				logger.debug( "  FAILED because session not found");
				logger.debug( "... end BorrowRequestResource.get()");
			}
			
			throw new WebApplicationException( Response.Status.UNAUTHORIZED );
		}
		if ( logger.isDebugEnabled()) logger.debug( "  Session was found" );

		/*
		 * Necessary to:
		 *   find ToolItem
		 *   
		 *   after above create and store Message
		 */
		
		if ( toolId <= 0 ) {

			logger.error( "Wrong parameters specified in BorrowRequestResource.get( toolId): " 
								+ toolId
			);

			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}
		
		// Determine sender
		OrgUser sender = session.getUser();
		
		// Determine Tool
		ToolItem item = DataFacade.getInstance().find( ToolItem.class, toolId );
		
		if ( item == null ) {
			logger.error( "Specified ToolItem with Id=" + toolId + " not found!" );
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}
		
		
		// Save message
		
		if ( MsgFacade.getInstance().addToolRequest( item, sender ) ) {
			if ( logger.isDebugEnabled()) logger.debug( "ToolItem " + item + " was requested" );
		} else {
			throw new WebApplicationException( Response.Status.INTERNAL_SERVER_ERROR );
		}

		return Response.ok().build();
	
	}
	
}
