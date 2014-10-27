package com.c2point.tools.resources;

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

import com.c2point.tools.datalayer.MsgFacade;
import com.c2point.tools.entity.authentication.Account;
import com.c2point.tools.entity.msg.Message;
import com.c2point.tools.entity.msg.MessageStatus;
import com.c2point.tools.resources.stubs.MsgListStub;

@Path("/getmessages")
public class GetMessagesResource extends BaseResource {
	private static Logger logger = LogManager.getLogger( GetMessagesResource.class.getName());
	
	@GET
	@Produces( MediaType.APPLICATION_JSON )
	public MsgListStub get(
			@DefaultValue("NOT_SPECIFIED") @QueryParam("sessionid") String sessionId, 
			@DefaultValue( "-1" ) @QueryParam("categoryid") long categoryId, 
			@DefaultValue( "UNKNOWN" ) @QueryParam("status") MessageStatus status 
		) {

		if ( logger.isDebugEnabled()) {
			logger.debug( "Start GetMessagesResource.getJSON()..." );
			// Show received parameters
			logger.debug( "  Request parameters: " 
							+ "sessionId='" + sessionId + "', "
							+ "msg status='" + status + "' "
			);
			
		}

		Account account = findAccount( sessionId );

		if ( account == null ) {
			if ( logger.isDebugEnabled()) {
				logger.debug( "  FAILED because account not found");
				logger.debug( "... end GetMessagesResource.getJSON()");
			}
			throw new WebApplicationException( Response.Status.NOT_FOUND );
		}
		if ( logger.isDebugEnabled()) logger.debug( "  Account found" );
		
		Collection<Message> msgList = null;
		
		if ( status == MessageStatus.UNREAD ) {
			if ( logger.isDebugEnabled()) logger.debug( "  UNREAD messages shall be read!" );
			
			msgList = MsgFacade.getInstance().list( account.getUser(), MessageStatus.UNREAD );
			
		} else {
			// In other cases Read ALL
			msgList = MsgFacade.getInstance().list( account.getUser() );
			
		} 
		
		if ( logger.isDebugEnabled()) {
			for ( Message member : msgList ) {
				logger.debug( member.toString());
			}
		}
		
		MsgListStub stub;
		stub = new MsgListStub( msgList );
		
		if ( logger.isDebugEnabled()) {
			logger.debug( "***** Response: succeeded!" );
			logger.debug( stub.toString());
			logger.debug( "... end GetMessagesResource.getJSON()");
		}
		
		return stub;
	
	}
	
}
