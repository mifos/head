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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.mifos.framework.TestUtils.EURO;
import static org.mifos.framework.TestUtils.RUPEE;

import java.math.BigDecimal;

import junit.framework.JUnit4TestAdapter;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mifos.application.master.business.MifosCurrency;
/**
 * This class is used to test Money class.
 */
public class MoneyTest {

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(MoneyTest.class);
	}
	
	@BeforeClass
	public static void init() {
		Money.setUsingNewMoney(false);
	}
	
	@Test
	public void testAdd() {
		Money money = new Money(RUPEE, "100.0");
		Money addendend = new Money(RUPEE, "200.0");
		assertEquals("testing Add, should succeed", new Money(RUPEE, "300.0"),
				money.add(addendend));
	}

	@Test
	public void testAddMultipleDecimalAmounts() {
		Money money = new Money(RUPEE, "0.1");
		Money money1 = money.add(new Money(RUPEE, "0.1"));
		Money money2 = money1.add(new Money(RUPEE, "0.1"));
		Money money3 = money2.add(new Money(RUPEE, "0.1"));
		Money money4 = money3.add(new Money(RUPEE, "0.1"));
		assertEquals("testing addMultipleDecimalAmounts, should succeed",
				new Money(RUPEE, "0.5"), money4);
	}

	@Test
	public void testAddWithDiffCurrencies() {
		Money money = new Money(RUPEE, "100.0");
		try {
			Money addendendWithDiffCurrecny = new Money(EURO, "200");
			money.add(addendendWithDiffCurrecny);
			fail("testing Add with different currencies should throw an exception.");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testSubtract() {
		Money subtrahend = new Money(RUPEE, "100.0");
		Money money = new Money(RUPEE, "200.0");
		assertEquals("testing subtract, should succeed", new Money(RUPEE,
				"100.0"), money.subtract(subtrahend));
	}

	@Test
	public void testSubtractWithDiffCurrencies() {
		Money money = new Money(RUPEE, "100.0");
		try {
			Money subtrahendWithDiffCurrecny = new Money(EURO, "100");
			money.subtract(subtrahendWithDiffCurrecny);
			fail("testing subtract with different currencies should throw an exception.");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testMultiply() {
		Money multiplicand = new Money(RUPEE, "10.0");
		Money money = new Money(RUPEE, "20.0");
		assertEquals("testing multiply, should succeed", new Money(RUPEE,
				"200.0"), money.multiply(multiplicand));
	}

	@Test
	public void testFactorMultiply() {
		Money money = new Money(RUPEE, "100.0");
		Double factor = new Double(1 + (24 / 100.0));
		assertEquals("testing multiply with a factor, should succeed",
				new Money(RUPEE, "124.0"), money.multiply(factor));
	}

	@Test
	public void testMultiplyWithDiffCurrencies() {
		Money money = new Money(RUPEE, "20.0");
		try {
			Money multiplicandWithDiffCurrecny = new Money(EURO, "10");
			money.multiply(multiplicandWithDiffCurrecny);
			fail("testing multiply with different currencies should throw an exception.");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testDivide() {
		Money dividend = new Money(RUPEE, "10.0");
		Money money = new Money(RUPEE, "20.0");
		assertEquals("testing divide, should succeed", new Money(RUPEE, "2.0"),
				money.divide(dividend));
	}

	@Test
	public void testDivideWithDiffCurrencies() {
		Money money = new Money(RUPEE, "20.0");
		try {
			Money dividendWithDiffCurrecny = new Money(EURO, "10");
			money.divide(dividendWithDiffCurrecny);
			fail("testing divide with different currencies should throw an exception.");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testNegate() {
		Money money = new Money(RUPEE, "20.0");
		assertEquals(new Money(RUPEE, "-20.0"), money.negate());
	}

	@Test
	public void testRoundUp() {
		Money money = new Money(RUPEE, "142.34");
		assertEquals(new Money(RUPEE, "143.00"), Money.round(money));
	}

	@Test
	public void testRoundDown() {
		Money money = new Money(EURO, "142.34");
		assertEquals(new Money(EURO, "142.00"), Money.round(money));
	}

	// TODO: This test is broken and needs to be fixed!
	@Test @Ignore
	public void testRoundRepeating() {
		MifosCurrency currency = new MifosCurrency((short)1,"test", "$", 
				MifosCurrency.CEILING_MODE, (float)3.0, (short)1, (short)1,"USD" );
		Money money = new Money(currency, "1");		
		// need to figure out what the actual result should be.  
		// this case was constructed just to generate the exception
		assertEquals(new Money(currency, "3"), Money.round(money));
	}

	// TODO: This test is broken and needs to be fixed!
	@Test @Ignore
	public void testDivideRepeating() {
		Money dividend = new Money(RUPEE, "3.0");
		Money money = new Money(RUPEE, "10.0");
		// need to figure out what final result should be, it won't be 0.0
		// this case was constructed just to generate the exception
		assertEquals("testing divide, should succeed", new Money(RUPEE, "3.3333333333330"),
				money.divide(dividend));
	}
	
	
	@Test
	public void testHashCode() {
		Money money = new Money(RUPEE, "142.34");
		BigDecimal amnt = new BigDecimal(142.34);
		assertEquals((money.getCurrency().getCurrencyId() * 100 + amnt
				.intValue()), money.hashCode());
	}

	@Test
	public void testHashCodeForNullAmnt() {
		Money money = new Money(RUPEE, "");
		assertEquals(0, money.hashCode());
	}

	@Test
	public void testSetScaleOldMoney() {
		assertEquals(142.3, new Money(RUPEE, "142.344").getAmountDoubleValue(),
				0.00000001);
		assertEquals(142.4, new Money(RUPEE, "142.356").getAmountDoubleValue(),
				0.00000001);
	}

	@Test @Ignore
	public void testSetScaleNewMoney() {
		assertEquals(142.344, new Money(RUPEE, "142.344").getAmountDoubleValue(),
				0.00000001);
		assertEquals(142.356, new Money(RUPEE, "142.356").getAmountDoubleValue(),
				0.00000001);
	}

	
	@Test
	public void testToString() {
		Money money = new Money(RUPEE, "4456456456.6");
		assertEquals("The toString of money returns : ", "4456456456.6", money
				.toString());
	}

	/*
	 * This test is being used to understand the behavior of the rounding amount,
	 * rounding mode, and digits after decimal.
	 */
	@Test
	public void testRoundingModesForOldMoney() {
		Short TEST_ID = 1;
		float ROUNDING_AMOUNT_1_0 = (float)1.0;
		float ROUNDING_AMOUNT_0_5 = (float)0.5;
		Short IS_DEFAULT_CURRENCY = 0;
		Short DIGITS_AFTER_DECIMAL_0 = 0;
		Short DIGITS_AFTER_DECIMAL_1 = 1;
		
		MifosCurrency ceilingCurrency = new MifosCurrency((short)1,"Test Currency","@",
				MifosCurrency.CEILING_MODE,ROUNDING_AMOUNT_1_0, IS_DEFAULT_CURRENCY,
				DIGITS_AFTER_DECIMAL_0,"TST");
		MifosCurrency floorCurrency = new MifosCurrency((short)2,"Test Currency","@",
				MifosCurrency.FLOOR_MODE,ROUNDING_AMOUNT_1_0, IS_DEFAULT_CURRENCY,
				DIGITS_AFTER_DECIMAL_0,"TST");
		
		// note that digits after decimal is enforced on creation of Money but
		// rounding mode is not
		assertEquals(new Money(ceilingCurrency, "100"),new Money(ceilingCurrency, "100.1"));
		assertEquals(new Money(ceilingCurrency, "101"),new Money(ceilingCurrency, "100.5"));
		assertEquals(new Money(ceilingCurrency, "11"), new Money(ceilingCurrency, "105").multiply(0.1));
		// note that rounding mode is not applied to a calculation
		assertEquals(new Money(ceilingCurrency, "10"), new Money(ceilingCurrency, "101").multiply(0.1));
		
		
		// note that digits after decimal is enforced on creation of Money but
		// rounding mode is not
		assertEquals(new Money(floorCurrency, "100"), new Money(floorCurrency, "100.1"));
		assertEquals(new Money(floorCurrency, "101"), new Money(floorCurrency, "100.5"));
		// note that rounding mode is not applied to a calculation
		assertEquals(new Money(floorCurrency, "11"), new Money(floorCurrency, "105").multiply(0.1));
		assertEquals(new Money(floorCurrency, "10"), new Money(floorCurrency, "101").multiply(0.1));
		
		MifosCurrency ceilingCurrency2 = new MifosCurrency((short)3,"Test Currency","@",
				MifosCurrency.CEILING_MODE,ROUNDING_AMOUNT_0_5, IS_DEFAULT_CURRENCY,
				DIGITS_AFTER_DECIMAL_1,"TST");
		MifosCurrency floorCurrency2 = new MifosCurrency((short)4,"Test Currency","@",
				MifosCurrency.FLOOR_MODE,ROUNDING_AMOUNT_0_5, IS_DEFAULT_CURRENCY,
				DIGITS_AFTER_DECIMAL_1,"TST");
		
		assertEquals(new Money(ceilingCurrency2, "100.1"),new Money(ceilingCurrency2, "100.1"));
		assertEquals(new Money(ceilingCurrency2, "100.6"),new Money(ceilingCurrency2, "100.6"));
		assertEquals(new Money(ceilingCurrency2, "10.6"), new Money(ceilingCurrency2, "106").multiply(0.1));
		assertEquals(new Money(ceilingCurrency2, "10.1"), new Money(ceilingCurrency2, "101").multiply(0.1));
		// note that rounding mode is applied when calling the round method
		assertEquals(new Money(ceilingCurrency2, "11.0"), Money.round(new Money(ceilingCurrency2, "10.6")));
		assertEquals(new Money(ceilingCurrency2, "10.5"), Money.round(new Money(ceilingCurrency2, "10.1")));
		// note that rounding mode is not applied to a calculation
		assertEquals(new Money(ceilingCurrency2, "0.2"), new Money(ceilingCurrency2, "1").divide(new Money(ceilingCurrency2, "5")));
		
		assertEquals(new Money(floorCurrency2, "100.1"), new Money(floorCurrency2, "100.1"));
		assertEquals(new Money(floorCurrency2, "100.6"), new Money(floorCurrency2, "100.6"));
		assertEquals(new Money(floorCurrency2, "10.6"), new Money(floorCurrency2, "106").multiply(0.1));
		assertEquals(new Money(floorCurrency2, "10.1"), new Money(floorCurrency2, "101").multiply(0.1));
		// note that rounding mode is applied when calling the round method
		assertEquals(new Money(floorCurrency2, "10.5"), Money.round(new Money(floorCurrency2, "10.6")));
		assertEquals(new Money(floorCurrency2, "10.0"), Money.round(new Money(floorCurrency2, "10.1")));
		// note that rounding mode is not applied to a calculation
		assertEquals(new Money(floorCurrency2, "0.2"), new Money(floorCurrency2, "1").divide(new Money(floorCurrency2, "5")));
		
	}

	/*
	 * This test is being used to understand the behavior of the rounding amount,
	 * rounding mode, and digits after decimal.
	 */
	@Test @Ignore
	public void testRoundingModesForNewMoney() {
		Short TEST_ID = 1;
		float ROUNDING_AMOUNT_1_0 = (float)1.0;
		float ROUNDING_AMOUNT_0_5 = (float)0.5;
		Short IS_DEFAULT_CURRENCY = 0;
		Short DIGITS_AFTER_DECIMAL_0 = 0;
		Short DIGITS_AFTER_DECIMAL_1 = 1;
		
		MifosCurrency ceilingCurrency = new MifosCurrency((short)1,"Test Currency","@",
				MifosCurrency.CEILING_MODE,ROUNDING_AMOUNT_1_0, IS_DEFAULT_CURRENCY,
				DIGITS_AFTER_DECIMAL_0,"TST");
		MifosCurrency floorCurrency = new MifosCurrency((short)2,"Test Currency","@",
				MifosCurrency.FLOOR_MODE,ROUNDING_AMOUNT_1_0, IS_DEFAULT_CURRENCY,
				DIGITS_AFTER_DECIMAL_0,"TST");
		
		// note that digits after decimal is enforced on creation of Money but
		// rounding mode is not
		assertFalse(new Money(ceilingCurrency, "100").equals(new Money(ceilingCurrency, "100.1")));
		assertFalse(new Money(ceilingCurrency, "101").equals(new Money(ceilingCurrency, "100.5")));
		assertEquals(new Money(ceilingCurrency, "11"), new Money(ceilingCurrency, "105").multiply(0.1));
		// note that rounding mode is not applied to a calculation
		assertEquals(new Money(ceilingCurrency, "10"), new Money(ceilingCurrency, "101").multiply(0.1));
		
		
		// note that digits after decimal is enforced on creation of Money but
		// rounding mode is not
		assertEquals(new Money(floorCurrency, "100"), new Money(floorCurrency, "100.1"));
		assertEquals(new Money(floorCurrency, "101"), new Money(floorCurrency, "100.5"));
		// note that rounding mode is not applied to a calculation
		assertEquals(new Money(floorCurrency, "11"), new Money(floorCurrency, "105").multiply(0.1));
		assertEquals(new Money(floorCurrency, "10"), new Money(floorCurrency, "101").multiply(0.1));
		
		MifosCurrency ceilingCurrency2 = new MifosCurrency((short)3,"Test Currency","@",
				MifosCurrency.CEILING_MODE,ROUNDING_AMOUNT_0_5, IS_DEFAULT_CURRENCY,
				DIGITS_AFTER_DECIMAL_1,"TST");
		MifosCurrency floorCurrency2 = new MifosCurrency((short)4,"Test Currency","@",
				MifosCurrency.FLOOR_MODE,ROUNDING_AMOUNT_0_5, IS_DEFAULT_CURRENCY,
				DIGITS_AFTER_DECIMAL_1,"TST");
		
		assertEquals(new Money(ceilingCurrency2, "100.1"),new Money(ceilingCurrency2, "100.1"));
		assertEquals(new Money(ceilingCurrency2, "100.6"),new Money(ceilingCurrency2, "100.6"));
		assertEquals(new Money(ceilingCurrency2, "10.6"), new Money(ceilingCurrency2, "106").multiply(0.1));
		assertEquals(new Money(ceilingCurrency2, "10.1"), new Money(ceilingCurrency2, "101").multiply(0.1));
		// note that rounding mode is applied when calling the round method
		assertEquals(new Money(ceilingCurrency2, "11.0"), Money.round(new Money(ceilingCurrency2, "10.6")));
		assertEquals(new Money(ceilingCurrency2, "10.5"), Money.round(new Money(ceilingCurrency2, "10.1")));
		// note that rounding mode is not applied to a calculation
		assertEquals(new Money(ceilingCurrency2, "0.2"), new Money(ceilingCurrency2, "1").divide(new Money(ceilingCurrency2, "5")));
		
		assertEquals(new Money(floorCurrency2, "100.1"), new Money(floorCurrency2, "100.1"));
		assertEquals(new Money(floorCurrency2, "100.6"), new Money(floorCurrency2, "100.6"));
		assertEquals(new Money(floorCurrency2, "10.6"), new Money(floorCurrency2, "106").multiply(0.1));
		assertEquals(new Money(floorCurrency2, "10.1"), new Money(floorCurrency2, "101").multiply(0.1));
		// note that rounding mode is applied when calling the round method
		assertEquals(new Money(floorCurrency2, "10.5"), Money.round(new Money(floorCurrency2, "10.6")));
		assertEquals(new Money(floorCurrency2, "10.0"), Money.round(new Money(floorCurrency2, "10.1")));
		// note that rounding mode is not applied to a calculation
		assertEquals(new Money(floorCurrency2, "0.2"), new Money(floorCurrency2, "1").divide(new Money(floorCurrency2, "5")));
		
	}
	
}
