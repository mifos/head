/**

 * OfficeHierarchyPersistenceTest.java    version: 1.0

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */
package org.mifos.application.office.persistence;

import java.util.List;

import org.mifos.application.office.business.OfficeLevelEntity;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class OfficeHierarchyPersistenceTest extends MifosTestCase {

	private static final int OFFICE_LEVELS = 5;

	public OfficeHierarchyPersistenceTest() throws SystemException, ApplicationException {
		super();
	}

	public void testGetOfficeLevels() throws Exception {
		List<OfficeLevelEntity> officeLevels = 
			new OfficeHierarchyPersistence()
				.getOfficeLevels(TestObjectFactory.TEST_LOCALE);
		assertEquals(OFFICE_LEVELS, officeLevels.size());
		for (OfficeLevelEntity officeLevelEntity : officeLevels) {
			assertTrue(officeLevelEntity.isConfigured());
		}
	}
	
	public void testIsOfficePresentForLevel() throws Exception {
		OfficeHierarchyPersistence persistence = new OfficeHierarchyPersistence();
		assertTrue(persistence.isOfficePresentForLevel(
			OfficeLevel.HEADOFFICE));
		assertTrue(persistence.isOfficePresentForLevel(
			OfficeLevel.BRANCHOFFICE));
		assertFalse(persistence.isOfficePresentForLevel(
				OfficeLevel.REGIONALOFFICE));
		
	}

	public void testIsOfficePresentForLevelFailure() throws Exception {
		OfficeHierarchyPersistence persistence = new OfficeHierarchyPersistence();
		TestObjectFactory.simulateInvalidConnection();
		try{
			persistence.isOfficePresentForLevel(
				OfficeLevel.HEADOFFICE);
			fail();
		} catch (PersistenceException expected) {
		}
		finally {
			HibernateUtil.closeSession();
		}
	}

}
