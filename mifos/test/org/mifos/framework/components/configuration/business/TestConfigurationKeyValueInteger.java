package org.mifos.framework.components.configuration.business;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.configuration.persistence.ConfigurationPersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestConfigurationKeyValueInteger extends MifosTestCase {
	private ConfigurationPersistence configurationPersistence = new ConfigurationPersistence();
	private final int TEST_VALUE = 100;
	private final int TEST_VALUE_2 = 200;
	private final String TEST_KEY = "test.key";
	private final String UNUSED_KEY = "unused.key";


	public void testGetConfigurationKeyValueInteger() throws Exception {
		configurationPersistence.addConfigurationKeyValueInteger(TEST_KEY, TEST_VALUE);
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		
		ConfigurationKeyValueInteger  keyValue = configurationPersistence.getConfigurationKeyValueInteger(TEST_KEY);
		assertEquals(keyValue.getKey(), TEST_KEY);
		assertEquals(keyValue.getValue(), TEST_VALUE);
		assertEquals(TEST_VALUE, configurationPersistence.getConfigurationValueInteger(TEST_KEY));
		
		configurationPersistence.delete(keyValue);
		HibernateUtil.commitTransaction();
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
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();

		try {
			configurationPersistence.addConfigurationKeyValueInteger(TEST_KEY, TEST_VALUE_2);			
			fail("Expected PersistenceException for violating uniqueness constraint on the key.");
		} catch (PersistenceException e) {
			assertTrue(e.getMessage().contains("could not insert"));
			HibernateUtil.rollbackTransaction();
			HibernateUtil.closeSession();
		}

		configurationPersistence.deleteConfigurationKeyValueInteger(TEST_KEY);
		HibernateUtil.commitTransaction();
		
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
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();

		configurationPersistence.updateConfigurationKeyValueInteger(TEST_KEY, TEST_VALUE_2);
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();

		ConfigurationKeyValueInteger  keyValue = configurationPersistence.getConfigurationKeyValueInteger(TEST_KEY);
		assertEquals(keyValue.getKey(), TEST_KEY);
		assertEquals(keyValue.getValue(), TEST_VALUE_2);

		configurationPersistence.deleteConfigurationKeyValueInteger(TEST_KEY);
		HibernateUtil.commitTransaction();
		
	}
}
