package com.c2point.tools.ui.upload.tools;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.CategoriesFacade;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.tool.Category;

public class CategoriesHolder {
	private static Logger logger = LogManager.getLogger( CategoriesHolder.class.getName());

	private List<Category>	existingCategories;
	
	private Organisation	org;

	public CategoriesHolder( Organisation org ) {
		
		this.org = org;
	}
	
	private List<Category> init() {
		
		existingCategories = CategoriesFacade.getInstance().listTop( org );
		if ( existingCategories == null ) {
			existingCategories = new ArrayList<Category>();
		}
		
		return existingCategories;
	}
	
	
	public Category findOrAddCategory( String [] stringParameters, int startingNumber ) {
		
		return findOrAddCategory( 
				stringParameters[ startingNumber ], 
				stringParameters[ startingNumber + 1 ], 
				stringParameters[ startingNumber + 2 ] 
		);
		
	}
	
	public Category findOrAddCategory( String name_1, String name_2, String name_3 ) {

		if ( name_1 == null || name_1.length() == 0 ) {

			return null;
			
		}

		Category resultCat = null;
		Category catTmp = null;
		Category catTmpParent = null;
		
		if ( existingCategories == null ) {
			init();
		}
		
		catTmp = find( name_1, existingCategories );
		
		if ( catTmp  != null ) {
			// Top branch found
			if ( name_2 == null || name_2.length() == 0 ) {
				//if name_2 is not specified than stop and return found
				resultCat = catTmp;
			} else {
				// We need search in second level because name_2 specified
				catTmpParent = catTmp; // memorize  Parent where search
				if ( catTmp.getChilds() != null && catTmp.getChilds().size() > 0 ) {
					// There are 2nd level categories
					catTmp = find( name_2, catTmp.getChilds());

					if ( catTmp  != null ) {
						// 2nd level category found
						if ( name_3 == null || name_3.length() == 0 ) {
							//if name_3 is not specified than stop and return found
							resultCat = catTmp;
						} else {
//							
							// We need search in 3rd level because name_3 specified
							catTmpParent = catTmp; // memorize  Parent where search
							if (  catTmp.getChilds() != null && catTmp.getChilds().size() > 0 ) {
								// There are 3nd level categories
								catTmp = find( name_3, catTmp.getChilds());

								if ( catTmp  != null ) {
									// 3nd level category found
									resultCat = catTmp;
								} else {
									//No 3nd Category found. Shall be added all
									String [] arrayStr = { name_3 };
									resultCat = add( catTmpParent, arrayStr );
								}
							} else {
								// There are NOT 3nd level categories. Shall be added all
								String [] arrayStr = { name_3 };
								resultCat = add( catTmp, arrayStr );
								
							}
//							
						}
					} else {
						//No 2nd Category found. Shall be added all
						String [] arrayStr = { name_2, name_3 };
						resultCat = add( catTmpParent, arrayStr );
					}
				} else {
					// There are NOT 2nd level categories. Shall be added all
					String [] arrayStr = { name_2, name_3 };
					resultCat = add( catTmp, arrayStr );
					
				}
				
			}
				
		} else {
			//No top Category found. Whole new branch shall be created
			// Not found in 1st category level. Shall be added all
			String [] arrayStr = { name_1, name_2, name_3 };
			resultCat = add( null, arrayStr );

		}

		return resultCat;
	}
	

	private Category find( String name, List<Category> catList ) {

		Category resultCat = null;
		
		for ( Category cat : catList ) {
			
			if ( cat.getName().trim().compareToIgnoreCase( name.trim()) == 0 ) {
				resultCat = cat;
				break;
			}
		}
	
	
		return resultCat;
	}
	
	private Category add( Category parent, String [] arrayOfCats ) {

		Category resultCat = null;
		
		if ( arrayOfCats != null && arrayOfCats.length > 0 ) {

			Category category = null;
			int arrayIndex = 0;
			
			while ( arrayIndex < arrayOfCats.length 
					&& arrayOfCats[ arrayIndex ] != null
					&& arrayOfCats[ arrayIndex ].length() > 0 ) {
				
				// Create category
				category = new Category( "", arrayOfCats[ arrayIndex ], ( parent == null ));
				category.setParent( parent );
				category.setOrg( this.org );
				
				// Add category to DB
				category = CategoriesFacade.getInstance().add( category );
				
				if ( category != null ) {
					if ( parent != null ) {
						parent.getChilds().add( category );
					} else {
						existingCategories.add( category );
					}
					
					arrayIndex++;
					parent = category;
					
				} else {
					return null;
				}
			}
			
			resultCat = category; 
			
		} else {
			logger.error( "Array of category names cannot be NULL or empty!" );
		}
	
		return resultCat;
	}
	
	public static String getCategoryPath( String [] nextLine ) {
		
		return nextLine[0] + ">>" + nextLine[1] + ">>" + nextLine[2];
	}
	
}
