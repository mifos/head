/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.customers.center.business.service;

import junit.framework.Assert;

import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CenterBusinessServiceIntegrationTest extends MifosIntegrationTestCase {
    
    public CenterBusinessServiceIntegrationTest() throws Exception {
        super();
    }

    private CustomerBO center;

    private CustomerBO group;

    private CustomerBO client;

    private SavingsBO savingsBO;

    private CenterBusinessService service;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        service = new CenterBusinessService();
    }

    @Override
    public void tearDown() throws Exception {
        try {
            TestObjectFactory.cleanUp(savingsBO);
            TestObjectFactory.cleanUp(client);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(center);
        } catch (Exception e) {
            // TODO Whoops, cleanup didnt work, reset db
            TestDatabase.resetMySQLDatabase();
        }
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testFailureFindBySystemId() throws Exception {
        center = createCenter("Center1");
        StaticHibernateUtil.closeSession();
        TestObjectFactory.simulateInvalidConnection();
        try {
            service.findBySystemId(center.getGlobalCustNum());
            Assert.assertTrue(false);
        } catch (ServiceException e) {
            Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    private CenterBO createCenter(String name) {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        return TestObjectFactory.createWeeklyFeeCenter(name, meeting);
    }
}
