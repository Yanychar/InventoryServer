package com.c2point.tools;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.c2point.tools.resources.CORSResponseFilter;
//import com.fasterxml.jackson.jaxrs.annotation.JacksonFeatures;

/**
 * @author sevastia
 *
 */
@ApplicationPath("rest")
public class RestConfiguration extends ResourceConfig {

	public RestConfiguration() {
		packages( "com.c2point.tools.resources" );
		register( JacksonFeature.class );
		
		register( CORSResponseFilter.class );
		
	}

}
