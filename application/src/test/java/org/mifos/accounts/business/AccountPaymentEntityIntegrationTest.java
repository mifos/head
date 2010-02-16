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

package org.mifos.accounts.business;

import java.sql.Date;
import java.util.List;

import junit.framework.Assert;

import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerAccountBOTestUtils;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerBOTestUtils;
import org.mifos.customers.business.CustomerScheduleEntity;
import org.mifos.customers.business.CustomerTrxnDetailEntity;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class AccountPaymentEntityIntegrationTest extends MifosIntegrationTestCase {

    public AccountPaymentEntityIntegrationTest() throws Exception {
        super();
    }

    private AccountBO accountBO = null;
    private CustomerBO center = null;
    private CustomerBO group = null;
    private CustomerBO client = null;
    private UserContext userContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        try {
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

    public void testReversalAdjustment() throws Exception {
        userContext = TestObjectFactory.getContext();
        createInitialObjects();
        accountBO = client.getCustomerAccount();
        Date currentDate = new Date(System.currentTimeMillis());
        CustomerAccountBO customerAccountBO = (CustomerAccountBO) accountBO;
        customerAccountBO.setUserContext(userContext);

        CustomerScheduleEntity accountAction = (CustomerScheduleEntity) customerAccountBO.getAccountActionDate(Short
                .valueOf("1"));
        CustomerAccountBOTestUtils.setMiscFeePaid(accountAction, TestObjectFactory.getMoneyForMFICurrency(100));
        CustomerAccountBOTestUtils.setMiscPenaltyPaid(accountAction, TestObjectFactory
                .getMoneyForMFICurrency(100));
        CustomerAccountBOTestUtils.setPaymentDate(accountAction, currentDate);
        accountAction.setPaymentStatus(PaymentStatus.PAID);

        MasterPersistence masterPersistenceService = new MasterPersistence();

        AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(customerAccountBO, TestObjectFactory
                .getMoneyForMFICurrency(100), "1111", currentDate, new PaymentTypeEntity(Short.valueOf("1")), new Date(
                System.currentTimeMillis()));

        CustomerTrxnDetailEntity accountTrxnEntity = new CustomerTrxnDetailEntity(accountPaymentEntity,
                AccountActionTypes.PAYMENT, Short.valueOf("1"), accountAction.getActionDate(),
                TestObjectFactory.getPersonnel(userContext.getId()), currentDate, TestObjectFactory
                        .getMoneyForMFICurrency(200), "payment done", null, TestObjectFactory
                        .getMoneyForMFICurrency(100), TestObjectFactory.getMoneyForMFICurrency(100),
                        masterPersistenceService);

        for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountAction.getAccountFeesActionDetails()) {
            accountFeesActionDetailEntity.setFeeAmountPaid(TestObjectFactory.getMoneyForMFICurrency(100));
            FeesTrxnDetailEntity feeTrxn = new FeesTrxnDetailEntity(accountTrxnEntity, accountFeesActionDetailEntity
                    .getAccountFee(), accountFeesActionDetailEntity.getFeeAmount());
            CustomerBOTestUtils.addFeesTrxnDetail(accountTrxnEntity, feeTrxn);
            // TODO: is there anything to assert on here?
            // totalFees = accountFeesActionDetailEntity.getFeeAmountPaid();
        }
        accountPaymentEntity.addAccountTrxn(accountTrxnEntity);
        customerAccountBO.addAccountPayment(accountPaymentEntity);

        TestObjectFactory.updateObject(customerAccountBO);
        TestObjectFactory.flushandCloseSession();
        customerAccountBO = TestObjectFactory.getObject(CustomerAccountBO.class, customerAccountBO.getAccountId());
        client = customerAccountBO.getCustomer();

        PersonnelBO loggedInUser = new PersonnelPersistence().getPersonnel(userContext.getId());
        List<AccountTrxnEntity> reversedTrxns = customerAccountBO.getLastPmnt().reversalAdjustment(loggedInUser,
                "adjustment");
        for (AccountTrxnEntity accntTrxn : reversedTrxns) {
            CustomerTrxnDetailEntity custTrxn = (CustomerTrxnDetailEntity) accntTrxn;
            CustomerScheduleEntity accntActionDate = (CustomerScheduleEntity) customerAccountBO
                    .getAccountActionDate(custTrxn.getInstallmentId());
           Assert.assertEquals(custTrxn.getMiscFeeAmount(), accntActionDate.getMiscFeePaid().negate());
           Assert.assertEquals(custTrxn.getMiscPenaltyAmount(), accntActionDate.getMiscPenaltyPaid().negate());
           Assert.assertEquals(loggedInUser.getPersonnelId(), custTrxn.getPersonnel().getPersonnelId());
        }

    }

    private void createInitialObjects() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        // TODO: Is CLIENT_ACTIVE really right? Shouldn't it be GROUP_ACTIVE?
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.CENTER_ACTIVE, center);
        client = TestObjectFactory.createClient("Client_Active_test", CustomerStatus.CLIENT_ACTIVE, group);
    }

}
