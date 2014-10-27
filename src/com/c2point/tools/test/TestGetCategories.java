
package com.c2point.tools.test;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

import com.c2point.tools.resources.AuthenticateResource;
import com.c2point.tools.resources.stubs.AuthenticationStub;

public class TestGetCategories {

	public static void main(String[] args) {

		TestGetCategories tests = new TestGetCategories();
		
		tests.test_1();

	}
	
	public void test_1() {
		
		AuthenticateResource authRes = new AuthenticateResource();
		
		AuthenticationStub authStub = authRes.authenticateJSON( "sev", "sev", "", "" );
		
		
	}

}
