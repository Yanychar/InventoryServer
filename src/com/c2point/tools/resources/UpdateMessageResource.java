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
import com.c2point.tools.entity.authentication.Session;
import com.c2point.tools.entity.msg.Message;
import com.c2point.tools.entity.msg.MessageStatus;

@Path("/updatemessage")
public class UpdateMessageResource extends BaseResource {
	private static Logger logger = LogManager.getLogger( UpdateMessageResource.class.getName());
	
	@GET
//	@Produces( MediaType.APPLICATION_JSON )
	public Response get(
			@DefaultValue("NOT_SPECIFIED") @QueryParam("sessionid") String sessionId, 
			@DefaultValue( "-1" ) @QueryParam("msgid") long msgId, 
			@DefaultValue( "UNKNOWN" ) @QueryParam("status") MessageStatus newStatus 
		) {

		if ( logger.isDebugEnabled()) {
			logger.debug( "Start UpdateMessageResource.get()..." );
			// Show received parameters
			logger.debug( "  Request parameters: " 
					+ "sessionId='" + sessionId + "', " 
					+ "msgId='" + msgId + "', "
					+ "new status='" + newStatus + "' "
			);
			
		}

		if ( msgId <= 0 || newStatus == MessageStatus.UNKNOWN ) {

			logger.error( "Wrong parameters specified in UpdateMessageResource.get( msgId ): " 
								+ msgId
			);

			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}
		
		Session session = findSession( sessionId );

		if ( session == null ) {
			if ( logger.isDebugEnabled()) {
				logger.debug( "  FAILED because session not found");
				logger.debug( "... end UpdateMessageResource.get()");
			}
			
			throw new WebApplicationException( Response.Status.UNAUTHORIZED );
		}
		if ( logger.isDebugEnabled()) logger.debug( "  Session was found" );
		
		/*
		 * Necessary to:
		 *   find message
		 *   update Message
		 *   store Message
		 *   
		 *   after above create and store Message
		 */
		
		
		// find message
		Message msg = DataFacade.getInstance().find( Message.class,  msgId );
		
		if ( msg == null ) {
			logger.error( "Specified Message with Id=" + msgId + " not found!" );
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}

		 //	Update Message
		msg.setStatus( newStatus );
		
		// Save message
		if ( DataFacade.getInstance().merge( msg ) != null ) {
			if ( logger.isDebugEnabled()) logger.debug( "Specified Message with Id=" + msgId + " has been updated" );
		} else {
			throw new WebApplicationException( Response.Status.INTERNAL_SERVER_ERROR );
		}

		return Response.ok().build();
	
	}
	
}
