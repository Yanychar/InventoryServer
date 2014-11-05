package com.c2point.tools.ui.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;

public class CSVFileReader {
	private static Logger logger = LogManager.getLogger( CSVFileReader.class.getName());

	private CSVReader csvReader = null;
		
	public boolean init( File inputFile, char delim ) {
		boolean result = false;

		try {
			if ( logger.isDebugEnabled()) logger.debug( "  Try to open file..." );
			
			csvReader = new CSVReader( 
					new InputStreamReader( new FileInputStream( inputFile ), "UTF-8" ), delim );
						
			result = true;
		} catch (FileNotFoundException e) {
			logger.error( "Did not find specified file: " + inputFile.getName());
			return false;
		} catch ( UnsupportedEncodingException e ) {
			logger.error( "Unsupported Encoding of specified file: " + inputFile.getName());
		} catch ( Exception e) {
			logger.error( "Unknown error when reads the specified file: " + inputFile.getName());
		}
		
		return result;
	}
	public boolean init( File inputFile ) {
		return init( inputFile, ';' );
	}
	

	public String [] nextLine() throws IOException {
		return csvReader.readNext();
	}

	public void close() {
		if ( csvReader != null ) {
			try {
				csvReader.close();
			} catch (IOException e) {
				logger.error( "Cannot close CSVReader properly" );
			} finally {
				csvReader = null;
			}
		}
	}
}
