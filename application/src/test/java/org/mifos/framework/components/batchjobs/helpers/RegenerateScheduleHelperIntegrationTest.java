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

package org.mifos.framework.components.batchjobs.helpers;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.MONTHLY;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.application.meeting.util.helpers.WeekDay.MONDAY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_SECOND_MONTH;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_SECOND_WEEK;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanBOTestUtils;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.business.CustomerAccountBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerBOTestUtils;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.InterestCalcType;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class RegenerateScheduleHelperIntegrationTest extends MifosIntegrationTestCase {

    public RegenerateScheduleHelperIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private MeetingBO meeting;

    private CustomerBO center;

    private CustomerBO client;

    private CustomerBO group;

    private AccountBO accountBO;

    private SavingsOfferingBO savingsOffering;

    private LoanOfferingBO loanOfferingBO;

    private CustomerBO client1;

    private CustomerBO client2;

    private SavingsBO savings;

    private UserContext userContext;

    PersonnelBO createdBy = null;

    RegenerateScheduleHelper regenerateScheduleHelper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        RegenerateScheduleTask regenerateScheduleTask = new RegenerateScheduleTask();
        regenerateScheduleHelper = (RegenerateScheduleHelper) regenerateScheduleTask.getTaskHelper();
        userContext = TestUtils.makeUser();
        createdBy = new PersonnelPersistence().getPersonnel(userContext.getId());
    }

    @Override
    public void tearDown() throws Exception {
        try {
            TestObjectFactory.cleanUp(savings);
            TestObjectFactory.cleanUp(accountBO);
            TestObjectFactory.cleanUp(client);
            TestObjectFactory.cleanUp(client1);
            TestObjectFactory.cleanUp(client2);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(center);
        } catch (Exception e) {
            // TODO Whoops, cleanup didnt work, reset db
            TestDatabase.resetMySQLDatabase();
        }
        regenerateScheduleHelper = null;
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testExcuteWithCustomerAccounts() throws NumberFormatException, SystemException, ApplicationException {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        CenterBO center1 = TestObjectFactory.createCenter("Center_Active_test1", meeting);
        GroupBO group1 = TestObjectFactory.createGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center1);
        client = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);
        ClientBO client2 = TestObjectFactory.createClient("client2", CustomerStatus.CLIENT_CLOSED, group);
        ClientBO client3 = TestObjectFactory.createClient("client3", CustomerStatus.CLIENT_CANCELLED, group1);
        center.getCustomerMeeting().getMeeting().getMeetingDetails().setRecurAfter(Short.valueOf("2"));
        CustomerBOTestUtils.setUpdatedFlag(center.getCustomerMeeting(), YesNoFlag.YES.getValue());

        List<java.util.Date> meetingDates = center.getCustomerMeeting().getMeeting().getAllDates((short) 10);
        meetingDates.remove(0);
        TestObjectFactory.updateObject(center);
        TestObjectFactory.flushandCloseSession();

        regenerateScheduleHelper.execute(System.currentTimeMillis());
        StaticHibernateUtil.closeSession();

        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        center1 = TestObjectFactory.getCenter(center1.getCustomerId());
        group1 = TestObjectFactory.getGroup(group1.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        client2 = TestObjectFactory.getClient(client2.getCustomerId());
        client3 = TestObjectFactory.getClient(client3.getCustomerId());

        for (AccountActionDateEntity actionDateEntity : center.getCustomerAccount().getAccountActionDates()) {
            if (actionDateEntity.getInstallmentId().equals(Short.valueOf("2"))) {
                Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(0).getTime()), DateUtils
                            .getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
            } else if (actionDateEntity.getInstallmentId().equals(Short.valueOf("3"))) {
                Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(1).getTime()), DateUtils
                            .getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
            }
        }

        for (AccountActionDateEntity actionDateEntity : group.getCustomerAccount().getAccountActionDates()) {
            if (actionDateEntity.getInstallmentId().equals(Short.valueOf("2"))) {
                Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(0).getTime()), DateUtils
                            .getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
            } else if (actionDateEntity.getInstallmentId().equals(Short.valueOf("3"))) {
                Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(1).getTime()), DateUtils
                            .getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
            }
        }
       Assert.assertEquals(YesNoFlag.NO.getValue(), center.getCustomerMeeting().getUpdatedFlag());
        TestObjectFactory.cleanUp(client3);
        TestObjectFactory.cleanUp(client2);
        TestObjectFactory.cleanUp(group1);
        TestObjectFactory.cleanUp(center1);
    }

    public void testExecuteWithLoanAccount() throws Exception {
        accountBO = getLoanAccount();
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        accountBO = (AccountBO) StaticHibernateUtil.getSessionTL().get(LoanBO.class, accountBO.getAccountId());

        center.getCustomerMeeting().getMeeting().getMeetingDetails().getMeetingRecurrence().setWeekDay(
                (WeekDaysEntity) new MasterPersistence().retrieveMasterEntity(WeekDay.THURSDAY.getValue(),
                        WeekDaysEntity.class, null));
        CustomerBOTestUtils.setUpdatedFlag(center.getCustomerMeeting(), YesNoFlag.YES.getValue());

        AccountActionDateEntity accountActionDateEntity = center.getCustomerAccount().getDetailsOfNextInstallment();
        center.getCustomerMeeting().getMeeting().setMeetingStartDate(accountActionDateEntity.getActionDate());

        TestObjectFactory.updateObject(center);
        TestObjectFactory.flushandCloseSession();
        regenerateScheduleHelper.execute(System.currentTimeMillis());

        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        accountBO = (AccountBO) StaticHibernateUtil.getSessionTL().get(AccountBO.class, accountBO.getAccountId());
        center.getCustomerMeeting().getMeeting().setMeetingStartDate(accountActionDateEntity.getActionDate());
        List<java.util.Date> meetingDates = center.getCustomerMeeting().getMeeting().getAllDates(10);

        for (AccountBO account : center.getAccounts()) {
            for (AccountActionDateEntity actionDateEntity : account.getAccountActionDates()) {
                if (actionDateEntity.getInstallmentId().equals(Short.valueOf("2"))) {
                    Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(1).getTime()), DateUtils
                                .getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
                } else if (actionDateEntity.getInstallmentId().equals(Short.valueOf("3"))) {
                    Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(2).getTime()), DateUtils
                                .getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
                }
            }
        }
        for (AccountBO account : group.getAccounts()) {
            for (AccountActionDateEntity actionDateEntity : account.getAccountActionDates()) {
                if (actionDateEntity.getInstallmentId().equals(Short.valueOf("2"))) {
                    Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(1).getTime()), DateUtils
                                .getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
                } else if (actionDateEntity.getInstallmentId().equals(Short.valueOf("3"))) {
                    Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(2).getTime()), DateUtils
                                .getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
                }
            }
        }
       Assert.assertEquals(YesNoFlag.NO.getValue(), center.getCustomerMeeting().getUpdatedFlag());

        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        accountBO = (AccountBO) StaticHibernateUtil.getSessionTL().get(AccountBO.class, accountBO.getAccountId());
    }

    public void testExecuteWithLoanAccountWithPastInstallmentsdPaid() throws Exception {
        accountBO = getLoanAccount();
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        accountBO = (AccountBO) StaticHibernateUtil.getSessionTL().get(LoanBO.class, accountBO.getAccountId());
        LoanBOTestUtils.setDisbursementDate(accountBO, offSetCurrentDate(21));
        accountBO.getAccountActionDate((short) 1).setPaymentStatus(PaymentStatus.PAID);
        accountBO.getAccountActionDate((short) 2).setPaymentStatus(PaymentStatus.PAID);
        accountBO.getAccountActionDate((short) 3).setPaymentStatus(PaymentStatus.PAID);

        center.getCustomerMeeting().getMeeting().getMeetingDetails().getMeetingRecurrence().setWeekDay(
                (WeekDaysEntity) new MasterPersistence().retrieveMasterEntity(WeekDay.THURSDAY.getValue(),
                        WeekDaysEntity.class, null));
        CustomerBOTestUtils.setUpdatedFlag(center.getCustomerMeeting(), YesNoFlag.YES.getValue());

        AccountActionDateEntity accountActionDateEntity = center.getCustomerAccount().getDetailsOfNextInstallment();
        center.getCustomerMeeting().getMeeting().setMeetingStartDate(accountActionDateEntity.getActionDate());

        TestObjectFactory.updateObject(center);
        TestObjectFactory.updateObject(accountBO);
        TestObjectFactory.flushandCloseSession();
        regenerateScheduleHelper.execute(System.currentTimeMillis());

        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        accountBO = (AccountBO) StaticHibernateUtil.getSessionTL().get(AccountBO.class, accountBO.getAccountId());
        List<java.util.Date> meetingDates = null;
        center.getCustomerMeeting().getMeeting().setMeetingStartDate(accountActionDateEntity.getActionDate());
        meetingDates = center.getCustomerMeeting().getMeeting().getAllDates((short) 10);

        for (AccountBO account : center.getAccounts()) {
            for (AccountActionDateEntity actionDateEntity : account.getAccountActionDates()) {
                if (actionDateEntity.getInstallmentId().equals(Short.valueOf("2"))) {
                    Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(1).getTime()), DateUtils
                                .getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
                } else if (actionDateEntity.getInstallmentId().equals(Short.valueOf("3"))) {
                    Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(2).getTime()), DateUtils
                                .getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
                }
            }
        }
        for (AccountBO account : group.getAccounts()) {
            for (AccountActionDateEntity actionDateEntity : account.getAccountActionDates()) {
                if (actionDateEntity.getInstallmentId().equals(Short.valueOf("2"))) {
                    Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(1).getTime()), DateUtils
                                .getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
                } else if (actionDateEntity.getInstallmentId().equals(Short.valueOf("3"))) {
                    Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(2).getTime()), DateUtils
                                .getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
                }
            }
        }
       Assert.assertEquals(YesNoFlag.NO.getValue(), center.getCustomerMeeting().getUpdatedFlag());

        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        accountBO = (AccountBO) StaticHibernateUtil.getSessionTL().get(AccountBO.class, accountBO.getAccountId());
    }

    public void testExecuteWithSavingsAccount() throws Exception {
        savings = getSavingAccount();
        TestObjectFactory.flushandCloseSession();
        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        client1 = TestObjectFactory.getCustomer(client1.getCustomerId());
        client2 = TestObjectFactory.getCustomer(client2.getCustomerId());

        center.getCustomerMeeting().getMeeting().getMeetingDetails().setRecurAfter(Short.valueOf("1"));
        CustomerBOTestUtils.setUpdatedFlag(center.getCustomerMeeting(), YesNoFlag.YES.getValue());

        java.util.Date meetingStartDate = center.getCustomerMeeting().getMeeting().getMeetingStartDate();
        AccountActionDateEntity accountActionDateEntity = center.getCustomerAccount().getDetailsOfNextInstallment();
        center.getCustomerMeeting().getMeeting().setMeetingStartDate(accountActionDateEntity.getActionDate());

        List<java.util.Date> meetingDates = center.getCustomerMeeting().getMeeting().getAllDates(
                DateUtils.getLastDayOfNextYear());
        center.getCustomerMeeting().getMeeting().setMeetingStartDate(meetingStartDate);
        meetingDates.remove(0);

        TestObjectFactory.updateObject(center);
        TestObjectFactory.flushandCloseSession();
        regenerateScheduleHelper.execute(System.currentTimeMillis());
        TestObjectFactory.flushandCloseSession();

        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        client1 = TestObjectFactory.getCustomer(client1.getCustomerId());
        client2 = TestObjectFactory.getCustomer(client2.getCustomerId());

        for (AccountBO account : center.getAccounts()) {
            for (AccountActionDateEntity actionDateEntity : account.getAccountActionDates()) {
                if (actionDateEntity.getInstallmentId().equals(Short.valueOf("2"))) {
                    Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(0).getTime()), DateUtils
                                .getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
                } else if (actionDateEntity.getInstallmentId().equals(Short.valueOf("3"))) {
                    Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(1).getTime()), DateUtils
                                .getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
                }
            }
        }
        for (AccountBO account : group.getAccounts()) {
            for (AccountActionDateEntity actionDateEntity : account.getAccountActionDates()) {
                if (actionDateEntity.getInstallmentId().equals(Short.valueOf("2"))) {
                    Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(0).getTime()), DateUtils
                                .getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
                } else if (actionDateEntity.getInstallmentId().equals(Short.valueOf("3"))) {
                    Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(1).getTime()), DateUtils
                                .getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
                }
            }
        }
        for (AccountBO account : client1.getAccounts()) {
            for (AccountActionDateEntity actionDateEntity : account.getAccountActionDates()) {
                if (actionDateEntity.getInstallmentId().equals(Short.valueOf("2"))) {
                    Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(0).getTime()), DateUtils
                                .getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
                } else if (actionDateEntity.getInstallmentId().equals(Short.valueOf("3"))) {
                    Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(1).getTime()), DateUtils
                                .getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
                }
            }
        }

        for (AccountBO account : client2.getAccounts()) {
            for (AccountActionDateEntity actionDateEntity : account.getAccountActionDates()) {
                if (actionDateEntity.getInstallmentId().equals(Short.valueOf("2"))) {
                    Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(0).getTime()), DateUtils
                                .getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
                } else if (actionDateEntity.getInstallmentId().equals(Short.valueOf("3"))) {
                    Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(meetingDates.get(1).getTime()), DateUtils
                                .getDateWithoutTimeStamp(actionDateEntity.getActionDate().getTime()));
                }
            }
        }

       Assert.assertEquals(YesNoFlag.NO.getValue(), center.getCustomerMeeting().getUpdatedFlag());

        TestObjectFactory.flushandCloseSession();

        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        client1 = TestObjectFactory.getCustomer(client1.getCustomerId());
        client2 = TestObjectFactory.getCustomer(client2.getCustomerId());
    }

    private SavingsBO getSavingAccount() throws Exception {
        MeetingBO meeting = TestObjectFactory.getNewMeeting(MONTHLY, EVERY_SECOND_MONTH, CUSTOMER_MEETING, MONDAY);
        meeting.setMeetingStartDate(new java.util.Date());
        meeting.getMeetingDetails().getMeetingRecurrence().setDayNumber(new Short("1"));
        TestObjectFactory.createMeeting(meeting);
        center = TestObjectFactory.createCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center);
        client1 = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);
        client2 = TestObjectFactory.createClient("client2", CustomerStatus.CLIENT_ACTIVE, group);
        MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY,
                EVERY_WEEK, CUSTOMER_MEETING));
        MeetingBO meetingIntPost = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY,
                EVERY_WEEK, CUSTOMER_MEETING));
        savingsOffering = TestObjectFactory.createSavingsProduct("SavingPrd1", ApplicableTo.GROUPS, new Date(System
                .currentTimeMillis()), PrdStatus.SAVINGS_ACTIVE, 300.0, RecommendedAmountUnit.PER_INDIVIDUAL, 24.0,
                200.0, 200.0, SavingsType.VOLUNTARY, InterestCalcType.MINIMUM_BALANCE, meetingIntCalc, meetingIntPost);
        SavingsBO savings = new SavingsBO(userContext, savingsOffering, group, AccountState.SAVINGS_ACTIVE,
                savingsOffering.getRecommendedAmount(), TestObjectFactory.getCustomFields());
        savings.save();
        StaticHibernateUtil.getTransaction().commit();
        return savings;
    }

    private AccountBO getLoanAccount() {
        Date startDate = new Date(System.currentTimeMillis());
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_SECOND_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createCenter("Center", meeting);
        group = TestObjectFactory.createGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        loanOfferingBO = TestObjectFactory.createLoanOffering(startDate, meeting);
        return TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                startDate, loanOfferingBO);
    }

    private java.sql.Date offSetCurrentDate(final int noOfDays) {
        Calendar currentDateCalendar = new GregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
        currentDateCalendar = new GregorianCalendar(year, month, day - noOfDays);
        return new java.sql.Date(currentDateCalendar.getTimeInMillis());
    }
    

    public void testCustomerMeetingChangeUpdatesAllInstallmentsForSavingsAndCustomerAccountsOfAClient()
            throws BatchJobException {

        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeeting(RecurrenceType.WEEKLY, Short
                .valueOf("1"), MeetingType.CUSTOMER_MEETING, WeekDay.THURSDAY));

        SavingsTestHelper helper = new SavingsTestHelper();
        SavingsOfferingBO nonIndividualSavingsOffering = helper.createSavingsOffering("Client Saving Product", "nisp");

        center = TestObjectFactory.createCenter(this.getClass().getSimpleName() + " Center", meeting);
        group = TestObjectFactory.createGroupUnderCenter(this.getClass().getSimpleName() + " Group",
                CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient(this.getClass().getSimpleName() + " Client",
                CustomerStatus.CLIENT_ACTIVE, group);
        SavingsBO nonIndividualSavingsAccount = createSavingsAccount(client, nonIndividualSavingsOffering);
        client.getAccounts().add(nonIndividualSavingsAccount);

        // move weekly center meeting
        MeetingBO newMeeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeeting(RecurrenceType.WEEKLY,
                Short.valueOf("1"), MeetingType.CUSTOMER_MEETING, WeekDay.SATURDAY));

        CustomerBOTestUtils.setUpdatedMeeting(center.getCustomerMeeting(), newMeeting);
        CustomerBOTestUtils.setUpdatedMeeting(group.getCustomerMeeting(), newMeeting);
        CustomerBOTestUtils.setUpdatedMeeting(client.getCustomerMeeting(), newMeeting);
        TestObjectFactory.updateObject(center);
        TestObjectFactory.updateObject(group);
        TestObjectFactory.updateObject(client);
        TestObjectFactory.flushandCloseSession();
        
        regenerateScheduleHelper.execute(System.currentTimeMillis());
        StaticHibernateUtil.closeSession();

        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        meeting = TestObjectFactory.getObject(MeetingBO.class, meeting.getMeetingId());

        verifyEachAccount(client, meeting);
        
    }

    private void verifyEachAccount(final CustomerBO verifyCustomer, final MeetingBO verifyMeeting) {

        List<java.util.Date> dates = null;
        try {
            dates = verifyMeeting.getAllDates(20, true);
        } catch (MeetingException e) {
            e.printStackTrace();
        }
        for (AccountBO account : verifyCustomer.getAccounts()) {
            if (account instanceof SavingsBO) {
                verifyInstallmentDatesMatchMeetingDates("Client Savings Account", dates, account
                        .getAccountActionDates());
            }
            if (account instanceof CustomerAccountBO) {
                verifyInstallmentDatesMatchMeetingDates("Client Customer Account", dates, account
                        .getAccountActionDates());
            }
            
        }
    }

    private void verifyInstallmentDatesMatchMeetingDates(final String accountString,
            final List<java.util.Date> meetingDates, final Set<AccountActionDateEntity> setActionDateEntity) {
        if (setActionDateEntity != null && setActionDateEntity.size() > 0) {
            // assuming set is created in the right date order
            Integer listEntry = 0;
            for (AccountActionDateEntity accountActionDateEntity : setActionDateEntity) {

                if (accountActionDateEntity.getActionDate().compareTo(DateUtils.getCurrentDateWithoutTimeStamp()) != 0) {
                    Assert.assertEquals("Incorrect Action Date for " + accountString, DateUtils
                            .getDateWithoutTimeStamp(meetingDates.get(listEntry)), DateUtils
                            .getDateWithoutTimeStamp(accountActionDateEntity.getActionDate()));
                }
                listEntry++;
            }
        }
    }

    private SavingsBO createSavingsAccount(final CustomerBO customer, final SavingsOfferingBO savingsOffering) {
        SavingsBO savings = null;
        try {
            savings = TestObjectFactory.createSavingsAccount(customer.getCustomerId().toString(), customer,
                    AccountStates.SAVINGS_ACC_APPROVED, new Date(System.currentTimeMillis()), savingsOffering);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return savings;
    }

}
