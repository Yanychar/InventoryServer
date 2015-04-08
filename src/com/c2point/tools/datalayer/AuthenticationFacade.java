package com.c2point.tools.datalayer;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.InventoryUI;
import com.c2point.tools.entity.authentication.Account;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.transactions.TransactionOperation;
import com.vaadin.ui.UI;

public class AuthenticationFacade {
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

					account = DataFacade.getInstance().merge( account );
					
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

		account = DataFacade.getInstance().merge( account );
		if ( logger.isDebugEnabled()) logger.debug( "Session for " + account.getUser() + " closed!" );
		
		if ( account != null )
			TransactionsFacade.getInstance().writeLogout( account.getUser());
		
		bRes = true;
		
		return bRes;
	}
	
	public Account findBySessionId( String sessionId ) {
		Account account;
		
		EntityManager em = DataFacade.getInstance().createEntityManager();
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
			account = DataFacade.getInstance().insert( account );
			
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
			DataFacade.getInstance().remove( existingAccount );
			
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
		tr = DataFacade.getInstance().insert( tr );

		if ( logger.isDebugEnabled()) logger.debug( tr );
	}
*/	
	/*		
	public Account findByUserId( OrgUser user ) {
		
		Account account = null;
		
		EntityManager em = DataFacade.getInstance().createEntityManager();
		try {
			// Fetched Account with specify UserName. Should be one account only!!!  
			TypedQuery<Account> q = em.createNamedQuery( "findAccountByUsrId", Account.class )
					.setParameter("userId", user.getId() );
			account = q.getSingleResult();
		} catch ( NoResultException e ) {
			account = null;
			if ( logger.isDebugEnabled())
				logger.debug( "Account Not Found for OrgUser: '" + user.getFirstAndLastNames() + "'" );
		} catch ( NonUniqueResultException e ) {
			account = null;
			logger.error( "It should be one account only for OrgUser: '" + user.getFirstAndLastNames() + "'" );
		} catch ( Exception e ) {
			account = null;
			logger.error( e );
		} finally {
			em.close();
		}
		return account;
		
		
	}
*/		

	public Account findByUserName( String usrName ) {
		Account account;
		
		EntityManager em = DataFacade.getInstance().createEntityManager();
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

	public Account addAccountDefault( OrgUser user ) {
		if ( user == null )
			throw new IllegalArgumentException( "Valid OrgUser cannot be null!" );

		Account account; 

		// Find account record for the OrgUser
		if ( logger.isDebugEnabled()) logger.debug( "Try to find Account for OrgUser: '" + user.getFirstAndLastNames() + "'" );

//		account = findByUserId( user );
		account = user.getAccount(); 
		if ( account != null ) {
			if ( logger.isDebugEnabled()) logger.debug( "Account exists already. Not necessary to create" );
		} else {
			if ( logger.isDebugEnabled()) logger.debug( "Account does not exist. Must be created" );
			// Create FREE name and Default password password
			String accName = getFreeUserName( user.getLastName());
			if ( accName != null ) {
				
				// Necessary to add account
				String accPwd = Account.generateNewPassword();
				
				account = addAccount( accName, accPwd, user );
				if ( logger.isDebugEnabled()) logger.debug( "User account will be added: '" + accName + "'" );
			} else {
				if ( logger.isDebugEnabled()) logger.debug( "Cannot create new UserName!" );
			}
		
		}
		
		return account;
	}
	
	// TODO
	// Minimum and maxlength of User Name should be in company settings
	int min_num = 8;
	int max_num = 11;
	// TODO
	// Put prefix into the company settings
	final static String USRNAME_PREFIX = ""; //"fi";
	
	public String getFreeUserName( String usrName ) {
		String retName = null;
		
		String tmpName;
		if ( usrName != null ) {
			tmpName = USRNAME_PREFIX + usrName;
			tmpName = tmpName.toLowerCase();
			
			tmpName = StringUtils.replaceChars( tmpName, "ˆ÷‰ƒÂ≈", "ooaaaa" );
//			tmpName = StringUtils.replaceChars( tmpName, "‰ƒ", "a" );
//			tmpName = StringUtils.replaceChars( tmpName, "Â≈", "a" );
			
			if ( tmpName.length() < min_num ) {
				tmpName = tmpName.concat( "123456789" ).substring( 0, min_num );
			} else if ( tmpName.length() > max_num ) {
				tmpName = tmpName.substring( 0, max_num );
			}

			int i = 1;
			String searchName = new String ( tmpName );
			EntityManager em = DataFacade.getInstance().createEntityManager();
			TypedQuery<Account> q = em.createNamedQuery( "findAccountByUsrName", Account.class );
			while ( true ) {
				try {
					q.setParameter("usrName", searchName );
					// Fetched Account with specify UserName. Should be one account only!!!  
					q.getSingleResult();
				} catch ( NoResultException e ) {
					if ( logger.isDebugEnabled()) logger.debug( "Name '" + searchName + "' is free! Will be used" );
					retName = searchName;
					break;
				} catch ( NonUniqueResultException e ) {
					logger.error( "It should be one account only for usrName: '" + searchName + "'" );
				} catch ( Exception e ) {
					logger.error( e );
				}

				searchName = tmpName.concat( "." + i );
				i++;
				
				if ( i > 500 ) {
					logger.error( "Cannot create unique username. " + i + " attempts were made!" );
					searchName = null;
					break;
				}
				
			}
			em.close();
			
		}
		
		return retName;
	}

}

