package com.c2point.tools.entity;

import javax.persistence.Embeddable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Embeddable
public class GeoLocation {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( GeoLocation.class.getName());

	private Double	latitude;
	private Double	longitude;
	private Double	accuracy;  // Accuracy of location measured in meters
	
	/**
	 * 
	 */
	public GeoLocation() {
		
		this( null, null, null );
		
	}
	/**
	 * @param latitude
	 * @param longitude
	 */
	public GeoLocation( Double latitude, Double longitude, Double accuracy ) {
		super();
		
		setLatitude( latitude );
		setLongitude( longitude );
		setAccuracy( accuracy );
		
	}

	public GeoLocation( GeoLocation coordinates ) {
		this( coordinates.latitude, coordinates.longitude, coordinates.accuracy );
	}

	public GeoLocation( Double latitude, Double longitude ) {
		this( latitude, longitude, new Double( 0 ));
	}

	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude( Double latitude ) {
		this.latitude = latitude;
	}
	
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude( Double longitude ) {
		this.longitude = longitude;
	}

	public Double getAccuracy() {
		return accuracy;
	}
	public void setAccuracy( Double accuracy ) {
		this.accuracy = accuracy;
	}
		
	public boolean isValid() {
		if ( this.latitude != null && this.longitude != null ) {
			if ( this.latitude >= -90 && this.latitude <= 90 && 
				 this.longitude >= -180 && this.longitude <= 180 ) {

				if ( this.accuracy == null || this.accuracy < 0 )
					this.accuracy = new Double( 0 );
				
				return true;
			}
		}
		
		return false;
	}

	public GeoLocation setInvalid() {
		this.latitude = null;
		this.longitude = null;
		this.accuracy = null;
		
		return this;
	}
	
	@Override
	public String toString() {
		return "Geo ["
				+ "lat=" + ( latitude != null ? latitude : "null" ) + ", "
				+ "long="+ ( longitude != null ? longitude : "null" ) + ", "
				+ "accur=" + ( accuracy != null ? accuracy : "null" ) + "]";
	}
	
	
}
