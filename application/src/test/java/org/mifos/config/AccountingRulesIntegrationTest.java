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

import junit.framework.Assert;

import org.apache.commons.configuration.Configuration;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups = { "integration", "configTestSuite" })
public class AccountingRulesIntegrationTest extends MifosIntegrationTestCase {

    public AccountingRulesIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    Configuration configuration;

    @BeforeMethod
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @AfterMethod
    @Override
    protected void tearDown() throws Exception {
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    @BeforeClass
    public static void init() throws Exception {
        MifosLogManager.configureLogging();
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
        Assert.assertEquals(configRoundingMode, AccountingRules.getCurrencyRoundingMode());
        // clear the RoundingRule property from the config file so should get
        // the default value
        configMgr.clearProperty(AccountingRulesConstants.CURRENCY_ROUNDING_MODE);
        RoundingMode defaultValue = AccountingRules.getCurrencyRoundingMode();
        Assert.assertEquals(defaultValue, RoundingMode.HALF_UP);
        // now set a wrong rounding mode in config
        roundingMode = "UP";
        configMgr.addProperty(AccountingRulesConstants.CURRENCY_ROUNDING_MODE, roundingMode);
        try {
            AccountingRules.getCurrencyRoundingMode();
            fail();
        } catch (RuntimeException e) {
            Assert.assertEquals(
                    "CurrencyRoundingMode defined in the config file is not CEILING, FLOOR, HALF_UP. It is "
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
        Assert.assertEquals(configRoundingMode, AccountingRules.getInitialRoundingMode());
        // clear the RoundingRule property from the config file
        configMgr.clearProperty(AccountingRulesConstants.INITIAL_ROUNDING_MODE);
        RoundingMode defaultValue = AccountingRules.getInitialRoundingMode();
        Assert.assertEquals(defaultValue, RoundingMode.HALF_UP);
        // now set a wrong rounding mode in config
        roundingMode = "UP";
        configMgr.addProperty(AccountingRulesConstants.INITIAL_ROUNDING_MODE, roundingMode);
        try {
            AccountingRules.getInitialRoundingMode();
            fail();
        } catch (RuntimeException e) {
            Assert.assertEquals("InitialRoundingMode defined in the config file is not CEILING, FLOOR, HALF_UP. It is "
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
        Assert.assertEquals(configRoundingMode, AccountingRules.getFinalRoundingMode());
        // clear the RoundingRule property from the config file
        configMgr.clearProperty(AccountingRulesConstants.FINAL_ROUNDING_MODE);
        RoundingMode defaultValue = AccountingRules.getFinalRoundingMode();
        Assert.assertEquals(defaultValue, RoundingMode.CEILING);
        // now set a wrong rounding mode in config
        roundingMode = "DOWN";
        configMgr.addProperty(AccountingRulesConstants.FINAL_ROUNDING_MODE, roundingMode);
        try {
            AccountingRules.getFinalRoundingMode();
            fail();
        } catch (RuntimeException e) {
            Assert.assertEquals("FinalRoundingMode defined in the config file is not CEILING, FLOOR, HALF_UP. It is "
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
        Assert.assertEquals(new BigDecimal(roundOffMultiple), AccountingRules.getFinalRoundOffMultiple());
        // clear the RoundingRule property from the config file
        configMgr.clearProperty(AccountingRulesConstants.FINAL_ROUND_OFF_MULTIPLE);
        BigDecimal defaultValue = AccountingRules.getFinalRoundOffMultiple();
        Assert.assertEquals(defaultValue, new BigDecimal("1"));
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
        Assert.assertEquals(new BigDecimal(roundOffMultiple), AccountingRules.getInitialRoundOffMultiple());
        // clear the RoundingRule property from the config file
        configMgr.clearProperty(AccountingRulesConstants.INITIAL_ROUND_OFF_MULTIPLE);
        BigDecimal defaultValue = AccountingRules.getInitialRoundOffMultiple();
        Assert.assertEquals(defaultValue, new BigDecimal("1"));
        // save it back
        configMgr.addProperty(AccountingRulesConstants.INITIAL_ROUND_OFF_MULTIPLE, configuredRoundOffMultiple
                .toString());
    }

    @Test
    public void testGetMifosCurrency() {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        String currencyCode = configMgr.getString(AccountingRulesConstants.CURRENCY_CODE);
        configMgr.setProperty(AccountingRulesConstants.CURRENCY_CODE, "INR");
        MifosCurrency currency = AccountingRules.getMifosCurrency();
        Assert.assertEquals(currency.getCurrencyCode(), "INR");
        configMgr.setProperty(AccountingRulesConstants.CURRENCY_CODE, "EUR");
        currency = AccountingRules.getMifosCurrency();
        Assert.assertEquals(currency.getCurrencyCode(), "EUR");
        configMgr.setProperty(AccountingRulesConstants.CURRENCY_CODE, "UUU");
        try {
            currency = AccountingRules.getMifosCurrency();
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
        Assert.assertEquals(insertedDays, AccountingRules.getNumberOfInterestDays());
        insertedDays = 360;
        // set new value
        configMgr.setProperty(AccountingRulesConstants.NUMBER_OF_INTEREST_DAYS, insertedDays);
        // return value from accounting rules class has to be the value defined
        // in the config file
        Assert.assertEquals(insertedDays, AccountingRules.getNumberOfInterestDays());
        insertedDays = 355;
        configMgr.setProperty(AccountingRulesConstants.NUMBER_OF_INTEREST_DAYS, insertedDays);
        // throw exception because the invalid value 355
        try {
            AccountingRules.getNumberOfInterestDays();
            fail();
        } catch (RuntimeException e) {
            Assert.assertEquals(
                    "Invalid number of interest days defined in property file " + insertedDays.shortValue(), e
                            .getMessage());
        } finally {
            configMgr.setProperty(AccountingRulesConstants.NUMBER_OF_INTEREST_DAYS, interestDaysInConfig);
        }
    }

    private void checkDigitsAfterDecimalMultiple(int digitsAfterDecimalInt, String multiple) {
        Short digitsAfterDecimal = (short) digitsAfterDecimalInt;
        ConfigurationManager.getInstance().setProperty(AccountingRulesConstants.DIGITS_AFTER_DECIMAL,
                digitsAfterDecimal);
        Assert.assertEquals(new BigDecimal(multiple), AccountingRules.getDigitsAfterDecimalMultiple());
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

}
