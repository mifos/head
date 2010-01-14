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
package org.mifos.framework.util.helpers;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.config.AccountingRules;
import org.mifos.config.AccountingRulesConstants;
import org.mifos.config.ConfigurationManager;
import org.mifos.framework.TestUtils;
import org.testng.Assert;

public class MoneyUtilsTest {

    private static ConfigurationManager configMgr;
    
    @BeforeClass
    public static void init() {
        configMgr = ConfigurationManager.getInstance();
        configMgr.setProperty(AccountingRulesConstants.CURRENCY_ROUNDING_MODE, RoundingMode.HALF_UP.toString());
        configMgr.setProperty(AccountingRulesConstants.DIGITS_AFTER_DECIMAL, "1");
        configMgr.setProperty(AccountingRulesConstants.INITIAL_ROUNDING_MODE, RoundingMode.HALF_UP.toString());
        configMgr.setProperty(AccountingRulesConstants.INITIAL_ROUND_OFF_MULTIPLE, "1");
        configMgr.setProperty(AccountingRulesConstants.FINAL_ROUNDING_MODE, RoundingMode.CEILING.toString());
        configMgr.setProperty(AccountingRulesConstants.FINAL_ROUND_OFF_MULTIPLE, "1");
    }
    
    @AfterClass
    public static void destroy() {
        configMgr.clear();
    }

    
    @Test
    public void testCreatMoneyWithBigDecimal() {
        Money m = MoneyUtils.createMoney(TestUtils.EURO, new BigDecimal("2.0"));
        Assert.assertEquals(m.getAmount(), new BigDecimal("2.0000000000000"));
        Assert.assertEquals(m.getCurrency(), TestUtils.EURO);
    }

    @Test
    public void testCreatMoneyWithBigDouble() {
        Money m = MoneyUtils.createMoney(TestUtils.EURO, 454.456974123578441d);
        Assert.assertEquals(m.getAmount(), new BigDecimal("454.4569741235785"));
        Assert.assertEquals(m.getAmountDoubleValue(), 454.4569741235785d);
        Assert.assertEquals(m.getAmount().doubleValue(), 454.4569741235785d);
        Assert.assertEquals(m.getCurrency(), TestUtils.EURO);
    }

    @Test
    public void testCurrencyRound() {
        Money m = MoneyUtils.createMoney(TestUtils.RUPEE, 454.545);
        Assert.assertEquals(MoneyUtils.currencyRound(m), new Money(TestUtils.RUPEE, "454.5"));
        m = MoneyUtils.createMoney(TestUtils.RUPEE, 454.559);
        Assert.assertEquals(MoneyUtils.currencyRound(m), new Money(TestUtils.RUPEE, "454.6"));
        m = MoneyUtils.createMoney(TestUtils.RUPEE, 454.551);
        Assert.assertEquals(MoneyUtils.currencyRound(m), new Money(TestUtils.RUPEE, "454.6"));
    }

    @Test
    public void testInitialRound() {
        Money m = MoneyUtils.createMoney(TestUtils.RUPEE, 454.49);
        Assert.assertEquals(MoneyUtils.initialRound(m), new Money(TestUtils.RUPEE, "454.0"));
        m = MoneyUtils.createMoney(TestUtils.RUPEE, 454.50);
        Assert.assertEquals(MoneyUtils.initialRound(m), new Money(TestUtils.RUPEE, "455.0"));
        m = MoneyUtils.createMoney(TestUtils.RUPEE, 454.51);
        Assert.assertEquals(MoneyUtils.initialRound(m), new Money(TestUtils.RUPEE, "455.0"));
    }

    @Test
    public void testFinalRound() {
        Money m = MoneyUtils.createMoney(TestUtils.RUPEE, 454.49);
        Assert.assertEquals(MoneyUtils.finalRound(m), new Money(TestUtils.RUPEE, "455.0"));
        m = MoneyUtils.createMoney(TestUtils.RUPEE, 454.01);
        Assert.assertEquals(MoneyUtils.finalRound(m), new Money(TestUtils.RUPEE, "455.0"));
        m = MoneyUtils.createMoney(TestUtils.RUPEE, 454.00);
        Assert.assertEquals(MoneyUtils.finalRound(m), new Money(TestUtils.RUPEE, "454.0"));
    }
}
