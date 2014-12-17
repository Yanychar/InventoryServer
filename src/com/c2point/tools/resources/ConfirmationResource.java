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

@Path("/confirm")
public class ConfirmationResource extends BaseResource {
	
	private static Logger logger = LogManager.getLogger( ConfirmationResource.class.getName());
	
	@GET
	public Response get(
			@DefaultValue("NOT_SPECIFIED") @QueryParam("sessionid") String sessionId, 
			@DefaultValue( "-1" ) @QueryParam("toolid") long itemId,
			@DefaultValue( "-1" ) @QueryParam("userid") long oldOwnerId 
		) {

		if ( logger.isDebugEnabled()) {
			logger.debug( "Start ConfirmationResource.get()..." );
			// Show received parameters
			logger.debug( "  Request parameters: " 
					+ "sessionId='" + sessionId + "', " 
					+ "Tool Id='" + itemId + "', "
					+ "New User Id='" + oldOwnerId + "' "
			);
			
		}

		if ( itemId <= 0 || oldOwnerId <= 0 ) {

			logger.error( "Wrong parameters specified in the ConfirmationResource.get(itemId, userId): " 
								+ itemId + ", " + oldOwnerId
			);

			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}
		
		Account account = findAccount( sessionId );

		if ( account == null ) {
			if ( logger.isDebugEnabled()) {
				logger.debug( "  FAILED because account not found");
				logger.debug( "... end AgreementResource.get()");
			}
			
			throw new WebApplicationException( Response.Status.NOT_FOUND );
		}
		if ( logger.isDebugEnabled()) logger.debug( "  Account found" );
		
		/*
		 * Necessary to:
		 *   find Tool Item
		 *   Find Old User
		 *   Remove reservation from Tool Item, set New User  and store it
		 *   Create CONFIRMATION Message and store it
		 */
		
		// find Tool Item
		ToolItem item = DataFacade.getInstance().find( ToolItem.class,  itemId );
		
		if ( item == null ) {
			logger.error( "Specified ToolItem with Id=" + itemId + " not found!" );
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}

		// Find New User
		OrgUser oldOwner = DataFacade.getInstance().find( OrgUser.class,  oldOwnerId );
		
		if ( oldOwner == null ) {
			logger.error( "Specified User with Id=" + oldOwnerId + " not found!" );
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}

		item.setReservedBy( null );
		item.setCurrentUser( account.getUser());

		if ( DataFacade.getInstance().merge( item ) != null ) {
			if ( logger.isDebugEnabled()) logger.debug( "Specified Tool Item with Id=" + item.getId() + " has been updated" );
		} else {
			throw new WebApplicationException( Response.Status.INTERNAL_SERVER_ERROR );
		}
		
		//   Create CONFIRMATION Message and store it
		Message msg = new Message( 
				MessageType.CONFIRMATION, 
				account.getUser(), 
				oldOwner,
				item
		);
		
		if ( MsgFacade.getInstance().addMessage( msg ) != null ) {
			if ( logger.isDebugEnabled()) logger.debug( "CONFIRMATION Message for user " + oldOwner.getFirstAndLastNames() + " has been added" );
		} else {
			throw new WebApplicationException( Response.Status.INTERNAL_SERVER_ERROR );
		}

		return Response.ok().build();
	
	}
	
}
