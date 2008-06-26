package org.mifos.config;

public class GeneralConfig {
	
	public static final String PerCenterTimeOutForBulkEntry = "GeneralConfig.PerCenterTimeOutForBulkEntry";
	public static final String MaxPointsPerPPISurvey = "GeneralConfig.MaxPointsPerPPISurvey";
	
	public static Integer getPerCenterTimeOutForBulkEntry()
	{
		Integer defaultValue = 10;
		Integer perCenterTimeOutForBulkEntry = defaultValue;
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(PerCenterTimeOutForBulkEntry))
			perCenterTimeOutForBulkEntry = configMgr.getInteger(PerCenterTimeOutForBulkEntry, defaultValue);
		return perCenterTimeOutForBulkEntry;
	}
	
	public static int getMaxPointsPerPPISurvey()
	{
		int maxPointsPerPPISurvey = 101;  // default value is 101
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(MaxPointsPerPPISurvey))
			maxPointsPerPPISurvey = configMgr.getInt(MaxPointsPerPPISurvey);
		return maxPointsPerPPISurvey;
	}

}
