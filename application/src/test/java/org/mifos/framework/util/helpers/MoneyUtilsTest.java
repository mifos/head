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

import org.junit.Ignore;
import org.junit.Test;
import org.mifos.framework.TestUtils;
import org.testng.Assert;

public class MoneyUtilsTest {
    
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

    @Ignore("FIXME: failing at trunk build 644")
    @Test
    public void testCurrencyRound(){
        Money m = MoneyUtils.createMoney(TestUtils.EURO, 454.54);
        Assert.assertEquals(MoneyUtils.currencyRound(m), new Money(TestUtils.EURO, "454.5"));
        m = MoneyUtils.createMoney(TestUtils.EURO, 454.554);
        Assert.assertEquals(MoneyUtils.currencyRound(m), new Money(TestUtils.EURO, "454.6"));
    }

    @Ignore("FIXME: failing at trunk build 644")
    @Test
    public void testInitialRound(){
        Money m = MoneyUtils.createMoney(TestUtils.EURO, 454.44);
        Assert.assertEquals(MoneyUtils.initialRound(m), new Money(TestUtils.EURO, "454.0"));
        m = MoneyUtils.createMoney(TestUtils.EURO, 454.554);
        Assert.assertEquals(MoneyUtils.initialRound(m), new Money(TestUtils.EURO, "455.0"));
    }

    @Test
    public void testFinalRound(){
        Money m = MoneyUtils.createMoney(TestUtils.EURO, 454.00);
        Assert.assertEquals(MoneyUtils.finalRound(m), new Money(TestUtils.EURO, "454.0"));
        m = MoneyUtils.createMoney(TestUtils.EURO, 454.0001);
        Assert.assertEquals(MoneyUtils.finalRound(m), new Money(TestUtils.EURO, "455.0"));
    }
}
