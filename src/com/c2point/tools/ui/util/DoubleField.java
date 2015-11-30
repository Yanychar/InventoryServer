package com.c2point.tools.ui.util;

import java.text.NumberFormat;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.data.util.converter.StringToDoubleConverter;
import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.ui.TextField;

public class DoubleField extends TextField {
	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger( DoubleField.class.getName());

	private DoubleRangeValidator	rangeVal = null; 
	private DoubleConverter			doubleConv = null;
	
	public DoubleField() {

		super();

		doubleConv = new DoubleConverter();
		setConverter( doubleConv );
		
		setMinValue( 0. );
		
		this.setNullSettingAllowed( true );
		this.setNullRepresentation( "" );
		
		setImmediate( true );
		
		
	}
	
	public DoubleField( String caption ) {
		this();
		
		setCaption( caption );
		
	}
	
	public DoubleField( String caption, Integer value ) {
		this( caption );
		
		setValue( value );
	}

	public void setValue( Integer value ) {
		
		setValue( value.toString());
		
	}

	public void setValue( Double value ) {
		
		setValue( value != null ? value.toString() : null );
		
	}

	public void setMinValue( Double min ) {
		
		if ( rangeVal == null ) {

			rangeVal = new DoubleRangeValidator( "RangeError", null, null );
			this.addValidator( rangeVal );
			
		}
		rangeVal.setMinValue( min );
		
	}
	
	public void setMaxValue( Double max ) {
		
		if ( rangeVal == null ) {

			rangeVal = new DoubleRangeValidator( "RangeError", null, null );
			this.addValidator( rangeVal );
			
		}
		rangeVal.setMinValue( max );
				
	}

	public Double getDoubleValueNoException() {
		
		try {
			return doubleConv.convertToModel( this.getValue(), Double.class, null );
		} catch ( Exception e ) {
			logger.debug( "Value of field '" + this.getCaption() + "' cannot be converted to Double. NULL has been returned");
		}
		
		return null;
	}
	
	
	private class DoubleConverter extends StringToDoubleConverter {

		private static final long serialVersionUID = 1L;
		
		@Override
	    public Double convertToModel(String value,
	            Class<? extends Double> targetType, Locale locale)
	            						throws ConversionException {
			
			Character curSign = '€';
			
			return super.convertToModel( StringUtils.remove( value, curSign ), targetType, locale );
			
		}
		
	    protected NumberFormat getFormat( Locale locale ) {
	        if (locale == null) {
	            locale = Locale.getDefault();
	        }

	        NumberFormat df = super.getFormat( locale );
	        df.setGroupingUsed( false );
	        df.setMinimumFractionDigits( 2 );
	        df.setMaximumFractionDigits( 2 );
	        	        
//	        DecimalFormat.getCurrencyInstance(locale );
//	        df = new DecimalFormat("#,##0.00" );
//	        df.setCurrency( Currency.getInstance( locale ));
	        
	        return df;
	    }
	    	
	}
}
