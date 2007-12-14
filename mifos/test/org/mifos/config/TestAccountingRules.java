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
import org.mifos.config.AccountingRules;
import org.mifos.application.master.business.MifosCurrency;


public class TestAccountingRules  extends MifosTestCase {
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestAccountingRules.class);
	}
	
	Configuration configuration;
	
	
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
	public void testGetMifosCurrency()
	{
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		String currencyCode = configMgr.getString(AccountingRules.AccountingRulesCurrencyCode);
		configMgr.setProperty(AccountingRules.AccountingRulesCurrencyCode, "INR");
		MifosCurrency currency = AccountingRules.getMifosCurrency();
		assertEquals(currency.getCurrencyCode(), "INR");
		configMgr.setProperty(AccountingRules.AccountingRulesCurrencyCode, "EUR");
		currency = AccountingRules.getMifosCurrency();
		assertEquals(currency.getCurrencyCode(), "EUR");
		configMgr.setProperty(AccountingRules.AccountingRulesCurrencyCode, "UUU");
		try
		{
			currency = AccountingRules.getMifosCurrency();
		}
		catch (Exception e)
		{
			
		}
		configMgr.setProperty(AccountingRules.AccountingRulesCurrencyCode, currencyCode);
		
	}
	
	@Test 
	public void testGetDigitsAfterDecimal() {
		Short defaultValue = 0;
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		Short digitsAfterDecimal = 1;
		configMgr.addProperty(AccountingRules.AccountingRulesDigitsAfterDecimal, digitsAfterDecimal);
		// return value from accounting rules class has to be the value defined in the config file
		assertEquals(digitsAfterDecimal, AccountingRules.getDigitsAfterDecimal(defaultValue));
		// clear the DigitsAfterDecimal property from the config file
		configMgr.clearProperty(AccountingRules.AccountingRulesDigitsAfterDecimal);
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
		configMgr.clearProperty(AccountingRules.AccountingRulesDigitsAfterDecimal);
		
	}

	
	@Test
	public void testGetAmountToBeRoundedTo() {
		Float defaultValue = (float)0.5;
		Float amountToBeRoundedTo = (float)0.01;
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		configMgr.addProperty(AccountingRules.AccountingRulesAmountToBeRoundedTo, amountToBeRoundedTo);
		// return value from accounting rules class has to be the value defined in the config file
		assertEquals(amountToBeRoundedTo, AccountingRules.getAmountToBeRoundedTo(defaultValue));
		// clear the AmountToBeRoundedTo property from the config file
		configMgr.clearProperty(AccountingRules.AccountingRulesAmountToBeRoundedTo);
		// now the return value from accounting rules class has to be the default value (value from db)
		assertEquals(defaultValue, AccountingRules.getAmountToBeRoundedTo(defaultValue));
	}
	
	@Test 
	public void testGetRoundingRule() {
		RoundingMode defaultValue = RoundingMode.CEILING;
		String roundingMode = "FLOOR";
		RoundingMode configRoundingMode = RoundingMode.FLOOR;
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		configMgr.addProperty(AccountingRules.AccountingRulesRoundingRule, roundingMode);
		// return value from accounting rules class has to be the value defined in the config file
		assertEquals(configRoundingMode, AccountingRules.getRoundingRule(defaultValue));
		// clear the RoundingRule property from the config file
		configMgr.clearProperty(AccountingRules.AccountingRulesRoundingRule);
		// now the return value from accounting rules class has to be the default value (value from db)
		assertEquals(defaultValue, AccountingRules.getRoundingRule(defaultValue));
		// now set a wrong rounding mode in config
		roundingMode = "UP";
		configMgr.addProperty(AccountingRules.AccountingRulesRoundingRule, roundingMode);
		try
		{
			AccountingRules.getRoundingRule(defaultValue);
		}
		catch (RuntimeException e)
		{
			assertEquals(e.getMessage(), "The rounding mode defined in the config file is not CEILING nor FLOOR. It is "
					+ roundingMode);
		}
		configMgr.clearProperty(AccountingRules.AccountingRulesRoundingRule);
	}
	
	
	@Test 
	public void testGetNumberOfInterestDays() {
		Short interestDaysInConfig = AccountingRules.getNumberOfInterestDays();
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		Short insertedDays = 365;
		configMgr.setProperty(AccountingRules.AccountingRulesNumberOfInterestDays, insertedDays);
		assertEquals(insertedDays, AccountingRules.getNumberOfInterestDays());
		insertedDays = 360;
		// set new value
		configMgr.setProperty(AccountingRules.AccountingRulesNumberOfInterestDays, insertedDays);
		// return value from accounting rules class has to be the value defined in the config file
		assertEquals(insertedDays, AccountingRules.getNumberOfInterestDays());
		insertedDays = 355;
		configMgr.setProperty(AccountingRules.AccountingRulesNumberOfInterestDays, insertedDays);
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
		configMgr.clearProperty(AccountingRules.AccountingRulesNumberOfInterestDays);
		// throw exception because no interest days defined in config file
		try
		{
			AccountingRules.getNumberOfInterestDays();
		}
		catch (RuntimeException e)
		{
			assertEquals(e.getMessage(), "The number of interest days is not defined in the config file ");
		}
		
		configMgr.addProperty(AccountingRules.AccountingRulesNumberOfInterestDays, interestDaysInConfig);
	}
	
	

}
