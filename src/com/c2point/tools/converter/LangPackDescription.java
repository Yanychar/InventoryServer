package com.c2point.tools.converter;

import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LangPackDescription {

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( LangPackDescription.class.getName());
	
	private String	langStr;
	private String	countryStr;

	private String 	fileName;

	private Locale	locale;
	//	private File	file;
	
	public LangPackDescription() {
		
	}
/*	
	public LangPackDescription( String languageStr ) {
	
		this();
		
		setLanguageStr( languageStr );
		
	}
*/
	public String getLangName() { return this.langStr; }
	public void setLangName( String langStr ) { 
		this.langStr = langStr;
		setupLocale();
	}
	
	public String getCountryName() { return this.countryStr; }
	public void setCountryName( String countryStr ) { 
		this.countryStr = countryStr;
		setupLocale();
	}
	
	public Locale getLocale() { return locale; }
	
	public String getFileName() { return fileName; }
	public void setFileName( String fileName ) { this.fileName = fileName; }
	
/*	
	public File getFile() {
		
		if ( this.file == null ) {
			// File is not opened. Shall be created and opened
			if ( this.fileName == null || this.fileName.length() == 0 ) {
				logger.error( "Filename shall be specified firstly" );
			}
			
			this.file = new File( this.fileName );
		}
		
		// File is opened and ready
		return this.file; 
	}
*/	

	private void setupLocale() {
		if ( this.langStr != null && this.countryStr != null ) {
			
			this.locale = new Locale( this.langStr, this.countryStr );
		}
	}

}
