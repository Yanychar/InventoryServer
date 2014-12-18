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
import com.c2point.tools.entity.authentication.Account;
import com.c2point.tools.entity.msg.Message;
import com.c2point.tools.entity.msg.MessageType;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ToolItem;

@Path("/rejection")
public class RejectionResource extends BaseResource {
	
	private static Logger logger = LogManager.getLogger( RejectionResource.class.getName());
	
	@GET
	public Response get(
			@DefaultValue("NOT_SPECIFIED") @QueryParam("sessionid") String sessionId, 
			@DefaultValue( "-1" ) @QueryParam("toolid") long itemId,
			@DefaultValue( "-1" ) @QueryParam("userid") long newUserId 
		) {

		if ( logger.isDebugEnabled()) {
			logger.debug( "Start RejectionResource.get()..." );
			// Show received parameters
			logger.debug( "  Request parameters: " 
					+ "sessionId='" + sessionId + "', " 
					+ "Tool Id='" + itemId + "', "
					+ "New User Id='" + newUserId + "' "
			);
			
		}

		if ( itemId <= 0 || newUserId <= 0 ) {

			logger.error( "Wrong parameters specified in the RejectionResource.get(itemId, userId): " 
								+ itemId + ", " + newUserId
			);

			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}
		
		Account account = findAccount( sessionId );

		if ( account == null ) {
			if ( logger.isDebugEnabled()) {
				logger.debug( "  FAILED because account not found");
				logger.debug( "... end RejectionResource.get()");
			}
			
			throw new WebApplicationException( Response.Status.NOT_FOUND );
		}
		if ( logger.isDebugEnabled()) logger.debug( "  Account found" );
		
		/*
		 * Necessary to:
		 *   find Tool Item
		 *   Find User
		 *   Create AGREEMENT Message and store it
		 */
		
		// find Tool Item
		ToolItem item = DataFacade.getInstance().find( ToolItem.class,  itemId );
		
		if ( item == null ) {
			logger.error( "Specified ToolItem with Id=" + itemId + " not found!" );
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}

		// Find User
		OrgUser rejectedUser = DataFacade.getInstance().find( OrgUser.class,  newUserId );
		
		if ( rejectedUser == null ) {
			logger.error( "Specified User with Id=" + newUserId + " not found!" );
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}

		//   Create Rejection Message and store it
		Message msg = new Message( 
				MessageType.REJECTION, 
				account.getUser(), 
				rejectedUser,
				item
		);
		
		if ( MsgFacade.getInstance().addMessage( msg ) != null ) {
			if ( logger.isDebugEnabled()) logger.debug( "REJECTION Message for user " + rejectedUser.getFirstAndLastNames() + " has been added" );
		} else {
			throw new WebApplicationException( Response.Status.INTERNAL_SERVER_ERROR );
		}

		return Response.ok().build();
	
	}
	
}
