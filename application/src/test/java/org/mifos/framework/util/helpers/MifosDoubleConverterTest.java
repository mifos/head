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

import java.util.Locale;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.mifos.config.Localization;
import org.mifos.framework.util.LocalizationConverter;

public class MifosDoubleConverterTest extends TestCase {

    private MifosDoubleConverter mifosDoubleConverter = null;

    public void testConvert() {
        mifosDoubleConverter = new MifosDoubleConverter();
        Double test = new Double(2.0);
        Locale locale = Localization.getInstance().getMainLocale();
        LocalizationConverter converter = LocalizationConverter.getInstance();
        if (locale.getCountry().equalsIgnoreCase("GB") && locale.getLanguage().equalsIgnoreCase("EN"))
           Assert.assertEquals(test, mifosDoubleConverter.convert(String.class, "2.0"));
        converter.setCurrentLocale(new Locale("IS", "is"));
       Assert.assertEquals(test, mifosDoubleConverter.convert(String.class, "2,0"));
        converter.setCurrentLocale(locale);
    }

}
