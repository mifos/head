package org.mifos.framework.components.fieldConfiguration;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.framework.components.fieldConfiguration.business.TestFieldConfigurationEntity;
import org.mifos.framework.components.fieldConfiguration.persistence.TestFieldConfigurationPersistence;
import org.mifos.framework.components.fieldConfiguration.util.helpers.TestFieldConfigImplementer;
import org.mifos.framework.components.fieldConfiguration.util.helpers.TestFieldConfigurationHelper;

public class FieldConfigurationTestSuite extends TestSuite {

	public static Test suite()throws Exception
	{
		TestSuite testSuite = new FieldConfigurationTestSuite();
		testSuite.addTestSuite(TestFieldConfigurationEntity.class);
		testSuite.addTestSuite(TestFieldConfigurationPersistence.class);
		testSuite.addTestSuite(TestFieldConfigImplementer.class);
		testSuite.addTestSuite(TestFieldConfigurationHelper.class);
		return testSuite;
	}
}
