package com.c2point.tools.datalayer;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDate;

import com.c2point.tools.entity.msg.Message;
import com.c2point.tools.entity.msg.MessageStatus;
import com.c2point.tools.entity.msg.MessageType;
import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ToolItem;

public class MsgFacade {

	private static Logger logger = LogManager.getLogger( MsgFacade.class.getName()); 

	private static int					MAX_INSTANCE_NUMBER = 1;
	private static MsgFacade []			instances;
	private static int					next_instance_number;
	
	public static MsgFacade getInstance() {
		
		if ( instances == null ) {
			instances = new MsgFacade[ MAX_INSTANCE_NUMBER ];
			for ( int i = 0; i < MAX_INSTANCE_NUMBER; i++ ) {
				instances[ i ] = new MsgFacade();  
			}
			next_instance_number = 0;
			
		}
		
		MsgFacade ret = instances[ next_instance_number ];
		if ( logger.isDebugEnabled()) logger.debug( "MsgFacade instance number retirned is " + next_instance_number + " from " + MAX_INSTANCE_NUMBER + " available!" );
		
		next_instance_number = ++next_instance_number % MAX_INSTANCE_NUMBER ;
		
		return ret;
	}
	
	public List<Message> list( OrgUser user  ) {

		if ( user == null )
			throw new IllegalArgumentException( "Valid OrgUser cannot be null!" );

		EntityManager em = DataFacade.getInstance().createEntityManager();
		TypedQuery<Message> query = null;
		List<Message> results = null;
		
		try {
			query = em.createNamedQuery( "listAllMsgForUser", Message.class )
							.setParameter( "user", user );

			results = query.getResultList();
			if ( logger.isDebugEnabled()) logger.debug( "**** Fetched list of Messages. Size = " + results.size());
			
		} catch ( NoResultException e ) {
			if ( logger.isDebugEnabled()) logger.debug( "No Messages found!" );
		} catch ( Exception e ) {
			results = null;
			logger.error( e );
		} finally {
			em.close();
		}
		
		return results;
	}
	
	public List<Message> list( OrgUser user,  MessageStatus status ) {
		
		if ( user == null )
			throw new IllegalArgumentException( "Valid OrgUser cannot be null!" );

		EntityManager em = DataFacade.getInstance().createEntityManager();
		TypedQuery<Message> query = null;
		List<Message> results = null;
		
		try {
			query = em.createNamedQuery( "listMsgWithStatus", Message.class )
					.setParameter( "user", user )
					.setParameter( "status", status );

			results = query.getResultList();
			if ( logger.isDebugEnabled()) logger.debug( "**** Fetched list of Messages. Size = " + results.size());
			
		} catch ( NoResultException e ) {
			if ( logger.isDebugEnabled()) logger.debug( "No Messages found!" );
		} catch ( Exception e ) {
			results = null;
			logger.error( e );
		} finally {
			em.close();
		}
		
		return results;
	}
	
	public List<Message> listLatest( OrgUser user,  LocalDate date ) {
		
		if ( user == null )
			throw new IllegalArgumentException( "Valid OrgUser cannot be null!" );
		
		if ( date == null ) date = LocalDate.now();

		EntityManager em = DataFacade.getInstance().createEntityManager();
		TypedQuery<Message> query = null;
		List<Message> results = null;
		
		try {
			query = em.createNamedQuery( "listLatestMsg", Message.class )
					.setParameter( "user", user )
					.setParameter( "date", date.minusDays( 14 ).toDate());

			results = query.getResultList();
			if ( logger.isDebugEnabled()) logger.debug( "**** Fetched list of Messages. Size = " + results.size());
			
		} catch ( NoResultException e ) {
			if ( logger.isDebugEnabled()) logger.debug( "No Messages found!" );
		} catch ( Exception e ) {
			results = null;
			logger.error( e );
		} finally {
			em.close();
		}
		
		return results;
	}

	public Message addMessage( 	MessageType type, 
								OrgUser sender, OrgUser receiver, 
								ToolItem item, 
								String text ) {

		boolean bResult = false;
		
		// 1. Create Request Message
		Message msg = new Message( 
			type, 
			sender, receiver,
			item,
			text
		);
		
		//   2.Send Request Message
		return addMessage( msg );
		
	}

	
	public Message addMessage( Message msg ) {
		
		if ( msg.getTo() == null ) {
			
			return null;
			
		}
		
		msg.setStatus( MessageStatus.UNREAD );
		
		Message newMsg = DataFacade.getInstance().insert( msg );
		logger.debug( "Message " + msg.getId() + " was added" );
		
		return newMsg;
	}
	
	public Message updateMessage( Message msg ) {
		
		Message newMsg = null;
		
		if ( logger.isDebugEnabled()) logger.debug( "  Search existing Message using uniqueId: " + msg.getId() );
		Message oldMsg = DataFacade.getInstance().find( Message.class, msg.getId());
		
		if ( oldMsg != null ) {
			if ( logger.isDebugEnabled()) logger.debug( "  Found Message" );

			oldMsg.update( msg );
			
			newMsg = DataFacade.getInstance().merge( oldMsg );
			if ( newMsg != null )
				logger.debug( "Message with id:" + newMsg.getId() + " was updated" );
			else
				logger.error( "Failed to update Message with id: " + oldMsg.getId() );
			
		} else {
			logger.error( "  Existing Msg with id: " + msg.getId() + " not found!" );
		}
			
		
		
		return newMsg;
	}

	/*
	 * Business methods
	 */

	public boolean addToolRequest( ToolItem item, OrgUser sender ) {

		boolean bResult = false;
		
		// 1. Create Request Message
		boolean infoMsgIsNecessary = true;
		
		if ( item.getCurrentUser() != null ) {

			if ( item.getCurrentUser() == item.getResponsible()) {
				infoMsgIsNecessary = false;				
			}
			
			// Create and send proper message: new message ( type, current user, ??? );
			bResult = ( addMessage( 
							MessageType.REQUEST, 
							sender, 
							item.getCurrentUser(), 
							item,
							sender.getFirstAndLastNames() 
							+ " want to get "
							+ item.getTool().getName()
						) != null );
			
		} else if ( item.getResponsible() != null ) {

			infoMsgIsNecessary = false;				
			
			bResult = 	( addMessage( 
							MessageType.REQUEST, 
							sender, 
							item.getResponsible(), 
							item,
							sender.getFirstAndLastNames() 
							+ " want to get "
							+ item.getTool().getName()
						) != null );
			
			
		}	else {
			logger.error( "Repository Item does not have Owner and User to send request to" );
		}

		//   2.Send Info Message to Owner that somebody wants to borrow the Tool if Owner != Current User
		if ( bResult ) {
			
			if( infoMsgIsNecessary && item != null && item.getResponsible() != null ) {

				addMessage(  
						MessageType.INFO, 
						sender, 
						item.getResponsible(), 
						item,
						sender.getFirstAndLastNames()
						+ " want to get "
						+ item.getTool().getName()
						+ " from "
						+ item.getResponsible().getFirstAndLastNames()
				);
				
			
			}
		
		}
		
		return bResult;
	}

	public boolean addToolBorrowedInfo( OrgUser newUser, OrgUser oldUser, ToolItem item ) {

		boolean bResult = false;

		bResult = 	( addMessage( 
				MessageType.INFO, 
				newUser,
				oldUser,
				item,
				newUser.getFirstAndLastNames() 
				+ " took over "
				+ item.getTool().getName()
			) != null );
		
		return bResult;
	}
}
