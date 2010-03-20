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

package org.mifos.framework.components.batchjobs.helpers;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_SECOND_WEEK;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.InterestCalcType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerBOTestUtils;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;


public class RegenerateScheduleHelperIntegrationTest extends MifosIntegrationTestCase {

    public RegenerateScheduleHelperIntegrationTest() throws Exception {
        super();
    }

    private MeetingBO meeting;
    private CustomerBO center;
    private CustomerBO client;
    private CustomerBO group;
    private AccountBO accountBO;
    private LoanOfferingBO loanOfferingBO;
    private CustomerBO client1;
    private CustomerBO client2;
    private SavingsBO savings;
    private RegenerateScheduleHelper regenerateScheduleHelper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        RegenerateScheduleTask regenerateScheduleTask = new RegenerateScheduleTask();
        regenerateScheduleHelper = (RegenerateScheduleHelper) regenerateScheduleTask.getTaskHelper();
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
        new DateTimeService().resetToCurrentSystemDateTime();
    }

    private MeetingBO setupWeeklyMeeting(DateTime startDate, short recurEvery) {
        new DateTimeService().setCurrentDateTime(startDate);
        return TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, recurEvery,
                CUSTOMER_MEETING));
    }

    private void validateAccountSchedule(List<DateTime> expectedMeetingDates, AccountBO account) {
        int count = 0;
        for (AccountActionDateEntity actionDateEntity : account.getAccountActionDates()) {
            if (count < expectedMeetingDates.size()) {
                checkScheduleDates(expectedMeetingDates.get(count), actionDateEntity);
            }
            ++count;
        }
    }

    private void checkScheduleDates(final DateTime expectedMeetingDate,
            final AccountActionDateEntity actionDateEntity) {
        System.out.println(new java.sql.Date(expectedMeetingDate.getMillis()) + ":" + actionDateEntity.getActionDate());

        Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(expectedMeetingDate.toDate()), DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate()));
    }

    public void testExcuteWithCustomerAccounts() throws NumberFormatException, SystemException, ApplicationException {
        DateTime startDate = new DateMidnight(2010,DateTimeConstants.FEBRUARY,10).toDateTime();
        DateTime testDate = new DateMidnight(2010,DateTimeConstants.FEBRUARY,12).toDateTime();
        MeetingBO meeting = setupWeeklyMeeting(startDate, EVERY_WEEK);

        List<DateTime> expectedMeetingDates = new ArrayList<DateTime>();
        expectedMeetingDates.add(new DateMidnight(2010,DateTimeConstants.FEBRUARY,10).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2010,DateTimeConstants.FEBRUARY,17).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2010,DateTimeConstants.MARCH,3).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2010,DateTimeConstants.MARCH,17).toDateTime());

        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        CenterBO center1 = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test1", meeting);
        GroupBO group1 = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group1", CustomerStatus.GROUP_ACTIVE, center1);
        client = TestObjectFactory.createClient("client1", CustomerStatus.CLIENT_ACTIVE, group);
        ClientBO client2 = TestObjectFactory.createClient("client2", CustomerStatus.CLIENT_CLOSED, group);
        ClientBO client3 = TestObjectFactory.createClient("client3", CustomerStatus.CLIENT_CANCELLED, group1);

        new DateTimeService().setCurrentDateTime(testDate);
        center.getCustomerMeeting().getMeeting().getMeetingDetails().setRecurAfter(Short.valueOf("2"));
        CustomerBOTestUtils.setUpdatedFlag(center.getCustomerMeeting(), YesNoFlag.YES.getValue());

        TestObjectFactory.updateObject(center);
        TestObjectFactory.flushandCloseSession();

        int dummy = 0;
        regenerateScheduleHelper.execute(dummy);
        StaticHibernateUtil.closeSession();

        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        center1 = TestObjectFactory.getCenter(center1.getCustomerId());
        group1 = TestObjectFactory.getGroup(group1.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        client2 = TestObjectFactory.getClient(client2.getCustomerId());
        client3 = TestObjectFactory.getClient(client3.getCustomerId());

        validateAccountSchedule(expectedMeetingDates, center.getCustomerAccount());
        validateAccountSchedule(expectedMeetingDates, group.getCustomerAccount());

       Assert.assertEquals(YesNoFlag.NO.getValue(), center.getCustomerMeeting().getUpdatedFlag());
        TestObjectFactory.cleanUp(client3);
        TestObjectFactory.cleanUp(client2);
        TestObjectFactory.cleanUp(group1);
        TestObjectFactory.cleanUp(center1);
    }

    // initial dates 2/10, 2/24, 3/10, 3/24
    public void testExecuteWithLoanAccount() throws Exception {
        LocalDate startDate = new LocalDate(2010,DateTimeConstants.FEBRUARY,10);
        LocalDate testDate = new LocalDate(2010,DateTimeConstants.FEBRUARY,20);

        accountBO = getLoanAccount(startDate);

        List<DateTime> expectedMeetingDates = new ArrayList<DateTime>();
        expectedMeetingDates.add(new DateMidnight(2010,DateTimeConstants.FEBRUARY,10).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2010,DateTimeConstants.FEBRUARY,25).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2010,DateTimeConstants.MARCH,11).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2010,DateTimeConstants.MARCH,25).toDateTime());

        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        accountBO = (AccountBO) StaticHibernateUtil.getSessionTL().get(LoanBO.class, accountBO.getAccountId());

        center.getCustomerMeeting().getMeeting().getMeetingDetails().getMeetingRecurrence().setWeekDay(
                (WeekDaysEntity) new MasterPersistence().retrieveMasterEntity(WeekDay.THURSDAY.getValue(),
                        WeekDaysEntity.class, null));
        CustomerBOTestUtils.setUpdatedFlag(center.getCustomerMeeting(), YesNoFlag.YES.getValue());

        new DateTimeService().setCurrentDateTime(testDate.toDateTimeAtStartOfDay());
        AccountActionDateEntity accountActionDateEntity = center.getCustomerAccount().getDetailsOfNextInstallment();
        center.getCustomerMeeting().getMeeting().setMeetingStartDate(accountActionDateEntity.getActionDate());

        TestObjectFactory.updateObject(center);
        TestObjectFactory.flushandCloseSession();
        int dummy = 0;
        regenerateScheduleHelper.execute(dummy);

        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        accountBO = (AccountBO) StaticHibernateUtil.getSessionTL().get(AccountBO.class, accountBO.getAccountId());

        validateAccountSchedule(expectedMeetingDates, accountBO);

        Assert.assertEquals(YesNoFlag.NO.getValue(), center.getCustomerMeeting().getUpdatedFlag());

        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        accountBO = (AccountBO) StaticHibernateUtil.getSessionTL().get(AccountBO.class, accountBO.getAccountId());
    }

    public void testExecuteWithLoanAccountWithPastInstallmentsdPaid() throws Exception {
        LocalDate startDate = new LocalDate(2010,3,3);
        LocalDate updateDate = new LocalDate(2010,3,24);
        new DateTimeService().setCurrentDateTime(startDate.toDateTimeAtStartOfDay());
        accountBO = getLoanAccount(startDate);
        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        accountBO = (AccountBO) StaticHibernateUtil.getSessionTL().get(LoanBO.class, accountBO.getAccountId());

        accountBO.getAccountActionDate((short) 1).setPaymentStatus(PaymentStatus.PAID);
        accountBO.getAccountActionDate((short) 2).setPaymentStatus(PaymentStatus.PAID);
        accountBO.getAccountActionDate((short) 3).setPaymentStatus(PaymentStatus.PAID);

        new DateTimeService().setCurrentDateTime(updateDate.toDateTimeAtStartOfDay());
        center.getCustomerMeeting().getMeeting().getMeetingDetails().getMeetingRecurrence().setWeekDay(
                (WeekDaysEntity) new MasterPersistence().retrieveMasterEntity(WeekDay.THURSDAY.getValue(),
                        WeekDaysEntity.class, null));
        CustomerBOTestUtils.setUpdatedFlag(center.getCustomerMeeting(), YesNoFlag.YES.getValue());

        TestObjectFactory.updateObject(center);
        TestObjectFactory.updateObject(accountBO);
        TestObjectFactory.flushandCloseSession();
        int dummy = 0;
        regenerateScheduleHelper.execute(dummy);

        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        accountBO = (AccountBO) StaticHibernateUtil.getSessionTL().get(AccountBO.class, accountBO.getAccountId());

        List<DateTime> expectedMeetingDates = new ArrayList<DateTime>();
        expectedMeetingDates.add(new DateMidnight(2010,DateTimeConstants.MARCH,3).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2010,DateTimeConstants.MARCH,17).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2010,DateTimeConstants.APRIL,1).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2010,DateTimeConstants.APRIL,15).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2010,DateTimeConstants.APRIL,29).toDateTime());

        validateAccountSchedule(expectedMeetingDates, center.getCustomerAccount());

        validateAccountSchedule(expectedMeetingDates, group.getCustomerAccount());

       Assert.assertEquals(YesNoFlag.NO.getValue(), center.getCustomerMeeting().getUpdatedFlag());

        TestObjectFactory.flushandCloseSession();
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        group = TestObjectFactory.getCustomer(group.getCustomerId());
        accountBO = (AccountBO) StaticHibernateUtil.getSessionTL().get(AccountBO.class, accountBO.getAccountId());
    }

    private AccountBO getLoanAccount(LocalDate startDate) {
        new DateTimeService().setCurrentDateTime(startDate.toDateTimeAtStartOfDay());
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_SECOND_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);

        loanOfferingBO = TestObjectFactory.createLoanOffering(new DateTimeService().getCurrentJavaDateTime(), meeting);

        return TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                new DateTimeService().getCurrentJavaDateTime(), loanOfferingBO);
    }

    public void testCustomerMeetingChangeUpdatesAllInstallmentsForSavingsAndCustomerAccountsOfAClient()
            throws Exception {
        // original schedule 2/11,2/18,2/25,3/4
        DateTime startDate = new DateMidnight(2010,DateTimeConstants.FEBRUARY,11).toDateTime();
        DateTime testDate = new DateMidnight(2010,DateTimeConstants.FEBRUARY,20).toDateTime();
        meeting = setupWeeklyMeeting(startDate, EVERY_WEEK);

        List<DateTime> expectedMeetingDates = new ArrayList<DateTime>();
        expectedMeetingDates.add(new DateMidnight(2010,DateTimeConstants.FEBRUARY,11).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2010,DateTimeConstants.FEBRUARY,18).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2010,DateTimeConstants.FEBRUARY,27).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2010,DateTimeConstants.MARCH,6).toDateTime());

        center = TestObjectFactory.createWeeklyFeeCenter(this.getClass().getSimpleName() + " Center", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter(this.getClass().getSimpleName() + " Group",
                CustomerStatus.GROUP_ACTIVE, center);
        client = TestObjectFactory.createClient(this.getClass().getSimpleName() + " Client",
                CustomerStatus.CLIENT_ACTIVE, group);
        SavingsBO savings = createSavingsAccount(meeting);

        // move weekly center meeting
        MeetingBO newMeeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeeting(RecurrenceType.WEEKLY,
                Short.valueOf("1"), MeetingType.CUSTOMER_MEETING, WeekDay.SATURDAY));
        new DateTimeService().setCurrentDateTime(testDate);

        StaticHibernateUtil.closeSession();
        center = TestObjectFactory.getCenter(center.getCustomerId());
        center.updateMeeting(newMeeting);

        int dummy = 0;
        regenerateScheduleHelper.execute(dummy);
        StaticHibernateUtil.closeSession();

        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
        client = TestObjectFactory.getClient(client.getCustomerId());
        meeting = TestObjectFactory.getObject(MeetingBO.class, meeting.getMeetingId());
        savings = TestObjectFactory.getObject(SavingsBO.class, savings.getAccountId());

        validateAccountSchedule(expectedMeetingDates, client.getCustomerAccount());
        validateAccountSchedule(expectedMeetingDates, savings);
    }

    public SavingsBO createSavingsAccount(MeetingBO meeting) throws Exception {
        MeetingBO meetingIntCalc = meeting;
        MeetingBO meetingIntPost = meeting;
        SavingsOfferingBO savingsOffering = TestObjectFactory.createSavingsProduct("SavingPrd1", ApplicableTo.CLIENTS,
                new DateTime().toDate(), PrdStatus.LOAN_ACTIVE, 300.0, RecommendedAmountUnit.PER_INDIVIDUAL, 24.0, 200.0,
                200.0, SavingsType.VOLUNTARY, InterestCalcType.MINIMUM_BALANCE, meetingIntCalc, meetingIntPost);
        SavingsBO savings = new SavingsBO(TestUtils.makeUser(), savingsOffering, client, AccountState.SAVINGS_ACTIVE,
                savingsOffering.getRecommendedAmount(), null);
        savings.save();
        StaticHibernateUtil.commitTransaction();
        return savings;
    }

}
