package com.c2point.tools.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.utils.ConfigUtil;
import com.vaadin.server.VaadinService;

public class Configuration {
	private static Logger logger = LogManager.getLogger( Configuration.class.getName());

	private static String	SYSTEM_CONFIG_DIR = "config";
	private static String	SYSTEM_CONFIG_FILE = "config.properties";
	
	
	private static Properties properties = null;
	private static Map<String, Properties> orgganisationsMap = new HashMap<String, Properties>();

	public static String getSystemConfigDir() { 
		
		return 
			  VaadinService.getCurrent().getBaseDirectory() 
			+ File.separator 
			+ SYSTEM_CONFIG_DIR;
	}
	
	public static String getSystemConfigFileName() { 
		
		return getSystemConfigDir()
			+ File.separator 
			+ SYSTEM_CONFIG_FILE;
	}
	
	public static boolean readConfiguration() { // TmsApplication app ) {
		boolean bRes = true;
		Properties locProp;

		if ( logger.isDebugEnabled()) logger.debug( "Start to 'read Mobile InventTori configuration' ..." );
		
//		TmsConfiguration.application = app;
		
		
		// 1. Read and setup if necessary config directory
		// Read tools.properties file
		locProp = new Properties();

		try {
			logger.info( "InventTori main config file: '" + getSystemConfigFileName() + "'" );
			
			locProp.load( new FileInputStream( getSystemConfigFileName()));
			
			if ( logger.isDebugEnabled()) logger.debug( "config.properties was read successfully" );

			addProperties( locProp );
			
		} catch ( Exception e) {
			logger.error( "config.properties was NOT read successfully!!!\n" + e  );
		}

		if ( logger.isDebugEnabled()) logger.debug( "... end of 'read InventTori configuration'" );

		return bRes;
	}
/*
	public static boolean readConfigurationTest() {
		boolean bRes = true;

		if ( logger.isDebugEnabled()) logger.debug( "Start to 'read InventTori configuration' ..." );

		
		
		// 1. Read and setup if necessary config directory and company dir
	!!!	System.setProperty( "tools.config.dir", "C:\\Users\\sevastia\\workspace_tms\\TMS Vaadin 7\\config" );

		// Read tms.properties file
		addProperties( ConfigUtil.getConfigDir() + File.separator + "tools.properties" );

		// Read Local OS specific file
		addProperties( ConfigUtil.getConfigDir() + File.separator + ConfigUtil.getLocalePropertiesFile() );

		// Read test properties file
		addProperties( ConfigUtil.getConfigDir() + File.separator + "testdatabase.properties");
		
		
		
		if ( logger.isDebugEnabled()) logger.debug( "... end of 'read InventTori configuration'" );

		return bRes;
	}
*/
	public static String getProperty( String key, String defVal ) {
		return properties.getProperty( key, defVal );
	}
	
	public static String getProperty( String key ) {
		return getProperty( key, "" );
	}
	
	public static void setProperty( String key, String value ) {
		properties.setProperty( key, value );
	}
	
	public static void addOrganisationConfig( Organisation org, Properties props ) {
		
		orgganisationsMap.put( org.getCode(), props );
	}
	
	public static Properties getProperties() {
		return properties;
	}
	
	public static Map<String, Properties> getOrganisationProperties() {
		return orgganisationsMap;
	}
	
	public static String getOrganisationProperty( String orgCode, String property ) {
		Properties props = orgganisationsMap.get( orgCode );
		if ( props != null ) {
			String res = props.getProperty( property );
			if ( res != null ) {
				return res;
			}
		} 
		
		logger.error( "Requested property '" + property + "' net found for Organisation: Code=" + orgCode );
		return null;

	}
	
	public static void setProperty( String orgCode, String key, String value ) {
		Properties props = orgganisationsMap.get( orgCode );
		if ( props == null ) {
			props = new Properties();
			orgganisationsMap.put( orgCode, props );
		} 
		if ( props != null ) {
			props.setProperty( key, value );
		} 
	}
	

	private static void addProperties( Properties properties ) {

		// Now add read properties
		if ( Configuration.properties == null ) {
			if ( logger.isDebugEnabled()) logger.debug( "Config.properties == null. Will be created!" );
			Configuration.properties = new Properties();
		}
		
		Configuration.properties.putAll( properties );
		if ( logger.isDebugEnabled()) logger.debug( "Properties were added to Config.properties!" );
	}

	@SuppressWarnings("unused")
	private static void addProperties( String fileName ) {

		Properties readProperties = ConfigUtil.readPropertiesFromFile( fileName );
		
		// Now add read properties
		if ( Configuration.properties == null ) {
			if ( logger.isDebugEnabled()) logger.debug( "Config.properties == null. Will be created!" );
			Configuration.properties = new Properties();
		}
		
		Configuration.properties.putAll( readProperties );
		if ( logger.isDebugEnabled()) logger.debug( "Properties were added to Config.properties!" );
	}

}
