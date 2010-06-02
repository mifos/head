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
import org.joda.time.Days;
import org.junit.Assert;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountTestUtils;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.business.FeesTrxnDetailEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeDto;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.accounts.fees.util.helpers.FeeStatus;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.accounts.util.helpers.WaiveEnum;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.util.UserContext;

public class CustomerAccountBOIntegrationTest extends MifosIntegrationTestCase {

    public CustomerAccountBOIntegrationTest() throws Exception {
        super();
    }

    private static final double DELTA = 0.00000001;

    private CustomerAccountBO customerAccountBO;
    private CustomerBO center;
    private CustomerBO group;
    private CustomerBO client;
    private UserContext userContext;
    private List<Days> workingDays;
    private List<Holiday> holidays;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestDatabase.resetMySQLDatabase();
        userContext = TestObjectFactory.getContext();
        workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();
        holidays = new ArrayList<Holiday>();
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            TestObjectFactory.cleanUp(client);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(center);
        } catch (Exception e) {
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
                TestUtils.createMoney("100.0"), null, center.getPersonnel(), "3424324", Short
                        .valueOf("1"), transactionDate, transactionDate);

        center = TestObjectFactory.getCustomer(center.getCustomerId());
        customerAccount = center.getCustomerAccount();
        customerAccount.applyPaymentWithPersist(accountPaymentDataView);
        StaticHibernateUtil.commitTransaction();

        Assert.assertEquals(customerAccount.getCustomerActivitDetails().size(), 1);
        Assert.assertEquals("The size of the payments done is", customerAccount.getAccountPayments().size(), 1);
        Assert.assertEquals("The size of the due insallments after payment is", TestObjectFactory
                .getDueActionDatesForAccount(customerAccount.getAccountId(), transactionDate).size(), 0);

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
                TestUtils.createMoney("100.0"), null, center.getPersonnel(), "3424324", Short
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

        client = TestObjectFactory.createClient("Client_Active_test", CustomerStatus.CLIENT_PENDING, group);
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
            Assert.assertEquals("Misc Fee Adjusted", accntActionDate.getMiscFeePaid(), TestUtils.createMoney());
            Assert.assertEquals("Misc Penalty Adjusted", accntActionDate.getMiscPenaltyPaid(),TestUtils.createMoney());
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

        Assert.assertEquals(customerAccount.getCustomerActivitDetails().size(), 1);
        for (CustomerActivityEntity activity : customerAccount.getCustomerActivitDetails()) {
            Assert.assertEquals(transactionDate, activity.getCreatedDate());
        }
        Assert.assertEquals("The size of the payments done is", customerAccount.getAccountPayments().size(), 1);
        Assert.assertEquals("The size of the due insallments after payment is", TestObjectFactory
                .getDueActionDatesForAccount(customerAccount.getAccountId(), transactionDate).size(), 0);
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        UserContext uc = TestUtils.makeUser();
        customerAccount = center.getCustomerAccount();
        customerAccount.setUserContext(uc);
        customerAccount.applyCharge(Short.valueOf(AccountConstants.MISC_FEES), new Double("33"));
        for (AccountActionDateEntity accountActionDateEntity : customerAccount.getAccountActionDates()) {
            CustomerScheduleEntity customerSchEntity = (CustomerScheduleEntity) accountActionDateEntity;
            if (customerSchEntity.getInstallmentId().equals(Short.valueOf("2"))) {
                Assert.assertEquals(new Money(getCurrency(), "33.0"), customerSchEntity.getMiscFee());
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
                Assert.assertEquals(new Money(getCurrency()), accountActionDateEntity.getTotalFeeDueWithMiscFee());
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
                    assertEquals(new Money(getCurrency()), accountFeesActionDetailEntity.getFeeAmount());
                } else {
                    assertEquals(new Money(getCurrency(), "100"), accountFeesActionDetailEntity.getFeeAmount());
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
                    assertEquals(new Money(getCurrency()), accountFeesActionDetailEntity.getFeeAmount());
                } else {
                    assertEquals(new Money(getCurrency(), "100"), accountFeesActionDetailEntity.getFeeAmount());
                }
            }
        }
    }

    public void testRemoveFees() throws NumberFormatException, SystemException, ApplicationException {
        createInitialObjects();
        CustomerAccountBO customerAccountBO = group.getCustomerAccount();
        for (AccountFeesEntity accountFeesEntity : customerAccountBO.getAccountFees()) {
            FeeBO feesBO = accountFeesEntity.getFees();
            customerAccountBO.removeFeesAssociatedWithUpcomingAndAllKnownFutureInstallments(feesBO.getFeeId(), Short.valueOf("1"));
        }
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        group = TestObjectFactory.getGroup(group.getCustomerId());
        customerAccountBO = group.getCustomerAccount();

        checkActivity(new Money(getCurrency()), customerAccountBO);

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
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_PARTIAL, center);
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        customerAccountBO = group.getCustomerAccount();
        UserContext uc = TestUtils.makeUser();
        customerAccountBO.setUserContext(uc);
        for (AccountFeesEntity accountFeesEntity : customerAccountBO.getAccountFees()) {
            FeeBO feesBO = accountFeesEntity.getFees();
            customerAccountBO.removeFeesAssociatedWithUpcomingAndAllKnownFutureInstallments(feesBO.getFeeId(), Short.valueOf("1"));
        }
        StaticHibernateUtil.commitTransaction();
        checkActivity(null, customerAccountBO);

        AccountFeesEntity accountFeesEntity = getInactiveFee();
        // TODO: race condition - will fail when run just at midnight
        Assert.assertEquals(DateUtils.getCurrentDateWithoutTimeStamp(), DateUtils
                .getDateWithoutTimeStamp(accountFeesEntity.getStatusChangeDate().getTime()));
    }

    private void checkActivity(final Money expectedAmount, final CustomerAccountBO customerAccountBO) {
        List<CustomerActivityEntity> customerActivitySet = customerAccountBO.getCustomerActivitDetails();
        Assert.assertEquals(1, customerActivitySet.size());
        CustomerActivityEntity customerActivityEntity = customerActivitySet.get(0);
        Assert.assertEquals(1, customerActivityEntity.getPersonnel().getPersonnelId().intValue());
        Assert.assertEquals("Maintenance Fee removed", customerActivityEntity.getDescription());
        Assert.assertEquals(expectedAmount, customerActivityEntity.getAmount());
    }

    public void testUpdateAccountActivity() throws NumberFormatException, SystemException, ApplicationException {
        createInitialObjects();
        CustomerAccountBO customerAccountBO = group.getCustomerAccount();
        customerAccountBO.updateAccountActivity(null, null, new Money(getCurrency(), "222"), null, Short.valueOf("1"),
                "Mainatnence Fee removed");
        TestObjectFactory.updateObject(customerAccountBO);
        group = TestObjectFactory.getGroup(group.getCustomerId());
        customerAccountBO = group.getCustomerAccount();
        List<CustomerActivityEntity> customerActivitySet = customerAccountBO.getCustomerActivitDetails();
        for (CustomerActivityEntity customerActivityEntity : customerActivitySet) {
            Assert.assertEquals(1, customerActivityEntity.getPersonnel().getPersonnelId().intValue());
            Assert.assertEquals("Mainatnence Fee removed", customerActivityEntity.getDescription());
            Assert.assertEquals("222.0", customerActivityEntity.getAmount().toString());
        }
    }

    public void testRegenerateFutureInstallments() throws Exception {
        createCenter();
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        AccountActionDateEntity nextInstallment = center.getCustomerAccount().getDetailsOfNextInstallment();

        MeetingBO meeting = center.getCustomerMeeting().getMeeting();
        meeting.getMeetingDetails().setRecurAfter(Short.valueOf("2"));
        meeting.setMeetingStartDate(nextInstallment.getActionDate());
        List<java.util.Date> meetingDates = null;

        meetingDates = TestObjectFactory.getMeetingDates(center.getOfficeId(), meeting, 10);
        meetingDates.remove(0);
        center.getCustomerAccount().regenerateFutureInstallments(nextInstallment, workingDays, holidays);
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

    public void testApplyMiscCharge() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        customerAccountBO = group.getCustomerAccount();
        UserContext uc = TestUtils.makeUser();
        customerAccountBO.setUserContext(uc);
        customerAccountBO.applyCharge(Short.valueOf("-1"), new Double("33"));
        Money amount = new Money(getCurrency());
        for (AccountActionDateEntity accountActionDateEntity : customerAccountBO.getAccountActionDates()) {
            CustomerScheduleEntity customerScheduleEntity = (CustomerScheduleEntity) accountActionDateEntity;
            if (customerScheduleEntity.getInstallmentId().equals(Short.valueOf("1"))) {
                amount = customerScheduleEntity.getMiscFee();
                Assert.assertEquals(new Money(getCurrency(), "33.0"), customerScheduleEntity.getMiscFee());
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
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_PARTIAL, center);
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
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);

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
        Money amount = new Money(getCurrency());
        for (AccountActionDateEntity accountActionDateEntity : customerAccountBO.getAccountActionDates()) {
            CustomerScheduleEntity customerScheduleEntity = (CustomerScheduleEntity) accountActionDateEntity;
            if (customerScheduleEntity.getInstallmentId().equals(Short.valueOf("2"))) {
                amount = customerScheduleEntity.getMiscFee();
                Assert.assertEquals(new Money(getCurrency(), "33.0"), customerScheduleEntity.getMiscFee());
            }
        }
        if (customerAccountBO.getCustomerActivitDetails() != null) {
            CustomerActivityEntity customerActivityEntity = (CustomerActivityEntity) customerAccountBO
                    .getCustomerActivitDetails().toArray()[0];
            Assert.assertEquals(AccountConstants.MISC_FEES_APPLIED, customerActivityEntity.getDescription());
            Assert.assertEquals(amount, customerActivityEntity.getAmount());
        }
    }

    // replaced by unit tests in CustomerAccountBOTest. Remove when we're sure that unit tests cover this
    // functionality

//    public void testApplyPeriodicFee() throws Exception {
//        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
//                CUSTOMER_MEETING));
//        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
//        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
//        TestObjectFactory.flushandCloseSession();
//        center = TestObjectFactory.getCustomer(center.getCustomerId());
//        group = TestObjectFactory.getCustomer(group.getCustomerId());
//        customerAccountBO = group.getCustomerAccount();
//        FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee("Periodic Fee", FeeCategory.ALLCUSTOMERS, "200",
//                RecurrenceType.WEEKLY, Short.valueOf("2"));
//        UserContext uc = TestUtils.makeUser();
//        customerAccountBO.setUserContext(uc);
//
//        // exercise test
//        customerAccountBO.applyCharge(periodicFee.getFeeId(), ((AmountFeeBO) periodicFee).getFeeAmount()
//                .getAmountDoubleValue());
//        StaticHibernateUtil.commitTransaction();
//
//        // verification
//        Date lastAppliedDate = null;
//        for (AccountActionDateEntity accountActionDateEntity : customerAccountBO.getAccountActionDates()) {
//            CustomerScheduleEntity customerScheduleEntity = (CustomerScheduleEntity) accountActionDateEntity;
//            if (customerScheduleEntity.getInstallmentId() % 2 == 0) {
//                //Maintenance fee only applies to installments 2, 4, 6, ...
//                Assert.assertEquals(1, customerScheduleEntity.getAccountFeesActionDetails().size());
//            } else {
//                // Both weekly maintenance fee and weekly periodic fee apply to installments 1, 3, 5, ...
//                Assert.assertEquals(2, customerScheduleEntity.getAccountFeesActionDetails().size());
//            }
//            lastAppliedDate = customerScheduleEntity.getActionDate();
//            for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : customerScheduleEntity
//                    .getAccountFeesActionDetails()) {
//                if (accountFeesActionDetailEntity.getFee().getFeeName().equals("Periodic Fee")) {
//                    Assert.assertEquals(TestUtils.createMoney("200"), accountFeesActionDetailEntity.getFeeAmount());
//                }
//            }
//        }
//        customerAccountBO.getAccountFees();
//        for (AccountFeesEntity accountFee : customerAccountBO.getAccountFees()) {
//            if (accountFee.getFees().getFeeName().equals("Periodic Fee")) {
//                Assert.assertEquals()
//            }
//        }
//        if (customerAccountBO.getCustomerActivitDetails() != null) {
//            CustomerActivityEntity customerActivityEntity = (CustomerActivityEntity) customerAccountBO
//            .getCustomerActivitDetails().toArray()[0];
//            Assert.assertEquals(periodicFee.getFeeName() + " applied", customerActivityEntity.getDescription());
//            Assert.assertEquals(new Money(getCurrency(), "1000"), customerActivityEntity.getAmount());
//            AccountFeesEntity accountFeesEntity = customerAccountBO.getAccountFees(periodicFee.getFeeId());
//            Assert.assertEquals(FeeStatus.ACTIVE.getValue(), accountFeesEntity.getFeeStatus());
//            Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(lastAppliedDate.getTime()), DateUtils
//                    .getDateWithoutTimeStamp(accountFeesEntity.getLastAppliedDate().getTime()));
//        }
//    }

    public void testApplyPeriodicFeeToPartialPending() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));

        // The factory creates a "maint
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_PENDING, center);
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

        customerAccountBO.removeFeesAssociatedWithUpcomingAndAllKnownFutureInstallments(periodicFee.getFeeId(), Short.valueOf("1"));
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
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
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
        Money amount = new Money(getCurrency(), "20");

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
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        Assert.assertEquals(100.00, center.getCustomerAccount().getNextDueAmount().getAmountDoubleValue(), DELTA);
    }

    public void testGenerateMeetingSchedule() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        List<FeeDto> feeDto = new ArrayList<FeeDto>();
        FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee("Periodic Fee", FeeCategory.ALLCUSTOMERS, "100",
                RecurrenceType.WEEKLY, Short.valueOf("1"));
        feeDto.add(new FeeDto(userContext, periodicFee));
        FeeBO upfrontFee = TestObjectFactory.createOneTimeAmountFee("Upfront Fee", FeeCategory.ALLCUSTOMERS, "30",
                FeePayment.UPFRONT);
        feeDto.add(new FeeDto(userContext, upfrontFee));
        center = TestObjectFactory.createCenter("Center_Active_test", meeting, feeDto);
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
                        Assert.assertEquals(new Money(getCurrency(), "100.0"), accountFeesActionDetailEntity.getFeeAmount());
                    } else {
                        Assert.assertEquals("Upfront Fee", accountFeesActionDetailEntity.getFee().getFeeName());
                        Assert.assertEquals(new Money(getCurrency(), "30.0"), accountFeesActionDetailEntity.getFeeAmount());
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

    public void testGenerateMeetingScheduleWithRecurAfterEveryTwoWeeks() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY,
                EVERY_SECOND_WEEK, CUSTOMER_MEETING));
        List<FeeDto> feeDto = new ArrayList<FeeDto>();
        FeeBO periodicFee = TestObjectFactory.createPeriodicAmountFee("Periodic Fee", FeeCategory.ALLCUSTOMERS, "100",
                RecurrenceType.WEEKLY, Short.valueOf("1"));
        feeDto.add(new FeeDto(userContext, periodicFee));
        FeeBO upfrontFee = TestObjectFactory.createOneTimeAmountFee("Upfront Fee", FeeCategory.ALLCUSTOMERS, "30",
                FeePayment.UPFRONT);
        feeDto.add(new FeeDto(userContext, upfrontFee));
        center = TestObjectFactory.createCenter("Center_Active_test", meeting, feeDto);
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
                        Assert.assertEquals(new Money(getCurrency(), "100.0"), accountFeesActionDetailEntity.getFeeAmount());
                    } else {
                        Assert.assertEquals("Upfront Fee", accountFeesActionDetailEntity.getFee().getFeeName());
                        Assert.assertEquals(new Money(getCurrency(), "30.0"), accountFeesActionDetailEntity.getFeeAmount());
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
        final Money paymentForThisInstallmentAndLastTwoInstallmentsInArrears = TestUtils.createMoney("300.0");
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
        assertThat(customerAccount.getCustomerActivitDetails().size(), is(1));
        assertThat("The size of the payments done is", customerAccount.getAccountPayments().size(), is(1));
        assertThat("The size of the due insallments after payment is", TestObjectFactory.getDueActionDatesForAccount(
                customerAccount.getAccountId(), transactionDate).size(), is(0));

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
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meetingBO);
        changeAllInstallmentDateToPreviousDate(center.getCustomerAccount(), 14);
        center.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
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
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meetingBO);
        changeFirstInstallmentDateToPreviousDate(center.getCustomerAccount());
        center.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
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
        PaymentData paymentData = PaymentData.createPaymentData(new Money(getCurrency(), "300"), center.getPersonnel(), Short
                .valueOf("1"), transactionDate);
        paymentData.setCustomer(center);

        customerAccount.makePayment(paymentData);
        // Assert.assertEquals(expectedErrorMessage, actualErrorMessage);

    }

    public void testAccountExceptionThrownForAZeroCustomerAccountPayment() throws Exception {
        createCenter();
        String expectedErrorMessage = "Attempting to pay a customer account balance of zero for customer: "
                + center.getGlobalCustNum();
        verifyExpectedMessageThrown(center, new Money(getCurrency(), "0.0"), 1, expectedErrorMessage);
    }

    public void testAccountExceptionThrownForAPaymentNotEqualToTheTotalOutstandingCustomerAccountAmount()
            throws Exception {
        createCenter();
        verifyExpectedDetailMessageThrown(center, new Money(getCurrency(), "299.99"), 14, "errors.paymentmismatch");
    }

    public void testAccountExceptionThrownForAPaymentWithNoOutstandingCustomerAccountInstallments() throws Exception {
        createCenter();
        verifyExpectedMessageThrown(center, new Money(getCurrency(), "8.54"), -2,
                "Trying to pay account charges before the due date.");
    }

    private void verifyExpectedDetailMessageThrown(final CustomerBO customer, final Money paymentAmount,
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
            actualErrorMessage = e.getMessage();
        }
        Assert.assertEquals(expectedErrorMessage, actualErrorMessage);
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
        PaymentData paymentData = PaymentData.createPaymentData(new Money(getCurrency(), "300"), center.getPersonnel(), Short
                .valueOf("1"), transactionDate);
        paymentData.setCustomer(center);
        customerAccount.applyPaymentWithPersist(paymentData);
        StaticHibernateUtil.commitTransaction();

        if (customerAccount.getAccountPayments() != null && customerAccount.getAccountPayments().size() == 1) {
            for (AccountPaymentEntity accountPaymentEntity : customerAccount.getAccountPayments()) {

                final Money zeroAmount = new Money(getCurrency(), "0.0");
                final Money OneHundredAmount = new Money(getCurrency(), "100.0");

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
        center = new CenterBuilder().with(weeklyMeeting).withName("Center").with(sampleBranchOffice())
                .withLoanOfficer(testUser()).build();
        IntegrationTestObjectMother.createCenter((CenterBO)center, weeklyMeeting);

        CustomerAccountBO customerAccount = center.getCustomerAccount();

        final Money fiftyAmount = new Money(getCurrency(), "50.0");
        final Money seventyAmount = new Money(getCurrency(), "70.0");
        final Money oneHundredTwentyAmount = new Money(getCurrency(), "120.0");
        final Money twoHundredFortyAmount = new Money(getCurrency(), "240.0");
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

        final Money eightAmount = new Money(getCurrency(), "8.0");
        final Money fiftyAmount = new Money(getCurrency(), "50.0");
        final Money seventyAmount = new Money(getCurrency(), "70.0");
        final Money oneHundredTwentyAmount = new Money(getCurrency(), "120.0");
        for (AccountActionDateEntity accountActionDateEntity : customerAccount.getAccountActionDates()) {
            CustomerScheduleEntity customerSchedule = (CustomerScheduleEntity) accountActionDateEntity;
            if (customerSchedule.getInstallmentId() == 2) {
                customerSchedule.setMiscFee(fiftyAmount);
                customerSchedule.setMiscPenalty(seventyAmount);
            }
            if (customerSchedule.getInstallmentId() == 3) {
                CustomerAccountBOTestUtils.applyPeriodicFees(customerSchedule, extraAccountFeesEntity.getFees()
                        .getFeeId(), new Money(getCurrency(), "8"));
            }
        }
        Date transactionDate = incrementCurrentDate(14);
        PaymentData paymentData = PaymentData.createPaymentData(new Money(getCurrency(), "428"), center.getPersonnel(), Short
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

    public void testCustomerActivitDetailsSortingByDate()
            throws Exception {
        userContext = TestUtils.makeUser();
        createInitialObjects();
        client = TestObjectFactory.createClient("Client_Active_test", CustomerStatus.CLIENT_ACTIVE, group);
        customerAccountBO = client.getCustomerAccount();
        customerAccountBO.addCustomerActivity(new CustomerActivityEntity(customerAccountBO, center.getPersonnel(),
                TestUtils.createMoney(10), AccountConstants.PAYMENT_RCVD, new Date(System.currentTimeMillis() + 172800000)));
        customerAccountBO.addCustomerActivity(new CustomerActivityEntity(customerAccountBO, center.getPersonnel(),
                TestUtils.createMoney(20), AccountConstants.PAYMENT_RCVD, new Date(System.currentTimeMillis())));
        customerAccountBO.addCustomerActivity(new CustomerActivityEntity(customerAccountBO, center.getPersonnel(),
                TestUtils.createMoney(30), AccountConstants.PAYMENT_RCVD, new Date(System.currentTimeMillis() + 345600000)));
        customerAccountBO.update();
        StaticHibernateUtil.commitTransaction();
        TestObjectFactory.flushandCloseSession();
        customerAccountBO = TestObjectFactory.getCustomer(client.getCustomerId()).getCustomerAccount();
        UserContext uc = TestUtils.makeUser();
        customerAccountBO.setUserContext(uc);
        Assert.assertEquals(3, customerAccountBO.getCustomerActivitDetails().size());
        Assert.assertEquals(TestUtils.createMoney(30), customerAccountBO.getCustomerActivitDetails().get(0).getAmount());
        Assert.assertEquals(TestUtils.createMoney(10), customerAccountBO.getCustomerActivitDetails().get(1).getAmount());
        Assert.assertEquals(TestUtils.createMoney(20), customerAccountBO.getCustomerActivitDetails().get(2).getAmount());
    }

    /*
     * Create a center with a default weekly maintenance fee of 100.
     */
    private void createCenter() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
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

        MeetingBO weeklyMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();
        IntegrationTestObjectMother.saveMeeting(weeklyMeeting);

        center = new CenterBuilder().withNumberOfExistingCustomersInOffice(3).with(weeklyMeeting).withName("Center_Active_test").with(
                sampleBranchOffice()).withLoanOfficer(testUser()).build();
        IntegrationTestObjectMother.createCenter((CenterBO)center, weeklyMeeting);

        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
    }

    private void applyPayment() throws Exception {
        client = TestObjectFactory.createClient("Client_Active_test", CustomerStatus.CLIENT_ACTIVE, group);
        customerAccountBO = client.getCustomerAccount();
        Date currentDate = new Date(System.currentTimeMillis());
        customerAccountBO.setUserContext(userContext);

        CustomerScheduleEntity accountAction = (CustomerScheduleEntity) customerAccountBO.getAccountActionDate(Short
                .valueOf("1"));
        accountAction.setMiscFeePaid(TestUtils.createMoney(100));
        accountAction.setMiscPenaltyPaid(TestUtils.createMoney(100));
        accountAction.setPaymentDate(currentDate);
        accountAction.setPaymentStatus(PaymentStatus.PAID);

        MasterPersistence masterPersistenceService = new MasterPersistence();

        AccountPaymentEntity accountPaymentEntity = new AccountPaymentEntity(customerAccountBO,
                TestUtils.createMoney(300), "1111", currentDate, new PaymentTypeEntity(Short.valueOf("1")),
                new Date(System.currentTimeMillis()));

        CustomerTrxnDetailEntity accountTrxnEntity = new CustomerTrxnDetailEntity(accountPaymentEntity,
                AccountActionTypes.PAYMENT, Short.valueOf("1"), accountAction.getActionDate(), TestObjectFactory
                        .getPersonnel(userContext.getId()), currentDate, TestUtils.createMoney(300),
                "payment done", null, TestUtils.createMoney(100), TestUtils.createMoney(100),
                masterPersistenceService);

        for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountAction.getAccountFeesActionDetails()) {
            CustomerAccountBOTestUtils.setFeeAmountPaid((CustomerFeeScheduleEntity) accountFeesActionDetailEntity,
                    TestUtils.createMoney(100));
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
