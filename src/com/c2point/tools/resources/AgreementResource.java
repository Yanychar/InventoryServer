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
import com.c2point.tools.entity.msg.Message;
import com.c2point.tools.entity.msg.MessageType;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ToolItem;

@Path("/agreement")
public class AgreementResource extends BaseResource {
	
	private static Logger logger = LogManager.getLogger( AgreementResource.class.getName());
	
	@GET
	public Response get(
			@DefaultValue("NOT_SPECIFIED") @QueryParam("sessionid") String sessionId, 
			@DefaultValue( "-1" ) @QueryParam("toolid") long itemId,
			@DefaultValue( "-1" ) @QueryParam("userid") long newUserId 
		) {

		if ( logger.isDebugEnabled()) {
			logger.debug( "Start AgreementResource.get()..." );
			// Show received parameters
			logger.debug( "  Request parameters: " 
					+ "sessionId='" + sessionId + "', " 
					+ "Tool Id='" + itemId + "', "
					+ "New User Id='" + newUserId + "' "
			);
			
		}

		if ( itemId <= 0 || newUserId <= 0 ) {

			logger.error( "Wrong parameters specified in the AgreementResource.get(itemId, userId): " 
								+ itemId + ", " + newUserId
			);

			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}

		Session session = findSession( sessionId );

		if ( session == null ) {
			if ( logger.isDebugEnabled()) {
				logger.debug( "  FAILED because session not found");
				logger.debug( "... end AgreementResource.get()");
			}
			
			throw new WebApplicationException( Response.Status.UNAUTHORIZED );
		}
		if ( logger.isDebugEnabled()) logger.debug( "  Session was found" );
		
		/*
		 * Necessary to:
		 *   find Tool Item
		 *   Find New User
		 *   Reserve Tool Item for New User and store it
		 *   Create AGREEMENT Message and store it
		 */
		
		// find Tool Item
		ToolItem item = DataFacade.getInstance().find( ToolItem.class,  itemId );
		
		if ( item == null ) {
			logger.error( "Specified ToolItem with Id=" + itemId + " not found!" );
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}

		// Find New User
		OrgUser newUser = DataFacade.getInstance().find( OrgUser.class,  newUserId );
		
		if ( newUser == null ) {
			logger.error( "Specified User with Id=" + newUserId + " not found!" );
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}

		//   Mark ToolItem as reserverd by New User and store Item
		item.setReservedBy( newUser );

		if ( DataFacade.getInstance().merge( item ) != null ) {
			if ( logger.isDebugEnabled()) logger.debug( "Specified Tool Item with Id=" + item.getId() + " has been updated" );
		} else {
			throw new WebApplicationException( Response.Status.INTERNAL_SERVER_ERROR );
		}
		
		//   Create AGREEMENT Message and store it
		Message msg = new Message( 
				MessageType.AGREEMENT, 
				session.getUser(), 
				newUser,
				item
		);
		
		if ( MsgFacade.getInstance().addMessage( msg ) != null ) {
			if ( logger.isDebugEnabled()) logger.debug( "AGREEMENT Message for user " + newUser.getFirstAndLastNames() + " has been added" );
		} else {
			throw new WebApplicationException( Response.Status.INTERNAL_SERVER_ERROR );
		}

		return Response.ok().build();
	
	}
	
}
