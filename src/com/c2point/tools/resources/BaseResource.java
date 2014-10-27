package com.c2point.tools.resources;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.AuthenticationFacade;
import com.c2point.tools.entity.authentication.Account;

public class BaseResource {

	private static Logger logger = LogManager.getLogger( BaseResource.class.getName());

	@Context HttpServletRequest req;		

	protected Account findAccount( String sessionId ) throws WebApplicationException {
	
		if ( logger.isDebugEnabled()) logger.debug( " Find User Session for sessionId='" + sessionId + "'" );
		Account account = AuthenticationFacade.getInstance().findBySessionId( sessionId );
		
		// if not found return "NOT FOUND
		if ( account == null ) {
			if ( logger.isDebugEnabled()) logger.debug( " NOT FOUND sessionId='" + sessionId + "'" );
			// If not than resp = FAILED
			if ( logger.isDebugEnabled()) logger.debug( "...end 'GetReportsResource.getReports'. Response = UNAUTHORIZED" );
			throw new WebApplicationException( Response.Status.UNAUTHORIZED );
		}
		return account;
	}
	
	protected HttpServletRequest getRequest() { return req; }
	protected void setRequest( HttpServletRequest req ) { this.req = req; }
}
