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

package org.mifos.accounts;

import static org.joda.time.DateTimeConstants.APRIL;
import static org.joda.time.DateTimeConstants.AUGUST;
import static org.joda.time.DateTimeConstants.DECEMBER;
import static org.joda.time.DateTimeConstants.FEBRUARY;
import static org.joda.time.DateTimeConstants.JANUARY;
import static org.joda.time.DateTimeConstants.JULY;
import static org.joda.time.DateTimeConstants.JUNE;
import static org.joda.time.DateTimeConstants.MARCH;
import static org.joda.time.DateTimeConstants.MAY;
import static org.joda.time.DateTimeConstants.NOVEMBER;
import static org.joda.time.DateTimeConstants.SEPTEMBER;
import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.InterestCalcType;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RankType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerBOTestUtils;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class AccountRegenerateScheduleIntegrationTestCase extends MifosIntegrationTestCase {
    public AccountRegenerateScheduleIntegrationTestCase() throws Exception {
        super();
    }

    protected LoanBO accountBO;
    protected SavingsBO savingsBO;
    protected CustomerBO center;
    protected CustomerBO group;
    protected ClientBO client;
    protected AccountPersistence accountPersistence;
    // hard coded number of loan installments that are created (in TestObjectFactory)
    private int numberOfLoanInstallments = 6;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            TestObjectFactory.cleanUp(savingsBO);
            TestObjectFactory.cleanUp(accountBO);
            TestObjectFactory.cleanUp(client);
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(center);

            accountPersistence = null;
        } catch (Exception e) {
            // TODO Whoops, cleanup didnt work, reset db
            TestDatabase.resetMySQLDatabase();
        } finally {
            StaticHibernateUtil.closeSession();
        }
        super.tearDown();
        new DateTimeService().resetToCurrentSystemDateTime();
    }

    public LoanBO createLoanAccount(MeetingBO customerMeeting, MeetingBO loanMeeting) {
        center = TestObjectFactory.createWeeklyFeeCenter("Center", customerMeeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group", CustomerStatus.GROUP_ACTIVE, center);
        LoanOfferingBO loanOffering = TestObjectFactory.createLoanOffering("Loan", ApplicableTo.GROUPS, new DateTime().toDate(),
                PrdStatus.LOAN_ACTIVE, 300.0, 1.2, (short) 3, InterestType.FLAT, loanMeeting);
        return TestObjectFactory.createLoanAccount("42423142341", group, AccountState.LOAN_ACTIVE_IN_GOOD_STANDING,
                new DateTime().toDate(), loanOffering);
    }

    public SavingsBO createSavingsAccount(MeetingBO meeting) throws Exception {
        MeetingBO meetingIntCalc = meeting;
        MeetingBO meetingIntPost = meeting;
        SavingsOfferingBO savingsOffering = TestObjectFactory.createSavingsProduct("SavingPrd1", ApplicableTo.CLIENTS,
                new DateTime().toDate(), PrdStatus.LOAN_ACTIVE, 300.0, RecommendedAmountUnit.PER_INDIVIDUAL, 24.0, 200.0,
                200.0, SavingsType.VOLUNTARY, InterestCalcType.MINIMUM_BALANCE, meetingIntCalc, meetingIntPost);
        client = TestObjectFactory.createClient("client", CustomerStatus.CLIENT_ACTIVE, group);
        SavingsBO savings = new SavingsBO(TestUtils.makeUser(), savingsOffering, client, AccountState.SAVINGS_ACTIVE,
                savingsOffering.getRecommendedAmount(), null);
        savings.save();
        StaticHibernateUtil.commitTransaction();
        return savings;
    }

    /*
     * original schedule dates: 5/23, 5/30, 6/6, 6/13, 6/20, 6/27
     */
    public void testChangeMeetingInFirstWeekOfSchedule() throws Exception {
        List<LocalDate> expectedMeetingDates = new ArrayList<LocalDate>();
        expectedMeetingDates.add(new LocalDate(2008,MAY,23));
        expectedMeetingDates.add(new LocalDate(2008,MAY,28));
        expectedMeetingDates.add(new LocalDate(2008,JUNE,4));
        expectedMeetingDates.add(new LocalDate(2008,JUNE,11));
        expectedMeetingDates.add(new LocalDate(2008,JUNE,18));
        expectedMeetingDates.add(new LocalDate(2008,JUNE,25));

        LocalDate startDate = new LocalDate(2008,MAY,23);
        LocalDate dateWhenMeetingWillBeChanged = new LocalDate(2008, MAY, 25);
        MeetingBO meeting = setupWeeklyMeeting(startDate, EVERY_WEEK);
        MeetingBO newMeeting = TestObjectFactory.getNewMeeting(RecurrenceType.WEEKLY, EVERY_WEEK, MeetingType.CUSTOMER_MEETING, WeekDay.WEDNESDAY);

        testChangeInMeetingScheduleForDates(meeting, newMeeting, startDate, dateWhenMeetingWillBeChanged);
        validateSchedules(expectedMeetingDates);
    }


    public void testChangeMeetingInSecondWeekOfSchedule() throws Exception {
        List<LocalDate> expectedMeetingDates = new ArrayList<LocalDate>();
        expectedMeetingDates.add(new LocalDate(2008,MAY,23));
        expectedMeetingDates.add(new LocalDate(2008,MAY,30));
        expectedMeetingDates.add(new LocalDate(2008,JUNE,4));
        expectedMeetingDates.add(new LocalDate(2008,JUNE,11));
        expectedMeetingDates.add(new LocalDate(2008,JUNE,18));
        expectedMeetingDates.add(new LocalDate(2008,JUNE,25));

        LocalDate startDate = new LocalDate(2008,MAY,23);
        LocalDate dateWhenMeetingWillBeChanged = new LocalDate(2008, MAY, 26);
        MeetingBO meeting = setupWeeklyMeeting(startDate, EVERY_WEEK);
        MeetingBO newMeeting = TestObjectFactory.getNewMeeting(RecurrenceType.WEEKLY, EVERY_WEEK, MeetingType.CUSTOMER_MEETING, WeekDay.WEDNESDAY);
        testChangeInMeetingScheduleForDates(meeting, newMeeting, startDate, dateWhenMeetingWillBeChanged);
        validateSchedules(expectedMeetingDates);
    }

    public void testChangeMeetingInSecondWeekOfScheduleOnOldMeetingDay() throws Exception {
        List<LocalDate> expectedMeetingDates = new ArrayList<LocalDate>();
        expectedMeetingDates.add(new LocalDate(2008,MAY,23));
        expectedMeetingDates.add(new LocalDate(2008,MAY,30));
        expectedMeetingDates.add(new LocalDate(2008,JUNE,4));
        expectedMeetingDates.add(new LocalDate(2008,JUNE,11));
        expectedMeetingDates.add(new LocalDate(2008,JUNE,18));
        expectedMeetingDates.add(new LocalDate(2008,JUNE,25));

        LocalDate startDate = new LocalDate(2008,MAY,23);
        LocalDate dateWhenMeetingWillBeChanged = new LocalDate(2008, MAY, 30);
        MeetingBO meeting = setupWeeklyMeeting(startDate, EVERY_WEEK);
        MeetingBO newMeeting = TestObjectFactory.getNewMeeting(RecurrenceType.WEEKLY, EVERY_WEEK, MeetingType.CUSTOMER_MEETING, WeekDay.WEDNESDAY);

        testChangeInMeetingScheduleForDates(meeting, newMeeting, startDate, dateWhenMeetingWillBeChanged);
        validateSchedules(expectedMeetingDates);
    }


    // a change in the last week of a schedule should not change the schedule
    public void testChangeMeetingInLastWeekOfSchedule() throws Exception {
        List<LocalDate> expectedMeetingDates = new ArrayList<LocalDate>();
        expectedMeetingDates.add(new LocalDate(2008,MAY,23));
        expectedMeetingDates.add(new LocalDate(2008,MAY,30));
        expectedMeetingDates.add(new LocalDate(2008,JUNE,6));
        expectedMeetingDates.add(new LocalDate(2008,JUNE,13));
        expectedMeetingDates.add(new LocalDate(2008,JUNE,20));
        expectedMeetingDates.add(new LocalDate(2008,JUNE,27));

        LocalDate startDate = new LocalDate(2008,MAY,23);
        LocalDate dateWhenMeetingWillBeChanged = new LocalDate(2008, JUNE, 25);
        MeetingBO meeting = setupWeeklyMeeting(startDate, EVERY_WEEK);
        MeetingBO newMeeting = TestObjectFactory.getNewMeeting(RecurrenceType.WEEKLY, EVERY_WEEK, MeetingType.CUSTOMER_MEETING, WeekDay.WEDNESDAY);
        testChangeInMeetingScheduleForDates(meeting, newMeeting, startDate, dateWhenMeetingWillBeChanged);
        validateSchedules(expectedMeetingDates);

    }

    // for closed and canceled accounts, we expect no change to the schedule
    public void testChangeMeetingScheduleForClosedAndCancelledAccounts() throws Exception {

        List<LocalDate> expectedUnchangedMeetingDates = new ArrayList<LocalDate>();
        expectedUnchangedMeetingDates.add(new LocalDate(2008,MAY,23));
        expectedUnchangedMeetingDates.add(new LocalDate(2008,MAY,30));
        expectedUnchangedMeetingDates.add(new LocalDate(2008,JUNE,6));
        expectedUnchangedMeetingDates.add(new LocalDate(2008,JUNE,13));
        expectedUnchangedMeetingDates.add(new LocalDate(2008,JUNE,20));
        expectedUnchangedMeetingDates.add(new LocalDate(2008,JUNE,27));

        LocalDate startDate = new LocalDate(2008,MAY,23);
        LocalDate dateWhenMeetingWillBeChanged = new LocalDate(2008, MAY, 30);
        MeetingBO meeting = setupWeeklyMeeting(startDate, EVERY_WEEK);
        MeetingBO newMeeting = TestObjectFactory.getNewMeeting(RecurrenceType.WEEKLY, EVERY_WEEK, MeetingType.CUSTOMER_MEETING, WeekDay.WEDNESDAY);
        testChangeInMeetingScheduleForDates(meeting, meeting, newMeeting, startDate, dateWhenMeetingWillBeChanged, true);
        validateSchedulesForClosedAndCancelled(expectedUnchangedMeetingDates, expectedUnchangedMeetingDates);

    }

    public void testChangeInFirstPeriodOfBiWeeklyMeetingSchedule() throws Exception {
        List<LocalDate> expectedMeetingDates = new ArrayList<LocalDate>();
        expectedMeetingDates.add(new LocalDate(2008,MAY,23));
        expectedMeetingDates.add(new LocalDate(2008,JUNE,4));
        expectedMeetingDates.add(new LocalDate(2008,JUNE,18));
        expectedMeetingDates.add(new LocalDate(2008,JULY,2));
        expectedMeetingDates.add(new LocalDate(2008,JULY,16));

        LocalDate startDate = new LocalDate(2008,MAY,23);
        LocalDate dateWhenMeetingWillBeChanged = new LocalDate(2008, MAY, 30);
        MeetingBO meeting = setupWeeklyMeeting(startDate, TestObjectFactory.EVERY_SECOND_WEEK);
        MeetingBO newMeeting = TestObjectFactory.getNewMeeting(RecurrenceType.WEEKLY, TestObjectFactory.EVERY_SECOND_WEEK, MeetingType.CUSTOMER_MEETING, WeekDay.WEDNESDAY);
        testChangeInMeetingScheduleForDates(meeting, newMeeting, startDate, dateWhenMeetingWillBeChanged);
        validateSchedules(expectedMeetingDates);
    }

    public void testChangeInSecondPeriodOfBiWeeklyMeetingSchedule() throws Exception {
        List<LocalDate> expectedMeetingDates = new ArrayList<LocalDate>();
        expectedMeetingDates.add(new LocalDate(2008,MAY,23));
        expectedMeetingDates.add(new LocalDate(2008,JUNE,6));
        expectedMeetingDates.add(new LocalDate(2008,JUNE,18));
        expectedMeetingDates.add(new LocalDate(2008,JULY,2));
        expectedMeetingDates.add(new LocalDate(2008,JULY,16));

        LocalDate startDate = new LocalDate(2008,MAY,23);
        LocalDate dateWhenMeetingWillBeChanged = new LocalDate(2008, JUNE, 2);
        MeetingBO meeting = setupWeeklyMeeting(startDate, TestObjectFactory.EVERY_SECOND_WEEK);
        MeetingBO newMeeting = TestObjectFactory.getNewMeeting(RecurrenceType.WEEKLY, TestObjectFactory.EVERY_SECOND_WEEK, MeetingType.CUSTOMER_MEETING, WeekDay.WEDNESDAY);
        testChangeInMeetingScheduleForDates(meeting, newMeeting, startDate, dateWhenMeetingWillBeChanged);
        validateSchedules(expectedMeetingDates);
    }


    public void testChangeMonthlyMeetingScheduleOnADateInFirstMonthBeforeMeetingDate() throws Exception {
        List<LocalDate> expectedMeetingDates = new ArrayList<LocalDate>();
        expectedMeetingDates.add(new LocalDate(2008,APRIL,9));
        expectedMeetingDates.add(new LocalDate(2008,MAY,9));
        expectedMeetingDates.add(new LocalDate(2008,JUNE,20));
        expectedMeetingDates.add(new LocalDate(2008,JULY,20));
        expectedMeetingDates.add(new LocalDate(2008,AUGUST,20));

        LocalDate startDate = new LocalDate(2008,APRIL,9);
        LocalDate dateWhenMeetingWillBeChanged = new LocalDate(2008, MAY, 3);
        MeetingBO meeting = setupMonthlyMeeting(startDate, 1, 9);
        MeetingBO newMeeting = TestObjectFactory.createMeeting(new MeetingBuilder().monthly().every(1).buildMonthlyForDayNumber(20));

        testChangeInMeetingScheduleForDates(meeting, newMeeting, startDate, dateWhenMeetingWillBeChanged);
        validateSchedules(expectedMeetingDates);
    }

    public void testChangeMonthlyMeetingScheduleOnADateInFirstMonthAfterMeetingDate() throws Exception {
        List<LocalDate> expectedMeetingDates = new ArrayList<LocalDate>();
        expectedMeetingDates.add(new LocalDate(2008,APRIL,9));
        expectedMeetingDates.add(new LocalDate(2008,MAY,9));
        expectedMeetingDates.add(new LocalDate(2008,JUNE,20));
        expectedMeetingDates.add(new LocalDate(2008,JULY,20));
        expectedMeetingDates.add(new LocalDate(2008,AUGUST,20));

        LocalDate startDate = new LocalDate(2008,APRIL,9);
        LocalDate dateWhenMeetingWillBeChanged = new LocalDate(2008, MAY, 31);
        MeetingBO meeting = setupMonthlyMeeting(startDate, 1, 9);
        MeetingBO newMeeting = TestObjectFactory.createMeeting(new MeetingBuilder().monthly().every(1).buildMonthlyForDayNumber(20));

        testChangeInMeetingScheduleForDates(meeting, newMeeting, startDate, dateWhenMeetingWillBeChanged);
        validateSchedules(expectedMeetingDates);
    }

    public void testChangeMonthlyMeetingScheduleOnADateInSecondMonth() throws Exception {
        List<LocalDate> expectedMeetingDates = new ArrayList<LocalDate>();
        expectedMeetingDates.add(new LocalDate(2008,APRIL,9));
        expectedMeetingDates.add(new LocalDate(2008,MAY,9));
        expectedMeetingDates.add(new LocalDate(2008,JUNE,9));
        expectedMeetingDates.add(new LocalDate(2008,JULY,20));
        expectedMeetingDates.add(new LocalDate(2008,AUGUST,20));

        LocalDate startDate = new LocalDate(2008,APRIL,9);
        LocalDate dateWhenMeetingWillBeChanged = new LocalDate(2008, JUNE, 1);
        MeetingBO meeting = setupMonthlyMeeting(startDate, 1, 9);
        MeetingBO newMeeting = TestObjectFactory.createMeeting(new MeetingBuilder().monthly().every(1).buildMonthlyForDayNumber(20));

        testChangeInMeetingScheduleForDates(meeting, newMeeting, startDate, dateWhenMeetingWillBeChanged);
        validateSchedules(expectedMeetingDates);
    }

    // original schedule 11/20/08, 12/18/08, 1/15/09, 2/19/09, 3/19/09, 4/16/09
    public void testChangeThirdThursdayMonthlyMeetingScheduleOnADateInFirstMonthAfterMeetingDate() throws Exception {
        List<LocalDate> expectedMeetingDates = new ArrayList<LocalDate>();
        expectedMeetingDates.add(new LocalDate(2008,NOVEMBER,20));
        expectedMeetingDates.add(new LocalDate(2008,DECEMBER,2));
        expectedMeetingDates.add(new LocalDate(2009,JANUARY,6));
        expectedMeetingDates.add(new LocalDate(2009,FEBRUARY,3));
        expectedMeetingDates.add(new LocalDate(2009,MARCH,3));
        expectedMeetingDates.add(new LocalDate(2009,APRIL,7));

        LocalDate startDate = new LocalDate(2008,NOVEMBER,20);
        LocalDate dateWhenMeetingWillBeChanged = new LocalDate(2008, NOVEMBER, 30);
        MeetingBO meeting = setupMonthlyMeeting(startDate, 1, RankType.THIRD, WeekDay.THURSDAY);
        MeetingBO newMeeting = TestObjectFactory.createMeeting(new MeetingBuilder().monthly().every(1).
                buildMonthlyFor(RankType.FIRST, WeekDay.TUESDAY));

        testChangeInMeetingScheduleForDates(meeting, newMeeting, startDate, dateWhenMeetingWillBeChanged);
        validateSchedules(expectedMeetingDates);
    }

    // original schedule 11/20/08, 1/15/09, 3/19/09, 5/21/09, 7/16/09, 9/17/09
    public void testChangeThirdThursdayBiMonthlyMeetingScheduleOnADateInThirdMonthAfterMeetingDate() throws Exception {
        List<LocalDate> expectedMeetingDates = new ArrayList<LocalDate>();
        expectedMeetingDates.add(new LocalDate(2008,NOVEMBER,20));
        expectedMeetingDates.add(new LocalDate(2009,JANUARY,15));
        expectedMeetingDates.add(new LocalDate(2009,MARCH,3));
        expectedMeetingDates.add(new LocalDate(2009,MAY,5));
        expectedMeetingDates.add(new LocalDate(2009,JULY,7));
        expectedMeetingDates.add(new LocalDate(2009,SEPTEMBER,1));

        LocalDate startDate = new LocalDate(2008,NOVEMBER,20);
        LocalDate dateWhenMeetingWillBeChanged = new LocalDate(2009, FEBRUARY, 15);
        MeetingBO meeting = setupMonthlyMeeting(startDate, 2, RankType.THIRD, WeekDay.THURSDAY);
        MeetingBO newMeeting = TestObjectFactory.createMeeting(new MeetingBuilder().monthly().every(2).
                buildMonthlyFor(RankType.FIRST, WeekDay.TUESDAY));

        testChangeInMeetingScheduleForDates(meeting, newMeeting, startDate, dateWhenMeetingWillBeChanged);
        validateSchedules(expectedMeetingDates);
    }

    private MeetingBO setupWeeklyMeeting(LocalDate startDate, short recurEvery) {
        new DateTimeService().setCurrentDateTime(startDate.toDateTimeAtStartOfDay());
        return TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, recurEvery,
                CUSTOMER_MEETING));
    }


    private MeetingBO setupMonthlyMeeting(LocalDate startDate, int recurEvery, int dayNumber) throws MeetingException {
        new DateTimeService().setCurrentDateTime(startDate.toDateTimeAtStartOfDay());
        return TestObjectFactory.createMeeting(new MeetingBuilder().monthly().every(recurEvery).buildMonthlyForDayNumber(dayNumber));

    }

    private MeetingBO setupMonthlyMeeting(LocalDate startDate, int recurEvery, RankType rank, WeekDay weekDay) throws MeetingException {
        new DateTimeService().setCurrentDateTime(startDate.toDateTimeAtStartOfDay());
        return TestObjectFactory.createMeeting(new MeetingBuilder().monthly().every(recurEvery).buildMonthlyFor(rank, weekDay));

    }

    protected void testChangeInMeetingScheduleForDates(MeetingBO meeting, MeetingBO newMeeting, LocalDate startDate,
            LocalDate dateWhenMeetingWillBeChanged) throws Exception {
        testChangeInMeetingScheduleForDates(meeting, meeting, newMeeting, startDate, dateWhenMeetingWillBeChanged, false);
    }

    protected void testChangeInMeetingScheduleForDates(MeetingBO customerMeeting, MeetingBO loanMeeting, MeetingBO newMeeting, LocalDate startDate, LocalDate dateWhenMeetingWillBeChanged,
            boolean useClosedAndCancelled) throws Exception {
        log("Start: " + startDate + ", Test: " + dateWhenMeetingWillBeChanged);
        accountBO = createLoanAccount(customerMeeting, loanMeeting);
        savingsBO = createSavingsAccount(customerMeeting);

        // center initially set up with meeting today
        center = TestObjectFactory.getCenter(center.getCustomerId());
        accountBO = TestObjectFactory.getObject(LoanBO.class, accountBO.getAccountId());
        if (useClosedAndCancelled) {
            accountBO.changeStatus(AccountState.LOAN_CANCELLED, null, "");
            savingsBO.changeStatus(AccountState.SAVINGS_CANCELLED.getValue(), null, "");
            CustomerStatusEntity customerStatusEntity = new CustomerStatusEntity(CustomerStatus.GROUP_CLOSED);
            CustomerBOTestUtils.setCustomerStatus(group, customerStatusEntity);
            StaticHibernateUtil.commitTransaction();
        }

        new DateTimeService().setCurrentDateTime(dateWhenMeetingWillBeChanged.toDateTimeAtStartOfDay());
        center.updateMeeting(newMeeting);

        TestObjectFactory.updateObject(center);

        List<Days> workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();
        List<Holiday> holidays = new ArrayList<Holiday>();
        center.getCustomerAccount().handleChangeInMeetingSchedule(workingDays, holidays);
        accountBO.handleChangeInMeetingSchedule(workingDays, holidays);
        savingsBO.handleChangeInMeetingSchedule(workingDays, holidays);

        StaticHibernateUtil.getTransaction().commit();
        StaticHibernateUtil.closeSession();

    }

    private void validateSchedules(List<LocalDate> expectedMeetingDates) {
        validateSchedules(expectedMeetingDates, expectedMeetingDates);
    }

    private void validateSchedulesForClosedAndCancelled(List<LocalDate> expectedMeetingDates, List<LocalDate> expectedLoanMeetingDates) {
        center = TestObjectFactory.getCenter(center.getCustomerId());
        accountBO = TestObjectFactory.getObject(LoanBO.class, accountBO.getAccountId());
        savingsBO = TestObjectFactory.getObject(SavingsBO.class, savingsBO.getAccountId());
        int count = 0;
        for (AccountActionDateEntity actionDateEntity : group.getCustomerAccount().getAccountActionDates()) {
            if (count < expectedMeetingDates.size()) {
                checkScheduleDates(expectedMeetingDates.get(count), actionDateEntity);
            }
            ++count;
        }
        log("---");
        count = 0;
        for (AccountActionDateEntity actionDateEntity : accountBO.getAccountActionDates()) {
            if (count < expectedLoanMeetingDates.size()) {
                checkScheduleDates(expectedLoanMeetingDates.get(count), actionDateEntity);
            }
            ++count;
        }
        log("+++");
        count = 0;
        for (AccountActionDateEntity actionDateEntity : savingsBO.getAccountActionDates()) {
            if (count < expectedLoanMeetingDates.size()) {
                checkScheduleDates(expectedLoanMeetingDates.get(count), actionDateEntity);
            }
            ++count;
        }
        log("===");
    }

    private void validateSchedules(List<LocalDate> expectedMeetingDates, List<LocalDate> expectedLoanMeetingDates) {
        center = TestObjectFactory.getCenter(center.getCustomerId());
        accountBO = TestObjectFactory.getObject(LoanBO.class, accountBO.getAccountId());
        savingsBO = TestObjectFactory.getObject(SavingsBO.class, savingsBO.getAccountId());
        int count = 0;
        for (AccountActionDateEntity actionDateEntity : center.getCustomerAccount().getAccountActionDates()) {
            if (count < expectedMeetingDates.size()) {
                checkScheduleDates(expectedMeetingDates.get(count), actionDateEntity);
            }
            ++count;
        }
        log("---");
        assertEquals(numberOfLoanInstallments , accountBO.getAccountActionDates().size());
        count = 0;
        for (AccountActionDateEntity actionDateEntity : accountBO.getAccountActionDates()) {
            if (count < expectedLoanMeetingDates.size()) {
                checkScheduleDates(expectedLoanMeetingDates.get(count), actionDateEntity);
            }
            ++count;
        }
        log("+++");
        count = 0;
        for (AccountActionDateEntity actionDateEntity : savingsBO.getAccountActionDates()) {
            if (count < expectedLoanMeetingDates.size()) {
                checkScheduleDates(expectedLoanMeetingDates.get(count), actionDateEntity);
            }
            ++count;
        }
        log("===");
    }

    private void checkScheduleDates(final LocalDate expectedMeetingDate,
            final AccountActionDateEntity actionDateEntity) {
        log(new java.sql.Date(expectedMeetingDate.toDateTimeAtStartOfDay().getMillis()) + ":" + actionDateEntity.getActionDate());

        Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(expectedMeetingDate.toDateTimeAtStartOfDay().toDate()), DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate()));
    }

    private void log(String message) {
        MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(message);
    }
}
