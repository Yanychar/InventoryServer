package com.c2point.tools.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

public class PasswordGenerator {

	private static final int 		PSW_LENGTH = 8;
	private static final char [] 	CHAR_TO_DELETE = { '0', 'o', 'O', 'l', 'I', '1' };
	
	public static String getNewPassword() {

		String password = "";
		
		boolean generate = true;
		
		while( generate ) {
			password = RandomStringUtils.randomAlphanumeric( PSW_LENGTH * 2 );
			
			// what shall be removed: 0oOlI1
			if ( StringUtils.containsAny( password, CHAR_TO_DELETE )) {
				// Delete chars
				for ( char c : CHAR_TO_DELETE ) {
					password = StringUtils.remove( password, c );
				}
			}
			
			// Check that length is required. Cut or select next passord
			if ( password.length() >= PSW_LENGTH ) {
				password = StringUtils.left( password, PSW_LENGTH );
				
				generate = false;
			}

		}
		
		return password;
	}

	
	
}
