
package com.c2point.tools.test;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.AuthenticationFacade;
import com.c2point.tools.datalayer.DataFacade;
import com.c2point.tools.datalayer.ItemsFacade;
import com.c2point.tools.datalayer.ToolsFacade;
import com.c2point.tools.entity.authentication.Account;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.tool.Manufacturer;
import com.c2point.tools.entity.tool.Tool;

public class TestCascadePersistance {
	private static Logger logger = LogManager.getLogger( TestCascadePersistance.class.getName());

	public static void main(String[] args) {

		TestCascadePersistance tests = new TestCascadePersistance();
		
//		tests.test_1();
//		tests.test_2();
		tests.test_3();

	}
	
	@SuppressWarnings("unused")
	/*
	 * Test adding tools with existing manufacturer
	 */
	public void test_1() {
		logger.debug( "***** Test 1 starts! ****" );
		
		String testName = "sergesee";
		String testPwd = "sergesee";
		/*
		 * 1. Login as Test company user
		 * 2. Read existing Manufact
		 * 3. Create New Tool
		 * 4. Store New Tool
		 * 5. Check.
		 * 6. Clearance: delete created NewTool
		 */
		
		// 1.
		Account account = AuthenticationFacade.getInstance().authenticateUser( testName, testPwd );
		OrgUser user = account.getUser();
		logger.info( "Account found. User: " + user );
		
		// 2. 
		Collection<Manufacturer> manLst = ItemsFacade.getInstance().getManufacturers();
		Manufacturer man = null;
		boolean foundFlag = false;
		for ( Manufacturer manTmp : manLst ) {
			
			if ( manTmp.getName() != null && manTmp.getName().compareToIgnoreCase( "makita" ) == 0 ) {
				man = manTmp;
				logger.debug( "Manufacturer '" + man.getName() + "' found!" );
				foundFlag = true;
				break;
			}
		}
		
		if ( !foundFlag ) {
			logger.error( "Manufacturer was NOT  found!" );
			return;
		}
		
		// 3.
		Tool newTool = new Tool( user.getOrganisation());
		
		newTool.setName( "NewTool NAME" );
		newTool.setManufacturer( man );
		newTool.setModel( "NewTool MODEL" );
		
		// 4.
		newTool = ToolsFacade.getInstance().add( newTool );
		
		// 5.
		AuthenticationFacade.getInstance().logout( user, false );
		
		// Find tool
		Tool sTool = DataFacade.getInstance().find( Tool.class, newTool.getId());
		if ( sTool == null ) {
			logger.error( "Tool was NOT  found!" );
		} else {
			logger.debug( "Tool IS OK!" );
			logger.debug( "Tool: " + sTool );
		}
		
		
		//6.
		try {
			DataFacade.getInstance().remove( newTool );
		} catch( Exception e ) {
			logger.error( "Cannot delete created Tool!" );
		}
		
		
	}

	@SuppressWarnings("unused")
	/*
	 * Test adding tools with NEW manufacturer
	 */
	public void test_2() {
		logger.debug( "***** Test 2 starts! ****" );
		
		String testName = "sergesee";
		String testPwd = "sergesee";
		/*
		 * 1. Login as Test company user
		 * 2. Create Manufacturer
		 * 3. Create New Tool
		 * 4. Store New Tool
		 * 5. Check.
		 * 6. Clearance: delete created NewTool
		 */
		
		// 1.
		Account account = AuthenticationFacade.getInstance().authenticateUser( testName, testPwd );
		OrgUser user = account.getUser();
		logger.info( "Account found. User: " + user );
		
		// 2. 
		Manufacturer man = new Manufacturer( "NEW Manufacturer" );
		
		// 3.
		Tool newTool = new Tool( user.getOrganisation());
		
		newTool.setName( "NewTool NAME" );
		newTool.setManufacturer( man );
		newTool.setModel( "NewTool MODEL" );
		
		// 4.
		newTool = ToolsFacade.getInstance().add( newTool );
		
		// 5.
		AuthenticationFacade.getInstance().logout( user, false );
		
		// Find tool
		Tool sTool = DataFacade.getInstance().find( Tool.class, newTool.getId());
		if ( sTool == null ) {
			logger.error( "Tool was NOT  found!" );
		} else {
			logger.debug( "Tool IS OK!" );
			logger.debug( "Tool: " + sTool );
		}
		
		
		//6.
		try {
			DataFacade.getInstance().remove( sTool );
			DataFacade.getInstance().remove( man );
		} catch( Exception e ) {
			logger.error( "Cannot delete created Tool!" );
		}
		
		
	}

	@SuppressWarnings("unused")
	/*
	 * Test store existing tool with modified manufacturer
	 */
	public void test_3() {
		logger.debug( "***** Test 3 starts! ****" );
		
		String testName = "sergesee";
		String testPwd = "sergesee";
		/*
		 */
		
		// 1.
		Account account = AuthenticationFacade.getInstance().authenticateUser( testName, testPwd );
		OrgUser user = account.getUser();
		logger.info( "Account found. User: " + user );
		
		// 2. 
		Manufacturer man = new Manufacturer( "NEW Manufacturer" );
		Tool newTool = new Tool( user.getOrganisation());
		newTool.setName( "NewTool NAME" );
		newTool.setManufacturer( man );
		newTool.setModel( "NewTool MODEL" );
		newTool = ToolsFacade.getInstance().add( newTool );
		AuthenticationFacade.getInstance().logout( user, false );

		
		// 4.
		Tool sTool = DataFacade.getInstance().find( Tool.class, newTool.getId());
		if ( sTool == null ) {
			logger.error( "Tool was NOT  found!" );
		} else {
			logger.debug( "Tool IS OK!" );
			logger.debug( "Tool: " + sTool );
		}

		man = sTool.getManufacturer();
		man.setName( man.getName() + "_EDITED");
		
		// 4.
		newTool = ToolsFacade.getInstance().update( sTool );
		
		// 5.
		
		// Find tool
		sTool = DataFacade.getInstance().find( Tool.class, newTool.getId());
		
		if ( sTool == null ) {
			logger.error( "Tool was NOT  found!" );
		} else {
			logger.debug( "Tool IS OK!" );
			logger.debug( "Tool: " + sTool );
		}
		
		
		//6.
		try {
			DataFacade.getInstance().remove( sTool );
			DataFacade.getInstance().remove( man );
		} catch( Exception e ) {
			logger.error( "Cannot delete created Tool!" );
		}
		
	}

}
