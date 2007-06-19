/**
 
 * MoneyTest.java    version: 1.0
 
 
 
 * Copyright (c) 2005-2006 Grameen Foundation USA
 
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
 
 * All rights reserved.
 
 
 
 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 *
 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the
 
 * License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license
 
 * and how it is applied.
 
 *
 
 */
package org.mifos.framework.util.helpers;

import static org.mifos.framework.TestUtils.EURO;
import static org.mifos.framework.TestUtils.RUPEE;

import java.math.BigDecimal;

import junit.framework.TestCase;

/**
 * This class is used to test Money class.
 */
public class MoneyTest extends TestCase {

	public void testAdd() {
		Money money = new Money(RUPEE, "100.0");
		Money addendend = new Money(RUPEE, "200.0");
		assertEquals("testing Add, should succeed", new Money(RUPEE, "300.0"),
				money.add(addendend));
	}

	public void testAddMultipleDecimalAmounts() {
		Money money = new Money(RUPEE, "0.1");
		Money money1 = money.add(new Money(RUPEE, "0.1"));
		Money money2 = money1.add(new Money(RUPEE, "0.1"));
		Money money3 = money2.add(new Money(RUPEE, "0.1"));
		Money money4 = money3.add(new Money(RUPEE, "0.1"));
		assertEquals("testing addMultipleDecimalAmounts, should succeed",
				new Money(RUPEE, "0.5"), money4);
	}

	public void testAddWithDiffCurrencies() {
		Money money = new Money(RUPEE, "100.0");
		try {
			Money addendendWithDiffCurrecny = new Money(EURO, "200");
			money.add(addendendWithDiffCurrecny);
			fail("testing Add with different currencies should throw an exception.");
		} catch (IllegalArgumentException e) {
		}
	}

	public void testSubtract() {
		Money subtrahend = new Money(RUPEE, "100.0");
		Money money = new Money(RUPEE, "200.0");
		assertEquals("testing subtract, should succeed", new Money(RUPEE,
				"100.0"), money.subtract(subtrahend));
	}

	public void testSubtractWithDiffCurrencies() {
		Money money = new Money(RUPEE, "100.0");
		try {
			Money subtrahendWithDiffCurrecny = new Money(EURO, "100");
			money.subtract(subtrahendWithDiffCurrecny);
			fail("testing subtract with different currencies should throw an exception.");
		} catch (IllegalArgumentException e) {
		}
	}

	public void testMultiply() {
		Money multiplicand = new Money(RUPEE, "10.0");
		Money money = new Money(RUPEE, "20.0");
		assertEquals("testing multiply, should succeed", new Money(RUPEE,
				"200.0"), money.multiply(multiplicand));
	}

	public void testFactorMultiply() {
		Money money = new Money(RUPEE, "100.0");
		Double factor = new Double(1 + (24 / 100.0));
		assertEquals("testing multiply with a factor, should succeed",
				new Money(RUPEE, "124.0"), money.multiply(factor));
	}

	public void testMultiplyWithDiffCurrencies() {
		Money money = new Money(RUPEE, "20.0");
		try {
			Money multiplicandWithDiffCurrecny = new Money(EURO, "10");
			money.multiply(multiplicandWithDiffCurrecny);
			fail("testing multiply with different currencies should throw an exception.");
		} catch (IllegalArgumentException e) {
		}
	}

	public void testDivide() {
		Money dividend = new Money(RUPEE, "10.0");
		Money money = new Money(RUPEE, "20.0");
		assertEquals("testing divide, should succeed", new Money(RUPEE, "2.0"),
				money.divide(dividend));
	}

	public void testDivideWithDiffCurrencies() {
		Money money = new Money(RUPEE, "20.0");
		try {
			Money dividendWithDiffCurrecny = new Money(EURO, "10");
			money.divide(dividendWithDiffCurrecny);
			fail("testing divide with different currencies should throw an exception.");
		} catch (IllegalArgumentException e) {
		}
	}

	public void testNegate() {
		Money money = new Money(RUPEE, "20.0");
		assertEquals(new Money(RUPEE, "-20.0"), money.negate());
	}

	public void testRoundUp() {
		Money money = new Money(RUPEE, "142.34");
		assertEquals(new Money(RUPEE, "143.00"), Money.round(money));
	}

	public void testRoundDown() {
		Money money = new Money(EURO, "142.34");
		assertEquals(new Money(EURO, "142.00"), Money.round(money));
	}

	public void testHashCode() {
		Money money = new Money(RUPEE, "142.34");
		BigDecimal amnt = new BigDecimal(142.34);
		assertEquals((money.getCurrency().getCurrencyId() * 100 + amnt
				.intValue()), money.hashCode());
	}

	public void testHashCodeForNullAmnt() {
		Money money = new Money(RUPEE, "");
		assertEquals(0, money.hashCode());
	}

	public void testSetScale() {
		assertEquals(142.3, new Money(RUPEE, "142.344").getAmountDoubleValue(),
				0.00000001);
		assertEquals(142.4, new Money(RUPEE, "142.356").getAmountDoubleValue(),
				0.00000001);
	}

	public void testToString() {
		Money money = new Money(RUPEE, "4456456456.6");
		assertEquals("The toString of money returns : ", "4456456456.6", money
				.toString());
	}

}
