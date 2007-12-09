package org.mifos.framework.util;


import junit.framework.JUnit4TestAdapter;

import org.joda.time.DateMidnight;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.config.Localization;
import java.util.Locale;
import org.mifos.framework.MifosTestCase;

public class TestLocalizationConverter extends MifosTestCase{
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestLocalizationConverter.class);
	}
	
	public void testGetDecimalFormatSymbol()
	{
		Locale locale = Localization.getInstance().getMainLocale();
		LocalizationConverter converter = LocalizationConverter.getInstance();
		char sep = '.';
		if (locale.getCountry().equalsIgnoreCase("GB") && locale.getLanguage().equalsIgnoreCase("EN"))
			assertEquals(sep, converter.getDecimalFormatSymbol());
		converter.setCurrentLocale(new Locale("IS", "is"));
		sep = ',';
		assertEquals(sep, converter.getDecimalFormatSymbol());
		converter.setCurrentLocale(locale);

	}
	
	public void testGetDoubleValueStringForCurrentLocale()
	{
		//long expectedDate = new DateMidnight(2005, 03, 04).getMillis();
		//java.sql.Date result = DateUtils.getDateAsSentFromBrowser("04/03/2005");
		//assertEquals(expectedDate, result.getTime());
		
		String doubleValueString = "2.59";
		Double dValue = 2.59;
		Locale locale = Localization.getInstance().getMainLocale();
		LocalizationConverter converter = LocalizationConverter.getInstance();
		String dString = converter.getDoubleValueStringForCurrentLocale(dValue);
		if (locale.getCountry().equalsIgnoreCase("GB") && locale.getLanguage().equalsIgnoreCase("EN"))
			assertEquals(doubleValueString, dString);
		converter.setCurrentLocale(new Locale("IS", "is"));
		doubleValueString = "2,59";
		dString = converter.getDoubleValueStringForCurrentLocale(dValue);
		assertEquals(doubleValueString, dString);
		converter.setCurrentLocale(locale);
	}
	
	
	public void testGetDateSeparator() {
		String separator = "/";
		Locale locale = Localization.getInstance().getMainLocale();
		LocalizationConverter converter = LocalizationConverter.getInstance();
		String dateSeparator = converter.getDateSeparatorForCurrentLocale();
		if (locale.getCountry().equalsIgnoreCase("GB") && locale.getLanguage().equalsIgnoreCase("EN"))
			assertEquals(separator, dateSeparator);
		converter.setCurrentLocale(new Locale("IS", "is"));
		dateSeparator = converter.getDateSeparatorForCurrentLocale();
		assertEquals(".", dateSeparator);
		converter.setCurrentLocale(locale);
		
	}
	

	public void testGetDoubleValueForCurrentLocale()
	{
		String doubleValueString = "2.59";
		Double dValue = 2.59;
		Locale locale = Localization.getInstance().getMainLocale();
		LocalizationConverter converter = LocalizationConverter.getInstance();
		Double dNumber = converter.getDoubleValueForCurrentLocale(doubleValueString);
		if (locale.getCountry().equalsIgnoreCase("GB") && locale.getLanguage().equalsIgnoreCase("EN"))
			assertEquals(dNumber, dValue);
		converter.setCurrentLocale(new Locale("IS", "is"));
		doubleValueString = "2,59";
		dNumber = converter.getDoubleValueForCurrentLocale(doubleValueString);
		assertEquals(dNumber, dValue);
		converter.setCurrentLocale(locale);
		
	}
	
	
	

}
