package org.mifos.config;

import org.apache.commons.configuration.Configuration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.FilePaths;
import java.util.ArrayList;

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
	
	Configuration configuration;
	private static final String LocalizationCountryCode="Localization.CountryCode";
	private static final String LocalizationLanguageCode="Localization.LanguageCode";
	
	
	
	@BeforeClass
	public static void init() throws Exception {
		MifosLogManager.configure(FilePaths.LOGFILE);
	}
	
	@Test 
	public void testGetCountryCode() {
		
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		String savedCountryCode = Localization.getInstance().getCountryCode();
		String countryCode = "GB";
		configMgr.setProperty(LocalizationCountryCode, countryCode);
		assertEquals(countryCode, Localization.getInstance().getCountryCode());
		// clear the country code property from the config file
		configMgr.clearProperty(LocalizationCountryCode);
		// should throw exception
		try
		{
			Localization.getInstance().getCountryCode();
		}
		catch (RuntimeException e)
		{
			assertEquals(e.getMessage(), "The country code is not defined in the config file.");
			configMgr.addProperty(LocalizationCountryCode, savedCountryCode); // add back for following tests
		}
		
	}
	@Test 
	public void testGetLanguageCode() {
		
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		String languageCode = "EN";
		// return value from Localization class has to be the value defined in the config file
		assertEquals(languageCode, Localization.getInstance().getLanguageCode());
		configMgr.addProperty(LocalizationLanguageCode, languageCode);
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
	public void testGetLanguageName() {
	
		String languageName = "English";
		String languageCode = "EN";
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		configMgr.setProperty(LocalizationLanguageCode, languageCode);
		assertEquals(languageName, Localization.getInstance().getLanguageName());
	}
	
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
		
		short size = 1;
		short localeId = 1;
		String savedCountryCode = Localization.getInstance().getCountryCode();
		String savedLanguageCode = Localization.getInstance().getLanguageCode();
		String countryCode = "GB";
		String languageCode = "EN";
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		configMgr.setProperty(LocalizationCountryCode, countryCode);
		configMgr.setProperty(LocalizationLanguageCode, languageCode);
		ArrayList<Short> locales = Localization.getInstance().getSupportedLocaleIds();
		assertEquals(size, locales.size());
		assertEquals(localeId, locales.get(0).shortValue());
		configMgr.setProperty(LocalizationCountryCode, savedCountryCode);
		configMgr.setProperty(LocalizationLanguageCode, savedLanguageCode);
		
	}
	

	
	

}

