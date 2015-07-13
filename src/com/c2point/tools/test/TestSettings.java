package com.c2point.tools.test;

import com.c2point.tools.datalayer.SettingsFacade;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.settings.AllProperties;

public class TestSettings {

	public static void main(String[] args) {

		TestGetToolsWithPersonalFlag tests = new TestGetToolsWithPersonalFlag();
		
		tests.test_1();

	}
	
	public void test_1() {

		SettingsFacade sf = SettingsFacade.getInstance();
		
		Organisation org_1 = new Organisation( "0001", "Org Number 1" ); 
		Organisation org_2 = new Organisation( "0002", "Org Number 1" );
		
		sf.set( org_1, "param_str", "value_str" );
		sf.set( org_1, "param_bool", true );
		sf.set( org_1, "param_int", 1 );
		sf.set( org_1, "param_long", 2L );
		
		AllProperties.getProperties( org_1 ).toString();
		
		
	}

}
