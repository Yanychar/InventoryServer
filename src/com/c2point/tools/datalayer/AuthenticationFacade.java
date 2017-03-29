package com.c2point.tools.datalayer;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.InventoryUI;
import com.c2point.tools.entity.authentication.Account;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.transactions.TransactionOperation;
import com.vaadin.ui.UI;

public class AuthenticationFacade extends DataFacade {
	private static Logger logger = LogManager.getLogger( AuthenticationFacade.class.getName()); 

	private static int						MAX_INSTANCE_NUMBER = 4;
	private static AuthenticationFacade []	instances;
	private static int						next_instance_number;

	public static AuthenticationFacade getInstance() {
		
		if ( instances == null ) {
			instances = new AuthenticationFacade[ MAX_INSTANCE_NUMBER ];
			for ( int i = 0; i < MAX_INSTANCE_NUMBER; i++ ) {
				instances[ i ] = new AuthenticationFacade();  
			}
			next_instance_number = 0;
			
		}
		
		AuthenticationFacade ret = instances[ next_instance_number ];
		if ( logger.isDebugEnabled()) 
			logger.debug( "AuthenticationFacade instance number retirned is " + next_instance_number + " from " + MAX_INSTANCE_NUMBER + " available!" );
		
		next_instance_number = ++next_instance_number % MAX_INSTANCE_NUMBER ;
		
		return ret;
	}

	public Account authenticateUser( String usrName, String pwd ) {
		return authenticateUser( usrName, pwd, null, null );
	}
	
	public Account authenticateUser( String usrName, String pwd, String version, String imei ) {
		Account account = null;
		
		// UserName and Pwd shall be not empty
		if ( usrName != null && pwd != null ) {
			
			// Finds account by username
			account = findByUserName( usrName.trim().toLowerCase()); 
			
			if ( account != null && account.valid()) {
				logger.debug( "Account and User found" );

				if ( account.getPwd().compareTo( pwd ) == 0 ) {
					account.setUniqueSessionID();

					account = merge( account );
					
				} else {
					if ( logger.isDebugEnabled())
						logger.debug( account + " has different pwd than '" + pwd + "'" );
					account = null;
				}
					
			} else {
				if ( logger.isDebugEnabled())
					logger.debug( "No account or User with User Name: '" + usrName + "' found!" );
			}
		} else {
			logger.error( "Account cannot have NULL usrname or pwd. UsrName=" + usrName + ", pwd=" + pwd );
		}
		
		if ( account == null ) {
			if ( logger.isDebugEnabled()) logger.debug( "'" + usrName + "' not authenticated!" );
		}
		
		return account;
	}
	
	public boolean logout( Account account, boolean bAutomatic ) {
		boolean bRes = false;
		
		// Set status logged = OFF
		account.closeSession();

		account = merge( account );
		if ( logger.isDebugEnabled()) logger.debug( "Session for " + account.getUser() + " closed!" );
		
		if ( account != null )
			TransactionsFacade.getInstance().writeLogout( account.getUser());
		
		bRes = true;
		
		return bRes;
	}
	
	public Account findBySessionId( String sessionId ) {
		Account account;
		
		EntityManager em = createEntityManager();
		try {
			// Fetched Account with specify UserName. Should be one account only!!!  
			TypedQuery<Account> q = em.createNamedQuery( "findAccountBySessionId", Account.class )
					.setParameter("sessionId", sessionId );
			account = q.getSingleResult();
		} catch ( NoResultException e ) {
			account = null;
			logger.debug( "Not found: NoResultException for sessionId: '" + sessionId + "'" );
		} catch ( NonUniqueResultException e ) {
			account = null;
			logger.error( "It should be one account only for sessionId: '" + sessionId + "'" );
			
			TypedQuery<Account> q2 = em.createNamedQuery( "findAccountBySessionId", Account.class )
					.setParameter("sessionId", sessionId );
			List<Account> lst = q2.getResultList();
			logger.debug( "Find by sessionID size = " + lst.size());
			for ( Account a : lst ) {
				logger.debug( "Account[ id, name, pwd ]: " + a.getId() + ", " + a.getUsrName() + ", " + a.getPwd() );
			}
			
	
			
			
			
		} catch ( Exception e ) {
			account = null;
			logger.error( e );
		} finally {
			em.close();
		}
		
		return account;
	}
	
	public Account addAccount( String usrName, String pwd, OrgUser user ) {
		

		if ( usrName == null || usrName.length() == 0 )
			throw new IllegalArgumentException ( "User Name cannot be null or emptyl!" );
		if ( pwd == null || pwd.length() == 0 )
			throw new IllegalArgumentException( "Password cannot be null or empty!" );
		if ( user == null )
			throw new IllegalArgumentException( "Valid OrgUser cannot be null!" );

		// Convert to Lower Case usrName to be case insensitive
		if ( usrName != null )
			usrName = usrName.trim().toLowerCase();
		
		Account account = new Account( usrName, pwd, user ); 
		
		
		try {
			account = insert( account );
			
			TransactionsFacade.getInstance().writeAccount( 
					(( InventoryUI )UI.getCurrent()).getSessionOwner(), 
					user, 
					TransactionOperation.ADD );
			
		} catch ( Exception e) {
			logger.error( "Cannot add account\n" + e );
		}
		
		if ( logger.isDebugEnabled())
				logger.debug( "New Account was added: " + account );

		return account;
		
	}

	public Account deleteAccount( OrgUser user ) {
		
		Account existingAccount = null;

		if ( user == null )
			throw new IllegalArgumentException( "Valid User cannot be null!" );

//		existingAccount = findByUserId( user ); 
		existingAccount = user.getAccount(); 
		
		try {
			remove( existingAccount );
			
			TransactionsFacade.getInstance().writeAccount( 
					(( InventoryUI )UI.getCurrent()).getSessionOwner(), 
					user, 
					TransactionOperation.DELETE );
			
		} catch ( Exception e) {
			logger.error( "Cannot remove account\n" + e );
			
			return null;
		}
		
		if ( logger.isDebugEnabled())
				logger.debug( "Account has been deleted: " + existingAccount );

		return existingAccount;
		
	}

	/*
	private void writeTransaction( Transaction tr ) {
		tr = insert( tr );

		if ( logger.isDebugEnabled()) logger.debug( tr );
	}
*/	

	public Account findByUserName( String usrName ) {
		Account account;
		
		EntityManager em = createEntityManager();
		try {
			// Fetched Account with specify UserName. Should be one account only!!!  
			TypedQuery<Account> q = em.createNamedQuery( "findAccountByUsrName", Account.class )
					.setParameter("usrName", usrName );
			account = q.getSingleResult();
		} catch ( NoResultException e ) {
			account = null;
			if ( logger.isDebugEnabled())
				logger.debug( "Account Not Found for usrName: '" + usrName + "'" );
		} catch ( NonUniqueResultException e ) {
			account = null;
			logger.error( "It should be one account only for usrName: '" + usrName + "'" );
		} catch ( Exception e ) {
			account = null;
			logger.error( e );
		} finally {
			em.close();
		}
		
		return account;
	}

	
	/*
	 *  Check usrname from account
	 * Return:
	 *	0 - account with such usrName exists and it is the same
	 *  1 - account with such usrName exists but other than checked
	 * -1 - account with such usrName does not exist
	 * 
	 */
	public int checkAccountName( Account account ) {
		
		int iRes = -1;
		
		Account existedAccount = findByUserName( account.getUsrName());
		
		if ( existedAccount == null  ) {
			// Nothing was found. No account with such name
			logger.debug( "Account with name '" + account.getUsrName() + "' does NOT exist" );
			iRes = -1;
			
		} else if ( existedAccount.getId() == account.getId() && existedAccount.getId() > 0 ) {
			// If id the same than account is the same 
			logger.debug( "Account with name '" + account.getUsrName() + "' found" );
			iRes = 0;
			
		} else {
			// Account found. But it is different from other. Need to be used or new name to use
			logger.debug( "Account with name '" + account.getUsrName() + "' found. But it is other than we have" );
			iRes = 1;
			
		}
			
			
			


		
		return iRes;
	}
	
	public String getModifiedName( String usrName ) {
		
		String ext = "";
		int count = 1;
		
		while( AuthenticationFacade.getInstance().findByUserName( usrName.concat( ext )) != null ) {

			ext = "." + Integer.toString( count++ );
			
		};
		
		logger.debug( "Normalized name (AFTER checking for existance): " + usrName.concat( ext ) );

		
		return usrName.concat( ext );
	}


	
}

