package com.c2point.tools.entity.transactions;

import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum TransactionOperation {

	ON,
	OFF,
	ADD,
	EDIT,
	DELETE,
	NEWSTATUS,
	USERCHANGED;
	
	private static Logger logger = LogManager.getLogger( TransactionOperation.class.getName());

	public String toString( ResourceBundle bundle ) {
    	
    	try {
    		return bundle.getString( "transaction.operation." + this.toString().toLowerCase());
    	} catch ( Exception e ) {
    		logger.error( "Resource string for '" + "transaction.operation." + this.toString().toLowerCase() + "' was not found");
    	}
    	
    	return this.toString();
    }
	
	
}
