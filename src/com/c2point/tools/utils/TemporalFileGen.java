package com.c2point.tools.utils;

import java.io.File;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TemporalFileGen {
	private static Logger logger = LogManager.getLogger( TemporalFileGen.class.getName());

	public static String createTempFileName( String prefix, String suffix ) {
//	    String fileName = MessageFormat.format("{0}.{1}", UUID.randomUUID(), extension.trim());
	    
	    File file = createTempFile( prefix, suffix );
	    
	    return file.getAbsolutePath();
	}

	public static String createTempFileName() {
		return createTempFileName( null, null );
	}
	

	public static File createTempFile() {
		return createTempFile( null, null );
	}
	
	public static File createTempFile( String prefix, String suffix ) {

		File file = null;
		
		if ( StringUtils.isEmpty( prefix )) prefix = "temp";
		if ( StringUtils.isEmpty( suffix )) suffix = ".tmp";

		try {
    		
    		file = File.createTempFile( prefix, suffix ); 
    		if ( file != null ) {
    			logger.debug( "File has been created: '" + file.getAbsolutePath() + "'" );
    		} else {
    			logger.debug( "Failed to create TMP file" );
    		}
    		
			
		} catch (IOException e) {
			logger.error( e );
		}
    	
		return file;
		
	}
	
}
