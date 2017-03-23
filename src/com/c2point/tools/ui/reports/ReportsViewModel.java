package com.c2point.tools.ui.reports;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.ItemsFacade;
import com.c2point.tools.datalayer.UsersFacade;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.ui.printpdf.PersonalToolsListForm;
import com.c2point.tools.ui.util.AbstractModel;

public class ReportsViewModel extends AbstractModel {
	private static Logger logger = LogManager.getLogger( ReportsViewModel.class.getName());

	private Organisation	currentOrg;
	
	public ReportsViewModel() {
		
		currentOrg = super.getSessionOwner().getOrganisation();
	}

	public void setOrganisation( Organisation org ) {
		this.currentOrg = org;
	}
	
	public Collection<OrgUser> getUsers() {
		
		return UsersFacade.getInstance().list( this.currentOrg );
	}

	public PersonalToolsListForm getDocToPrint() {
		
		Collection<OrgUser> userList = getUsers();
		
		if ( userList == null || userList.size() == 0 ) {
			logger.error( "List of Users passed to form creator == null or empty!" );
			return null;
		}

		PersonalToolsListForm document = new PersonalToolsListForm( getApp().getSessionData().getBundle());
		
		for ( OrgUser user : userList ) {
			
			getDocToPrint( document, user );
		}
		
		if ( document != null )
			document.close();
		
		return document;
		
	}
	
	public PersonalToolsListForm getDocToPrint( OrgUser user ) {

		PersonalToolsListForm document = new PersonalToolsListForm( getApp().getSessionData().getBundle());

		document = getDocToPrint( document, user );

		if ( document != null )
			document.close();
		
		return document;
	}
		
	private PersonalToolsListForm getDocToPrint( PersonalToolsListForm document, OrgUser user ) {
		
		if ( user == null ) {
			logger.error( "User passed to form creator == null!" );
			return document;
		}
		
		// Get list of Tools
		List<ToolItem> tiList = ( List<ToolItem> ) 
				ItemsFacade.getInstance().getItems( user );
		
		if ( tiList == null || tiList.size() == 0 ) {
			// Nothing to report!
			return document;
		}
		
		normalizeToolItems( tiList );
		

		document.printHeader( user );
		
		// Fill document
//		document.addSubheader();

		document.printList( tiList );
//		document.printBody();		
				
		document.printFooter();
		
		return document;
		
	}
	
	private void normalizeToolItems( List<ToolItem> tiList ) {

		// Sort according to name
		Collections.sort( tiList, new Comparator<ToolItem>() {
			@Override
			public int compare( ToolItem arg0, ToolItem arg1 ) {
				String s0 = arg0.getFullName();
				String s1 = arg1.getFullName();

				if ( s0 == null || s0.length() == 0 ) {
					return -1;
				} else if ( s1 == null || s1.length() == 0 ) {
					return 1;
				} else {
					return s0.trim().toLowerCase().compareTo( s1.trim().toLowerCase() );
				}
			}

		});
		
		
		
	}
	
	
}
