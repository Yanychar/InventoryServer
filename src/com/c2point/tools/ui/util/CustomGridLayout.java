package com.c2point.tools.ui.util;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

public class CustomGridLayout extends GridLayout {
	private static final long serialVersionUID = 1L;

	public CustomGridLayout() {
		super( 3, 1 );
	}
	
	public void addField( String caption, AbstractComponent field ) {
		
		int currentRow = this.getRows();

		setRows( currentRow + 1 );
		
		if ( !StringUtils.isBlank( caption )) {
			
			Label label = new Label( caption );
			this.addComponent( label, 0, currentRow );
			
			setComponentAlignment( label, Alignment.TOP_LEFT);
		}
		this.addComponent( 				  field, 1, currentRow );
		setComponentAlignment( field, Alignment.TOP_LEFT);
	}

	public void addLastInLineField( AbstractComponent field ) {

		this.addComponent( field, this.getColumns() - 1, this.getRows() - 1 );
		setComponentAlignment( field, Alignment.TOP_LEFT);
		
	}
	
	public void addFieldFullLine( AbstractComponent field ) {
		
		int currentRow = this.getRows();

		setRows( currentRow + 1 );
		
		this.addComponent( field, 0, currentRow, getColumns() - 1, currentRow );
	}

	public void addSeparator() {

		HorizontalSeparator separator = new HorizontalSeparator();
		
		addFieldFullLine( separator );

//		public Label setLabelContent( String content, int col, int row, boolean wholeWidth ) { 
		
	}

		
	
	public Label setLabelContent( int row ) {
		
		return setLabelContent( 0, row, true );
	}

	public Label setLabelContent( int col, int row ) {
		
		return setLabelContent( col, row, false );
		
	}
	
	public Label setLabelContent( int col, int row, boolean wholeWidth ) {
		
		return setLabelContent( "", col, row, wholeWidth ); 
		
	}
	
	public Label setLabelContent( String content, int row ) {
		
		return setLabelContent( content, 0, row, true ); 
		
	}
	
	public Label setLabelContent( String content, int col, int row ) {
		
		return setLabelContent( content, col, row, false ); 
		
	}
	
	public Label setLabelContent( String content, int col, int row, boolean wholeWidth ) { 
	
		Component comp = getComponent( col, row );

		Label lb = null;
		
		if ( comp == null || comp instanceof Label ) {
		
			lb = ( Label )comp;
			
			if ( lb == null ) {
			
				lb = new Label( "", ContentMode.HTML );
				
				if ( wholeWidth )
					addComponent( lb, 0, row, getColumns() - 1, row );
				
				else
					addComponent( lb, col, row, col, row );
				
			}
			
			lb.setValue( content );
		}
		
		return lb;
	}
	
	protected BoldLabel addLabelValueComponent( String labelCaption ) {
		
		BoldLabel resComp = new BoldLabel();

		addField( labelCaption, resComp );
		
		return resComp;
	}


	
}
