/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.core.MifosRuntimeException;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.framework.util.helpers.Money;

public class AccountingRules {

    // if you change any of the following values please change the test cases to
    // match these values
    // if any of these configured entries are not defined in the application
    // config file they will get these values
    private static final BigDecimal DEFAULT_INITIAL_ROUNDOFF_MULTIPLE = new BigDecimal("1");
    private static final BigDecimal DEFAULT_FINAL_ROUNDOFF_MULTIPLE = new BigDecimal("1");
    private static final RoundingMode DEFAULT_INITIAL_ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final RoundingMode DEFAULT_FINAL_ROUNDING_MODE = RoundingMode.CEILING;
    private static final RoundingMode DEFAULT_CURRENCY_ROUNDING_MODE = RoundingMode.HALF_UP;

    /**
     * An internal property which represents the digits before decimal of an amount that can be entered through
     * any User Interface, due to <b>MySQL DECIMAL(21,4)</b> for amount field in database we can store a value of up to 17
     * digits, but some of them are totals. 
     * <br /><br />
     * To make sure that totals will not overflow we have allowed 14 as the limit.
     * 
     * for details see http://mifosforge.jira.com/browse/MIFOS-1537
     */
    private static final Short DIGITS_BEFORE_DECIMAL_FOR_AMOUNT = 14;
    
    /**
     * An internal property which represents the digits before decimal of an interest that can be entered through
     * any User Interface, due to <b>MySQL DECIMAL(13,10)</b> for interest field in database we can store a value of up to 13
     * digits, but some of them are totals. 
     * <br /><br />
     * To make sure that totals will not overflow we have allowed 10 as the limit.
     * 
     * for details see http://mifosforge.jira.com/browse/MIFOS-1537
     */
    private static final Short DIGITS_BEFORE_DECIMAL_FOR_INTEREST = 10;

    // FIXME: we should use a standard caching mechanism rather than ad hoc caches like 
    // this.  Also, we need to consider if this should be thread safe since this initial
    // implementation is not thread safe for initialization.  Re-initialization should 
    // only happen for test cases, so that most likely is okay.  Adding some 
    // synchronization could make it thread safe, but this will be accessed every time
    // a non-default currency is read from the database, so care needs to be taken
    // regarding performance.
    private static final LinkedList<MifosCurrency> currencies = new LinkedList<MifosCurrency>();
    
    /*
     * Allow for reloading the currencies if the configuration has been changed during testing.
     */
    public static void init() {
        currencies.clear();
        getCurrencies();
    }
    
    public static MifosCurrency getMifosCurrency(ConfigurationPersistence configurationPersistence) {
        return getMifosCurrency(getDefaultCurrencyCode(),configurationPersistence);
    }
    
    public static MifosCurrency getMifosCurrency(String currencyCode,ConfigurationPersistence configurationPersistence) {
        MifosCurrency currency = configurationPersistence.getCurrency(currencyCode);
        if (currency == null)
            throw new RuntimeException("Can't find in the database the currency define in the config file "
                    + currencyCode);
        Short digitsAfterDecimal = getDigitsAfterDecimal(currency);
        BigDecimal amountToBeRoundedTo = getAmountToBeRoundedTo(currency.getRoundingAmount());
        return new MifosCurrency(currency.getCurrencyId(), currency.getCurrencyName(),
                amountToBeRoundedTo, currencyCode);
    }
    
    /**
     * 
     * Gets the List of currencies configured to use in Mifos,
     * the first element will be the default currency.
     * 
     * @return List of currencies
     */
    public static  LinkedList<MifosCurrency> getCurrencies() {
        if (currencies.size() == 0) {
            currencies.add(Money.getDefaultCurrency());
            ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
            for (String currencyCode: AccountingRules.getAdditionalCurrencyCodes()) {
                currencies.add(getMifosCurrency(currencyCode, configurationPersistence));
            }
        }
        return currencies;
    }
    
    /**
     * Gets the currency by currency id from the list of currencies configured to used in Mifos 
     * {@link AccountingRules#getCurrencies()}
     * 
     * @param currencyId
     * @return {@link MifosCurrency}
     */
    public static MifosCurrency getCurrencyByCurrencyId(Short currencyId) {
        LinkedList<MifosCurrency> currencies = getCurrencies();
        Iterator<MifosCurrency> i = currencies.iterator();
        while(i.hasNext()) {
            MifosCurrency a = i.next();
            if(a.getCurrencyId().equals(currencyId)) {
                return a;
            }
        }        
        throw new MifosRuntimeException("Unable to find currency with id: " + currencyId + 
                ". You may be missing an entry for the currency with this id in " + 
                ConfigurationManager.CUSTOM_CONFIG_PROPS_FILENAME + ".");
    }

    public static String getDefaultCurrencyCode() {
        return ConfigurationManager.getInstance().getString(AccountingRulesConstants.CURRENCY_CODE);
    }

    /*
     * suppress unchecked casts to allow genericized List<String> to be used by
     * callers
     */
    @SuppressWarnings("unchecked")
    public static List<String> getAdditionalCurrencyCodes() {
        return ConfigurationManager.getInstance().getList(AccountingRulesConstants.ADDITIONAL_CURRENCY_CODES);
    }

    public static Double getMaxInterest() {
        return ConfigurationManager.getInstance().getDouble(AccountingRulesConstants.MAX_INTEREST);
    }

    public static Double getMinInterest() {
        return ConfigurationManager.getInstance().getDouble(AccountingRulesConstants.MIN_INTEREST);
    }

    public static Short getDigitsAfterDecimal() {
        return ConfigurationManager.getInstance().getShort(AccountingRulesConstants.DIGITS_AFTER_DECIMAL);
    }

    public static Short getDigitsAfterDecimal(final MifosCurrency currency) {
        final String code = currency.getCurrencyCode();
        if (getDefaultCurrencyCode().equals(code)) {
            return getDigitsAfterDecimal();
        }
        if (!getAdditionalCurrencyCodes().contains(code)) {
            throw new IllegalArgumentException("Currency not configured. " + currency);
        }
        return ConfigurationManager.getInstance().getShort(AccountingRulesConstants.DIGITS_AFTER_DECIMAL + "." + code,
                getDigitsAfterDecimal());
    }

    public static Short getDigitsBeforeDecimal() {
        return DIGITS_BEFORE_DECIMAL_FOR_AMOUNT;
    }

    public static Short getDigitsBeforeDecimalForInterest() {
        return DIGITS_BEFORE_DECIMAL_FOR_INTEREST;
    }

    public static Short getDigitsAfterDecimalForInterest() {
        return ConfigurationManager.getInstance().getShort(AccountingRulesConstants.DIGITS_AFTER_DECIMAL_FOR_INTEREST);
    }

    // the defaultValue passed in should be the value from database
    public static BigDecimal getAmountToBeRoundedTo(BigDecimal defaultValue) {
        return ConfigurationManager.getInstance().getBigDecimal(AccountingRulesConstants.AMOUNT_TO_BE_ROUNDED_TO,
                defaultValue);
    }

    public static Short getRoundingMode(Short defaultValue) {
        Short mode;
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        if (configMgr.containsKey(AccountingRulesConstants.ROUNDING_RULE)) {
            String returnStr = configMgr.getString(AccountingRulesConstants.ROUNDING_RULE);
            if (returnStr.equals("FLOOR"))
                mode = MifosCurrency.FLOOR_MODE;
            else if (returnStr.equals("CEILING"))
                mode = MifosCurrency.CEILING_MODE;
            else if (returnStr.equals("HALF_UP"))
                mode = MifosCurrency.HALF_UP_MODE;
            else
                throw new RuntimeException(
                        "The rounding mode defined in the config file is not CEILING, FLOOR, HALF_UP. It is "
                                + returnStr);
        } else
            mode = defaultValue;
        return mode;
    }

    /*
     * Expected to return either 360 or 365
     */
    public static Short getNumberOfInterestDays() {
        Short days = ConfigurationManager.getInstance().getShort(AccountingRulesConstants.NUMBER_OF_INTEREST_DAYS);
        if ((days != 365) && (days != 360))
            throw new RuntimeException("Invalid number of interest days defined in property file " + days);
        return days;
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
        return cm.getBoolean(AccountingRulesConstants.BACKDATED_TRANSACTIONS_ALLOWED);
    }

    public static Boolean isMultiCurrencyEnabled() {
        if (getAdditionalCurrencyCodes().isEmpty()) {
            return false;
        }
        return true;
    }

    public static RoundingMode getInitialRoundingMode() {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        String modeStr = configMgr.getString(AccountingRulesConstants.INITIAL_ROUNDING_MODE);
        return getRoundingModeFromString(modeStr, "InitialRoundingMode", DEFAULT_INITIAL_ROUNDING_MODE);
    }

    private static RoundingMode getRoundingModeFromString(String modeStr, String type, RoundingMode defaultRoundingMode) {
        if (StringUtils.isBlank(modeStr)) {
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

    public static RoundingMode getFinalRoundingMode() {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        String modeStr = configMgr.getString(AccountingRulesConstants.FINAL_ROUNDING_MODE);
        return getRoundingModeFromString(modeStr, "FinalRoundingMode", DEFAULT_FINAL_ROUNDING_MODE);
    }

    private static BigDecimal getRoundOffMultipleFromString(String roundOffStr, BigDecimal defaultRoundOffMultiple) {
        if (StringUtils.isBlank(roundOffStr)) {
            return defaultRoundOffMultiple;
        }
        return new BigDecimal(roundOffStr);
    }

    public static BigDecimal getInitialRoundOffMultiple() {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        String modeStr = configMgr.getString(AccountingRulesConstants.INITIAL_ROUND_OFF_MULTIPLE);
        return getRoundOffMultipleFromString(modeStr, DEFAULT_INITIAL_ROUNDOFF_MULTIPLE);
    }

    public static BigDecimal getInitialRoundOffMultiple(final MifosCurrency currency) {
        final String code = currency.getCurrencyCode();
        if (getDefaultCurrencyCode().equals(code)) {
            return getInitialRoundOffMultiple();
        }
        if (!getAdditionalCurrencyCodes().contains(code)) {
            throw new IllegalArgumentException("Currency not configured. " + currency);
        }
        String modeStr = ConfigurationManager.getInstance().getString(
                AccountingRulesConstants.INITIAL_ROUND_OFF_MULTIPLE + "." + code);
        return getRoundOffMultipleFromString(modeStr, DEFAULT_INITIAL_ROUNDOFF_MULTIPLE);
    }

    public static BigDecimal getFinalRoundOffMultiple() {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        String modeStr = configMgr.getString(AccountingRulesConstants.FINAL_ROUND_OFF_MULTIPLE);
        return getRoundOffMultipleFromString(modeStr, DEFAULT_FINAL_ROUNDOFF_MULTIPLE);
    }
    
    public static BigDecimal getFinalRoundOffMultiple(final MifosCurrency currency) {
        final String code = currency.getCurrencyCode();
        if (getDefaultCurrencyCode().equals(code)) {
            return getFinalRoundOffMultiple();
        }
        if (!getAdditionalCurrencyCodes().contains(code)) {
            throw new IllegalArgumentException("Currency not configured. " + currency);
        }
        String modeStr = ConfigurationManager.getInstance().getString(
                AccountingRulesConstants.FINAL_ROUND_OFF_MULTIPLE + "." + code);
        return getRoundOffMultipleFromString(modeStr, DEFAULT_FINAL_ROUNDOFF_MULTIPLE);
    }

    public static RoundingMode getCurrencyRoundingMode() {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        String modeStr = configMgr.getString(AccountingRulesConstants.CURRENCY_ROUNDING_MODE);
        return getRoundingModeFromString(modeStr, "CurrencyRoundingMode", DEFAULT_CURRENCY_ROUNDING_MODE);
    }

    /*
     * Return a decimal corresponding to the number of digits after the decimal.
     * For example 2 digits after the decimal should map to 0.01, one digit to
     * 0.1
     */
    public static BigDecimal getDigitsAfterDecimalMultiple(MifosCurrency currency) {
        int digitsAfterDecimal = getDigitsAfterDecimal(currency).intValue();
        if (digitsAfterDecimal >= 0) {
            BigDecimal divisor = new BigDecimal("10").pow(digitsAfterDecimal);
            return new BigDecimal("1").divide(divisor);
        }
        return new BigDecimal("10").pow(-digitsAfterDecimal);
    }

    public static void setDigitsAfterDecimal(Short value) {
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.DIGITS_AFTER_DECIMAL, value);
    }

    public static void setDigitsAfterDecimalForInterest(Short value) {
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.DIGITS_AFTER_DECIMAL_FOR_INTEREST,
                value);
    }

    public static void setRoundingRule(RoundingMode mode) {
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.ROUNDING_RULE, mode.name());
    }

    public static void setFinalRoundOffMultiple(BigDecimal finalRoundOffMultiple) {
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.FINAL_ROUND_OFF_MULTIPLE,
                finalRoundOffMultiple.toString());
    }

    public static void setInitialRoundOffMultiple(BigDecimal initialRoundOffMultiple) {
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.INITIAL_ROUND_OFF_MULTIPLE,
                initialRoundOffMultiple.toString());
    }

    public static void setCurrencyRoundingMode(RoundingMode currencyRoundingMode) {
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.CURRENCY_ROUNDING_MODE,
                currencyRoundingMode.name());
    }

    public static void setInitialRoundingMode(RoundingMode intialRoundingMode) {
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.INITIAL_ROUNDING_MODE,
                intialRoundingMode.name());
    }

    public static void setFinalRoundingMode(RoundingMode finalRoundingMode) {
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.FINAL_ROUNDING_MODE,
                finalRoundingMode.name());
    }

    public static void setBackDatedTransactionsAllowed(Boolean value) {
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.BACKDATED_TRANSACTIONS_ALLOWED, value);
    }

    public static void setMaxInterest(BigDecimal maxInterest) {
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.MAX_INTEREST, maxInterest.toString());
    }

    public static void setMinInterest(BigDecimal minInterest) {
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.MIN_INTEREST, minInterest.toString());
    }

    public static void setNumberOfInterestDays(Integer numberOfInterestDays) {
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.NUMBER_OF_INTEREST_DAYS,
                numberOfInterestDays);
    }
}
