package org.mifos.config;

import java.math.RoundingMode;

import org.apache.commons.configuration.Configuration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.FilePaths;

import junit.framework.JUnit4TestAdapter;
import org.mifos.config.GeneralConfig;

public class GeneralConfigTest extends MifosIntegrationTest{
	
	public GeneralConfigTest() throws SystemException, ApplicationException {
        super();
    }


    public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(GeneralConfigTest.class);
	}
	
	
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
	
	@Test 
	public void testGetPerCenterTimeOutForBulkEntry() {
		Integer configuredValue = GeneralConfig.getPerCenterTimeOutForBulkEntry();
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		Integer currentValue = 30;
		configMgr.setProperty(GeneralConfig.PerCenterTimeOutForBulkEntry, currentValue);
		assertEquals(currentValue, GeneralConfig.getPerCenterTimeOutForBulkEntry());
		// clear the PerCenterTimeOutForBulkEntry property from the config file so should get the default value
		configMgr.clearProperty(GeneralConfig.PerCenterTimeOutForBulkEntry);
		Integer defaultValue = GeneralConfig.getPerCenterTimeOutForBulkEntry();
		Integer expectedDefaultValue = 10;
		assertEquals(defaultValue, expectedDefaultValue);
		// save it back
		configMgr.setProperty(GeneralConfig.PerCenterTimeOutForBulkEntry, configuredValue);
		
	}
	
	@Test 
	public void testGetMaxPointsPerPPISurvey() {
		int configuredValue = GeneralConfig.getMaxPointsPerPPISurvey();
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		int currentValue = 30;
		configMgr.setProperty(GeneralConfig.MaxPointsPerPPISurvey, currentValue);
		assertEquals(currentValue, GeneralConfig.getMaxPointsPerPPISurvey());
		// clear the MaxPointsPerPPISurvey property from the config file so should get the default value
		configMgr.clearProperty(GeneralConfig.MaxPointsPerPPISurvey);
		int defaultValue = GeneralConfig.getMaxPointsPerPPISurvey();
		int expectedDefaultValue = 101;
		assertEquals(defaultValue, expectedDefaultValue);
		// save it back
		configMgr.setProperty(GeneralConfig.MaxPointsPerPPISurvey, configuredValue);
		
	}
	

	@Test 
	public void testGetBatchSizeForBatchJobs() {
		int configuredValue = GeneralConfig.getBatchSizeForBatchJobs();
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		int currentValue = 40;
		configMgr.setProperty(GeneralConfig.BatchSizeForBatchJobs, currentValue);
		assertEquals(currentValue, GeneralConfig.getBatchSizeForBatchJobs());
		// clear the BatchSizeForBatchJobs property from the config file so should get the default value
		configMgr.clearProperty(GeneralConfig.BatchSizeForBatchJobs);
		int defaultValue = GeneralConfig.getBatchSizeForBatchJobs();
		int expectedDefaultValue = 40;
		assertEquals(defaultValue, expectedDefaultValue);
		// save it back
		configMgr.setProperty(GeneralConfig.BatchSizeForBatchJobs, configuredValue);
		
	}
	
	@Test 
	public void testGetRecordCommittingSizeForBatchJobs() {
		int configuredValue = GeneralConfig.getRecordCommittingSizeForBatchJobs();
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		int currentValue = 500;
		configMgr.setProperty(GeneralConfig.RecordCommittingSizeForBatchJobs, currentValue);
		assertEquals(currentValue, GeneralConfig.getRecordCommittingSizeForBatchJobs());
		// clear the BatchSizeForBatchJobs property from the config file so should get the default value
		configMgr.clearProperty(GeneralConfig.RecordCommittingSizeForBatchJobs);
		int defaultValue = GeneralConfig.getRecordCommittingSizeForBatchJobs();
		int expectedDefaultValue = 1000;
		assertEquals(defaultValue, expectedDefaultValue);
		// save it back
		configMgr.setProperty(GeneralConfig.RecordCommittingSizeForBatchJobs, configuredValue);
		
	}

}
