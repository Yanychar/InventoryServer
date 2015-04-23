package com.c2point.tools.ui.transactions;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDate;

import com.c2point.tools.datalayer.SettingsFacade;
import com.c2point.tools.entity.organisation.Organisation;

public class TrnsFilter {
	private static Logger logger = LogManager.getLogger( TrnsFilter.class.getName());

	private Date		dateStart;
	private Date		dateEnd;
	
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
	

}
