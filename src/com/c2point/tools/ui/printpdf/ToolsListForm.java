package com.c2point.tools.ui.printpdf;

import java.util.Collection;
import java.util.ResourceBundle;

import com.c2point.tools.entity.repository.ToolItem;
import com.itextpdf.layout.element.Table;

public class ToolsListForm extends AbstractPdfForm {

	public ToolsListForm( ResourceBundle bundle ) {
		super( bundle );
		
	}

	/*	
	public ToolsListForm( ResourceBundle r) {
		super();
	}
*/
	@Override
	public void printHeader() {
		// TODO Auto-generated method stub
	
	}

	@Override
	public void printFooter() {
		// TODO Auto-generated method stub
		
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
