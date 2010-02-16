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

package org.mifos.application.holiday.util.helpers;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.loan.business.LoanBOTestUtils;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.business.HolidayPK;
import org.mifos.application.holiday.business.RepaymentRuleEntity;
import org.mifos.application.holiday.persistence.HolidayPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.persistence.MeetingPersistence;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RankType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.config.ConfigurationManager;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
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
        savedConfigWorkingDays = configMgr.getProperty(FiscalCalendarRules.FiscalCalendarRulesWorkingDays).toString();
        savedConfigWorkingDays = savedConfigWorkingDays.replace("[", "");
        savedConfigWorkingDays = savedConfigWorkingDays.replace("]", "");
        setNewWorkingDays(sundayExcludedWorkingDays);
    }

    private void setSavedConfig() {
        configMgr.setProperty(FiscalCalendarRules.FiscalCalendarRulesWorkingDays, savedConfigWorkingDays);
        FiscalCalendarRules.reloadConfigWorkingDays();
    }

    private void setNewWorkingDays(String newWorkingDays) {
        configMgr.setProperty(FiscalCalendarRules.FiscalCalendarRulesWorkingDays, newWorkingDays);
        FiscalCalendarRules.reloadConfigWorkingDays();
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            TestObjectFactory.cleanUpAccount(accountId);
            StaticHibernateUtil.closeSession();
            TestObjectFactory.cleanUp(group);
            TestObjectFactory.cleanUp(center);
        } catch (Exception e) {
            // TODO Whoops, cleanup didnt work, reset db
            TestDatabase.resetMySQLDatabase();
        }
        StaticHibernateUtil.closeSession();
        setSavedConfig();
        super.tearDown();
    }

    private void toggleFirstDayOnOff(Short state) throws PersistenceException {

        WeekDaysEntity weekDay = (WeekDaysEntity) new MeetingPersistence().getPersistentObject(WeekDaysEntity.class,
                Short.valueOf("1"));
        weekDay.setWorkDay(state);
        weekDay.save();
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

        // Create Holiday
        RepaymentRuleEntity entity = new HolidayPersistence().getRepaymentRule((short) 1);
        HolidayPK holidayPK = new HolidayPK((short) 1, holidayStartDate);
        HolidayBO holidayEntity = new HolidayBO(holidayPK, holidayEndDate, "Same Day Holiday", entity);// the
        // last
        // string
        // has
        // no
        // effect.

        // disable holiday Validation because we are creating Holiday with fixed
        // dates
        // which one day it may violate the validation rules.
        holidayEntity.setValidationEnabled(false);

        holidayEntity.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        holidayEntity = (HolidayBO) TestObjectFactory.getObject(HolidayBO.class, holidayEntity.getHolidayPK());

        // assert that the holiday is created
       Assert.assertEquals("Same Day Holiday", holidayEntity.getHolidayName());

        Calendar adjustedCalendar = HolidayUtils.adjustDate(DateUtils.getCalendar(testDate), meeting);

       Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(adjustedCalendar.getTimeInMillis()).getTime(), DateUtils
                .getDateWithoutTimeStamp(testDate.getTime()).getTime());

        // Clean up the Holiday that was created
        TestObjectFactory.cleanUp(holidayEntity);
    }

    public void testNexteMeetingOrRepayment() throws Exception {
        Date holidayStartDate = getDate("20/08/2007");
        Date holidayEndDate = getDate("24/08/2007");

        Date testDate = getDate("22/08/2007");

        // Create Holiday
        HolidayPK holidayPK = new HolidayPK((short) 1, holidayStartDate);
        RepaymentRuleEntity entity = new HolidayPersistence().getRepaymentRule((short) 2);
        HolidayBO holidayEntity = new HolidayBO(holidayPK, holidayEndDate, "Next Meeting Or Repayment Holiday", entity);// the
        // last
        // string
        // has
        // no
        // effect.

        // Disable holiday Validation because we are creating Holiday with fixed
        // dates
        // which one day it may violate the validation rules.
        holidayEntity.setValidationEnabled(false);

        holidayEntity.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        holidayEntity = (HolidayBO) TestObjectFactory.getObject(HolidayBO.class, holidayEntity.getHolidayPK());

        // assert that the holiday is created
       Assert.assertEquals("Next Meeting Or Repayment Holiday", holidayEntity.getHolidayName());

        Calendar adjustedCalendar = HolidayUtils.adjustDate(DateUtils.getCalendarDate(testDate.getTime()), meeting);

       Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(adjustedCalendar.getTimeInMillis()).getTime(), DateUtils
                .getDateWithoutTimeStamp(getDate("30/08/2007").getTime()).getTime()); // was
        // 25/08/2006

        // Clean up the Holiday that was created
        TestObjectFactory.cleanUp(holidayEntity);
    }

    public void testNextWorkingDay() throws Exception {

        Date holidayStartDate = getDate("20/08/2007");
        Date holidayEndDate = getDate("22/08/2007");

        Date testDate = getDate("21/08/2007");

        HolidayBO holidayEntity = createHoliday(holidayStartDate, holidayEndDate, Short.valueOf("3"),
                "Next Working Day Holiday");

        holidayEntity = (HolidayBO) TestObjectFactory.getObject(HolidayBO.class, holidayEntity.getHolidayPK());

        // assert that the holiday is created
       Assert.assertEquals("Next Working Day Holiday", holidayEntity.getHolidayName());

        Calendar adjustedCalendar = HolidayUtils.adjustDate(DateUtils.getCalendarDate(testDate.getTime()), meeting);

       Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(adjustedCalendar.getTimeInMillis()).getTime(), DateUtils
                .getDateWithoutTimeStamp(getDate("23/08/2007").getTime()).getTime());

        // Clean up the Holiday that was created
        TestObjectFactory.cleanUp(holidayEntity);

    }

    public void testHolidayCombinations() throws Exception {
        for (int i = 0; i < inputDates.length; i++) {

            Date inputDate = getDate(inputDates[i]);
            Date outputDate = getDate(outputDates[i]);

            HolidayBO[] holidays = new HolidayBO[3];

            holidays = createHolidayCollection(holidyRanges[i], rpaymentRules[i]);

            String[] meetingData = meetings[i].split("-");
            startDate = getDate(meetingData[1]);

            if (meetingData[0].equalsIgnoreCase("Weekly")) {
                meeting = createWeeklyMeeting(WeekDay.THURSDAY, recurAfter, startDate);
            } else if (meetingData[0].equalsIgnoreCase("Monthly")) {
                meeting = createMonthlyMeetingOnWeekDay(WeekDay.THURSDAY, RankType.FIRST, recurAfter, startDate);
            }
            // ///////////////////////////////////////////////////////

            Calendar adjustedCalendar = HolidayUtils
                    .adjustDate(DateUtils.getCalendarDate(inputDate.getTime()), meeting);

           Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(adjustedCalendar.getTimeInMillis()).getTime(), DateUtils
                    .getDateWithoutTimeStamp(outputDate.getTime()).getTime());

            // Clean up the Holiday that was created
            for (int j = 0; j < holidays.length; j++)
                TestObjectFactory.cleanUp(holidays[j]);
        }
    }

    public void testRescheduleLoanRepaymentDates() throws Exception {
        // Make SUNDAY (FirstDay) as Working Day.
        toggleFirstDayOnOff(Short.valueOf("1"));
        setNewWorkingDays(sundayIncludedWorkingDays);

        // Create the Schedule to be used in testing
        Calendar startDate = Calendar.getInstance();
        // TODO: It is ugly to call into another test like this,
        // and this code also isn't cleaning up the data correctly.
        LoanBOTestUtils loanBOTestUtils = new LoanBOTestUtils();

        LoanScheduleEntity[] loanRepaymentSchedule = loanBOTestUtils.createLoanRepaymentSchedule();

        AccountBO accountBO = (AccountBO) new MeetingPersistence().getPersistentObject(AccountBO.class,
                loanRepaymentSchedule[0].getAccount().getAccountId());

        loanRepaymentSchedule = loanBOTestUtils.getSortedAccountActionDateEntity(accountBO.getAccountActionDates());

        startDate.setTime(loanRepaymentSchedule[0].getActionDate());

        for (int i = 0; i < loanRepaymentSchedule.length; i++) {
            // System.out.println("[DATE] ActionDate     = " +
            // loanRepaymentSchedule[i].getActionDate().toLocaleString());
           Assert.assertEquals(startDate, DateUtils.getCalendarDate(loanRepaymentSchedule[i].getActionDate().getTime()));

            startDate.add(Calendar.DAY_OF_MONTH, 7);
        }
        // ///////////////////////////////////////////////////////////

        // Create Holiday that the first ActionDate fall in it.
        Date holidayStartDate = loanRepaymentSchedule[0].getActionDate();

        Calendar end = DateUtils.getCalendarDate(loanRepaymentSchedule[0].getActionDate().getTime());
        end.add(Calendar.DAY_OF_MONTH, 3);
        Date holidayEndDate = end.getTime();// getDate("31/03/2007");

        // Holiday RepaymentRule is Next Working Day.
        HolidayBO holiday = createHoliday(holidayStartDate, holidayEndDate, Short.valueOf("3"),
                "testRescheduleLoanRepaymentDates");

        holiday = (HolidayBO) TestObjectFactory.getObject(HolidayBO.class, holiday.getHolidayPK());

        // assert that the holiday is created
       Assert.assertEquals("testRescheduleLoanRepaymentDates", holiday.getHolidayName());
        // /////////////////////////////////////////////////////////////
        HolidayUtils.rescheduleLoanRepaymentDates(holiday);
        // ///////////////////////////////////////////////////////////

        accountBO = loanRepaymentSchedule[0].getAccount();

        accountBO = (AccountBO) new MeetingPersistence().getPersistentObject(AccountBO.class, loanRepaymentSchedule[0]
                .getAccount().getAccountId());

        Set<AccountActionDateEntity> actionDateEntities = accountBO.getAccountActionDates();

        loanRepaymentSchedule = loanBOTestUtils.getSortedAccountActionDateEntity(actionDateEntities);
        startDate.setTime(holidayStartDate); // for calculating the other dates
        // correctly

        for (int i = 0; i < loanRepaymentSchedule.length; i++) {
            // System.out.println("[RE_DATE] ActionDate     = " +
            // loanRepaymentSchedule[i].getActionDate().toLocaleString());

            if (i == 0) { // the one that we made it fall in holiday
                Calendar adjustedDate = DateUtils.getCalendarDate(holidayStartDate.getTime());
                adjustedDate.add(Calendar.DAY_OF_MONTH, 4);
                // System.out.println("[RE_DATE] CalculatedDate = " +
                // adjustedDate.getTime().toLocaleString());
               Assert.assertEquals(adjustedDate, DateUtils
                        .getCalendarDate(loanRepaymentSchedule[i].getActionDate().getTime()));
            } else {
                // System.out.println("[RE_DATE] CalculatedDate = " +
                // startDate.getTime().toLocaleString());
               Assert.assertEquals(startDate, DateUtils.getCalendarDate(loanRepaymentSchedule[i].getActionDate().getTime()));
            }

            startDate.add(Calendar.DAY_OF_MONTH, 7);
        }

        // ///////////////////////////////////////////////////////////

        // Clean up the Holiday that was created
        TestObjectFactory.cleanUp(holiday);

        // Create Non Working Day
        toggleFirstDayOnOff(Short.valueOf("0"));
        setNewWorkingDays(sundayIncludedWorkingDays);
        
        // FIXME this test leaves CUSTOMER table in dirty state
        TestDatabase.resetMySQLDatabase();
    }

    public void testRescheduleSavingDates() throws Exception {
        // Make SUNDAY (FirstDay) as Working Day.
        toggleFirstDayOnOff(Short.valueOf("1"));
        setNewWorkingDays(sundayIncludedWorkingDays);

        // Create the Schedule to be used in testing
        Calendar startDate = Calendar.getInstance();

        SavingsBO savingBO = createSavingSchedule();

        Set<AccountActionDateEntity> actionDateEntities = savingBO.getAccountActionDates();

        AccountActionDateEntity[] savingSchedule = new AccountActionDateEntity[actionDateEntities.size()];
        // Sort Dates
        for (AccountActionDateEntity actionDateEntity : actionDateEntities) {
            savingSchedule[actionDateEntity.getInstallmentId().intValue() - 1] = actionDateEntity;
        }

        startDate.setTime(savingSchedule[0].getActionDate());

        for (int i = 0; i < savingSchedule.length; i++) {
            // System.out.println("[SAVING] ActionDate = " +
            // savingSchedule[i].getActionDate().toLocaleString());
           Assert.assertEquals(startDate, DateUtils.getCalendarDate(savingSchedule[i].getActionDate().getTime()));
            startDate.add(Calendar.DAY_OF_MONTH, 7);
        }
        // ///////////////////////////////////////////////////////////

        // Create Holiday that the first ActionDate fall in it.
        Date holidayStartDate = savingSchedule[0].getActionDate();

        Calendar end = DateUtils.getCalendarDate(savingSchedule[0].getActionDate().getTime());
        end.add(Calendar.DAY_OF_MONTH, 3);
        Date holidayEndDate = end.getTime();

        // Holiday RepaymentRule is Next Working Day.
        HolidayBO holiday = createHoliday(holidayStartDate, holidayEndDate, Short.valueOf("3"),
                "testRescheduleSavingDates");

        holiday = (HolidayBO) TestObjectFactory.getObject(HolidayBO.class, holiday.getHolidayPK());

        // assert that the holiday is created
       Assert.assertEquals("testRescheduleSavingDates", holiday.getHolidayName());
        // /////////////////////////////////////////////////////////////

        HolidayUtils.rescheduleSavingDates(holiday);

        // ///////////////////////////////////////////////////////////

        AccountBO accountBO = savingSchedule[0].getAccount();

        accountBO = (AccountBO) new MeetingPersistence().getPersistentObject(AccountBO.class, savingSchedule[0]
                .getAccount().getAccountId());

        actionDateEntities = accountBO.getAccountActionDates();

        savingSchedule = new AccountActionDateEntity[actionDateEntities.size()];
        // Sort Dates
        for (AccountActionDateEntity actionDateEntity : actionDateEntities) {
            savingSchedule[actionDateEntity.getInstallmentId().intValue() - 1] = actionDateEntity;
        }

        startDate.setTime(holidayStartDate); // for calculating the other dates
        // correctly

        for (int i = 0; i < savingSchedule.length; i++) {
            if (i == 0) { // the actionDate that falls in the created holiday.
                Calendar adjustedDate = DateUtils.getCalendarDate(holidayStartDate.getTime());
                adjustedDate.add(Calendar.DAY_OF_MONTH, 4);

                // System.out.println("[RE_SAVING] CalculatedDate = " +
                // adjustedDate.getTime().toLocaleString());
               Assert.assertEquals(adjustedDate, DateUtils.getCalendarDate(savingSchedule[i].getActionDate().getTime()));
            } else {
                // System.out.println("[RE_SAVING] CalculatedDate = " +
                // startDate.getTime().toLocaleString());
               Assert.assertEquals(startDate, DateUtils.getCalendarDate(savingSchedule[i].getActionDate().getTime()));
            }

            startDate.add(Calendar.DAY_OF_MONTH, 7);
        }

        // ///////////////////////////////////////////////////////////

        // Clean up the Holiday that was created
        // TODO: This should be in tearDown
        TestObjectFactory.cleanUp(holiday);

        // Create Non Working Day
        toggleFirstDayOnOff(Short.valueOf("0"));
        setNewWorkingDays(sundayExcludedWorkingDays);
    }

    private HolidayBO[] createHolidayCollection(String holidayDateList, String ruleList) throws Exception {
        Date holidayStartDate, holidayEndDate;
        String[] holidayRanges = holidayDateList.split(",");
        HolidayBO[] holidays = new HolidayBO[holidayRanges.length];
        String[] rules = ruleList.split(",");

        for (int i = 0; i < holidayRanges.length; i++) {
            String[] holidayStartEnd = holidayRanges[i].split("-");

            holidayStartDate = getDate(holidayStartEnd[0]);
            holidayEndDate = getDate(holidayStartEnd[1]);

            holidays[i] = createHoliday(holidayStartDate, holidayEndDate, Short.valueOf(rules[i]),
                    "testHolidayCombination_1");

            holidays[i] = (HolidayBO) TestObjectFactory.getObject(HolidayBO.class, holidays[i].getHolidayPK());

            // assert that the holiday is created
           Assert.assertEquals("testHolidayCombination_1", holidays[i].getHolidayName());
        }

        return holidays;
    }

    private HolidayBO createHoliday(Date holidayStartDate, Date holidayEndDate, Short repaymentRule, String holidayName)
            throws ApplicationException {
        // Create Holiday
        RepaymentRuleEntity entity = new HolidayPersistence().getRepaymentRule(repaymentRule);
        HolidayPK holidayPK = new HolidayPK((short) 1, holidayStartDate);
        HolidayBO holidayEntity = new HolidayBO(holidayPK, holidayEndDate, holidayName, entity);// the
        // last
        // string
        // has
        // no
        // effect.

        // Disable holiday Validation because we are creating Holiday with fixed
        // dates
        // which one day it may violate the validation rules.
        holidayEntity.setValidationEnabled(false);

        holidayEntity.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        return holidayEntity;
    }

    private MeetingBO createWeeklyMeeting(WeekDay weekDay, Short recurAfer, Date startDate) throws MeetingException {
        return new MeetingBO(weekDay, recurAfer, startDate, MeetingType.CUSTOMER_MEETING, "MeetingPlace");
    }

    private MeetingBO createMonthlyMeetingOnWeekDay(WeekDay weekDay, RankType rank, Short recurAfer, Date startDate)
            throws MeetingException {
        return new MeetingBO(weekDay, rank, recurAfer, startDate, MeetingType.CUSTOMER_MEETING, "MeetingPlace");
    }

    private SavingsBO createSavingSchedule() throws Exception {
        createInitialObjects();
        savingsProduct = TestObjectFactory.createSavingsProduct("dfasdasd1", "sad1",
                RecommendedAmountUnit.COMPLETE_GROUP);
        SavingsBO savings = new SavingsTestHelper().createSavingsAccount(savingsProduct, group,
                AccountState.SAVINGS_ACTIVE, TestUtils.makeUser());
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        accountId = savings.getAccountId();
        return TestObjectFactory.getObject(SavingsBO.class, accountId);
    }

    private void createInitialObjects() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
    }

    public void testInHolidayThrowsNPEIfThruDateIsNull() throws Exception {
        Short branchId = Short.valueOf("3");
        Date holidayFromDate = DateUtils.getDate(2008, Calendar.MAY, 22);
        RepaymentRuleEntity repaymentRuleEntity = new RepaymentRuleEntity(RepaymentRuleTypes.NEXT_WORKING_DAY
                .getValue(), "next working day");
        List<HolidayBO> holidays = new ArrayList<HolidayBO>();
        holidays.add(new HolidayBO(new HolidayPK(branchId, holidayFromDate), null, "TestHoliday", repaymentRuleEntity));
        try {
            HolidayUtils.inHoliday(DateUtils.getCalendar(DateUtils.getDate(2008, Calendar.MAY, 23)), holidays);
            Assert.fail("Should throw null pointer exception");
        } catch (NullPointerException e) {
        }

    }

}
