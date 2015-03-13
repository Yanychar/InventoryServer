package com.c2point.tools.ui.upload;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class FileProcessor {
	private static Logger logger = LogManager.getLogger( FileProcessor.class.getName());

	private File	processFile;

	private EventListenerList	listenerList = new EventListenerList();
	
	protected FileProcessor() {
		
	}
		
	protected FileProcessor( File processFile ) {
		
		setFile( processFile );
		
	}

	public void setFile( File processFile ) { this.processFile = processFile; }
	public File getFile() { return this.processFile; }
	
	
	public boolean process() {

//		boolean result = true;
		
		int processed = 0;
		List< Integer > errRecNumbers = new ArrayList< Integer >();

//		info( "*** Start to process file: " + processFile.getName() + " ***" );
		
		CSVFileReader reader = new CSVFileReader();
		
		if ( !reader.init( processFile )) {
//			error( "ERROR: Failed to process file: " + processFile.getName());
			logger.error( "ERROR: Failed to process file: " + processFile.getName());
			
//			fireFailure( "ERROR: Failed to process file: " + processFile.getName(), processed, errRecNumbers );
			fireFailure( processed, errRecNumbers );
			
			return false;
		}
		
		fireStarted();

		String [] currentLine = null;
		ProcessedStatus result;

		if ( logger.isDebugEnabled()) logger.debug( "  Start to traverse the file line by line......" );
		try {
			
			while (( currentLine = reader.nextLine()) != null ) {
				processed++;

				if ( logger.isDebugEnabled()) {
					String outstr = "[ ";
					for ( int j = 0; j < currentLine.length; j++ )
						outstr = outstr.concat( "'" + (( currentLine[ j ] != null ) ? currentLine[ j ] : "NULL" ) + "'  " );
					outstr = outstr.concat( "]" );
					logger.debug( "    Line # " + processed + " length=" + currentLine.length + ": " + outstr ); 
				}

				clearProcessedObject();
				
				result = validateLine( currentLine, processed );
				
				if ( result == ProcessedStatus.VALIDATED ) {
					
					result = processLine( currentLine, processed );
				}

				fireProcessed( getProcessedObject(), result, processed );
				
				if (   result == ProcessedStatus.FAILED 
					|| result == ProcessedStatus.VALIDATION_FAILED
					|| result == ProcessedStatus.EXIST ) {

					errRecNumbers.add( processed );
					
				}
						
			}
		} catch (IOException e) {
			if ( logger.isDebugEnabled()) logger.debug( "Faileds to read line="+processed+" in import file: " + processFile.getName() );

//			error( "ERROR: I/O error. Line #" + processed );
			fireProcessed( processedObject, ProcessedStatus.FAILED, processed );
			
			errRecNumbers.add( processed );
		}
		if ( logger.isDebugEnabled()) logger.debug( "  ... end file traversal" );
		
		reader.close();
		
		if ( errRecNumbers.size() == 0 ) {
//			info( "*** Successfully processed without errors ***");
			fireSuccess( processed );
		} else {
			logger.error( errRecNumbers.size() + " lines were not imported from file: '" + processFile.getName() + "'. Lines:" );

//			error( "ERROR: *** Faileded to process lines: " + errMsg );
			fireFailure( processed, errRecNumbers );
		}

//		info( "*** ... File processing: " + ( result ? "SUCCEDED" :"FAILED" ) + " ***" );
		
		// Create Transaction
/*		
		try {
			Transaction transaction = new FileImportTransaction( this.getClass().getSimpleName(), processFile, this.organisation, errMsg );
			writeTransaction( transaction );
		} catch ( JAXBException e ) {
			logger.error( "Cannot convert to XML for transaction log (FileImport) " );
		}
*/		
		
		return ( errRecNumbers.size() == 0 );
	}
	
	
 	public void addChangedListener( ProcessListener listener ) {
		listenerList.add( ProcessListener.class, listener);
	}
	
 	public void removeChangedListener( ProcessListener listener ) {
		listenerList.remove( ProcessListener.class, listener);
	}
	
	private void fireStarted() {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ProcessListener.class) {
	    		(( ProcessListener )listeners[ i + 1 ] ).processingStarted( processFile.getAbsolutePath());
	         }
	     }
	}
	
	private void fireProcessed( Object processedObject, ProcessedStatus status, int lineNumber ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ProcessListener.class) {
	    		(( ProcessListener )listeners[ i + 1 ] ).lineProcessed( processedObject, status, lineNumber );
	         }
	     }
	}
	
	private void fireFailure( int processed, List<Integer> errRecNumbers ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ProcessListener.class) {
	    		(( ProcessListener )listeners[ i + 1 ] ).processingFailed( processed, errRecNumbers );
	         }
	     }
	}
	
	private void fireSuccess( int processed ) {
		Object[] listeners = listenerList.getListenerList();

	    for ( int i = listeners.length-2; i >= 0; i -= 2) {
	    	if ( listeners[ i ] == ProcessListener.class) {
	    		(( ProcessListener )listeners[ i + 1 ] ).processingSucceeded( processed );
	         }
	     }
	}

	/*
	 *  Return:
	 *  	COMMENT,
	 *  	VALIDATED
	 *  	VALIDATION_FAILED,
	 *  	EXIST,
	 *  
	 */
	
	protected abstract ProcessedStatus validateLine( String [] nextLine, int lineNumber );
	
	/*
	 *  Return:
	 *  	PROCESSED,
	 *  	FAILED
	 */
	protected abstract ProcessedStatus processLine( String [] nextLine, int lineNumber );

	
	protected Object processedObject = null;
	
	protected void clearProcessedObject() {
		this.processedObject = null;
	}
	
	protected Object getProcessedObject() { return this.processedObject; }
	protected void setProcessedObject( Object processedObject ) { this.processedObject = processedObject; }
	
	
	
	public class PatternLen {
		
		String pattern;
		int length;
		
		public PatternLen( String pattern, int length ) {
			this.pattern = pattern; 
			this.length = length;
		}
		
		public String getPattern() { return pattern; }
		public int getLength() { return length; }
		
		public String toString() {
			
			return "PatternLen[ '" + this.pattern + "', " + this.length + " ]"; 
		}
	}
	
	
	
}
