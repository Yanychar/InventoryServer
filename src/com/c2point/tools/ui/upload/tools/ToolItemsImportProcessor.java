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
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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
	
	private ToolsListModel			model;

	private CategoriesHolder		catHolder;
	private ManufacturersHolder		prodHolder;
	
	// Internally used status to pass value between methods with occupied return value
	private ProcessedStatus 		tmpRes = ProcessedStatus.FAILED;
	
	private PatternLen [] columnPatterns = {					// Was  Now
			new PatternLen( "", 20 ),  // Top category 			0		0		
			new PatternLen( "", 20 ),  // Sub-category 1		1		1		
			new PatternLen( "", 20 ),  // Sub-category 2		2		2
			new PatternLen( "", 20 ),  // Manufacturer			6		3
			new PatternLen( "", 20 ),  // Model							4
			new PatternLen( "", 10 ),  // Tool Code				3		5		
			new PatternLen( "", 50 ),  // Tool Name				4		6		
			new PatternLen( "",255 ),  // Tool Description		5		7
			new PatternLen( "", 50 ),  // Barcode				7		8
			new PatternLen( "", 10 ),  // Buy Time						9
			new PatternLen( "", 10 ),  // Price							10
			new PatternLen( "",  2 ),  // Guarantee						11
			new PatternLen( "", 10 ),  // Last Maintenance Time  		12
			new PatternLen( "", 40 ),	// First Name of User	8		13
			new PatternLen( "", 40 ),	// Last Name of User	9		14
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
		if ( catHolder == null ) catHolder = new CategoriesHolder( model.getSelectedOrg());
		
		if ( nextLine[ 0 ] == null || nextLine[ 0 ].length() == 0 ) {

			if ( nextLine[ 6 ] != null && nextLine[ 6 ].length() != 0 ) {
				logger.debug( "    Validation failed: Top Category for Tool is not specified but Tool is specified" );
			
				return ProcessedStatus.VALIDATION_FAILED;
			}
			
		}
		
		// 4.2 Manufacturer validation
		if ( prodHolder == null ) prodHolder = new ManufacturersHolder();
		
		if ( nextLine[ 3 ] == null || nextLine[ 3 ].length() == 0 ) {

			logger.debug( "    Manufacturer is not specified but this is OK" );
			
		}
		
		
		
		if ( logger.isDebugEnabled()) logger.debug( "    Validation passed" ); 
		return ProcessedStatus.VALIDATED;
	}

	@Override
	protected ProcessedStatus processLine(String[] nextLine, int lineNumber) {

		ProcessedStatus res = ProcessedStatus.FAILED; //( i++ % 10 ) != 0;
		
		Category category = handleCategory( nextLine );

		Manufacturer manufacturer = handleManufacturer( nextLine );
		
		tmpRes = ProcessedStatus.FAILED;
		Tool tool = handleTool( nextLine, category, manufacturer );
		
		if ( tool != null && tmpRes == ProcessedStatus.PROCESSED ) { 
			
			handleToolItem( nextLine, tool );

			res = tmpRes;
			
		} else {

			res = tmpRes;
			
		}
		
		logger.debug( "      Processing " + ( res == ProcessedStatus.PROCESSED ? "passed" : "FAILED" )); 
			
		return res;
	}

	
	private Tool createTool( String [] nextLine, Category category, Manufacturer manufacturer ) {

		// Fields currently imported:
		//	model:			nextLine[4]
		// 	code:			nextLine[5]    
		// 	name:    		nextLine[6]
		// 	description:	nextLine[7]

		// Enough to specify:
		//	- Name
		//	- Manufacturer+Model

		Tool resTool = null;
		
		if ( nextLine[ 6 ] != null && nextLine[ 6 ].length() != 0 
			|| 
			 manufacturer != null && nextLine[ 4 ] != null && nextLine[ 4 ].length() != 0
		) {
			
			resTool = new Tool();
			
			resTool.setManufacturer( manufacturer );
			resTool.setModel( nextLine [ 4 ] );
			setupCode( resTool, nextLine [ 5 ] );
			resTool.setName( nextLine [ 6 ] );
			resTool.setDescription( nextLine [ 7 ] );
	
			resTool.setCategory( category );
			
			resTool.setOrg( model.getSelectedOrg());
	
			logger.debug( "Created " + resTool );

		}
		
		return resTool;
	}

	private Tool findExistingTool( Tool tool ) {
	
		Tool resTool = ToolsFacade.getInstance().getTool( model.getSelectedOrg(), tool );
		
		return resTool;
	}

	private Tool addTool( Tool tool ) {
	
		Tool addedTool = ToolsFacade.getInstance().add( tool );
		
		return addedTool;
	}

	private ToolItem createToolItem( String [] nextLine, Tool tool ) {
	
		ToolItem resItem = 	new ToolItem( tool, model.getSessionOwner(), model.getSessionOwner() );

		// If Barcode exist than this item definitely shall be created 
		if ( nextLine[ 8 ] != null && nextLine[ 8 ].length() > 0 ) {
			resItem.setBarcode( nextLine[ 8 ]);

			logger.debug( "Not necessary to create Tools Item from imported record. Tool is enough (no user specified)");

		}
		
		// 
		setCurrentUserIfNecessary( nextLine,  resItem );
		
		// If There is no current user specified or specified but does not exist than no Item will be created
		if ( resItem.getCurrentUser() == null ) {

			logger.debug( "No current user identified. Item shall not be created! Tool is enough" );
			
			resItem = null;
		} else {
			// Setup other ToolItem specific parameters:
			//	buyTime
			//	price
			//	takuu
			//	maintenanceTime
			
			resItem.setBuyTime( createLocalDate( nextLine[ 9 ] ));
			try {
				resItem.setPrice( Double.valueOf( nextLine[ 10 ] ));
			} catch ( Exception e ) {
				resItem.setPrice( null );
			}
			try {
				resItem.setTakuu( Integer.valueOf( nextLine[ 11 ] ));
			} catch ( Exception e ) {
				resItem.setTakuu( null );
			}
			resItem.setMaintenance( createLocalDate( nextLine[ 12 ] ));
		}
		
		return resItem;
	}

	private void setCurrentUserIfNecessary( String [] nextLine,  ToolItem item ) {
	
		if ( nextLine[ 13 ] != null && nextLine[ 13 ].length() > 0 
			&&
			 nextLine[ 14 ] != null && nextLine[ 14 ].length() > 0
		) {
			
			// Find User (org, firstName, lastName)
			List< OrgUser > userList = UsersFacade.getInstance().listByFIO( model.getSelectedOrg(), nextLine[ 13 ], nextLine[ 14 ] );
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
	
/*		
		ToolItem addedItem = ItemsFacade.getInstance().add( item );
*/
		ToolItem addedItem = model.add( item );
		
		if ( logger.isDebugEnabled() && addedItem != null )
		
			logger.debug( "ToolItem was added: " + addedItem.getTool().getFullName());
		
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
							SettingsFacade.getInstance().getProperty( model.getSelectedOrg(), "lastToolCode" ));
				} catch ( NumberFormatException e ) {
					
					logger.error( "Wrong value for lastToolCode was written in properties: " + 
							SettingsFacade.getInstance().getProperty( model.getSelectedOrg(), "lastToolCode" ));	
				}
				
				if ( lastUniqueCode == 0 ) {
					
					lastUniqueCode = ToolsFacade.getInstance().count( model.getSelectedOrg());

				}

				int codeLength = 6;
				try {
					codeLength = Integer.parseInt( 
							SettingsFacade.getInstance().getProperty( model.getSelectedOrg(), "toolCodeLength", "4" ));
				} catch ( NumberFormatException e ) {
					
					logger.error( "Wrong value for length of ToolCode was written in properties: " + 
							SettingsFacade.getInstance().getProperty( model.getSelectedOrg(), "toolCodeLength" ));	
				}
				
				lastUniqueCode++;
				
				String newCode = StringUtils.leftPad(
						Long.toString( lastUniqueCode ),
						codeLength,	
						'0'
				);

				// Store new lastUniqueCode
				SettingsFacade.getInstance().setProperty( model.getSelectedOrg(), 
														  "lastToolCode", 
														  Long.toString( lastUniqueCode ));
				// set up User code
				tool.setCode( newCode );
				
			}
		}
	}
	
	private Category handleCategory( String[] nextLine ) {

		Category category = catHolder.findOrAddCategory( nextLine, 0 );
		
		if ( logger.isDebugEnabled()) {
			
			if ( category != null )
				logger.debug( "   Category '" + CategoriesHolder.getCategoryPath( nextLine ) + "' was found. Is it Top Level? " 
								+ ( category.isTopCategoryFlag() ? "Yes" : "No" ));
			else
				logger.debug( "   Category '" + CategoriesHolder.getCategoryPath( nextLine ) + "' was NOT found or added." );
		}
		
		return category;
	}

	private Manufacturer handleManufacturer( String[] nextLine ) {
	
		Manufacturer manufacturer = prodHolder.findOrAddManufacturer( nextLine, 3 );

		logger.debug( "   Manufacturer '" + nextLine[3] + "' was" + ( manufacturer != null ? "" : " NOT" ) + " found or added" );
		
		return manufacturer;
	}

	private Tool handleTool( String[] nextLine, Category category, Manufacturer manufacturer ) {
	
		Tool existingTool = null;

		this.tmpRes = ProcessedStatus.FAILED;
		
		Tool tool = createTool( nextLine, category, manufacturer );

		if ( tool != null && category == null ) {
			logger.error( "Category cannot be null to create tool! " );
			
			return null;
		}

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
			
			if ( tool != null ) {

				setProcessedObject( tool );

				this.tmpRes = ProcessedStatus.PROCESSED;
				
			}
			
		} else {
			
			// Not enough info to create the tool!
			// Everything except Category and Manufacturer shall be null in this case. Otherwise FAILED
			logger.debug( "Not enough info to create the Tool! But it can be ok" );
			
			// Validate the rest of parameters after Manufacturer
			this.tmpRes = ProcessedStatus.PROCESSED;
			for ( int i = 4; i < nextLine.length; i++ ) {
				
				if ( nextLine[ i ] != null && nextLine[ i ].length() > 0 ) {
					
					this.tmpRes = ProcessedStatus.FAILED;
					break;
				}
			}
			
		}
		
		return tool;
	}

	private ToolItem handleToolItem( String[] nextLine, Tool tool ) {
		
		ToolItem toolItem = null;

		this.tmpRes = ProcessedStatus.FAILED;

		// Now after Tool data processing ToolItem data shall be processed 
		if ( tool != null ) { 
			// Tool was found or new one was added
			// Tool Item need to be processed
			toolItem = createToolItem( nextLine, tool );
			ToolItem existingItem = null; 
			
			if ( toolItem != null ) {
				
				existingItem = findExistingToolItem( toolItem );
			
				if ( existingItem != null ) {
					// Tool Item with the same data found in database. Will be used 
					logger.debug( "Tool Item exists in DB: " + existingItem );
					
					toolItem = existingItem;
					
				} else {
					// No existing Tool Item was found in DB. New one will be added and used
					logger.debug( "Tool Item does NOT exists in DB. Will be added" );
					toolItem = addToolItem( toolItem );
					
				}
				
				// Now if Tool Item was not found or added ==>> Processing of this record failed
				// Record without Tool and ToolItem info shall be rejected in validation stage
				if ( toolItem != null ) {
					
					setProcessedObject( toolItem );
					
					this.tmpRes = ProcessedStatus.PROCESSED;
					
				}
			} else {

				logger.debug( "Not necessary to create Tools Item from imported record. Tool is enough (no user specified)");

				this.tmpRes = ProcessedStatus.PROCESSED;
				
			}
				
			
		} else {
			logger.debug( "Tool Item cannot be found or created from imported data");
			
		}
		
		return toolItem;
		
	}

	private LocalDate createLocalDate( String dateStr ) {
		
		LocalDate date = null;
		final DateTimeFormatter df = DateTimeFormat.forPattern("dd.mm.yyyy");
		
		try {
			date = df.parseLocalDate( dateStr );			
			
		} catch( Exception e ) {
			logger.error( "Cannot convert to date: '" + dateStr + "'" );
			date = null;
		}
		
		return date;
		
	}
	
	
}
