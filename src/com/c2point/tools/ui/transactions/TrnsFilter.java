package com.c2point.tools.ui.transactions;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDate;

import com.c2point.tools.datalayer.SettingsFacade;
import com.c2point.tools.entity.organisation.Organisation;
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
			
		}

		return res;
	}

	@Override
	public boolean appliesToProperty( Object propertyId ) {

		return ( propertyId != null && propertyId.equals( "data" ) && !allFlagsSet());
		
	}

	
}

