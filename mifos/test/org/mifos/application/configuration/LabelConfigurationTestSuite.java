package org.mifos.application.configuration;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.framework.MifosTestSuite;

public class LabelConfigurationTestSuite extends MifosTestSuite {
	
	public static void main (String[] args)
	{
		Test testSuite = suite();
		TestRunner.run (testSuite);
	}
	

	public static Test suite()
	{
		TestSuite testSuite = new TestSuite();
		testSuite.addTestSuite(TestConfigurationPersistence.class);
		testSuite.addTestSuite(TestConfigurationPersistenceService.class);
		testSuite.addTestSuite(TestConfiguration.class);

		return testSuite;
		
	}

}
