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

package org.mifos.customers.office.business;

import junit.framework.Assert;

import org.mifos.customers.office.exceptions.OfficeException;
import org.mifos.customers.office.util.helpers.OfficeConstants;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class OfficeLevelEntityIntegrationTest extends MifosIntegrationTestCase {

    public OfficeLevelEntityIntegrationTest() throws Exception {
        super();
    }

    public void testUpdateLevelFailureIfOfficePresent() throws Exception {
        OfficeBO parent = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
        OfficeBO regionalOffice = TestObjectFactory.createOffice(OfficeLevel.REGIONALOFFICE, parent, "Office_Regional",
                "OFB");
        StaticHibernateUtil.closeSession();
        regionalOffice = (OfficeBO) StaticHibernateUtil.getSessionTL()
                .get(OfficeBO.class, regionalOffice.getOfficeId());
        try {
            OfficeLevelEntity officeLevelEntity = regionalOffice.getLevel();
            officeLevelEntity.update(false);
            Assert.assertFalse(true);
        } catch (OfficeException oe) {
           Assert.assertTrue(true);
           Assert.assertEquals(oe.getKey(), OfficeConstants.KEYHASACTIVEOFFICEWITHLEVEL);
        }

        TestObjectFactory.cleanUp(regionalOffice);

    }

}
