package com.c2point.tools.datalayer;

import java.util.Collection;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.organisation.Organisation;
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
	
	public boolean writeAccessRights( OrgUser whoDid, OrgUser whoisRights, TransactionOperation op ) {
		
		BaseTransaction trn = new BaseTransaction( whoDid, TransactionType.ACCESSRIGHTS, op );
		
		trn.setSourceUser( whoisRights );
		
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
		trn.setNewStatus( item.getStatus());
		
		return write( trn ); 
	}
	
	/*
	 * Transaction keeps: who made ownership transfer, tool, old user, new user 
	 */
	public boolean writeToolItemUserChanged( OrgUser whoDid, ToolItem item, OrgUser oldUser, OrgUser newUser ) {
		
		BaseTransaction trn = new BaseTransaction( whoDid, TransactionType.TOOLITEM, TransactionOperation.USERCHANGED );
		
		trn.setToolItem(  item );
		trn.setSourceUser( oldUser );
		trn.setDestUser( newUser );
		
		return write( trn ); 
	}
	
	/*
	 * Transaction keeps: who made changes, tool, operation 
	 */
	public boolean writeManufacturer( OrgUser whoDid, String manName, TransactionOperation op ) {
		
		BaseTransaction trn = new BaseTransaction( whoDid, TransactionType.MANUFACTURER, op );
		
		trn.setMessage( StringUtils.defaultString( manName ));
		
		return write( trn ); 
	}


	public boolean writeToolItem( OrgUser whoDid, ToolItem item, TransactionOperation op ) {
		
		BaseTransaction trn = new BaseTransaction( whoDid, TransactionType.TOOLITEM, op );
		
		trn.setToolItem(  item );
		
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

	public boolean writeOrg( OrgUser whoDid, Organisation org, TransactionOperation op ) {
		
		BaseTransaction trn = new BaseTransaction( whoDid, TransactionType.ORGANISATION, op );
		
		trn.setOrg( org );
		
		return write( trn ); 
	}

	
/*
 * Below methods to fetch transactions according to the particular criterias
 */
	public Collection<BaseTransaction> getTransactions( OrgUser user, Date dateStart, Date dateEnd ) {
		
		if ( user == null || user.getOrganisation() == null ) {
			throw new IllegalArgumentException( "Valid User and User.organisation cannot be null!" );
		}
		if ( dateStart == null ) {
			dateStart = new Date( new Date().getTime() - 1000 * 60*60*24 * 30 * 6 );
		}
		if ( dateEnd == null ) {
			dateEnd = new Date( new Date().getTime() + 1000 * 60*60*24 );
		}


		
		Collection<BaseTransaction> results = null;
		
		EntityManager em = DataFacade.getInstance().createEntityManager();
		TypedQuery<BaseTransaction> query = null;
		String queryName = "listTransactionsForUser";
		
		
		try {
			query = em.createNamedQuery( queryName, BaseTransaction.class )
						.setParameter( "user", user )
						.setParameter( "org", user.getOrganisation())
						.setParameter( "startDate", dateStart, TemporalType.DATE )
						.setParameter( "endDate", dateEnd, TemporalType.DATE );
			
			results = query.getResultList();
			if ( logger.isDebugEnabled()) logger.debug( "**** Fetched list of Transactions. Size = " + results.size());
		} catch ( NoResultException e ) {
			if ( logger.isDebugEnabled()) logger.debug( "No Transactions found!" );
		} catch ( Exception e ) {
			results = null;
			logger.error( e );
		} finally {
			em.close();
			releaseInstance();
		}

		
		return results;
		
	}

	public Collection<BaseTransaction> getTransactions( Tool tool, Date dateStart, Date dateEnd ) {
		
		if ( tool == null  ) {
			throw new IllegalArgumentException( "Valid Tool cannot be null!" );
		}
		if ( dateStart == null ) {
			dateStart = new Date( new Date().getTime() - 1000 * 60*60*24 * 30 * 6 );
		}
		if ( dateEnd == null ) {
			dateEnd = new Date( new Date().getTime() + 1000 * 60*60*24 );
		}


		
		Collection<BaseTransaction> results = null;
		
		EntityManager em = DataFacade.getInstance().createEntityManager();
		TypedQuery<BaseTransaction> query = null;
		String queryName = "listTransactionsForTool";
		
		
		try {
			query = em.createNamedQuery( queryName, BaseTransaction.class )
						.setParameter( "tool", tool )
						.setParameter( "org", tool.getOrg())
						.setParameter( "startDate", dateStart, TemporalType.DATE )
						.setParameter( "endDate", dateEnd, TemporalType.DATE );
			
			results = query.getResultList();
			if ( logger.isDebugEnabled()) logger.debug( "**** Fetched list of Transactions. Size = " + results.size());
		} catch ( NoResultException e ) {
			if ( logger.isDebugEnabled()) logger.debug( "No Transactions found!" );
		} catch ( Exception e ) {
			results = null;
			logger.error( e );
		} finally {
			em.close();
			releaseInstance();
		}

		
		return results;
		
	}

	/*
	 * Below methods to fetch all transactions for Transaction View
	 */
	public Collection<BaseTransaction> getTransactions( Organisation org, Date dateStart, Date dateEnd ) {
		
		if ( org == null ) {
			throw new IllegalArgumentException( "Valid Organisation cannot be null!" );
		}
		if ( dateStart == null ) {
			dateStart = new Date( new Date().getTime() - 1000 * 60*60*24 * 30 * 6 );
		}
		if ( dateEnd == null ) {
			dateEnd = new Date( new Date().getTime() + 1000 * 60*60*24 );
		}


		Collection<BaseTransaction> results = null;
		
		EntityManager em = DataFacade.getInstance().createEntityManager();
		TypedQuery<BaseTransaction> query = null;
		String queryName = "listTransactionsForOrg";
		
		
		try {
			query = em.createNamedQuery( queryName, BaseTransaction.class )
						.setParameter( "org", org )
						.setParameter( "startDate", dateStart, TemporalType.DATE )
						.setParameter( "endDate", dateEnd, TemporalType.DATE );
			
			results = query.getResultList();
			if ( logger.isDebugEnabled()) logger.debug( "**** Fetched list of Transactions. Size = " + results.size());
		} catch ( NoResultException e ) {
			if ( logger.isDebugEnabled()) logger.debug( "No Transactions found!" );
		} catch ( Exception e ) {
			results = null;
			logger.error( e );
		} finally {
			em.close();
			releaseInstance();
		}

		return results;
		
	}


}
