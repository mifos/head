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

import org.apache.commons.lang.StringUtils;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.components.configuration.persistence.ConfigurationPersistence;
import org.mifos.config.AccountingRulesConstants;

public class AccountingRules {

    // if you change any of the following values please change the test cases to
    // match these values
    // if any of these configured entries are not defined in the application
    // config file they will get these values
    private static final BigDecimal defaultInitialRoundOffMultiple = new BigDecimal("1");
    private static final BigDecimal defaultFinalRoundOffMultiple = new BigDecimal("1");
    private static final RoundingMode defaultInitialRoundingMode = RoundingMode.HALF_UP;
    private static final RoundingMode defaultFinalRoundingMode = RoundingMode.CEILING;
    private static final RoundingMode defaultCurrencyRoundingMode = RoundingMode.HALF_UP;

    public static MifosCurrency getMifosCurrency(ConfigurationPersistence configurationPersistence) {
        String currencyCode = getCurrencyCode();
        MifosCurrency currency = configurationPersistence.getCurrency(getCurrencyCode());
        if (currency == null)
            throw new RuntimeException("Can't find in the database the currency define in the config file "
                    + currencyCode);
        Short digitsAfterDecimal = getDigitsAfterDecimal();
        Float amountToBeRoundedTo = getAmountToBeRoundedTo(currency.getRoundingAmount());
        Short roundingMode = getRoundingMode(currency.getRoundingMode());
        return new MifosCurrency(currency.getCurrencyId(), currency.getCurrencyName(), roundingMode,
                amountToBeRoundedTo, digitsAfterDecimal, currencyCode);
    }

    public static String getCurrencyCode() {
        return ConfigurationManager.getInstance().getString(AccountingRulesConstants.CURRENCY_CODE);
    }

    public static Double getMaxInterest() {
        return ConfigurationManager.getInstance().getDouble(AccountingRulesConstants.MAX_INTEREST);
    }

    public static Double getMinInterest() {
        return  ConfigurationManager.getInstance().getDouble(AccountingRulesConstants.MIN_INTEREST);
    }

    public static Short getDigitsAfterDecimal() {
        return ConfigurationManager.getInstance().getShort(AccountingRulesConstants.DIGITS_AFTER_DECIMAL);
    }

    /**
     * Broken. See <a
     * href="https://mifos.dev.java.net/issues/show_bug.cgi?id=1537">issue
     * 1537</a>
     */
    public static Short getDigitsBeforeDecimal() {
        Short digits;
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        // default from applicationConfiguration.default.properties revision
        // 14052
        digits = configMgr.getShort(AccountingRulesConstants.DIGITS_BEFORE_DECIMAL, (short) 7);
        return digits;
    }

    /**
     * Broken. See <a
     * href="https://mifos.dev.java.net/issues/show_bug.cgi?id=1537">issue
     * 1537</a>
     */
    public static Short getDigitsBeforeDecimalForInterest() {
        Short digits;
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        // default from applicationConfiguration.default.properties revision
        // 14052
        digits = configMgr.getShort(AccountingRulesConstants.DIGITS_BEFORE_DECIMAL_FOR_INTEREST, (short) 10);
        return digits;
    }

    public static Short getDigitsAfterDecimalForInterest() {
        return ConfigurationManager.getInstance().getShort(AccountingRulesConstants.DIGITS_AFTER_DECIMAL_FOR_INTEREST);
    }

    // the defaultValue passed in should be the value from database
    public static Float getAmountToBeRoundedTo(Float defaultValue) {
        return ConfigurationManager.getInstance().getFloat(AccountingRulesConstants.AMOUNT_TO_BE_ROUNDED_TO, defaultValue);
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
    
    public static Boolean isMultiCurrencyEnabled(){
        //FIXME this method might be called from AccountingRules 
        // for now testing is being done so the value returned is true        
        return true;
    }

    public static RoundingMode getInitialRoundingMode() {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        String modeStr = configMgr.getString(AccountingRulesConstants.INITIAL_ROUNDING_MODE);
        return getRoundingModeFromString(modeStr, "InitialRoundingMode", defaultInitialRoundingMode);
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
        return getRoundingModeFromString(modeStr, "FinalRoundingMode", defaultFinalRoundingMode);
    }

    private static BigDecimal getRoundOffMultipleFromString(String roundOffStr,
            BigDecimal defaultRoundOffMultiple) {
        if (StringUtils.isBlank(roundOffStr)) {
            return defaultRoundOffMultiple;
        }
        return new BigDecimal(roundOffStr);
    }

    public static BigDecimal getInitialRoundOffMultiple() {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        String modeStr = configMgr.getString(AccountingRulesConstants.INITIAL_ROUND_OFF_MULTIPLE);
        return getRoundOffMultipleFromString(modeStr, defaultInitialRoundOffMultiple);
    }

    public static BigDecimal getFinalRoundOffMultiple() {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        String modeStr = configMgr.getString(AccountingRulesConstants.FINAL_ROUND_OFF_MULTIPLE);
        return getRoundOffMultipleFromString(modeStr, defaultFinalRoundOffMultiple);
    }

    public static RoundingMode getCurrencyRoundingMode() {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        String modeStr = configMgr.getString(AccountingRulesConstants.CURRENCY_ROUNDING_MODE);
        return getRoundingModeFromString(modeStr, "CurrencyRoundingMode", defaultCurrencyRoundingMode);
    }

    /*
     * Return a decimal corresponding to the number of digits after the decimal.
     * For example 2 digits after the decimal should map to 0.01, one digit to
     * 0.1
     */
    public static BigDecimal getDigitsAfterDecimalMultiple() {
        int digitsAfterDecimal = getDigitsAfterDecimal().intValue();
        if (digitsAfterDecimal >= 0) {
            BigDecimal divisor = new BigDecimal("10").pow(digitsAfterDecimal);
            return new BigDecimal("1").divide(divisor);
        }
        return new BigDecimal("10").pow(-digitsAfterDecimal);
    }

    public static void setDigitsAfterDecimal(Short value) {
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.DIGITS_AFTER_DECIMAL, value);
    }

    public static void setDigitsBeforeDecimal(Short value) {
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.DIGITS_BEFORE_DECIMAL, value);
    }

    public static void setDigitsBeforeDecimalForInterest(Short value) {
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.DIGITS_BEFORE_DECIMAL_FOR_INTEREST, value);
    }

    public static void setDigitsAfterDecimalForInterest(Short value) {
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.DIGITS_AFTER_DECIMAL_FOR_INTEREST, value);
    }

    public static void setRoundingRule(RoundingMode mode) {
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.ROUNDING_RULE, mode.name());
    }

    public static void setFinalRoundOffMultiple(BigDecimal finalRoundOffMultiple) {
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.FINAL_ROUND_OFF_MULTIPLE, finalRoundOffMultiple.toString());
    }

    public static void setInitialRoundOffMultiple(BigDecimal initialRoundOffMultiple) {
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.INITIAL_ROUND_OFF_MULTIPLE, initialRoundOffMultiple.toString());
    }

    public static void setCurrencyRoundingMode(RoundingMode currencyRoundingMode) {
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.CURRENCY_ROUNDING_MODE, currencyRoundingMode.name());
    }

    public static void setInitialRoundingMode(RoundingMode intialRoundingMode) {
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.INITIAL_ROUNDING_MODE, intialRoundingMode.name());
    }

    public static void setFinalRoundingMode(RoundingMode finalRoundingMode) {
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.FINAL_ROUNDING_MODE, finalRoundingMode.name());
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
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.NUMBER_OF_INTEREST_DAYS, numberOfInterestDays);
    }
}
