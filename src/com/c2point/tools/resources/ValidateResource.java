package com.c2point.tools.resources;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.c2point.tools.datalayer.DataFacade;
import com.c2point.tools.datalayer.MsgFacade;
import com.c2point.tools.datalayer.ToolsAndItemsFacade;
import com.c2point.tools.entity.authentication.Account;
import com.c2point.tools.entity.location.GeoLocation;
import com.c2point.tools.entity.location.LocationRecord;
import com.c2point.tools.entity.location.LocationStatus;
import com.c2point.tools.entity.msg.MessageType;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ItemStatus;
import com.c2point.tools.entity.repository.ToolItem;

@Path("/validated")
public class ValidateResource extends BaseResource {
	private static Logger logger = LogManager.getLogger( ValidateResource.class.getName());
	
	@GET
//	@Produces( MediaType.APPLICATION_JSON )
	public Response get(
			@DefaultValue("NOT_SPECIFIED") @QueryParam("sessionid") String sessionId, 
			@DefaultValue( "-1" ) @QueryParam("toolid") long toolId,
			@DefaultValue( "NOT_SPECIFIED" ) @QueryParam( "date" ) String dateStr,
			@DefaultValue( "-1000" ) @QueryParam( "latitude" ) Double latitude,
			@DefaultValue( "-1000" ) @QueryParam( "longitude" ) Double longitude,
			@DefaultValue( "-1000" ) @QueryParam( "accuracy" ) Double accuracy,
			@DefaultValue( "UNKNOWN" ) @QueryParam( "status" ) LocationStatus status
			
		) {

		if ( logger.isDebugEnabled()) {
			logger.debug( "Start ValidateResource.get()..." );
			// Show received parameters
			logger.debug( "  Request parameters: " 
					+ "sessionid='" + sessionId + "', "
					+ "toolid='" + toolId + "', "
					+ "date='" + dateStr + "', "
					+ "latitude='" + latitude + "', "
					+ "longitude='" + longitude + "', "
					+ "accuracy='" + accuracy + "', "
					+ "status='" + status + "' "
			);
			
		}

		Account account = findAccount( sessionId );

		if ( account == null ) {
			if ( logger.isDebugEnabled()) {
				logger.debug( "  FAILED because account not found");
				logger.debug( "... end ReleaseToolResource.get()");
			}
			
			throw new WebApplicationException( Response.Status.NOT_FOUND );
		}
		if ( logger.isDebugEnabled()) logger.debug( "  Account found" );
		
		/*
		 * Necessary to:
		 *   find ToolItem
		 *   Set correct status and store
		 */
		
		if ( toolId <= 0 ) {

			logger.error( "Wrong parameters specified in ReleaseToolResource.get( toolId): " 
								+ toolId
			);

			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}
		
		// Determine Tool
		ToolItem item = DataFacade.getInstance().find( ToolItem.class, toolId );
		
		if ( item == null ) {
			logger.error( "Specified ToolItem with Id=" + toolId + " not found!" );
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}

		// Create the time
		DateTime date;
		
		try {
			date = DateTimeFormat.forPattern( "ddMMyyyyHHmm" ).parseDateTime( dateStr );
		} catch( Exception e ) {
			logger.error( "Wrong format of specified date!" );
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}
		
		// Create Geo object
		GeoLocation location = new GeoLocation( latitude, longitude, accuracy );
		if ( !location.isValid()) {
			logger.error( "Invalid location has been specified: !" + location );
			throw new WebApplicationException( Response.Status.BAD_REQUEST );
		}
		
		// Update last location within toolitem
		ToolItem updatedItem = ToolsAndItemsFacade.getInstance().updateLocation( item, location );
		
		if ( updatedItem != null ) {
			if ( logger.isDebugEnabled()) logger.debug( "Specified Tool Item with Id=" + item.getId() + " has been updated" );
		} else {
			throw new WebApplicationException( Response.Status.INTERNAL_SERVER_ERROR );
		}

		// Add location to the location history
		LocationRecord locRecord = new LocationRecord( 	item, 
														date, account.getUser(), 
														location, status ); 
		DataFacade.getInstance().insert( locRecord );

		return Response.ok().build();
	
	}
	
}
