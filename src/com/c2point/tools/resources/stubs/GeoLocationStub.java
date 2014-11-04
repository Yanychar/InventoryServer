package com.c2point.tools.resources.stubs;

import javax.persistence.Embeddable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.location.GeoLocation;

@Embeddable
public class GeoLocationStub {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( GeoLocationStub.class.getName());

	private Double	latitude;
	private Double	longitude;
	private Double	accuracy;  // Accuracy of location measured in meters
	
	/**
	 * 
	 */
	public GeoLocationStub() {
		
		setLatitude( null );
		setLongitude( null );
		setAccuracy( null );
		
	}
	public GeoLocationStub( GeoLocation coordinates ) {

		setLatitude( coordinates.getLatitude());
		setLongitude( coordinates.getLongitude());
		setAccuracy( coordinates.getAccuracy());
	}

	public Double getLatitude() { return latitude; }
	public void setLatitude( Double latitude ) { this.latitude = latitude; }
	
	public Double getLongitude() { return longitude; }
	public void setLongitude( Double longitude ) { this.longitude = longitude; }

	public Double getAccuracy() { return accuracy; }
	public void setAccuracy( Double accuracy ) { this.accuracy = accuracy; }
		
	@Override
	public String toString() {
		return "Geo ["
				+ "lat=" + ( latitude != null ? latitude : "null" ) + ", "
				+ "long="+ ( longitude != null ? longitude : "null" ) + ", "
				+ "accur=" + ( accuracy != null ? accuracy : "null" ) + "]";
	}
	
	
}
