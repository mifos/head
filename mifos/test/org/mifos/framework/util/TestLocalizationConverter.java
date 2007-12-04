package org.mifos.framework.util;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.util.helpers.FilePaths;
import junit.framework.JUnit4TestAdapter;
import org.mifos.framework.util.LocalizationConverter;
import java.text.DateFormat;
import org.mifos.config.Localization;
import java.util.Locale;
import junit.framework.TestCase;

public class TestLocalizationConverter extends TestCase{
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestLocalizationConverter.class);
	}
	
	
	public void testGetDateSeparator() {
		String separator = "/";
		Locale locale = Localization.getInstance().getMainLocale();
		LocalizationConverter converter = LocalizationConverter.getInstance();
		String dateSeparator = converter.getDateSeparatorForCurrentLocale();
		if (locale.getCountry().equalsIgnoreCase("GB") && locale.getLanguage().equalsIgnoreCase("EN"))
			assertEquals(separator, dateSeparator);
		

		
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
		
	}
	
	

}
