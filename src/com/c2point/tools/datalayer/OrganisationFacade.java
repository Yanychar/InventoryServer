package com.c2point.tools.datalayer;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.InventoryUI;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.transactions.TransactionOperation;
import com.vaadin.ui.UI;


public class OrganisationFacade {
	private static Logger logger = LogManager.getLogger(OrganisationFacade.class.getName());

	public static OrganisationFacade getInstance() {

		return new OrganisationFacade();
	}
	
	private OrganisationFacade() {}

	public Organisation add( Organisation org ) {

		Organisation newOrg = null;
		
		if ( org == null )
			throw new IllegalArgumentException( "Valid Organisation cannot be null!" );
		
		try {
			newOrg = DataFacade.getInstance().insert( org );
		} catch ( Exception e ) {
			logger.error( "Failed to add Organisation: " + org );
			logger.error( e );
			return null;
		}

		try {
			OrgUser whoDid = (( InventoryUI )UI.getCurrent()).getSessionOwner();
			TransactionsFacade.getInstance().writeOrg( whoDid, newOrg, TransactionOperation.ADD );
			
		} catch ( Exception e ) {
			logger.error( "Cannot identify who added User");
		}
		
		if ( logger.isDebugEnabled() && newOrg != null ) 
			logger.debug( "Organisation has been added: " + newOrg );
		
		
		return newOrg;
		
	}

	public Collection<Organisation> getOrganisations() {
		
		return DataFacade.getInstance().list( Organisation.class );
		
	}

	public Organisation delete( Organisation org ) {
		
		if ( !org.isServiceOwner()) {
			org.setDeleted();
			return updateInternal( org, true );
		}
		
		return null;
	}

	public Organisation update( Organisation org ) {
		
		return updateInternal( org, false );
	}

	// Internal implementation update and delete
	private Organisation updateInternal( Organisation org, boolean delete ) {

		Organisation newOrg = null;
		
		if ( org == null )
			throw new IllegalArgumentException( "Valid Organisation cannot be null!" );
		
		try {
			newOrg = DataFacade.getInstance().merge( org );
		} catch ( Exception e ) {
			logger.error( "Failed to update Organisation: " + org );
			logger.error( e );
			return null;
		}

		try {
			OrgUser whoDid = (( InventoryUI )UI.getCurrent()).getSessionOwner();
			TransactionsFacade.getInstance().writeOrg( whoDid, newOrg, ( delete ? TransactionOperation.DELETE : TransactionOperation.EDIT ));
			
		} catch ( Exception e ) {
			logger.error( "Cannot identify who edited User");
		}
		
		if ( logger.isDebugEnabled() && newOrg != null ) 
			logger.debug( "Organisation has been " + ( delete ? "deleted" : "updated" ) + ": " + newOrg );
		
		
		return newOrg;
		
	}
	
	
	public void setUniqueCode( Organisation org ) {

		Collection<Organisation> orgList = getOrganisations();
		
		long lastUniqueCode = 1;
		int codeLength = 6;
		long code = 0;

		if ( orgList != null  ) {
			
			lastUniqueCode = orgList.size() + 1;
			
			for ( Organisation orgTmp : orgList ) {

				try {
					code = Long.parseLong( orgTmp.getCode());
					
					if ( lastUniqueCode <= code )
						lastUniqueCode = code++;
					
				} catch ( Exception e ) {
					
				}
				
			}
			
		}

		String newCode = StringUtils.leftPad(
				Long.toString( lastUniqueCode ),
				codeLength,	
				'0'
		);

		// set up User code
		org.setCode( newCode );
		
		
	}
	
	
}

