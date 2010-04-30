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

package org.mifos.customers.business;

import java.sql.Date;

import junit.framework.Assert;

import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountTestUtils;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.business.FeesTrxnDetailEntity;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.ClientBuilder;
import org.mifos.application.collectionsheet.persistence.GroupBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.util.UserContext;

public class CustomerTrxnDetailEntityIntegrationTest extends MifosIntegrationTestCase {

    public CustomerTrxnDetailEntityIntegrationTest() throws Exception {
        super();
    }

    private MeetingBO weeklyMeeting;
    private AccountBO accountBO = null;
    private CenterBO center = null;
    private GroupBO group = null;
    private ClientBO client = null;
    private UserContext userContext = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userContext = TestUtils.makeUser();

        OfficeBO office = IntegrationTestObjectMother.sampleBranchOffice();
        PersonnelBO testUser = IntegrationTestObjectMother.testUser();

        weeklyMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();
        IntegrationTestObjectMother.saveMeeting(weeklyMeeting);

        center = new CenterBuilder().with(weeklyMeeting).withName("Center").with(office).withLoanOfficer(
                testUser).build();
        IntegrationTestObjectMother.createCenter(center, weeklyMeeting);

        group = new GroupBuilder().withMeeting(weeklyMeeting).withName("Group").withOffice(office).withLoanOfficer(
                testUser).withParentCustomer(center).build();
        IntegrationTestObjectMother.createGroup(group, weeklyMeeting);

        client = new ClientBuilder().withMeeting(weeklyMeeting).withName("Client 1").withOffice(office)
                .withLoanOfficer(testUser).withParentCustomer(group).buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(client, weeklyMeeting);
    }

    @Override
    protected void tearDown() throws Exception {
        IntegrationTestObjectMother.cleanCustomerHierarchyWithMeeting(client, group, center, weeklyMeeting);
    }

    public void testGenerateReverseTrxn() throws Exception {
        accountBO = client.getCustomerAccount();
        Date currentDate = new Date(System.currentTimeMillis());
        CustomerAccountBO customerAccountBO = (CustomerAccountBO) accountBO;
        customerAccountBO.setUserContext(userContext);

        CustomerScheduleEntity accountAction = (CustomerScheduleEntity) customerAccountBO.getAccountActionDate(Short
                .valueOf("1"));
        accountAction.setMiscFeePaid(TestUtils.createMoney(100));
        accountAction.setMiscPenaltyPaid(TestUtils.createMoney(100));
        accountAction.setPaymentDate(currentDate);
        accountAction.setPaymentStatus(PaymentStatus.PAID);

        MasterPersistence masterPersistenceService = new MasterPersistence();

        AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(accountBO, TestUtils.createMoney(100),
                "1111", currentDate, new PaymentTypeEntity(Short.valueOf("1")), new Date(System.currentTimeMillis()));

        CustomerTrxnDetailEntity accountTrxnEntity = new CustomerTrxnDetailEntity(accountPaymentEntity,
                AccountActionTypes.PAYMENT, Short.valueOf("1"), accountAction.getActionDate(), TestObjectFactory
                        .getPersonnel(userContext.getId()), currentDate, TestUtils.createMoney(200), "payment done",
                null, TestUtils.createMoney(100), TestUtils.createMoney(100), masterPersistenceService);

        for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountAction.getAccountFeesActionDetails()) {
            CustomerAccountBOTestUtils.setFeeAmountPaid((CustomerFeeScheduleEntity) accountFeesActionDetailEntity,
                    TestUtils.createMoney(100));
            FeesTrxnDetailEntity feeTrxn = new FeesTrxnDetailEntity(accountTrxnEntity, accountFeesActionDetailEntity
                    .getAccountFee(), accountFeesActionDetailEntity.getFeeAmount());
            accountTrxnEntity.addFeesTrxnDetail(feeTrxn);
        }
        accountPaymentEntity.addAccountTrxn(accountTrxnEntity);
        AccountTestUtils.addAccountPayment(accountPaymentEntity, customerAccountBO);

        PersonnelBO loggedInUser = new PersonnelPersistence().getPersonnel(userContext.getId());
        for (AccountTrxnEntity accntTrxn : customerAccountBO.getLastPmnt().getAccountTrxns()) {
            AccountTrxnEntity reverseAccntTrxn = ((CustomerTrxnDetailEntity) accntTrxn).generateReverseTrxn(
                    loggedInUser, "adjustment");
            Assert.assertEquals(reverseAccntTrxn.getAmount(), accntTrxn.getAmount().negate());
            Assert.assertEquals(loggedInUser.getPersonnelId(), reverseAccntTrxn.getPersonnel().getPersonnelId());
        }
    }
}