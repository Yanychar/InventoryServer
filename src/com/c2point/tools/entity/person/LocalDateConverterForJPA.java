package com.c2point.tools.entity.person;

import java.util.Date;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;
import org.joda.time.LocalDate;

public class LocalDateConverterForJPA implements Converter {

	private static final long serialVersionUID = 1L;

	@Override
	public Object convertDataValueToObjectValue( Object dataValue, Session session ) {

//		return dataValue != null ? new LocalDate(( Timestamp ) dataValue ) : null ; 

        if ( dataValue instanceof Date ) {
        	 
            return new LocalDate( dataValue );
            
        }
 
        throw new IllegalStateException("Converstion exception, value is not of Date type."); 
		
		
	}

	@Override
	public Object convertObjectValueToDataValue( Object objectValue, Session session ) {

//		return objectValue != null ? new Timestamp((( LocalDate) objectValue ).toDate().getTime()) : null;
		
        if ( objectValue != null ) {
			
        	if ( objectValue instanceof LocalDate ) {
	        	 
	            return (( LocalDate )objectValue ).toDateTimeAtStartOfDay().toDate();
	            
	        }
        	
            throw new IllegalStateException( "Converstion exception, value is not of LocalDate type. Its type is: " + objectValue.getClass().getName()); 
			
        } 

        return ( Date )null; //new Date();
		
	}

	@Override
	public void initialize(DatabaseMapping arg0, Session arg1) {
		
	}

	@Override
	public boolean isMutable() {
		return false;
	}

}
