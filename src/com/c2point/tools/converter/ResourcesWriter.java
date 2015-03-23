package com.c2point.tools.converter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcesWriter {

	private static Logger logger = LogManager.getLogger( ResourcesWriter.class.getName());

	protected static String 	EOL = "\r\n";
	
	public static void createResourceFiles( LangPacks allPacks ) {
		for ( LangPackDescription lpd : allPacks.getLangPackDescriptions()) {
			if ( lpd != null ) {
				createResource( allPacks, lpd );
			}
		}
	}

	private static boolean createResource( LangPacks allPacks, LangPackDescription lpd ) {
		
		// Create Lang Resource property file
		String filename = lpd.getFileName();
	
		try {
			
			FileWriter fstream = new FileWriter( filename );
			BufferedWriter out = new BufferedWriter( fstream );			
			
			Iterator<String> iter = allPacks.keys().iterator();
			String key;
			
			writeHeader( out, allPacks, lpd );
			
			while( iter.hasNext()) {
				key = iter.next();
				
				AbstractEntity entity =  allPacks.get( key );
	
				if ( entity.getType() == AbstractEntity.EntityType.EMPTY ) {
					
					writeEmpty( out );
					
				} else if ( entity.getType() == AbstractEntity.EntityType.COMMENT ) {
	
					writeComment( out, entity.getValue());
										// 				
				} else if ( entity.getType() == AbstractEntity.EntityType.PROPERTY ) {
					
					writeProperty( out, key, entity.getValue( lpd.getLocale()));

				}
				/*
				else if ( entity.getType() == AbstractEntity.EntityType.BRANCH ) {
				
					writeBranch( out, ( BranchEntity )entity, lpd.getLocale());
	
				}
				*/
				
			}

			writeFooter( out, allPacks, lpd );
			
			out.close();
			fstream.close();
		} catch ( Exception e ) {
			logger.error( "Error: " + e );
		}
			
		
		return true;
	}

	private static void writeProperty( Writer out, String key, String value ) {

		writePropertyInt( out, "\t", key, value ); 
		
	}

	private static void writePropertyInt( Writer out, String tab, String key, String value ) {

		try {
			out.write( key + " = " + StringUtils.trim( value ) );
			out.write( EOL );
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static void writeEmpty( Writer out ) {

		try {
//			out.write( "" );
			out.write( EOL );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private static void writeComment( Writer out, String value ) {
/*
		try {
			
			out.write( "//" + value );
			out.write( EOL );
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/		
	}
/*
	private static void writeBranch( Writer out, BranchEntity entity, Locale locale ) {
		
		try {
			
			out.write( "\t" + entity.getName() + "\t:\t{" );
			out.write( EOL );
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Here shall be all properties
		Iterator< PropertyEntity > iter = entity.getProperties().values().iterator();
		PropertyEntity property;
		
		while ( iter.hasNext()) {

			property = iter.next();

			if ( property != null ) {
				
				writePropertyInt( out, "\t\t", property.getName(), property.getValue( locale ));
						
			}
			
		}
		
		
		
		
		try {
			
			out.write( "\t}," );
			out.write( EOL );
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
*/	
	
	
	private static void writeHeader( Writer out, LangPacks allPacks, LangPackDescription lpd ) {
/*
		try {
			
			out.write( "var " + lpd.getResourceName() + " = {" );
			out.write( EOL );
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/		
	}
	
	private static void writeFooter( Writer out, LangPacks allPacks, LangPackDescription lpd ) {
/*
		try {
			
			out.write( "}" );
			out.write( EOL );
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/		
		
	}


}
