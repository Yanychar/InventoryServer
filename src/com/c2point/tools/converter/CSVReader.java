package com.c2point.tools.converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CSVReader {

	private static Logger logger = LogManager.getLogger( CSVReader.class.getName());
	
	
	public static void readFile( LangPacks allPacks ) {
		InputStream stream = null;
		
		BufferedReader in = null;
		String str;
		
				
		String fileName = Settings.getInputFileName();

		int count = 0;
		try {

			stream = new FileInputStream( fileName );
					
			in = new BufferedReader( new InputStreamReader( stream ) );

			
			// Read first header line. Take Locale sequence
//			str = in.readLine();
//			Locale [] localeSequence = getLocaleSequence( str );
			
			// Read Second header line and pass it through without handling
//			str = in.readLine();
			
			while ((str = in.readLine()) != null) {
			    
		    	count++;
				logger.debug( "Line "+ count + ". " + str );

				handleCsvString( allPacks, str );
			    	
			}
			logger.info( "Number of handled strings from file " + fileName + " is "+ count );
			in.close();
		} catch ( FileNotFoundException e ) {
			logger.error( "File: " + fileName + " NOT found!");
			logger.error( "AbsPath = " + new File(".").getAbsolutePath());

		}catch (IOException e) {
			
			e.printStackTrace();
			
		} finally {
		}
						
		
	}
	
	private static void handleCsvString( LangPacks allPacks, String str ) {

		if ( str != null ) {
	    	String [] array = str.split( Settings.csvDelimiter );

    		logger.debug( "Number of fields in csv string == " + array.length + "." );
    		
	    	if ( array == null || array.length < 1 ) {

	    		// Add empty line
	    		logger.debug( "Empty line was added" );
	    		allPacks.add();

	    	} else if ( array[ 0 ] != null 
	    			&& StringUtils.trim( array[ 0 ]).length() > 0 
	    			&& StringUtils.startsWithIgnoreCase( StringUtils.trim( array[ 0 ]), "##" )) {
	    		
	    		// Add comment line
	    		logger.debug( "Comment line was added" );
	    		
	    		allPacks.add( StringUtils.removeStart( StringUtils.trim( array[ 0 ]), "##" ));
	    		
	    	} else if ( isInstructionsField( allPacks, array )) {
	    		
	    		// Processed during validation in the if operator
		    		
    		} else {

    			LangPackDescription lpd;
    			
    	    	for ( int i = 1; i < array.length; i++ ) {
    	    		
    				lpd = allPacks.getLangPackDescription( i - 1 );
    				
    				if ( lpd != null && lpd.getLocale() != null ) {

    					allPacks.add( array[ 0 ], lpd.getLocale(), array[ i ] );
    					
    				}
    				
    	    		
    	    		
    	    		
    	    	}
    			
	    	}

		} else {
    		logger.error( "CSV String == null! Error!" );
    		return;
		}
	}

	private static boolean isInstructionsField( LangPacks allPacks, String [] array ) {
		
		String field = StringUtils.trim( array[ 0 ] );
		
		if ( field != null ) {

			if ( StringUtils.equalsIgnoreCase( StringUtils.trim( field ), Settings.recLangField )) {

				processLangName( allPacks, array );
				
				return true;
			}
			
			if ( StringUtils.equalsIgnoreCase( StringUtils.trim( field ), Settings.recCountryField )) {

				processCountryName( allPacks, array );
				
				return true;
			}
			
			if ( StringUtils.equalsIgnoreCase( StringUtils.trim( field ), Settings.recFileNameField )) {
					
				processFileName( allPacks, array );
					
				return true;
			}
		}
		
		return false;
	}
	
	private static void processLangName( LangPacks allPacks, String [] array ) {

		LangPackDescription lpd;
		for ( int i = 0; i < ( array.length - 1 ); i++ ) {
			
			lpd = allPacks.getLangPackDescription( i );
			lpd.setLangName( array[ i + 1 ] );
		}

	}
	
	private static void processCountryName( LangPacks allPacks, String [] array ) {

		LangPackDescription lpd;
		for ( int i = 0; i < ( array.length - 1 ); i++ ) {
			
			lpd = allPacks.getLangPackDescription( i );
			lpd.setCountryName( array[ i + 1 ] );
		}

	}
	
	private static void processFileName( LangPacks allPacks, String [] array ) {

		LangPackDescription lpd;
		for ( int i = 0; i < ( array.length - 1 ); i++ ) {
			
			lpd = allPacks.getLangPackDescription( i );
			lpd.setFileName( array[ i + 1 ] );
		}

	}
	

	
}
