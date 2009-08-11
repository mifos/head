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

package org.mifos.test.acceptance.framework.testhelpers;

import com.thoughtworks.selenium.Selenium;

public class CustomPropertiesHelper {
    private static final String UPDATE_PAGE = "customPropertiesUpdate.ftl";
    
    private final Selenium selenium;
    
    public CustomPropertiesHelper(Selenium selenium) {
        this.selenium = selenium;
    }
    
    /**
     * Sets the language and country code.
     * See application\src\main\resources\org\mifos\config\resources\applicationConfiguration.default.properties
     * for more details about these two settings.
     * @param languageCode The language code.
     * @param countryCode The country code.
     */
    public void setLocale(String languageCode, String countryCode) {
        selenium.open(UPDATE_PAGE + "?Localization.LanguageCode="+languageCode+"&Localization.CountryCode=" + countryCode);
    }
    
    /**
     * Sets the number of digits after the decimal sign to <tt>digits</tt>.
     * @param digits Number of digits after the decimal.
     */
    public void setDigitsAfterDecimal(int digits) {
        selenium.open(UPDATE_PAGE + "?AccountingRules.DigitsAfterDecimal=" + digits);
    }
}
