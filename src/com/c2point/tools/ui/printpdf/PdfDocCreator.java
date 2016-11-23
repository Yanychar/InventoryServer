package com.c2point.tools.ui.printpdf;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;

public class PdfDocCreator {
	private static Logger logger = LogManager.getLogger( PdfDocCreator.class.getName());

	private Document	document;
	private PdfWriter	writer;
	
	private String 		fileDest;
	
	public PdfDocCreator( String fileDest ) {
			
		setFileNameDest( fileDest );
	}
	
	public PdfDocCreator create( ) {
		
		// File name cannot be empty
		if ( this.getFileNameDest() == null ) 
			return null;
		// Create file writer
		try {
			
//			File file = new File( getFileNameDest());
//	        file.getParentFile().mkdirs();			
			
			writer = new PdfWriter( getFileNameDest());
			logger.debug( "PdfWriter created" );
			// Initialize PDF document 
			PdfDocument pdf = new PdfDocument( writer );
			logger.debug( "PdfDocument created" );
			// Init abstract(hides PDF nature document
			document = new Document( pdf, PageSize.A4 );
			document.setMargins( 20, 20, 20, 20 );
			
			logger.debug( "Document created" );
			
		} catch (FileNotFoundException e) {
			logger.error( "Cannot create PDF document!" );
			return null;
		}
		
		return this;
	}
	public PdfDocCreator create( String fileDest ) {
		// Setup file name to store PDF file
		setFileNameDest( fileDest );
		// Create PDF file to work with 
		return create();
	}

	public void close() {
		
		if ( document != null ) {
			// Close document at the end. Otherwise changes can be lost!"
			try {
				document.close();
			} catch ( Exception e ) {
				logger.debug( "Cannot close PDF document: " + e.getMessage());
			}
		} else {
			logger.error( "Document cannot be close. " + ( getFileNameDest() != null ? getFileNameDest() : "It" ) + " does not exist!" );  
		}
		
	}
	
	
	public String getFileNameDest() { return this.fileDest; } 
	public void setFileNameDest( String fileDest ) { this.fileDest = fileDest; } 

	
    public void testFill() {
    	
		PdfFont font = null;
		try {
			font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	document.add(new Paragraph( "iText is:" ).setFont(font));

        // Create a List
        List list = new List()
            .setSymbolIndent(12)
            .setListSymbol("\u2022")
            .setFont(font);
        // Add ListItem objects
        list.add(new ListItem("BBBBB"))
        	.add(new ListItem("Never gonna give you up"))
            .add(new ListItem("Never gonna let you down"))
            .add(new ListItem("Never gonna run around and desert you"))
            .add(new ListItem("Never gonna make you cry"))
            .add(new ListItem("Never gonna say goodbye"))
            .add(new ListItem("Never gonna tell a lie and hurt you"));
//        	.add(new ListItem( "*** " + Integer.toString(i++)+ " ***" ));
        // Add the list
        document.add(list);    	
   
    }
	static int i = 0;

    public Document getDocument() { return document; }

}
