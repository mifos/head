/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
package org.mifos.config;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang.StringUtils;
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
	
	public static final String AccountingRulesInitialRoundingMode = "AccountingRules.InitialRoundingMode";
	public static final String AccountingRulesInitialRoundOffMultiple = "AccountingRules.InitialRoundOffMultiple";
	public static final String AccountingRulesFinalRoundingMode = "AccountingRules.FinalRoundingMode";
	public static final String AccountingRulesFinalRoundOffMultiple = "AccountingRules.FinalRoundOffMultiple";
	public static final String AccountingRulesCurrencyRoundingMode = "AccountingRules.CurrencyRoundingMode";
	
	// if you change any of the following values please change the test cases to match these values
	// if any of these configured entries are not defined in the application config file they will get these values
	private static final BigDecimal defaultInitialRoundOffMultiple = new BigDecimal("0.1");
	private static final BigDecimal defaultFinalRoundOffMultiple = new BigDecimal("0.01");
	private static final RoundingMode defaultInitialRoundingMode = RoundingMode.FLOOR;
	private static final RoundingMode defaultFinalRoundingMode = RoundingMode.CEILING;
	private static final RoundingMode defaultCurrencyRoundingMode = RoundingMode.HALF_UP;

	/*
	private static final BigDecimal defaultInitialRoundOffMultiple = new BigDecimal("1");
	private static final BigDecimal defaultFinalRoundOffMultiple = new BigDecimal("1");
	private static final RoundingMode defaultInitialRoundingMode = RoundingMode.CEILING;
	private static final RoundingMode defaultFinalRoundingMode = RoundingMode.CEILING;
	private static final RoundingMode defaultCurrencyRoundingMode = RoundingMode.CEILING;
	*/
	
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
			else if (returnStr.equals("HALF_UP"))
				mode = MifosCurrency.HALF_UP_MODE;
			else
				throw new RuntimeException("The rounding mode defined in the config file is not CEILING, FLOOR, HALF_UP. It is " 
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
			else if (returnStr.equals("HALF_UP"))
				mode = RoundingMode.HALF_UP;
			else
				throw new RuntimeException("The rounding mode defined in the config file is not CEILING, FLOOR, HALF_UP. It is " 
						+ returnStr);
		}
		else
			mode = defaultValue;
		return mode;
	}
	
	/*
	 * Expected to return either 360 or 365
	 */
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
	
	public static void setRoundingRule(RoundingMode mode)
	{
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		configMgr.setProperty(AccountingRulesRoundingRule, mode.name());
	}
	
	public static void setFinalRoundOffMultiple(BigDecimal finalRoundOffMultiple) {
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		configMgr.setProperty(AccountingRulesFinalRoundOffMultiple, finalRoundOffMultiple.toString());
	}

	public static void setInitialRoundOffMultiple(BigDecimal initialRoundOffMultiple) {
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		configMgr.setProperty(AccountingRulesInitialRoundOffMultiple, initialRoundOffMultiple.toString());
	}
	
	public static void setCurrencyRoundingMode(RoundingMode currencyRoundingMode) {
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		configMgr.setProperty(AccountingRulesCurrencyRoundingMode, currencyRoundingMode.name());
	}
	
	public static void setInitialRoundingMode(RoundingMode intialRoundingMode) {
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		configMgr.setProperty(AccountingRulesInitialRoundingMode, intialRoundingMode.name());
	}
	
	public static void setFinalRoundingMode(RoundingMode finalRoundingMode) {
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		configMgr.setProperty(AccountingRulesFinalRoundingMode, finalRoundingMode.name());
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

	
	public static RoundingMode getInitialRoundingMode()
	{
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		String modeStr = configMgr.getString(AccountingRulesInitialRoundingMode);
		return getRoundingModeFromString(modeStr, "InitialRoundingMode", defaultInitialRoundingMode);
	}
	
	private static RoundingMode getRoundingModeFromString(String modeStr, String type, RoundingMode defaultRoundingMode)
	{
		
		if ((modeStr == null) || (StringUtils.isEmpty(modeStr)))
		{
			return defaultRoundingMode;
		}
		RoundingMode mode = null;
		if (modeStr.equals("FLOOR")) {
			mode = RoundingMode.FLOOR;
		} else if (modeStr.equals("CEILING")) {
			mode = RoundingMode.CEILING;
		} else if (modeStr.equals("HALF_UP")) {
			mode = RoundingMode.HALF_UP;
		} else {
			throw new RuntimeException(type + " defined in the config file is not CEILING, FLOOR, HALF_UP. It is " 
					+ modeStr);
		}
		return mode;
	}
	
	public static RoundingMode getFinalRoundingMode()
	{
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		String modeStr = configMgr.getString(AccountingRulesFinalRoundingMode);
		return getRoundingModeFromString(modeStr, "FinalRoundingMode", defaultFinalRoundingMode);
	}

	
	private static BigDecimal getRoundOffMultipleFromString(String roundOffStr, String type, BigDecimal defaultRoundOffMultiple)
	{
		if ((roundOffStr == null) || (StringUtils.isEmpty(roundOffStr)))
		{
			return defaultRoundOffMultiple;
		}
		BigDecimal roundOffMultiple =  new BigDecimal(roundOffStr);
		return roundOffMultiple;
	}
	
	public static BigDecimal getInitialRoundOffMultiple() {
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		String modeStr = configMgr.getString(AccountingRulesInitialRoundOffMultiple);
		return getRoundOffMultipleFromString(modeStr, "InitialRoundOffMultiple", defaultInitialRoundOffMultiple);
	}
	
	public static BigDecimal getFinalRoundOffMultiple() {
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		String modeStr = configMgr.getString(AccountingRulesFinalRoundOffMultiple);
		return getRoundOffMultipleFromString(modeStr, "FinalRoundOffMultiple", defaultFinalRoundOffMultiple);
	}

	public static RoundingMode getCurrencyRoundingMode()
	{
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		String modeStr = configMgr.getString(AccountingRulesCurrencyRoundingMode);
		return getRoundingModeFromString(modeStr, "CurrencyRoundingMode", defaultCurrencyRoundingMode);
	}

	

	// refactor this later
	/*
	 * Return a decimal corresponding to the number of digits after the decimal.
	 * For example 2 digits after the decimal should map to 0.01, one digit to 0.1 
	 */
	public static BigDecimal getDigitsAfterDecimalMultiple() {
		return new BigDecimal("." + StringUtils.leftPad("1",getDigitsAfterDecimal().intValue(),"0"));
	}
	
	
}
