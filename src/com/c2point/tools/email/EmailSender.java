package com.c2point.tools.email;

import com.c2point.tools.entity.person.OrgUser;

public class EmailSender {

	
	private ArrayList a;
	
	private EmailSender() {
		
	}
	
	public static boolean sendCredentialsMsg( OrgUser receiver, OrgUser user ) {
		
		return EmailSender.send( new CredentialsMessage( receiver, user ) );
		
	}

	private static boolean send( Message msg ) {

		boolean bRes = false;

		init();
		
		
		close();
		
		return bRes;
	}
	
	
	private void sendMessagesFromTheQueue() {
		
	}

}
