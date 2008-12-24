/**
 
 * StringToMoneyConverterTest.java    version: 1.0
 
 
 
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

import org.apache.commons.beanutils.Converter;
import org.mifos.framework.MifosTestCase;

/**
 * This class is used to test StringToMoneyConverter.
 */
public class StringToMoneyConverterTest extends MifosTestCase {
	
	public StringToMoneyConverterTest(){
		
	}
	
	/**
	 *This method creates a money object with MFI currency because 
	 *as of now the converter creates 
	 *a new Money object with the currency set to MFI currency.
	 */
	public void testConvert(){
		Converter stringToMoney = new StringToMoneyConverter();
		Money money = new Money(TestObjectFactory.getMFICurrency(),"142.34");
		assertEquals("testing StringToMoneyConverter should have returned a Money object.",money, (Money)stringToMoney.convert(Money.class, "142.34"));
	}
	
	/**
	 * Testing converter by passing blank string.
	 * 
	 *This method creates a money object with MFI currency because as of now the converter creates 
	 *a new Money object with the currency set to MFI currency.
	 */
	public void testConvertWithEmptyString(){
		
		Converter stringToMoney = new StringToMoneyConverter();
		Money money = new Money(TestObjectFactory.getMFICurrency(),"0");
		assertEquals("testing StringToMoneyConverter should have returned a Money object with amount set to zero.",money, (Money)stringToMoney.convert(Money.class, ""));
		
	}
	

}
