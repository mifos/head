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
 
package org.mifos.framework.struts.tags;

import static org.junit.Assert.assertEquals;
import junit.framework.JUnit4TestAdapter;

import org.apache.struts.util.MessageResources;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.TestCaseInitializer;

public class MifosPropertyMessageResourcesTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TestCaseInitializer initializer = new TestCaseInitializer();
		initializer.initialize();
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetMessageLocaleString() {
		MifosPropertyMessageResourcesFactory factory = new MifosPropertyMessageResourcesFactory();
		MessageResources resource = factory.createResources(FilePaths.ACCOUNTS_UI_RESOURCE_PROPERTYFILE);
		
		// get a simple resource bundle entry
		assertEquals("Admin", resource.getMessage(TestUtils.ukLocale(), "Account.Admin"));
		// get a LookupValue 
		assertEquals("Branch Office", resource.getMessage(TestUtils.ukLocale(), ConfigurationConstants.BRANCHOFFICE));
	}

}
