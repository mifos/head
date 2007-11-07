package org.mifos.config;

import org.apache.commons.configuration.Configuration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.FilePaths;
import java.util.ArrayList;
import static org.junit.Assert.assertTrue;

import org.mifos.config.Localization;;

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
	
	//Configuration configuration;
	//private static final String LocalizationCountryCode="Localization.CountryCode";
	//private static final String LocalizationLanguageCode="Localization.LanguageCode";
	
	
	
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
	public void testGetDoubleValueForCurrentLocale()
	{
		String doubleValueString = "2.59";
		Double dValue = 2.59;
		Localization localization = Localization.getInstance();
		ConfigLocale savedConfigLocale = localization.getConfigLocale();
		ConfigLocale newConfigLocale = new ConfigLocale();
		String countryCode = "GB";
		String languageCode = "EN";
		newConfigLocale.setCountryCode(countryCode);
		newConfigLocale.setLanguageCode(languageCode);
		localization.setConfigLocale(newConfigLocale);
		Double dNumber = localization.getDoubleValueForCurrentLocale(doubleValueString);
		assertEquals(dValue, dNumber);
		String doubleValueString2 = localization.getDoubleValueStringForCurrentLocale(dValue);
		assertEquals(doubleValueString, doubleValueString2);
		restoreConfigSetup(localization, savedConfigLocale);
	}
	
	@Test 
	public void testGetCountryCodeAndGetLanguageCode() {
		Localization localization = Localization.getInstance();
		ConfigLocale savedConfigLocale = localization.getConfigLocale();
		ConfigLocale newConfigLocale = new ConfigLocale();
		String countryCode = "GB";
		String languageCode = "EN";
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
			localization.getCountryCode();
		}
		catch (RuntimeException e)
		{
			assertEquals(e.getMessage(), "The country code is not defined in the config file.");
			restoreConfigSetupToConfigFile(localization, savedConfigLocale);
		}
		
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
		assertEquals(languageName, localization.getLanguageName());
		assertEquals(countryName, localization.getCountryName());
		restoreConfigSetup(localization, savedConfigLocale);
	}
	
	@Test 
	public void testGetSupportedLocale() {
		
		short localeId = 1;
		String countryCode = "GB";
		String languageCode = "EN";
		Localization localization = Localization.getInstance();
		ConfigLocale savedConfigLocale = localization.getConfigLocale();
		ConfigLocale newConfigLocale = new ConfigLocale();
		newConfigLocale.setCountryCode(countryCode);
		newConfigLocale.setLanguageCode(languageCode);
		localization.setConfigLocale(newConfigLocale);
		localization.refresh();
		assertEquals(localeId, localization.getSupportedLocale().getLocaleId().shortValue());
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
	
	@Test
	public void testGetDateSeparator() {
		String separator = "/";
		String countryCode = "GB";
		String languageCode = "EN";
		Localization localization = Localization.getInstance();
		ConfigLocale savedConfigLocale = localization.getConfigLocale();
		ConfigLocale newConfigLocale = new ConfigLocale();
		newConfigLocale.setCountryCode(countryCode);
		newConfigLocale.setLanguageCode(languageCode);
		localization.setConfigLocale(newConfigLocale);
		assertEquals(separator, localization.getDateSeparator());
		restoreConfigSetup(localization, savedConfigLocale);
		
	}
	/*@Test 
	public void testGetLanguageCode() {
		
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		String languageCode = "EN";
		configMgr.setProperty(LocalizationLanguageCode, languageCode);
		//		 return value from Localization class has to be the value defined in the config file
		assertEquals(languageCode, Localization.getInstance().getLanguageCode());
		// clear the country code property from the config file
		configMgr.clearProperty(LocalizationLanguageCode); // add back for following tests
		// should throw exception
		try
		{
			Localization.getInstance().getLanguageCode();
		}
		catch (RuntimeException e)
		{
			assertEquals(e.getMessage(), "The language code is not defined in the config file.");
			configMgr.addProperty(LocalizationLanguageCode, languageCode);
		}
		
	}
	
	@Test 
	
	
	@Test 
	public void testGetCountryName() {
	
		String savedCountryCode = Localization.getInstance().getCountryCode();
		String countryName = "United Kingdom";
		String countryCode = "GB";
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		configMgr.setProperty(LocalizationCountryCode, countryCode);
		assertEquals(countryName, Localization.getInstance().getCountryName());
		configMgr.setProperty(LocalizationCountryCode, savedCountryCode);
	
	}
	
	@Test 
	public void testGetSupportedLocale() {
		
		short localeId = 1;
		String savedCountryCode = Localization.getInstance().getCountryCode();
		String savedLanguageCode = Localization.getInstance().getLanguageCode();
		String countryCode = "GB";
		String languageCode = "EN";
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		configMgr.setProperty(LocalizationCountryCode, countryCode);
		configMgr.setProperty(LocalizationLanguageCode, languageCode);

		assertEquals(localeId, Localization.getInstance().getSupportedLocale().getLocaleId().shortValue());
		configMgr.setProperty(LocalizationCountryCode, savedCountryCode);
		configMgr.setProperty(LocalizationLanguageCode, savedLanguageCode);
	}
	
	@Test
	public void testGetSupportedLocaleIds() {
		
	
		short localeId = 1;
		String savedCountryCode = Localization.getInstance().getCountryCode();
		String savedLanguageCode = Localization.getInstance().getLanguageCode();
		String countryCode = "GB";
		String languageCode = "EN";
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		configMgr.setProperty(LocalizationCountryCode, countryCode);
		configMgr.setProperty(LocalizationLanguageCode, languageCode);
		ArrayList<Short> locales = Localization.getInstance().getSupportedLocaleIds();
		assertEquals(localeId, locales.get(0).shortValue());
		configMgr.setProperty(LocalizationCountryCode, savedCountryCode);
		configMgr.setProperty(LocalizationLanguageCode, savedLanguageCode);
		
	}*/
	

	
	

}

