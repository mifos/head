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

package org.mifos.application.customer.business;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.sampleBranchOffice;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.testUser;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_SECOND_WEEK;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.AccountTestUtils;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountTestUtils;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.business.FeesTrxnDetailEntity;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.accounts.util.helpers.WaiveEnum;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.FeeView;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeePayment;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CustomerAccountBOIntegrationTest extends MifosIntegrationTestCase {
    public CustomerAccountBOIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private static final double DELTA = 0.00000001;

    private CustomerAccountBO customerAccountBO;

    private CustomerBO center;

    private CustomerBO group;

    private CustomerBO client;

    private UserContext userContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userContext = TestObjectFactory.getContext();
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

    public void testSuccessfulMakePayment() throws Exception {
        createCenter();
        CustomerAccountBO customerAccount = center.getCustomerAccount();
        Assert.assertNotNull(customerAccount);
        Date transactionDate = new Date(System.currentTimeMillis());
        List<AccountActionDateEntity> dueActionDates = TestObjectFactory.getDueActionDatesForAccount(customerAccount
                .getAccountId(), transactionDate);
        Assert.assertEquals("The size of the due insallments is ", dueActionDates.size(), 1);

        PaymentData accountPaymentDataView = TestObjectFactory.getCustomerAccountPaymentDataView(dueActionDates,
                new Money(TestObjectFactory.getMFICurrency(), "100.0"), null, center.getPersonnel(), "3424324", Short
                        .valueOf("1"), transactionDate, transactionDate);

        center = TestObjectFactory.getCustomer(center.getCustomerId());
        customerAccount = center.getCustomerAccount();
        customerAccount.applyPaymentWithPersist(accountPaymentDataView);
        StaticHibernateUtil.commitTransaction();
        Assert.assertEquals("The size of the payments done is", customerAccount.getAccountPayments().size(), 1);
        Assert.assertEquals("The size of the due insallments after payment is", TestObjectFactory
                .getDueActionDatesForAccount(customerAccount.getAccountId(), transactionDate).size(), 0);
        Assert.assertEquals(customerAccount.getCustomerActivitDetails().size(), 1);

        for (CustomerActivityEntity activity : customerAccount.getCustomerActivitDetails()) {

            Assert.assertEquals(transactionDate, activity.getCreatedDate());
        }

        assertThat(customerAccount.getAccountPayments().size(), is(1));
        for (AccountPaymentEntity accountPayment : customerAccount.getAccountPayments()) {
            assertThat(accountPayment.getAccountTrxns().size(), is(1));
            for (AccountTrxnEntity accountTrxnEntity : accountPayment.getAccountTrxns()) {
                assertThat(accountTrxnEntity.getFinancialTransactions().size(), is(2));
            }
        }
    }

    public void testFailureMakePayment() throws Exception {
        createCenter();
        CustomerAccountBO customerAccount = center.getCustomerAccount();
        Assert.assertNotNull(customerAccount);
        Date transactionDate = new Date(System.currentTimeMillis());
        List<AccountActionDateEntity> dueActionDates = TestObjectFactory.getDueActionDatesForAccount(customerAccount
                .getAccountId(), transactionDate);
        Assert.assertEquals("The size of the due insallments is ", dueActionDates.size(), 1);
        PaymentData accountPaymentDataView = TestObjectFactory.getCustomerAccountPaymentDataView(dueActionDates,
                new Money(TestObjectFactory.getMFICurrency(), "100.0"), null, center.getPersonnel(), "3424324", Short
                        .valueOf("1"), transactionDate, transactionDate);
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        customerAccount = center.getCustomerAccount();

        customerAccount.applyPaymentWithPersist(accountPaymentDataView);
        StaticHibernateUtil.commitTransaction();
        Assert.assertEquals("The size of the payments done is", customerAccount.getAccountPayments().size(), 1);

        try {
            customerAccount.applyPaymentWithPersist(accountPaymentDataView);
            Assert.fail("Payment is done even though they are no dues");
        } catch (AccountException ae) {
            Assert.assertTrue("Payment is not allowed when there are no dues", true);
        }
    }

    public void testIsAdjustPossibleOnLastTrxn_NotActiveState() throws Exception {
        userContext = TestUtils.makeUser();
        createInitialObjects();

        // TODO: Is CENTER_ACTIVE right or should it be CLIENT_ACTIVE?
        client = TestObjectFactory.createClient("Client_Active_test", CustomerStatus.CENTER_ACTIVE, group);
        customerAccountBO = client.getCustomerAccount();
        Assert.assertFalse("State is not active hence adjustment is not possible", customerAccountBO
                .isAdjustPossibleOnLastTrxn());

    }

    public void testIsAdjustPossibleOnLastTrxn_LastPaymentNull() throws Exception {
        userContext = TestUtils.makeUser();
        createInitialObjects();
        client = TestObjectFactory.createClient("Client_Active_test", CustomerStatus.CLIENT_ACTIVE, group);
        customerAccountBO = client.getCustomerAccount();
        Assert.assertFalse("Last payment was null hence adjustment is not possible", customerAccountBO
                .isAdjustPossibleOnLastTrxn());

    }

    public void testIsAdjustPossibleOnLastTrxn_LastPaymentWasAdjustment() throws Exception {
        userContext = TestUtils.makeUser();
        createInitialObjects();
        applyPayment();
        customerAccountBO = TestObjectFactory.getObject(CustomerAccountBO.class, customerAccountBO.getAccountId());
        client = customerAccountBO.getCustomer();
        customerAccountBO.setUserContext(userContext);
        customerAccountBO.adjustPmnt("payment adjusted");
        TestObjectFactory.updateObject(customerAccountBO);
        Assert.assertFalse("Last payment was adjustment hence adjustment is not possible", customerAccountBO
                .isAdjustPossibleOnLastTrxn());

    }

    public void testUpdateInstallmentAfterAdjustment() throws Exception {
        userContext = TestUtils.makeUser();
        createInitialObjects();
        applyPayment();
        customerAccountBO = TestObjectFactory.getObject(CustomerAccountBO.class, customerAccountBO.getAccountId());
        client = customerAccountBO.getCustomer();
        customerAccountBO.setUserContext(userContext);
        List<AccountTrxnEntity> reversedTrxns = AccountTestUtils.reversalAdjustment("payment adjustment done",
                customerAccountBO.getLastPmnt());
        customerAccountBO.updateInstallmentAfterAdjustment(reversedTrxns);
        for (AccountTrxnEntity accntTrxn : reversedTrxns) {
            CustomerTrxnDetailEntity custTrxn = (CustomerTrxnDetailEntity) accntTrxn;
            CustomerScheduleEntity accntActionDate = (CustomerScheduleEntity) customerAccountBO
                    .getAccountActionDate(custTrxn.getInstallmentId());
            Assert.assertEquals("Misc Fee Adjusted", accntActionDate.getMiscFeePaid().getAmountDoubleValue(), 0.0, 0.0);
            Assert.assertEquals("Misc Penalty Adjusted", accntActionDate.getMiscPenaltyPaid().getAmountDoubleValue(),
                    0.0, 0.0);
        }
        for (CustomerActivityEntity customerActivityEntity : customerAccountBO.getCustomerActivitDetails()) {
            Assert.assertEquals("Amnt Adjusted", customerActivityEntity.getDescription());
        }

    }

    public void testAdjustPmnt() throws Exception {
        userContext = TestUtils.makeUser();
        createInitialObjects();
        applyPayment();
        customerAccountBO = TestObjectFactory.getObject(CustomerAccountBO.class, customerAccountBO.getAccountId());
        client = customerAccountBO.getCustomer();
        customerAccountBO.setUserContext(userContext);
        customerAccountBO.adjustPmnt("payment adjusted");
        TestObjectFactory.updateObject(customerAccountBO);
        int countFinTrxns = 0;
        for (AccountTrxnEntity accountTrxnEntity : customerAccountBO.getLastPmnt().getAccountTrxns()) {
            countFinTrxns += accountTrxnEntity.getFinancialTransactions().size();

        }

        Assert.assertEquals(countFinTrxns, 6);

        CustomerTrxnDetailEntity custTrxn = null;
        for (AccountTrxnEntity accTrxn : customerAccountBO.getLastPmnt().getAccountTrxns()) {
            custTrxn = (CustomerTrxnDetailEntity) accTrxn;
            break;
        }
        AccountActionDateEntity installment = customerAccountBO.getAccountActionDate(custTrxn.getInstallmentId());
        Assert.assertFalse("The installment adjusted should now be marked unpaid(due).", installment.isPaid());

    }

    public void testWaiveForIntallmentOncePaid() throws Exception {
        createCenter();
        CustomerAccountBO customerAccount = center.getCustomerAccount();
        Assert.assertNotNull(customerAccount);
        Date transactionDate = new Date(System.currentTimeMillis());
        List<AccountActionDateEntity> dueActionDates = TestObjectFactory.getDueActionDatesForAccount(customerAccount
                .getAccountId(), transactionDate);
        Assert.assertEquals(1, dueActionDates.size());
        CustomerScheduleEntity customerScheduleEntity = (CustomerScheduleEntity) customerAccount
                .getAccountActionDate(Short.valueOf("1"));
        PaymentData accountPaymentDataView = TestObjectFactory.getCustomerAccountPaymentDataView(dueActionDates,
                customerScheduleEntity.getTotalFeeDueWithMiscFee(), null, center.getPersonnel(), "3424324", Short
                        .valueOf("1"), transactionDate, transactionDate);
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        customerAccount = center.getCustomerAccount();
        customerAccount.applyPaymentWithPersist(accountPaymentDataView);
        StaticHibernateUtil.commitTransaction();
        Assert.assertEquals("The size of the payments done is", customerAccount.getAccountPayments().size(), 1);
        Assert.assertEquals("The size of the due insallments after payment is", TestObjectFactory
                .getDueActionDatesForAccount(customerAccount.getAccountId(), transactionDate).size(), 0);
        Assert.assertEquals(customerAccount.getCustomerActivitDetails().size(), 1);
        for (CustomerActivityEntity activity : customerAccount.getCustomerActivitDetails()) {
            Assert.assertEquals(transactionDate, activity.getCreatedDate());
        }
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        UserContext uc = TestUtils.makeUser();
        customerAccount = center.getCustomerAccount();
        customerAccount.setUserContext(uc);
        customerAccount.applyCharge(Short.valueOf(AccountConstants.MISC_FEES), new Double("33"));
        for (AccountActionDateEntity accountActionDateEntity : customerAccount.getAccountActionDates()) {
            CustomerScheduleEntity customerSchEntity = (CustomerScheduleEntity) accountActionDateEntity;
            if (customerSchEntity.getInstallmentId().equals(Short.valueOf("2"))) {
                Assert.assertEquals(new Money("33.0"), customerSchEntity.getMiscFee());
            }
        }
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        customerAccount = center.getCustomerAccount();
        customerAccount.setUserContext(uc);
        customerAccount.waiveAmountDue(WaiveEnum.ALL);
        for (AccountActionDateEntity accountAction : customerAccount.getAccountActionDates()) {
            CustomerScheduleEntity accountActionDateEntity = (CustomerScheduleEntity) accountAction;
            if (accountActionDateEntity.getInstallmentId().equals(Short.valueOf("2"))) {
                Assert.assertEquals(new Money(), accountActionDateEntity.getTotalFeeDueWithMiscFee());
            }
        }
    }

    public void testWaiveChargeDue() throws Exception {
        createInitialObjects();
        TestObjectFactory.flushandCloseSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        userContext = TestUtils.makeUser();
        CustomerAccountBO customerAccountBO = group.getCustomerAccount();
        customerAccountBO.setUserContext(userContext);
        customerAccountBO.waiveAmountDue(WaiveEnum.ALL);
        for (AccountActionDateEntity accountAction : customerAccountBO.getAccountActionDates()) {
            CustomerScheduleEntity accountActionDateEntity = (CustomerScheduleEntity) accountAction;
            for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountActionDateEntity
                    .getAccountFeesActionDetails()) {
                if (accountActionDateEntity.getInstallmentId().equals(Short.valueOf("1"))) {
                    assertEquals(new Money(), accountFeesActionDetailEntity.getFeeAmount());
                } else {
                    assertEquals(new Money("100"), accountFeesActionDetailEntity.getFeeAmount());
                }
            }
        }
    }

    public void testWaiveChargeOverDue() throws Exception {
        createInitialObjects();
        TestObjectFactory.flushandCloseSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        CustomerAccountBO customerAccountBO = group.getCustomerAccount();
        changeFirstInstallmentDateToPreviousDate(customerAccountBO);
        TestObjectFactory.updateObject(customerAccountBO);
        TestObjectFactory.flushandCloseSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        userContext = TestUtils.makeUser();
        customerAccountBO = group.getCustomerAccount();
        customerAccountBO.setUserContext(userContext);
        customerAccountBO.waiveAmountOverDue(WaiveEnum.ALL);
        for (AccountActionDateEntity accountAction : customerAccountBO.getAccountActionDates()) {
            CustomerScheduleEntity accountActionDateEntity = (CustomerScheduleEntity) accountAction;
            for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountActionDateEntity
                    .getAccountFeesActionDetails()) {
                if (accountActionDateEntity.getInstallmentId().equals(Short.valueOf("1"))) {
                    assertEquals(new Money(), accountFeesActionDetailEntity.getFeeAmount());
                } else {
                    assertEquals(new Money("100"), accountFeesActionDetailEntity.getFeeAmount());
                }
            }
        }
    }

    public void testApplyPeriodicFees() throws ApplicationException, SystemException {
        createInitialObjects();
        FeeBO fee = TestObjectFactory.createPeriodicAmountFee("Periodic Fee", FeeCategory.LOAN, "100",
                RecurrenceType.WEEKLY, Short.valueOf("1"));
        AccountFeesEntity accountFeesEntity = new AccountFeesEntity(group.getCustomerAccount(), fee,
                ((AmountFeeBO) fee).getFeeAmount().getAmountDoubleValue(), null, null, new Date(System
                        .currentTimeMillis()));
        AccountTestUtils.addAccountFees(accountFeesEntity, group.getCustomerAccount());
        TestObjectFactory.updateObject(group);
        TestObjectFactory.flushandCloseSession();

        group = TestObjectFactory.getGroup(group.getCustomerId());

        CustomerScheduleEntity accountActionDateEntity = (CustomerScheduleEntity) group.getCustomerAccount()
                .getAccountActionDates().toArray()[0];

        Set<AccountFeesActionDetailEntity> feeDetailsSet = accountActionDateEntity.getAccountFeesActionDetails();

        List<Integer> feeList = new ArrayList<Integer>();
        for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : feeDetailsSet) {
            feeList.add(accountFeesActionDetailEntity.getAccountFeesActionDetailId());
        }
        group.getCustomerAccount().applyPeriodicFees();
        TestObjectFactory.flushandCloseSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        CustomerScheduleEntity firstInstallment = (CustomerScheduleEntity) group.getCustomerAccount()
                .getAccountActionDates().toArray()[0];
        for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : firstInstallment
                .getAccountFeesActionDetails()) {
            if (!feeList.contains(accountFeesActionDetailEntity.getAccountFeesActionDetailId())) {
                Assert.assertEquals("Periodic Fee", accountFeesActionDetailEntity.getFee().getFeeName());
                break;
            }
        }
        StaticHibernateUtil.closeSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
    }

    public void testRemoveFees() throws NumberFormatException, SystemException, ApplicationException {
        createInitialObjects();
        CustomerAccountBO customerAccountBO = group.getCustomerAccount();
        for (AccountFeesEntity accountFeesEntity : customerAccountBO.getAccountFees()) {
            FeeBO feesBO = accountFeesEntity.getFees();
            customerAccountBO.removeFees(feesBO.getFeeId(), Short.valueOf("1"));
        }
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        customerAccountBO = group.getCustomerAccount();

        checkActivity(new Money(), customerAccountBO);

        getInactiveFee();
    }

    private AccountFeesEntity getInactiveFee() {
        Set<AccountFeesEntity> fees = group.getCustomerAccount().getAccountFees();
        Assert.assertEquals(1, fees.size());
        AccountFeesEntity accountFeesEntity = fees.iterator().next();
        Assert.assertEquals(FeeStatus.INACTIVE, accountFeesEntity.getFeeStatusAsEnum());
        return accountFeesEntity;
    }

    public void testRemoveFeeWithNonActiveCustomer() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_PARTIAL, center);
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        customerAccountBO = group.getCustomerAccount();
        UserContext uc = TestUtils.makeUser();
        customerAccountBO.setUserContext(uc);
        for (AccountFeesEntity accountFeesEntity : customerAccountBO.getAccountFees()) {
            FeeBO feesBO = accountFeesEntity.getFees();
            customerAccountBO.removeFees(feesBO.getFeeId(), Short.valueOf("1"));
        }
        StaticHibernateUtil.commitTransaction();
        checkActivity(null, customerAccountBO);

        AccountFeesEntity accountFeesEntity = getInactiveFee();
        // TODO: race condition - will fail when run just at midnight
        Assert.assertEquals(DateUtils.getCurrentDateWithoutTimeStamp(), DateUtils
                .getDateWithoutTimeStamp(accountFeesEntity.getStatusChangeDate().getTime()));
    }

    private void checkActivity(final Money expectedAmount, final CustomerAccountBO customerAccountBO) {
        Set<CustomerActivityEntity> customerActivitySet = customerAccountBO.getCustomerActivitDetails();
        Assert.assertEquals(1, customerActivitySet.size());
        CustomerActivityEntity customerActivityEntity = customerActivitySet.iterator().next();
        Assert.assertEquals(1, customerActivityEntity.getPersonnel().getPersonnelId().intValue());
        Assert.assertEquals("Maintenance Fee removed", customerActivityEntity.getDescription());
        Assert.assertEquals(expectedAmount, customerActivityEntity.getAmount());
    }

    public void testUpdateAccountActivity() throws NumberFormatException, SystemException, ApplicationException {
        createInitialObjects();
        CustomerAccountBO customerAccountBO = group.getCustomerAccount();
        customerAccountBO.updateAccountActivity(null, null, new Money("222"), null, Short.valueOf("1"),
                "Mainatnence Fee removed");
        TestObjectFactory.updateObject(customerAccountBO);
        group = TestObjectFactory.getGroup(group.getCustomerId());
        customerAccountBO = group.getCustomerAccount();
        Set<CustomerActivityEntity> customerActivitySet = customerAccountBO.getCustomerActivitDetails();
        for (CustomerActivityEntity customerActivityEntity : customerActivitySet) {
            Assert.assertEquals(1, customerActivityEntity.getPersonnel().getPersonnelId().intValue());
            Assert.assertEquals("Mainatnence Fee removed", customerActivityEntity.getDescription());
            Assert.assertEquals("222.0", customerActivityEntity.getAmount().toString());
        }
    }

    public void testRegenerateFutureInstallments() throws Exception {
        createCenter();
        java.util.Date currentDate = DateUtils.getCurrentDateWithoutTimeStamp();
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        AccountActionDateEntity accountActionDateEntity = center.getCustomerAccount().getDetailsOfNextInstallment();
        short nextInstallmentId = 0;
        if (accountActionDateEntity != null) {
            if (accountActionDateEntity.getActionDate().compareTo(currentDate) == 0) {
                nextInstallmentId = (short) (accountActionDateEntity.getInstallmentId().intValue() + 1);
            } else {
                nextInstallmentId = (short) accountActionDateEntity.getInstallmentId().intValue();
            }
        }
        MeetingBO meeting = center.getCustomerMeeting().getMeeting();
        meeting.getMeetingDetails().setRecurAfter(Short.valueOf("2"));
        meeting.setMeetingStartDate(accountActionDateEntity.getActionDate());
        List<java.util.Date> meetingDates = null;

        meetingDates = meeting.getAllDates((short) 10);
        meetingDates.remove(0);
        center.getCustomerAccount().regenerateFutureInstallments(nextInstallmentId);
        TestObjectFactory.updateObject(center);

        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        for (AccountActionDateEntity actionDateEntity : center.getCustomerAccount().getAccountActionDates()) {
            if (actionDateEntity.getInstallmentId().equals(Short.valueOf("2"))) {
                Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()),
                        DateUtils.getDateWithoutTimeStamp(meetingDates.get(0).getTime()));
            } else if (actionDateEntity.getInstallmentId().equals(Short.valueOf("3"))) {
                Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()),
                        DateUtils.getDateWithoutTimeStamp(meetingDates.get(1).getTime()));
            }
        }
    }

    public void testRegenerateFutureInstallmentsForGroup() throws Exception {
        createInitialObjects();
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        MeetingBO meeting = center.getCustomerMeeting().getMeeting();
        meeting.getMeetingDetails().setRecurAfter(Short.valueOf("2"));

        List<java.util.Date> meetingDates = meeting.getAllDates((short) 10);
        meetingDates.remove(0);
        group.getCustomerAccount().regenerateFutureInstallments((short) 2);
        TestObjectFactory.updateObject(center);
        TestObjectFactory.updateObject(group);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        for (AccountActionDateEntity actionDateEntity : group.getCustomerAccount().getAccountActionDates()) {
            if (actionDateEntity.getInstallmentId().equals(Short.valueOf("2"))) {
                Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()),
                        DateUtils.getDateWithoutTimeStamp(meetingDates.get(0).getTime()));
            } else if (actionDateEntity.getInstallmentId().equals(Short.valueOf("3"))) {
                Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()),
                        DateUtils.getDateWithoutTimeStamp(meetingDates.get(1).getTime()));
            }
        }
    }

    public void testRegenerateFutureInstallmentsWithAccountClosed() throws ApplicationException, SystemException {
        createInitialObjects();
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        Date installment2ndDate = null;
        Date installment3ndDate = null;
        for (AccountActionDateEntity actionDateEntity : group.getCustomerAccount().getAccountActionDates()) {
            if (actionDateEntity.getInstallmentId().equals(Short.valueOf("2"))) {
                installment2ndDate = actionDateEntity.getActionDate();
            } else if (actionDateEntity.getInstallmentId().equals(Short.valueOf("3"))) {
                installment3ndDate = actionDateEntity.getActionDate();
            }
        }
        MeetingBO meeting = center.getCustomerMeeting().getMeeting();
        meeting.getMeetingDetails().setRecurAfter(Short.valueOf("2"));
        CustomerStatusEntity customerStatusEntity = new CustomerStatusEntity(CustomerStatus.GROUP_CLOSED);
        CustomerBOTestUtils.setCustomerStatus(group, customerStatusEntity);
        group.getCustomerAccount().regenerateFutureInstallments((short) 2);
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());

        for (AccountActionDateEntity actionDateEntity : group.getCustomerAccount().getAccountActionDates()) {
            if (actionDateEntity.getInstallmentId().equals(Short.valueOf("2"))) {
                Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(installment2ndDate.getTime()), DateUtils
                        .getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
            } else if (actionDateEntity.getInstallmentId().equals(Short.valueOf("3"))) {
                Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(installment3ndDate.getTime()), DateUtils
                        .getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
            }
        }
    }

    public void testGenerateMeetingsForNextSet() throws Exception {
        createInitialObjects();
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        int lastInstallmentId = center.getCustomerAccount().getAccountActionDates().size();
        AccountActionDateEntity accountActionDateEntity = center.getCustomerAccount().getAccountActionDate(
                (short) lastInstallmentId);
        center.getCustomerAccount().generateNextSetOfMeetingDates();
        MeetingBO meetingBO = center.getCustomerMeeting().getMeeting();
        meetingBO.setMeetingStartDate(accountActionDateEntity.getActionDate());
        List<java.util.Date> meetingDates = meetingBO.getAllDates(DateUtils.getLastDayOfYearAfterNextYear().getTime());
        meetingDates.remove(0);
        Date date = center.getCustomerAccount().getAccountActionDate((short) (lastInstallmentId + 1)).getActionDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(meetingDates.get(0));
        Assert.assertEquals(0, new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE), 0, 0, 0).compareTo(new GregorianCalendar(calendar2.get(Calendar.YEAR),
                calendar2.get(Calendar.MONTH), calendar2.get(Calendar.DATE), 0, 0, 0)));

    }

    public void testApplyMiscCharge() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        customerAccountBO = group.getCustomerAccount();
        UserContext uc = TestUtils.makeUser();
        customerAccountBO.setUserContext(uc);
        customerAccountBO.applyCharge(Short.valueOf("-1"), new Double("33"));
        Money amount = new Money();
        for (AccountActionDateEntity accountActionDateEntity : customerAccountBO.getAccountActionDates()) {
            CustomerScheduleEntity customerScheduleEntity = (CustomerScheduleEntity) accountActionDateEntity;
            if (customerScheduleEntity.getInstallmentId().equals(Short.valueOf("1"))) {
                amount = customerScheduleEntity.getMiscFee();
                Assert.assertEquals(new Money("33.0"), customerScheduleEntity.getMiscFee());
            }
        }
        if (customerAccountBO.getCustomerActivitDetails() != null) {
            CustomerActivityEntity customerActivityEntity = (CustomerActivityEntity) customerAccountBO
                    .getCustomerActivitDetails().toArray()[0];
            Assert.assertEquals(AccountConstants.MISC_FEES_APPLIED, customerActivityEntity.getDescription());
            Assert.assertEquals(amount, customerActivityEntity.getAmount());
        }
    }

    public void testApplyMiscChargeWithNonActiveCustomer() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_PARTIAL, center);
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        customerAccountBO = group.getCustomerAccount();
        UserContext uc = TestUtils.makeUser();
        customerAccountBO.setUserContext(uc);
        try {
            customerAccountBO.applyCharge(Short.valueOf("-1"), new Double("33"));
            Assert.assertFalse(false);
        } catch (AccountException e) {
            Assert.assertEquals(AccountConstants.MISC_CHARGE_NOT_APPLICABLE, e.getKey());
        }
    }

    public void testApplyMiscChargeWithFirstInstallmentPaid() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);

        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        customerAccountBO = group.getCustomerAccount();
        for (AccountActionDateEntity accountActionDateEntity : customerAccountBO.getAccountActionDates()) {
            CustomerScheduleEntity customerScheduleEntity = (CustomerScheduleEntity) accountActionDateEntity;
            if (customerScheduleEntity.getInstallmentId().equals(Short.valueOf("1"))) {
                customerScheduleEntity.setPaymentStatus(PaymentStatus.PAID);
            }
        }
        UserContext uc = TestUtils.makeUser();
        customerAccountBO.setUserContext(uc);
        customerAccountBO.applyCharge(Short.valueOf("-1"), new Double("33"));
        Money amount = new Money();
        for (AccountActionDateEntity accountActionDateEntity : customerAccountBO.getAccountActionDates()) {
            CustomerScheduleEntity customerScheduleEntity = (CustomerScheduleEntity) accountActionDateEntity;
            if (customerScheduleEntity.getInstallmentId().equals(Short.valueOf("2"))) {
                amount = customerScheduleEntity.getMiscFee();
                Assert.assertEquals(new Money("33.0"), customerScheduleEntity.getMiscFee());
            }
        }
        if (customerAccountBO.getCustomerActivitDetails() != null) {
            CustomerActivityEntity customerActivityEntity = (CustomerActivityEntity) customerAccountBO
                    .getCustomerActivitDetails().toArray()[0];
            Assert.assertEquals(AccountConstants.MISC_FEES_APPLIED, customerActivityEntity.getDescription());
            Assert.assertEquals(amount, customerActivityEntity.getAmount());
        }
    }

    public void testApplyPeriodicFee() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        customerAccountBO = group.getCustomerAccount();
        FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee("Periodic Fee", FeeCategory.ALLCUSTOMERS, "200",
                RecurrenceType.WEEKLY, Short.valueOf("2"));
        UserContext uc = TestUtils.makeUser();
        customerAccountBO.setUserContext(uc);
        customerAccountBO.applyCharge(periodicFee.getFeeId(), ((AmountFeeBO) periodicFee).getFeeAmount()
                .getAmountDoubleValue());
        StaticHibernateUtil.commitTransaction();
        Date lastAppliedDate = null;
        Money amount = new Money();
        for (AccountActionDateEntity accountActionDateEntity : customerAccountBO.getAccountActionDates()) {
            CustomerScheduleEntity customerScheduleEntity = (CustomerScheduleEntity) accountActionDateEntity;
            if (customerScheduleEntity.getInstallmentId().equals(Short.valueOf("1"))) {
                Assert.assertEquals(2, customerScheduleEntity.getAccountFeesActionDetails().size());
                amount = amount.add(new Money("200"));
                lastAppliedDate = customerScheduleEntity.getActionDate();
                for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : customerScheduleEntity
                        .getAccountFeesActionDetails()) {
                    if (accountFeesActionDetailEntity.getFee().getFeeName().equals("Periodic Fee")) {
                        Assert.assertEquals(new Double("200"), accountFeesActionDetailEntity.getFeeAmount()
                                .getAmountDoubleValue(), 0.0);
                    }
                }
            }
        }
        if (customerAccountBO.getCustomerActivitDetails() != null) {
            CustomerActivityEntity customerActivityEntity = (CustomerActivityEntity) customerAccountBO
                    .getCustomerActivitDetails().toArray()[0];
            Assert.assertEquals(periodicFee.getFeeName() + " applied", customerActivityEntity.getDescription());
            Assert.assertEquals(amount, customerActivityEntity.getAmount());
            AccountFeesEntity accountFeesEntity = customerAccountBO.getAccountFees(periodicFee.getFeeId());
            Assert.assertEquals(FeeStatus.ACTIVE.getValue(), accountFeesEntity.getFeeStatus());
            Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(lastAppliedDate.getTime()), DateUtils
                    .getDateWithoutTimeStamp(accountFeesEntity.getLastAppliedDate().getTime()));
        }
    }

    public void testApplyPeriodicFeeToPartialPending() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_PENDING, center);
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        customerAccountBO = group.getCustomerAccount();
        FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee("Periodic Fee", FeeCategory.ALLCUSTOMERS, "200",
                RecurrenceType.WEEKLY, Short.valueOf("2"));
        UserContext uc = TestUtils.makeUser();
        customerAccountBO.setUserContext(uc);

        customerAccountBO.applyCharge(periodicFee.getFeeId(), ((AmountFeeBO) periodicFee).getFeeAmount()
                .getAmountDoubleValue());
        StaticHibernateUtil.commitTransaction();
        AccountFeesEntity accountFeesEntity = customerAccountBO.getAccountFees(periodicFee.getFeeId());
        Assert.assertEquals(Short.valueOf("1"), accountFeesEntity.getFeeStatus());
        Assert.assertNull(accountFeesEntity.getLastAppliedDate());
        Assert.assertEquals(DateUtils.getCurrentDateWithoutTimeStamp(), DateUtils
                .getDateWithoutTimeStamp(accountFeesEntity.getStatusChangeDate().getTime()));
        Assert.assertEquals(1, customerAccountBO.getCustomerActivitDetails().size());

        customerAccountBO.removeFees(periodicFee.getFeeId(), Short.valueOf("1"));
        StaticHibernateUtil.commitTransaction();
        Assert.assertEquals(2, customerAccountBO.getCustomerActivitDetails().size());
        Assert.assertEquals(2, customerAccountBO.getAccountFees().size());
        for (AccountFeesEntity accountFees : customerAccountBO.getAccountFees()) {
            if (accountFees.getFees().getFeeName().equals("Periodic Fee")) {
                Assert.assertEquals(Short.valueOf("2"), accountFees.getFeeStatus());
                Assert.assertEquals(DateUtils.getCurrentDateWithoutTimeStamp(), DateUtils
                        .getDateWithoutTimeStamp(accountFees.getStatusChangeDate().getTime()));
            }
        }

        customerAccountBO.applyCharge(periodicFee.getFeeId(), ((AmountFeeBO) periodicFee).getFeeAmount()
                .getAmountDoubleValue());
        StaticHibernateUtil.commitTransaction();
        Assert.assertEquals(3, customerAccountBO.getCustomerActivitDetails().size());
        accountFeesEntity = customerAccountBO.getAccountFees(periodicFee.getFeeId());
        Assert.assertEquals(Short.valueOf("1"), accountFeesEntity.getFeeStatus());
        Assert.assertNull(accountFeesEntity.getLastAppliedDate());
        Assert.assertEquals(DateUtils.getCurrentDateWithoutTimeStamp(), DateUtils
                .getDateWithoutTimeStamp(accountFeesEntity.getStatusChangeDate().getTime()));
    }

    public void testApplyUpfrontFee() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        customerAccountBO = group.getCustomerAccount();
        FeeBO upfrontFee = TestObjectFactory.createOneTimeAmountFee("Upfront Fee", FeeCategory.ALLCUSTOMERS, "20",
                FeePayment.UPFRONT);
        UserContext uc = TestUtils.makeUser();
        customerAccountBO.setUserContext(uc);
        customerAccountBO.applyCharge(upfrontFee.getFeeId(), ((AmountFeeBO) upfrontFee).getFeeAmount()
                .getAmountDoubleValue());
        StaticHibernateUtil.commitTransaction();
        Date lastAppliedDate = null;
        Money amount = new Money("20");

        for (AccountActionDateEntity accountActionDateEntity : customerAccountBO.getAccountActionDates()) {
            CustomerScheduleEntity customerScheduleEntity = (CustomerScheduleEntity) accountActionDateEntity;
            if (customerScheduleEntity.getInstallmentId().equals(Short.valueOf("1"))) {
                Assert.assertEquals(2, customerScheduleEntity.getAccountFeesActionDetails().size());
                lastAppliedDate = customerScheduleEntity.getActionDate();
            }
        }

        if (customerAccountBO.getCustomerActivitDetails() != null) {
            CustomerActivityEntity customerActivityEntity = (CustomerActivityEntity) customerAccountBO
                    .getCustomerActivitDetails().toArray()[0];
            Assert.assertEquals(upfrontFee.getFeeName() + " applied", customerActivityEntity.getDescription());
            Assert.assertEquals(amount, customerActivityEntity.getAmount());
            AccountFeesEntity accountFeesEntity = customerAccountBO.getAccountFees(upfrontFee.getFeeId());
            Assert.assertEquals(FeeStatus.ACTIVE.getValue(), accountFeesEntity.getFeeStatus());
            Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(lastAppliedDate.getTime()), DateUtils
                    .getDateWithoutTimeStamp(accountFeesEntity.getLastAppliedDate().getTime()));
        }
    }

    public void testGetNextDueAmount() throws Exception {

        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createCenter("Center_Active_test", meeting);
        Assert.assertEquals(100.00, center.getCustomerAccount().getNextDueAmount().getAmountDoubleValue(), DELTA);
    }

    public void testGenerateMeetingSchedule() throws AccountException {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        List<FeeView> feeView = new ArrayList<FeeView>();
        FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee("Periodic Fee", FeeCategory.ALLCUSTOMERS, "100",
                RecurrenceType.WEEKLY, Short.valueOf("1"));
        feeView.add(new FeeView(userContext, periodicFee));
        FeeBO upfrontFee = TestObjectFactory.createOneTimeAmountFee("Upfront Fee", FeeCategory.ALLCUSTOMERS, "30",
                FeePayment.UPFRONT);
        feeView.add(new FeeView(userContext, upfrontFee));
        center = TestObjectFactory.createCenter("Center_Active_test", meeting, feeView);
        Date startDate = new Date(System.currentTimeMillis());
        for (AccountActionDateEntity accountActionDateEntity : center.getCustomerAccount().getAccountActionDates()) {
            if (accountActionDateEntity.getInstallmentId().equals(Short.valueOf("1"))) {
                Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(startDate.getTime()), DateUtils
                        .getDateWithoutTimeStamp(((CustomerScheduleEntity) accountActionDateEntity).getActionDate()
                                .getTime()));
                Assert.assertEquals(2, ((CustomerScheduleEntity) accountActionDateEntity).getAccountFeesActionDetails()
                        .size());
                for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : ((CustomerScheduleEntity) accountActionDateEntity)
                        .getAccountFeesActionDetails()) {

                    if (accountFeesActionDetailEntity.getFee().getFeeName().equals("Periodic Fee")) {
                        Assert.assertEquals(new Money("100.0"), accountFeesActionDetailEntity.getFeeAmount());
                    } else {
                        Assert.assertEquals("Upfront Fee", accountFeesActionDetailEntity.getFee().getFeeName());
                        Assert.assertEquals(new Money("30.0"), accountFeesActionDetailEntity.getFeeAmount());
                    }
                }
            } else if (accountActionDateEntity.getInstallmentId().equals(Short.valueOf("2"))) {
                Assert.assertEquals(1, ((CustomerScheduleEntity) accountActionDateEntity).getAccountFeesActionDetails()
                        .size());
                Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(incrementCurrentDate(7).getTime()), DateUtils
                        .getDateWithoutTimeStamp(((CustomerScheduleEntity) accountActionDateEntity).getActionDate()
                                .getTime()));
            }
            Assert.assertFalse(((CustomerScheduleEntity) accountActionDateEntity).isPaid());
        }
    }

    public void testGenerateMeetingScheduleWithRecurAfterEveryTwoWeeks() throws AccountException {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY,
                EVERY_SECOND_WEEK, CUSTOMER_MEETING));
        List<FeeView> feeView = new ArrayList<FeeView>();
        FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee("Periodic Fee", FeeCategory.ALLCUSTOMERS, "100",
                RecurrenceType.WEEKLY, Short.valueOf("1"));
        feeView.add(new FeeView(userContext, periodicFee));
        FeeBO upfrontFee = TestObjectFactory.createOneTimeAmountFee("Upfront Fee", FeeCategory.ALLCUSTOMERS, "30",
                FeePayment.UPFRONT);
        feeView.add(new FeeView(userContext, upfrontFee));
        center = TestObjectFactory.createCenter("Center_Active_test", meeting, feeView);
        Date startDate = new Date(System.currentTimeMillis());
        for (AccountActionDateEntity accountActionDateEntity : center.getCustomerAccount().getAccountActionDates()) {
            if (accountActionDateEntity.getInstallmentId().equals(Short.valueOf("1"))) {
                Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(startDate.getTime()), DateUtils
                        .getDateWithoutTimeStamp(((CustomerScheduleEntity) accountActionDateEntity).getActionDate()
                                .getTime()));
                Assert.assertEquals(2, ((CustomerScheduleEntity) accountActionDateEntity).getAccountFeesActionDetails()
                        .size());
                for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : ((CustomerScheduleEntity) accountActionDateEntity)
                        .getAccountFeesActionDetails()) {

                    if (accountFeesActionDetailEntity.getFee().getFeeName().equals("Periodic Fee")) {
                        Assert.assertEquals(new Money("100.0"), accountFeesActionDetailEntity.getFeeAmount());
                    } else {
                        Assert.assertEquals("Upfront Fee", accountFeesActionDetailEntity.getFee().getFeeName());
                        Assert.assertEquals(new Money("30.0"), accountFeesActionDetailEntity.getFeeAmount());
                    }
                }
            } else if (accountActionDateEntity.getInstallmentId().equals(Short.valueOf("2"))) {
                Assert.assertEquals(1, ((CustomerScheduleEntity) accountActionDateEntity).getAccountFeesActionDetails()
                        .size());
                Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(incrementCurrentDate(14).getTime()), DateUtils
                        .getDateWithoutTimeStamp(((CustomerScheduleEntity) accountActionDateEntity).getActionDate()
                                .getTime()));
            }
            Assert.assertFalse(((CustomerScheduleEntity) accountActionDateEntity).isPaid());
        }
    }

    public void testActivityForMultiplePayments() throws Exception {

        // setup
        createCenter();
        CustomerAccountBO customerAccount = center.getCustomerAccount();
        Assert.assertNotNull(customerAccount);
        Date transactionDate = incrementCurrentDate(14);
        final Money paymentForThisInstallmentAndLastTwoInstallmentsInArrears = new Money(TestObjectFactory
                .getMFICurrency(), "300.0");
        List<AccountActionDateEntity> dueActionDates = TestObjectFactory.getDueActionDatesForAccount(customerAccount
                .getAccountId(), transactionDate);
        Assert.assertEquals("The size of the due insallments is ", dueActionDates.size(), 3);

        PaymentData accountPaymentDataView = TestObjectFactory.getCustomerAccountPaymentDataView(dueActionDates,
                paymentForThisInstallmentAndLastTwoInstallmentsInArrears, null, center.getPersonnel(), "3424324", Short
                        .valueOf("1"), transactionDate, transactionDate);

        center = TestObjectFactory.getCustomer(center.getCustomerId());
        customerAccount = center.getCustomerAccount();

        // exercise test
        customerAccount.applyPaymentWithPersist(accountPaymentDataView);
        StaticHibernateUtil.commitTransaction();

        // verification
        assertThat("The size of the payments done is", customerAccount.getAccountPayments().size(), is(1));
        assertThat("The size of the due insallments after payment is", TestObjectFactory.getDueActionDatesForAccount(
                customerAccount.getAccountId(), transactionDate).size(), is(0));
        assertThat(customerAccount.getCustomerActivitDetails().size(), is(1));

        assertThat(customerAccount.getAccountPayments().size(), is(1));
        for (AccountPaymentEntity accountPayment : customerAccount.getAccountPayments()) {
            assertThat(accountPayment.getAccountTrxns().size(), is(3));
            for (AccountTrxnEntity accountTrxnEntity : accountPayment.getAccountTrxns()) {
                assertThat(accountTrxnEntity.getFinancialTransactions().size(), is(2));
            }
        }
    }

    public void testGenerateMeetingScheduleWhenFirstTwoMeeingDatesOfCenterIsPassed() throws ApplicationException,
            SystemException {
        MeetingBO meetingBO = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY,
                EVERY_WEEK, CUSTOMER_MEETING));
        center = TestObjectFactory.createCenter("Center_Active_test", meetingBO);
        changeAllInstallmentDateToPreviousDate(center.getCustomerAccount(), 14);
        center.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        java.util.Date nextMeetingDate = center.getCustomerAccount().getNextMeetingDate();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        for (AccountActionDateEntity actionDateEntity : group.getCustomerAccount().getAccountActionDates()) {
            if (actionDateEntity.getInstallmentId().equals(Short.valueOf("1"))) {
                Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()),
                        DateUtils.getDateWithoutTimeStamp(nextMeetingDate.getTime()));
            }
        }
    }

    public void testGenerateMeetingScheduleForGroupWhenMeeingDatesOfCenterIsPassed() throws ApplicationException,
            SystemException {
        MeetingBO meetingBO = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY,
                EVERY_WEEK, CUSTOMER_MEETING));
        center = TestObjectFactory.createCenter("Center_Active_test", meetingBO);
        changeFirstInstallmentDateToPreviousDate(center.getCustomerAccount());
        center.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        java.util.Date nextMeetingDate = center.getCustomerAccount().getNextMeetingDate();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        for (AccountActionDateEntity actionDateEntity : group.getCustomerAccount().getAccountActionDates()) {
            if (actionDateEntity.getInstallmentId().equals(Short.valueOf("1"))) {
                Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()),
                        DateUtils.getDateWithoutTimeStamp(nextMeetingDate.getTime()));
            }
        }
    }

    public void testAccountTrxnsAreZeroWhenOnlyCustomerAccountFeesAreDueForMultipleInstallments() throws Exception {
        createCenter();
        CustomerAccountBO customerAccount = center.getCustomerAccount();

        Date transactionDate = incrementCurrentDate(14);
        PaymentData paymentData = PaymentData.createPaymentData(new Money("300"), center.getPersonnel(), Short
                .valueOf("1"), transactionDate);
        paymentData.setCustomer(center);

        customerAccount.makePayment(paymentData);
        // Assert.assertEquals(expectedErrorMessage, actualErrorMessage);

    }

    public void testAccountExceptionThrownForAZeroCustomerAccountPayment() throws Exception {
        createCenter();
        String expectedErrorMessage = "Attempting to pay a customer account balance of zero for customer: "
                + center.getGlobalCustNum();
        verifyExpectedMessageThrown(center, new Money("0.0"), 1, expectedErrorMessage);
    }

    public void testAccountExceptionThrownForAPaymentNotEqualToTheTotalOutstandingCustomerAccountAmount()
            throws Exception {
        createCenter();
        String expectedErrorMessage = "Attempting to pay a customer account balance that does not equal the total outstanding amount for customer: "
                + center.getGlobalCustNum();
        verifyExpectedMessageThrown(center, new Money("299.99"), 14, expectedErrorMessage);
    }

    public void testAccountExceptionThrownForAPaymentWithNoOutstandingCustomerAccountInstallments() throws Exception {

        createCenter();
        verifyExpectedMessageThrown(center, new Money("8.54"), -2, center.getCustomerAccount().getGlobalAccountNum());
    }

    private void verifyExpectedMessageThrown(final CustomerBO customer, final Money paymentAmount,
            final Integer numberOfDaysForward, final String expectedErrorMessage) throws Exception {
        CustomerAccountBO customerAccount = customer.getCustomerAccount();

        Date transactionDate = incrementCurrentDate(numberOfDaysForward);
        PaymentData paymentData = PaymentData.createPaymentData(paymentAmount, customer.getPersonnel(), Short
                .valueOf("1"), transactionDate);
        paymentData.setCustomer(customer);

        String actualErrorMessage = "No Error Message";
        try {
            customerAccount.makePayment(paymentData);
        } catch (AccountException e) {
            actualErrorMessage = (String) e.getValues()[0];
        }
        Assert.assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    public void testTrxnDetailEntityObjectsForMultipleInstallmentsWhenOnlyCustomerAccountFeesAreDue() throws Exception {
        createCenter();
        CustomerAccountBO customerAccount = center.getCustomerAccount();

        Date transactionDate = incrementCurrentDate(14);
        PaymentData paymentData = PaymentData.createPaymentData(new Money("300"), center.getPersonnel(), Short
                .valueOf("1"), transactionDate);
        paymentData.setCustomer(center);
        customerAccount.applyPaymentWithPersist(paymentData);
        StaticHibernateUtil.commitTransaction();

        if (customerAccount.getAccountPayments() != null && customerAccount.getAccountPayments().size() == 1) {
            for (AccountPaymentEntity accountPaymentEntity : customerAccount.getAccountPayments()) {

                final Money zeroAmount = new Money("0.0");
                final Money OneHundredAmount = new Money("100.0");

                if (accountPaymentEntity.getAccountTrxns() != null
                        && accountPaymentEntity.getAccountTrxns().size() == 3) {
                    for (AccountTrxnEntity accountTrxnEntity : accountPaymentEntity.getAccountTrxns()) {
                        CustomerTrxnDetailEntity customerTrxnDetailEntity = (CustomerTrxnDetailEntity) accountTrxnEntity;
                        Assert.assertEquals(zeroAmount, customerTrxnDetailEntity.getAmount());
                        Assert.assertEquals(zeroAmount, customerTrxnDetailEntity.getTotalAmount());
                        Assert.assertEquals(zeroAmount, customerTrxnDetailEntity.getMiscFeeAmount());
                        Assert.assertEquals(zeroAmount, customerTrxnDetailEntity.getMiscPenaltyAmount());

                        if (customerTrxnDetailEntity.getFeesTrxnDetails() != null
                                && customerTrxnDetailEntity.getFeesTrxnDetails().size() == 1) {
                            for (FeesTrxnDetailEntity feesTrxnDetailEntity : customerTrxnDetailEntity
                                    .getFeesTrxnDetails()) {
                                Assert.assertEquals(OneHundredAmount, feesTrxnDetailEntity.getFeeAmount());
                            }
                        } else {
                            throw new Exception("Expected one FeesTrxnDetailEntity, found none or more than one");
                        }
                    }
                } else {
                    throw new Exception("Expected three CustomerTrxnDetailEntity, found none or not three");
                }
            }
        } else {
            throw new Exception("Expected one AccountPaymentEntity, found none or more than one");
        }
    }

    public void testTrxnDetailEntityObjectsForMultipleInstallmentsWhenOnlyCustomerAccountChargesAreDue()
            throws Exception {
        
        MeetingBO weeklyMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();
        center = new CenterBuilder().withMeeting(weeklyMeeting).withName("Center").withOffice(sampleBranchOffice())
                .withLoanOfficer(testUser()).build();
        IntegrationTestObjectMother.saveCustomer(center);
        
        CustomerAccountBO customerAccount = center.getCustomerAccount();

        final Money fiftyAmount = new Money("50.0");
        final Money seventyAmount = new Money("70.0");
        final Money oneHundredTwentyAmount = new Money("120.0");
        final Money twoHundredFortyAmount = new Money("240.0");
        for (AccountActionDateEntity accountActionDateEntity : customerAccount.getAccountActionDates()) {
            CustomerScheduleEntity customerSchedule = (CustomerScheduleEntity) accountActionDateEntity;
            if (customerSchedule.getInstallmentId() != 2) {
                customerSchedule.setMiscFee(fiftyAmount);
                customerSchedule.setMiscPenalty(seventyAmount);
            }
        }
        Date transactionDate = incrementCurrentDate(14);
        PaymentData paymentData = PaymentData.createPaymentData(twoHundredFortyAmount, center.getPersonnel(), Short
                .valueOf("1"), transactionDate);
        paymentData.setCustomer(center);
        customerAccount.applyPaymentWithPersist(paymentData);
        StaticHibernateUtil.commitTransaction();

        if (customerAccount.getAccountPayments() != null && customerAccount.getAccountPayments().size() == 1) {
            for (AccountPaymentEntity accountPaymentEntity : customerAccount.getAccountPayments()) {

                final Money zeroAmount = new Money(accountPaymentEntity.getAmount().getCurrency(), "0.0");

                if (accountPaymentEntity.getAccountTrxns() != null
                        && accountPaymentEntity.getAccountTrxns().size() == 3) {
                    for (AccountTrxnEntity accountTrxnEntity : accountPaymentEntity.getAccountTrxns()) {
                        CustomerTrxnDetailEntity customerTrxnDetailEntity = (CustomerTrxnDetailEntity) accountTrxnEntity;

                        if (customerTrxnDetailEntity.getInstallmentId() != 2) {
                            Assert.assertEquals(oneHundredTwentyAmount, customerTrxnDetailEntity.getAmount());
                            Assert.assertEquals(oneHundredTwentyAmount, customerTrxnDetailEntity.getTotalAmount());
                            Assert.assertEquals(fiftyAmount, customerTrxnDetailEntity.getMiscFeeAmount());
                            Assert.assertEquals(seventyAmount, customerTrxnDetailEntity.getMiscPenaltyAmount());
                        } else {
                            Assert.assertEquals(zeroAmount, customerTrxnDetailEntity.getAmount());
                            Assert.assertEquals(zeroAmount, customerTrxnDetailEntity.getTotalAmount());
                            Assert.assertEquals(zeroAmount, customerTrxnDetailEntity.getMiscFeeAmount());
                            Assert.assertEquals(zeroAmount, customerTrxnDetailEntity.getMiscPenaltyAmount());
                        }
                        Assert.assertEquals(0, customerTrxnDetailEntity.getFeesTrxnDetails().size());
                    }
                } else {
                    throw new Exception("Expected three CustomerTrxnDetailEntity, found none or not three");
                }
            }
        } else {
            throw new Exception("Expected one AccountPaymentEntity, found none or more than one");
        }
    }

    public void testTrxnDetailEntityObjectsForMultipleInstallmentsWhenBothCustomerAccountChargesAndFeesAreDue()
            throws Exception {
        createCenter();
        FeeBO extraFee = TestObjectFactory.createPeriodicAmountFee("extra fee", FeeCategory.ALLCUSTOMERS, "5.5",
                RecurrenceType.WEEKLY, Short.valueOf("1"));

        CustomerAccountBO customerAccount = center.getCustomerAccount();
        AccountFeesEntity extraAccountFeesEntity = new AccountFeesEntity(customerAccount, extraFee, 11.66);
        customerAccount.getAccountFees().add(extraAccountFeesEntity);

        final Money eightAmount = new Money("8.0");
        final Money fiftyAmount = new Money("50.0");
        final Money seventyAmount = new Money("70.0");
        final Money oneHundredTwentyAmount = new Money("120.0");
        for (AccountActionDateEntity accountActionDateEntity : customerAccount.getAccountActionDates()) {
            CustomerScheduleEntity customerSchedule = (CustomerScheduleEntity) accountActionDateEntity;
            if (customerSchedule.getInstallmentId() == 2) {
                customerSchedule.setMiscFee(fiftyAmount);
                customerSchedule.setMiscPenalty(seventyAmount);
            }
            if (customerSchedule.getInstallmentId() == 3) {
                applyPeriodicFees(customerSchedule, extraAccountFeesEntity.getFees().getFeeId(), new Money("8"));
            }
        }
        Date transactionDate = incrementCurrentDate(14);
        PaymentData paymentData = PaymentData.createPaymentData(new Money("428"), center.getPersonnel(), Short
                .valueOf("1"), transactionDate);
        paymentData.setCustomer(center);
        customerAccount.applyPaymentWithPersist(paymentData);
        StaticHibernateUtil.commitTransaction();

        if (customerAccount.getAccountPayments() != null && customerAccount.getAccountPayments().size() == 1) {
            for (AccountPaymentEntity accountPaymentEntity : customerAccount.getAccountPayments()) {

                final Money zeroAmount = new Money(accountPaymentEntity.getAmount().getCurrency(), "0.0");
                final Money OneHundredAmount = new Money(accountPaymentEntity.getAmount().getCurrency(), "100.0");

                if (accountPaymentEntity.getAccountTrxns() != null
                        && accountPaymentEntity.getAccountTrxns().size() == 3) {
                    for (AccountTrxnEntity accountTrxnEntity : accountPaymentEntity.getAccountTrxns()) {
                        CustomerTrxnDetailEntity customerTrxnDetailEntity = (CustomerTrxnDetailEntity) accountTrxnEntity;

                        if (customerTrxnDetailEntity.getInstallmentId() == 2) {
                            Assert.assertEquals(oneHundredTwentyAmount, customerTrxnDetailEntity.getAmount());
                            Assert.assertEquals(oneHundredTwentyAmount, customerTrxnDetailEntity.getTotalAmount());
                            Assert.assertEquals(fiftyAmount, customerTrxnDetailEntity.getMiscFeeAmount());
                            Assert.assertEquals(seventyAmount, customerTrxnDetailEntity.getMiscPenaltyAmount());
                        } else {
                            Assert.assertEquals(zeroAmount, customerTrxnDetailEntity.getAmount());
                            Assert.assertEquals(zeroAmount, customerTrxnDetailEntity.getTotalAmount());
                            Assert.assertEquals(zeroAmount, customerTrxnDetailEntity.getMiscFeeAmount());
                            Assert.assertEquals(zeroAmount, customerTrxnDetailEntity.getMiscPenaltyAmount());
                        }
                        if (customerTrxnDetailEntity.getFeesTrxnDetails() != null
                                && customerTrxnDetailEntity.getFeesTrxnDetails().size() < 3) {
                            for (FeesTrxnDetailEntity feesTrxnDetailEntity : customerTrxnDetailEntity
                                    .getFeesTrxnDetails()) {
                                if (feesTrxnDetailEntity.getAccountFees().getAccountFeeId() == extraAccountFeesEntity
                                        .getAccountFeeId()) {
                                    Assert.assertEquals(eightAmount, feesTrxnDetailEntity.getFeeAmount());
                                } else {
                                    Assert.assertEquals(OneHundredAmount, feesTrxnDetailEntity.getFeeAmount());
                                }
                            }
                        } else {
                            throw new Exception("Expected one FeesTrxnDetailEntity, found none or more than two");
                        }
                    }
                } else {
                    throw new Exception("Expected three CustomerTrxnDetailEntity, found none or not three");
                }
            }
        } else {
            throw new Exception("Expected one AccountPaymentEntity, found none or more than one");
        }
    }


    /*
     * Create a center with a default weekly maintenance fee of 100.
     */
    private void createCenter() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createCenter("Center", meeting);
    }

    private void changeFirstInstallmentDateToPreviousDate(final CustomerAccountBO customerAccountBO) {
        Calendar currentDateCalendar = new GregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
        currentDateCalendar = new GregorianCalendar(year, month, day - 1);
        for (AccountActionDateEntity accountActionDateEntity : customerAccountBO.getAccountActionDates()) {
            if (accountActionDateEntity.getInstallmentId().equals(Short.valueOf("1"))) {
                ((CustomerScheduleEntity) accountActionDateEntity).setActionDate(new java.sql.Date(currentDateCalendar
                        .getTimeInMillis()));
                break;
            }
        }
    }

    private void createInitialObjects() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
    }

    private void applyPayment() throws ServiceException, FinancialException, NumberFormatException,
            PersistenceException {
        client = TestObjectFactory.createClient("Client_Active_test", CustomerStatus.CLIENT_ACTIVE, group);
        customerAccountBO = client.getCustomerAccount();
        Date currentDate = new Date(System.currentTimeMillis());
        customerAccountBO.setUserContext(userContext);

        CustomerScheduleEntity accountAction = (CustomerScheduleEntity) customerAccountBO.getAccountActionDate(Short
                .valueOf("1"));
        accountAction.setMiscFeePaid(TestObjectFactory.getMoneyForMFICurrency(100));
        accountAction.setMiscPenaltyPaid(TestObjectFactory.getMoneyForMFICurrency(100));
        accountAction.setPaymentDate(currentDate);
        accountAction.setPaymentStatus(PaymentStatus.PAID);

        MasterPersistence masterPersistenceService = new MasterPersistence();

        AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(customerAccountBO, TestObjectFactory
                .getMoneyForMFICurrency(300), "1111", currentDate, new PaymentTypeEntity(Short.valueOf("1")), new Date(
                System.currentTimeMillis()));

        CustomerTrxnDetailEntity accountTrxnEntity = new CustomerTrxnDetailEntity(accountPaymentEntity,
                AccountActionTypes.PAYMENT, Short.valueOf("1"), accountAction.getActionDate(), TestObjectFactory
                        .getPersonnel(userContext.getId()), currentDate, TestObjectFactory.getMoneyForMFICurrency(300),
                "payment done", null, TestObjectFactory.getMoneyForMFICurrency(100), TestObjectFactory
                        .getMoneyForMFICurrency(100), masterPersistenceService);

        for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountAction.getAccountFeesActionDetails()) {
            CustomerAccountBOTestUtils.setFeeAmountPaid((CustomerFeeScheduleEntity) accountFeesActionDetailEntity,
                    TestObjectFactory.getMoneyForMFICurrency(100));
            FeesTrxnDetailEntity feeTrxn = new FeesTrxnDetailEntity(accountTrxnEntity, accountFeesActionDetailEntity
                    .getAccountFee(), accountFeesActionDetailEntity.getFeeAmount());
            accountTrxnEntity.addFeesTrxnDetail(feeTrxn);
        }
        accountPaymentEntity.addAccountTrxn(accountTrxnEntity);
        AccountTestUtils.addAccountPayment(accountPaymentEntity, customerAccountBO);
        TestObjectFactory.updateObject(customerAccountBO);
        TestObjectFactory.flushandCloseSession();
    }

    private Date incrementCurrentDate(final int noOfDays) {
        return new java.sql.Date(new DateTime().plusDays(noOfDays).toDate().getTime());
    }

    private void changeAllInstallmentDateToPreviousDate(final CustomerAccountBO customerAccountBO, final int noOfDays) {
        Calendar currentDateCalendar = new GregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
        currentDateCalendar = new GregorianCalendar(year, month, day - noOfDays);
        for (AccountActionDateEntity accountActionDateEntity : customerAccountBO.getAccountActionDates()) {
            ((CustomerScheduleEntity) accountActionDateEntity).setActionDate(new java.sql.Date(currentDateCalendar
                    .getTimeInMillis()));
        }
    }
}
