package com.c2point.tools.email;

import java.io.StringWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.SettingsFacade;

public class FailedToSendMessage extends Message {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( FailedToSendMessage.class.getName());

//	private OrgUser	user;
	
	public FailedToSendMessage( Message msg ) {
		super( "" );
		
		
		setReceiver( SettingsFacade.getInstance().getSystemProperty( "email.service_error.address" ));
		
		createSubject( msg );
		createBody( msg );
	}

	protected void createSubject( Message msg ) {
		
        setSubject( 
        		"Mobile InventTori Service: Failed to send message"
        );
	}
	
	protected void createBody( Message msg ) {
		
        StringWriter messageBody = new StringWriter();
        
        messageBody.write( "Mobile InventTori Service failed to send email:\n" );
        messageBody.write( "  To: " + msg.getReceiver() + "\n" );
        messageBody.write( "  Subject: " + msg.getSubject() + "\n" );
        messageBody.write( "  Content: " + msg.getBody() + "\n" );
        
        messageBody.write( "\n\n" );

        setBody( messageBody.toString());
        
	}
	
}