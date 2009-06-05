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

import java.util.NoSuchElementException;

import junit.framework.JUnit4TestAdapter;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.util.helpers.FilePaths;

public class ConfigurationManagerTest {

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ConfigurationManagerTest.class);
    }

    Configuration configuration;
    private static final String badKey = "Bad Key";

    @BeforeClass
    public static void init() throws Exception {
        MifosLogManager.configure(FilePaths.LOG_CONFIGURATION_FILE);
    }

    @Before
    public void before() {
        configuration = ConfigurationManager.getInstance().getConfiguration();
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetUndefinedProperty() {
        configuration.getShort(badKey);
    }
}
