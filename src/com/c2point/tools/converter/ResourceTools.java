package com.c2point.tools.converter;

// Shall be saved in UTF-8. ААА
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourceTools {

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( ResourceTools.class.getName());

	private LangPacks allPacks;
	
	public static void main(String[] args) {

		
		ResourceTools main = new ResourceTools();
		
		main.allPacks = new LangPacks();

		// 1. Read WebResources_en_FI.properties 
		//	 This method reads     en_FI resource file and create internal sorted structure 
		//   to fill other lang packs from .csv files (one csv per lang pack)
		// ReadResource.readPropertyFile( main.allPacks, LOCALE_EN );
		
		// 2. Read ALL csv files
		//	  This method reads csv files (one csv per lang pack) and put
		//    items into internal structure
		//	ReadResource.readCSVfiles( main.allPacks );
	
		// 2. Read ONE csv file with all language packs
		//    This file can be created from Google Drive Lang Packs document. GoogleDrive Doc==> OpenOfice Sheet .ods ==> .csv
		//    The method read this file at once and fill internal structure
		CSVReader.readFile( main.allPacks );
//		main.allPacks.print();
		
		// 3. Put resources into the resource files!
		//    This method creates Resource FileS for all supported lang packs.
		//    Files have correct names + ".new" extension
		ResourcesWriter.createResourceFiles( main.allPacks );

		// 4. Create one csv file for export/sharing
		//    This method creates csv file with all lang packs items ordered as in initial lang pack (p.1)
//		CreateResourcesTool.createCsvFile( main.allPacks );
		
		// 4. Print something if necessary (for debugging )
//		ExportTool.print( main.allPacks );
	}

/*	

	public static String propertyFileName( Locale locale ) {
		return propertyFileName( locale, null );
	}
	public static String propertyFileName( Locale locale, String suffix ) {

		
		String resultFileName = propertyFileBaseName + "_" + locale + propertyFileExtName + ( suffix != null ? suffix : "" );
		
		logger.info( "Resource file name: " + resultFileName );
		
		return resultFileName;
		
	}

	public static String csvFileName( Locale locale ) {

		
		String resultFileName = csvFileBaseName + ( locale != null ? "_" + locale : "" ) + csvFileExtName;
		
		logger.info( "Resource file name: " + resultFileName );
		
		return resultFileName;
		
	}
	public static String csvFileName() {

		return csvFileName( null );
		
	}

*/

}
