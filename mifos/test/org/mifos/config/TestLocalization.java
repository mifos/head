package org.mifos.config;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.FilePaths;
import java.util.ArrayList;
import java.util.Locale;


import org.mifos.config.Localization;

public class TestLocalization extends MifosTestCase {
	
	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	
	
	@BeforeClass
	public static void init() throws Exception {
		MifosLogManager.configure(FilePaths.LOGFILE);
	}
	
	private void restoreConfigSetup(Localization localization, ConfigLocale savedConfigLocale)
	{
		localization.setConfigLocale(savedConfigLocale);
	}
	
	private void restoreConfigSetupToConfigFile(Localization localization, ConfigLocale savedConfigLocale)
	{
		localization.setCountryCodeLanguageCodeToConfigFile(savedConfigLocale);
	}
	
	
	@Test 
	public void testGetCountryCodeAndGetLanguageCode() {
		Localization localization = Localization.getInstance();
		ConfigLocale savedConfigLocale = localization.getConfigLocale();
		ConfigLocale newConfigLocale = new ConfigLocale();
		String countryCode = "GB";
		String languageCode = "en";
		newConfigLocale.setCountryCode(countryCode);
		newConfigLocale.setLanguageCode(languageCode);
		localization.setConfigLocale(newConfigLocale);
		localization.refresh();
		//configMgr.setProperty(LocalizationCountryCode, countryCode);
		assertEquals(countryCode, localization.getCountryCode());
		assertEquals(languageCode, localization.getLanguageCode());
		// clear the country code property from the config file
		localization.clearCountryCodeLanguageCodeFromConfigFile();
		// should throw exception
		try
		{
			localization.refresh();
		}
		catch (RuntimeException e)
		{
			assertEquals(e.getMessage(), "The country code is not defined in the config file.");
			
		}
		restoreConfigSetupToConfigFile(localization, savedConfigLocale);
		
	}
	
	@Test 
	public void testGetLanguageNameCountryName() {
		
		String languageName = "English";
		String languageCode = "EN";
		String countryName = "United Kingdom";
		String countryCode = "GB";
		Localization localization = Localization.getInstance();
		ConfigLocale savedConfigLocale = localization.getConfigLocale();
		ConfigLocale newConfigLocale = new ConfigLocale();
		newConfigLocale.setCountryCode(countryCode);
		newConfigLocale.setLanguageCode(languageCode);
		localization.setConfigLocale(newConfigLocale);
		localization.refresh();
		if (Locale.getDefault().equals(Locale.UK))
		{
			assertEquals(languageName, localization.getLanguageName());
			assertEquals(countryName, localization.getCountryName());
		}
		restoreConfigSetup(localization, savedConfigLocale);
	}
	
	
	
	private boolean findLocaleId(ArrayList<Short> locales, short localeId)
	{
		for (int i=0; i < locales.size(); i++)
			if (locales.get(i).shortValue() == localeId)
				return true;
		return false;
	}
	
	@Test
	public void testGetSupportedLocaleIds() {
		short localeId = 1;
		String countryCode = "GB";
		String languageCode = "EN";
		Localization localization = Localization.getInstance();
		ConfigLocale savedConfigLocale = localization.getConfigLocale();
		ConfigLocale newConfigLocale = new ConfigLocale();
		newConfigLocale.setCountryCode(countryCode);
		newConfigLocale.setLanguageCode(languageCode);
		localization.setConfigLocale(newConfigLocale);
		ArrayList<Short> locales = localization.getSupportedLocaleIds();
		assertTrue(findLocaleId(locales, localeId));
		restoreConfigSetup(localization, savedConfigLocale);
		
	}
	
	
	

}

