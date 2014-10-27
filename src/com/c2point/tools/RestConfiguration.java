package com.c2point.tools;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * @author sevastia
 *
 */
@ApplicationPath("rest")
public class RestConfiguration extends ResourceConfig {

	public RestConfiguration() {
		packages( "com.c2point.tools.resources" );
		register( JacksonFeature.class );
		
	}

}
