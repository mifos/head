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

package org.mifos.config;

import java.util.ArrayList;
import java.util.Locale;

import junit.framework.Assert;

import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test(groups={"integration", "configTestSuite"})
public class LocalizationIntegrationTest extends MifosIntegrationTestCase {

    public LocalizationIntegrationTest() throws Exception {
        super();
    }

    @BeforeMethod
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @AfterMethod
    @Override
    protected void tearDown() throws Exception {
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    private void restoreConfigSetup(Localization localization, ConfigLocale savedConfigLocale) {
        localization.setConfigLocale(savedConfigLocale);
    }

    private void restoreConfigSetupToConfigFile(Localization localization, ConfigLocale savedConfigLocale) {
        localization.setCountryCodeLanguageCodeToConfigFile(savedConfigLocale);
    }

    @Test
    public void testGetCountryCodeAndGetLanguageCode() {
        Localization localization = Localization.getInstance();
        ConfigLocale savedConfigLocale = localization.getConfigLocale();
        ConfigLocale newConfigLocale = new ConfigLocale();
        String countryCode = "GB";
        String languageCode = "en";
        newConfigLocale.setCountryCode(countryCode);
        newConfigLocale.setLanguageCode(languageCode);
        localization.setConfigLocale(newConfigLocale);
        localization.refresh();
        // configMgr.setProperty(LocalizationCountryCode, countryCode);
       Assert.assertEquals(countryCode, localization.getCountryCode());
       Assert.assertEquals(languageCode, localization.getLanguageCode());
        // clear the country code property from the config file
        localization.clearCountryCodeLanguageCodeFromConfigFile();
        // should throw exception
        try {
            localization.refresh();
        } catch (RuntimeException e) {
           Assert.assertEquals(e.getMessage(), "The country code is not defined in the config file.");

        }
        restoreConfigSetupToConfigFile(localization, savedConfigLocale);

    }

    @Test
    public void testGetLanguageNameCountryName() {

        String languageName = "English";
        String languageCode = "EN";
        String countryName = "United Kingdom";
        String countryCode = "GB";
        Localization localization = Localization.getInstance();
        ConfigLocale savedConfigLocale = localization.getConfigLocale();
        ConfigLocale newConfigLocale = new ConfigLocale();
        newConfigLocale.setCountryCode(countryCode);
        newConfigLocale.setLanguageCode(languageCode);
        localization.setConfigLocale(newConfigLocale);
        localization.refresh();
        if (Locale.getDefault().equals(Locale.UK)) {
           Assert.assertEquals(languageName, localization.getLanguageName());
           Assert.assertEquals(countryName, localization.getCountryName());
        }
        restoreConfigSetup(localization, savedConfigLocale);
    }

    private boolean findLocaleId(ArrayList<Short> locales, short localeId) {
        for (int i = 0; i < locales.size(); i++)
            if (locales.get(i).shortValue() == localeId)
                return true;
        return false;
    }

    @Test
    public void testGetSupportedLocaleIds() {
        short localeId = 1;
        String countryCode = "GB";
        String languageCode = "EN";
        Localization localization = Localization.getInstance();
        ConfigLocale savedConfigLocale = localization.getConfigLocale();
        ConfigLocale newConfigLocale = new ConfigLocale();
        newConfigLocale.setCountryCode(countryCode);
        newConfigLocale.setLanguageCode(languageCode);
        localization.setConfigLocale(newConfigLocale);
        ArrayList<Short> locales = localization.getSupportedLocaleIds();
       Assert.assertTrue(findLocaleId(locales, localeId));
        restoreConfigSetup(localization, savedConfigLocale);

    }

}
