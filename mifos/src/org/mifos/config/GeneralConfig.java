package org.mifos.config;

public class GeneralConfig {
	
	public static final String PerCenterTimeOutForBulkEntry = "GeneralConfig.PerCenterTimeOutForBulkEntry";
	public static final String MaxPointsPerPPISurvey = "GeneralConfig.MaxPointsPerPPISurvey";
	public static final String BatchSizeForBatchJobs = "GeneralConfig.BatchSizeForBatchJobs";
	public static final String RecordCommittingSizeForBatchJobs = "GeneralConfig.RecordCommittingSizeForBatchJobs";
	
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
	
	public static int getBatchSizeForBatchJobs()
	{
		int batchSizeForBatchJobs = 40;  // default value is 40
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(BatchSizeForBatchJobs))
			batchSizeForBatchJobs = configMgr.getInt(BatchSizeForBatchJobs);
		return batchSizeForBatchJobs;
	}
	
	public static int getRecordCommittingSizeForBatchJobs()
	{
		int committingRecordSizeForBatchJobs = 1000;  // default value is 1000
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(RecordCommittingSizeForBatchJobs))
			committingRecordSizeForBatchJobs = configMgr.getInt(RecordCommittingSizeForBatchJobs);
		return committingRecordSizeForBatchJobs;
	}
	
	

}
