package com.c2point.tools.entity.settings;

import java.util.HashMap;
import java.util.Map;

import com.c2point.tools.entity.organisation.Organisation;

public class AllProperties {

//	private static AllProperties instance = null;
	
	private static Map<Long, OrgProperties> orgMap = null;
	
	public AllProperties() {

		orgMap = new HashMap<Long, OrgProperties>();

	}
/*	
	private static AllProperties getInstance() {
		
		if ( instance == null ) {
			
			instance = new AllProperties();
			
		}
		
		return instance;
	}
*/	

	public static OrgProperties getProperties( Organisation org ) {
		
		// Create Object if it was not created before. Similar to instance in sceleton pattern
		if ( orgMap == null )
			
			new AllProperties();
		
		
		OrgProperties orgProps = orgMap.get( org.getId());
		
		if ( orgProps == null ) {
			
			orgMap.put( org.getId(), new OrgProperties( org ));
		}
		
		
		return null;
	}
	
}