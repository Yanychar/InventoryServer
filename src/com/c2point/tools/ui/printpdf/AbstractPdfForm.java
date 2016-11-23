package com.c2point.tools.ui.printpdf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.c2point.tools.utils.TemporalFileGen;
import com.itextpdf.layout.element.Paragraph;
import com.vaadin.server.StreamResource;

public abstract class AbstractPdfForm {

	protected PdfDocCreator			docCreator = null;
	private  Map<String, String> 	params = new HashMap<String, String>();
	
	protected ResourceBundle 		rb = null;
	private   File					outputFile;
	
	public AbstractPdfForm( ResourceBundle rb ) {
		this();
		
		this.rb = rb;
	}
	public AbstractPdfForm() {
		
		outputFile = TemporalFileGen.createTempFile();

		docCreator = new PdfDocCreator( outputFile.getAbsolutePath()).create();

		docCreator.getDocument().add( new Paragraph( " " ));
		
	}
	
	public PdfDocCreator getDocCreator() { return docCreator; }
	
	public void setParameter( String name, String value ) {
		params.put( name,  value );
	}
	
	public abstract void printHeader();

	public abstract void printFooter();

	public abstract void printBody();
	
	public abstract void printList( Collection<?> list );
/*
	public StreamResource getStream() {
	
		return docCreator.getStream();
	}
*/	
	public void close() {
		if ( docCreator != null ) {
			docCreator.close();
			docCreator = null;
		}
	}
	
	public void deleteTmp() {
		try {
			outputFile.delete();
		} catch ( Exception e ) {
			
		}
	}
	
	public StreamResource getStream() {
		
		StreamResource.StreamSource stream = new StreamResource.StreamSource() {
			private static final long serialVersionUID = 1L;

			@Override
			public InputStream getStream() {
				// TODO Auto-generated method stub
				try {
//					File f = new File ( fileName );
//					return new FileInputStream( f ) ;
					
					return new FileInputStream( outputFile );
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return null;
			}
			
		};
		StreamResource resource = new StreamResource( stream, outputFile.getAbsolutePath());
		// doc.getStream()
		
		return resource;
	}

	protected ResourceBundle getResourceBundle() { return this.rb; }

}
