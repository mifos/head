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
 
package org.mifos.application.configuration;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.mifos.application.configuration.struts.action.CustomFieldsActionTest;
import org.mifos.application.configuration.struts.action.HiddenMandatoryConfigurationActionTest;
import org.mifos.application.configuration.struts.action.LabelConfigurationActionTest;
import org.mifos.application.configuration.struts.action.LookupOptionsActionTest;
import org.mifos.application.configuration.struts.tag.CustomFieldCategoryListTagTest;
import org.mifos.application.configuration.struts.tag.CustomFieldsListTagTest;
import org.mifos.framework.components.configuration.persistence.ConfigurationPersistenceTest;

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
		testSuite.addTestSuite(CustomFieldsListTagTest.class);
		testSuite.addTestSuite(ConfigurationPersistenceTest.class);
		return testSuite;
		
	}

}
