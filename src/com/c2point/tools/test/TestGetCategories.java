
package com.c2point.tools.test;

import com.c2point.tools.resources.AuthenticateResource;

public class TestGetCategories {

	public static void main(String[] args) {

		TestGetCategories tests = new TestGetCategories();
		
		tests.test_1();

	}
	
	public void test_1() {
		
		AuthenticateResource authRes = new AuthenticateResource();
		
//		AuthenticationStub authStub = authRes.authenticateJSON( "sev", "sev", "", "" );
		
		authRes.authenticateJSON( "sev", "sev", "", "" );
		
		
	}

}
