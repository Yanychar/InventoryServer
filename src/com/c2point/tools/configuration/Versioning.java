package com.c2point.tools.configuration;

/*
 *   versions:
 * 
 *  1.0.0 - initial tracked version
 *  1.0.1 (27.11.2014) - Tools, Items, etc Mass Load was added
 *  1.0.2 (04.12.2014) - Tool Management implementation was added
 *  1.0.3 (04.12.2014) - Reporting with Filters (Category, user, status, manufacturer) was added
 *  1.0.4 (18.12.2014) - Bugs, Transactions backend, basic authorization (fake backend), model in filters
 *  1.0.5 (06.01.2015) - Client related Transaction Management. Personnel based view only
 *  1.0.6 (08.01.2015) - Client related Transaction Management
 *  1.1.0 (31.03.2015) - Major release
 *  Content of release:
 *	- One account per several Companies (and even per several users in one company)
 *	- Resource conversion tool was added
 *	- Organisation Management
 *	- Basic Access Rights Management
 *	- "Forgot password" implemented with emailing it to the user or Service Owner
 *	- New InventoryView with advanced filtering
 * 
 * 
 */

public class Versioning {

	private static int major_version = 1;
	private static int mid_version = 1;
	private static int minor_version = 0;
	
	public static String getVersion() {
		
		return major_version + "." + mid_version + "." + minor_version;
	}
	
}
