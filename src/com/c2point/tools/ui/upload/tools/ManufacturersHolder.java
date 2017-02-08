package com.c2point.tools.ui.upload.tools;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.ItemsFacade;
import com.c2point.tools.entity.tool.Manufacturer;

public class ManufacturersHolder {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( ManufacturersHolder.class.getName());

	private Map<String, Manufacturer>			existingManufacturers;

	public ManufacturersHolder() {
		
	}
	
	private void init() {

		if ( existingManufacturers == null ) {
			existingManufacturers = new HashMap<String, Manufacturer>();
		} 
		
		Collection<Manufacturer> pList = ItemsFacade.getInstance().getManufacturers();
		
		if ( pList != null ) {
			
			for( Manufacturer p : pList ) {
				
				existingManufacturers.put( p.getName().trim().toLowerCase(), p );
			}
		} 
		
	}
	
	
	public Manufacturer findOrAddManufacturer( String [] stringParameters, int number ) {
		
		return findOrAddManufacturer( stringParameters[ number ] );
	}
	
	protected Manufacturer findOrAddManufacturer( String name ) {
		
		if ( name == null || name.trim().length() == 0 ) {

			return null;
			
		}

		Manufacturer result = null;
		
		if ( existingManufacturers == null ) {
			init();
		}
		
		result =  existingManufacturers.get( name.trim().toLowerCase());
				
		if ( result == null ) {
			
			result = add( name );
			
		}

		return result;
	}
	

	private Manufacturer add( String manufacturerName ) {

		Manufacturer result = ItemsFacade.getInstance().addManufacturer( manufacturerName );

		if ( result != null )
			
			existingManufacturers.put( result.getName().trim().toLowerCase(), result );

		
		return result;
	}
	
	
}
