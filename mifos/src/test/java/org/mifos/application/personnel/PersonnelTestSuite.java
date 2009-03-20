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
 
package org.mifos.application.personnel;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.application.personnel.business.TestPersonnelBO;
import org.mifos.application.personnel.business.TestPersonnelStatusEntity;
import org.mifos.application.personnel.business.service.PersonnelBusinessServiceTest;
import org.mifos.application.personnel.persistence.TestPersonnelPersistence;
import org.mifos.application.personnel.struts.action.PersonnelSettingsActionTest;
import org.mifos.application.personnel.struts.action.TestPersonAction;
import org.mifos.application.personnel.struts.action.TestPersonnelNoteAction;

public class PersonnelTestSuite extends TestSuite {
	public PersonnelTestSuite() {
		super();
	}

	public static Test suite() throws Exception {
		TestSuite testSuite = new PersonnelTestSuite();
		testSuite.addTestSuite(TestPersonnelStatusEntity.class);
		testSuite.addTestSuite(TestPersonnelPersistence.class);
		testSuite.addTestSuite(TestPersonnelBO.class);
		testSuite.addTestSuite(TestPersonAction.class);
		testSuite.addTestSuite(TestPersonnelNoteAction.class);
		testSuite.addTestSuite(PersonnelSettingsActionTest.class);
		testSuite.addTestSuite(PersonnelBusinessServiceTest.class);
		return testSuite;
	}

}
