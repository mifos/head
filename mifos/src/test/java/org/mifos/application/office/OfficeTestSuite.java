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
 
package org.mifos.application.office;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.office.business.OfficeLevelEntityTest;
import org.mifos.application.office.business.TestOfficeBO;
import org.mifos.application.office.business.TestOfficeStatusEntity;
import org.mifos.application.office.business.service.OfficeHierarchyBusinessServiceTest;
import org.mifos.application.office.business.service.TestOfficeBusinessService;
import org.mifos.application.office.persistence.OfficeHierarchyPersistenceTest;
import org.mifos.application.office.persistence.TestOfficePersistence;
import org.mifos.application.office.struts.action.OffHierarchyActionTest;
import org.mifos.application.office.struts.action.TestOfficeAction;

public class OfficeTestSuite extends TestSuite {
	public OfficeTestSuite() throws Exception {
		super();
	}

	public static Test suite() throws Exception {
		TestSuite testSuite = new OfficeTestSuite();
		testSuite.addTestSuite(TestOfficePersistence.class);
		testSuite.addTestSuite(TestOfficeAction.class);
		testSuite.addTestSuite(TestOfficeBO.class);
		testSuite.addTestSuite(TestOfficeBusinessService.class);
		testSuite.addTestSuite(OfficeHierarchyPersistenceTest.class);
		testSuite.addTestSuite(OfficeHierarchyBusinessServiceTest.class);
		testSuite.addTestSuite(OffHierarchyActionTest.class);
		testSuite.addTestSuite(TestOfficeStatusEntity.class);
		testSuite.addTestSuite(OfficeLevelEntityTest.class);
		return testSuite;
	}
}
