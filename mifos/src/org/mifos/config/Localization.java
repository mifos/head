package org.mifos.config;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.application.configuration.persistence.ApplicationConfigurationPersistence;

public class Localization {
	
	private static final String LocalizationCountryCode="Localization.CountryCode";
	private static final String LocalizationLanguageCode="Localization.LanguageCode";
	private static Map<String, SupportedLocalesEntity> localeCache;
	
	private Localization() {
		localeCache = new ConcurrentHashMap<String, SupportedLocalesEntity>();
		
	}
	
	public void init()
	{
		initializeLocaleCache();
	}
	
	private static final Localization localization = 
		new Localization();

	public static Localization getInstance() {
		return localization;	
	}
	
	public  String getCountryCode()
	{
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(LocalizationCountryCode))
			return configMgr.getString(LocalizationCountryCode);
		else
			throw new RuntimeException("The country code is not defined in the config file.");
	}
	
	
	public String getLanguageCode()
	{
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(LocalizationLanguageCode))
			return configMgr.getString(LocalizationLanguageCode);
		else
			throw new RuntimeException("The language code is not defined in the config file.");
	}
	
	public String getLanguageName()
	{
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(LocalizationCountryCode) && configMgr.containsKey(LocalizationLanguageCode))
		{
			Locale locale = getLocale();
			return locale.getDisplayLanguage();
		}
		else
			throw new RuntimeException("The country code and language code are not defined in the config file.");
	}
	
	public String getCountryName()
	{
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(LocalizationCountryCode) && configMgr.containsKey(LocalizationLanguageCode))
		{
			Locale locale = getLocale();
			return locale.getDisplayCountry();
		}
		else
			throw new RuntimeException("The country code and language code are not defined in the config file.");
	}
	
	// get the language code and country code from config file and search cache for the matched localeId
	// return the localeId from table Supported_Locale
	public Short getLocaleId() {
		
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(LocalizationCountryCode) && configMgr.containsKey(LocalizationLanguageCode))
		{
			SupportedLocalesEntity supportedLocale = getSupportedLocale();
			return supportedLocale.getLocaleId();
		}
		else
			throw new RuntimeException("The country code and language code are not defined in the config file.");
		
	}
	
	public SupportedLocalesEntity getSupportedLocale()
	{
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(LocalizationCountryCode) && configMgr.containsKey(LocalizationLanguageCode))
		{
			String countryCode =  configMgr.getString(LocalizationCountryCode);
			String languageCode =  configMgr.getString(LocalizationLanguageCode);
			String localeKey = languageCode.toLowerCase() + "_" + countryCode.toUpperCase();
			SupportedLocalesEntity supportedLocale = localeCache.get(localeKey);
			if (supportedLocale == null)
				throw new RuntimeException("Failed to find the supported locale to match with the country code and language code defined in the config file.");
			return supportedLocale;
		}
		else
			throw new RuntimeException("The country code and language code are not defined in the config file.");
	}
	
	// from the language code and country code defined in the config return the java Locale class
	// instantiated using the language code and country code
	public Locale getLocale()
	{
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(LocalizationCountryCode) && configMgr.containsKey(LocalizationLanguageCode))
		{
			String countryCode =  configMgr.getString(LocalizationCountryCode);
			String languageCode =  configMgr.getString(LocalizationLanguageCode);
			Locale locale = new Locale(languageCode.toLowerCase(), countryCode.toUpperCase());
			return locale;
		}
		else
			throw new RuntimeException("The country code and language code are not defined in the config file.");
	}
	
	public ArrayList<Short> getSupportedLocaleIds()
	{
		ArrayList<Short> localeIds = new ArrayList<Short>();
		Object[] locales = localeCache.values().toArray();
		for (int i=0; i < locales.length; i++)
		{
			SupportedLocalesEntity entity = (SupportedLocalesEntity)locales[i];
			localeIds.add(entity.getLocaleId());
		}
		return localeIds;
	}
	
	private void initializeLocaleCache() {
		List<SupportedLocalesEntity> locales = 
			new ApplicationConfigurationPersistence().getSupportedLocale();
		localeCache.clear();
		for (SupportedLocalesEntity locale : locales) {

			localeCache.put(locale.getLanguage().getLanguageShortName()
					.toLowerCase()
					+ "_" + locale.getCountry().getCountryShortName().toUpperCase(), locale);
		}
		
	}
	

}
