package com.c2point.tools.ui.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.c2point.tools.utils.TemporalFileGen;
import com.vaadin.ui.Upload;

public class UploadComponent extends Upload  { // implements StartedListener, SucceededListener, FailedListener, ProgressListener {
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( UploadComponent.class.getName());

	private FileReceiver uploadReceiver;
	
	public UploadComponent() {
		this( "" );
	}
	
	public UploadComponent( String caption ) {
		super();
		setButtonCaption( caption );
		
        setImmediate(true);

        uploadReceiver = new FileReceiver();
		setReceiver( uploadReceiver );
		
	}

	public File getUploadFile() {  return uploadReceiver.getTempFile(); }
	
	class FileReceiver implements Receiver {
		private static final long serialVersionUID = 1L;
		
		private File downloadedFile;

		@Override
		public OutputStream receiveUpload(String filename, String mimeType) {

            try {
                /* Here, we'll stored the uploaded file as a temporary file. No doubt there's
                  a way to use a ByteArrayOutputStream, a reader around it, use ProgressListener (and
                  a progress bar) and a separate reader thread to populate a container *during*
                  the update.
       
                  This is quick and easy example, though.
                  */
            	File tmpFile = getTempFile();
            	
                return new FileOutputStream( tmpFile );
              } catch (IOException e) {
                e.printStackTrace();
                return null;
              }
			
		}
/*
		private File createTempFile() {
		
	    	try {
	    		
	    		downloadedFile = File.createTempFile("temp_upload_inventory", ".txt"); 
	    		if ( downloadedFile != null ) {
	    			logger.debug( "File has been created: '" + downloadedFile.getAbsolutePath() + "'" );
	    		} else {
	    			logger.debug( "Failed to create TMP file" );
	    		}
	    		
				return downloadedFile;
				
			} catch (IOException e) {
				logger.error( e );
			}
	    	
			return null;
		}
*/		
		public File getTempFile() { 
			
			if ( downloadedFile == null ) {
				
				downloadedFile = TemporalFileGen.createTempFile();
			}
			
			return downloadedFile; 
		}
		
		
	}

}
