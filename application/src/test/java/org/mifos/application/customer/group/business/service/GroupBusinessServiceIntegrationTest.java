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

package org.mifos.application.customer.group.business.service;

import java.sql.Date;

import junit.framework.Assert;

import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.business.GroupPerformanceHistoryEntity;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class GroupBusinessServiceIntegrationTest extends MifosIntegrationTestCase {
    public GroupBusinessServiceIntegrationTest() throws Exception {
        super();
    }

    private MeetingBO meeting;

    private CustomerBO center;

    private CustomerBO group;

    private CustomerBO client;

    private SavingsTestHelper helper = new SavingsTestHelper();

    private SavingsOfferingBO savingsOffering;

    private LoanBO loanBO;

    private SavingsBO savingsBO1;

    private SavingsBO savingsBO2;

    private GroupBusinessService groupBusinessService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        groupBusinessService = (GroupBusinessService) ServiceFactory.getInstance().getBusinessService(
                BusinessServiceName.Group);
    }

    @Override
    public void tearDown() throws Exception {
        TestObjectFactory.cleanUp(loanBO);
        TestObjectFactory.cleanUp(savingsBO1);
        TestObjectFactory.cleanUp(savingsBO2);
        TestObjectFactory.cleanUp(client);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testGetGroupBySystemId() throws Exception {
        center = createCenter("Center_Active_test");
        String groupName = "Group_Active_test";
        group = createGroup(groupName);
        client = createClient("Client_Active_test");
        savingsBO2 = getSavingsAccount(center, "fsaf5", "ads5");
        savingsBO1 = getSavingsAccount(group, "fsaf6", "ads6");
        loanBO = getLoanAccount(group);
        StaticHibernateUtil.closeSession();
        loanBO = TestObjectFactory.getObject(LoanBO.class, loanBO.getAccountId());
        savingsBO2 = TestObjectFactory.getObject(SavingsBO.class, savingsBO2.getAccountId());
        savingsBO1 = TestObjectFactory.getObject(SavingsBO.class, savingsBO1.getAccountId());
        group = groupBusinessService.findBySystemId(group.getGlobalCustNum());
        Assert.assertNotNull(group);
       Assert.assertEquals(groupName, group.getDisplayName());
       Assert.assertEquals(3, group.getAccounts().size());
       Assert.assertEquals(1, group.getOpenLoanAccounts().size());
       Assert.assertEquals(1, group.getOpenSavingAccounts().size());
       Assert.assertEquals(CustomerStatus.GROUP_ACTIVE.getValue(), group.getCustomerStatus().getId());
       Assert.assertEquals(1, ((GroupPerformanceHistoryEntity) group.getPerformanceHistory()).getActiveClientCount()
                .intValue());
        StaticHibernateUtil.closeSession();
        loanBO = TestObjectFactory.getObject(LoanBO.class, loanBO.getAccountId());
        savingsBO1 = TestObjectFactory.getObject(SavingsBO.class, savingsBO1.getAccountId());
        savingsBO2 = TestObjectFactory.getObject(SavingsBO.class, savingsBO2.getAccountId());
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
    }

    public void testFailureGetGroupBySystemId() throws Exception {
        center = createCenter("Center_Active_test");
        String groupName = "Group_Active_test";
        group = createGroup(groupName);
        StaticHibernateUtil.closeSession();
        TestObjectFactory.simulateInvalidConnection();
        try {
            groupBusinessService.findBySystemId(group.getGlobalCustNum());
           Assert.assertTrue(false);
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        }
        StaticHibernateUtil.closeSession();
    }

    public void testSuccessfulGet() throws Exception {
        center = createCenter("Center_Active_test");
        String groupName = "Group_Active_test";
        group = createGroup(groupName);
        client = createClient("Client_Active_test");
        savingsBO1 = getSavingsAccount(group, "fsaf6", "ads6");
        loanBO = getLoanAccount(group);
        StaticHibernateUtil.closeSession();
        loanBO = TestObjectFactory.getObject(LoanBO.class, loanBO.getAccountId());
        savingsBO1 = TestObjectFactory.getObject(SavingsBO.class, savingsBO1.getAccountId());
        group = groupBusinessService.getGroup(group.getCustomerId());
        Assert.assertNotNull(group);
       Assert.assertEquals(groupName, group.getDisplayName());
       Assert.assertEquals(3, group.getAccounts().size());
       Assert.assertEquals(1, group.getOpenLoanAccounts().size());
       Assert.assertEquals(1, group.getOpenSavingAccounts().size());
       Assert.assertEquals(CustomerStatus.GROUP_ACTIVE.getValue(), group.getCustomerStatus().getId());
       Assert.assertEquals(1, ((GroupPerformanceHistoryEntity) group.getPerformanceHistory()).getActiveClientCount()
                .intValue());
        StaticHibernateUtil.closeSession();
        loanBO = TestObjectFactory.getObject(LoanBO.class, loanBO.getAccountId());
        savingsBO1 = TestObjectFactory.getObject(SavingsBO.class, savingsBO1.getAccountId());
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
    }

    public void testFailureGet() throws Exception {
        center = createCenter("Center_Active_test");
        String groupName = "Group_Active_test";
        group = createGroup(groupName);
        StaticHibernateUtil.closeSession();
        TestObjectFactory.simulateInvalidConnection();
        try {
            groupBusinessService.getGroup(group.getCustomerId());
           Assert.assertTrue(false);
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        }
        StaticHibernateUtil.closeSession();
    }

    public void testSearch() throws Exception {
        center = createCenter("Center_Active_test");
        String groupName = "Group_Active_test";
        group = createGroup(groupName);
        QueryResult queryResult = groupBusinessService.search(group.getDisplayName(), Short.valueOf("1"));
        Assert.assertNotNull(queryResult);
       Assert.assertEquals(1, queryResult.getSize());
       Assert.assertEquals(1, queryResult.get(0, 10).size());
    }

    public void testSearchForAddingClientToGroup() throws Exception {
        center = createCenter("Center_Active_test");
        String groupName = "Group_Active_test";
        group = createGroup(groupName);
        QueryResult queryResult = groupBusinessService.searchForAddingClientToGroup(group.getDisplayName(), Short
                .valueOf("1"));
        Assert.assertNotNull(queryResult);
       Assert.assertEquals(1, queryResult.getSize());
       Assert.assertEquals(1, queryResult.get(0, 10).size());
    }

    public void testFailureSearch() throws Exception {
        center = createCenter("Center_Active_test");
        String groupName = "Group_Active_test";
        group = createGroup(groupName);
        StaticHibernateUtil.closeSession();
        TestObjectFactory.simulateInvalidConnection();
        try {
            groupBusinessService.search(group.getDisplayName(), Short.valueOf("1"));
           Assert.assertTrue(false);
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        }
        StaticHibernateUtil.closeSession();
    }

    private GroupBO createGroup(String groupName) {
        return TestObjectFactory.createWeeklyFeeGroupUnderCenter(groupName, CustomerStatus.GROUP_ACTIVE, center);
    }

    private CenterBO createCenter(String name) {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        return TestObjectFactory.createWeeklyFeeCenter(name, meeting);
    }

    private ClientBO createClient(String clientName) {
        return TestObjectFactory.createClient(clientName, CustomerStatus.CLIENT_ACTIVE, group);
    }

    private LoanBO getLoanAccount(CustomerBO customerBO) {
        Date startDate = new Date(System.currentTimeMillis());
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering(startDate, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", customerBO,
                AccountState.LOAN_ACTIVE_IN_GOOD_STANDING, startDate, loanOffering);
    }

    private SavingsBO getSavingsAccount(CustomerBO customerBO, String offeringName, String shortName) throws Exception {
        savingsOffering = helper.createSavingsOffering(offeringName, shortName);
        return TestObjectFactory.createSavingsAccount("000100000000017", customerBO,
                AccountStates.SAVINGS_ACC_APPROVED, new Date(System.currentTimeMillis()), savingsOffering);
    }
}
