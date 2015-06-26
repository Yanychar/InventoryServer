package com.c2point.tools.ui.transactions;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDate;

import com.c2point.tools.datalayer.SettingsFacade;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Tool;
import com.c2point.tools.entity.transactions.BaseTransaction;
import com.vaadin.data.Container;
import com.vaadin.data.Item;

public class TrnsFilter implements Container.Filter {
	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger( TrnsFilter.class.getName());

	private Date		dateStart;
	private Date		dateEnd;
	
	private boolean 	loginFlag;
	private boolean 	userFlag;
	private boolean 	adminFlag;
	private boolean 	toolFlag;
	private boolean 	otherFlag;

	private Collection<String>	searchStringArray;
	
	public TrnsFilter( Organisation org ) {

		// Read default value of time period in months
		int lengthInMonths = 6;
		try {
			lengthInMonths = Integer.parseInt( 
					SettingsFacade.getInstance().getProperty( org, "periodInMonths", Integer.toString( lengthInMonths )));
		} catch ( NumberFormatException e ) {
			
			logger.error( "Wrong value for length of PeriodInMonths was written in properties: " + 
					SettingsFacade.getInstance().getProperty( org, "periodInMonths" ));	
		}
		
		setDateStart( new LocalDate().minusMonths( lengthInMonths ).toDate() );
		setDateEnd( new Date());

		selectAllFilterFlags();
		
	}
	
	public Date getDateStart() { return dateStart; }
	public void setDateStart( Date dateStart ) { 
		if ( this.dateStart != dateStart ) {
			this.dateStart = dateStart;

		}

	}

	public Date getDateEnd() { return dateEnd; }
	public void setDateEnd( Date dateEnd ) { 

		if ( this.dateEnd != dateEnd ) {
			this.dateEnd = dateEnd; 

		}

	}

	public boolean isLoginFlag() { return loginFlag; }
	public void setLoginFlag(boolean loginFlag) { this.loginFlag = loginFlag; }

	public boolean isUserFlag() { return userFlag; }
	public void setUserFlag(boolean userFlag) { this.userFlag = userFlag; }

	public boolean isAdminFlag() { return adminFlag; }
	public void setAdminFlag(boolean adminFlag) { this.adminFlag = adminFlag; }

	public boolean isToolFlag() { return toolFlag;}
	public void setToolFlag(boolean toolFlag) { this.toolFlag = toolFlag; }

	public boolean isOtherFlag() { return otherFlag; }
	public void setOtherFlag(boolean otherFlag) { this.otherFlag = otherFlag; }
	
	public void selectAllFilterFlags() {
		
		setLoginFlag( true );
		setUserFlag( true );
		setAdminFlag( true );
		setToolFlag( true );
		setOtherFlag( true );
		
	}
	
	public void clearAllFilterFlags() {

		setLoginFlag( false );
		setUserFlag( false );
		setAdminFlag( false );
		setToolFlag( false );
		setOtherFlag( false );
		
	}
	
	public boolean allFlagsSet() {

		return isLoginFlag() 
				&& isUserFlag()
				&& isAdminFlag()
				&& isToolFlag()
				&& isOtherFlag();
		
	}

	@Override
	public boolean passesFilter(Object itemId, Item item)
			throws UnsupportedOperationException {
		
		boolean res = false; 
		
		BaseTransaction trn;
		try {
			trn = ( BaseTransaction ) item.getItemProperty( "data" ).getValue();
		} catch ( Exception e ) {
			return false;
		}
		
		if ( trn == null ) return false;
		
		switch ( trn.getTrnType()) {
			case ACCESSRIGHTS:
			case ORGANISATION:
				res = isAdminFlag(); 
				break;
			case LOGIN:
				res = isLoginFlag(); 
				break;
			case CATEGORY:
			case TOOL:
			case TOOLITEM:
				res = isToolFlag(); 
				break;
			case ACCOUNT:
			case USER:
				res = isUserFlag(); 
				break;
			default:
				res = isOtherFlag();
				break;
		}
		
		if ( res ) {
			
			res = passesSearchString( trn );
		}

		return res;
	}

	@Override
	public boolean appliesToProperty( Object propertyId ) {

		return ( propertyId != null && propertyId.equals( "data" ) && !allFlagsSet());
		
	}

	public void setSearchString( String searchString ) {

		if ( searchString != null && searchString.trim().length() > 0 ) {
			
				searchStringArray = Arrays.asList( searchString.trim().split( " " ));
				
		} else {
			this.searchStringArray = null;
		}
	}

	private boolean passesSearchString( BaseTransaction trn ) {
		
		boolean bRes = true;
		
		if ( this.searchStringArray != null && this.searchStringArray.size() > 0 ) {

			for ( String searchString : this.searchStringArray ) {
			
				switch ( trn.getTrnType()) {
					case ACCESSRIGHTS:
						bRes = bRes 
							&& ( checkPerformerName( trn, searchString ) || checkSourceUserName( trn, searchString ));
						break;
					case ORGANISATION:
						bRes = bRes
							&& ( checkPerformerName( trn, searchString ) || checkOrgName( trn, searchString ));
						break;
					case LOGIN:
						bRes = bRes
							&& checkPerformerName( trn, searchString );
						break;
					case CATEGORY:
						bRes = bRes
							&& checkPerformerName( trn, searchString );
						break;
					case TOOL:
						bRes = bRes
							&& ( checkPerformerName( trn, searchString ) || checkToolName( trn, searchString ));
						break;
					case TOOLITEM:
						bRes = bRes 
							&& ( checkPerformerName( trn, searchString )
									|| checkSourceUserName( trn, searchString ) || checkDestUserName( trn, searchString ) 
									|| checkToolName( trn, searchString ) || checkToolItem( trn, searchString ));
						
						break;
					case ACCOUNT:
						bRes = bRes
							&& ( checkPerformerName( trn, searchString ) || checkSourceUserName( trn, searchString ));
						break;
					case USER:
						bRes = bRes
							&& ( checkPerformerName( trn, searchString ) || checkSourceUserName( trn, searchString ));
						break;
					default:
						break;
				}
			
				if ( !bRes )
					break;
				
			}
			
		}
		
		
		return bRes;
		
	}

	private boolean checkOrgName( BaseTransaction trn, String searchString ) {

		if ( trn.getOrg() != null &&
			trn.getOrg().getName().toLowerCase().indexOf( searchString ) != -1 ) return true;
		
		return false;
	}
	
	private boolean checkPerformerName( BaseTransaction trn, String searchString ) {

		if ( trn.getUser() != null &&
			trn.getUser().getFirstAndLastNames().toLowerCase().indexOf( searchString ) != -1 ) return true;
		
		return false;
	}
	
	private boolean checkSourceUserName( BaseTransaction trn, String searchString ) {

		if ( trn.getSourceUser() != null &&
			trn.getSourceUser().getFirstAndLastNames().toLowerCase().indexOf( searchString ) != -1 ) return true;
		
		return false;
	}
	
	private boolean checkDestUserName( BaseTransaction trn, String searchString ) {

		if ( trn.getDestUser() != null &&
			trn.getDestUser().getFirstAndLastNames().toLowerCase().indexOf( searchString ) != -1 ) return true;
		
		return false;
	}
	
	private boolean checkToolName( BaseTransaction trn, String searchString ) {
		
		Tool tool;
		try {
			tool = trn.getTool();
		} catch ( Exception e ) {
			return false;
		}
		
		if ( tool == null ) return false;

		try {
			if ( tool.getName() != null &&
				 tool.getName().toLowerCase().indexOf( searchString.toLowerCase()) != -1 ) return true;
			
			if ( tool.getDescription() != null &&
				 tool.getDescription().toLowerCase().indexOf( searchString.toLowerCase()) != -1 ) return true;

			if ( tool.getManufacturer() != null && tool.getManufacturer().getName() != null 
					&& tool.getManufacturer().getName().toLowerCase().indexOf( searchString.toLowerCase()) != -1 ) return true;
				
			if ( tool.getModel() != null &&
					 tool.getModel().toLowerCase().indexOf( searchString.toLowerCase()) != -1 ) return true;

		} catch ( Exception e ) {
			return false;
		}
		
		return false;
	}
	
	private boolean checkToolItem( BaseTransaction trn, String searchString ) {
		
		ToolItem toolItem;
		
		try {
			toolItem = trn.getToolItem();
		} catch ( Exception e ) {
			return false;
		}
		
		if ( toolItem == null ) return false;
		
		try {
			
			if ( toolItem.getBarcode() != null &&
				 toolItem.getBarcode().toLowerCase().indexOf( searchString.toLowerCase()) != -1 ) return true;
			
			if ( toolItem.getSerialNumber() != null &&
				 toolItem.getSerialNumber().toLowerCase().indexOf( searchString.toLowerCase()) != -1 ) return true;
			
		} catch ( Exception e ) {
			return false;
		}
		
		return false;
	}
	
}

