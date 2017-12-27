package com.c2point.tools.entity.authentication;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.person.OrgUser;

public class ActiveSessions extends HashMap<String, Session> {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( ActiveSessions.class.getName());

	private static long LIVE_SESSION_PERIOD = 12*60*60*1000; // 12 hours in milliseconds
	
	private static ActiveSessions sessionsList = null;
	
	public static ActiveSessions getActiveSessions() {
		
		if ( sessionsList == null ) {
			
			sessionsList = new ActiveSessions(); 
		}
		
		return sessionsList;
	}
	
	private ActiveSessions() {
		super( );
		// TODO Auto-generated constructor stub
	}

	public Session findSession( String sessionId ) {
		
		closeOldSessions();
		
		return this.get( sessionId );
	}
	
	public Session addSession( OrgUser user ) {
		
		Session session = new Session( user ); 

		this.put( session.getUniqueSessionID(), session );
	
		return session;
	}
	
	// When sessions were checked last time for old ones
	private long lastTimeChecked = new Date().getTime();
	
	public void closeOldSessions() {
		
		// Firstly check that if was not executed lately
		long now = new Date().getTime(); 
		if (( now - lastTimeChecked ) > LIVE_SESSION_PERIOD ) {

			// Now time to remove old sessions
			
			// Store execution time 
			lastTimeChecked = now;
		
		
			Session session; 
			Iterator<Entry<String, Session>> iter = this.entrySet().iterator();
			while( iter.hasNext()) {
				session = iter.next().getValue();
				if (( now - session.getLastAccessed().getTime()) > LIVE_SESSION_PERIOD ) {
					iter.remove();
				}
				
			}
		}
	}
}
