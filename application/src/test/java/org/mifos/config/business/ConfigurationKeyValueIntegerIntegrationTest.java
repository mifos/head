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

package org.mifos.config.business;

import junit.framework.Assert;

import org.junit.Test;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class ConfigurationKeyValueIntegerIntegrationTest extends MifosIntegrationTestCase {

    private ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
    private final int TEST_VALUE = 100;
    private final int TEST_VALUE_2 = 200;
    private final String TEST_KEY = "test.key";
    private final String UNUSED_KEY = "unused.key";

    @Test
    public void testGetConfigurationKeyValueInteger() throws Exception {
        configurationPersistence.addConfigurationKeyValueInteger(TEST_KEY, TEST_VALUE);
        StaticHibernateUtil.flushSession();

        ConfigurationKeyValue keyValue = configurationPersistence.getConfigurationKeyValue(TEST_KEY);
        Assert.assertEquals(keyValue.getKey(), TEST_KEY);
        Assert.assertEquals(Integer.parseInt(keyValue.getValue()), TEST_VALUE);
        Assert.assertEquals(TEST_VALUE, configurationPersistence.getConfigurationValueInteger(TEST_KEY));

        configurationPersistence.delete(keyValue);
        StaticHibernateUtil.flushSession();
    }

    @Test
    public void testUnusedConfigurationKeyValueInteger() throws Exception {
        ConfigurationKeyValue keyValue = configurationPersistence.getConfigurationKeyValue(UNUSED_KEY);
        Assert.assertNull(keyValue);
        try {
            configurationPersistence.getConfigurationValueInteger(UNUSED_KEY);
            Assert.fail("Expected runtime exeption for key lookup failure");
        } catch (RuntimeException e) {
            Assert.assertTrue(e.getMessage().contains("parameter not found for key"));
        }
    }

    @Test
    public void testAddDupliateKey() throws Exception {
        configurationPersistence.addConfigurationKeyValueInteger(TEST_KEY, TEST_VALUE);
        StaticHibernateUtil.flushSession();

        try {
            configurationPersistence.addConfigurationKeyValueInteger(TEST_KEY, TEST_VALUE_2);
            Assert.fail("Expected PersistenceException for violating uniqueness constraint on the key.");
        } catch (PersistenceException e) {
            Assert.assertTrue(e.getCause().getMessage().contains("could not insert"));
        }

        StaticHibernateUtil.getSessionTL().clear();
        configurationPersistence.deleteConfigurationKeyValue(TEST_KEY);
        StaticHibernateUtil.flushSession();
    }

    @Test
    public void testIllegalArgument() throws Exception {
        try {
            new ConfigurationKeyValue(null, 0);
            Assert.fail("A null key is not allowed for ConfigurationKeyValue");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("null"));
        }
    }

    @Test
    public void testUpdateConfigurationKeyValueInteger() throws Exception {
        configurationPersistence.addConfigurationKeyValueInteger(TEST_KEY, TEST_VALUE);
        StaticHibernateUtil.flushSession();

        configurationPersistence.updateConfigurationKeyValueInteger(TEST_KEY, TEST_VALUE_2);
        StaticHibernateUtil.flushSession();

        ConfigurationKeyValue keyValue = configurationPersistence.getConfigurationKeyValue(TEST_KEY);
        Assert.assertEquals(keyValue.getKey(), TEST_KEY);
        Assert.assertEquals(Integer.parseInt(keyValue.getValue()), TEST_VALUE_2);

        configurationPersistence.deleteConfigurationKeyValue(TEST_KEY);
        StaticHibernateUtil.flushSession();

    }
}
