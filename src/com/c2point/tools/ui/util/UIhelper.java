package com.c2point.tools.ui.util;

import java.util.Locale;

import com.c2point.tools.InventoryUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.UI;

public class UIhelper {

	
	public static void fillCountryCombo( ComboBox field ) {
		String[] locales = Locale.getISOCountries();
		 
		for ( String countryCode : locales) {
	 
			Locale locale = new Locale( "", countryCode );

			field.addItem( locale.getCountry());
			field.setItemCaption( locale.getCountry(), locale.getDisplayCountry());
			
		}
	}
	
	public static boolean selectCountryInCombo( ComboBox field, String countryCode ) {
		
		boolean bRes = false;
		
		if ( countryCode == null ) {
			Locale obj = (( InventoryUI )UI.getCurrent()).getSessionData().getLocale();
			if ( obj != null && obj.getCountry() != null ) {
				countryCode = obj.getCountry();
			} else {
				countryCode = "FI";
			}
			
		}
		
		field.setValue( countryCode );
		
		bRes = true;
		
		return bRes;
	}

}
