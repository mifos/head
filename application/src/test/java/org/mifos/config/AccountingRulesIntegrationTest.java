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
import org.apache.commons.configuration.Configuration;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.config.AccountingRules;
import org.mifos.application.master.business.MifosCurrency;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups= {"integration", "configTestSuite"} )
public class AccountingRulesIntegrationTest extends MifosIntegrationTest {

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
        MifosLogManager.configure(FilePaths.LOG_CONFIGURATION_FILE);
    }

    @Test
    public void testGetCurrencyRoundingMode() {
        RoundingMode configuredMode = AccountingRules.getCurrencyRoundingMode();
        String roundingMode = "FLOOR";
        RoundingMode configRoundingMode = RoundingMode.FLOOR;
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        configMgr.setProperty(AccountingRules.AccountingRulesCurrencyRoundingMode, roundingMode);
        // return value from accounting rules class has to be the value defined
        // in the config file
        assertEquals(configRoundingMode, AccountingRules.getCurrencyRoundingMode());
        // clear the RoundingRule property from the config file so should get
        // the default value
        configMgr.clearProperty(AccountingRules.AccountingRulesCurrencyRoundingMode);
        RoundingMode defaultValue = AccountingRules.getCurrencyRoundingMode();
        assertEquals(defaultValue, RoundingMode.HALF_UP);
        // now set a wrong rounding mode in config
        roundingMode = "UP";
        configMgr.addProperty(AccountingRules.AccountingRulesCurrencyRoundingMode, roundingMode);
        try {
            AccountingRules.getCurrencyRoundingMode();
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(),
                    "CurrencyRoundingMode defined in the config file is not CEILING, FLOOR, HALF_UP. It is "
                            + roundingMode);
        }
        // save it back
        configMgr.setProperty(AccountingRules.AccountingRulesCurrencyRoundingMode, configuredMode.toString());

    }

    @Test
    public void testGetInitialRoundingMode() {
        RoundingMode configuredMode = AccountingRules.getInitialRoundingMode();
        String roundingMode = "FLOOR";
        RoundingMode configRoundingMode = RoundingMode.FLOOR;
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        configMgr.setProperty(AccountingRules.AccountingRulesInitialRoundingMode, roundingMode);
        // return value from accounting rules class has to be the value defined
        // in the config file
        assertEquals(configRoundingMode, AccountingRules.getInitialRoundingMode());
        // clear the RoundingRule property from the config file
        configMgr.clearProperty(AccountingRules.AccountingRulesInitialRoundingMode);
        RoundingMode defaultValue = AccountingRules.getInitialRoundingMode();
        assertEquals(defaultValue, RoundingMode.HALF_UP);
        // now set a wrong rounding mode in config
        roundingMode = "UP";
        configMgr.addProperty(AccountingRules.AccountingRulesInitialRoundingMode, roundingMode);
        try {
            AccountingRules.getInitialRoundingMode();
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(),
                    "InitialRoundingMode defined in the config file is not CEILING, FLOOR, HALF_UP. It is "
                            + roundingMode);
        }
        // save it back
        configMgr.setProperty(AccountingRules.AccountingRulesInitialRoundingMode, configuredMode.toString());

    }

    @Test
    public void testGetFinalRoundingMode() {
        RoundingMode configuredMode = AccountingRules.getFinalRoundingMode();
        String roundingMode = "CEILING";
        RoundingMode configRoundingMode = RoundingMode.CEILING;
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        configMgr.setProperty(AccountingRules.AccountingRulesFinalRoundingMode, roundingMode);
        // return value from accounting rules class has to be the value defined
        // in the config file
        assertEquals(configRoundingMode, AccountingRules.getFinalRoundingMode());
        // clear the RoundingRule property from the config file
        configMgr.clearProperty(AccountingRules.AccountingRulesFinalRoundingMode);
        RoundingMode defaultValue = AccountingRules.getFinalRoundingMode();
        assertEquals(defaultValue, RoundingMode.CEILING);
        // now set a wrong rounding mode in config
        roundingMode = "DOWN";
        configMgr.addProperty(AccountingRules.AccountingRulesFinalRoundingMode, roundingMode);
        try {
            AccountingRules.getFinalRoundingMode();
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(),
                    "FinalRoundingMode defined in the config file is not CEILING, FLOOR, HALF_UP. It is "
                            + roundingMode);
        }
        // save it back
        configMgr.setProperty(AccountingRules.AccountingRulesFinalRoundingMode, configuredMode.toString());

    }

    @Test
    public void testGetFinalRoundOffMultiple() {
        BigDecimal configuredRoundOffMultiple = AccountingRules.getFinalRoundOffMultiple();
        String roundOffMultiple = "1";
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        configMgr.setProperty(AccountingRules.AccountingRulesFinalRoundOffMultiple, roundOffMultiple);
        // return value from accounting rules class has to be the value defined
        // in the config file
        assertEquals(new BigDecimal(roundOffMultiple), AccountingRules.getFinalRoundOffMultiple());
        // clear the RoundingRule property from the config file
        configMgr.clearProperty(AccountingRules.AccountingRulesFinalRoundOffMultiple);
        BigDecimal defaultValue = AccountingRules.getFinalRoundOffMultiple();
        assertEquals(defaultValue, new BigDecimal("1"));
        // save it back
        configMgr.addProperty(AccountingRules.AccountingRulesFinalRoundOffMultiple, configuredRoundOffMultiple
                .toString());

    }

    @Test
    public void testGetInitialRoundOffMultiple() {
        BigDecimal configuredRoundOffMultiple = AccountingRules.getInitialRoundOffMultiple();
        String roundOffMultiple = "1";
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        configMgr.setProperty(AccountingRules.AccountingRulesInitialRoundOffMultiple, roundOffMultiple);
        // return value from accounting rules class has to be the value defined
        // in the config file
        assertEquals(new BigDecimal(roundOffMultiple), AccountingRules.getInitialRoundOffMultiple());
        // clear the RoundingRule property from the config file
        configMgr.clearProperty(AccountingRules.AccountingRulesInitialRoundOffMultiple);
        BigDecimal defaultValue = AccountingRules.getInitialRoundOffMultiple();
        assertEquals(defaultValue, new BigDecimal("1"));
        // save it back
        configMgr.addProperty(AccountingRules.AccountingRulesInitialRoundOffMultiple, configuredRoundOffMultiple
                .toString());

    }

    @Test
    public void testGetMifosCurrency() {

        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        String currencyCode = configMgr.getString(AccountingRules.AccountingRulesCurrencyCode);
        configMgr.setProperty(AccountingRules.AccountingRulesCurrencyCode, "INR");
        MifosCurrency currency = AccountingRules.getMifosCurrency();
        assertEquals(currency.getCurrencyCode(), "INR");
        configMgr.setProperty(AccountingRules.AccountingRulesCurrencyCode, "EUR");
        currency = AccountingRules.getMifosCurrency();
        assertEquals(currency.getCurrencyCode(), "EUR");
        configMgr.setProperty(AccountingRules.AccountingRulesCurrencyCode, "UUU");
        try {
            currency = AccountingRules.getMifosCurrency();
        } catch (Exception e) {

        }
        configMgr.setProperty(AccountingRules.AccountingRulesCurrencyCode, currencyCode);

    }

    @Test
    public void testGetDigitsAfterDecimal() {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        Short digitsAfterDecimalSaved = AccountingRules.getDigitsAfterDecimal();
        Short digitsAfterDecimal = 1;
        configMgr.addProperty(AccountingRules.AccountingRulesDigitsAfterDecimal, digitsAfterDecimal);
        // return value from accounting rules class has to be the value defined
        // in the config file
        assertEquals(digitsAfterDecimal, AccountingRules.getDigitsAfterDecimal());
        // clear the DigitsAfterDecimal property from the config file
        configMgr.clearProperty(AccountingRules.AccountingRulesDigitsAfterDecimal);
        // should throw exception
        try {
            AccountingRules.getDigitsAfterDecimal();
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(), "The number of digits after decimal is not defined in the config file.");
        }
        configMgr.setProperty(AccountingRules.AccountingRulesDigitsAfterDecimal, digitsAfterDecimalSaved);
    }

    public void testGetDigitsBeforeDecimal() {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        Short digitsBeforeDecimal = 7;
        configMgr.setProperty(AccountingRules.AccountingRulesDigitsBeforeDecimal, digitsBeforeDecimal);
        // return value from accounting rules class has to be the value defined
        // in the config file
        assertEquals(digitsBeforeDecimal, AccountingRules.getDigitsBeforeDecimal());
        // clear the DigitsBeforeDecimal property from the config file
        configMgr.clearProperty(AccountingRules.AccountingRulesDigitsBeforeDecimal);
        // should throw exception
        try {
            AccountingRules.getDigitsBeforeDecimal();
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(), "The number of digits before decimal is not defined in the config file.");
        }
        configMgr.setProperty(AccountingRules.AccountingRulesDigitsBeforeDecimal, digitsBeforeDecimal);

    }

    @Test
    public void testGetDigitsBeforeDecimalForInterest() {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        Short digitsBeforeDecimalForInterest = 10;
        configMgr.addProperty(AccountingRules.AccountingRulesDigitsBeforeDecimalForInterest,
                digitsBeforeDecimalForInterest);
        // return value from accounting rules class has to be the value defined
        // in the config file
        assertEquals(digitsBeforeDecimalForInterest, AccountingRules.getDigitsBeforeDecimalForInterest());
        // clear the DigitsBeforeDecimalForInterest property from the config
        // file
        configMgr.clearProperty(AccountingRules.AccountingRulesDigitsBeforeDecimalForInterest);
        // should throw exception
        try {
            AccountingRules.getDigitsBeforeDecimalForInterest();
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(),
                    "The number of digits before decimal for interest is not defined in the config file.");
        }
        configMgr.setProperty(AccountingRules.AccountingRulesDigitsBeforeDecimalForInterest,
                digitsBeforeDecimalForInterest);

    }

    @Test
    public void testGetDigitsAfterDecimalForInterest() {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        Short digitsAfterDecimalForInterest = 5;
        configMgr.addProperty(AccountingRules.AccountingRulesDigitsBeforeDecimalForInterest,
                digitsAfterDecimalForInterest);
        // return value from accounting rules class has to be the value defined
        // in the config file
        assertEquals(digitsAfterDecimalForInterest, AccountingRules.getDigitsAfterDecimalForInterest());
        // clear the DigitsBeforeDecimalForInterest property from the config
        // file
        configMgr.clearProperty(AccountingRules.AccountingRulesDigitsAfterDecimalForInterest);
        // should throw exception
        try {
            AccountingRules.getDigitsAfterDecimalForInterest();
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(),
                    "The number of digits after decimal for interest is not defined in the config file.");
        }
        configMgr.setProperty(AccountingRules.AccountingRulesDigitsAfterDecimalForInterest,
                digitsAfterDecimalForInterest);

    }

    @Test
    public void testGetMinInterests() {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        Double minInterestSaved = AccountingRules.getMinInterest();
        Double minInterest = 0.0;
        configMgr.addProperty(AccountingRules.AccountingRulesMinInterest, minInterest);
        // return value from accounting rules class has to be the value defined
        // in the config file
        assertEquals(minInterest, AccountingRules.getMinInterest());
        // clear the DigitsBeforeDecimalForInterest property from the config
        // file
        configMgr.clearProperty(AccountingRules.AccountingRulesMinInterest);
        // should throw exception
        try {
            AccountingRules.getMinInterest();
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(), "Min interest is not defined in the config file.");
        }
        configMgr.setProperty(AccountingRules.AccountingRulesMinInterest, minInterestSaved);

    }

    @Test
    public void testGetMaxInterests() {
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        Double maxInterestSaved = AccountingRules.getMaxInterest();
        Double maxInterest = 999.0;
        configMgr.addProperty(AccountingRules.AccountingRulesMaxInterest, maxInterest);
        // return value from accounting rules class has to be the value defined
        // in the config file
        assertEquals(maxInterest, AccountingRules.getMaxInterest());
        // clear the DigitsBeforeDecimalForInterest property from the config
        // file
        configMgr.clearProperty(AccountingRules.AccountingRulesMaxInterest);
        // should throw exception
        try {
            AccountingRules.getMaxInterest();
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(), "Max interest is not defined in the config file.");
        }
        configMgr.setProperty(AccountingRules.AccountingRulesMaxInterest, maxInterestSaved);

    }

    @Test
    public void testGetAmountToBeRoundedTo() {
        Float defaultValue = (float) 0.5;
        Float amountToBeRoundedTo = (float) 0.01;
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        configMgr.addProperty(AccountingRules.AccountingRulesAmountToBeRoundedTo, amountToBeRoundedTo);
        // return value from accounting rules class has to be the value defined
        // in the config file
        assertEquals(amountToBeRoundedTo, AccountingRules.getAmountToBeRoundedTo(defaultValue));
        // clear the AmountToBeRoundedTo property from the config file
        configMgr.clearProperty(AccountingRules.AccountingRulesAmountToBeRoundedTo);
        // now the return value from accounting rules class has to be the
        // default value (value from db)
        assertEquals(defaultValue, AccountingRules.getAmountToBeRoundedTo(defaultValue));
    }

    @Test
    public void testGetRoundingRule() {
        RoundingMode defaultValue = RoundingMode.CEILING;
        String roundingMode = "FLOOR";
        RoundingMode configRoundingMode = RoundingMode.FLOOR;
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        configMgr.addProperty(AccountingRules.AccountingRulesRoundingRule, roundingMode);
        // return value from accounting rules class has to be the value defined
        // in the config file
        assertEquals(configRoundingMode, AccountingRules.getRoundingRule(defaultValue));
        // clear the RoundingRule property from the config file
        configMgr.clearProperty(AccountingRules.AccountingRulesRoundingRule);
        // now the return value from accounting rules class has to be the
        // default value (value from db)
        assertEquals(defaultValue, AccountingRules.getRoundingRule(defaultValue));
        // now set a wrong rounding mode in config
        roundingMode = "UP";
        configMgr.addProperty(AccountingRules.AccountingRulesRoundingRule, roundingMode);
        try {
            AccountingRules.getRoundingRule(defaultValue);
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(),
                    "The rounding mode defined in the config file is not CEILING, FLOOR, HALF_UP. It is "
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
        // return value from accounting rules class has to be the value defined
        // in the config file
        assertEquals(insertedDays, AccountingRules.getNumberOfInterestDays());
        insertedDays = 355;
        configMgr.setProperty(AccountingRules.AccountingRulesNumberOfInterestDays, insertedDays);
        // throw exception because the invalid value 355
        try {
            AccountingRules.getNumberOfInterestDays();
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(), "Invalid number of interest days defined in property file "
                    + insertedDays.shortValue());
        }
        // clear the NumberOfInterestDays property from the config file
        configMgr.clearProperty(AccountingRules.AccountingRulesNumberOfInterestDays);
        // throw exception because no interest days defined in config file
        try {
            AccountingRules.getNumberOfInterestDays();
        } catch (RuntimeException e) {
            assertEquals(e.getMessage(), "The number of interest days is not defined in the config file ");
        }

        configMgr.addProperty(AccountingRules.AccountingRulesNumberOfInterestDays, interestDaysInConfig);
    }

    private void checkDigitsAfterDecimalMultiple(int digitsAfterDecimalInt, String multiple) {
        Short digitsAfterDecimal = (short) digitsAfterDecimalInt;
        ConfigurationManager.getInstance().setProperty(AccountingRules.AccountingRulesDigitsAfterDecimal,
                digitsAfterDecimal);
        assertEquals(new BigDecimal(multiple), AccountingRules.getDigitsAfterDecimalMultiple());
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
            configMgr.setProperty(AccountingRules.AccountingRulesDigitsAfterDecimal, digitsAfterDecimalSaved);
        }
    }

}
