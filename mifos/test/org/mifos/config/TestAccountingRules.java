package org.mifos.config;

import static org.junit.Assert.assertEquals;
import java.math.RoundingMode;
import org.apache.commons.configuration.Configuration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.util.helpers.FilePaths;
import junit.framework.JUnit4TestAdapter;
import org.mifos.config.AccountingRules;


public class TestAccountingRules {
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestAccountingRules.class);
	}
	
	Configuration configuration;
	private static final String AccountingRulesDigitsAfterDecimal = "AccountingRules.DigitsAfterDecimal";
	private static final String AccountingRulesRoundingRule = "AccountingRules.RoundingRule";
	private static final String AccountingRulesNumberOfInterestDays = "AccountingRules.NumberOfInterestDays";
	private static final String AccountingRulesAmountToBeRoundedTo="AccountingRules.AmountToBeRoundedTo";
	
	
	
	
	@BeforeClass
	public static void init() throws Exception {
		MifosLogManager.configure(FilePaths.LOGFILE);
		
	}
	
	@Test 
	public void testGetDigitsAfterDecimal() {
		Short defaultValue = 0;
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		Short digitsAfterDecimal = 1;
		configMgr.addProperty(AccountingRulesDigitsAfterDecimal, digitsAfterDecimal);
		// return value from accounting rules class has to be the value defined in the config file
		assertEquals(digitsAfterDecimal, AccountingRules.getDigitsAfterDecimal(defaultValue));
		// clear the DigitsAfterDecimal property from the config file
		configMgr.clearProperty(AccountingRulesDigitsAfterDecimal);
		// now the return value from accounting rules class has to be the default value (value from db)
		assertEquals(defaultValue, AccountingRules.getDigitsAfterDecimal(defaultValue));
		// value not defined in config and defaultValue is null
		defaultValue = null;
		// should throw exception
		try
		{
			AccountingRules.getDigitsAfterDecimal(defaultValue);
		}
		catch (RuntimeException e)
		{
			assertEquals(e.getMessage(), "The number of digits after decimal is not defined in the config file nor database.");
		}
		configMgr.clearProperty(AccountingRulesDigitsAfterDecimal);
		
	}

	
	@Test
	public void testGetAmountToBeRoundedTo() {
		Float defaultValue = (float)0.5;
		Float amountToBeRoundedTo = (float)0.01;
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		configMgr.addProperty(AccountingRulesAmountToBeRoundedTo, amountToBeRoundedTo);
		// return value from accounting rules class has to be the value defined in the config file
		assertEquals(amountToBeRoundedTo, AccountingRules.getAmountToBeRoundedTo(defaultValue));
		// clear the AmountToBeRoundedTo property from the config file
		configMgr.clearProperty(AccountingRulesAmountToBeRoundedTo);
		// now the return value from accounting rules class has to be the default value (value from db)
		assertEquals(defaultValue, AccountingRules.getAmountToBeRoundedTo(defaultValue));
	}
	
	@Test 
	public void testGetRoundingRule() {
		RoundingMode defaultValue = RoundingMode.CEILING;
		String roundingMode = "FLOOR";
		RoundingMode configRoundingMode = RoundingMode.FLOOR;
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		configMgr.addProperty(AccountingRulesRoundingRule, roundingMode);
		// return value from accounting rules class has to be the value defined in the config file
		assertEquals(configRoundingMode, AccountingRules.getRoundingRule(defaultValue));
		// clear the RoundingRule property from the config file
		configMgr.clearProperty(AccountingRulesRoundingRule);
		// now the return value from accounting rules class has to be the default value (value from db)
		assertEquals(defaultValue, AccountingRules.getRoundingRule(defaultValue));
		// now set a wrong rounding mode in config
		roundingMode = "UP";
		configMgr.addProperty(AccountingRulesRoundingRule, roundingMode);
		try
		{
			AccountingRules.getRoundingRule(defaultValue);
		}
		catch (RuntimeException e)
		{
			assertEquals(e.getMessage(), "The rounding mode defined in the config file is not CEILING nor FLOOR. It is "
					+ roundingMode);
		}
		configMgr.clearProperty(AccountingRulesRoundingRule);
	}
	
	
	@Test 
	public void testGetNumberOfInterestDays() {
		Short interestDaysInConfig = AccountingRules.getNumberOfInterestDays();
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		Short insertedDays = 365;
		configMgr.setProperty(AccountingRulesNumberOfInterestDays, insertedDays);
		assertEquals(insertedDays, AccountingRules.getNumberOfInterestDays());
		insertedDays = 360;
		// set new value
		configMgr.setProperty(AccountingRulesNumberOfInterestDays, insertedDays);
		// return value from accounting rules class has to be the value defined in the config file
		assertEquals(insertedDays, AccountingRules.getNumberOfInterestDays());
		insertedDays = 355;
		configMgr.setProperty(AccountingRulesNumberOfInterestDays, insertedDays);
		// throw exception because the invalid value 355
		try
		{
			AccountingRules.getNumberOfInterestDays();
		}
		catch (RuntimeException e)
		{
			assertEquals(e.getMessage(), "Invalid number of interest days defined in property file "
					+ insertedDays.shortValue());
		}
		// clear the NumberOfInterestDays property from the config file
		configMgr.clearProperty(AccountingRulesNumberOfInterestDays);
		// throw exception because no interest days defined in config file
		try
		{
			AccountingRules.getNumberOfInterestDays();
		}
		catch (RuntimeException e)
		{
			assertEquals(e.getMessage(), "The number of interest days is not defined in the config file ");
		}
		
		configMgr.addProperty(AccountingRulesNumberOfInterestDays, interestDaysInConfig);
	}
	
	

}
