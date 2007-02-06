package org.mifos.framework.components.configuration;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.master.business.TestMifosCurrency;
import org.mifos.framework.components.configuration.business.TestConfiguration;
import org.mifos.framework.components.configuration.cache.TestKey;
import org.mifos.framework.components.configuration.persistence.TestConfigurationPersistence;
import org.mifos.framework.components.configuration.persistence.service.TestConfigurationPersistenceService;
import org.mifos.framework.components.configuration.util.helpers.TestConfigurationIntializer;
import org.mifos.framework.components.configuration.util.helpers.TestSystemConfig;

public class ConfigurationTestSuite extends TestSuite {

	public ConfigurationTestSuite() {
		super();
	}

	public static Test suite()throws Exception
	{
		TestSuite testSuite = new ConfigurationTestSuite();
		testSuite.addTestSuite(TestConfigurationPersistence.class);
		testSuite.addTestSuite(TestConfigurationPersistenceService.class);
		testSuite.addTestSuite(TestSystemConfig.class);
		testSuite.addTestSuite(TestMifosCurrency.class);
		testSuite.addTestSuite(TestConfigurationIntializer.class);
		testSuite.addTestSuite(TestConfiguration.class);
		testSuite.addTestSuite(TestKey.class);
		return testSuite;
		
	}
}
