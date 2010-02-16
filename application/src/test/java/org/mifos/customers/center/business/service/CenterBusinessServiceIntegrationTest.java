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

package org.mifos.customers.center.business.service;

import java.util.Date;

import junit.framework.Assert;

import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CenterBusinessServiceIntegrationTest extends MifosIntegrationTestCase {
    public CenterBusinessServiceIntegrationTest() throws Exception {
        super();
    }

    private CustomerBO center;

    private CustomerBO group;

    private CustomerBO client;

    private SavingsTestHelper helper = new SavingsTestHelper();

    private SavingsOfferingBO savingsOffering;

    private SavingsBO savingsBO;

    private CenterBusinessService service;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        service = (CenterBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Center);
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

    public void testGetCenter() throws Exception {
        center = createCenter("center1");
        createAccountsForCenter();
        savingsBO = getSavingsAccount(center, "fsaf6", "ads6");
        StaticHibernateUtil.closeSession();
        center = service.getCenter(center.getCustomerId());
        Assert.assertNotNull(center);
       Assert.assertEquals("center1", center.getDisplayName());
       Assert.assertEquals(2, center.getAccounts().size());
       Assert.assertEquals(0, center.getOpenLoanAccounts().size());
       Assert.assertEquals(1, center.getOpenSavingAccounts().size());
       Assert.assertEquals(CustomerStatus.CENTER_ACTIVE.getValue(), center.getCustomerStatus().getId());
        StaticHibernateUtil.closeSession();
        retrieveAccountsToDelete();
    }

    public void testSuccessfulGet() throws Exception {
        center = createCenter("Center2");
        createAccountsForCenter();
        savingsBO = getSavingsAccount(center, "fsaf6", "ads6");
        StaticHibernateUtil.closeSession();
        center = service.getCenter(center.getCustomerId());
        Assert.assertNotNull(center);
       Assert.assertEquals("Center2", center.getDisplayName());
       Assert.assertEquals(2, center.getAccounts().size());
       Assert.assertEquals(0, center.getOpenLoanAccounts().size());
       Assert.assertEquals(1, center.getOpenSavingAccounts().size());
       Assert.assertEquals(CustomerStatus.CENTER_ACTIVE.getValue(), center.getCustomerStatus().getId());
        StaticHibernateUtil.closeSession();
        retrieveAccountsToDelete();
    }

    public void testFailureGet() throws Exception {
        center = createCenter("Center1");
        StaticHibernateUtil.closeSession();
        TestObjectFactory.simulateInvalidConnection();
        try {
            service.getCenter(center.getCustomerId());
           Assert.assertTrue(false);
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }
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

    public void testSearch() throws Exception {
        center = createCenter("center1");
        QueryResult queryResult = service.search("center1", Short.valueOf("1"));
       Assert.assertEquals(1, queryResult.getSize());
       Assert.assertEquals(1, queryResult.get(0, 10).size());
    }

    public void testFailureSearch() throws Exception {
        center = createCenter("Center1");
        TestObjectFactory.simulateInvalidConnection();
        try {
            service.search("center1", Short.valueOf("1"));
           Assert.assertTrue(false);
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    private SavingsBO getSavingsAccount(CustomerBO customerBO, String offeringName, String shortName) throws Exception {
        savingsOffering = helper.createSavingsOffering(offeringName, shortName);
        return TestObjectFactory.createSavingsAccount("000100000000017", customerBO,
                AccountStates.SAVINGS_ACC_APPROVED, new Date(System.currentTimeMillis()), savingsOffering);
    }

    private CenterBO createCenter(String name) {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        return TestObjectFactory.createWeeklyFeeCenter(name, meeting);
    }

    private GroupBO createGroup(String groupName) {
        return TestObjectFactory.createWeeklyFeeGroupUnderCenter(groupName, CustomerStatus.GROUP_ACTIVE, center);
    }

    private ClientBO createClient(String clientName) {
        return TestObjectFactory.createClient(clientName, CustomerStatus.CLIENT_ACTIVE, group);
    }

    private void createAccountsForCenter() throws Exception {
        String groupName = "Group_Active_test";
        group = createGroup(groupName);
        client = createClient("Client_Active_test");
    }

    private void retrieveAccountsToDelete() {
        savingsBO = TestObjectFactory.getObject(SavingsBO.class, savingsBO.getAccountId());
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
    }
}
