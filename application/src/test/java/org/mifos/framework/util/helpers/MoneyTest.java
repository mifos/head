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

import static org.mifos.framework.TestUtils.EURO;
import static org.mifos.framework.TestUtils.RUPEE;

import java.math.BigDecimal;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.core.CurrencyMismatchException;
import org.testng.annotations.Test;

@Test(groups={"unit", "fastTestsSuite"},  dependsOnGroups={"productMixTestSuite"})
public class MoneyTest extends TestCase {
    private static final double DELTA = 0.00000001;

    public void testAdd() {
        Money money = new Money(RUPEE, "100.0");
        Money addendend = new Money(RUPEE, "200.0");
       Assert.assertEquals("testing Add, should succeed", new Money(RUPEE, "300.0"), money.add(addendend));
    }

    public void testAddMultipleDecimalAmounts() {
        Money money = new Money(RUPEE, "0.1");
        Money money1 = money.add(new Money(RUPEE, "0.1"));
        Money money2 = money1.add(new Money(RUPEE, "0.1"));
        Money money3 = money2.add(new Money(RUPEE, "0.1"));
        Money money4 = money3.add(new Money(RUPEE, "0.1"));
       Assert.assertEquals("testing addMultipleDecimalAmounts, should succeed", new Money(RUPEE, "0.5"), money4);
    }

    public void testAddWithDiffCurrencies() {
        Money money = new Money(RUPEE, "100.0");
        try {
            Money addendendWithDiffCurrecny = new Money(EURO, "200");
            money.add(addendendWithDiffCurrecny);
            Assert.fail("testing Add with different currencies should throw an exception.");
        } catch (CurrencyMismatchException e) {
        }
    }

    public void testSubtract() {
        Money subtrahend = new Money(RUPEE, "100.0");
        Money money = new Money(RUPEE, "200.0");
       Assert.assertEquals("testing subtract, should succeed", new Money(RUPEE, "100.0"), money.subtract(subtrahend));
    }

    public void testSubtractWithDiffCurrencies() {
        Money money = new Money(RUPEE, "100.0");
        try {
            Money subtrahendWithDiffCurrecny = new Money(EURO, "100");
            money.subtract(subtrahendWithDiffCurrecny);
            Assert.fail("testing subtract with different currencies should throw an exception.");
        } catch (CurrencyMismatchException e) {
        }
    }

    public void testMultiply() {
        Money multiplicand = new Money(RUPEE, "10.0");
        Money money = new Money(RUPEE, "20.0");
       Assert.assertEquals("testing multiply, should succeed", new Money(RUPEE, "200.0"), money.multiply(multiplicand));
    }

    public void testFactorMultiply() {
        Money money = new Money(RUPEE, "100.0");
        Double factor = new Double(1 + (24 / 100.0));
       Assert.assertEquals("testing multiply with a factor, should succeed", new Money(RUPEE, "124.0"), money
                .multiply(factor));
    }

    public void testMultiplyWithDiffCurrencies() {
        Money money = new Money(RUPEE, "20.0");
        try {
            Money multiplicandWithDiffCurrecny = new Money(EURO, "10");
            money.multiply(multiplicandWithDiffCurrecny);
            Assert.fail("testing multiply with different currencies should throw an exception.");
        } catch (CurrencyMismatchException e) {
        }
    }

    public void testDivideByMoney() {
        Money dividend = new Money(RUPEE, "10.0");
        Money money = new Money(RUPEE, "20.0");
       Assert.assertEquals("testing divide, should succeed", new Money(RUPEE, "2.0"), money.divide(dividend));
    }

    public void testDivide() {
        BigDecimal dividend = new BigDecimal("10.0", Money.getInternalPrecisionAndRounding());
        Money money = new Money(RUPEE, "20.0");
       Assert.assertEquals("testing divide, should succeed", new Money(RUPEE, "2.0"), money.divide(dividend));
    }

    public void testDivideRepeating() {
        BigDecimal dividend = new BigDecimal("3.0", Money.getInternalPrecisionAndRounding());
        Money money = new Money(RUPEE, "10.0");
       Assert.assertEquals("testing divide, should succeed", new Money(RUPEE, "3.3333333333330"), money.divide(dividend));
    }

    public void testDivideWithDiffCurrencies() {
        Money money = new Money(RUPEE, "20.0");
        try {
            Money dividendWithDiffCurrecny = new Money(EURO, "10");
            money.divide(dividendWithDiffCurrecny);
            Assert.fail("testing divide with different currencies should throw an exception.");
        } catch (CurrencyMismatchException e) {
        }
    }

    public void testNegate() {
        Money money = new Money(RUPEE, "20.0");
       Assert.assertEquals(new Money(RUPEE, "-20.0"), money.negate());
    }

    public void testRoundUp() {
        Money money = new Money(RUPEE, "142.34");
       Assert.assertEquals(new Money(RUPEE, "143.00"), Money.round(money));
    }

    public void testRoundDown() {
        Money money = new Money(EURO, "142.34");
       Assert.assertEquals(new Money(EURO, "142.00"), Money.round(money));
    }

    public void testRoundRepeating() {
        MifosCurrency currency = new MifosCurrency((short) 1, "test", MifosCurrency.CEILING_MODE, (float) 3.0, (short) 1,
                "USD");
        Money money = new Money(currency, "1");
       Assert.assertEquals(new Money(currency, "3"), Money.round(money));
    }

    public void testIsRoundedAmount() {
       Assert.assertTrue((new Money(EURO, "1")).isRoundedAmount());
        Assert.assertFalse((new Money(EURO, "1.1")).isRoundedAmount());
    }

    public void testDivideMoneyRepeating() {
        Money dividend = new Money(RUPEE, "3.0");
        Money money = new Money(RUPEE, "10.0");
       Assert.assertEquals("testing divide, should succeed", new Money(RUPEE, "3.3333333333330"), money.divide(dividend));
    }

    public void testHashCode() {
        Money money = new Money(RUPEE, "142.34");
        BigDecimal amnt = new BigDecimal(142.34);
       Assert.assertEquals((money.getCurrency().getCurrencyId() * 100 + amnt.intValue()), money.hashCode());
    }

    public void testHashCodeForNullAmnt() {
        Money money = new Money(RUPEE, "");
       Assert.assertEquals(0, money.hashCode());
    }

    public void testSetScaleNewMoney() {
       Assert.assertEquals(142.344, new Money(RUPEE, "142.344").getAmountDoubleValue(), DELTA);
       Assert.assertEquals(142.356, new Money(RUPEE, "142.356").getAmountDoubleValue(), DELTA);
    }

    public void testToString() {
        Money money = new Money(RUPEE, "4456456456.6");
       Assert.assertEquals("The toString of money returns : ", "4456456456.6", money.toString());
    }

    public void testIsGreaterThan() {
        Money large = new Money(RUPEE, "10.0");
        Money small = new Money(RUPEE, "1.0");

       Assert.assertTrue("large should be greater than small", large.isGreaterThan(small));
        Assert.assertFalse("large should not be greater than itself", large.isGreaterThan(large));
        Assert.assertFalse("small should not be greater than large", small.isGreaterThan(large));
    }

    public void testIsLessThan() {
        Money large = new Money(EURO, "10.0");
        Money small = new Money(EURO, "1.0");

       Assert.assertTrue("small should be less than large", small.isLessThan(large));
        Assert.assertFalse("small should not be less than itself", small.isLessThan(small));
        Assert.assertFalse("large should not be less than small", large.isLessThan(small));
    }

    public void testIsLessThanForDifferentCurrencies() {
        Money large = new Money(RUPEE, "10.0");
        Money small = new Money(EURO, "1.0");

        try {
            Assert.assertFalse("large should not be less than small", large.isLessThan(small));
            Assert.fail("Comparing two different currencies should throw and exceeption");
        } catch (RuntimeException e) {
        }
    }

    public void testIsGreaterThanForDifferentCurrencies() {
        Money large = new Money(RUPEE, "10.0");
        Money small = new Money(EURO, "1.0");

        try {
            Assert.assertFalse("large should not be less than small", large.isGreaterThan(small));
            Assert.fail("Comparing two different currencies should throw and exceeption");
        } catch (RuntimeException e) {
        }
    }
}
