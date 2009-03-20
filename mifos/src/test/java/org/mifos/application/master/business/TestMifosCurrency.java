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
 
package org.mifos.application.master.business;

import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

public class TestMifosCurrency extends MifosIntegrationTest {

	public TestMifosCurrency() throws SystemException, ApplicationException {
        super();
    }


    public void testEqualsOnCurrencyId(){
		MifosCurrency currency1 = new MifosCurrency(Short.valueOf("1") ,"Dollar","$",Short.valueOf("1"),Float.valueOf("1"),Short.valueOf("1"),Short.valueOf("3"), "USD");
		MifosCurrency currency2 = new MifosCurrency(Short.valueOf("1") ,"Dollar","$",Short.valueOf("1"),Float.valueOf("1"),Short.valueOf("1"),Short.valueOf("3"), "USD");
		assertTrue(currency1.equals(currency2)); 
	}
	
	
	public void testEqualsFailureOnCurrencyId(){
		MifosCurrency currency1 = new MifosCurrency(Short.valueOf("1") ,"Dollar","$",Short.valueOf("1"),Float.valueOf("1"),Short.valueOf("1"),Short.valueOf("3"),"USD");
		MifosCurrency currency2 = new MifosCurrency(Short.valueOf("2") ,"Rupees","Rs",Short.valueOf("1"),Float.valueOf("1"),Short.valueOf("1"),Short.valueOf("3"),"USD");
		assertFalse(currency1.equals(currency2)); 
	}

	

}
