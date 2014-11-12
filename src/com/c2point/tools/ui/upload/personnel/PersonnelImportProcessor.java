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
package com.c2point.tools.ui.upload.personnel;

import java.io.File;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.UsersFacade;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.ui.personnelmgmt.StuffListModel;
import com.c2point.tools.ui.upload.FileProcessor;
import com.c2point.tools.ui.upload.ProcessedStatus;

public class PersonnelImportProcessor extends FileProcessor {
	private static Logger logger = LogManager.getLogger( PersonnelImportProcessor.class.getName());
	
	private StuffListModel	model;
	
	
	private PatternLen [] columnPatterns = {
			new PatternLen( "", 10 ), 				//( "\\d{0,10}", 10 ),
			new PatternLen( "", 40 ),
			new PatternLen( "", 40 ),
			new PatternLen( "", 20 ),				//"(\\d|\\+)[\\d\\s\\-]{8,40}", 20 ),
			new PatternLen( "", 60 ),				//"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", 60 ),
			new PatternLen( "", 10 ),
	};
	
	
	public PersonnelImportProcessor( StuffListModel model, File processFile ) {
		
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
				logger.debug( "       Pattern length (should be > 0): " + columnPatterns[ i ].getLength() );
				logger.debug( "       Validation failed: String '" 
								+ nextLine[ i ] + "' should matches pattern "+ columnPatterns[ i ] + ". "
								+ "Matches: " + Pattern.matches( columnPatterns[ i ].getPattern(), nextLine[ i ] )
					);//+ "': " + Pattern.matches( pattern, str ));
				
				return ProcessedStatus.VALIDATION_FAILED;
				
			}

		}
		
		// 4. Specific validation if necessary
		// Check does exist user with the same first+ last name

		if ( UsersFacade.getInstance().listByFIO( model.getOrg(), nextLine[ 1 ], nextLine[ 2 ] ) != null ) {
			
			return ProcessedStatus.EXIST;
		}
		
		
		
		if ( logger.isDebugEnabled()) logger.debug( "    Validation passed" ); 
		return ProcessedStatus.VALIDATED;
	}

	@Override
	protected ProcessedStatus processLine(String[] nextLine, int lineNumber) {

		ProcessedStatus res = ProcessedStatus.FAILED; //( i++ % 10 ) != 0;
		
		OrgUser user = new OrgUser();
		
		user.setOrganisation( model.getOrg());
		
		user.setCode( 			nextLine[ 0 ] );
		user.setFirstName( 		nextLine[ 1 ] );
		user.setLastName( 		nextLine[ 2 ] );
		user.setPhoneNumber( 	nextLine[ 3 ] );
		user.setEmail( 			nextLine[ 4 ] );
		
		user.setSuperUserFlag( Boolean.parseBoolean( nextLine[ 5 ] ));
		
		
		if ( model.add( user ) != null ) {
			
			res = ProcessedStatus.PROCESSED;
			
		}
		
		
		if ( logger.isDebugEnabled()) logger.debug( "      Processing " + ( res == ProcessedStatus.PROCESSED ? "passed" : "FAILED" )); 
			
		return res;
	}

}
