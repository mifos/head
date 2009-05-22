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
 
package org.mifos.framework.components.fieldConfiguration;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.framework.components.fieldConfiguration.business.AddFieldTest;
import org.mifos.framework.components.fieldConfiguration.business.FieldConfigurationEntityIntegrationTest;
import org.mifos.framework.components.fieldConfiguration.persistence.FieldConfigurationPersistenceIntegrationTest;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigIntegrationTest;
import org.mifos.framework.components.fieldConfiguration.util.helpers.FieldConfigurationHelperIntegrationTest;

public class FieldConfigurationTestSuite extends TestSuite {

	public static Test suite()throws Exception
	{
		TestSuite testSuite = new FieldConfigurationTestSuite();
		testSuite.addTestSuite(FieldConfigurationEntityIntegrationTest.class);
		testSuite.addTestSuite(FieldConfigurationPersistenceIntegrationTest.class);
		testSuite.addTestSuite(FieldConfigIntegrationTest.class);
		testSuite.addTestSuite(FieldConfigurationHelperIntegrationTest.class);
		testSuite.addTest(AddFieldTest.suite());
		return testSuite;
	}
}
