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
 
package org.mifos.framework.components.configuration.business;

import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.components.configuration.persistence.ConfigurationPersistence;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestConfigurationKeyValueInteger extends MifosIntegrationTest {
	public TestConfigurationKeyValueInteger() throws SystemException, ApplicationException {
        super();
    }

    private ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
	private final int TEST_VALUE = 100;
	private final int TEST_VALUE_2 = 200;
	private final String TEST_KEY = "test.key";
	private final String UNUSED_KEY = "unused.key";


	public void testGetConfigurationKeyValueInteger() throws Exception {
		configurationPersistence.addConfigurationKeyValueInteger(TEST_KEY, TEST_VALUE);
		StaticHibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		
		ConfigurationKeyValueInteger  keyValue = configurationPersistence.getConfigurationKeyValueInteger(TEST_KEY);
		assertEquals(keyValue.getKey(), TEST_KEY);
		assertEquals(keyValue.getValue(), TEST_VALUE);
		assertEquals(TEST_VALUE, configurationPersistence.getConfigurationValueInteger(TEST_KEY));
		
		configurationPersistence.delete(keyValue);
		StaticHibernateUtil.commitTransaction();
	}

	public void testUnusedConfigurationKeyValueInteger() throws Exception {
		ConfigurationKeyValueInteger  keyValue = configurationPersistence.getConfigurationKeyValueInteger(UNUSED_KEY);
		assertNull(keyValue);
		try {
			configurationPersistence.getConfigurationValueInteger(UNUSED_KEY);
			fail("Expected runtime exeption for key lookup failure");
		} catch (RuntimeException e) {
			assertTrue(e.getMessage().contains("parameter not found for key"));
		}
	}

	public void testAddDupliateKey() throws Exception {
		configurationPersistence.addConfigurationKeyValueInteger(TEST_KEY, TEST_VALUE);
		StaticHibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();

		try {
			configurationPersistence.addConfigurationKeyValueInteger(TEST_KEY, TEST_VALUE_2);			
			fail("Expected PersistenceException for violating uniqueness constraint on the key.");
		} catch (PersistenceException e) {
			assertTrue(e.getMessage().contains("could not insert"));
			StaticHibernateUtil.rollbackTransaction();
			StaticHibernateUtil.closeSession();
		}

		configurationPersistence.deleteConfigurationKeyValueInteger(TEST_KEY);
		StaticHibernateUtil.commitTransaction();
		
	}
	
	public void testIllegalArgument() throws Exception {
		try {
			new ConfigurationKeyValueInteger(null, 0);
			fail("A null key is not allowed for ConfiguruationKeyValueInteger");
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("null"));
		}
	}
	
	public void testUpdateConfigurationKeyValueInteger() throws Exception {
		configurationPersistence.addConfigurationKeyValueInteger(TEST_KEY, TEST_VALUE);
		StaticHibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();

		configurationPersistence.updateConfigurationKeyValueInteger(TEST_KEY, TEST_VALUE_2);
		StaticHibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();

		ConfigurationKeyValueInteger  keyValue = configurationPersistence.getConfigurationKeyValueInteger(TEST_KEY);
		assertEquals(keyValue.getKey(), TEST_KEY);
		assertEquals(keyValue.getValue(), TEST_VALUE_2);

		configurationPersistence.deleteConfigurationKeyValueInteger(TEST_KEY);
		StaticHibernateUtil.commitTransaction();
		
	}
}
