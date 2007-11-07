package org.mifos.config;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;


import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.application.configuration.persistence.ApplicationConfigurationPersistence;

public class Localization {
	
	private static Map<String, SupportedLocalesEntity> localeCache;
	private static SupportedLocalesEntity supportedLocaleEntity;
	private static Locale currentLocale;
	private static Short localeId = -1;
	private static ConfigLocale configLocale;
	private static String dateSeparator; 
	private static DecimalFormat decimalFormat;
	
	private Localization() {
		localeCache = new ConcurrentHashMap<String, SupportedLocalesEntity>();	
		configLocale = new ConfigLocale();
	}
	
	// After a new configLocale is set, refresh is called to refresh supportedLocale,locale and localeId 
	public void refresh()
	{
		loadMembers();
	}
	
	private void loadMembers()
	{
		supportedLocaleEntity = getSupportedLocaleFromConfig();
		currentLocale = getLocaleFromConfig();
		localeId = getLocaleIdFromConfigAndCache();
		dateSeparator = getDateSeparatorForCurrentLocale();
		decimalFormat = getDecimalFormatForCurrentLocale();
	}
	
	public void init()
	{
		initializeLocaleCache();
		loadMembers();
	}
	
	private static final Localization localization = 
		new Localization();

	public static Localization getInstance() {
		return localization;	
	}
	
	public  String getCountryCode()
	{
		if (configLocale != null)
			return configLocale.getCountryCode();
		else
		{
			configLocale = new ConfigLocale();
			return configLocale.getCountryCode();
		}
		
	}
	
	
	// for the testing purpose
	public void clearCountryCodeLanguageCodeFromConfigFile()
	{
		configLocale.clearCountryCode();
		configLocale.clearLanguageCode();
		configLocale = null;
	}
	
	public void setCountryCodeLanguageCodeToConfigFile(ConfigLocale locale)
	{
		configLocale = locale;
		configLocale.setCountryCodeToConfigFile();
		configLocale.setLanguageCodeToConfigFile();
		refresh();
	}
	
	
	public String getLanguageCode()
	{
		if (configLocale != null)
			return configLocale.getLanguageCode();
		else
		{
			configLocale = new ConfigLocale();
			return configLocale.getLanguageCode();
		}
		
	}
	
	public String getLanguageName()
	{
		
		if (currentLocale != null)
			return currentLocale.getDisplayLanguage();
		else
		{
			currentLocale = getLocale();
			return currentLocale.getDisplayLanguage();
		}
	}
	
	public String getCountryName()
	{
		
		if (currentLocale != null)
			return currentLocale.getDisplayCountry();
		else
		{
			currentLocale = getLocale();
			return currentLocale.getDisplayCountry();
		}
		
	}
	
	// get the language code and country code from config file and search cache for the matched localeId
	// return the localeId from table Supported_Locale
	private Short getLocaleIdFromConfigAndCache() {
		
		if (supportedLocaleEntity != null)
			return supportedLocaleEntity.getLocaleId();
		else
		{
			supportedLocaleEntity = getSupportedLocale();
			return supportedLocaleEntity.getLocaleId();
		}
	
	}
	
	public Short getLocaleId()
	{
		if (localeId > -1)
			return localeId;
		localeId = getLocaleIdFromConfigAndCache();
		return localeId;
	}
	
	public SupportedLocalesEntity getSupportedLocale()
	{
		if (supportedLocaleEntity != null)
			return supportedLocaleEntity;
		else
		{
			supportedLocaleEntity = getSupportedLocaleFromConfig();
			return supportedLocaleEntity;
		}
			
	}
	
	private SupportedLocalesEntity getSupportedLocaleFromConfig()
	{
		
		if (configLocale == null)
			configLocale = new ConfigLocale();
		String localeKey = configLocale.getLanguageCode().toLowerCase() + "_" + 
		configLocale.getCountryCode().toUpperCase();
		supportedLocaleEntity = localeCache.get(localeKey);
		if (supportedLocaleEntity == null)
			throw new RuntimeException("Failed to find the supported locale to match with the country code and language code defined in the config file.");
		return supportedLocaleEntity;
		
	}
	
	public Locale getLocale()
	{
		if (currentLocale != null)
			return currentLocale;
		else
		{
			currentLocale = getLocaleFromConfig();
			return currentLocale;
		}
			
	}
	
	// from the language code and country code defined in the config return the java Locale class
	// instantiated using the language code and country code
	private Locale getLocaleFromConfig()
	{
		
		if (configLocale == null)
			configLocale = new ConfigLocale();
		Locale locale = new Locale(configLocale.getLanguageCode().toLowerCase(), 
				configLocale.getCountryCode().toUpperCase());
		return locale;
		
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
	
	private DecimalFormat getDecimalFormatForCurrentLocale()
	{
		if (currentLocale == null)
			currentLocale = getLocaleFromConfig();
		Locale[] locales = NumberFormat.getInstance().getAvailableLocales();
		Locale tempLocale = null;
		boolean find = false;
		DecimalFormat decimalFormat = null;
		for (int i=0; i < locales.length; i++)
		{
			tempLocale = locales[i];
			if (tempLocale.getCountry().equals(currentLocale.getCountry()) && 
					(tempLocale.getLanguage().equals(currentLocale.getLanguage())))
			{
				find = true;
				break;
			}
		}
		if (find == false)
			throw new RuntimeException("NumberFormat doesn't support this country code: " +
					currentLocale.getCountry() + " and language code: " + currentLocale.getLanguage());
		NumberFormat format = NumberFormat.getInstance(currentLocale);
		format = DecimalFormat.getInstance(currentLocale);
		if (format instanceof DecimalFormat) 
			decimalFormat = (DecimalFormat)format;
		return decimalFormat;
	}
	
	public Double getDoubleValueForCurrentLocale(String doubleValueString)
	{
		if (decimalFormat == null)
			decimalFormat = getDecimalFormatForCurrentLocale();
		Double dNum = null;
		try
		{
			Number num = decimalFormat.parse(doubleValueString);
			dNum = num.doubleValue();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e.getMessage());
		}
		return dNum;
	}
	
	public String getDoubleValueStringForCurrentLocale(Double dNumber)
	{
		if (decimalFormat == null)
			decimalFormat = getDecimalFormatForCurrentLocale();
		
		return decimalFormat.format(dNumber);
		
	}
	
	public String getDateSeparatorForCurrentLocale()
	{
		if (currentLocale == null)
			currentLocale = getLocaleFromConfig();
		Locale[] locales = DateFormat.getInstance().getAvailableLocales();
		Locale tempLocale = null;
		boolean find = false;
		for (int i=0; i < locales.length; i++)
		{
			tempLocale = locales[i];
			if (tempLocale.getCountry().equals(currentLocale.getCountry()) && 
					(tempLocale.getLanguage().equals(currentLocale.getLanguage())))
			{
				find = true;
				break;
			}
		}
		if (find == false)
			throw new RuntimeException("DateFormat doesn't support this country code: " +
					currentLocale.getCountry() + " and language code: " + currentLocale.getLanguage());
		
		String separator = "";
		DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, currentLocale);
		String now = format.format(new java.util.Date());
		char chArray[] = now.toCharArray();
		for (int i = 0; i < chArray.length; i++) 
		{
			if (Character.isDigit(chArray[i]) == false)
			{
				separator = String.valueOf(chArray[i]);
				break;
			}
		}
		return separator;
		
	}
	
	public String getDateSeparator()
	{
		if (dateSeparator != null)
			return dateSeparator;
		else
		{
			dateSeparator = getDateSeparatorForCurrentLocale();
			return dateSeparator;
		}
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

	public static ConfigLocale getConfigLocale() {
		return configLocale;
	}

	public void setConfigLocale(ConfigLocale locale) {
		if (configLocale.equals(locale) == false)
		{
			Localization.configLocale = locale;
			refresh();
		}
	}
	

}
