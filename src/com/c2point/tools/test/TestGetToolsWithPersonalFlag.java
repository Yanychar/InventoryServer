
package com.c2point.tools.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.c2point.tools.resources.AuthenticateResource;
import com.c2point.tools.resources.GetToolsResource;
import com.c2point.tools.resources.stubs.AuthUserStub;
import com.c2point.tools.resources.stubs.ToolItemsListStub;

public class TestGetToolsWithPersonalFlag {
	private static Logger logger = LogManager.getLogger( TestGetToolsWithPersonalFlag.class.getName());

	public static void main(String[] args) {

		TestGetToolsWithPersonalFlag tests = new TestGetToolsWithPersonalFlag();
		
		tests.test_1();

	}
	
	public void test_1() {
		
		AuthenticateResource authRes = new AuthenticateResource();
		
		AuthUserStub authStub = authRes.authenticateJSON( "sev", "sev", "", "" );
		
		GetToolsResource getToolsRes = new GetToolsResource();
		
		ToolItemsListStub listStub;
/*		
		listStub = getToolsRes.get( authStub.getSessionId(), 3231, -1, -1, "", null, true );
		logger.debug( "Number of Public only Records found:" + ( listStub != null ? listStub.size() : "-1" ));
		
		listStub = getToolsRes.get( authStub.getSessionId(), 3231, -1, -1, "", null, false );
		logger.debug( "Number of All Records found:" + ( listStub != null ? listStub.size() : "-1" ));
*/		
		listStub = getToolsRes.get( authStub.getSessionId(), -1, -1, -1, "", "lineet", false );
		logger.debug( "Number of Public only Records found:" + ( listStub != null ? listStub.size() : "-1" ));
		
	}

}
