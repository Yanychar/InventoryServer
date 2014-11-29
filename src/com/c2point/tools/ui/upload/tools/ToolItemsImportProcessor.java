/*

 * Process string array to import Tools (categories, manufacturers, tools, toolitems)  info.
 * Firstly validate it according to patterns.
 * 
 * Current columns and their patterns:
 * (d-digit, C-character, length-length of field
 *   
 *   1. 
 *   2. 
 *   3. 
 *   4. 
 *   5. 
 *   6. 
 *   
 */
package com.c2point.tools.ui.upload.tools;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.ItemsFacade;
import com.c2point.tools.datalayer.SettingsFacade;
import com.c2point.tools.datalayer.ToolsFacade;
import com.c2point.tools.datalayer.UsersFacade;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Category;
import com.c2point.tools.entity.tool.Manufacturer;
import com.c2point.tools.entity.tool.Tool;
import com.c2point.tools.ui.toolsmgmt.ToolsListModel;
import com.c2point.tools.ui.upload.FileProcessor;
import com.c2point.tools.ui.upload.ProcessedStatus;

public class ToolItemsImportProcessor extends FileProcessor {
	private static Logger logger = LogManager.getLogger( ToolItemsImportProcessor.class.getName());
	
	private ToolsListModel	model;

	private CategoriesHolder		catHolder;
	private ManufacturersHolder			prodHolder;
	
	
	private PatternLen [] columnPatterns = {
			new PatternLen( "", 20 ),  // Top category 				//( "\\d{0,10}", 10 ),
			new PatternLen( "", 20 ),  // Sub-category 1
			new PatternLen( "", 20 ),  // Sub-category 2
			new PatternLen( "", 10 ),  // Tool Code				//"(\\d|\\+)[\\d\\s\\-]{8,40}", 20 ),
			new PatternLen( "", 50 ),  // Tool Name				//"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$", 60 ),
			new PatternLen( "",255 ),  // Tool Description
			new PatternLen( "", 20 ),  // Manufacturer
			new PatternLen( "", 50 ),  // Barcode
			new PatternLen( "", 40 ),	// First Name of User
			new PatternLen( "", 40 ),	// Last Name of User
	};
	
	
	public ToolItemsImportProcessor( ToolsListModel model, File processFile ) {
		
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
			logger.debug( "   Validation passed: Line #"+lineNumber+" is empty or commented out" );

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
		if ( catHolder == null ) catHolder = new CategoriesHolder( model.getOrg());
		
		if ( nextLine[ 0 ] == null || nextLine[ 0 ].length() == 0 ) {

			if ( nextLine[ 4 ] != null && nextLine[ 4 ].length() != 0 ) {
				logger.debug( "    Validation failed: Top Category for Tool is not specified but Tool is specified" );
			
				return ProcessedStatus.VALIDATION_FAILED;
			}
			
		}
		
		// 4.2 Manufacturer validation
		if ( prodHolder == null ) prodHolder = new ManufacturersHolder();
		
		if ( nextLine[ 6 ] == null || nextLine[ 0 ].length() == 0 ) {

			logger.debug( "    Manufacturer is not specified but this is OK" );
			
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
		
		Manufacturer manufacturer = prodHolder.findOrAddManufacturer( nextLine, 6 );

		logger.debug( "   Manufacturer '" + nextLine[6] + "' was" + ( manufacturer != null ? "" : " NOT" ) + " found or added" ); 
		
		
		Tool tool = createTool( nextLine, category, manufacturer );
		Tool existingTool = null; 
		
		if ( tool != null ) {
			
			existingTool = findExistingTool( tool );
			
			if ( existingTool != null ) {
				// Tool with the same data found in database. Will be used to add ToolItem
				logger.debug( "Tool exists in DB: " + existingTool );
				
				tool = existingTool;
				
			} else {
				// No such tool was found in DB. New one will be added and used
				logger.debug( "Tool does NOT exists in DB. Will be added" );
				
				tool = addTool( tool );
				
			}

			// Now after Tool data processing ToolItem data shall be processed 
			if ( tool != null ) {
				// Tool was found or new one was added
				// Tool Item need to be processed
				ToolItem item = createToolItem( nextLine, tool );
				ToolItem existingItem = null; 
				
				if ( item != null ) {
					
					existingItem = findExistingToolItem( item );
				
					if ( existingItem != null ) {
						// Tool Item with the same data found in database. Will be used 
						logger.debug( "Tool Item exists in DB: " + existingItem );
						
						item = existingItem;
						
					} else {
						// No existing Tool Item was found in DB. New one will be added and used
						logger.debug( "Tool Item does NOT exists in DB. Will be added" );
						item = addToolItem( item );
						
					}
					
					// Now if Tool Item was not found or added ==>> Processing of this record failed
					// Record without Tool and ToolItem info shall be rejected in validation stage
					if ( item != null ) {
						
						res = ProcessedStatus.PROCESSED;
						
					}
				} else {

					logger.debug( "Not necessary to create Tools Item from imported record. Toll is enough (no user specified)");

					res = ProcessedStatus.PROCESSED;
					
				}
					
				
			} else {
				logger.debug( "Tool Item cannot be found or created from imported data");
				
			}
			

		
		} else {
			
			logger.debug( "Could not create Tool from imported record");
			res = ProcessedStatus.PROCESSED;

		}
		
		logger.debug( "      Processing " + ( res == ProcessedStatus.PROCESSED ? "passed" : "FAILED" )); 
			
		return res;
	}

	
	private Tool createTool( String [] nextLine, Category category, Manufacturer manufacturer ) {
	
		if (   	( nextLine[ 4 ] == null || nextLine[ 4 ].length() == 0 ) 
			&& 
				( nextLine[ 5 ] == null || nextLine[ 5 ].length() == 0 )
		) {
			// Not enough information to create the tool
			return null;
			
		}
		
		
		Tool resTool = new Tool();

		// Fields currently imported:
		// 	code;			nextLine[3]    
		// 	name;    		nextLine[4]
		// 	description;	nextLine[5]
		
		setupCode( resTool, nextLine [ 3 ] );
		resTool.setName( nextLine [ 4 ] );
		resTool.setDescription( nextLine [ 5 ] );

		resTool.setCategory( category );
		resTool.setManufacturer( manufacturer );
		
		resTool.setOrg( model.getOrg());

		logger.debug( "Created " + resTool );
		
		return resTool;
	}

	private Tool findExistingTool( Tool tool ) {
	
		Tool resTool = ToolsFacade.getInstance().getTool( model.getOrg(), tool );
		
		return resTool;
	}

	private Tool addTool( Tool tool ) {
	
		Tool addedTool = ToolsFacade.getInstance().add( tool );
		
		return addedTool;
	}

	private ToolItem createToolItem( String [] nextLine, Tool tool ) {
	
		ToolItem resItem = 	new ToolItem( tool, null, null );

		// If Barcode exist than this item definitely shall be created 
		if ( nextLine[ 7 ] != null && nextLine[ 7 ].length() > 0 ) {
			resItem.setBarcode( nextLine[ 7 ]);
			resItem.setCurrentUser( model.getSessionOwner());

			logger.debug( "Not necessary to create Tools Item from imported record. Toll is enough (no user specified)");

		}
		
		// 
		setCurrentUserIfNecessary( nextLine,  resItem );
		
		// If There is no current user specified or specified but does not exist than no Item will be created
		if ( resItem.getCurrentUser() == null ) {

			logger.debug( "No current user identified. Item shall not be created! Tool is enough" );
			
			resItem = null;
		}
		
		return resItem;
	}

	private void setCurrentUserIfNecessary( String [] nextLine,  ToolItem item ) {
	
		if ( nextLine[ 8 ] != null && nextLine[ 8 ].length() > 0 
			&&
			 nextLine[ 9 ] != null && nextLine[ 9 ].length() > 0
		) {
			
			// Find User (org, firstName, lastName)
			List< OrgUser > userList = UsersFacade.getInstance().listByFIO( model.getOrg(), nextLine[ 8 ], nextLine[ 9 ] );
			// If user found set it as current user
			if ( userList != null ) {
				item.setCurrentUser( userList.get( 0 ));
				logger.debug( "User: " + userList.get( 0 ) + " was set as Current User" );
			}
			
			
		}
	}
	
	private ToolItem findExistingToolItem( ToolItem item ) {
	
		// TODO if necessary
		
		// Currently it is not understood how to find existing tool and what does "existing tool" mean 
		
		ToolItem resItem = null;
		
		
		return resItem;
	}

	private ToolItem addToolItem( ToolItem item ) {
	
		ToolItem addedItem = ItemsFacade.getInstance().add( item );
		
		logger.debug( "Was added: " + addedItem );
		
		return addedItem;
	}

	private void setupCode( Tool tool, String code ) {
		
		if ( tool != null ) {
			
			if ( code != null && code.length() > 0 ) {
				tool.setCode( code );
			} else {
				
				// Code will be generated
				long lastUniqueCode = 0;
				
				try {
					lastUniqueCode = Long.parseLong( 
							SettingsFacade.getInstance().getProperty( model.getOrg(), "lastToolCode" ));
				} catch ( NumberFormatException e ) {
					
					logger.error( "Wrong value for lastToolCode was written in properties: " + 
							SettingsFacade.getInstance().getProperty( model.getOrg(), "lastToolCode" ));	
				}
				
				if ( lastUniqueCode == 0 ) {
					
					lastUniqueCode = ToolsFacade.getInstance().count( model.getOrg());

				}

				int codeLength = 6;
				try {
					codeLength = Integer.parseInt( 
							SettingsFacade.getInstance().getProperty( model.getOrg(), "toolCodeLength", "4" ));
				} catch ( NumberFormatException e ) {
					
					logger.error( "Wrong value for length of ToolCode was written in properties: " + 
							SettingsFacade.getInstance().getProperty( model.getOrg(), "toolCodeLength" ));	
				}
				
				lastUniqueCode++;
				
				String newCode = StringUtils.leftPad(
						Long.toString( lastUniqueCode ),
						codeLength,	
						'0'
				);

				// Store new lastUniqueCode
				SettingsFacade.getInstance().setProperty( model.getOrg(), 
														  "lastToolCode", 
														  Long.toString( lastUniqueCode ));
				// set up User code
				tool.setCode( newCode );
				
			}
		}
	}
	
	
	
}
