package com.c2point.tools;

/*
 *   versions:
 * 
 *  1.0.0 - initial tracked version
 *  1.0.1 (27.11.2014) - Tools, Items, etc Mass Load was added
 *  1.0.2 (04.12.2014) - Tool Management implementation was added
 *  1.0.3 (04.12.2014) - Reporting with Filters (Category, user, status, manufacturer) was added
 *  1.0.4 (18.12.2014) - Bugs, Transactions backend, basic authorization (fake backend), model in filters
 *  1.0.5 (06.01.2015) - Client relatedTransaction Management. personnel based only
 * 
 * 
 */

public class Configuration {

	private static int major_version = 1;
	private static int mid_version = 0;
	private static int minor_version = 5;
	
	
	public static String getVersion() {
		
		return major_version + "." + mid_version + "." + minor_version;
	}
}
