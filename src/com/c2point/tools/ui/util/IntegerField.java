package com.c2point.tools.ui.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.ui.TextField;

public class IntegerField extends TextField {
	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger( IntegerField.class.getName());

	private IntegerRangeValidator		rangeVal = null; 
	private StringToIntegerConverter	intConv = null;
	
	public IntegerField() {

		super();

		intConv = new StringToIntegerConverter();
		setConverter( intConv );
		
		setupRangeVal( 0,32000 );
		this.setNullSettingAllowed( true );
		this.setNullRepresentation( "" );
		
		setImmediate( true );
		
		
	}
	
	public IntegerField( String caption ) {
		this();
		
		setCaption( caption );
		
	}
	
	public IntegerField( String caption, Integer value ) {
		this( caption );
		
		setValue( value );
	}

	public void setValue( Integer value ) {
		
		setValue( value != null ? value.toString() : null );
		
	}

	public Integer getIntegerValueNoException() {
		
		try {
			return intConv.convertToModel( this.getValue(), Integer.class, null );
		} catch ( Exception e ) {
			logger.debug( "Value of field '" + this.getCaption() + "' cannot be converted to Integer. NULL has been returned");
		}
		
		return null;
	}
	
	
	public void setupMinValue( int min ) {
		
		rangeVal.setMinValue( min );
	}
	
	public void setupMaxValue( int max ) {
		
		rangeVal.setMaxValue( max );
		
	}

	private void setupRangeVal( int min, int max ) {
		
		if ( rangeVal == null ) {

			rangeVal = new IntegerRangeValidator( "RangeError", min, max );
			
		} else {

			rangeVal.setMinValue( min );
			rangeVal.setMaxValue( max );
			
		}
		
		
		this.addValidator( rangeVal );
	}
	
}
