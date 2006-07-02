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

import java.math.BigDecimal;

import junit.framework.TestCase;

/**
 * This class is used to test Money class.
 */
public class MoneyTest extends TestCase {
	
	
	public MoneyTest(){
		
	}
	private Money createMoneyWithMFICurrency(String amnt){
		return new Money(TestObjectFactory.getMFICurrency(),amnt);
	}
	
	private Money createMoneyObj(Short currencyId,String amnt){
		return new Money(TestObjectFactory.getCurrency(currencyId),amnt);
	}
	
	public void testAdd(){
		Money money = createMoneyWithMFICurrency("100.0");
		Money addendend = createMoneyWithMFICurrency("200.0");
		assertEquals("testing Add, should succeed", createMoneyWithMFICurrency("300.0"),money.add(addendend));
		
	}
	
	public void testAddMultipleDecimalAmounts(){
		Money money = createMoneyWithMFICurrency("0.1");
		Money money1 = money.add(createMoneyWithMFICurrency("0.1"));
		Money money2 = money1.add(createMoneyWithMFICurrency("0.1"));
		Money money3 = money2.add(createMoneyWithMFICurrency("0.1"));
		Money money4 = money3.add(createMoneyWithMFICurrency("0.1"));
		assertEquals("testing addMultipleDecimalAmounts, should succeed", createMoneyWithMFICurrency("0.5"),money4);
		
	}
	
	public void testAddWithDiffCurrencies(){
		Money money = createMoneyWithMFICurrency("100.0");
		try{
			Money addendendWithDiffCurrecny= createMoneyObj(Short.valueOf("2"),"200");
			money.add(addendendWithDiffCurrecny);
		}catch(Exception e){
			assertEquals("testing Add with different currencies should throw an exception.",e.getClass(), IllegalArgumentException.class);
		}
	}
	
	
	
	public void testSubtract(){
		Money subtrahend = createMoneyWithMFICurrency("100.0");
		Money money = createMoneyWithMFICurrency("200.0");
		assertEquals("testing subtract, should succeed",createMoneyWithMFICurrency("100.0"),money.subtract(subtrahend));
	
	}
	
	public void testSubtractWithDiffCurrencies(){
		Money money = createMoneyWithMFICurrency("100.0");
		try{
			Money subtrahendWithDiffCurrecny= createMoneyObj(Short.valueOf("2"),"100");
			money.subtract(subtrahendWithDiffCurrecny);
		}catch(Exception e){
			assertEquals("testing subtract with different currencies should throw an exception.",e.getClass(), IllegalArgumentException.class);
		}
	}
	
	public void testMultiply(){
		Money multiplicand = createMoneyWithMFICurrency("10.0");
		Money money = createMoneyWithMFICurrency("20.0");
		assertEquals("testing multiply, should succeed",createMoneyWithMFICurrency("200.0"),money.multiply(multiplicand));
	
	}
	
	public void testFactorMultiply(){
		Money money = createMoneyWithMFICurrency("100.0");
		Double factor = new Double(1+(24/100.0));
		assertEquals("testing multiply with a factor, should succeed",createMoneyWithMFICurrency("124.0"),money.multiply(factor));
	
	}
	public void testMultiplyWithDiffCurrencies(){
		Money money = createMoneyWithMFICurrency("20.0");
		try{
			Money multiplicandWithDiffCurrecny= createMoneyObj(Short.valueOf("2"),"10");
			money.multiply(multiplicandWithDiffCurrecny);
		}catch(Exception e){
			assertEquals("testing multiply with different currencies should throw an exception.",e.getClass(), IllegalArgumentException.class);
		}
	}
	
	public void testDivide(){
		Money dividend = createMoneyWithMFICurrency("10.0");
		Money money = createMoneyWithMFICurrency("20.0");
		assertEquals("testing divide, should succeed",createMoneyWithMFICurrency("2.0"),money.divide(dividend));
	
	}
	
	public void testDivideWithDiffCurrencies(){
		Money money = createMoneyWithMFICurrency("20.0");
		try{
			Money dividendWithDiffCurrecny= createMoneyObj(Short.valueOf("2"),"10");
			money.divide(dividendWithDiffCurrecny);
		}catch(Exception e){
			assertEquals("testing divide with different currencies should throw an exception.",e.getClass(), IllegalArgumentException.class);
		}
	}
	
	public void testNegate(){
		
		Money money = createMoneyWithMFICurrency("20.0");
		assertEquals(createMoneyWithMFICurrency("-20.0"),money.negate());
	}
	
	public void testRoundUp(){
		
		Money money = createMoneyWithMFICurrency("142.34");
		assertEquals(createMoneyWithMFICurrency("143.00"),Money.round(money));
		
	}
	
	public void testRoundDown(){
		Money money = createMoneyObj(Short.valueOf("3"),"142.34");
		assertEquals(createMoneyObj(Short.valueOf("3"),"142.00"),Money.round(money));
	}
	
	public void testHashCode(){
		Money money = createMoneyWithMFICurrency("142.34");
		BigDecimal amnt = new BigDecimal(142.34);
		assertEquals((money.getCurrency().getCurrencyId()*100 + amnt.intValue()), money.hashCode());
		
		
	}
	
	public void testHashCodeForNullAmnt(){
		Money money = new Money(TestObjectFactory.getMFICurrency(),"");
		assertEquals(0, money.hashCode());
	}
	
	public void testSetScale(){
		Money money=null;
		money = createMoneyWithMFICurrency("142.344");
		assertEquals(142.3,money.getAmountDoubleValue());
		money = createMoneyWithMFICurrency("142.356");
		assertEquals(142.4,money.getAmountDoubleValue());
	}
	
	public void testToString() {
		Money money = createMoneyWithMFICurrency("4456456456.6");
		assertEquals("The toString of money returns : ","4456456456.6",money.toString());
	}
	
	
}
