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
import junit.framework.JUnit4TestAdapter;

import org.junit.Before;
import org.junit.Test;
import org.mifos.framework.components.logger.MifosLogManager;

public class TestingServiceTest {
    static TestingService testingService = null;

    @Before
    public void setUp() {
        MifosLogManager.configureLogging();
        testingService = new TestingService();
    }

    @Test
    public void testGetDatabaseConnectionSettings() throws IOException {
        Properties p = testingService.getDatabaseConnectionSettings();
        Assert.assertNotNull(p.getProperty("hibernate.connection.url"));
    }
    
    @Test
    public void testGetDefaultSettingsFilename()
    {
        String actual = testingService.getDefaultSettingsFilename("acceptance");
        Assert.assertEquals("acceptanceDatabase.properties", actual);
    }
    
    @Test
    public void testTranslateToHibernate() {
        Properties p = new Properties();
        p.setProperty("integration.database", "fozzy");
        Properties q = testingService.translateToHibernate(p, "integration");
        String url = q.getProperty("hibernate.connection.url");
        Assert.assertNotNull(url);
        Assert.assertTrue(url.contains("fozzy"));
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(TestingServiceTest.class);
    }
}
