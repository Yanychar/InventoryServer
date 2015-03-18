package com.c2point.tools.email;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import com.c2point.tools.entity.person.OrgUser;

public class EmailSender {

	
	private Queue<Message> msgQueue = new ArrayBlockingQueue<Message>( 20 );
	
	private EmailSender() {
		
	}
	
	public static boolean sendCredentialsMsg( OrgUser receiver, OrgUser user ) {
		
		return EmailSender.send( new CredentialsMessage( receiver, user ) );
		
		
		
	}

	
	
	private void sendMessagesFromTheQueue() {
		
	}

	
	public class SendingThread extends Thread {

	    public void run() {
	    	
	    	while ( msgQueue.size() > 0 ) {
	    		
	    		Message msg = msgQueue.poll();
	    		
	    		if ( msg != null ) {
	    			
	    			send( msg );
	    		}
	    	}
	    }

		private boolean send( Message msg ) {

			boolean bRes = false;

			init();
			
			
			close();
			
			return bRes;
		}

	}	
}
