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

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
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
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerBOTestUtils;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
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
    protected AccountPersistence accountPersistence;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            TestObjectFactory.cleanUp(savingsBO);
            TestObjectFactory.cleanUp(accountBO);
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
        SavingsOfferingBO savingsOffering = TestObjectFactory.createSavingsProduct("SavingPrd1", ApplicableTo.GROUPS, 
                new DateTime().toDate(), PrdStatus.LOAN_ACTIVE, 300.0, RecommendedAmountUnit.PER_INDIVIDUAL, 24.0, 200.0,
                200.0, SavingsType.VOLUNTARY, InterestCalcType.MINIMUM_BALANCE, meetingIntCalc, meetingIntPost);
        //SavingsBO savings = TestObjectFactory.createSavingsAccount("1234", group, AccountState.SAVINGS_ACTIVE, new DateTime().toDate(),
        //            savingsOffering, TestUtils.makeUser());
        SavingsBO savings = new SavingsBO(TestUtils.makeUser(), savingsOffering, group, AccountState.SAVINGS_ACTIVE,
                savingsOffering.getRecommendedAmount(), null);     
        savings.save();
        StaticHibernateUtil.commitTransaction();
        return savings;
    }

    /*
     * original schedule dates: 5/23, 5/30, 6/6, 6/13, 6,20
     */
    public void testHandleChangeInMeetingSchedule1() throws Exception {
        List<DateTime> expectedMeetingDates = new ArrayList<DateTime>();
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.MAY,23).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.MAY,28).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.JUNE,4).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.JUNE,11).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.JUNE,18).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.JUNE,25).toDateTime());
        
        DateTime startDate = new DateMidnight(2008,DateTimeConstants.MAY,23).toDateTime();
        DateTime testDate = new DateMidnight(2008, DateTimeConstants.MAY, 25).toDateTime();
        MeetingBO meeting = setupWeeklyMeeting(startDate, EVERY_WEEK);
        MeetingBO newMeeting = TestObjectFactory.getNewMeeting(RecurrenceType.WEEKLY, EVERY_WEEK, MeetingType.CUSTOMER_MEETING, WeekDay.WEDNESDAY);
        
        testChangeInMeetingScheduleForDates(meeting, newMeeting, startDate, testDate);
        validateSchedules(expectedMeetingDates);
    }   

    public void testHandleChangeInMeetingSchedule2() throws Exception {
        List<DateTime> expectedMeetingDates = new ArrayList<DateTime>();
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.MAY,23).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.MAY,30).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.JUNE,4).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.JUNE,11).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.JUNE,18).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.JUNE,25).toDateTime());

        DateTime startDate = new DateMidnight(2008,DateTimeConstants.MAY,23).toDateTime();
        DateTime testDate = new DateMidnight(2008, DateTimeConstants.MAY, 26).toDateTime(); 
        MeetingBO meeting = setupWeeklyMeeting(startDate, EVERY_WEEK);
        MeetingBO newMeeting = TestObjectFactory.getNewMeeting(RecurrenceType.WEEKLY, EVERY_WEEK, MeetingType.CUSTOMER_MEETING, WeekDay.WEDNESDAY);
        testChangeInMeetingScheduleForDates(meeting, newMeeting, startDate, testDate);
        validateSchedules(expectedMeetingDates);
    }   
    
    public void testHandleChangeInMeetingSchedule3() throws Exception {
        List<DateTime> expectedMeetingDates = new ArrayList<DateTime>();
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.MAY,23).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.MAY,30).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.JUNE,4).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.JUNE,11).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.JUNE,18).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.JUNE,25).toDateTime());

        DateTime startDate = new DateMidnight(2008,DateTimeConstants.MAY,23).toDateTime();
        DateTime testDate = new DateMidnight(2008, DateTimeConstants.MAY, 30).toDateTime(); 
        MeetingBO meeting = setupWeeklyMeeting(startDate, EVERY_WEEK);
        MeetingBO newMeeting = TestObjectFactory.getNewMeeting(RecurrenceType.WEEKLY, EVERY_WEEK, MeetingType.CUSTOMER_MEETING, WeekDay.WEDNESDAY);
        testChangeInMeetingScheduleForDates(meeting, newMeeting, startDate, testDate);
        validateSchedules(expectedMeetingDates);

    }   

    public void testHandleChangeInMeetingSchedule3WithCancelledLoan() throws Exception {

        List<DateTime> expectedUnchangedMeetingDates = new ArrayList<DateTime>();
        expectedUnchangedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.MAY,23).toDateTime());
        expectedUnchangedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.MAY,30).toDateTime());
        expectedUnchangedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.JUNE,6).toDateTime());
        expectedUnchangedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.JUNE,13).toDateTime());
        expectedUnchangedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.JUNE,20).toDateTime());
        expectedUnchangedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.JUNE,27).toDateTime());

        DateTime startDate = new DateMidnight(2008,DateTimeConstants.MAY,23).toDateTime();
        DateTime testDate = new DateMidnight(2008, DateTimeConstants.MAY, 30).toDateTime(); 
        MeetingBO meeting = setupWeeklyMeeting(startDate, EVERY_WEEK);
        MeetingBO newMeeting = TestObjectFactory.getNewMeeting(RecurrenceType.WEEKLY, EVERY_WEEK, MeetingType.CUSTOMER_MEETING, WeekDay.WEDNESDAY);
        testChangeInMeetingScheduleForDates(meeting, meeting, newMeeting, startDate, testDate, true);
        validateSchedulesForClosedAndCancelled(expectedUnchangedMeetingDates, expectedUnchangedMeetingDates);

    } 
    
    public void testHandleChangeInMeetingSchedule4() throws Exception {
        List<DateTime> expectedMeetingDates = new ArrayList<DateTime>();
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.MAY,23).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.JUNE,4).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.JUNE,18).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.JULY,2).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.JULY,16).toDateTime());

        DateTime startDate = new DateMidnight(2008,DateTimeConstants.MAY,23).toDateTime();
        DateTime testDate = new DateMidnight(2008, DateTimeConstants.MAY, 30).toDateTime(); 
        MeetingBO meeting = setupWeeklyMeeting(startDate, TestObjectFactory.EVERY_SECOND_WEEK);
        MeetingBO newMeeting = TestObjectFactory.getNewMeeting(RecurrenceType.WEEKLY, TestObjectFactory.EVERY_SECOND_WEEK, MeetingType.CUSTOMER_MEETING, WeekDay.WEDNESDAY);
        testChangeInMeetingScheduleForDates(meeting, newMeeting, startDate, testDate);
        validateSchedules(expectedMeetingDates);
    }   

    public void testHandleChangeInMeetingSchedule5() throws Exception {
        List<DateTime> expectedMeetingDates = new ArrayList<DateTime>();
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.APRIL,9).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.MAY,9).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.JUNE,20).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.JULY,20).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.AUGUST,20).toDateTime());

        DateTime startDate = new DateMidnight(2008,DateTimeConstants.APRIL,9).toDateTime();
        DateTime testDate = new DateMidnight(2008, DateTimeConstants.MAY, 3).toDateTime(); 
        MeetingBO meeting = setupMonthlyMeeting(startDate, 1, 9);
        MeetingBO newMeeting = TestObjectFactory.createMeeting(new MeetingBuilder().monthly().every(1).buildMonthlyForDayNumber(20));
 
        testChangeInMeetingScheduleForDates(meeting, newMeeting, startDate, testDate);
        validateSchedules(expectedMeetingDates);
    }      

    public void testHandleChangeInMeetingSchedule6() throws Exception {
        List<DateTime> expectedMeetingDates = new ArrayList<DateTime>();
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.APRIL,9).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.MAY,9).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.JUNE,20).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.JULY,20).toDateTime());
        expectedMeetingDates.add(new DateMidnight(2008,DateTimeConstants.AUGUST,20).toDateTime());

        DateTime startDate = new DateMidnight(2008,DateTimeConstants.APRIL,9).toDateTime();
        DateTime testDate = new DateMidnight(2008, DateTimeConstants.MAY, 31).toDateTime(); 
        MeetingBO meeting = setupMonthlyMeeting(startDate, 1, 9);
        MeetingBO newMeeting = TestObjectFactory.createMeeting(new MeetingBuilder().monthly().every(1).buildMonthlyForDayNumber(20));
 
        testChangeInMeetingScheduleForDates(meeting, newMeeting, startDate, testDate);
        validateSchedules(expectedMeetingDates);
    }   
    
    private MeetingBO setupWeeklyMeeting(DateTime startDate, short recurEvery) {
        new DateTimeService().setCurrentDateTime(startDate);
        return TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, recurEvery,
                CUSTOMER_MEETING));        
    }

    
    private MeetingBO setupMonthlyMeeting(DateTime startDate, int recurEvery, int dayNumber) throws MeetingException {
        new DateTimeService().setCurrentDateTime(startDate);
        return TestObjectFactory.createMeeting(new MeetingBuilder().monthly().every(recurEvery).buildMonthlyForDayNumber(dayNumber));
        
    }

    protected void testChangeInMeetingScheduleForDates(MeetingBO meeting, MeetingBO newMeeting, DateTime startDate, 
            DateTime testDate) throws Exception {
        testChangeInMeetingScheduleForDates(meeting, meeting, newMeeting, startDate, testDate, false);
    }
    
    protected void testChangeInMeetingScheduleForDates(MeetingBO customerMeeting, MeetingBO loanMeeting, MeetingBO newMeeting, DateTime startDate, DateTime testDate,
            boolean useCloxedAndCancelled) throws Exception {
        System.out.println("Start: " + startDate.toLocalDate() + ", Test: " + testDate.toLocalDate());
        accountBO = createLoanAccount(customerMeeting, loanMeeting);
        savingsBO = createSavingsAccount(customerMeeting);
        
        // center initially set up with meeting today
        center = TestObjectFactory.getCenter(center.getCustomerId());
        accountBO = TestObjectFactory.getObject(LoanBO.class, accountBO.getAccountId());
        if (useCloxedAndCancelled) {
            accountBO.changeStatus(AccountState.LOAN_CANCELLED, null, "");
            CustomerStatusEntity customerStatusEntity = new CustomerStatusEntity(CustomerStatus.GROUP_CLOSED);
            CustomerBOTestUtils.setCustomerStatus(group, customerStatusEntity);           
            StaticHibernateUtil.commitTransaction();
        }

        new DateTimeService().setCurrentDateTime(testDate);      
        center.updateMeeting(newMeeting);
        
        TestObjectFactory.updateObject(center);

        List<Days> workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();
        List<Holiday> holidays = new ArrayList<Holiday>();        
        center.getCustomerAccount().handleChangeInMeetingSchedule(workingDays, holidays);
        accountBO.handleChangeInMeetingSchedule(workingDays, holidays);
        
        StaticHibernateUtil.getTransaction().commit();
        StaticHibernateUtil.closeSession();
        
    }

    private void validateSchedules(List<DateTime> expectedMeetingDates) {
        validateSchedules(expectedMeetingDates, expectedMeetingDates);        
    }

    private void validateSchedulesForClosedAndCancelled(List<DateTime> expectedMeetingDates, List<DateTime> expectedLoanMeetingDates) {
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
        System.out.println("---");
        count = 0;
        for (AccountActionDateEntity actionDateEntity : accountBO.getAccountActionDates()) {
            if (count < expectedLoanMeetingDates.size()) {
                checkScheduleDates(expectedLoanMeetingDates.get(count), actionDateEntity);
            }
            ++count;
        }
        System.out.println("+++");
        count = 0;
        for (AccountActionDateEntity actionDateEntity : savingsBO.getAccountActionDates()) {
            if (count < expectedLoanMeetingDates.size()) {
                checkScheduleDates(expectedLoanMeetingDates.get(count), actionDateEntity);
            }
            ++count;
        }
        System.out.println("===");
    }  
    
    private void validateSchedules(List<DateTime> expectedMeetingDates, List<DateTime> expectedLoanMeetingDates) {
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
        System.out.println("---");
        count = 0;
        for (AccountActionDateEntity actionDateEntity : accountBO.getAccountActionDates()) {
            if (count < expectedLoanMeetingDates.size()) {
                checkScheduleDates(expectedLoanMeetingDates.get(count), actionDateEntity);
            }
            ++count;
        }
        System.out.println("+++");
        count = 0;
        for (AccountActionDateEntity actionDateEntity : savingsBO.getAccountActionDates()) {
            if (count < expectedLoanMeetingDates.size()) {
                checkScheduleDates(expectedLoanMeetingDates.get(count), actionDateEntity);
            }
            ++count;
        }
        System.out.println("===");
    }   
    
    private void checkScheduleDates(final DateTime expectedMeetingDate,
            final AccountActionDateEntity actionDateEntity) {
        System.out.println(new java.sql.Date(expectedMeetingDate.getMillis()) + ":" + actionDateEntity.getActionDate());

        Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(expectedMeetingDate.toDate()), DateUtils.getDateWithoutTimeStamp(actionDateEntity.getActionDate()));
    }
    
}
