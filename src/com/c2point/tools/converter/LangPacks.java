package com.c2point.tools.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.converter.AbstractEntity.EntityType;

public class LangPacks {

	private static Logger logger = LogManager.getLogger( LangPacks.class.getName());

	private static String prefix_empty = "empty_"; 
	private static String prefix_comment = "comment_"; 

	

	private List< LangPackDescription> listDescrs;  
	// Key is property name
	private Map<String, AbstractEntity> mapProps;  
	
	
	public LangPacks() {
		
		listDescrs = new ArrayList< LangPackDescription >();
		
		mapProps = new LinkedHashMap< String, AbstractEntity >();
	}
	
	public List< LangPackDescription> getLangPackDescriptions() {
		
		return this.listDescrs;
	}
	
	public LangPackDescription getLangPackDescription( int i ) {
		
		LangPackDescription lpd = null;
		
		try {
			lpd = listDescrs.get( i );
		} catch ( Exception e ) {
			// Array is empty
		}
		
		if ( lpd == null ) {
			
			lpd = new LangPackDescription();
			
			listDescrs.add( i, lpd );
			
		}
		
		return lpd;
	}
	
	public boolean add() {
		mapProps.put( createUniqueKey( prefix_empty ), new EmptyEntity());
		return true;
	}
	public boolean add( String comment ) {
		mapProps.put( createUniqueKey( prefix_comment ), new CommentEntity( comment ));
		return true;
	}

	// Add property directly without the branch
	public void add( String propName, Locale locale, String value ) {
		
		AbstractEntity prop = mapProps.get( propName );
		
		if ( prop == null ) {
			prop = new PropertyEntity( propName );
			mapProps.put( propName, prop );
		}
		
		if ( prop.getType() != EntityType.PROPERTY ) {

			logger.error( "Found entity '" + prop.getName() + "entity with type:" + prop.getType());
			return;
		}
		
		(( PropertyEntity )prop ).set( locale, value );

	}

/*	
	// Add property to the branch
	public boolean add( String branchName, String propName, Locale locale, String value ) {
		
		if ( propName == null || propName.length() == 0 ) {
			// Add property without branch
			add( branchName, locale, value );
			
			return true;
			
		}
		
		AbstractEntity branch = mapProps.get( branchName );
		
		if ( branch == null ) {
			
			// Branch not found. Shall be added
			branch = new BranchEntity( branchName );
			mapProps.put( branchName, branch );
		}

		// Validate that this is a pranch
		if ( branch.getType() != EntityType.BRANCH ) {
			logger.error( "Found entity '" + branch.getName() + "entity with type:" + branch.getType());
			return false;
		}
		
		// Now try to find PROPERTY within the branch. Add new if necessary
		(( BranchEntity )branch).addProp( propName, locale, value );

		return true;
	}
*/	
	public Collection<AbstractEntity> values() { return mapProps.values(); }
	public Set<String> keys() { return mapProps.keySet(); }
	
	public AbstractEntity get( String key ) {
		
		return mapProps.get( key );
	}
	
	
	private int uniqueKey = 1;
	private String createUniqueKey( String prefix ) {
		String key;
		
		do {
			key = prefix + Integer.toString( uniqueKey );
			uniqueKey++;
		} while( mapProps.containsKey( key ));
		
		return key;
	}

	public int size() {
		return mapProps.size();
	}

	public Locale[] getLocaleArray() {
		
		Locale [] localeList = new Locale[ listDescrs.size()];
		
		for ( int i = 0; i < localeList.length; i++ ) {
			
			localeList[ i ] =  listDescrs.get( i ).getLocale();
		}
		
		return localeList;
	}
	
	public void print() {
		
		Iterator< AbstractEntity> iter = mapProps.values().iterator();
		
		while( iter.hasNext()) {

				logger.debug( iter.next().toString( getLocaleArray()));
		}
	
	}

}
