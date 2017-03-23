package com.c2point.tools.ui.msg;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDate;

import com.c2point.tools.datalayer.MsgFacade;
import com.c2point.tools.entity.msg.Message;
import com.c2point.tools.entity.msg.MessageStatus;
import com.c2point.tools.entity.msg.MessageType;
import com.c2point.tools.ui.util.AbstractModel;

public class MessagingModel extends AbstractModel {

	private static Logger logger = LogManager.getLogger( MessagingModel.class.getName());

	public enum ViewState { Unread, Latest_Plus_Unread, All };
	
	private ViewState	viewState;
	
	public MessagingModel() {
		super();
		
		setState( ViewState.Unread );
	}
	
	public void init() {
		
		// Initial model initialization here if necesary
		
		
		fireListChanged();
	}
	
	public ViewState getState() { return viewState; }
	public void setState( ViewState state ) { this.viewState = state; }
	
	
	public Collection<Message> getMessages() {
		
		Collection<Message> retList = null;
		
		switch ( getState()) {
			case Unread:
				retList = MsgFacade.getInstance().list( getApp().getSessionData().getOrgUser(), MessageStatus.UNREAD );
				break;
			case Latest_Plus_Unread:
				// Read all received messages no older than 10 days and all Unread
				retList = MsgFacade.getInstance().listLatest( getApp().getSessionData().getOrgUser(), LocalDate.now());
				break;
			case All:
				retList = MsgFacade.getInstance().list( getApp().getSessionData().getOrgUser());
				break;
			default:
				break;
		
		}
		
		return retList;
	}

	public void addChangedListener( MessageModelListener listener ) {
		listenerList.add( MessageModelListener.class, listener);
	}
	
	
	protected void fireListChanged() {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == MessageModelListener.class) {
	    		(( MessageModelListener )listeners[ i + 1 ] ).listWasChanged();
	         }
	     }
	 }

	protected void fireAdded( Message msg ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == MessageModelListener.class) {
	    		(( MessageModelListener )listeners[ i + 1 ] ).wasAdded( msg );
	         }
	     }
	 }
	
	protected void fireChanged( Message msg ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == MessageModelListener.class) {
	    		(( MessageModelListener )listeners[ i + 1 ] ).wasChanged( msg );
	         }
	     }
	 }
	
	protected void fireDeleted( Message msg ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == MessageModelListener.class) {
	    		(( MessageModelListener )listeners[ i + 1 ] ).wasDeleted( msg );
	         }
	     }
	 }

	protected void fireSelected( Message msg ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == MessageModelListener.class) {
	    		(( MessageModelListener )listeners[ i + 1 ] ).selected( msg );
	         }
	     }
	 }

	public void messageSelected( Message msg ) {
		logger.debug( "Message has been selected. Id : " + ( msg != null ? msg.getId() : "NULL" ));

		fireSelected( msg );
		
	}

	public void approveToBorrow( Message msg ) {
/*
		// Tool changes its CurrentUser
		RepositoryItem item = msg.getItem();
		if ( item != null ) {
			item.setCurrentUser( msg.getFrom());
			
			
			
		}
*/		
		// Change old message that it is read + all necessary changes
		msg.getItem().setCurrentUser( msg.getFrom());
		msg.setStatus( MessageStatus.READ );
		
		
		// Create reply and send it
		Message newMsg = msg.createReply();
		
		if ( newMsg != null ) {
			newMsg = MsgFacade.getInstance().addMessage( newMsg );
		}

		
		// Store old message
		msg = MsgFacade.getInstance().updateMessage( msg );
		
		// Send old message change notification
		fireChanged( msg );
	}
	
	public void rejectToBorrow( Message msg ) {

		// Change old message that it is read + all necessary changes
		msg.setStatus( MessageStatus.READ );

		// Create reply and send it
		Message newMsg = msg.createReply( MessageType.REJECTION );
		
		if ( newMsg != null ) {
			newMsg = MsgFacade.getInstance().addMessage( newMsg );
		}

		// Store old message
		msg = MsgFacade.getInstance().updateMessage( msg );
		
		
		// Send old message change notification
		fireChanged( msg );
		
	}
	
}
