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

package org.mifos.application.holiday.util.helpers;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.holiday.persistence.HolidayDetails;
import org.mifos.application.holiday.persistence.HolidayServiceFacadeWebTier;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RankOfDay;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.config.ConfigurationManager;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class HolidayUtilsIntegrationTest extends MifosIntegrationTestCase {

    public HolidayUtilsIntegrationTest() throws Exception {
        super();
    }

    private MeetingBO meeting;
    private Short recurAfter = Short.valueOf("1"); // recur After January
    private Date startDate;
    private ConfigurationManager configMgr = null;
    private String savedConfigWorkingDays = null;
    private String sundayExcludedWorkingDays = "MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY";
    private String sundayIncludedWorkingDays = "MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY";

    private String[] inputDates = { "21/08/2007", "21/08/2007", "21/08/2007", "26/08/2007", "23/08/2007" },
            outputDates = { "21/08/2007", "25/08/2007", "27/08/2007", "29/08/2007", "06/09/2007" };// "08/09/2007"};

    private String[] holidyRanges = { "20/08/2007 - 22/08/2007, 22/08/2007 - 24/08/2007", // [Holiday
            // -
            // Holiday]
            // [Same
            // Day
            // -
            // Same
            // Day]
            // case
            "20/08/2007 - 22/08/2007, 22/08/2007 - 24/08/2007", // [Holiday -
            // Holiday]
            // [Next Working
            // Day - Next
            // Working Day]
            // case
            "20/08/2007 - 22/08/2007, 22/08/2007 - 25/08/2007", // [Holiday -
            // Holiday -
            // Non-Working
            // Day] [Next
            // Working Day -
            // Next Working
            // Day] case
            "27/08/2007 - 28/08/2007", // [Non-Working Day - Holiday] [Next
            // Working Day - Next Working Day] case
            "20/08/2007 - 25/08/2007, 27/08/2007 - 01/09/2007" // [Holiday -
    // Non-Working
    // Day - Holiday]
    // [Next Meeting
    // - Next
    // Meeting] case
    };

    private String[] rpaymentRules = { "1,1", "3,3", "3,3", "3,3", "2,2" };
    private String[] meetings = { "Monthly-18/08/2007", "Monthly-18/08/2007", "Monthly-18/08/2007",
            "Monthly-18/08/2007", "Weekly-18/08/2007" };

    private CenterBO center;
    private GroupBO group;
    private SavingsOfferingBO savingsProduct;
    private Integer accountId;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        startDate = getDate("18/08/2007");
        meeting = createWeeklyMeeting(WeekDay.THURSDAY, recurAfter, startDate);

        // ///////////////////
        // Make SUNDAY as Non Working Day
        toggleFirstDayOnOff(Short.valueOf("0"));
        // ///////////////////
        configMgr = ConfigurationManager.getInstance();
        savedConfigWorkingDays = configMgr.getProperty(new FiscalCalendarRules().FiscalCalendarRulesWorkingDays)
                .toString();
        savedConfigWorkingDays = savedConfigWorkingDays.replace("[", "");
        savedConfigWorkingDays = savedConfigWorkingDays.replace("]", "");
        setNewWorkingDays(sundayExcludedWorkingDays);
    }

    private void setSavedConfig() {
        configMgr.setProperty(new FiscalCalendarRules().FiscalCalendarRulesWorkingDays, savedConfigWorkingDays);
        new FiscalCalendarRules().reloadConfigWorkingDays();
    }

    private void setNewWorkingDays(String newWorkingDays) {
        configMgr.setProperty(new FiscalCalendarRules().FiscalCalendarRulesWorkingDays, newWorkingDays);
        new FiscalCalendarRules().reloadConfigWorkingDays();
    }

    @Override
    protected void tearDown() throws Exception {
        rollback();
        setSavedConfig();
        super.tearDown();
    }

    private void rollback() {
        Transaction transaction = StaticHibernateUtil.getSessionTL().getTransaction();
        if(transaction.isActive()){
            transaction.rollback();
        }
    }

    private void createHolidayForHeadOffice(HolidayDetails holidayDetails) throws ServiceException {
        List<Short> officeIds = new LinkedList<Short>();
        officeIds.add((short) 1);
        new HolidayServiceFacadeWebTier(new OfficePersistence()).createHoliday(holidayDetails, officeIds);
    }


    private static void toggleFirstDayOnOff(Short state) {
        List<WeekDay> workingDays = new FiscalCalendarRules().getWorkingDays();
        workingDays.remove(0);
        new FiscalCalendarRules().setWorkingDays(workingDays);
    }

    public void testNonWorkingDay() throws Exception {
        setNewWorkingDays(sundayIncludedWorkingDays);
        Date testDate = getDate("19/08/2007");
        Calendar adjustedCalendar = HolidayUtils.adjustDate(DateUtils.getCalendar(testDate), meeting);
        Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(adjustedCalendar.getTimeInMillis()).getTime(), DateUtils
                .getDateWithoutTimeStamp(getDate("19/08/2007").getTime()).getTime());
    }

    public void testSameDay() throws Exception {
        Date holidayStartDate = getDate("20/08/2007");
        Date holidayEndDate = getDate("22/08/2007");
        Date testDate = getDate("21/08/2007");
        HolidayDetails holidayDetails = new HolidayDetails("Same Day Holiday", holidayStartDate, holidayEndDate, RepaymentRuleTypes.SAME_DAY );
        holidayDetails.disableValidation(true);
        createHolidayForHeadOffice(holidayDetails);
        Calendar adjustedCalendar = HolidayUtils.adjustDate(DateUtils.getCalendar(testDate), meeting);
        Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(adjustedCalendar.getTimeInMillis()).getTime(), DateUtils
                .getDateWithoutTimeStamp(testDate.getTime()).getTime());
    }

    public void testNexteMeetingOrRepayment() throws Exception {
        Date holidayStartDate = getDate("20/08/2007");
        Date holidayEndDate = getDate("24/08/2007");
        Date testDate = getDate("22/08/2007");
        HolidayDetails holidayDetails = new HolidayDetails("Next Meeting Or Repayment Holiday", holidayStartDate, holidayEndDate, RepaymentRuleTypes.NEXT_MEETING_OR_REPAYMENT );
        holidayDetails.disableValidation(true);
        createHolidayForHeadOffice(holidayDetails);
        Calendar adjustedCalendar = HolidayUtils.adjustDate(DateUtils.getCalendarDate(testDate.getTime()), meeting);
        Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(adjustedCalendar.getTimeInMillis()).getTime(), DateUtils
                .getDateWithoutTimeStamp(getDate("30/08/2007").getTime()).getTime()); // was
        // 25/08/2006

    }

    public void testNextWorkingDay() throws Exception {
        Date holidayStartDate = getDate("20/08/2007");
        Date holidayEndDate = getDate("22/08/2007");
        Date testDate = getDate("21/08/2007");
        HolidayDetails holidayDetails = new HolidayDetails("Next Working Day Holiday", holidayStartDate, holidayEndDate, RepaymentRuleTypes.NEXT_WORKING_DAY );
        holidayDetails.disableValidation(true);
        createHolidayForHeadOffice(holidayDetails);
        Calendar adjustedCalendar = HolidayUtils.adjustDate(DateUtils.getCalendarDate(testDate.getTime()), meeting);
        Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(adjustedCalendar.getTimeInMillis()).getTime(), DateUtils
                .getDateWithoutTimeStamp(getDate("23/08/2007").getTime()).getTime());
    }

    public void testHolidayCombinations() throws Exception {
        for (int i = 0; i < inputDates.length; i++) {
            Date inputDate = getDate(inputDates[i]);
            Date outputDate = getDate(outputDates[i]);
            createHolidayCollection(holidyRanges[i], rpaymentRules[i]);
            String[] meetingData = meetings[i].split("-");
            startDate = getDate(meetingData[1]);
            if (meetingData[0].equalsIgnoreCase("Weekly")) {
                meeting = createWeeklyMeeting(WeekDay.THURSDAY, recurAfter, startDate);
            } else if (meetingData[0].equalsIgnoreCase("Monthly")) {
                meeting = createMonthlyMeetingOnWeekDay(WeekDay.THURSDAY, RankOfDay.FIRST, recurAfter, startDate);
            }
            Calendar adjustedCalendar = HolidayUtils
                    .adjustDate(DateUtils.getCalendarDate(inputDate.getTime()), meeting);
            Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(adjustedCalendar.getTimeInMillis()).getTime(),
                    DateUtils.getDateWithoutTimeStamp(outputDate.getTime()).getTime());
            rollback();
        }
    }

    private void createHolidayCollection(String holidayDateList, String ruleList) throws Exception {
        Date holidayStartDate, holidayEndDate;
        String[] holidayRanges = holidayDateList.split(",");
        String[] rules = ruleList.split(",");
        for (int i = 0; i < holidayRanges.length; i++) {
            String[] holidayStartEnd = holidayRanges[i].split("-");
            holidayStartDate = getDate(holidayStartEnd[0]);
            holidayEndDate = getDate(holidayStartEnd[1]);
            HolidayDetails holidayDetails = new HolidayDetails("testHolidayCombination_1", holidayStartDate, holidayEndDate, RepaymentRuleTypes.fromShort(Short.valueOf(rules[i])));
            holidayDetails.disableValidation(true);
            createHolidayForHeadOffice(holidayDetails);
        }
    }

    private MeetingBO createWeeklyMeeting(WeekDay weekDay, Short recurAfer, Date startDate) throws MeetingException {
        return new MeetingBO(weekDay, recurAfer, startDate, MeetingType.CUSTOMER_MEETING, "MeetingPlace");
    }

    private MeetingBO createMonthlyMeetingOnWeekDay(WeekDay weekDay, RankOfDay rank, Short recurAfer, Date startDate) throws MeetingException {
        return new MeetingBO(weekDay, rank, recurAfer, startDate, MeetingType.CUSTOMER_MEETING, "MeetingPlace");
    }

    private SavingsBO createSavingSchedule() throws Exception {
        createInitialObjects();
        savingsProduct = TestObjectFactory.createSavingsProduct("dfasdasd1", "sad1",
                RecommendedAmountUnit.COMPLETE_GROUP);
        SavingsBO savings = new SavingsTestHelper().createSavingsAccount(savingsProduct, group,
                AccountState.SAVINGS_ACTIVE, TestUtils.makeUser());
        accountId = savings.getAccountId();
        return TestObjectFactory.getObject(SavingsBO.class, accountId);
    }

    private void createInitialObjects() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE,
                center);
    }
}
