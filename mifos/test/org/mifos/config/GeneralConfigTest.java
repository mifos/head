package org.mifos.config;

import java.math.RoundingMode;

import org.apache.commons.configuration.Configuration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.FilePaths;

import junit.framework.JUnit4TestAdapter;
import org.mifos.config.GeneralConfig;

public class GeneralConfigTest extends MifosTestCase{
	
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
		// clear the RoundingRule property from the config file so should get the default value
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
		// clear the RoundingRule property from the config file so should get the default value
		configMgr.clearProperty(GeneralConfig.MaxPointsPerPPISurvey);
		int defaultValue = GeneralConfig.getMaxPointsPerPPISurvey();
		int expectedDefaultValue = 101;
		assertEquals(defaultValue, expectedDefaultValue);
		// save it back
		configMgr.setProperty(GeneralConfig.MaxPointsPerPPISurvey, configuredValue);
		
	}

}
