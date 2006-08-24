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
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class OfficeHierarchyPersistenceTest extends MifosTestCase {

	private static final int OFFICE_LEVELS = 5;

	public OfficeHierarchyPersistenceTest() {
		super();
	}

	public void testGetOfficeLevels() throws Exception {
		UserContext userContext = TestObjectFactory.getUserContext();
		List<OfficeLevelEntity> officeLevels = new OfficeHierarchyPersistence()
				.getOfficeLevels(userContext.getLocaleId());
		assertEquals(OFFICE_LEVELS, officeLevels.size());
		for (OfficeLevelEntity officeLevelEntity : officeLevels) {
			assertTrue(officeLevelEntity.isConfigured());
		}
	}

}
