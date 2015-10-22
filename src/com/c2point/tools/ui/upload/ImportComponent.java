package com.c2point.tools.ui.upload;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ToolItem;
import com.c2point.tools.entity.tool.Tool;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FailedListener;
import com.vaadin.ui.Upload.ProgressListener;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;

public class ImportComponent implements StartedListener, SucceededListener, FailedListener,  ProgressListener, ProcessListener {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger( ImportComponent.class.getName());

	private ProgressWindow progressWindow;
	
	private FileProcessor  importProcessor;
	
	public ImportComponent( FileProcessor importProcessor ) {
		super();
		setFileProcessor( importProcessor );
	}
	
	public ImportComponent() {
		super();
	}
	
	public void setFileProcessor( FileProcessor importProcessor ) {
		this.importProcessor = importProcessor;
	}
	
	public void showProgress() {
	
		progressWindow = new ProgressWindow( "File Import" );
		
		UI.getCurrent().addWindow( progressWindow );
		logger.debug( "progress window shall be shown" );
	}

	@Override
	public void uploadStarted(StartedEvent event) {
        logger.debug( "Upload started" );		        

        showProgress();
        
        writeLog( "Start downloading '" + event.getFilename() + "' file ..." );

	}

	@Override
	public void updateProgress(long readBytes, long contentLength) {
        logger.debug( "  Upload progressing" );		        

        if ( logger.isDebugEnabled()) {
        	writeLog( "  progressing ..." );
        }
		
	}

	@Override
	public void uploadFailed(FailedEvent event) {
        logger.debug( "Upload failed" );		        

        writeLog( "File download failed! Cannot import." );
		
	}

	@Override
	public void uploadSucceeded( SucceededEvent event ) {
        logger.debug( "  Upload succeeded!" );		        

        writeLog( "File was downloaded successfully\n" );

		if ( importProcessor != null ) {

			try {

				importProcessor.addChangedListener( this );
				importProcessor.process();
					
			} catch ( Exception e ) {
				writeLog( "\nERROR!!! ... Failed import file: " + event.getFilename());
				logger.error( "Failed to import file: " + event.getFilename());
				logger.error( e );
			}
		}
		
		
	}

	@Override
	public void processingStarted( String filename ) {

		writeLog( "Import has been started ..." );
		
	}

	@Override
	public void lineProcessed( Object processedObject, ProcessedStatus status, int lineNumber) {

		switch ( status ) {
			case COMMENT:
//				if ( logger.isDebugEnabled())
//					writeLog( "  Line " + lineNumber + ": This is comment" );
				break;
			case FAILED:
				writeLog( "  Line " + lineNumber + ": Failed to process:  " + showObject( status, processedObject ));
				break;
			case VALIDATION_FAILED:
				writeLog( "  Line " + lineNumber + ": Failed to validate:  " + showObject( status, processedObject ));
				break;
			case PERSON_NOT_FOUND:
				writeLog( "  Line " + lineNumber + ": User not found:  " + showObject( status, processedObject ));
				break;
			case TOOL_ITEM_EXIST:
				writeLog( "  Line " + lineNumber + ": Tool Item with similar barcode exists already:  " + showObject( status, processedObject ));
				break;
			case PROCESSED:
				if ( logger.isDebugEnabled())
					writeLog( "  Line " + lineNumber + ". Processed: " + showObject( status, processedObject ));
				break;
			case VALIDATED:
				if ( logger.isDebugEnabled())
					writeLog( "  Line " + lineNumber + ". Validated: " + showObject( status, processedObject ));
				break;
			case EXIST:
				writeLog( "  Line " + lineNumber + ". FAILED. Exists already. Was not added!" );
				break;
			default:
				break;
			
		}
		
	}

	@Override
	public void processingFailed( int processed, List<Integer> errRecNumbers ) {


		String str = "";
		for ( Integer i : errRecNumbers ) {
			str = str.concat( "#" + i + " " );
		}
		
		writeLog( "\nERROR!!! ... Failed to process following lines from the file: " + str );
		
		try {
			importProcessor.getFile().delete();
		} catch( Exception e ) {
			logger.error( "Cannot delete temoral file: " + importProcessor.getFile().getAbsolutePath());
		}

		importProcessor.removeChangedListener( this );
		
	}

	@Override
	public void processingSucceeded(int processed) {

		writeLog( "... All records were imported successfully" );
		
		try {
			importProcessor.getFile().delete();
		} catch( Exception e ) {
			logger.error( "Cannot delete temporal file: " + importProcessor.getFile().getAbsolutePath());
		}

		importProcessor.removeChangedListener( this );
		
	}

	private void writeLog( String text ) {
		
		if ( progressWindow != null ) {
			
			progressWindow.write( text );
		}
	}

	private String showObject( ProcessedStatus status, Object processedObject ) {
		
		String resp = "???";
		
		if ( processedObject != null ) {

			if ( processedObject instanceof Tool ) {
			
				resp = (( Tool )processedObject ).getFullName();
				
			} else if ( processedObject instanceof ToolItem ) {

				ToolItem item = ( ToolItem )processedObject;
				
				resp = item.getFullName() + " assigned to " + item.getCurrentUser().getLastAndFirstNames();
				
			} else if ( processedObject instanceof OrgUser ) {
				
				OrgUser user = ( OrgUser )processedObject;

				resp = user.getLastAndFirstNames();
				
			} else {
				
			}
				
			
		}
		
		return resp;
	}

}
