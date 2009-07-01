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

package org.mifos.application.meeting.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;

import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingConstants;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RankType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;

public class MeetingBOIntegrationTest extends MifosIntegrationTest {
    public MeetingBOIntegrationTest() throws SystemException, ApplicationException {
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

    public void testShouldThrowMeetingExceptionWhenEndDateForGeneratingMeetingDatesIsNull() throws Exception {
        startDate = getDate("18/08/2005");
        endDate = getDate("02/10/2005");
        meeting = createDailyMeeting(TEN, startDate);
        try {
            meeting.getAllDates(null);
            fail("Should have thrown exception while validating endDate");
        } catch (MeetingException e) {
            assertEquals(MeetingConstants.INVALID_ENDDATE, e.getKey());
        }
    }

    /**
     * Performs the following test: 1. Creates test data - from Sunday to
     * Saturday, and its expected dates after invoking
     * meeting.getFirstDateForWeek() method 2. Invokes
     * verifyFirstDateForWeekForGivenStartDate for each test data set and
     * asserts that meeting.getFirstDateForWeek() method works as expected
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
     * Note, it invokes verifyFirstDateForWeekForGivenStartDateAndWeekDay for
     * each day of the week
     */
    private void verifyFirstDateForWeekForGivenStartDate(Date startDate, Date[] expectedDates) throws MeetingException {
        verifyFirstDateForWeekForGivenStartDateAndWeekDay(WeekDay.SUNDAY, startDate, expectedDates[0]);
        verifyFirstDateForWeekForGivenStartDateAndWeekDay(WeekDay.MONDAY, startDate, expectedDates[1]);
        verifyFirstDateForWeekForGivenStartDateAndWeekDay(WeekDay.TUESDAY, startDate, expectedDates[2]);
        verifyFirstDateForWeekForGivenStartDateAndWeekDay(WeekDay.WEDNESDAY, startDate, expectedDates[3]);
        verifyFirstDateForWeekForGivenStartDateAndWeekDay(WeekDay.THURSDAY, startDate, expectedDates[4]);
        verifyFirstDateForWeekForGivenStartDateAndWeekDay(WeekDay.FRIDAY, startDate, expectedDates[5]);
        verifyFirstDateForWeekForGivenStartDateAndWeekDay(WeekDay.SATURDAY, startDate, expectedDates[6]);
    }

    /**
     * Asserts that meeting.getFirstDateForWeek(startDate) returns a date
     * matching the expected date.
     * 
     * A dummy meeting is created with the given weekDay, and then the resulting
     * date from meeting.getFirstDateForWeek(startDate) is asserted to be the
     * expected date
     * 
     */
    private void verifyFirstDateForWeekForGivenStartDateAndWeekDay(WeekDay weekDay, Date startDate, Date expectedDate)
            throws MeetingException {
        // Assert getFirstDateForWeek works as expected for meeting with given
        // weekDay
        MeetingBO meeting = createWeeklyMeeting(weekDay, ONE, new Date());
        assertTrue(meeting.getFirstDateForWeek(startDate).compareTo(expectedDate) == 0);
    }

    public void testGetAllDatesWeeklyWithRepaymentIndepOfMeetingEnabled() throws Exception {
        startDate = getDate("15/11/2005");
        endDate = getDate("01/03/2006");
        meeting = createWeeklyMeeting(WeekDay.THURSDAY, ONE, startDate);
        List list = meeting.getAllDatesWithRepaymentIndepOfMeetingEnabled(1);
        List expectedList = createExpectedList("17/11/2005");
        matchDateLists(expectedList, list);
    }

    public void testGetAllDatesMonthlyWithRepaymentIndepOfMeetingEnabled() throws Exception {
        // dates that lies on second monday of every month
        startDate = getDate("15/11/2005");
        endDate = getDate("15/10/2006");
        meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankType.SECOND, ONE, startDate);
        List list = meeting.getAllDatesWithRepaymentIndepOfMeetingEnabled(2);
        List expectedList = createExpectedList("12/12/2005,09/01/2006");
        matchDateLists(expectedList, list);
    }

    public void testShouldThrowMeetingExceptionIfEndDateBeforeStartDateForGeneratingAllMeetingDates() throws Exception {
        startDate = getDate("18/08/2006");
        endDate = getDate("17/08/2006");
        meeting = createDailyMeeting(TEN, startDate);
        try {
            meeting.getAllDates(endDate);
            fail("Should throw exception if endDate is before startDate");
        } catch (MeetingException e) {
            assertEquals(MeetingConstants.INVALID_ENDDATE, e.getKey());
        }
    }

    public void testShouldReturnEmptyListIfStartDateAndEndDateSameForGeneratingAllMeetingDates() throws Exception {
        startDate = getDate("18/08/2006");
        endDate = getDate("18/08/2006");
        meeting = createDailyMeeting(ONE, startDate);
        assertTrue(meeting.getAllDates(endDate).isEmpty());
    }

    public void testSuccessfulGetAllDates_Day() throws Exception {
        startDate = getDate("18/08/2005");
        endDate = getDate("02/10/2005");
        meeting = createDailyMeeting(TEN, startDate);
        List list = meeting.getAllDates(endDate);
        List expectedList = createExpectedList("28/08/2005,07/09/2005,17/09/2005,27/09/2005");
        matchDateLists(expectedList, list);
    }

    public void testSuccessfulGetAllDates_30Day() throws Exception {
        List list = null;
        List expectedList = null;
        startDate = getDate("28/09/2006");
        endDate = getDate("02/10/2006");
        meeting = createDailyMeeting(ONE, startDate);
        list = meeting.getAllDates(endDate);
        assertNotNull(list);
        expectedList = createExpectedList("29/09/2006,30/09/2006,01/10/2006,02/10/2006");
        assertEquals(expectedList.size(), list.size());
        matchDateLists(expectedList, list);
    }

    public void testSuccessfulGetAllDates_31Day() throws Exception {
        List list = null;
        List expectedList = null;
        startDate = getDate("28/08/2006");
        endDate = getDate("02/09/2006");
        meeting = createDailyMeeting(ONE, startDate);
        list = meeting.getAllDates(endDate);
        assertNotNull(list);
        expectedList = createExpectedList("29/08/2006,30/08/2006,31/08/2006,01/09/2006,02/09/2006");
        assertEquals(expectedList.size(), list.size());
        matchDateLists(expectedList, list);
    }

    public void testSuccessfulGetAllDates_28Day() throws Exception {
        List list = null;
        List expectedList = null;
        startDate = getDate("25/02/2006");
        endDate = getDate("02/03/2006");
        meeting = createDailyMeeting(ONE, startDate);
        list = meeting.getAllDates(endDate);
        assertNotNull(list);
        expectedList = createExpectedList("26/02/2006,27/02/2006,28/02/2006,01/03/2006,02/03/2006");
        assertEquals(expectedList.size(), list.size());
        matchDateLists(expectedList, list);
    }

    public void testSuccessfulGetAllDates_29Day() throws Exception {
        List list = null;
        List expectedList = null;
        startDate = getDate("26/02/2004");
        endDate = getDate("02/03/2004");
        meeting = createDailyMeeting(ONE, startDate);
        list = meeting.getAllDates(endDate);
        assertNotNull(list);
        expectedList = createExpectedList("27/02/2004,28/02/2004,29/02/2004,01/03/2004,02/03/2004");
        assertEquals(expectedList.size(), list.size());
        matchDateLists(expectedList, list);
    }

    public void testGetAllDates_EveryWeek() throws Exception {
        startDate = getDate("15/11/2005");
        endDate = getDate("01/03/2006");
        meeting = createWeeklyMeeting(WeekDay.THURSDAY, ONE, startDate);
        List list = meeting.getAllDates(endDate);
        List expectedList = createExpectedList("17/11/2005,24/11/2005,1/12/2005,08/12/2005,15/12/2005,"
                + "22/12/2005,29/12/2005,05/01/2006,12/01/2006,19/01/2006,26/01/2006,02/02/2006,09/02/2006,"
                + "16/02/2006,23/02/2006");
        assertNotNull(list);
        assertEquals(expectedList.size(), list.size());
        matchDateLists(expectedList, list);
    }

    public void testGelAllDates_EveryThreeWeek() throws Exception {
        List list = null;
        List expectedList = null;
        startDate = getDate("15/11/2005");
        endDate = getDate("01/03/2006");
        meeting = createWeeklyMeeting(WeekDay.FRIDAY, THREE, startDate);
        list = meeting.getAllDates(endDate);
        expectedList = createExpectedList("18/11/2005,09/12/2005,30/12/2005,20/01/2006,10/02/2006");
        assertNotNull(list);
        assertEquals(expectedList.size(), list.size());
        matchDateLists(expectedList, list);
    }

    public void testGelAllDates_EverySixWeek() throws Exception {
        List list = null;
        List expectedList = null;
        startDate = getDate("15/11/2005");
        endDate = getDate("01/03/2006");
        meeting = createWeeklyMeeting(WeekDay.TUESDAY, SIX, startDate);
        list = meeting.getAllDates(endDate);
        expectedList = createExpectedList("15/11/2005,27/12/2005,07/02/2006");
        assertNotNull(list);
        assertEquals(expectedList.size(), list.size());
        matchDateLists(expectedList, list);
    }

    public void testGelAllDates_MonthlyOnWeekDay() throws Exception {
        // dates that lies on second monday of every month
        List list = null;
        List expectedList = null;
        startDate = getDate("15/11/2005");
        endDate = getDate("15/10/2006");
        meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankType.SECOND, ONE, startDate);
        list = meeting.getAllDates(endDate);
        expectedList = createExpectedList("12/12/2005,09/01/2006,13/02/2006,13/03/2006,10/04/2006,08/05/2006,12/06/2006,10/07/2006,14/08/2006,11/09/2006,09/10/2006");
        assertNotNull(list);
        assertEquals(expectedList.size(), list.size());
        matchDateLists(expectedList, list);
    }

    public void testGelAllDates_2MonthlyOnWeekDay() throws Exception {
        // dates that lies on last friday of every two months
        List list = null;
        List expectedList = null;
        startDate = getDate("15/11/2005");
        endDate = getDate("15/10/2006");
        meeting = createMonthlyMeetingOnWeekDay(WeekDay.FRIDAY, RankType.LAST, TWO, startDate);
        list = meeting.getAllDates(endDate);
        expectedList = createExpectedList("25/11/2005,27/01/2006,31/03/2006,26/05/2006,28/07/2006,29/09/2006");
        assertNotNull(list);
        assertEquals(expectedList.size(), list.size());
        matchDateLists(expectedList, list);
    }

    public void testGelAllDates_MonthlyOnDate() throws Exception {
        // 5th day of every three months
        List list = null;
        List expectedList = null;
        startDate = getDate("15/11/2005");
        endDate = getDate("15/10/2006");
        dayNumber = FIVE;
        meeting = createMonthlyMeetingOnDate(dayNumber, THREE, startDate);
        list = meeting.getAllDates(endDate);
        expectedList = createExpectedList("05/12/2005,05/03/2006,05/06/2006,05/09/2006");
        assertNotNull(list);
        assertEquals(expectedList.size(), list.size());
        matchDateLists(expectedList, list);
    }

    public void testGelAllDates_30MonthlyOnDate() throws Exception {
        // 30th day of every month
        List list = null;
        List expectedList = null;
        startDate = getDate("15/11/2005");
        endDate = getDate("15/04/2006");
        dayNumber = Short.valueOf("30");
        meeting = createMonthlyMeetingOnDate(dayNumber, ONE, startDate);
        list = meeting.getAllDates(endDate);
        expectedList = createExpectedList("30/11/2005,30/12/2005,30/01/2006,28/02/2006,30/03/2006");
        assertNotNull(list);
        assertEquals(expectedList.size(), list.size());
        matchDateLists(expectedList, list);
    }

    public void testGelAllDates_31MonthlyOnDate() throws Exception {
        // 30th day of every month
        List list = null;
        List expectedList = null;
        startDate = getDate("15/11/2005");
        endDate = getDate("15/05/2006");
        dayNumber = Short.valueOf("31");
        meeting = createMonthlyMeetingOnDate(dayNumber, ONE, startDate);
        list = meeting.getAllDates(endDate);
        expectedList = createExpectedList("30/11/2005,31/12/2005,31/01/2006,28/02/2006,31/03/2006,30/04/2006");
        assertNotNull(list);
        assertEquals(expectedList.size(), list.size());
        matchDateLists(expectedList, list);
    }

    public void testGelAllDates_28MonthlyOnDate() throws Exception {
        // 28th day of every month
        List list = null;
        List expectedList = null;
        startDate = getDate("15/11/2003");
        endDate = getDate("15/04/2004");
        dayNumber = Short.valueOf("28");
        meeting = createMonthlyMeetingOnDate(dayNumber, ONE, startDate);
        list = meeting.getAllDates(endDate);
        expectedList = createExpectedList("28/11/2003,28/12/2003,28/01/2004,28/02/2004,28/03/2004");
        assertNotNull(list);
        assertEquals(expectedList.size(), list.size());
        matchDateLists(expectedList, list);
    }

    public void testGelAllDates_29MonthlyOnDate() throws Exception {
        // 29th day of every month
        List list = null;
        List expectedList = null;
        startDate = getDate("15/11/2003");
        endDate = getDate("29/03/2004");
        dayNumber = Short.valueOf("29");
        meeting = createMonthlyMeetingOnDate(dayNumber, ONE, startDate);
        list = meeting.getAllDates(endDate);
        expectedList = createExpectedList("29/11/2003,29/12/2003,29/01/2004,29/02/2004,29/03/2004");
        assertNotNull(list);
        assertEquals(expectedList.size(), list.size());
        matchDateLists(expectedList, list);
    }

    public void testFailureGetAllDates_ZeroOccurences() throws Exception {
        try {
            startDate = getDate("18/08/2006");
            occurrences = 0;
            meeting = createDailyMeeting(TEN, startDate);
            meeting.getAllDates(occurrences);
            fail();
        } catch (MeetingException e) {
            assertTrue(true);
        }
    }

    public void testSuccessfulGetAllDates_Day_3Occurences() throws Exception {
        List list = null;
        List expectedList = null;
        startDate = getDate("18/08/2005");
        occurrences = 3;
        meeting = createDailyMeeting(TEN, startDate);
        list = meeting.getAllDates(occurrences);
        assertNotNull(list);
        expectedList = createExpectedList("28/08/2005,07/09/2005,17/09/2005");
        assertEquals(expectedList.size(), list.size());
        matchDateLists(expectedList, list);
    }

    public void testSuccessfulGetAllDates_30Day_4Occurences() throws Exception {
        List list = null;
        List expectedList = null;
        startDate = getDate("28/09/2006");
        occurrences = 4;
        meeting = createDailyMeeting(ONE, startDate);
        list = meeting.getAllDates(occurrences);
        assertNotNull(list);
        expectedList = createExpectedList("29/09/2006,30/09/2006,01/10/2006,02/10/2006");
        assertEquals(expectedList.size(), list.size());
        matchDateLists(expectedList, list);
    }

    public void testSuccessfulGetAllDates_5Occurences() throws Exception {
        List list = null;
        List expectedList = null;
        startDate = getDate("28/08/2006");
        occurrences = 5;
        meeting = createDailyMeeting(ONE, startDate);
        list = meeting.getAllDates(occurrences);
        assertNotNull(list);
        expectedList = createExpectedList("29/08/2006,30/08/2006,31/08/2006,01/09/2006,02/09/2006");
        assertEquals(expectedList.size(), list.size());
        matchDateLists(expectedList, list);
    }

    public void testSuccessfulGetAllDates_28Day_5Occurences() throws Exception {
        startDate = getDate("25/02/2006");
        occurrences = 5;
        meeting = createDailyMeeting(ONE, startDate);
        List list = meeting.getAllDates(occurrences);
        assertNotNull(list);
        List expectedList = createExpectedList("26/02/2006,27/02/2006,28/02/2006,01/03/2006,02/03/2006");
        matchDateLists(expectedList, list);
    }

    public void testSuccessfulGetAllDates_29Day_6Occurences() throws Exception {
        List list = null;
        List expectedList = null;
        startDate = getDate("26/02/2004");
        occurrences = 6;
        meeting = createDailyMeeting(ONE, startDate);
        list = meeting.getAllDates(occurrences);
        assertNotNull(list);
        expectedList = createExpectedList("27/02/2004,28/02/2004,29/02/2004,01/03/2004,02/03/2004,03/03/2004");
        assertEquals(expectedList.size(), list.size());
        matchDateLists(expectedList, list);
    }

    public void testGelAllDates_EveryWeek_15Occurences() throws Exception {
        List list = null;
        List expectedList = null;
        startDate = getDate("15/11/2005");
        occurrences = 15;
        meeting = createWeeklyMeeting(WeekDay.THURSDAY, ONE, startDate);
        list = meeting.getAllDates(occurrences);
        expectedList = createExpectedList("17/11/2005,24/11/2005,1/12/2005,08/12/2005,15/12/2005,22/12/2005,29/12/2005,05/01/2006,12/01/2006,19/01/2006,26/01/2006,02/02/2006,09/02/2006,16/02/2006,23/02/2006");
        assertNotNull(list);
        assertEquals(expectedList.size(), list.size());
        matchDateLists(expectedList, list);
    }

    public void testGelAllDates_EveryThreeWeek_5Occurences() throws Exception {
        List list = null;
        List expectedList = null;
        startDate = getDate("15/11/2005");
        occurrences = 5;
        meeting = createWeeklyMeeting(WeekDay.FRIDAY, THREE, startDate);
        list = meeting.getAllDates(occurrences);
        expectedList = createExpectedList("18/11/2005,09/12/2005,30/12/2005,20/01/2006,10/02/2006");
        assertNotNull(list);
        assertEquals(expectedList.size(), list.size());
        matchDateLists(expectedList, list);
    }

    public void testGelAllDates_EverySixWeek_3Occurences() throws Exception {
        List list = null;
        List expectedList = null;
        startDate = getDate("15/11/2005");
        occurrences = 3;
        meeting = createWeeklyMeeting(WeekDay.TUESDAY, SIX, startDate);
        list = meeting.getAllDates(occurrences);
        expectedList = createExpectedList("15/11/2005,27/12/2005,07/02/2006");
        assertNotNull(list);
        assertEquals(expectedList.size(), list.size());
        matchDateLists(expectedList, list);
    }

    public void testGelAllDates_MonthlyOnWeekDay_11Occurences() throws Exception {
        // dates that lies on second monday of every month
        List list = null;
        List expectedList = null;
        startDate = getDate("15/11/2005");
        occurrences = 11;
        meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankType.SECOND, ONE, startDate);
        list = meeting.getAllDates(occurrences);
        expectedList = createExpectedList("12/12/2005,09/01/2006,13/02/2006,13/03/2006,10/04/2006,08/05/2006,12/06/2006,10/07/2006,14/08/2006,11/09/2006,09/10/2006");
        assertNotNull(list);
        assertEquals(expectedList.size(), list.size());
        matchDateLists(expectedList, list);
    }

    public void testGelAllDates_2MonthlyOnWeekDay_6Occurences() throws Exception {
        // dates that lies on last friday of every two months
        List list = null;
        List expectedList = null;
        startDate = getDate("15/11/2005");
        occurrences = 6;
        meeting = createMonthlyMeetingOnWeekDay(WeekDay.FRIDAY, RankType.LAST, TWO, startDate);
        list = meeting.getAllDates(occurrences);
        expectedList = createExpectedList("25/11/2005,27/01/2006,31/03/2006,26/05/2006,28/07/2006,29/09/2006");
        assertNotNull(list);
        assertEquals(expectedList.size(), list.size());
        matchDateLists(expectedList, list);
    }

    public void testGelAllDates_MonthlyOnDate_4Occurences() throws Exception {
        // 5th day of every three months
        List list = null;
        List expectedList = null;
        startDate = getDate("15/11/2005");
        occurrences = 4;
        dayNumber = FIVE;
        meeting = createMonthlyMeetingOnDate(dayNumber, THREE, startDate);
        list = meeting.getAllDates(occurrences);
        expectedList = createExpectedList("05/12/2005,05/03/2006,05/06/2006,05/09/2006");
        assertNotNull(list);
        assertEquals(expectedList.size(), list.size());
        matchDateLists(expectedList, list);
    }

    public void testGelAllDates_30MonthlyOnDate_5Occurences() throws Exception {
        // 30th day of every month
        List list = null;
        List expectedList = null;
        startDate = getDate("15/11/2005");
        occurrences = 5;
        dayNumber = Short.valueOf("30");
        meeting = createMonthlyMeetingOnDate(dayNumber, ONE, startDate);
        list = meeting.getAllDates(occurrences);
        expectedList = createExpectedList("30/11/2005,30/12/2005,30/01/2006,28/02/2006,30/03/2006");
        assertNotNull(list);
        assertEquals(expectedList.size(), list.size());
        matchDateLists(expectedList, list);
    }

    public void testGelAllDates_31MonthlyOnDate_6Occurences() throws Exception {
        // 30th day of every month
        List list = null;
        List expectedList = null;
        startDate = getDate("15/11/2005");
        occurrences = 6;
        dayNumber = Short.valueOf("31");
        meeting = createMonthlyMeetingOnDate(dayNumber, ONE, startDate);
        list = meeting.getAllDates(occurrences);
        expectedList = createExpectedList("30/11/2005,31/12/2005,31/01/2006,28/02/2006,31/03/2006,30/04/2006");
        assertNotNull(list);
        assertEquals(expectedList.size(), list.size());
        matchDateLists(expectedList, list);
    }

    public void testGelAllDates_28MonthlyOnDate_5Occurences() throws Exception {
        // 28th day of every month
        List list = null;
        List expectedList = null;
        startDate = getDate("15/11/2003");
        occurrences = 5;
        dayNumber = Short.valueOf("28");
        meeting = createMonthlyMeetingOnDate(dayNumber, ONE, startDate);
        list = meeting.getAllDates(occurrences);
        expectedList = createExpectedList("28/11/2003,28/12/2003,28/01/2004,28/02/2004,28/03/2004");
        assertNotNull(list);
        assertEquals(expectedList.size(), list.size());
        matchDateLists(expectedList, list);
    }

    public void testGelAllDates_29MonthlyOnDate_5Occurences() throws Exception {
        // 29th day of every month
        List list = null;
        List expectedList = null;
        startDate = getDate("15/11/2003");
        occurrences = 5;
        dayNumber = Short.valueOf("29");
        meeting = createMonthlyMeetingOnDate(dayNumber, ONE, startDate);
        list = meeting.getAllDates(occurrences);
        expectedList = createExpectedList("29/11/2003,29/12/2003,29/01/2004,29/02/2004,29/03/2004");
        assertNotNull(list);
        assertEquals(expectedList.size(), list.size());
        matchDateLists(expectedList, list);
    }

    public void testFailureIsValidScheduleDate_MeetingDateNull() throws Exception {
        startDate = getDate("15/11/2003");
        occurrences = 5;
        dayNumber = Short.valueOf("29");
        meeting = createMonthlyMeetingOnDate(dayNumber, ONE, startDate);
        try {
            meeting.isValidMeetingDate(null, occurrences);
        } catch (MeetingException me) {
            assertTrue(true);
            assertEquals(MeetingConstants.INVALID_MEETINGDATE, me.getKey());
        }
    }

    public void testFailureIsValidScheduleDate_OccurencesNull() throws Exception {
        startDate = getDate("15/11/2003");
        occurrences = 5;
        dayNumber = Short.valueOf("29");
        meeting = createMonthlyMeetingOnDate(dayNumber, ONE, startDate);
        try {
            meeting.isValidMeetingDate(getDate("29/02/2004"), 0);
            fail();
        } catch (MeetingException me) {
            assertTrue(true);
            assertEquals(MeetingConstants.INVALID_OCCURENCES, me.getKey());
        }
    }

    // ExpectedList("29/11/2003,29/12/2003,29/01/2004,29/02/2004,29/03/2004");
    public void testIsValidScheduleDate_MonthlyOnDate_5Occurences() throws Exception {
        startDate = getDate("15/11/2003");
        occurrences = 5;
        dayNumber = Short.valueOf("29");
        meeting = createMonthlyMeetingOnDate(dayNumber, ONE, startDate);
        assertTrue(meeting.isValidMeetingDate(getDate("29/02/2004"), occurrences));
        assertFalse(meeting.isValidMeetingDate(getDate("28/02/2004"), occurrences));
    }

    // ExpectedList("25/11/2005,27/01/2006,31/03/2006,26/05/2006,28/07/2006,29/09/2006");
    public void testIsValidScheduleDate_MonthlyOnWeekDay_6Occurences() throws Exception {
        // dates that lies on last friday of every two months
        startDate = getDate("15/11/2005");
        occurrences = 6;
        meeting = createMonthlyMeetingOnWeekDay(WeekDay.FRIDAY, RankType.LAST, TWO, startDate);
        assertTrue(meeting.isValidMeetingDate(getDate("31/03/2006"), occurrences));
        assertFalse(meeting.isValidMeetingDate(getDate("27/10/2006"), occurrences));
    }

    // ExpectedList("15/11/2005,27/12/2005,07/02/2006");
    public void testIsValidScheduleDate_EverySixWeek_3Occurences() throws Exception {
        startDate = getDate("15/11/2005");
        occurrences = 3;
        meeting = createWeeklyMeeting(WeekDay.TUESDAY, SIX, startDate);

        assertTrue(meeting.isValidMeetingDate(getDate("27/12/2005"), occurrences));
        assertFalse(meeting.isValidMeetingDate(getDate("26/12/2006"), occurrences));
    }

    // ExpectedList("28/08/2005,07/09/2005,17/09/2005");
    public void testIsValidScheduleDate_Day_3Occurences() throws Exception {
        startDate = getDate("18/08/2005");
        occurrences = 3;
        meeting = createDailyMeeting(TEN, startDate);
        assertTrue(meeting.isValidMeetingDate(getDate("07/09/2005"), occurrences));
        assertFalse(meeting.isValidMeetingDate(getDate("27/09/2006"), occurrences));
    }

    public void testFailureIsValidScheduleDate_WhenMeetingDateNull() throws Exception {
        startDate = getDate("15/11/2003");
        endDate = getDate("15/10/2006");
        dayNumber = Short.valueOf("29");
        meeting = createMonthlyMeetingOnDate(dayNumber, ONE, startDate);
        try {
            meeting.isValidMeetingDate(null, endDate);
            fail();
        } catch (MeetingException me) {
            assertTrue(true);
            assertEquals(MeetingConstants.INVALID_MEETINGDATE, me.getKey());
        }
    }

    public void testFailureIsValidScheduleDate_EndDateNull() throws Exception {
        startDate = getDate("15/11/2003");
        endDate = getDate("15/10/2006");
        dayNumber = Short.valueOf("29");
        meeting = createMonthlyMeetingOnDate(dayNumber, ONE, startDate);
        try {
            meeting.isValidMeetingDate(getDate("29/02/2004"), null);
            fail();
        } catch (MeetingException me) {
            assertTrue(true);
            assertEquals(MeetingConstants.INVALID_ENDDATE, me.getKey());
        }
    }

    // ExpectedList("05/12/2005,05/03/2006,05/06/2006,05/09/2006");
    public void testIsValidScheduleDate_MonthlyOnDate() throws Exception {
        // 5th day of every three months
        startDate = getDate("15/11/2005");
        endDate = getDate("15/10/2006");
        dayNumber = FIVE;
        meeting = createMonthlyMeetingOnDate(dayNumber, THREE, startDate);

        assertTrue(meeting.isValidMeetingDate(getDate("05/09/2006"), endDate));
        assertFalse(meeting.isValidMeetingDate(getDate("05/12/2006"), endDate));
    }

    // ExpectedList("29/08/2006,30/08/2006,31/08/2006,01/09/2006,02/09/2006");
    public void testIsValidScheduleDate_31Day() throws Exception {
        startDate = getDate("28/08/2006");
        endDate = getDate("02/09/2006");
        meeting = createDailyMeeting(ONE, startDate);
        assertTrue(meeting.isValidMeetingDate(getDate("29/08/2006"), endDate));
        assertFalse(meeting.isValidMeetingDate(getDate("01/19/2006"), endDate));
    }

    // ExpectedList("12/12/2005,09/01/2006,13/02/2006,13/03/2006,10/04/2006,08/05/2006,12/06/2006,10/07/2006,14/08/2006,11/09/2006,09/10/2006");
    public void testIsValidScheduleDate_MonthlyOnWeekDay() throws Exception {
        // dates that lies on second monday of every month
        startDate = getDate("15/11/2005");
        endDate = getDate("15/10/2006");
        meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankType.SECOND, ONE, startDate);
        assertTrue(meeting.isValidMeetingDate(getDate("13/03/2006"), endDate));
        assertFalse(meeting.isValidMeetingDate(getDate("15/10/2006"), endDate));
    }

    // ExpectedList("18/11/2005,09/12/2005,30/12/2005,20/01/2006,10/02/2006");
    public void testIsValidScheduleDate_EveryThreeWeek() throws Exception {
        startDate = getDate("15/11/2005");
        endDate = getDate("01/03/2006");
        meeting = createWeeklyMeeting(WeekDay.FRIDAY, THREE, startDate);
        assertTrue(meeting.isValidMeetingDate(getDate("18/11/2005"), endDate));
        assertFalse(meeting.isValidMeetingDate(getDate("03/03/2006"), endDate));
    }

    public void testGetFailureNextScheduleDate() throws Exception {
        startDate = getDate("01/01/2006");
        dayNumber = ONE;
        meeting = createMonthlyMeetingOnDate(dayNumber, TWO, startDate);
        try {
            meeting.getNextScheduleDateAfterRecurrence(null);
            fail();
        } catch (MeetingException me) {
            assertTrue(true);
            assertEquals(MeetingConstants.INVALID_MEETINGDATE, me.getKey());
        }
    }

    public void testGetNextScheduleDateAfterRecurrenceOnStartofMonth() throws Exception {
        startDate = getDate("01/01/2006");
        dayNumber = ONE;
        meeting = createMonthlyMeetingOnDate(dayNumber, TWO, startDate);
        Date resultDate = meeting.getNextScheduleDateAfterRecurrence(getDate("01/01/2006"));
        assertEquals(getDate("01/03/2006"), resultDate);
    }

    public void testGetNextScheduleDateAfterRecurrenceOnEndofMonth() throws Exception {
        startDate = getDate("01/01/2006");
        dayNumber = Short.valueOf("31");
        meeting = createMonthlyMeetingOnDate(dayNumber, TWO, startDate);
        Date resultDate = meeting.getNextScheduleDateAfterRecurrence(getDate("01/01/2006"));
        assertEquals(getDate("31/03/2006"), resultDate);
    }

    /**
     * What case is this intended to test? Right now it is just the same as
     * {@link #testGetNextScheduleDateAfterRecurrenceOnEndofMonth}
     */
    public void xtestGetFailurePrevScheduleDate() throws Exception {
        startDate = getDate("01/01/2006");
        dayNumber = Short.valueOf("31");
        meeting = createMonthlyMeetingOnDate(dayNumber, TWO, startDate);
        try {
            meeting.getNextScheduleDateAfterRecurrence(getDate("01/01/2006"));
            fail();
        } catch (MeetingException me) {
            assertTrue(true);
            assertEquals(MeetingConstants.INVALID_MEETINGDATE, me.getKey());
        }
    }

    public void testGetPrevScheduleDateAfterRecurrenceOnStartofMonth() throws Exception {
        startDate = getDate("01/01/2006");
        dayNumber = ONE;
        meeting = createMonthlyMeetingOnDate(dayNumber, TWO, startDate);
        Date resultDate = meeting.getPrevScheduleDateAfterRecurrence(getDate("01/05/2006"));
        assertEquals(getDate("01/03/2006"), resultDate);
    }

    public void testGetPrevScheduleDateAfterRecurrenceOnEndofMonth() throws Exception {
        startDate = getDate("01/01/2006");
        dayNumber = Short.valueOf("31");
        meeting = createMonthlyMeetingOnDate(dayNumber, TWO, startDate);
        Date resultDate = meeting.getPrevScheduleDateAfterRecurrence(getDate("31/05/2006"));
        assertEquals(getDate("31/03/2006"), resultDate);
    }

    public void testFailureCreateDailyMeeting_recurAfterIsNull() {
        try {
            meeting = createDailyMeeting(null, new Date());
            assertNull(meeting);
            fail();
        } catch (MeetingException me) {
            assertTrue(true);
            assertEquals(MeetingConstants.INVALID_RECURAFTER, me.getKey());
        }
    }

    public void testFailureCreateDailyMeeting_recurAfterInvalid() {
        try {
            meeting = createDailyMeeting((short) -1, new Date());
            assertNull(meeting);
            fail();
        } catch (MeetingException me) {
            assertTrue(true);
            assertEquals(MeetingConstants.INVALID_RECURAFTER, me.getKey());
        }
    }

    public void testFailureCreateDailyMeeting_startDateIsNull() {
        try {
            meeting = createDailyMeeting(ONE, null);
            assertNull(meeting);
            fail();
        } catch (MeetingException me) {
            assertTrue(true);
            assertEquals(MeetingConstants.INVALID_STARTDATE, me.getKey());
        }
    }

    public void testSuccessfulCreateDailyMeeting() throws MeetingException {
        meeting = createDailyMeeting(ONE, new Date());
        meeting.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        meeting = (MeetingBO) StaticHibernateUtil.getSessionTL().get(MeetingBO.class, meeting.getMeetingId());
        assertNotNull(meeting);
        assertEquals(ONE, meeting.getMeetingDetails().getRecurAfter());
        assertEquals(RecurrenceType.DAILY, meeting.getMeetingDetails().getRecurrenceTypeEnum());
    }

    public void testFailureCreateWeeklyMeeting_weekDayIsNull() {
        try {
            meeting = createWeeklyMeeting(null, ONE, new Date());
            assertNull(meeting);
            fail();
        } catch (MeetingException me) {
            assertTrue(true);
            assertEquals(MeetingConstants.INVALID_WEEKDAY, me.getKey());
        }
    }

    public void testFailureCreateWeeklyMeeting_recurAfterIsNull() {
        try {
            meeting = createWeeklyMeeting(WeekDay.MONDAY, null, new Date());
            assertNull(meeting);
            fail();
        } catch (MeetingException me) {
            assertTrue(true);
            assertEquals(MeetingConstants.INVALID_RECURAFTER, me.getKey());
        }
    }

    public void testFailureCreateWeeklyMeeting_startDateIsNull() {
        try {
            meeting = createWeeklyMeeting(WeekDay.MONDAY, ONE, null);
            assertNull(meeting);
            fail();
        } catch (MeetingException me) {
            assertTrue(true);
            assertEquals(MeetingConstants.INVALID_STARTDATE, me.getKey());
        }
    }

    public void testFailureCreateWeeklyMeeting_meetingPlaceIsNull() {
        try {
            meeting = new MeetingBO(WeekDay.MONDAY, ONE, new Date(), MeetingType.CUSTOMER_MEETING, "");
            assertNull(meeting);
            fail();
        } catch (MeetingException me) {
            assertTrue(true);
            assertEquals(MeetingConstants.INVALID_MEETINGPLACE, me.getKey());
        }
    }

    public void testSuccessfulCreateWeeklyMeeting() throws MeetingException {
        meeting = createWeeklyMeeting(WeekDay.MONDAY, TWO, new Date());
        meeting.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        meeting = (MeetingBO) StaticHibernateUtil.getSessionTL().get(MeetingBO.class, meeting.getMeetingId());
        assertNotNull(meeting);
        assertEquals(TWO, meeting.getMeetingDetails().getRecurAfter());
        assertTrue(meeting.isWeekly());
        assertEquals(WeekDay.MONDAY, meeting.getMeetingDetails().getWeekDay());
    }

    public void testFailureCreateMonthlyMeetingOnDate_dayNumberIsNull() {
        try {
            meeting = createMonthlyMeetingOnDate(null, ONE, new Date());
            assertNull(meeting);
            fail();
        } catch (MeetingException me) {
            assertTrue(true);
            assertEquals(MeetingConstants.INVALID_DAYNUMBER_OR_WEEK, me.getKey());
        }
    }

    public void testFailureCreateMonthlyMeetingOnDate_dayNumberIsNInvalid() {
        try {
            dayNumber = Short.valueOf("32");
            meeting = createMonthlyMeetingOnDate(dayNumber, ONE, new Date());
            assertNull(meeting);
            fail();
        } catch (MeetingException me) {
            assertTrue(true);
            assertEquals(MeetingConstants.INVALID_DAYNUMBER, me.getKey());
        }
    }

    public void testFailureCreateMonthlyMeetingOnDate_recurAfterIsNull() {
        try {
            dayNumber = FIVE;
            meeting = createMonthlyMeetingOnDate(dayNumber, null, new Date());
            assertNull(meeting);
            fail();
        } catch (MeetingException me) {
            assertTrue(true);
            assertEquals(MeetingConstants.INVALID_RECURAFTER, me.getKey());
        }
    }

    public void testFailureCreateMonthlyMeetingOnDate_startDateIsNull() {
        try {
            dayNumber = FIVE;
            meeting = createMonthlyMeetingOnDate(dayNumber, ONE, null);
            assertNull(meeting);
            fail();
        } catch (MeetingException me) {
            assertTrue(true);
            assertEquals(MeetingConstants.INVALID_STARTDATE, me.getKey());
        }
    }

    public void testSuccessfulCreateMonthlyMeetingOnDate() throws MeetingException {
        dayNumber = FIVE;
        meeting = createMonthlyMeetingOnDate(dayNumber, ONE, new Date());
        meeting.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        meeting = (MeetingBO) StaticHibernateUtil.getSessionTL().get(MeetingBO.class, meeting.getMeetingId());
        assertNotNull(meeting);
        assertEquals(ONE, meeting.getMeetingDetails().getRecurAfter());
        assertTrue(meeting.isMonthlyOnDate());
        assertEquals(dayNumber, meeting.getMeetingDetails().getDayNumber());
    }

    public void testFailureCreateMonthlyMeetingOnWeekDay_weekDayIsNull() {
        try {
            meeting = createMonthlyMeetingOnWeekDay(null, RankType.FIRST, ONE, new Date());
            assertNull(meeting);
            fail();
        } catch (MeetingException me) {
            assertTrue(true);
            assertEquals(MeetingConstants.INVALID_WEEKDAY_OR_WEEKRANK, me.getKey());
        }
    }

    public void testFailureCreateMonthlyMeetingOnWeekDay_weekRankIsNull() {
        try {
            meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, null, ONE, new Date());
            assertNull(meeting);
            fail();
        } catch (MeetingException me) {
            assertTrue(true);
            assertEquals(MeetingConstants.INVALID_WEEKDAY_OR_WEEKRANK, me.getKey());
        }
    }

    public void testFailureCreateMonthlyMeetingOnWeekDay_recurAfterIsNull() {
        try {
            meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankType.FIRST, null, new Date());
            assertNull(meeting);
            fail();
        } catch (MeetingException me) {
            assertTrue(true);
            assertEquals(MeetingConstants.INVALID_RECURAFTER, me.getKey());
        }
    }

    public void testFailureCreateMonthlyMeetingOnWeekDay_startDateIsNull() {
        try {
            dayNumber = FIVE;
            meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankType.FIRST, ONE, null);
            assertNull(meeting);
            fail();
        } catch (MeetingException me) {
            assertTrue(true);
            assertEquals(MeetingConstants.INVALID_STARTDATE, me.getKey());
        }
    }

    public void testSuccessfulCreateMonthlyMeetingOnWeekDay() throws MeetingException {
        meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankType.FIRST, ONE, new Date());
        meeting.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        meeting = (MeetingBO) StaticHibernateUtil.getSessionTL().get(MeetingBO.class, meeting.getMeetingId());
        assertNotNull(meeting);
        assertEquals(ONE, meeting.getMeetingDetails().getRecurAfter());
        assertTrue(meeting.isMonthly());
        assertFalse(meeting.isMonthlyOnDate());
        assertEquals(WeekDay.MONDAY, meeting.getMeetingDetails().getWeekDay());
        assertEquals(RankType.FIRST, meeting.getMeetingDetails().getWeekRank());
    }

    public void testFailureUpdateWeeklyMeeting_MeetingPlace_Null() throws MeetingException {
        meeting = createWeeklyMeeting(WeekDay.MONDAY, FIVE, new Date());
        try {
            meeting.update(WeekDay.WEDNESDAY, "");
            fail();
        } catch (MeetingException me) {
            assertTrue(true);
            assertEquals(MeetingConstants.INVALID_MEETINGPLACE, me.getKey());
        }
    }

    public void testSuccessfulUpdateWeeklyMeeting() throws MeetingException {
        meeting = createWeeklyMeeting(WeekDay.MONDAY, FIVE, new Date());
        meeting.update(WeekDay.WEDNESDAY, "Delhi");
        assertEquals("Delhi", meeting.getMeetingPlace());
        assertEquals(FIVE, meeting.getMeetingDetails().getRecurAfter());
        assertEquals(WeekDay.WEDNESDAY, meeting.getMeetingDetails().getWeekDay());
    }

    public void testFailureUpdateMontlyMeetingInValidDayNumber() throws MeetingException {
        meeting = createMonthlyMeetingOnDate(Short.valueOf("5"), TWO, new Date());
        try {
            meeting.update(Short.valueOf("32"), "Delhi");
            fail();
        } catch (MeetingException me) {
            assertTrue(true);
            assertEquals(MeetingConstants.INVALID_DAYNUMBER, me.getKey());
        }
    }

    public void testFailureUpdateMontlyMeeting_MeetingPlace_Null() throws MeetingException {
        meeting = createMonthlyMeetingOnDate(Short.valueOf("5"), TWO, new Date());
        try {
            meeting.update(SIX, "");
            fail();
        } catch (MeetingException me) {
            assertTrue(true);
            assertEquals(MeetingConstants.INVALID_MEETINGPLACE, me.getKey());
        }
    }

    public void testSuccessfulUpdateMontlyMeetingOnDate() throws MeetingException {
        meeting = createMonthlyMeetingOnDate(Short.valueOf("5"), TWO, new Date());
        meeting.update(TEN, "Delhi");
        assertEquals("Delhi", meeting.getMeetingPlace());
        assertEquals(TWO, meeting.getMeetingDetails().getRecurAfter());
        assertEquals(TEN, meeting.getMeetingDetails().getDayNumber());
    }

    public void testFailureUpdateMontlyMeetingOnWeekDay() throws MeetingException {
        meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankType.FIRST, TWO, new Date());
        try {
            meeting.update(null, null, "Delhi");
            fail();
        } catch (MeetingException me) {
            assertTrue(true);
            assertEquals(MeetingConstants.INVALID_WEEKDAY_OR_WEEKRANK, me.getKey());
        }
    }

    public void testSuccessfulUpdateMontlyMeetingOnWeekDay() throws MeetingException {
        meeting = createMonthlyMeetingOnWeekDay(WeekDay.MONDAY, RankType.FIRST, TWO, new Date());
        meeting.update(WeekDay.THURSDAY, RankType.THIRD, "Delhi");
        assertEquals("Delhi", meeting.getMeetingPlace());
        assertEquals(TWO, meeting.getMeetingDetails().getRecurAfter());
        assertEquals(WeekDay.THURSDAY, meeting.getMeetingDetails().getWeekDay());
        assertEquals(RankType.THIRD, meeting.getMeetingDetails().getWeekRank());
    }

    private MeetingBO createDailyMeeting(Short recurAfer, Date startDate) throws MeetingException {
        return new MeetingBO(RecurrenceType.DAILY, recurAfer, startDate, MeetingType.CUSTOMER_MEETING);
    }

    private MeetingBO createWeeklyMeeting(WeekDay weekDay, Short recurAfer, Date startDate) throws MeetingException {
        return new MeetingBO(weekDay, recurAfer, startDate, MeetingType.CUSTOMER_MEETING, "MeetingPlace");
    }

    private MeetingBO createMonthlyMeetingOnDate(Short dayNumber, Short recurAfer, Date startDate)
            throws MeetingException {
        return new MeetingBO(dayNumber, recurAfer, startDate, MeetingType.CUSTOMER_MEETING, "MeetingPlace");
    }

    private MeetingBO createMonthlyMeetingOnWeekDay(WeekDay weekDay, RankType rank, Short recurAfer, Date startDate)
            throws MeetingException {
        return new MeetingBO(weekDay, rank, recurAfer, startDate, MeetingType.CUSTOMER_MEETING, "MeetingPlace");
    }

    private List createExpectedList(String dates) throws Exception {
        List expectedList = new ArrayList();
        StringTokenizer tokenizer = new StringTokenizer(dates, ",");
        while (tokenizer.hasMoreTokens()) {
            String date = tokenizer.nextToken();
            expectedList.add(getDate(date));
        }
        return expectedList;
    }

    void matchDateLists(List<Date> expectedList, List<Date> list) {
        assertEquals(expectedList.size(), list.size());
        for (int i = 0; i < expectedList.size(); i++)
            assertEquals("Dates are invalid", expectedList.get(i), list.get(i));
    }

    public void testShouldReturnDateOfNextWeekOnSameWeekdayAsConfiguredIfStartDateDayIsAfterConfiguredWeekday()
            throws Exception {
        Date dateFallingOnTuesday = DateUtils.getDate(2008, Calendar.JANUARY, 8);
        MeetingBO meetingBO = createWeeklyMeeting(WeekDay.MONDAY, ONE, DateUtils.getDate(2008, Calendar.JANUARY, 1));
        Date shouldBeDateFallingNextWeekOnMeetingWeekday = meetingBO
                .getNextScheduleDateAfterRecurrence(dateFallingOnTuesday);
        assertEquals(DateUtils.getDate(2008, Calendar.JANUARY, 14), shouldBeDateFallingNextWeekOnMeetingWeekday);
    }

    public void testShouldReturnDateOfSameWeekOnSameWeekdayAsConfiguredIfStartDateDayIsBeforeConfiguredWeekday()
            throws Exception {
        Date dateFallingOnSunday = DateUtils.getDate(2008, Calendar.JANUARY, 13);
        MeetingBO meetingBO = createWeeklyMeeting(WeekDay.MONDAY, ONE, DateUtils.getDate(2008, Calendar.JANUARY, 1));
        Date shouldBeDateFallingNextWeekOnMeetingWeekday = meetingBO
                .getNextScheduleDateAfterRecurrence(dateFallingOnSunday);
        assertEquals(DateUtils.getDate(2008, Calendar.JANUARY, 14), shouldBeDateFallingNextWeekOnMeetingWeekday);
    }
}
