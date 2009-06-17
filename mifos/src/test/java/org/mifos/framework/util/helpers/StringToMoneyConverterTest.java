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

import junit.framework.TestCase;

import org.apache.commons.beanutils.Converter;
import org.mifos.application.master.business.MifosCurrency;

/**
 * This class is used to test StringToMoneyConverter.
 */
public class StringToMoneyConverterTest extends TestCase {

    private MifosCurrency oldMifosCurrency = null;
    
    public void setUp() {
        oldMifosCurrency = Money.getDefaultCurrency();
        Short zero = Short.valueOf("0");
        Money.setDefaultCurrency(new MifosCurrency(zero,null,null,zero,0.0F,zero,zero,null));
    }

    public void tearDown() {
        Money.setDefaultCurrency(oldMifosCurrency);
    }
    /**
     *This method creates a money object with MFI currency because as of now
     * the converter creates a new Money object with the currency set to MFI
     * currency.
     */
    public void testConvert() {
        Converter stringToMoney = new StringToMoneyConverter();
        Money money = new Money("142.34");
        assertEquals("testing StringToMoneyConverter should have returned a Money object.", money,
                (Money) stringToMoney.convert(Money.class, "142.34"));
    }

    /**
     * Testing converter by passing blank string.
     * 
     *This method creates a money object with MFI currency because as of now
     * the converter creates a new Money object with the currency set to MFI
     * currency.
     */
    public void testConvertWithEmptyString() {

        Converter stringToMoney = new StringToMoneyConverter();
        Money money = new Money("0");
        assertEquals("testing StringToMoneyConverter should have returned a Money object with amount set to zero.",
                money, (Money) stringToMoney.convert(Money.class, ""));

    }

}
