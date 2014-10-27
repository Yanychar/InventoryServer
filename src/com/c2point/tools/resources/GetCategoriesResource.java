package com.c2point.tools.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.CategoriesFacade;
import com.c2point.tools.entity.authentication.Account;
import com.c2point.tools.entity.tool.Category;
import com.c2point.tools.resources.stubs.CategoriesStub;

@Path("/getcategories")
public class GetCategoriesResource extends BaseResource {
	private static Logger logger = LogManager.getLogger( GetCategoriesResource.class.getName());
	
	@GET
	@Produces( MediaType.APPLICATION_JSON )
	public CategoriesStub get(
			@DefaultValue("NOT_SPECIFIED") @QueryParam("sessionid") String sessionId, 
			@DefaultValue("true") @QueryParam("all") boolean showEvenEmpty 
		) {

		if ( logger.isDebugEnabled()) {
			logger.debug( "Start GetCategoriesResource.getJSON()..." );
			// Show received parameters
			logger.debug( "  Request parameters: " 
							+ "sessionId='" + sessionId + "', "
							+ "showEvenEmpty categories='" + showEvenEmpty + "' "
			);
			
		}

		Account account = findAccount( sessionId );

		if ( account == null ) {
			if ( logger.isDebugEnabled()) {
				logger.debug( "  FAILED because account not found");
				logger.debug( "... end GetCategoriesResource.getJSON()");
			}
			throw new WebApplicationException( Response.Status.NOT_FOUND );
		}
		
		// Fetch categories
		List<Category> list = CategoriesFacade.getInstance().listTop();

		if ( logger.isDebugEnabled()) {
			for ( Category member : list ) {
				logger.debug( member.toString( true ));
			}
		}
		
		
		CategoriesStub stub;
		stub = new CategoriesStub( list );
		
		if ( logger.isDebugEnabled()) {
			logger.debug( "***** Response: succeeded!" );
			logger.debug( stub.toString( true ));
			logger.debug( "... end GetCategoriesResource.getJSON()");
		}
		
		return stub;
	
	}
	
}
