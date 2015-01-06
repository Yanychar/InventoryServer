package com.c2point.tools.entity.transactions;

import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum TransactionType {

	LOGIN,
	ACCOUNT,
	USER,
	CATEGORY,
	TOOL,
	TOOLITEM;

	private static Logger logger = LogManager.getLogger( TransactionType.class.getName());
	
    public String toString( ResourceBundle bundle ) {
    	
    	try {
    		return bundle.getString( "transaction.type." + this.toString().toLowerCase());
    	} catch ( Exception e ) {
    		logger.error( "Resource string for '" + "transaction.type." + this.toString().toLowerCase() + "' was not found");
    	}
    	
    	return this.toString();
    }
	
}
