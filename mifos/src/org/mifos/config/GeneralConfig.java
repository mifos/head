package org.mifos.config;

public class GeneralConfig {
	
	public static final String PerCenterTimeOutForBulkEntry = "GeneralConfig.PerCenterTimeOutForBulkEntry";
	
	public static Integer getPerCenterTimeOutForBulkEntry()
	{
		Integer defaultValue = 10;
		Integer perCenterTimeOutForBulkEntry = defaultValue;
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(PerCenterTimeOutForBulkEntry))
			perCenterTimeOutForBulkEntry = configMgr.getInteger(PerCenterTimeOutForBulkEntry, defaultValue);
		return perCenterTimeOutForBulkEntry;
	}

}
