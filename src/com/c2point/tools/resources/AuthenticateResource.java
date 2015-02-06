package com.c2point.tools.resources;

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
import org.joda.time.DateTime;

import com.c2point.tools.datalayer.AuthenticationFacade;
import com.c2point.tools.datalayer.TransactionsFacade;
import com.c2point.tools.entity.authentication.Account;
import com.c2point.tools.resources.stubs.AuthenticationStub;

@Path("/authenticate")
public class AuthenticateResource extends BaseResource {
	private static Logger logger = LogManager.getLogger( AuthenticateResource.class.getName());
	
	@GET
	@Produces( MediaType.APPLICATION_JSON )
	public AuthenticationStub authenticateJSON(
			@DefaultValue("") @QueryParam("name") String usrname, 
			@DefaultValue("") @QueryParam("pwd") String pwd, 
			@DefaultValue("None") @QueryParam("hwc") String imei, 
			@DefaultValue("None") @QueryParam("av") String appVer
		) {

		if ( logger.isDebugEnabled()) {
			logger.debug( "Start AuthenticateResource.authenticateJSON()...");
			logger.debug( "  name/pwd: " + usrname + "/" + pwd );
			
		}

		Account account = AuthenticationFacade.getInstance().authenticateUser( usrname, pwd, appVer, imei );

		
		
		if ( account != null && account.valid()) {

			// TODO. Account can be connected to several User records in diferent organisations
			// Should be redesigned
			TransactionsFacade.getInstance().writeLogin( account.getUsers().iterator().next());
			
		} else {
			if ( logger.isDebugEnabled()) {
				logger.debug( "  FAILED because account not found");
				logger.debug( "... end AuthenticateResource.authenticate()");
			}
			throw new WebApplicationException( Response.Status.NOT_FOUND );
		}
		
		AuthenticationStub stub;
		stub = new AuthenticationStub( account, DateTime.now());
		if ( logger.isDebugEnabled()) logger.debug( "***** Response: authenticated!" );
		if ( logger.isDebugEnabled()) logger.debug( "... end AuthenticateResource.authenticateJSON()");
		
		return stub;

	}
}
