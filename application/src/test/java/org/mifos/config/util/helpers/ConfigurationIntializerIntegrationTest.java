/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.config.util.helpers;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mifos.config.business.SystemConfiguration;
import org.mifos.config.cache.OfficeCache;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.StartUpException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.ExceptionConstants;

public class ConfigurationIntializerIntegrationTest extends MifosIntegrationTestCase {

    private ConfigurationInitializer configInitializer;

    @Before
    public void setUp() throws Exception {
        configInitializer = new ConfigurationInitializer();
    }

    @After
    public void tearDown() throws Exception {
        StaticHibernateUtil.flushSession();
    }

    @Test
    public void testCreateSystemCache() throws Exception {
        SystemConfiguration configuration = configInitializer.createSystemConfiguration();
        Assert.assertNotNull(configuration);
        Assert.assertNotNull(configuration.getCurrency());
        Assert.assertNotNull(configuration.getMifosTimeZone());
    }

    @Test
    public void testCreateOfficeCache() throws Exception {
        configInitializer.initialize();
        OfficeCache officeCache = configInitializer.createOfficeCache();
        Assert.assertNotNull(officeCache);
    }

    @Test
    @Ignore
    public void testStartUpException() throws Exception {

        try {
            configInitializer.initialize();
            Assert.fail();
        } catch (StartUpException sue) {
           Assert.assertEquals(ExceptionConstants.STARTUP_EXCEPTION, sue.getKey());
        } finally {
            StaticHibernateUtil.flushSession();
        }
    }
}
