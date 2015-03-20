package com.c2point.tools.email;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import com.c2point.tools.datalayer.SettingsFacade;

public class EmailConnectorFactory {
	
	public EmailConnectorFactory() {

	}

	public static Session createSession() {
		
		Session session = null;
		
		Properties props = new Properties();
		
		props.put( "mail.smtp.auth", 			SettingsFacade.getInstance().getSystemProperty( "email.smtp.auth" ));
        props.put( "mail.smtp.starttls.enable", SettingsFacade.getInstance().getSystemProperty( "email.smtp.starttls.enable" ));
        props.put( "mail.smtp.host", 			SettingsFacade.getInstance().getSystemProperty( "email.smtp.host" ));
        props.put( "mail.smtp.port", 			SettingsFacade.getInstance().getSystemProperty( "email.smtp.port" ));

        
		if ( props != null ) {

			Authenticator auth = new javax.mail.Authenticator() {
    			protected PasswordAuthentication getPasswordAuthentication() {
    							return new PasswordAuthentication( 
    									SettingsFacade.getInstance().getSystemProperty( "email.smtp.user" ), 
    									SettingsFacade.getInstance().getSystemProperty( "email.smtp.pwd" )
    							);
    			}
			};
			
	        session = Session.getInstance( props, auth );
			
		}

		return session;
	}
	
	
}
