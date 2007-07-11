package org.mifos.application.configuration;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.application.configuration.struts.action.CustomFieldsActionTest;
import org.mifos.application.configuration.struts.action.HiddenMandatoryConfigurationActionTest;
import org.mifos.application.configuration.struts.action.LabelConfigurationActionTest;
import org.mifos.application.configuration.struts.action.LookupOptionsActionTest;
import org.mifos.application.configuration.struts.tag.CustomFieldCategoryListTagTest;

public class ApplicationConfigurationTestSuite extends TestSuite {
	
	public static void main (String[] args)
	{
		Test testSuite = suite();
		TestRunner.run (testSuite);
	}
	

	public static Test suite()
	{
		TestSuite testSuite = new ApplicationConfigurationTestSuite();
		testSuite.addTestSuite(TestApplicationConfigurationPersistence.class);
		testSuite.addTestSuite(TestConfiguration.class);
		testSuite.addTestSuite(LabelConfigurationActionTest.class);
		testSuite.addTestSuite(HiddenMandatoryConfigurationActionTest.class);
		testSuite.addTestSuite(LookupOptionsActionTest.class);
		testSuite.addTestSuite(CustomFieldsActionTest.class);
		testSuite.addTestSuite(CustomFieldCategoryListTagTest.class);
		return testSuite;
		
	}

}
