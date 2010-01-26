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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.configuration.persistence.ConfigurationPersistence;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccountingRulesTest {

    @Mock
    private static ConfigurationPersistence configurationPersistence;

    @BeforeClass
    public static void setupMifosLoggerDueToUseOfStaticClientRules() {
        Money.setDefaultCurrency(TestUtils.RUPEE);
    }

    @Test
    public void testGetCurrencyRoundingMode() {
        RoundingMode configuredMode = AccountingRules.getCurrencyRoundingMode();
        String roundingMode = "FLOOR";
        RoundingMode configRoundingMode = RoundingMode.FLOOR;
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        configMgr.setProperty(AccountingRulesConstants.CURRENCY_ROUNDING_MODE, roundingMode);
        // return value from accounting rules class has to be the value defined
        // in the config file
        assertEquals(configRoundingMode, AccountingRules.getCurrencyRoundingMode());
        // clear the RoundingRule property from the config file so should get
        // the default value
        configMgr.clearProperty(AccountingRulesConstants.CURRENCY_ROUNDING_MODE);
        RoundingMode defaultValue = AccountingRules.getCurrencyRoundingMode();
        assertEquals(defaultValue, RoundingMode.HALF_UP);
        // now set a wrong rounding mode in config
        roundingMode = "UP";
        configMgr.addProperty(AccountingRulesConstants.CURRENCY_ROUNDING_MODE, roundingMode);
        try {
            AccountingRules.getCurrencyRoundingMode();
            fail();
        } catch (RuntimeException e) {
            assertEquals("CurrencyRoundingMode defined in the config file is not CEILING, FLOOR, HALF_UP. It is "
                    + roundingMode, e.getMessage());
        }
        // save it back
        configMgr.setProperty(AccountingRulesConstants.CURRENCY_ROUNDING_MODE, configuredMode.toString());

    }

    @Test
    public void testGetInitialRoundingMode() {
        RoundingMode configuredMode = AccountingRules.getInitialRoundingMode();
        String roundingMode = "FLOOR";
        RoundingMode configRoundingMode = RoundingMode.FLOOR;
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        configMgr.setProperty(AccountingRulesConstants.INITIAL_ROUNDING_MODE, roundingMode);
        // return value from accounting rules class has to be the value defined
        // in the config file
        assertEquals(configRoundingMode, AccountingRules.getInitialRoundingMode());
        // clear the RoundingRule property from the config file
        configMgr.clearProperty(AccountingRulesConstants.INITIAL_ROUNDING_MODE);
        RoundingMode defaultValue = AccountingRules.getInitialRoundingMode();
        assertEquals(defaultValue, RoundingMode.HALF_UP);
        // now set a wrong rounding mode in config
        roundingMode = "UP";
        configMgr.addProperty(AccountingRulesConstants.INITIAL_ROUNDING_MODE, roundingMode);
        try {
            AccountingRules.getInitialRoundingMode();
            fail();
        } catch (RuntimeException e) {
            assertEquals("InitialRoundingMode defined in the config file is not CEILING, FLOOR, HALF_UP. It is "
                    + roundingMode, e.getMessage());
        }
        // save it back
        configMgr.setProperty(AccountingRulesConstants.INITIAL_ROUNDING_MODE, configuredMode.toString());
    }

    @Test
    public void testGetFinalRoundingMode() {
        RoundingMode configuredMode = AccountingRules.getFinalRoundingMode();
        String roundingMode = "CEILING";
        RoundingMode configRoundingMode = RoundingMode.CEILING;
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        configMgr.setProperty(AccountingRulesConstants.FINAL_ROUNDING_MODE, roundingMode);
        // return value from accounting rules class has to be the value defined
        // in the config file
        assertEquals(configRoundingMode, AccountingRules.getFinalRoundingMode());
        // clear the RoundingRule property from the config file
        configMgr.clearProperty(AccountingRulesConstants.FINAL_ROUNDING_MODE);
        RoundingMode defaultValue = AccountingRules.getFinalRoundingMode();
        assertEquals(defaultValue, RoundingMode.CEILING);
        // now set a wrong rounding mode in config
        roundingMode = "DOWN";
        configMgr.addProperty(AccountingRulesConstants.FINAL_ROUNDING_MODE, roundingMode);
        try {
            AccountingRules.getFinalRoundingMode();
            fail();
        } catch (RuntimeException e) {
            assertEquals("FinalRoundingMode defined in the config file is not CEILING, FLOOR, HALF_UP. It is "
                    + roundingMode, e.getMessage());
        }
        // save it back
        configMgr.setProperty(AccountingRulesConstants.FINAL_ROUNDING_MODE, configuredMode.toString());

    }

    @Test
    public void testGetFinalRoundOffMultiple() {
        BigDecimal configuredRoundOffMultiple = AccountingRules.getFinalRoundOffMultiple();
        String roundOffMultiple = "1";
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        configMgr.setProperty(AccountingRulesConstants.FINAL_ROUND_OFF_MULTIPLE, roundOffMultiple);
        // return value from accounting rules class has to be the value defined
        // in the config file
        assertEquals(new BigDecimal(roundOffMultiple), AccountingRules.getFinalRoundOffMultiple());
        // clear the RoundingRule property from the config file
        configMgr.clearProperty(AccountingRulesConstants.FINAL_ROUND_OFF_MULTIPLE);
        BigDecimal defaultValue = AccountingRules.getFinalRoundOffMultiple();
        assertEquals(defaultValue, new BigDecimal("1"));
        // save it back
        configMgr.addProperty(AccountingRulesConstants.FINAL_ROUND_OFF_MULTIPLE, configuredRoundOffMultiple.toString());
    }

    @Test
    public void testGetInitialRoundOffMultiple() {
        BigDecimal configuredRoundOffMultiple = AccountingRules.getInitialRoundOffMultiple();
        String roundOffMultiple = "1";
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        configMgr.setProperty(AccountingRulesConstants.INITIAL_ROUND_OFF_MULTIPLE, roundOffMultiple);
        // return value from accounting rules class has to be the value defined
        // in the config file
        assertEquals(new BigDecimal(roundOffMultiple), AccountingRules.getInitialRoundOffMultiple());
        // clear the RoundingRule property from the config file
        configMgr.clearProperty(AccountingRulesConstants.INITIAL_ROUND_OFF_MULTIPLE);
        BigDecimal defaultValue = AccountingRules.getInitialRoundOffMultiple();
        assertEquals(defaultValue, new BigDecimal("1"));
        // save it back
        configMgr.addProperty(AccountingRulesConstants.INITIAL_ROUND_OFF_MULTIPLE, configuredRoundOffMultiple
                .toString());
    }

    @Test
    public void testgetMifosCurrency() {
        when(configurationPersistence.getCurrency("INR")).thenReturn(TestUtils.RUPEE);
        when(configurationPersistence.getCurrency("EUR")).thenReturn(TestUtils.EURO);
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        String currencyCode = configMgr.getString(AccountingRulesConstants.CURRENCY_CODE);
        configMgr.setProperty(AccountingRulesConstants.CURRENCY_CODE, "INR");
        MifosCurrency currency = AccountingRules.getMifosCurrency(configurationPersistence);
        assertEquals(currency.getCurrencyCode(), "INR");
        configMgr.setProperty(AccountingRulesConstants.CURRENCY_CODE, "EUR");
        currency = AccountingRules.getMifosCurrency(configurationPersistence);
        assertEquals(currency.getCurrencyCode(), "EUR");
        configMgr.setProperty(AccountingRulesConstants.CURRENCY_CODE, "UUU");
        try {
            currency = AccountingRules.getMifosCurrency(configurationPersistence);
            fail();
        } catch (Exception e) {

        }
        configMgr.setProperty(AccountingRulesConstants.CURRENCY_CODE, currencyCode);

    }

    @Test
    public void testGetNumberOfInterestDays() {
        Short interestDaysInConfig = AccountingRules.getNumberOfInterestDays();
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        Short insertedDays = 365;
        configMgr.setProperty(AccountingRulesConstants.NUMBER_OF_INTEREST_DAYS, insertedDays);
        assertEquals(insertedDays, AccountingRules.getNumberOfInterestDays());
        insertedDays = 360;
        // set new value
        configMgr.setProperty(AccountingRulesConstants.NUMBER_OF_INTEREST_DAYS, insertedDays);
        // return value from accounting rules class has to be the value defined
        // in the config file
        assertEquals(insertedDays, AccountingRules.getNumberOfInterestDays());
        insertedDays = 355;
        configMgr.setProperty(AccountingRulesConstants.NUMBER_OF_INTEREST_DAYS, insertedDays);
        // throw exception because the invalid value 355
        try {
            AccountingRules.getNumberOfInterestDays();
            fail();
        } catch (RuntimeException e) {
            assertEquals("Invalid number of interest days defined in property file " + insertedDays.shortValue(), e
                    .getMessage());
        } finally {
            configMgr.setProperty(AccountingRulesConstants.NUMBER_OF_INTEREST_DAYS, interestDaysInConfig);
        }
    }

    private void checkDigitsAfterDecimalMultiple(int digitsAfterDecimalInt, String multiple) {
        Short digitsAfterDecimal = (short) digitsAfterDecimalInt;
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.DIGITS_AFTER_DECIMAL,
                digitsAfterDecimal);
        assertEquals(new BigDecimal(multiple), AccountingRules.getDigitsAfterDecimalMultiple(Money.getDefaultCurrency()));
    }

    @Test
    public void testGetDigitsAfterDecimalMultiple() {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        Short digitsAfterDecimalSaved = AccountingRules.getDigitsAfterDecimal();
        try {
            checkDigitsAfterDecimalMultiple(2, "0.01");
            checkDigitsAfterDecimalMultiple(1, "0.1");
            checkDigitsAfterDecimalMultiple(0, "1");
            checkDigitsAfterDecimalMultiple(-1, "10");
            checkDigitsAfterDecimalMultiple(-2, "100");

        } finally {
            configMgr.setProperty(AccountingRulesConstants.DIGITS_AFTER_DECIMAL, digitsAfterDecimalSaved);
        }
    }

    @Test
    public void globalMulticurrencyFlagWorks() {
        assertFalse(AccountingRules.isMultiCurrencyEnabled());
        List<String> codes = new ArrayList<String>();
        codes.add("LBP");
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        try {
            configMgr.setProperty(AccountingRulesConstants.ADDITIONAL_CURRENCY_CODES, codes);
            assertTrue(AccountingRules.isMultiCurrencyEnabled());
        } finally {
            configMgr.clearProperty(AccountingRulesConstants.ADDITIONAL_CURRENCY_CODES);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void alternateCurrenciesNotConfigured() {
        AccountingRules.getDigitsAfterDecimal(TestUtils.EURO);
    }

    @Test
    public void digitsAfterDecimalFallsBackToDefault() {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        try {
            configMgr.setProperty(AccountingRulesConstants.ADDITIONAL_CURRENCY_CODES, TestUtils.EURO.getCurrencyCode());
            assertEquals(new Short("1"), AccountingRules.getDigitsAfterDecimal(TestUtils.EURO));
        } finally {
            configMgr.clearProperty(AccountingRulesConstants.ADDITIONAL_CURRENCY_CODES);
        }
    }

    @Test
    public void canConfigureAlternateCurrencyWithNonDefaultDigitsAfterDecimal() {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        try {
            configMgr.setProperty(AccountingRulesConstants.ADDITIONAL_CURRENCY_CODES, TestUtils.EURO.getCurrencyCode());
            configMgr.setProperty(AccountingRulesConstants.DIGITS_AFTER_DECIMAL + "."
                    + TestUtils.EURO.getCurrencyCode(), (short) 2);
            assertEquals(new Short("2"), AccountingRules.getDigitsAfterDecimal(TestUtils.EURO));
        } finally {
            configMgr.clearProperty(AccountingRulesConstants.ADDITIONAL_CURRENCY_CODES);
            configMgr.clearProperty(AccountingRulesConstants.DIGITS_AFTER_DECIMAL + "."
                    + TestUtils.EURO.getCurrencyCode());
        }
    }

    /**
     * Example usage: LBP should round to the 10s or 100s. Not sure if
     * DigitsAfterDecimal is the correct configuration option (there may be
     * several that need to go negative).
     * 
     * See: <a href="http://mingle.mifos.org/projects/mifos/cards/2306">Card
     * 2306</a> and <a href="http://mifosforge.jira.com/browse/MIFOS-2600">Issue
     * 2600</a>.
     */
    @Test
    public void canConfigureNegativeDigitsAfterDecimal() {
        // TODO: implement
    }
}
