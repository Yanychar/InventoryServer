package com.c2point.tools.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

public class PasswordGenerator {

	public static final int			USER_NAME_LENGTH = 8;  // Will be 8 or 9
	public static final int 		PASSWORD_LENGTH = 8;

	private static final String 	USRNAME_PREFIX = ""; //"fi";
	private static final char [] 	CHAR_TO_DELETE_FROM_PASSWORD = { '0', 'o', 'O', 'l', 'I', '1' };
	
	public static String generateUserName( String firstPart, String secondPart) {
		
		String newName =  
				USRNAME_PREFIX
			+	StringUtils.defaultString( firstPart ).trim().toLowerCase()
			+   StringUtils.defaultString( secondPart ).trim().toLowerCase();
		
		// Make username length 8 or 9
		int size = newName.length();
		
		if ( size == USER_NAME_LENGTH - 1 ) {
			newName = newName.concat( ".x" );
		} else if ( size < USER_NAME_LENGTH ) {
			newName = newName.concat( "." ).concat( StringUtils.repeat( 'x', USER_NAME_LENGTH - size - 1 ));
		} else {  // In this case lenght >= DEFAULT_NAME_LENGTH
			newName = newName.toLowerCase().substring( 0,  USER_NAME_LENGTH );   
		}

		return newName;
			
	}
	
	public static String generatePassword() {
		
		String password = "";
		
		boolean generate = true;
		
		while( generate ) {
			password = RandomStringUtils.randomAlphanumeric( PASSWORD_LENGTH * 2 );
			
			// what shall be removed: 0oOlI1
			if ( StringUtils.containsAny( password, CHAR_TO_DELETE_FROM_PASSWORD )) {
				// Delete chars
				for ( char c : CHAR_TO_DELETE_FROM_PASSWORD ) {
					password = StringUtils.remove( password, c );
				}
			}
			
			// Check that length is required. Cut or select next passord
			if ( password.length() >= PASSWORD_LENGTH ) {
				password = StringUtils.left( password, PASSWORD_LENGTH );
				
				generate = false;
			}

		}
		
		return password;
	}

	public static boolean validateUsrName( String usrName ) {
		
		return !StringUtils.isBlank( usrName )
			&&
			usrName.trim().length() >= USER_NAME_LENGTH;
				
	}
	public static boolean validatePassword( String pwd ) {
		
		return !StringUtils.isBlank( pwd )
			&&
				pwd.trim().length() >= PASSWORD_LENGTH;
				
	}
	public static int getMinNameLength() { return USER_NAME_LENGTH; }
	public static int getMinPwdLength() { return PASSWORD_LENGTH; }
	
}
