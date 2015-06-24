package com.c2point.tools.entity.transactions;

import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;

public class TransactionToResourceEncoder {

	private static String [][] resourceConversionTable = {
		{ "LOGIN", "ON", "trn.login" },
		{ "LOGIN", "OFF", "trn.logout" },

		{ "ACCOUNT", "ADD", "trn.account.add" },
		{ "ACCOUNT", "EDIT", "trn.account.edit" },
		{ "ACCOUNT", "DELETE", "trn.account.delete" },
		
		{ "USER", "ADD", "trn.user.add" },
		{ "USER", "EDIT", "trn.user.edit" },
		{ "USER", "DELETE", "trn.user.delete" },
		
		{ "ORGANISATION", "ADD", "trn.org.add" },
		{ "ORGANISATION", "EDIT", "trn.org.edit" },
		{ "ORGANISATION", "DELETE", "trn.org.delete" },

		{ "CATEGORY", "ADD", "trn.cat.add" },
		{ "CATEGORY", "EDIT", "trn.cat.edit" },
		{ "CATEGORY", "DELETE", "trn.cat.delete" },

		{ "TOOL", "ADD", "trn.tool.add" },
		{ "TOOL", "EDIT", "trn.tool.edit" },
		{ "TOOL", "DELETE", "trn.tool.delete" },

		{ "ACCESSRIGHTS", "ADD", "trn.access.add" },
		{ "ACCESSRIGHTS", "EDIT", "trn.access.edit" },
		{ "ACCESSRIGHTS", "DELETE", "trn.access.delete" },
		
		{ "TOOLITEM", "ADD", "trn.item.add" },
		{ "TOOLITEM", "EDIT", "trn.item.edit" },
		{ "TOOLITEM", "DELETE", "trn.item.delete" },
		{ "TOOLITEM", "NEWSTATUS", "trn.item.newstatus" },
		{ "TOOLITEM", "USERCHANGED", "trn.item.transfer" },
		
	};
	
	private static Map<String, String> resourceIDMap = new HashMap<String, String>(); 
	static {
		for ( String [] item : resourceConversionTable ) {
			
//			System.out.println( item[0] + "." + item[1] );
	
			resourceIDMap.put( item[0] + "." + item[1], item[2] );
			
			
		}
	}
	
	
	public static String getStringFromTypeAndOperation( BaseTransaction trn, boolean shortForm ) {
		
		String key = trn.getTrnType() + "." + trn.getTrnOperation();
		String res = resourceIDMap.get( key );
		
		if ( res == null ) {
		
			throw new MissingResourceException( "", "String", key );
		}
		
		// If long form specified than add '.more' to the resource key
		if ( !shortForm ) {
			
			res = res.concat( ".more" );
		}
		
		return res;
	}
	

}
