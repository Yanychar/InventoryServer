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
import com.c2point.tools.entity.msg.MessageStatus;
import com.c2point.tools.entity.msg.MessageType;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ToolItem;

@Path("/msganswer")
public class MsgAnswerResource extends BaseResource {
	private static Logger logger = LogManager.getLogger( MsgAnswerResource.class.getName());

	@GET
	public Response get(
			@DefaultValue("NOT_SPECIFIED") @QueryParam("sessionid") String sessionId, 
			@DefaultValue( "-1" ) @QueryParam("msgid") long msgId,
			@DefaultValue( "-1" ) @QueryParam("answer") boolean isOk 
		) {

		if ( logger.isDebugEnabled()) {
			logger.debug( "Start MsgAnswerResource.get()..." );
			// Show received parameters
			logger.debug( "  Request parameters: " 
					+ "sessionId='" + sessionId + "', " 
					+ "Message Id='" + msgId + "', "
					+ "Is OK selected? '" + isOk + "' "
			);
			
		}
		
		if ( msgId <= 0  ) {

			logger.error( "Wrong parameters specified in MsgAnswerResource.get( msgId ): " + msgId );
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}

		Session session = findSession( sessionId );

		if ( session == null ) {
			if ( logger.isDebugEnabled()) {
				logger.debug( "  FAILED because session not found");
				logger.debug( "... end MsgAnswerResource.get()");
			}
			
			throw new WebApplicationException( Response.Status.UNAUTHORIZED );
		}
		if ( logger.isDebugEnabled()) logger.debug( "  Session was found" );
		
		/*
		 * Necessary to:
		 *   find message
		 *   process the message based on its type and status 
		 *   set it as READ
		 *   create response message if necessary and store
		 *   set received message as RESPONDED
		 */
		
		// Find the message
		Message msg = DataFacade.getInstance().find( Message.class,  msgId );
		
		if ( msg == null ) {
			logger.error( "Specified Message with Id=" + msgId + " not found!" );
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}
		
		// Determina whats to do based on message type
		switch ( msg.getType()) {
	    	case AGREEMENT:
				if ( isOk ) {
					takeOver( msg );
				} else {
					noNeecessary( msg );
				}
	    		break;
			case REQUEST:
				if ( isOk ) {
					agreedToBorrow( msg );
				} else {
					rejectToBorrow( msg );
				}
				break;
			case REJECTION:
			case CONFIRMATION:
			case NOTNEEDED:
			case INFO:
			case TEXT:
			default:
				read( msg );
				break;
		}

		
		
		if ( logger.isDebugEnabled()) 
			logger.debug( " ... end MsgAnswerResource.get()" );
		return Response.ok().build();
	}	

	
	private void agreedToBorrow( Message msg ) {
		
	// reserve the tool for the user

		// find Tool Item
		try {
			long itemId = msg.getItem().getId();
			ToolItem item = DataFacade.getInstance().find( ToolItem.class,  itemId );
			
			if ( item == null ) {
				logger.error( "Specified ToolItem with Id=" + itemId + " not found!" );
				throw new WebApplicationException( Response.Status.BAD_REQUEST );
			}
	
			// Find New User
			long newUserId = msg.getFrom().getId();
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
		} catch ( Exception e ) {
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}
		
	// Create and store agreement message to new user
		Message newMsg = msg.createReply( MessageType.AGREEMENT );
		newMsg = MsgFacade.getInstance().addMessage( newMsg );
		
	// update request message as RESPONDED
		msg = MsgFacade.getInstance().updateMessage( msg, MessageStatus.RESPONDED );
		
		
	}
		
	private void rejectToBorrow( Message msg ) {

	// Create and store rejection message to new user
		Message newMsg = msg.createReply( MessageType.REJECTION );
		newMsg = MsgFacade.getInstance().addMessage( newMsg );

	// update request message as RESPONDED
		msg = MsgFacade.getInstance().updateMessage( msg, MessageStatus.RESPONDED );

	}
		
	private void takeOver( Message msg ) {

	// Transfer ToolItem ownership/usage
		// find Tool Item
		try {
			long itemId = msg.getItem().getId();
			ToolItem item = DataFacade.getInstance().find( ToolItem.class,  itemId );
			
			if ( item == null ) {
				logger.error( "Specified ToolItem with Id=" + itemId + " not found!" );
				throw new WebApplicationException( Response.Status.BAD_REQUEST );
			}
	
			// Find New User
			long newUserId = msg.getTo().getId();
			OrgUser newUser = DataFacade.getInstance().find( OrgUser.class,  newUserId );
			
			if ( newUser == null ) {
				logger.error( "Specified User with Id=" + newUserId + " not found!" );
				throw new WebApplicationException( Response.Status.BAD_REQUEST );
			}
	
			//  Remove Reservation and set up new Owner
			item.setReservedBy( null );
			item.setCurrentUser( newUser );
	
			if ( DataFacade.getInstance().merge( item ) != null ) {
				if ( logger.isDebugEnabled()) logger.debug( "Specified Tool Item with Id=" + item.getId() + " has been updated" );
			} else {
				throw new WebApplicationException( Response.Status.INTERNAL_SERVER_ERROR );
			}
		} catch ( Exception e ) {
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}
		
	// Create and store Confirmation message to previous owner
		Message newMsg = msg.createReply( MessageType.CONFIRMATION );
		newMsg = MsgFacade.getInstance().addMessage( newMsg );
		
	// update agreement message as RESPONDED
		msg = MsgFacade.getInstance().updateMessage( msg, MessageStatus.RESPONDED );

	}
	
	private void noNeecessary( Message msg ) {

	// Free ToolItem if reserved to this guy
		// find Tool Item
		try {
			long itemId = msg.getItem().getId();
			ToolItem item = DataFacade.getInstance().find( ToolItem.class,  itemId );
			
			if ( item == null ) {
				logger.error( "Specified ToolItem with Id=" + itemId + " not found!" );
				throw new WebApplicationException( Response.Status.BAD_REQUEST );
			}
	
			// Find New User
			long newUserId = msg.getTo().getId();
			OrgUser newUser = DataFacade.getInstance().find( OrgUser.class,  newUserId );
			
			if ( newUser == null ) {
				logger.error( "Specified User with Id=" + newUserId + " not found!" );
				throw new WebApplicationException( Response.Status.BAD_REQUEST );
			}
	
			//  Remove reservation
			if ( item.getReservedBy().getId() == newUserId ) {
				item.setReservedBy( null );
		
				if ( DataFacade.getInstance().merge( item ) != null ) {
					if ( logger.isDebugEnabled()) logger.debug( "Specified Tool Item with Id=" + item.getId() + " has been updated" );
				} else {
					throw new WebApplicationException( Response.Status.INTERNAL_SERVER_ERROR );
				}
			}
			
		} catch ( Exception e ) {
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}
	// Create and store NotNeeded message to the owner
		Message newMsg = msg.createReply( MessageType.NOTNEEDED );
		newMsg = MsgFacade.getInstance().addMessage( newMsg );
		
	// update agreement message as RESPONDED
		msg = MsgFacade.getInstance().updateMessage( msg, MessageStatus.RESPONDED );
		
	}
	
	private void read( Message msg ) {

	// update agreement message as READ
		msg = MsgFacade.getInstance().updateMessage( msg, MessageStatus.READ );
		
	}
	
	
	
}
