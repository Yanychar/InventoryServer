package com.c2point.tools.resources;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("test")
public class TestRestInterface extends BaseResource {

	private static Logger logger = LogManager.getLogger( TestRestInterface.class.getName());
	
  // This method is called if TEXT_PLAIN is request
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String sayPlainTextTest() {
	  logger.debug( "Inventory received REST requests ( TEXT_PLAIN )" );
	  return "Inventory is ready for REST requests (TEXT)";
  }

  // This method is called if TEXT_XML is request
  @GET
  @Produces(MediaType.TEXT_XML)
  public String sayXmlTest() {
	  logger.debug( "Inventory received REST requests ( TEXT_XML )" );
	  return "<?xml version=\"1.0\"?>" + "<test>" + " Inventory is ready for REST requests!(XML) " + "</test>";
  }

  // This method is called if HTML is request
  @GET
  @Produces(MediaType.TEXT_HTML)
  public String sayHtmlTest() {
	  
	  logger.debug( "Inventory received REST requests ( TEXT_HTML )" );
	  
    return "<html> " + "<title>" + "Inventory is ready for REST requests (HTML)" + "</title>"
        + "<body><h1>" + "Inventory is ready for REST requests (HTML)" + "</body></h1>" + "</html> ";
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String sayJsonTest() {
	  
	  logger.debug( "Inventory received REST requests ( APPLICATION_JSON )" );
	  
    return "Inventory is ready for REST requests (JSON)";
  }
  
}