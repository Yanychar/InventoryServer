package com.c2point.tools.ui.printpdf;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import java.util.ResourceBundle;

import com.c2point.tools.entity.person.OrgUser;
import com.c2point.tools.entity.repository.ToolItem;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

public class PersonalToolsListForm extends ToolsListForm {
	@SuppressWarnings("unused")
	private static Logger logger = LogManager.getLogger( PersonalToolsListForm.class.getName()); 

	public PersonalToolsListForm( ResourceBundle bundle ) {
		super( bundle );
		
	}

	@Override
	public void printHeader() {
		// TODO Auto-generated method stub
	
	}

	public void printHeader( OrgUser user ) {
/*		
		PdfFont font;
		try {
			font = PdfFontFactory.createFont(FontConstants.TIMES_BOLD );
		} catch (IOException e) {
			logger.debug( "Failed to create font: " + e.getMessage());
		}
*/		
		
		docCreator.getDocument().add( new Paragraph( user.getFirstAndLastNames()));		
		docCreator.getDocument().add( new Paragraph( " " ));
		docCreator.getDocument().add( new Paragraph( " " ));
//		docCreator.getDocument().add( Chunk.NEWLINE );
	
	}
	
	@Override
	public void printFooter() {

		LocalDate today = LocalDate.now();
		LocalDate returnDay = today.plusDays( 7 );
		
		if( returnDay.getDayOfWeek() == DateTimeConstants.SATURDAY ) {
			returnDay = returnDay.plusDays( 2 );
		} else if ( returnDay.getDayOfWeek() == DateTimeConstants.SUNDAY ) {
			returnDay = returnDay.plusDays( 1 );
		}
			
		
		docCreator.getDocument().add( new Paragraph( " " ));
		docCreator.getDocument().add( new Paragraph( " " ));
		docCreator.getDocument().add( new Paragraph( today.toString( DateTimeFormat.forPattern("dd.MM.yyyy"))));	
		
		docCreator.getDocument().add( new Paragraph( " " ));
		
		Table table = new Table( new float[] { 1 });
		table.setWidthPercent( 100 );		
    	
		Cell cell = new Cell();
		
		String str_1 = 	"Allekirjoituksellani vakuutan, että luetellut työkalut ovat tällä hetkellä halussani. "
					+	"Työkalut, jotka eivät ole halussani, on vedetty ylitse. Jos tästä listauksesta puuttuu "
					+	"työkalujani, ne on lueteltu tämän lomakkeen kääntöpuolella (työkalun nimi ja viivakoodi, "
					+	"jos se on saatavilla).\n\n\n";
		String str_2 = 	"Paikka ja päiväys:___________________\n\n";
		String str_3 = 	"Allekirjoitus:_______________________\n\n";
		String str_4 = 	"Palauta listaus työnjohtajalle XX.XX.XXXX klo 10:00 mennessä.\n\n";
		
		str_4 = StringUtils.replace( str_4, 
				"XX.XX.XXXX", 
				returnDay.toString( DateTimeFormat.forPattern("EE dd.MM.yyyy").withLocale( new Locale( "fi", "FI" ))));
		
		try {
			cell.add( new Paragraph( str_1 ).setFont(PdfFontFactory.createFont(FontConstants.COURIER)));
			cell.add( new Paragraph( str_2 ).setFont(PdfFontFactory.createFont(FontConstants.COURIER)));
			cell.add( new Paragraph( str_3 ).setFont(PdfFontFactory.createFont(FontConstants.COURIER)));
			cell.add( new Paragraph( str_4 ).setFont(PdfFontFactory.createFont(FontConstants.COURIER_BOLD)));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		cell.setFont( PdfFontFactory.createFont(FontConstants.COURIER_BOLD), 14);	
		table.addCell( cell );

		docCreator.getDocument().add( table );
		
		docCreator.getDocument().add( new AreaBreak());
		
	}

	@Override
	public void printBody() {

	}

	@Override
	public void printList( Collection<?> list ) {

		@SuppressWarnings("unchecked")
		Collection<ToolItem> tiList = ( Collection<ToolItem> )list;
/*
		PdfFont font = null;
		try {
			font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		docCreator.getDocument().add(new Paragraph( "iText is:" ).setFont(font));
*/		
		
		Table table = new Table( new float[] { 1, 7, 2, 3 });
		table.setWidthPercent( 100 );		
//		Table table = new Table( 4);
		//tiList.size()
		
        addHeader( table );
		
		int counter = 1;
		for( ToolItem ti : tiList) {

            addItemToTable( ti, table, counter );
            counter++;
			
//			document.addItem( str );
		}

		docCreator.getDocument().add( table );
		
		
		
	}

    private void addHeader( Table table ) {

    	table.addHeaderCell( "No." );
    	table.addHeaderCell( this.getResourceBundle().getString( "toolsmgmt.list.header.tool" ));
    	table.addHeaderCell( this.getResourceBundle().getString( "toolsmgmt.list.header.status" ));
    	table.addHeaderCell( this.getResourceBundle().getString( "toolsmgmt.view.label.barcode" ) +":" );
    	
    }
    
    private void addItemToTable( ToolItem ti, Table table, int counter ) {

    	table.addCell( Integer.toString( counter ));
    	table.addCell( ti.getFullName());
    	table.addCell( ti.getStatus().toString( this.getResourceBundle()));
    	table.addCell( ti.getBarcode());

    }
	
}
