package com.c2point.tools.email;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.SettingsFacade;
import com.c2point.tools.entity.person.OrgUser;

public class EmailSender {
	private static Logger logger = LogManager.getLogger( EmailSender.class.getName());

	private static EmailSender	instance = null;
	
	private Queue<Message>	msgQueue = new ArrayBlockingQueue<Message>( 20 );
	private boolean			sendRunning = false;

	private final Lock 		queueBusyLock = new ReentrantLock();
	
    private static final String APPLICATION_EMAIL_BODY_START = "\n";
    private static final String APPLICATION_EMAIL_BODY_END = "\n\n(This is an automated message. Please do not reply)";	
	
	public static EmailSender getInstance() {
		
		if ( instance == null ) {
			
			instance = new EmailSender(); 
		}
		
		return instance;
	}
	
	
	private EmailSender() {
		
	}
	
	public boolean sendCredentialsMsg( OrgUser receiver, OrgUser user ) {
		
		store( new CredentialsMessage( receiver, user ));
		
		startToSend();
		
		
		return true;
	}

	private boolean store( Message msg ) {
		
		boolean bRes = false;
		
		try {
			queueBusyLock.lock();

			bRes = msgQueue.offer( msg );
			
		} finally {
			queueBusyLock.unlock();
		}
		
    	return bRes;
	}
	
	private void startToSend() {
		
		if ( !sendRunning ) new SendingThread().start(); 
	}

	public class SendingThread extends Thread {

	    public void run() {
	    	
	    	sendRunning = true;
    		Message msg;

			init();
    		
	    	while ( msgQueue.size() > 0 ) {
	    		
	    		if ( queueBusyLock.tryLock()) {
		    		try {
		    			
			    		msg = msgQueue.poll();
		    			
		    		} catch ( Exception e ) {
		    			msg = null;
		    		} finally {
		    			queueBusyLock.unlock();
		    		}
		    		
		    		if ( msg != null ) {
		    			
		    			if ( !send( msg )) {
		    				send( new FailedToSendMessage( msg ));
		    			}
		    		}

	    		} else {
	    			// Queue is busy
	    			// Wait 0.5 sec and try again
	    			try {
						Thread.sleep( 500 );
					} catch (InterruptedException e) {
					}
	    			
	    		}
		    	
	    		if ( msgQueue.size() == 0 ) {
	    			
	    			try {
	    				Thread.sleep( 5000 );
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
	    		}
	    	}

	    	close();
	    	sendRunning = false;	    	
	    	
	    }

		private boolean send( Message msg ) {

			boolean bRes = false;

/*
			
			long l = new Date().getTime();
			bRes = ( l % 5 ) != 0;
			
			logger.debug( "Message to '" 
						+ msg.getReceiver() 
						+ "' was " 
						+ (bRes ? "" : "NOT ") 
						+ "sent ( " + l + " )" );
			
*/
			
	        try {
	            MimeMessage message = new MimeMessage(session);
	            message.setFrom( new InternetAddress( SettingsFacade.getInstance().getSystemProperty( "email.noreply.address" )));
	            message.setRecipients( RecipientType.TO, InternetAddress.parse( msg.getReceiver()));
	            message.setSubject( msg.getSubject());
	            message.setSentDate(new Date());
	            message.setText( APPLICATION_EMAIL_BODY_START + msg.getBody() + APPLICATION_EMAIL_BODY_END);
	            Transport.send(message);
	            logger.debug( "Send message successfully!" );
	            
	            bRes = true;
	        } catch (MessagingException ex) {
	            // Exception during exception handling. Just log it.
	            logger.error( "Exception when sending email: " + ex );
	        }			
			
			return bRes;
		}

	}
	
    Session session;
	private void init() {
		
		session = EmailConnectorFactory.createSession();
				
    }
	
	private void close() {
		
	}
	
	
}
