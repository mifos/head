package org.mifos.config;

public class ConfigLocale {
	
	private static final String LocalizationCountryCode="Localization.CountryCode";
	private static final String LocalizationLanguageCode="Localization.LanguageCode";
	
	private String countryCode;
	private String languageCode;
	
	public ConfigLocale(String languageCode, String countryCode)
	{
		this.countryCode = countryCode;
		this.languageCode = languageCode;
	}
	
	public ConfigLocale()
	{
		load();
	}
	
	// for testing purpose
	public void clearCountryCode()
	{
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		configMgr.clearProperty(LocalizationCountryCode);
	}
	
	//	 for testing purpose
	public void clearLanguageCode()
	{
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		configMgr.clearProperty(LocalizationLanguageCode);
	}

	
	//	 for testing purpose
	public void setCountryCodeToConfigFile()
	{
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		configMgr.addProperty(LocalizationCountryCode, countryCode);
	}
	
	//	 for testing purpose
	public void setLanguageCodeToConfigFile()
	{
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		configMgr.addProperty(LocalizationLanguageCode, languageCode);
	}

	// save to the configuration object in memory, not write to file
	public void save()
	{
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		configMgr.setProperty(LocalizationLanguageCode, languageCode);
		configMgr.setProperty(LocalizationCountryCode, countryCode);
	}
	
	public void save(String countryCode, String languageCode)
	{
		this.languageCode = languageCode;
		this.countryCode = countryCode;
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		configMgr.setProperty(LocalizationLanguageCode, languageCode);
		configMgr.setProperty(LocalizationCountryCode, countryCode);
	}
	
	private void load()
	{
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(LocalizationCountryCode))
			countryCode = configMgr.getString(LocalizationCountryCode);
		else
			throw new RuntimeException("The country code is not defined in the config file.");
		if (configMgr.containsKey(LocalizationLanguageCode))
			languageCode = configMgr.getString(LocalizationLanguageCode);
		else
			throw new RuntimeException("The language code is not defined in the config file.");
	}
	
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getLanguageCode() {
		return languageCode;
	}
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}
	
}

