package com.c2point.tools.email;


public abstract class Message {

	protected String receiver;
	protected String body;

	protected Message( String receiver ) {
		
		setReceiver( receiver );
		
	}

	
	public String getReceiver() { return receiver; }
	public void setReceiver(String receiver) { this.receiver = receiver; }

	public String getBody() { return body; }
	public void setBody(String body) { this.body = body; }
	

}
