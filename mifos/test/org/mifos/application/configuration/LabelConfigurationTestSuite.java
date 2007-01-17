package org.mifos.application.configuration;

import org.mifos.application.configuration.struts.action.LabelConfigurationActionTest;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class LabelConfigurationTestSuite extends TestSuite {
	
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
		testSuite.addTestSuite(LabelConfigurationActionTest.class);
		return testSuite;
		
	}

}
