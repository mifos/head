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

package org.mifos.application.meeting.business;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingConstants;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RankOfDay;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.schedule.ScheduledEvent;
import org.mifos.schedule.ScheduledEventFactory;

public class MeetingBOIntegrationTest extends MifosIntegrationTestCase {
    public MeetingBOIntegrationTest() throws Exception {
        super();
    }

    private static final Short ONE = Short.valueOf("1");
    private static final Short TWO = Short.valueOf("2");
    private static final Short THREE = Short.valueOf("3");
    private static final Short FIVE = Short.valueOf("5");
    private static final Short SIX = Short.valueOf("6");
    private static final Short TEN = Short.valueOf("10");
    private MeetingBO meeting;
    private Date startDate;
    private Date endDate;
    private Short dayNumber;
    private int occurrences;

    /**
     * Performs the following test: 1. Creates test data - from Sunday to Saturday, and its expected dates after
     * invoking meeting.getFirstDateForWeek() method 2. Invokes verifyFirstDateForWeekForGivenStartDate for each test
     * data set and asserts that meeting.getFirstDateForWeek() method works as expected
     */
    public void testFirstDateForWeekMethodForEachDayOfTheWeek() throws Exception {

        // Set test data - from Sunday to Saturday, and its expected dates after
        // invoking meeting.getFirstDateForWeek() method
        Date sundayStartDate = new GregorianCalendar(2008, Calendar.JUNE, 8).getTime(); // SUNDAY
        // 8
        // June
        Date[] expectedDatesForSunday = { sundayStartDate, // SUNDAY 8 June
                new GregorianCalendar(2008, Calendar.JUNE, 9).getTime(), // MONDAY
                // 9
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 10).getTime(), // TUESDAY
                // 10
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 11).getTime(), // WEDNESDAY
                // 11
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 12).getTime(), // THURSDAY
                // 12
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 13).getTime(), // FRIDAY
                // 13
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 14).getTime(), // SATURDAY
        // 14
        // June
        };

        Date mondayStartDate = new GregorianCalendar(2008, Calendar.JUNE, 9).getTime(); // MONDAY
        // 9
        // June
        Date[] expectedDatesForMonday = { new GregorianCalendar(2008, Calendar.JUNE, 15).getTime(), // SUNDAY
                // 15
                // June
                mondayStartDate, // MONDAY 9 June
                new GregorianCalendar(2008, Calendar.JUNE, 10).getTime(), // TUESDAY
                // 10
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 11).getTime(), // WEDNESDAY
                // 11
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 12).getTime(), // THURSDAY
                // 12
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 13).getTime(), // FRIDAY
                // 13
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 14).getTime(), // SATURDAY
        // 14
        // June
        };

        Date tuesdayStartDate = new GregorianCalendar(2008, Calendar.JUNE, 10).getTime(); // TUESDAY
        // 10
        // June
        Date[] expectedDatesForTuesday = { new GregorianCalendar(2008, Calendar.JUNE, 15).getTime(), // SUNDAY
                // 15
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 16).getTime(), // MONDAY
                // 16
                // June
                tuesdayStartDate, // TUESDAY 10 June
                new GregorianCalendar(2008, Calendar.JUNE, 11).getTime(), // WEDNESDAY
                // 11
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 12).getTime(), // THURSDAY
                // 12
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 13).getTime(), // FRIDAY
                // 13
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 14).getTime(), // SATURDAY
        // 14
        // June
        };

        Date wednesdayStartDate = new GregorianCalendar(2008, Calendar.JUNE, 11).getTime(); // WEDNESDAY
        // 11
        // June
        Date[] expectedDatesForWednesday = { new GregorianCalendar(2008, Calendar.JUNE, 15).getTime(), // SUNDAY
                // 15
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 16).getTime(), // MONDAY
                // 16
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 17).getTime(), // TUESDAY
                // 17
                // June
                wednesdayStartDate, // WEDNESDAY 11 June
                new GregorianCalendar(2008, Calendar.JUNE, 12).getTime(), // THURSDAY
                // 12
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 13).getTime(), // FRIDAY
                // 13
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 14).getTime(), // SATURDAY
        // 14
        // June
        };

        Date thursdayStartDate = new GregorianCalendar(2008, Calendar.JUNE, 12).getTime(); // THURSDAY
        // 12
        // June
        Date[] expectedDatesForThursday = { new GregorianCalendar(2008, Calendar.JUNE, 15).getTime(), // SUNDAY
                // 15
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 16).getTime(), // MONDAY
                // 16
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 17).getTime(), // TUESDAY
                // 17
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 18).getTime(), // WEDNESDAY
                // 18
                // June
                thursdayStartDate, // THURSDAY 12 June
                new GregorianCalendar(2008, Calendar.JUNE, 13).getTime(), // FRIDAY
                // 13
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 14).getTime(), // SATURDAY
        // 14
        // June
        };

        Date fridayStartDate = new GregorianCalendar(2008, Calendar.JUNE, 13).getTime(); // FRIDAY
        // 13
        // June
        Date[] expectedDatesForFriday = { new GregorianCalendar(2008, Calendar.JUNE, 15).getTime(), // SUNDAY
                // 15
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 16).getTime(), // MONDAY
                // 16
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 17).getTime(), // TUESDAY
                // 17
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 18).getTime(), // WEDNESDAY
                // 18
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 19).getTime(), // THURSDAY
                // 19
                // June
                fridayStartDate, // FRIDAY 13 June
                new GregorianCalendar(2008, Calendar.JUNE, 14).getTime(), // SATURDAY
        // 14
        // June
        };

        Date saturdayStartDate = new GregorianCalendar(2008, Calendar.JUNE, 14).getTime(); // SATURDAY
        // 14
        // June
        Date[] expectedDatesForSaturday = { new GregorianCalendar(2008, Calendar.JUNE, 15).getTime(), // SUNDAY
                // 15
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 16).getTime(), // MONDAY
                // 16
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 17).getTime(), // TUESDAY
                // 17
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 18).getTime(), // WEDNESDAY
                // 18
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 19).getTime(), // THURSDAY
                // 19
                // June
                new GregorianCalendar(2008, Calendar.JUNE, 20).getTime(), // FRIDAY
                // 20
                // June
                saturdayStartDate, // SATURDAY 14 June
        };

        // Verify that meeting.getFirstDateForWeek() method works as expected
        verifyFirstDateForWeekForGivenStartDate(sundayStartDate, expectedDatesForSunday);
        verifyFirstDateForWeekForGivenStartDate(mondayStartDate, expectedDatesForMonday);
        verifyFirstDateForWeekForGivenStartDate(tuesdayStartDate, expectedDatesForTuesday);
        verifyFirstDateForWeekForGivenStartDate(wednesdayStartDate, expectedDatesForWednesday);
        verifyFirstDateForWeekForGivenStartDate(thursdayStartDate, expectedDatesForThursday);
        verifyFirstDateForWeekForGivenStartDate(fridayStartDate, expectedDatesForFriday);
        verifyFirstDateForWeekForGivenStartDate(saturdayStartDate, expectedDatesForSaturday);
    }

    /**
     * Verify first date for week is resolved as expected for given start date.
     *
     * Note, it invokes verifyFirstDateForWeekForGivenStartDateAndWeekDay for each day of the week
     */
    private void verifyFirstDateForWeekForGivenStartDate(final Date startDate, final Date[] expectedDates)
            throws MeetingException {
        verifyFirstDateForWeekForGivenStartDateAndWeekDay(WeekDay.SUNDAY, startDate, expectedDates[0]);
        verifyFirstDateForWeekForGivenStartDateAndWeekDay(WeekDay.MONDAY, startDate, expectedDates[1]);
        verifyFirstDateForWeekForGivenStartDateAndWeekDay(WeekDay.TUESDAY, startDate, expectedDates[2]);
        verifyFirstDateForWeekForGivenStartDateAndWeekDay(WeekDay.WEDNESDAY, startDate, expectedDates[3]);
        verifyFirstDateForWeekForGivenStartDateAndWeekDay(WeekDay.THURSDAY, startDate, expectedDates[4]);
        verifyFirstDateForWeekForGivenStartDateAndWeekDay(WeekDay.FRIDAY, startDate, expectedDates[5]);
        verifyFirstDateForWeekForGivenStartDateAndWeekDay(WeekDay.SATURDAY, startDate, expectedDates[6]);
    }

    /**
     * Asserts that meeting.getFirstDateForWeek(startDate) returns a date matching the expected date.
     *
     * A dummy meeting is created with the given weekDay, and then the resulting date from
     * meeting.getFirstDateForWeek(startDate) is asserted to be the expected date
     *
     */
    private void verifyFirstDateForWeekForGivenStartDateAndWeekDay(final WeekDay weekDay, final Date startDate,
            final Date expectedDate) throws MeetingException {
        // Assert getFirstDateForWeek works as expected for meeting with given
        // weekDay
        MeetingBO meeting = createWeeklyMeeting(weekDay, ONE, new Date());
        ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(meeting);
        DateTime nearestDate = scheduledEvent.nearestMatchingDateBeginningAt(new DateTime(startDate));

        Assert.assertTrue(nearestDate.toDate().compareTo(expectedDate) == 0);
    }

    public void testFailureIsValidScheduleDate_MeetingDateNull() throws Exception {
        startDate = getDate("15/11/2003");
        occurrences = 5;
        dayNumber = Short.valueOf("29");
        meeting = createMonthlyMeetingOnDate(dayNumber, ONE, startDate);
        try {
            meeting.isValidMeetingDate(null, occurrences);
        } catch (MeetingException me) {
            Assert.assertTrue(true);
            Assert.assertEquals(MeetingConstants.INVALID_MEETINGDATE, me.getKey());
        }
    }

    public void testFailureIsValidScheduleDate_OccurencesNull() throws Exception {
        startDate = getDate("15/11/2003");
        occurrences = 5;
        dayNumber = Short.valueOf("29");
        meeting = createMonthlyMeetingOnDate(dayNumber, ONE, startDate);
        try {
            meeting.isValidMeetingDate(getDate("29/02/2004"), 0);
            Assert.fail();
        } catch (MeetingException me) {
            Assert.assertTrue(true);
            Assert.assertEquals(MeetingConstants.INVALID_OCCURENCES, me.getKey());
        }
    }

    // ExpectedList("29/11/2003,29/12/2003,29/01/2004,29/02/2004,29/03/2004");
    public void testIsValidScheduleDate_MonthlyOnDate_5Occurences() throws Exception {
        startDate = getDate("15/11/2003");
        occurrences = 5;
        dayNumber = Short.valueOf("29");
        meeting = createMonthlyMeetingOnDate(dayNumber, ONE, startDate);
        Assert.assertTrue(meeting.isValidMeetingDate(getDate("29/02/2004"), occurrences));
        Assert.assertFalse(meeting.isValidMeetingDate(getDate("28/02/2004"), occurrences));
    }

    // ExpectedList("25/11/2005,27/01/2006,31/03/2006,26/05/2006,28/07/2006,29/09/2006");
    public void testIsValidScheduleDate_MonthlyOnWeekDay_6Occurences() throws Exception {
        // dates that lies on last friday of every two months
        startDate = getDate("15/11/2005");
        occurrences = 6;
        meeting = createMonthlyMeetingOnWeekDay(WeekDay.FRIDAY, RankOfDay.LAST, TWO, startDate);
        Assert.assertTrue(meeting.isValidMeetingDate(getDate("31/03/2006"), occurrences));
        Assert.assertFalse(meeting.isValidMeetingDate(getDate("27/10/2006"), occurrences));
    }

    // ExpectedList("15/11/2005,27/12/2005,07/02/2006");
    public void testIsValidScheduleDate_EverySixWeek_3Occurences() throws Exception {
        startDate = getDate("15/11/2005");
        occurrences = 3;
        meeting = createWeeklyMeeting(WeekDay.TUESDAY, SIX, startDate);

        Assert.assertTrue(meeting.isValidMeetingDate(getDate("27/12/2005"), occurrences));
        Assert.assertFalse(meeting.isValidMeetingDate(getDate("26/12/2006"), occurrences));
    }

    // ExpectedList("28/08/2005,07/09/2005,17/09/2005");
    public void testIsValidScheduleDate_Day_3Occurences() throws Exception {
        startDate = getDate("18/08/2005");
        occurrences = 3;
        meeting = createDailyMeeting(TEN, startDate);
        Assert.assertTrue(meeting.isValidMeetingDate(getDate("07/09/2005"), occurrences));
        Assert.assertFalse(meeting.isValidMeetingDate(getDate("27/09/2006"), occurrences));
    }

    public void testFailureIsValidScheduleDate_WhenMeetingDateNull() throws Exception {
        startDate = getDate("15/11/2003");
        endDate = getDate("15/10/2006");
        dayNumber = Short.valueOf("29");
        meeting = createMonthlyMeetingOnDate(dayNumber, ONE, startDate);
        try {
            meeting.isValidMeetingDate(null, endDate);
            Assert.fail();
        } catch (MeetingException me) {
            Assert.assertTrue(true);
            Assert.assertEquals(MeetingConstants.INVALID_MEETINGDATE, me.getKey());
        }
    }

    public void testFailureIsValidScheduleDate_EndDateNull() throws Exception {
        startDate = getDate("15/11/2003");
        endDate = getDate("15/10/2006");
        dayNumber = Short.valueOf("29");
        meeting = createMonthlyMeetingOnDate(dayNumber, ONE, startDate);
        try {
            meeting.isValidMeetingDate(getDate("29/02/2004"), null);
            Assert.fail();
        } catch (MeetingException me) {
            Assert.assertTrue(true);
            Assert.assertEquals(MeetingConstants.INVALID_ENDDATE, me.getKey());
        }
    }

    // ExpectedList("05/12/2005,05/03/2006,05/06/2006,05/09/2006");
    public void testIsValidScheduleDate_MonthlyOnDate() throws Exception {
        // 5th day of every three months
        startDate = getDate("15/11/2005");
        endDate = getDate("15/10/2006");
        dayNumber = FIVE;
        meeting = createMonthlyMeetingOnDate(dayNumber, THREE, startDate);

        Assert.assertTrue(meeting.isValidMeetingDate(getDate("05/09/2006"), endDate));
        Assert.assertFalse(meeting.isValidMeetingDate(getDate("05/12/2006"), endDate));
    }

    // ExpectedList("29/08/2006,30/08/2006,31/08/2006,01/09/2006,02/09/2006");
    public void testIsValidScheduleDate_31Day() throws Exception {
        startDate = getDate("28/08/2006");
        endDate = getDate("02/09/2006");
        meeting = createDailyMeeting(ONE, startDate);
        Assert.assertTrue(meeting.isValidMeetingDate(getDate("29/08/2006"), endDate));
        Assert.assertFalse(meeting.isValidMeetingDate(getDate("01/19/2006"), endDate));
    }

    // ExpectedList("12/12/2005,09/01/2006,13/02/2006,13/03/2006,10/04/2006,08/05/2006,12/06/2006,10/07/2006,14/08/2006,11/09/2006,09/10/2006");
    public void testIsValidScheduleDate_MonthlyOnWeekDay() throws Exception {
        // dates that lies on second monday of every month
        startDate = getDate("15/11/2005");
        endDate = getDate("15/10/2006");
        meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankOfDay.SECOND, ONE, startDate);
        Assert.assertTrue(meeting.isValidMeetingDate(getDate("13/03/2006"), endDate));
        Assert.assertFalse(meeting.isValidMeetingDate(getDate("15/10/2006"), endDate));
    }

    // ExpectedList("18/11/2005,09/12/2005,30/12/2005,20/01/2006,10/02/2006");
    public void testIsValidScheduleDate_EveryThreeWeek() throws Exception {
        startDate = getDate("15/11/2005");
        endDate = getDate("01/03/2006");
        meeting = createWeeklyMeeting(WeekDay.FRIDAY, THREE, startDate);
        Assert.assertTrue(meeting.isValidMeetingDate(getDate("18/11/2005"), endDate));
        Assert.assertFalse(meeting.isValidMeetingDate(getDate("03/03/2006"), endDate));
    }

    public void testGetPrevScheduleDateAfterRecurrenceOnStartofMonth() throws Exception {
        startDate = getDate("01/01/2006");
        dayNumber = ONE;
        meeting = createMonthlyMeetingOnDate(dayNumber, TWO, startDate);
        Date resultDate = meeting.getPrevScheduleDateAfterRecurrence(getDate("01/05/2006"));
        Assert.assertEquals(getDate("01/03/2006"), resultDate);
    }

    public void testGetPrevScheduleDateAfterRecurrenceOnEndofMonth() throws Exception {
        startDate = getDate("01/01/2006");
        dayNumber = Short.valueOf("31");
        meeting = createMonthlyMeetingOnDate(dayNumber, TWO, startDate);
        Date resultDate = meeting.getPrevScheduleDateAfterRecurrence(getDate("31/05/2006"));
        Assert.assertEquals(getDate("31/03/2006"), resultDate);
    }

    public void testFailureCreateDailyMeeting_recurAfterIsNull() {
        try {
            meeting = createDailyMeeting(null, new Date());
            Assert.assertNull(meeting);
            Assert.fail();
        } catch (MeetingException me) {
            Assert.assertTrue(true);
            Assert.assertEquals(MeetingConstants.INVALID_RECURAFTER, me.getKey());
        }
    }

    public void testFailureCreateDailyMeeting_recurAfterInvalid() {
        try {
            meeting = createDailyMeeting((short) -1, new Date());
            Assert.assertNull(meeting);
            Assert.fail();
        } catch (MeetingException me) {
            Assert.assertTrue(true);
            Assert.assertEquals(MeetingConstants.INVALID_RECURAFTER, me.getKey());
        }
    }

    public void testFailureCreateDailyMeeting_startDateIsNull() {
        try {
            meeting = createDailyMeeting(ONE, null);
            Assert.assertNull(meeting);
            Assert.fail();
        } catch (MeetingException me) {
            Assert.assertTrue(true);
            Assert.assertEquals(MeetingConstants.INVALID_STARTDATE, me.getKey());
        }
    }

    public void testSuccessfulCreateDailyMeeting() throws MeetingException {
        meeting = createDailyMeeting(ONE, new Date());
        IntegrationTestObjectMother.saveMeeting(meeting);

        meeting = (MeetingBO) StaticHibernateUtil.getSessionTL().get(MeetingBO.class, meeting.getMeetingId());
        Assert.assertNotNull(meeting);
        Assert.assertEquals(ONE, meeting.getMeetingDetails().getRecurAfter());
        Assert.assertEquals(RecurrenceType.DAILY, meeting.getMeetingDetails().getRecurrenceTypeEnum());
    }

    public void testFailureCreateWeeklyMeeting_weekDayIsNull() {
        try {
            meeting = createWeeklyMeeting(null, ONE, new Date());
            Assert.assertNull(meeting);
            Assert.fail();
        } catch (MeetingException me) {
            fail("should be illegal state exception - not a checked exception");
        } catch (IllegalStateException e) {
            Assert.assertEquals(MeetingConstants.INVALID_WEEKDAY, e.getMessage());
        }
    }

    public void testFailureCreateWeeklyMeeting_recurAfterIsNull() {
        try {
            meeting = createWeeklyMeeting(WeekDay.MONDAY, null, new Date());
            Assert.assertNull(meeting);
            Assert.fail();
        } catch (MeetingException me) {
            Assert.assertTrue(true);
            Assert.assertEquals(MeetingConstants.INVALID_RECURAFTER, me.getKey());
        }
    }

    public void testFailureCreateWeeklyMeeting_startDateIsNull() {
        try {
            meeting = createWeeklyMeeting(WeekDay.MONDAY, ONE, null);
            Assert.assertNull(meeting);
            Assert.fail();
        } catch (MeetingException me) {
            Assert.assertTrue(true);
            Assert.assertEquals(MeetingConstants.INVALID_STARTDATE, me.getKey());
        }
    }

    public void testFailureCreateWeeklyMeeting_meetingPlaceIsNull() {
        try {
            meeting = new MeetingBO(WeekDay.MONDAY, ONE, new Date(), MeetingType.CUSTOMER_MEETING, "");
            Assert.assertNull(meeting);
            Assert.fail();
        } catch (MeetingException me) {
            Assert.assertTrue(true);
            Assert.assertEquals(MeetingConstants.INVALID_MEETINGPLACE, me.getKey());
        }
    }

    public void testSuccessfulCreateWeeklyMeeting() throws MeetingException {
        meeting = createWeeklyMeeting(WeekDay.MONDAY, TWO, new Date());
        IntegrationTestObjectMother.saveMeeting(meeting);

        meeting = (MeetingBO) StaticHibernateUtil.getSessionTL().get(MeetingBO.class, meeting.getMeetingId());
        Assert.assertNotNull(meeting);
        Assert.assertEquals(TWO, meeting.getMeetingDetails().getRecurAfter());
        Assert.assertTrue(meeting.isWeekly());
        Assert.assertEquals(WeekDay.MONDAY, meeting.getMeetingDetails().getWeekDay());
    }

    public void testFailureCreateMonthlyMeetingOnDate_dayNumberIsNull() {
        try {
            meeting = createMonthlyMeetingOnDate(null, ONE, new Date());
            Assert.assertNull(meeting);
            Assert.fail();
        } catch (MeetingException me) {
            Assert.assertTrue(true);
            Assert.assertEquals(MeetingConstants.INVALID_DAYNUMBER_OR_WEEK, me.getKey());
        }
    }

    public void testFailureCreateMonthlyMeetingOnDate_dayNumberIsNInvalid() {
        try {
            dayNumber = Short.valueOf("32");
            meeting = createMonthlyMeetingOnDate(dayNumber, ONE, new Date());
            Assert.assertNull(meeting);
            Assert.fail();
        } catch (MeetingException me) {
            Assert.assertTrue(true);
            Assert.assertEquals(MeetingConstants.INVALID_DAYNUMBER, me.getKey());
        }
    }

    public void testFailureCreateMonthlyMeetingOnDate_recurAfterIsNull() {
        try {
            dayNumber = FIVE;
            meeting = createMonthlyMeetingOnDate(dayNumber, null, new Date());
            Assert.assertNull(meeting);
            Assert.fail();
        } catch (MeetingException me) {
            Assert.assertTrue(true);
            Assert.assertEquals(MeetingConstants.INVALID_RECURAFTER, me.getKey());
        }
    }

    public void testFailureCreateMonthlyMeetingOnDate_startDateIsNull() {
        try {
            dayNumber = FIVE;
            meeting = createMonthlyMeetingOnDate(dayNumber, ONE, null);
            Assert.assertNull(meeting);
            Assert.fail();
        } catch (MeetingException me) {
            Assert.assertTrue(true);
            Assert.assertEquals(MeetingConstants.INVALID_STARTDATE, me.getKey());
        }
    }

    public void testSuccessfulCreateMonthlyMeetingOnDate() throws MeetingException {
        dayNumber = FIVE;
        meeting = createMonthlyMeetingOnDate(dayNumber, ONE, new Date());
        IntegrationTestObjectMother.saveMeeting(meeting);

        meeting = (MeetingBO) StaticHibernateUtil.getSessionTL().get(MeetingBO.class, meeting.getMeetingId());
        Assert.assertNotNull(meeting);
        Assert.assertEquals(ONE, meeting.getMeetingDetails().getRecurAfter());
        Assert.assertTrue(meeting.isMonthlyOnDate());
        Assert.assertEquals(dayNumber, meeting.getMeetingDetails().getDayNumber());
    }

    public void testFailureCreateMonthlyMeetingOnWeekDay_weekDayIsNull() {
        try {
            meeting = createMonthlyMeetingOnWeekDay(null, RankOfDay.FIRST, ONE, new Date());
            Assert.assertNull(meeting);
            Assert.fail();
        } catch (MeetingException me) {
            Assert.assertTrue(true);
            Assert.assertEquals(MeetingConstants.INVALID_WEEKDAY_OR_WEEKRANK, me.getKey());
        }
    }

    public void testFailureCreateMonthlyMeetingOnWeekDay_weekRankIsNull() {
        try {
            meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, null, ONE, new Date());
            Assert.assertNull(meeting);
            Assert.fail();
        } catch (MeetingException me) {
            Assert.assertTrue(true);
            Assert.assertEquals(MeetingConstants.INVALID_WEEKDAY_OR_WEEKRANK, me.getKey());
        }
    }

    public void testFailureCreateMonthlyMeetingOnWeekDay_recurAfterIsNull() {
        try {
            meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankOfDay.FIRST, null, new Date());
            Assert.assertNull(meeting);
            Assert.fail();
        } catch (MeetingException me) {
            Assert.assertTrue(true);
            Assert.assertEquals(MeetingConstants.INVALID_RECURAFTER, me.getKey());
        }
    }

    public void testFailureCreateMonthlyMeetingOnWeekDay_startDateIsNull() {
        try {
            dayNumber = FIVE;
            meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankOfDay.FIRST, ONE, null);
            Assert.assertNull(meeting);
            Assert.fail();
        } catch (MeetingException me) {
            Assert.assertTrue(true);
            Assert.assertEquals(MeetingConstants.INVALID_STARTDATE, me.getKey());
        }
    }

    public void testSuccessfulCreateMonthlyMeetingOnWeekDay() throws MeetingException {
        meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankOfDay.FIRST, ONE, new Date());
        IntegrationTestObjectMother.saveMeeting(meeting);

        meeting = (MeetingBO) StaticHibernateUtil.getSessionTL().get(MeetingBO.class, meeting.getMeetingId());
        Assert.assertNotNull(meeting);
        Assert.assertEquals(ONE, meeting.getMeetingDetails().getRecurAfter());
        Assert.assertTrue(meeting.isMonthly());
        Assert.assertFalse(meeting.isMonthlyOnDate());
        Assert.assertEquals(WeekDay.MONDAY, meeting.getMeetingDetails().getWeekDay());
        Assert.assertEquals(RankOfDay.FIRST, meeting.getMeetingDetails().getWeekRank());
    }

    public void testFailureUpdateWeeklyMeeting_MeetingPlace_Null() throws MeetingException {
        meeting = createWeeklyMeeting(WeekDay.MONDAY, FIVE, new Date());
        try {
            meeting.update(WeekDay.WEDNESDAY, "");
            Assert.fail();
        } catch (MeetingException me) {
            Assert.assertTrue(true);
            Assert.assertEquals(MeetingConstants.INVALID_MEETINGPLACE, me.getKey());
        }
    }

    public void testSuccessfulUpdateWeeklyMeeting() throws MeetingException {
        meeting = createWeeklyMeeting(WeekDay.MONDAY, FIVE, new Date());
        meeting.update(WeekDay.WEDNESDAY, "Delhi");
        Assert.assertEquals("Delhi", meeting.getMeetingPlace());
        Assert.assertEquals(FIVE, meeting.getMeetingDetails().getRecurAfter());
        Assert.assertEquals(WeekDay.WEDNESDAY, meeting.getMeetingDetails().getWeekDay());
    }

    public void testFailureUpdateMontlyMeetingInValidDayNumber() throws MeetingException {
        meeting = createMonthlyMeetingOnDate(Short.valueOf("5"), TWO, new Date());
        try {
            meeting.update(Short.valueOf("32"), "Delhi");
            Assert.fail();
        } catch (MeetingException me) {
            Assert.assertTrue(true);
            Assert.assertEquals(MeetingConstants.INVALID_DAYNUMBER, me.getKey());
        }
    }

    public void testFailureUpdateMontlyMeeting_MeetingPlace_Null() throws MeetingException {
        meeting = createMonthlyMeetingOnDate(Short.valueOf("5"), TWO, new Date());
        try {
            meeting.update(SIX, "");
            Assert.fail();
        } catch (MeetingException me) {
            Assert.assertTrue(true);
            Assert.assertEquals(MeetingConstants.INVALID_MEETINGPLACE, me.getKey());
        }
    }

    public void testSuccessfulUpdateMontlyMeetingOnDate() throws MeetingException {
        meeting = createMonthlyMeetingOnDate(Short.valueOf("5"), TWO, new Date());
        meeting.update(TEN, "Delhi");
        Assert.assertEquals("Delhi", meeting.getMeetingPlace());
        Assert.assertEquals(TWO, meeting.getMeetingDetails().getRecurAfter());
        Assert.assertEquals(TEN, meeting.getMeetingDetails().getDayNumber());
    }

    public void testFailureUpdateMontlyMeetingOnWeekDay() throws MeetingException {
        meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankOfDay.FIRST, TWO, new Date());
        try {
            meeting.update(null, null, "Delhi");
            Assert.fail();
        } catch (MeetingException me) {
            Assert.assertTrue(true);
            Assert.assertEquals(MeetingConstants.INVALID_WEEKDAY_OR_WEEKRANK, me.getKey());
        }
    }

    public void testSuccessfulUpdateMontlyMeetingOnWeekDay() throws MeetingException {
        meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankOfDay.FIRST, TWO, new Date());
        meeting.update(WeekDay.THURSDAY, RankOfDay.THIRD, "Delhi");
        Assert.assertEquals("Delhi", meeting.getMeetingPlace());
        Assert.assertEquals(TWO, meeting.getMeetingDetails().getRecurAfter());
        Assert.assertEquals(WeekDay.THURSDAY, meeting.getMeetingDetails().getWeekDay());
        Assert.assertEquals(RankOfDay.THIRD, meeting.getMeetingDetails().getWeekRank());
    }

    private MeetingBO createDailyMeeting(final Short recurAfer, final Date startDate) throws MeetingException {
        return new MeetingBO(RecurrenceType.DAILY, recurAfer, startDate, MeetingType.CUSTOMER_MEETING);
    }

    private MeetingBO createWeeklyMeeting(final WeekDay weekDay, final Short recurAfer, final Date startDate)
            throws MeetingException {
        return new MeetingBO(weekDay, recurAfer, startDate, MeetingType.CUSTOMER_MEETING, "MeetingPlace");
    }

    private MeetingBO createMonthlyMeetingOnDate(final Short dayNumber, final Short recurAfer, final Date startDate)
            throws MeetingException {
        return new MeetingBO(dayNumber, recurAfer, startDate, MeetingType.CUSTOMER_MEETING, "MeetingPlace");
    }

    private MeetingBO createMonthlyMeetingOnWeekDay(final WeekDay weekDay, final RankOfDay rank, final Short recurAfer,
            final Date startDate) throws MeetingException {
        return new MeetingBO(weekDay, rank, recurAfer, startDate, MeetingType.CUSTOMER_MEETING, "MeetingPlace");
    }

    void matchDateLists(final List<Date> expectedList, final List<Date> list) {
        Assert.assertEquals(expectedList.size(), list.size());
        for (int i = 0; i < expectedList.size(); i++) {
            Assert.assertEquals("Dates are invalid", expectedList.get(i), list.get(i));
        }
    }
}
