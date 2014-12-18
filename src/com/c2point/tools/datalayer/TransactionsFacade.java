package com.c2point.tools.datalayer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Tool;
import com.c2point.tools.entity.transactions.BaseTransaction;
import com.c2point.tools.entity.transactions.TransactionOperation;
import com.c2point.tools.entity.transactions.TransactionType;

public class TransactionsFacade extends DataFacade {

	private static Logger logger = LogManager.getLogger( TransactionsFacade.class.getName()); 

	private static int						MAX_INSTANCE_NUMBER = 4;
	private static TransactionsFacade []	instances;
	private static int						next_instance_number;
	
	private boolean							instanceFree;
	
	public static TransactionsFacade getInstance() {
		
		if ( instances == null ) {
			instances = new TransactionsFacade[ MAX_INSTANCE_NUMBER ];
			for ( int i = 0; i < MAX_INSTANCE_NUMBER; i++ ) {
				instances[ i ] = new TransactionsFacade();  
			}
			next_instance_number = 0;
			
		}
		
		TransactionsFacade ret = instances[ next_instance_number ];
		if ( logger.isDebugEnabled()) logger.debug( "ItemsFacade instance number retirned is " + next_instance_number + " from " + MAX_INSTANCE_NUMBER + " available!" );
		
		next_instance_number = ++next_instance_number % MAX_INSTANCE_NUMBER ;

		if ( !ret.isFreeInstance() ) { 
			ret = getInstance();
		} else {
			
			ret.reserveInstance();
		}
		
		return ret;
	}
	
	private TransactionsFacade() {
		super();
		
		releaseInstance();
	}
	
	private boolean isFreeInstance() { return this.instanceFree; } 
	private void reserveInstance() { this.instanceFree = false; } 
	private void releaseInstance() { this.instanceFree = true; } 
	/*
	 * Below methods for Transaction objects manipulation
	 */
	
	public boolean write( BaseTransaction trn ) {

		boolean res = false;
		
		if ( trn != null ) {
		
			if ( DataFacade.getInstance().insert( trn ) != null ) {
				res = true;
			} else {
				logger.error( "Failed to add Transaction: " + trn );
			}
		} else {
			logger.error( "Valid Transaction to add cannot be null!" );
		}
		
		releaseInstance();
		
		return res;
		
	}
	
	public boolean writeLogin( OrgUser user ) {
		
		return write( new BaseTransaction( user, TransactionType.LOGIN, TransactionOperation.ON )); 
	}
	
	public boolean writeLogout( OrgUser user ) {
		
		return write( new BaseTransaction( user, TransactionType.LOGIN, TransactionOperation.OFF )); 
	}
	
	public boolean writeAccount( OrgUser whoDid, OrgUser accountOwner, TransactionOperation op ) {
		
		BaseTransaction trn = new BaseTransaction( whoDid, TransactionType.ACCOUNT, op );
		
		trn.setSourceUser( accountOwner );
		
		return write( trn ); 
		
	}
	
	public boolean writeCategory( OrgUser whoDid, TransactionOperation op ) {
		
		return write( new BaseTransaction( whoDid, TransactionType.CATEGORY, op )); 
	}
	
	/*
	 * Transaction keeps: who changed status, tool, new status of tool 
	 */
	public boolean writeToolItemStatus( OrgUser whoDid, ToolItem item ) {
		
		BaseTransaction trn = new BaseTransaction( whoDid, TransactionType.TOOLITEM, TransactionOperation.NEWSTATUS );
		
		trn.setToolItem(  item );
		trn.setTool( item.getTool());
		trn.setNewStatus( item.getStatus());
		
		return write( trn ); 
	}
	
	/*
	 * Transaction keeps: who made ownership transfer, tool, old user, new user 
	 */
	public boolean writeToolItemUserChanged( OrgUser whoDid, ToolItem item, OrgUser oldUser, OrgUser newUser ) {
		
		BaseTransaction trn = new BaseTransaction( whoDid, TransactionType.TOOLITEM, TransactionOperation.NEWUSER );
		
		trn.setToolItem(  item );
		trn.setTool( item.getTool());
		trn.setSourceUser( oldUser );
		trn.setDestUser( newUser );
		
		return write( trn ); 
	}
	
	/*
	 * Transaction keeps: who made changes, tool, operation 
	 */
	public boolean writeToolItem( OrgUser whoDid, ToolItem item, TransactionOperation op ) {
		
		BaseTransaction trn = new BaseTransaction( whoDid, TransactionType.TOOLITEM, op );
		
		trn.setToolItem(  item );
		trn.setTool( item.getTool());
		
		return write( trn ); 
	}

	public boolean writeTool( OrgUser whoDid, Tool tool, TransactionOperation op ) {
		
		BaseTransaction trn = new BaseTransaction( whoDid, TransactionType.TOOL, op );
		
		trn.setTool( tool );
		
		return write( trn ); 
	}

	public boolean writeUser( OrgUser whoDid, OrgUser user, TransactionOperation op ) {
		
		BaseTransaction trn = new BaseTransaction( whoDid, TransactionType.USER, op );
		
		trn.setSourceUser( user );
		
		return write( trn ); 
	}

	
}
