package com.c2point.tools.utils.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;

public class Locales {

	public static final Locale LOCALE_NONE = new Locale("-");


	private static final String[] iso3166 = new String[] { "AFGHANISTAN", "AF",
		"ÃLAND ISLANDS", "AX", "ALBANIA", "AL", "ALGERIA", "DZ",
		"AMERICAN SAMOA", "AS", "ANDORRA", "AD", "ANGOLA", "AO",
		"ANGUILLA", "AI", "ANTARCTICA", "AQ", "ANTIGUA AND BARBUDA", "AG",
		"ARGENTINA", "AR", "ARMENIA", "AM", "ARUBA", "AW", "AUSTRALIA",
		"AU", "AUSTRIA", "AT", "AZERBAIJAN", "AZ", "BAHAMAS", "BS",
		"BAHRAIN", "BH", "BANGLADESH", "BD", "BARBADOS", "BB", "BELARUS",
		"BY", "BELGIUM", "BE", "BELIZE", "BZ", "BENIN", "BJ", "BERMUDA",
		"BM", "BHUTAN", "BT", "BOLIVIA", "BO", "BOSNIA AND HERZEGOVINA",
		"BA", "BOTSWANA", "BW", "BOUVET ISLAND", "BV", "BRAZIL", "BR",
		"BRITISH INDIAN OCEAN TERRITORY", "IO", "BRUNEI DARUSSALAM", "BN",
		"BULGARIA", "BG", "BURKINA FASO", "BF", "BURUNDI", "BI",
		"CAMBODIA", "KH", "CAMEROON", "CM", "CANADA", "CA", "CAPE VERDE",
		"CV", "CAYMAN ISLANDS", "KY", "CENTRAL AFRICAN REPUBLIC", "CF",
		"CHAD", "TD", "CHILE", "CL", "CHINA", "CN", "CHRISTMAS ISLAND",
		"CX", "COCOS (KEELING) ISLANDS", "CC", "COLOMBIA", "CO", "COMOROS",
		"KM", "CONGO", "CG", "CONGO, THE DEMOCRATIC REPUBLIC OF THE", "CD",
		"COOK ISLANDS", "CK", "COSTA RICA", "CR", "CÃTE D'IVOIRE", "CI",
		"CROATIA", "HR", "CUBA", "CU", "CYPRUS", "CY", "CZECH REPUBLIC",
		"CZ", "DENMARK", "DK", "DJIBOUTI", "DJ", "DOMINICA", "DM",
		"DOMINICAN REPUBLIC", "DO", "ECUADOR", "EC", "EGYPT", "EG",
		"EL SALVADOR", "SV", "EQUATORIAL GUINEA", "GQ", "ERITREA", "ER",
		"ESTONIA", "EE", "ETHIOPIA", "ET", "FALKLAND ISLANDS (MALVINAS)",
		"FK", "FAROE ISLANDS", "FO", "FIJI", "FJ", "FINLAND", "FI",
		"FRANCE", "FR", "FRENCH GUIANA", "GF", "FRENCH POLYNESIA", "PF",
		"FRENCH SOUTHERN TERRITORIES", "TF", "GABON", "GA", "GAMBIA", "GM",
		"GEORGIA", "GE", "GERMANY", "DE", "GHANA", "GH", "GIBRALTAR", "GI",
		"GREECE", "GR", "GREENLAND", "GL", "GRENADA", "GD", "GUADELOUPE",
		"GP", "GUAM", "GU", "GUATEMALA", "GT", "GUERNSEY", "GG", "GUINEA",
		"GN", "GUINEA-BISSAU", "GW", "GUYANA", "GY", "HAITI", "HT",
		"HEARD ISLAND AND MCDONALD ISLANDS", "HM",
		"HOLY SEE (VATICAN CITY STATE)", "VA", "HONDURAS", "HN",
		"HONG KONG", "HK", "HUNGARY", "HU", "ICELAND", "IS", "INDIA", "IN",
		"INDONESIA", "ID", "IRAN, ISLAMIC REPUBLIC OF", "IR", "IRAQ", "IQ",
		"IRELAND", "IE", "ISLE OF MAN", "IM", "ISRAEL", "IL", "ITALY",
		"IT", "JAMAICA", "JM", "JAPAN", "JP", "JERSEY", "JE", "JORDAN",
		"JO", "KAZAKHSTAN", "KZ", "KENYA", "KE", "KIRIBATI", "KI",
		"KOREA, DEMOCRATIC PEOPLE'S REPUBLIC OF", "KP",
		"KOREA, REPUBLIC OF", "KR", "KUWAIT", "KW", "KYRGYZSTAN", "KG",
		"LAO PEOPLE'S DEMOCRATIC REPUBLIC", "LA", "LATVIA", "LV",
		"LEBANON", "LB", "LESOTHO", "LS", "LIBERIA", "LR",
		"LIBYAN ARAB JAMAHIRIYA", "LY", "LIECHTENSTEIN", "LI", "LITHUANIA",
		"LT", "LUXEMBOURG", "LU", "MACAO", "MO",
		"MACEDONIA, THE FORMER YUGOSLAV REPUBLIC OF", "MK", "MADAGASCAR",
		"MG", "MALAWI", "MW", "MALAYSIA", "MY", "MALDIVES", "MV", "MALI",
		"ML", "MALTA", "MT", "MARSHALL ISLANDS", "MH", "MARTINIQUE", "MQ",
		"MAURITANIA", "MR", "MAURITIUS", "MU", "MAYOTTE", "YT", "MEXICO",
		"MX", "MICRONESIA, FEDERATED STATES OF", "FM",
		"MOLDOVA, REPUBLIC OF", "MD", "MONACO", "MC", "MONGOLIA", "MN",
		"MONTENEGRO", "ME", "MONTSERRAT", "MS", "MOROCCO", "MA",
		"MOZAMBIQUE", "MZ", "MYANMAR", "MM", "NAMIBIA", "NA", "NAURU",
		"NR", "NEPAL", "NP", "NETHERLANDS", "NL", "NETHERLANDS ANTILLES",
		"AN", "NEW CALEDONIA", "NC", "NEW ZEALAND", "NZ", "NICARAGUA",
		"NI", "NIGER", "NE", "NIGERIA", "NG", "NIUE", "NU",
		"NORFOLK ISLAND", "NF", "NORTHERN MARIANA ISLANDS", "MP", "NORWAY",
		"NO", "OMAN", "OM", "PAKISTAN", "PK", "PALAU", "PW",
		"PALESTINIAN TERRITORY, OCCUPIED", "PS", "PANAMA", "PA",
		"PAPUA NEW GUINEA", "PG", "PARAGUAY", "PY", "PERU", "PE",
		"PHILIPPINES", "PH", "PITCAIRN", "PN", "POLAND", "PL", "PORTUGAL",
		"PT", "PUERTO RICO", "PR", "QATAR", "QA", "REUNION", "RE",
		"ROMANIA", "RO", "RUSSIAN FEDERATION", "RU", "RWANDA", "RW",
		"SAINT BARTHÃLEMY", "BL", "SAINT HELENA", "SH",
		"SAINT KITTS AND NEVIS", "KN", "SAINT LUCIA", "LC", "SAINT MARTIN",
		"MF", "SAINT PIERRE AND MIQUELON", "PM",
		"SAINT VINCENT AND THE GRENADINES", "VC", "SAMOA", "WS",
		"SAN MARINO", "SM", "SAO TOME AND PRINCIPE", "ST", "SAUDI ARABIA",
		"SA", "SENEGAL", "SN", "SERBIA", "RS", "SEYCHELLES", "SC",
		"SIERRA LEONE", "SL", "SINGAPORE", "SG", "SLOVAKIA", "SK",
		"SLOVENIA", "SI", "SOLOMON ISLANDS", "SB", "SOMALIA", "SO",
		"SOUTH AFRICA", "ZA",
		"SOUTH GEORGIA AND THE SOUTH SANDWICH ISLANDS", "GS", "SPAIN",
		"ES", "SRI LANKA", "LK", "SUDAN", "SD", "SURINAME", "SR",
		"SVALBARD AND JAN MAYEN", "SJ", "SWAZILAND", "SZ", "SWEDEN", "SE",
		"SWITZERLAND", "CH", "SYRIAN ARAB REPUBLIC", "SY",
		"TAIWAN, PROVINCE OF CHINA", "TW", "TAJIKISTAN", "TJ",
		"TANZANIA, UNITED REPUBLIC OF", "TZ", "THAILAND", "TH",
		"TIMOR-LESTE", "TL", "TOGO", "TG", "TOKELAU", "TK", "TONGA", "TO",
		"TRINIDAD AND TOBAGO", "TT", "TUNISIA", "TN", "TURKEY", "TR",
		"TURKMENISTAN", "TM", "TURKS AND CAICOS ISLANDS", "TC", "TUVALU",
		"TV", "UGANDA", "UG", "UKRAINE", "UA", "UNITED ARAB EMIRATES",
		"AE", "UNITED KINGDOM", "GB", "UNITED STATES", "US",
		"UNITED STATES MINOR OUTLYING ISLANDS", "UM", "URUGUAY", "UY",
		"UZBEKISTAN", "UZ", "VANUATU", "VU", "VENEZUELA", "VE", "VIET NAM",
		"VN", "VIRGIN ISLANDS, BRITISH", "VG", "VIRGIN ISLANDS, U.S.",
		"VI", "WALLIS AND FUTUNA", "WF", "WESTERN SAHARA", "EH", "YEMEN",
		"YE", "ZAMBIA", "ZM", "ZIMBABWE", "ZW" };
		
	public static final Object iso3166_PROPERTY_NAME = "name";
	public static final Object iso3166_PROPERTY_SHORT = "short";
	public static final Object iso3166_PROPERTY_FLAG = "flag";

	public static final Locale LOCALE_FI = new Locale("fi", "FI");

	public static final Locale LOCALE_ET = new Locale("et", "FI");

	public static final Locale LOCALE_EN = new Locale("en", "FI");

	public static final Locale LOCALE_SV = new Locale("sv", "FI");

	public static final Locale LOCALE_RU = new Locale("ru", "FI");

	public static final Locale DEFAULT_LOCALE = LOCALE_FI;

/*	
	public static final Object locale_PROPERTY_LOCALE = "locale";
	public static final Object locale_PROPERTY_NAME = "name";
	private static final String[][] locales = { 
		{ "fi", "FI", "Finnish" },
		{ "de", "DE", "German" }, 
		{ "en", "US", "US - English" },
		{ "sv", "SE", "Swedish" } };	
*/	
	public static List<Locale> getAvailableLocales() {
		ArrayList<Locale> locales = new ArrayList<Locale>();
		locales.add( LOCALE_FI );
		locales.add( LOCALE_EN );
//		locales.add( LOCALE_SV );
		locales.add( LOCALE_ET );
		locales.add( LOCALE_RU );

		return locales;
	}
	
	/**
	 * Load resource bundle for given Locale.
	 * 
	 * @param l
	 *            Locale of the resource bundle
	 * @return ResourceBundle or null if not found.
	 */
	public static ResourceBundle loadBundle(String bundleName, Locale l) {
		ResourceBundle b = null;
		try {
			b = ResourceBundle.getBundle(bundleName, l);
		} catch ( MissingResourceException e ) {
			b = null;
		}
		if (b == null) {
			System.err.println("Translations " + bundleName + " not found for locale '" + l + "'");
		} else {
			// System.err.println("Loaded translations " + bundleName
			// + " for locale '" + l + "'");
		}
		return b;
	}

public static IndexedContainer getISO3166Container() {

	IndexedContainer c = new IndexedContainer();
	fillIso3166Container(c);
	return c;
}

@SuppressWarnings("unchecked")
private static void fillIso3166Container(IndexedContainer container) {
	
	container.addContainerProperty( iso3166_PROPERTY_NAME, String.class, null);
	container.addContainerProperty( iso3166_PROPERTY_SHORT, String.class, null);
	container.addContainerProperty( iso3166_PROPERTY_FLAG, Resource.class, null);
	
	for (int i = 0; i < iso3166.length; i++) {
	
		String name = iso3166[i++];
		String id = iso3166[i];
		
		Item item = container.addItem( id );
		
		item.getItemProperty( iso3166_PROPERTY_NAME ).setValue(name);
		item.getItemProperty( iso3166_PROPERTY_SHORT ).setValue(id);
		item.getItemProperty( iso3166_PROPERTY_FLAG ).setValue(
						new ThemeResource( "flags/" + id.toLowerCase() + ".gif" ));
		
	}
	
	container.sort( 
			new Object[] { iso3166_PROPERTY_NAME },
			new boolean[] { true });
	}	
}
