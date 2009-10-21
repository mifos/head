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

package org.mifos.framework.util;

import java.io.IOException;
import java.util.Properties;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.service.test.TestMode;
import org.testng.annotations.Test;

@Test(groups = { "unit", "fastTestsSuite" }, dependsOnGroups = { "productMixTestSuite" })
public class TestingServiceTest extends TestCase {
    static StandardTestingService standardTestingService = null;
    static TestMode savedTestMode = null;

    @Override
    public void setUp() {
        MifosLogManager.configureLogging();
        standardTestingService = new StandardTestingService();
        savedTestMode = standardTestingService.getTestMode();
        standardTestingService.setTestMode(TestMode.ACCEPTANCE);
    }

    @Override
    public void tearDown() {
        standardTestingService.setTestMode(savedTestMode);
    }

    public void testGetDatabaseConnectionSettings() throws IOException {
        Properties p = standardTestingService.getDatabaseConnectionSettings();
        Assert.assertNotNull(p.getProperty("hibernate.connection.url"));
    }

    public void testGetDefaultSettingsFilename() {

        String actual = standardTestingService.getDefaultSettingsFilename(standardTestingService.getTestMode());
        Assert.assertEquals("acceptanceDatabase.properties", actual);
    }

    public void testTranslateToHibernate() {
        Properties p = new Properties();
        p.setProperty("integration.database", "fozzy");
        Properties q = standardTestingService.translateToHibernate(p, TestMode.INTEGRATION);
        String url = q.getProperty("hibernate.connection.url");
        Assert.assertNotNull(url);
        Assert.assertTrue(url.contains("fozzy"));
    }

}
