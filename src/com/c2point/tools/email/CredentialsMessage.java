package com.c2point.tools.email;

import java.io.StringWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.person.OrgUser;

public class CredentialsMessage extends Message {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( CredentialsMessage.class.getName());

//	private OrgUser	user;
	
	public CredentialsMessage( OrgUser receiver, OrgUser user ) {
		super( receiver.getEmail());
		
//		setUser( user );
		
		createBody( receiver, user );
	}

	
	protected void createBody( OrgUser receiver, OrgUser user ) {
		
        StringWriter messageBody = new StringWriter();
        
        messageBody.write( "Hello\n\n" );
        messageBody.write( "We have received the request for credentials to access Mobile InventTori service.\n" );
        
        if ( receiver.getId() != user.getId()) {
        	// Credentials for other person will be sent
            messageBody.write( "\n" );
            messageBody.write( "Your password is: " + user.getAccount().getPwd());
        } else {
        	// Credentials for person himself will be sent
            messageBody.write( "\n" );
            messageBody.write( "Password for user '" + user.getLastAndFirstNames() + " is: " + user.getAccount().getPwd());
        }
        
        
        
        messageBody.write( "\n\n" );
        
	}
	
}