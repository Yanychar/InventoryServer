/*

 * Process string array to import personnel info.
 * Firstly validate it according to patterns.
 * 
 * Current columns and their patterns:
 * (d-digit, C-character, length-length of field
 *   
 *   1. Code: 				optional, D, length <=10
 *   2. First name:			optional, C, length <=40
 *   3. Last Name: 			mandatory,  C, length <=40
 *   4. Phone Number: 		mandatory,  C, length <=20
 *   5. Email: 				mandatory,  C, length <=60. Email pattern
 *   6. Superuser Flag: 	optional, "true" or "false". Default "false"
 *   
 */
package com.c2point.tools.ui.upload.tools;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.UsersFacade;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.tool.Category;
import com.c2point.tools.entity.tool.Producer;
import com.c2point.tools.ui.toolsmgmt.ToolsManagementModel;
import com.c2point.tools.ui.upload.FileProcessor;
import com.c2point.tools.ui.upload.ProcessedStatus;

public class ToolItemsImportProcessor extends FileProcessor {
	private static Logger logger = LogManager.getLogger( ToolItemsImportProcessor.class.getName());
	
	private ToolsManagementModel	model;

	private CategoriesHolder		catHolder;
	private ProducersHolder			prodHolder;
	
	
	private PatternLen [] columnPatterns = {
			new PatternLen( "", 20 ), 				//( "\\d{0,10}", 10 ),
			new PatternLen( "", 20 ),
			new PatternLen( "", 20 ),
			new PatternLen( "", 10 ),				//"(\\d|\\+)[\\d\\s\\-]{8,40}", 20 ),
			new PatternLen( "", 20 ),				//"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", 60 ),
			new PatternLen( "",255 ),
			new PatternLen( "", 10 ),
			new PatternLen( "", 20 ),
			new PatternLen( "", 50 ),
	};
	
	
	public ToolItemsImportProcessor( ToolsManagementModel model, File processFile ) {
		
		super(processFile);
		
		this.model = model;

		if ( logger.isDebugEnabled()) {
			
			logger.debug( "Found patterns [pattern, length]:" );
			for ( PatternLen pattern :  columnPatterns ) {
				logger.debug( "  " + pattern );
			}
			
		}
		
	}

	@Override
	protected ProcessedStatus validateLine( String[] nextLine, int lineNumber ) {

		// 1. Firstly check that Line is not comment (start from #) and not empty)
		if ( nextLine == null 
				|| nextLine.length == 0 
				|| nextLine.length == 1 && nextLine[0].trim().length() == 0
				|| nextLine[0].trim().length() > 0 && nextLine[ 0 ].trim().charAt( 0 ) == '#' ) {
			
			// Comment or empty line
			if ( logger.isDebugEnabled()) logger.debug( "   Validation passed: Line #"+lineNumber+" is empty or commented out" );

			return ProcessedStatus.COMMENT;
			
		}
		
		// 2. Validate number of columns
		if ( nextLine.length != columnPatterns.length ) {
			logger.debug( "    Validation failed: wrong number of fields: " + nextLine.length + ". Should be " + columnPatterns.length ); 
			// Number of columns in string is wrong!
			return ProcessedStatus.VALIDATION_FAILED;
		}
		
		// 3. validate each column
		for ( int i = 0; i < columnPatterns.length; i++ ) {
			
			if (	columnPatterns[ i ].getPattern() != null 
				&& 	columnPatterns[ i ].getPattern().length() > 0 
				&& !Pattern.matches( columnPatterns[ i ].getPattern(), nextLine[ i ] )) {

				
				logger.debug( "    Validation failed: " );
				logger.debug( "       Pattern length (should be > 0): " + columnPatterns[ i ].getLength());
				logger.debug( "       Validation failed: String '" 
								+ nextLine[ i ] + "' should matches pattern "+ columnPatterns[ i ] + ". "
								+ "Matches: " + Pattern.matches( columnPatterns[ i ].getPattern(), nextLine[ i ] )
					);//+ "': " + Pattern.matches( pattern, str ));
				
				return ProcessedStatus.VALIDATION_FAILED;
				
			}

		}
		
		// 4. Specific validation if necessary
		
		// 4.1 Categories validation
		if ( catHolder == null ) catHolder = new CategoriesHolder();
		
		if ( nextLine[ 0 ] == null || nextLine[ 0 ].length() == 0 ) {

			if ( nextLine[ 4 ] != null && nextLine[ 4 ].length() != 0 ) {
				logger.debug( "    Validation failed: Top Category for Tool is not specified" );
			
				return ProcessedStatus.VALIDATION_FAILED;
			}
			
		}
		
		// 4.2 Producer validation
		if ( prodHolder == null ) prodHolder = new ProducersHolder();
		
		if ( nextLine[ 7 ] == null || nextLine[ 0 ].length() == 0 ) {

			logger.debug( "    Producer is not specified but this is OK" );
			
		}
		
		
		
		if ( logger.isDebugEnabled()) logger.debug( "    Validation passed" ); 
		return ProcessedStatus.VALIDATED;
	}

	@Override
	protected ProcessedStatus processLine(String[] nextLine, int lineNumber) {

		ProcessedStatus res = ProcessedStatus.FAILED; //( i++ % 10 ) != 0;
		
		Category category = catHolder.findOrAddCategory( nextLine, 0 );
		
		if ( logger.isDebugEnabled()) {
			
			if ( category != null )
				logger.debug( "   Category '" + CategoriesHolder.getCategoryPath( nextLine ) + "' was found. Is it Top Level? " 
								+ ( category.isTopCategoryFlag() ? "Yes" : "No" ));
			else
				logger.debug( "   Category '" + CategoriesHolder.getCategoryPath( nextLine ) + "' was NOT found or added." );
		}
		
		Producer producer = prodHolder.findOrAddProducer( nextLine, 7 );
		if ( logger.isDebugEnabled()) {
			logger.debug( "   Producer '" + nextLine[7] + "' was" + ( producer != null ? "" : " NOT" ) + " found or added" ); 
		}
		
		
		
		
		res = ProcessedStatus.PROCESSED;
			
		if ( logger.isDebugEnabled()) logger.debug( "      Processing " + ( res == ProcessedStatus.PROCESSED ? "passed" : "FAILED" )); 
			
		return res;
	}

	
	
}
