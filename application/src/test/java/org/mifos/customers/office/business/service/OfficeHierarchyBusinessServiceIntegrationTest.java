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

package org.mifos.customers.office.business.service;

import java.util.List;

import junit.framework.Assert;

import org.mifos.customers.office.business.OfficeLevelEntity;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class OfficeHierarchyBusinessServiceIntegrationTest extends MifosIntegrationTestCase {

    private static final int OFFICE_LEVELS = 5;

    public OfficeHierarchyBusinessServiceIntegrationTest() throws Exception {
        super();
    }

    public void testGetOfficeLevels() throws Exception {
        List<OfficeLevelEntity> officeLevels = new OfficeHierarchyBusinessService()
                .getOfficeLevels(TestObjectFactory.TEST_LOCALE);
       Assert.assertEquals(OFFICE_LEVELS, officeLevels.size());
        for (OfficeLevelEntity officeLevelEntity : officeLevels) {
           Assert.assertTrue(officeLevelEntity.isConfigured());
        }
    }

    /**
     * Test that we wrap a PersistenceException in a ServiceException. (isn't
     * there an easier way to get whatever user-visible behavior is desired on
     * those exceptions?)
     */
    public void testGetOfficeLevelsFailure() throws Exception {
        TestObjectFactory.simulateInvalidConnection();
        try {
            new OfficeHierarchyBusinessService().getOfficeLevels(TestObjectFactory.TEST_LOCALE);
            Assert.fail();
        } catch (ServiceException e) {
           Assert.assertEquals("exception.framework.ApplicationException", e.getKey());
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

}
