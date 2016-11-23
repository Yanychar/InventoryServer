package com.c2point.tools.ui.util;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class CaptionedHLabel extends HorizontalLayout {
	private static final long serialVersionUID = 1L;
	
	private Label label = null;
	
	public CaptionedHLabel() {
		this( null, false );
	}

	public CaptionedHLabel( String caption ) {

		this( caption, false );
	
	}

	public CaptionedHLabel( String caption, boolean bolded ) {
		super();
		
		label = ( bolded ? new BoldLabel() : new Label());
		this.addComponent( label );
		
		this.setCaption( caption );
		
		this.setComponentAlignment( label,  Alignment.TOP_LEFT);
	}

	public CaptionedHLabel( String caption, String style ) {
		super();
		
		label = new StyledLabel( style );
		this.addComponent( label );

		this.setCaption( caption );
		
	}

	public void setBold( boolean bolded ) {
		
		String currentValue = label.getValue();
		
		this.removeComponent( label );

		label = ( bolded ? new BoldLabel( currentValue ) : new Label( currentValue ));
			
		this.addComponent( label );
				
	}

	public void setStyle( String style ) {
		
		String currentValue = label.getValue();
		
		this.removeComponent( label );

		label = new StyledLabel( currentValue, style );
			
		this.addComponent( label );
				
	}

	public void setValue( String value ) {
		
		label.setValue( value );
	}

	
}
