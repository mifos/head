package org.mifos.application.configuration;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.application.configuration.struts.action.HiddenMandatoryConfigurationActionTest;
import org.mifos.application.configuration.struts.action.LabelConfigurationActionTest;

public class LabelConfigurationTestSuite extends TestSuite {
	
	public static void main (String[] args)
	{
		Test testSuite = suite();
		TestRunner.run (testSuite);
	}
	

	public static Test suite()
	{
		/* TODO: should be "new LabelConfigurationTestSuite();"
		 * so that it is displayed correctly in eclipse (and maybe
		 * other test runners).
		 */
		TestSuite testSuite = new TestSuite();
		testSuite.addTestSuite(TestConfigurationPersistence.class);
		testSuite.addTestSuite(TestConfigurationPersistenceService.class);
		testSuite.addTestSuite(TestConfiguration.class);
		testSuite.addTestSuite(LabelConfigurationActionTest.class);
		testSuite.addTestSuite(HiddenMandatoryConfigurationActionTest.class);
		return testSuite;
		
	}

}
