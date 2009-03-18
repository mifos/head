/**

* OfficeLevelEntityTest.java version: 1.0



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

package org.mifos.application.office.business;

import org.mifos.application.office.exceptions.OfficeException;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class OfficeLevelEntityTest extends MifosIntegrationTest {

	
	
	public OfficeLevelEntityTest() throws SystemException, ApplicationException {
        super();
    }

    public void testUpdateLevelFailureIfOfficePresent() throws Exception {
		OfficeBO parent = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		OfficeBO regionalOffice = TestObjectFactory.createOffice(
				OfficeLevel.REGIONALOFFICE, parent, "Office_Regional", "OFB");
		StaticHibernateUtil.closeSession();
		regionalOffice = (OfficeBO) StaticHibernateUtil.getSessionTL().get(
				OfficeBO.class, regionalOffice.getOfficeId());
		try{
		OfficeLevelEntity officeLevelEntity = regionalOffice.getLevel();
		officeLevelEntity.update(false);
		assertFalse(true);
		}
		catch(OfficeException oe){
			assertTrue(true);
			assertEquals(oe.getKey(),OfficeConstants.KEYHASACTIVEOFFICEWITHLEVEL);
		}
		
		TestObjectFactory.cleanUp(regionalOffice);
		

	}


}
