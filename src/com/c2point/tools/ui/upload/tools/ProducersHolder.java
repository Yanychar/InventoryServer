package com.c2point.tools.ui.upload.tools;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.datalayer.DataFacade;
import com.c2point.tools.datalayer.ItemsFacade;
import com.c2point.tools.entity.tool.Producer;

public class ProducersHolder {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( ProducersHolder.class.getName());

	private Map<String, Producer>			existingProducers;

	public ProducersHolder() {
		
	}
	
	private void init() {

		if ( existingProducers == null ) {
			existingProducers = new HashMap<String, Producer>();
		} 
		
		Collection<Producer> pList = ItemsFacade.getInstance().getProducers();
		
		if ( pList != null ) {
			
			for( Producer p : pList ) {
				
				existingProducers.put( p.getName().trim().toLowerCase(), p );
			}
		} 
		
	}
	
	
	public Producer findOrAddProducer( String [] stringParameters, int number ) {
		
		return findOrAddProducer( stringParameters[ number ] );
	}
	
	protected Producer findOrAddProducer( String name ) {
		
		if ( name == null || name.trim().length() == 0 ) {

			return null;
			
		}

		Producer result = null;
		
		if ( existingProducers == null ) {
			init();
		}
		
		result =  existingProducers.get( name.trim().toLowerCase());
				
		if ( result == null ) {
			
			result = add( name );
			
		}

		return result;
	}
	

	private Producer add( String producerName ) {

		Producer result = null;
		
		result = DataFacade.getInstance().insert( new Producer( producerName ) );

		if ( result != null )
			
			existingProducers.put( result.getName().trim().toLowerCase(), result );

		
		return result;
	}
	
	
}
