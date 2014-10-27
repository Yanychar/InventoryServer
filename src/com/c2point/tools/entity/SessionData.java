package com.c2point.tools.entity;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.access.SecurityContext;
import com.c2point.tools.entity.organisation.Organisation;
import com.c2point.tools.entity.person.OrgUser;


public class SessionData {

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( SessionData.class.getName());

	private OrgUser			orgUser;
	private SecurityContext	context; 
	
	private Locale			locale;
	private ResourceBundle	bundle;
	
	public SessionData() {
		
		
	}

	public OrgUser getOrgUser() { return orgUser; }
	public void setOrgUser( OrgUser orgUser ) { 

		this.orgUser = orgUser; 
		
	}
	
	public Organisation getOrg() { return getOrgUser().getOrganisation(); }

	public SecurityContext getContext() { return context; }
	public void setContext( SecurityContext context ) { this.context = context; }

	public Locale getLocale() {
		return locale;
	}

	public void setLocale( Locale locale ) {
		this.locale = locale;
	}

	public ResourceBundle getBundle() {
		return bundle;
	}

	public void setBundle( ResourceBundle bundle ) {
		this.bundle = bundle;
	}



}
