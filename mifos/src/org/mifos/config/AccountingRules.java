package org.mifos.config;

import java.math.RoundingMode;


public class AccountingRules {
	
	private static final String AccountingRulesDigitsAfterDecimal = "AccountingRules.DigitsAfterDecimal";
	private static final String AccountingRulesRoundingRule = "AccountingRules.RoundingRule";
	private static final String AccountingRulesNumberOfInterestDays = "AccountingRules.NumberOfInterestDays";
	private static final String AccountingRulesAmountToBeRoundedTo="AccountingRules.AmountToBeRoundedTo";
	
	
	public static Short getDigitsAfterDecimal(Short defaultValue)
	{

		Short digits;
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(AccountingRulesDigitsAfterDecimal))
			digits = configMgr.getShort(AccountingRulesDigitsAfterDecimal);
		else if (defaultValue != null)
			digits = defaultValue;
		else
			throw new RuntimeException("The number of digits after decimal is not defined in the config file nor database.");
		return digits;
	}
	
	
	
	// the defaultValue passed in should be the value from database
	public static Float getAmountToBeRoundedTo(Float defaultValue)
	{
		Float amount;
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(AccountingRulesAmountToBeRoundedTo))
			amount = configMgr.getFloat(AccountingRulesAmountToBeRoundedTo);
		else
			amount = defaultValue;
		return amount;
		
	}
	
	//	the defaultValue passed in should be the value from database
	public static RoundingMode getRoundingRule(RoundingMode defaultValue)
	{
		RoundingMode mode;
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(AccountingRulesRoundingRule))
		{
			String returnStr = configMgr.getString(AccountingRulesRoundingRule);
			if (returnStr.equals("FLOOR"))
				mode = RoundingMode.FLOOR;
			else if (returnStr.equals("CEILING"))
				mode = RoundingMode.CEILING;
			else
				throw new RuntimeException("The rounding mode defined in the config file is not CEILING nor FLOOR. It is " 
						+ returnStr);
		}
		else
			mode = defaultValue;
		return mode;
	}
	
	
	public static Short getNumberOfInterestDays()
	{
		Short days;
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(AccountingRulesNumberOfInterestDays))
		{
			days = configMgr.getShort(AccountingRulesNumberOfInterestDays);
			if ((days != 365) && (days != 360))
				throw new RuntimeException("Invalid number of interest days defined in property file " + days);
		}
		else
			throw new RuntimeException("The number of interest days is not defined in the config file ");
			
		return days;
	}

}
