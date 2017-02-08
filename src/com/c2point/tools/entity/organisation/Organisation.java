package com.c2point.tools.entity.organisation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.swing.event.EventListenerList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.SimplePojo;
import com.c2point.tools.entity.person.Address;
import com.c2point.tools.entity.person.OrgUser;

@Entity
public class Organisation extends SimplePojo {

	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( Organisation.class.getName());
	
	private String code;
	private String name;
	
	@OneToMany( mappedBy = "organisation", 
			cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH },
			fetch=FetchType.LAZY )
	@MapKey( name = "id" )
	private Map<Long, OrgUser> 
							employees = new HashMap<Long, OrgUser>();

	private Address			address;
	private String			tunnus;

	private String			phoneNumber;
	private String			email;

	private String			info;

	@OneToOne( cascade=CascadeType.ALL)
	private OrgUser			responsible;
	
	@Column(name="service_owner")
	private boolean			serviceOwner;
	
//	private String		propString;
	@Transient
	private Properties props = new Properties();
	@Transient
	private boolean propWereRead = false;

	@Transient
	protected EventListenerList	listenerList = new EventListenerList(); 

	public Organisation() {
		super();
	}
	
	public Organisation( String code, String name ) {
		super();
		
		setCode( code );
		setName( name );
		
	}
	
	public String getCode() { return code; }
	public void setCode( String code ) { this.code = code; }

	public String getName() { return name; }
	public void setName( String name ) { this.name = name; }

	public Map<Long, OrgUser> getEmployees() { return employees; }
	public void setEmployees( Map< Long, OrgUser > employees ) { this.employees = employees; }
	
	public Address getAddress() { return address; }
	public void setAddress( Address address ) { this.address = address; }

	public String getPhoneNumber() { return phoneNumber; }
	public void setPhoneNumber( String phoneNumber ) { this.phoneNumber = phoneNumber; }

	public String getEmail() { return email; }
	public void setEmail( String email ) { this.email = email; }
	
	public String getTunnus() { return tunnus; }
	public void setTunnus( String tunnus ) { this.tunnus = tunnus; }
	
	public String getInfo() { return info; }
	public void setInfo( String info ) { this.info = info; }

	public boolean isServiceOwner() { return serviceOwner; }
	public void setServiceOwner(boolean serviceOwner) { this.serviceOwner = serviceOwner; }
	
	public OrgUser getResponsible() { return responsible; }
	public void setResponsible(OrgUser responsible) { this.responsible = responsible; }
/*
	public String getPropString() { return propString; }
	public void setPropString( String propString ) { 
		this.propString = propString;

		this.propWereRead = false;
		getProperties();

		firePropertiesWereChanged();
		
	}
*/
	/**
	 * Convert stored XML string into the properties object
	 * @return Properties
	 */
/*	
	public Properties getProperties() {
//		Properties props = new Properties();

		if ( !propWereRead ) {
			try {
				props.load( new ByteArrayInputStream( getPropString().getBytes( "UTF-8" )));
				propWereRead = true;
		
				logger.debug( "XML String has been converted to Properties object successfully:" );
				
				String key;
				for ( Enumeration<Object> e = props.keys(); e.hasMoreElements();) {
				       key = (String) e.nextElement();
				       logger.debug( "[ " + key + ", " + props.get( key) + " ]" );
				       
				}
				
			} catch (InvalidPropertiesFormatException e) {
				logger.error( "Organisation " + this.getName() + " property string is not valid XML String" );
			} catch (UnsupportedEncodingException e) {
				logger.error( "Organisation " + this.getName() + " property string. Wrong encoding" );
			} catch (IOException e) {
				logger.error( "Organisation " + this.getName() + " property string.\n" + e );
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
			logger.error( "Organisation " + this.getName() + " property string. Wrong encoding" );
		} catch ( IOException e ) {
			logger.error( "Organisation " + this.getName() + " property string.\n" + e );
		}

//		propWereRead = false;

	}

	public String getProperty( String name ) {
		return getProperty( name, null );
	}
	public String getProperty( String name, String defValue ) {
		
		return getProperties().getProperty( name, defValue );
		
	}
	public void setProperty( String name, String value ) {
		
		getProperties().setProperty( name, value );

		ByteArrayOutputStream outByte = new ByteArrayOutputStream();
		try {
			getProperties().store( outByte, null );
			propString = outByte.toString( "UTF-8" );

			firePropertyWasChanged( name, value );
			
		} catch ( UnsupportedEncodingException e ) {
			logger.error( "Organisation " + this.getName() + " property string. Wrong encoding" );
		} catch ( IOException e ) {
			logger.error( "Organisation " + this.getName() + " property string.\n" + e );
		}
		
		
	}
	
	public interface PropertyChangedListener  extends EventListener {
		
		public void propertyWasChanged( String name, String value );
		public void propertiesWereChanged();
	}

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
*/

	/*
	 * Business methods
	 */
	
	public boolean addUser( OrgUser user ) {
		
		boolean res = false;
		
		if ( user.getOrganisation() == null 
			||	
			user.getOrganisation().getId() != this.getId()) {
			user.setOrganisation( this );
		}
		
		if ( this.getEmployees().get( user.getId()) == null ) {
			
			this.getEmployees().put( user.getId(), user );
			
			res = true;
		}
		
		return res;
		
	}

	
}
