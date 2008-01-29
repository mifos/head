/**
 * Copyright (c) 2005-2008 Grameen Foundation USA
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
package org.mifos.config;

import java.math.RoundingMode;
import org.mifos.framework.components.configuration.persistence.ConfigurationPersistence;
import org.mifos.framework.components.configuration.util.helpers.ConfigConstants;
import org.mifos.application.master.business.MifosCurrency;


public class AccountingRules {
	
	public static final String AccountingRulesDigitsAfterDecimal = "AccountingRules.DigitsAfterDecimal";
	public static final String AccountingRulesRoundingRule = "AccountingRules.RoundingRule";
	public static final String AccountingRulesNumberOfInterestDays = "AccountingRules.NumberOfInterestDays";
	public static final String AccountingRulesAmountToBeRoundedTo="AccountingRules.AmountToBeRoundedTo";
	public static final String AccountingRulesCurrencyCode="AccountingRules.CurrencyCode";
	public static final String AccountingRulesDigitsBeforeDecimal = "AccountingRules.DigitsBeforeDecimal";
	public static final String AccountingRulesDigitsAfterDecimalForInterest = "AccountingRules.DigitsAfterDecimalForInterest";
	public static final String AccountingRulesDigitsBeforeDecimalForInterest = "AccountingRules.DigitsBeforeDecimalForInterest";
	public static final String AccountingRulesMaxInterest = "AccountingRules.MaxInterest";
	public static final String AccountingRulesMinInterest = "AccountingRules.MinInterest";
	
	
	public static MifosCurrency getMifosCurrency()
	{
		String currencyCode = getCurrencyCode();
		MifosCurrency currency = new ConfigurationPersistence().getCurrency(getCurrencyCode());
		if (currency == null)
			throw new RuntimeException("Can't find in the database the currency define in the config file " + currencyCode);
		Short digitsAfterDecimal = getDigitsAfterDecimal();
		Float amountToBeRoundedTo = getAmountToBeRoundedTo(currency.getRoundingAmount());
		Short roundingMode = getRoundingMode(currency.getRoundingMode());
		return new MifosCurrency(currency.getCurrencyId(), currency.getCurrencyName(), currency.getDisplaySymbol(),
				roundingMode, amountToBeRoundedTo, currency.getDefaultCurrency(), digitsAfterDecimal, currencyCode);
	
	}

	
	public static String getCurrencyCode()
	{
		String currencyCode;
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(AccountingRulesCurrencyCode))
			currencyCode = configMgr.getString(AccountingRulesCurrencyCode);
		else
			throw new RuntimeException("The currency code is not defined in the config file.");
		return currencyCode;
	}
	
	public static Double getMaxInterest()
	{
		Double maxInterest = null;
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(AccountingRulesMaxInterest))
			maxInterest = configMgr.getDouble(AccountingRulesMaxInterest);
		else
			throw new RuntimeException("Max interest is not defined in the config file.");
		return maxInterest;
	}
	
	public static Double getMinInterest()
	{
		Double maxInterest = null;
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(AccountingRulesMinInterest))
			maxInterest = configMgr.getDouble(AccountingRulesMinInterest);
		else
			throw new RuntimeException("Min interest is not defined in the config file.");
		return maxInterest;
	}

	public static Short getDigitsAfterDecimal()
	{
		Short digits;
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(AccountingRulesDigitsAfterDecimal))
			digits = configMgr.getShort(AccountingRulesDigitsAfterDecimal);
		else
			throw new RuntimeException("The number of digits after decimal is not defined in the config file.");
		return digits;
	}
	
	public static Short getDigitsBeforeDecimal()
	{

		Short digits;
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(AccountingRulesDigitsBeforeDecimal))
			digits = configMgr.getShort(AccountingRulesDigitsBeforeDecimal);
		else
			throw new RuntimeException("The number of digits before decimal is not defined in the config file.");
		return digits;
	}
	
	public static Short getDigitsBeforeDecimalForInterest()
	{

		Short digits;
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(AccountingRulesDigitsBeforeDecimalForInterest))
			digits = configMgr.getShort(AccountingRulesDigitsBeforeDecimalForInterest);
		else
			throw new RuntimeException("The number of digits before decimal for interest is not defined in the config file.");
		return digits;
	}
	
	public static Short getDigitsAfterDecimalForInterest()
	{

		Short digits;
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(AccountingRulesDigitsAfterDecimalForInterest))
			digits = configMgr.getShort(AccountingRulesDigitsAfterDecimalForInterest);
		else
			throw new RuntimeException("The number of digits after decimal for interest is not defined in the config file.");
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
	
	public static Short getRoundingMode(Short defaultValue)
	{
		Short mode;
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		if (configMgr.containsKey(AccountingRulesRoundingRule))
		{
			String returnStr = configMgr.getString(AccountingRulesRoundingRule);
			if (returnStr.equals("FLOOR"))
				mode = MifosCurrency.FLOOR_MODE;
			else if (returnStr.equals("CEILING"))
				mode = MifosCurrency.CEILING_MODE;
			else
				throw new RuntimeException("The rounding mode defined in the config file is not CEILING nor FLOOR. It is " 
						+ returnStr);
		}
		else
			mode = defaultValue;
		return mode;
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
	
	// these methods below are for testing purpose and not used by Mifos
	public static void setDigitsAfterDecimal(Short value)
	{
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		configMgr.setProperty(AccountingRulesDigitsAfterDecimal, value);
	}
	
	public static void setDigitsBeforeDecimal(Short value)
	{
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		configMgr.setProperty(AccountingRulesDigitsBeforeDecimal, value);
	}
	
	public static void setDigitsBeforeDecimalForInterest(Short value)
	{
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		configMgr.setProperty(AccountingRulesDigitsBeforeDecimalForInterest, value);
	}
	
	public static void setDigitsAfterDecimalForInterest(Short value)
	{

		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		configMgr.setProperty(AccountingRulesDigitsAfterDecimalForInterest, value);
	}
	
	/**
	 * Head Office can specify whether/not system will accept back-dated
	 * transactions. This is an MFI-wide setting and will be applicable to all
	 * transactions in all offices for all loans, savings and client accounts.
	 * By default, backdated transactions should be allowed. If the setting is
	 * changed it only applies to future transactions
	 * <ul>
	 * <li>If "true", user can enter transactions dated earlier than current
	 * date (but later than last meeting date).</li>
	 * <li>If "false", user can only enter transactions dated with the current
	 * date. Also, "date of transaction" for bulk entry will always be the
	 * current date.</li>
	 * </ul>
	 */
	public static boolean isBackDatedTxnAllowed() {
		ConfigurationManager cm = ConfigurationManager.getInstance();
		return cm.getBoolean(ConfigConstants.BACK_DATED_TRANSACTIONS_ALLOWED);
	}

}
