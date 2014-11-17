package com.c2point.tools.entity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.swing.event.EventListenerList;

import com.c2point.tools.entity.organisation.Organisation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Entity
@NamedQueries({
	@NamedQuery( name = "getSettings", 
		query = "SELECT settings FROM Settings settings " +
					"WHERE settings.organisation = :org AND " +
					"settings.deleted = false"
	),
})
public class Settings extends SimplePojo {
	private static Logger logger = LogManager.getLogger( Settings.class.getName());
	
	@OneToOne
	private Organisation 	organisation;

	private String			propString;
	

	public Settings() {}
	public Settings( Organisation org ) {
		
		setOrganisation( org );
	}
	
	
	
	public Organisation getOrganisation() { return organisation; }
	public void setOrganisation( Organisation organisation ) { this.organisation = organisation; }
	
	public String getPropString() { return propString; }
	public void setPropString( String propString ) { 
		this.propString = propString;

		this.propWereRead = false;
		getProperties();

		firePropertiesWereChanged();
		
	}

	@Transient
	private Properties props = new Properties();
	@Transient
	private boolean propWereRead = false;
	
	/**
	 * Convert stored string into the properties object
	 * @return Properties
	 */
	public Properties getProperties() {
//		Properties props = new Properties();

		if ( !propWereRead ) {
			try {
				if ( getPropString() != null ) {
					props.load( new ByteArrayInputStream( getPropString().getBytes( "UTF-8" )));
					propWereRead = true;
			
					logger.debug( "XML String has been converted to Properties object successfully:" );
					
					String key;
					for ( Enumeration<Object> e = props.keys(); e.hasMoreElements();) {
					       key = (String) e.nextElement();
					       logger.debug( "[ " + key + ", " + props.get( key) + " ]" );
					       
					}
				}	
			} catch (InvalidPropertiesFormatException e) {
				logger.error( "Organisation " + this.getOrganisation().getName() + " property string is not valid Property String" );
			} catch (UnsupportedEncodingException e) {
				logger.error( "Organisation " + this.getOrganisation().getName() + " property string. Wrong encoding" );
			} catch (IOException e) {
				logger.error( "Organisation " + this.getOrganisation().getName() + " property string.\n" + e );
			} catch (Exception e) {
				logger.error( "Organisation " + this.getOrganisation().getName() + " property string.\n" + e );
			}


		}
		
		return props;
	}

	public void setProperties( Properties properties ) {
		
		ByteArrayOutputStream outByte = new ByteArrayOutputStream();
		try {
//			properties.storeToXML( outByte, "Comment to store properties" );
			properties.store( outByte, null );
			setPropString( outByte.toString( "UTF-8" ));

			
		} catch ( UnsupportedEncodingException e ) {
			logger.error( "Organisation " + this.getOrganisation().getName() + " property string. Wrong encoding" );
		} catch ( IOException e ) {
			logger.error( "Organisation " + this.getOrganisation().getName() + " property string.\n" + e );
		}

	}

	/*
	 * property change listening implementation
	 */
	
	public interface PropertyChangedListener  extends EventListener {
		
		public void propertyWasChanged( String name, String value );
		public void propertiesWereChanged();
	}

	@Transient
	protected EventListenerList	listenerList = new EventListenerList(); 
	
	public void addChangedListener( PropertyChangedListener listener ) {
		listenerList.add( PropertyChangedListener.class, listener);
	}
	public void removeChangedListener( PropertyChangedListener listener ) {
		listenerList.remove( PropertyChangedListener.class, listener );
	}
	@SuppressWarnings("unused")
	private void firePropertyWasChanged( String name, String value ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == PropertyChangedListener.class ) {
	    		
	    		(( PropertyChangedListener )listeners[ i + 1 ] ).propertyWasChanged( name, value );
	    		
	    		if ( logger.isDebugEnabled()) logger.debug( "PropertyWasChanged event has been sent. PropName: " + name );
	    	}
	    }
	}

	private void firePropertiesWereChanged() {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == PropertyChangedListener.class ) {
	    		
	    		(( PropertyChangedListener )listeners[ i + 1 ] ).propertiesWereChanged();
	    		
	    		if ( logger.isDebugEnabled()) logger.debug( "PropertiesWereChanged event has been sent" );
	    	}
	    }
	}

	
	
}
