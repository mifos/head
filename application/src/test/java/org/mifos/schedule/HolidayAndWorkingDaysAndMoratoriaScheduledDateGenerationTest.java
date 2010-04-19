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

package org.mifos.schedule;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.junit.Before;
import org.junit.Test;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.calendar.DayOfWeek;
import org.mifos.domain.builders.HolidayBuilder;
import org.mifos.domain.builders.ScheduledEventBuilder;
import org.mifos.schedule.internal.HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration;

/**
 * I test {@link HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration}.
 *
 * <p>Tests involve dates starting in June, 2011. To make it easier to construct tests with certain
 * days of the week/month, the calendar for these months appears below:
 *
 * <pre>
 ***********************
 *   2011
 ***********************
 *  S  M  T  W  T  F  S
 *  1  2  3  4  5  6  7  May
 *  8  9 10 11 12 13 14
 * 15 16 17 18 19 20 21
 * 22 23 24 25 26 27 28
 * 29 30 31
 *           1  2  3  4  June
 *  5  6  7  8  9 10 11
 * 12 13 14 15 16 17 18
 * 19 20 21 22 23 24 25
 * 26 27 28 29 30
 *                 1  2
 *  3  4  5  6  7  8  9  July
 * 10 11 12 13 14 15 16
 * 17 18 19 20 21 22 23
 * 24 25 26 27 28 29 30
 * 31
 *     1  2  3  4  5  6
 *  7  8  9 10 11 12 13  August
 * 14 15 16 17 18 19 20
 * 21 22 23 24 25 26 27
 * 28 29 30 31
 *              1  2  3
 *  4  5  6  7  8  9 10  September
 * 11 12 13 14 15 16 17
 * 18 19 20 21 22 23 24
 * 25 26 27 28 29 30
 *                    1
 *  2  3  4  5  6  7  8
 *  9 10 11 12 13 14 15  October
 * 16 17 18 19 20 21 22
 * 23 24 25 26 27 28 29
 * 30 31
 *        1  2  3  4  5
 *  6  7  8  9 10 11 12  November
 * 13 14 15 16 17 18 19
 * 20 21 22 23 24 25 26
 * 27 28 29 30
 *
 *              1  2  3
 *  4  5  6  7  8  9 10  December
 * 11 12 13 14 15 16 17
 * 18 19 20 21 22 23 24
 * 25 26 27 28 29 30 31
 ***********************
 *   2012 (leap year)
 ***********************
 *  1  2  3  4  5  6  7  January
 *  8  9 10 11 12 13 14
 * 15 16 17 18 19 20 21
 * 22 23 24 25 26 27 28
 * 29 30 31
 *           1  2  3  4
 *  5  6  7  8  9 10 11  February (leap year)
 * 12 13 14 15 16 17 18
 * 19 20 21 22 23 24 25
 * 26 27 28 29
 *              1  2  3
 *  4  5  6  7  8  9 10  March
 * 11 12 13 14 15 16 17
 * 18 19 20 21 22 23 24
 * 25 26 27 28 29 30 31
 * </pre>
 *
 */
public class HolidayAndWorkingDaysAndMoratoriaScheduledDateGenerationTest {

    private ScheduledDateGeneration scheduleGeneration;

    private List<Days> workingDays;

    //private DateTime mon_2011_07_04 = date(2011,7,4);
    private DateTime mon_2011_06_20 = date(2011,6,20);
    private DateTime wed_2011_06_22 = date(2011,6,22);
    private DateTime wed_2011_06_29 = date(2011,6,29);
    private DateTime mon_2011_07_04 = date(2011,7,4);
    private DateTime mon_2011_06_27 = date(2011,6,27);
    private Holiday sevenDayMoratoriumStartingJuly4th = new HolidayBuilder().from(mon_2011_07_04)
                                                          .to(mon_2011_07_04.plusDays(6))
                                                          .withNextMeetingRule()
                                                          .withRepaymentMoratoriumRule()
                                                          .build();


    @Before
    public void setupAndInjectDependencies() {

        workingDays = Arrays.asList(DayOfWeek.mondayAsDay(), DayOfWeek.tuesdayAsDay(), DayOfWeek
                .wednesdayAsDay(), DayOfWeek.thursdayAsDay(), DayOfWeek.fridayAsDay());

        List<Holiday> upcomingHolidays = new ArrayList<Holiday>();

        scheduleGeneration = new HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration(workingDays, upcomingHolidays);

        //Dates frequently used in tests
    }

    @Test
    public void canGenerateScheduledDates() {

        DateTime lastScheduledDate = DayOfWeek.mondayMidnight();

        ScheduledEvent recurringEvent = new ScheduledEventBuilder().every(1).weeks().on(DayOfWeek.monday()).build();

        List<DateTime> scheduledDates = scheduleGeneration
                .generateScheduledDates(10, lastScheduledDate, recurringEvent);

        assertThat(scheduledDates.size(), is(notNullValue()));
        assertThat(scheduledDates.size(), is(10));
    }

    @Test
    public void canGenerateScheduledDatesThatMatchScheduledEventBasedOnLastScheduledDate() {

        DateTime lastScheduledDate = DayOfWeek.mondayMidnight();

        ScheduledEvent recurringEvent = new ScheduledEventBuilder().every(1).weeks().on(DayOfWeek.monday()).build();

        List<DateTime> scheduledDates = scheduleGeneration
                .generateScheduledDates(10, lastScheduledDate, recurringEvent);

        assertThat(scheduledDates.get(0), is(lastScheduledDate));
        assertThat(scheduledDates.get(1), is(DayOfWeek.oneWeekFrom(lastScheduledDate)));
    }

    @Test
    public void canGenerateAllScheduledDatesThatMatchScheduleEvent() {

        DateTime lastScheduledDate = DayOfWeek.mondayMidnight();
        DateTime dayAfterLastScheduledDate = DayOfWeek.tuesdayMidnight();

        ScheduledEvent scheduleEvent = new ScheduledEventBuilder().every(1).weeks().on(DayOfWeek.monday()).build();

        List<DateTime> scheduledDates = scheduleGeneration.generateScheduledDates(10, dayAfterLastScheduledDate,
                scheduleEvent);

        DateTime lastDate = lastScheduledDate;
        for (DateTime generatedDate : scheduledDates) {
            assertThat(generatedDate, is(DayOfWeek.oneWeekFrom(lastDate)));
            lastDate = generatedDate;
        }
    }

    @Test
    public void weeklyScheduledEventWithSecondDateInOneWeekNextMeetingHolidayShouldPushOutSecondDateOneWeek() {

        Holiday oneWeekNextMeetingHolidayStartingJuly4th
            = new HolidayBuilder().from(mon_2011_07_04).to(mon_2011_07_04.plusWeeks(1))
                                                       .withNextMeetingRule()
                                                       .build();
        validateDates(new ScheduleBuilder().withWeeklyEvent(1, wed_2011_06_29.getDayOfWeek())
                                           .withHolidays(oneWeekNextMeetingHolidayStartingJuly4th)
                                           .withStartDate(wed_2011_06_29)
                                           .withNumberOfDates(4)
                                           .build(),
                      wed_2011_06_29, wed_2011_06_29.plusWeeks(2),
                      wed_2011_06_29.plusWeeks(2), wed_2011_06_29.plusWeeks(3));
    }

    @Test
    public void weeklyScheduledEventSecondThirdFourthDateInThreeWeekNextMeetingHolidayShouldQuadrupleUpDatesAfterHoliday() {
        Holiday threeWeekNextMeetngHolidayStartingJuly4th
            = new HolidayBuilder().from(mon_2011_07_04)
                                  .to(mon_2011_07_04.plusWeeks(3))
                                  .withNextMeetingRule()
                                  .build();
        validateDates(new ScheduleBuilder().withHolidays(threeWeekNextMeetngHolidayStartingJuly4th)
                                           .withWeeklyEvent(1, wed_2011_06_29.getDayOfWeek())
                                           .withNumberOfDates(4)
                                           .withStartDate(wed_2011_06_29.minusDays(1))
                                           .build(),
                      wed_2011_06_29, wed_2011_06_29.plusWeeks(4),
                      wed_2011_06_29.plusWeeks(4), wed_2011_06_29.plusWeeks(4));
    }

    @Test
    public void weeklyScheduleOneWeekMoratoriumEnclosesSecondScheduledDateShouldPushOutSecondAndThirdDatesOneWeek() {

        validateDates(new ScheduleBuilder().withHolidays(sevenDayMoratoriumStartingJuly4th)
                                           .withStartDate(wed_2011_06_29.minusDays(1))
                                           .withWeeklyEvent(1, wed_2011_06_29.getDayOfWeek())
                                           .withNumberOfDates(3)
                                           .build(),
                      wed_2011_06_29, wed_2011_06_29.plusWeeks(2), wed_2011_06_29.plusWeeks(3));
    }

    @Test
    public void weeklyScheduleTwoWeekMoratoriumEnclosesSecondAndThirdScheduledDatesShouldPushSecondAndFollowingDatesTwoWeeks() {

        Holiday twoWeekMoratoriumStartingJuly4th = new HolidayBuilder()
                                         .from(mon_2011_07_04)
                                         .to(mon_2011_07_04.plusWeeks(2))
                                         .withRepaymentMoratoriumRule()
                                         .build();
        validateDates(new ScheduleBuilder().withHolidays(twoWeekMoratoriumStartingJuly4th)
                                           .withStartDate(wed_2011_06_29.minusDays(1))
                                           .withWeeklyEvent(1, wed_2011_06_29.getDayOfWeek())
                                           .withNumberOfDates(3)
                                           .build(),
                      wed_2011_06_29, wed_2011_06_29.plusWeeks(3), wed_2011_06_29.plusWeeks(4));
    }

    @Test
    public void weeklyScheduledEventWithSecondThirdFourthDateInThreeWeekSameDayHolidayExpectNoAdjustment() {

        Holiday threeWeekSameDay = new HolidayBuilder()
                                        .from(mon_2011_07_04)
                                        .to(mon_2011_07_04.plusWeeks(3))
                                        .withSameDayAsRule()
                                        .build();
        validateDates(new ScheduleBuilder().withHolidays(threeWeekSameDay)
                                           .withWeeklyEvent(1, wed_2011_06_29.getDayOfWeek())
                                           .withStartDate(wed_2011_06_29.minusDays(1))
                                           .withNumberOfDates(5)
                                           .build(),
                      wed_2011_06_29, wed_2011_06_29.plusWeeks(1), wed_2011_06_29.plusWeeks(2),
                      wed_2011_06_29.plusWeeks(3), wed_2011_06_29.plusWeeks(4));
    }

    @Test
    public void weeklyScheduledEventWithMoratoriumImmediatelyFollowingNexRepaymentHoliday() {

       //First holiday extends for 14 consecutive days from 2011/6/27
        Holiday twoWeekNextRepaymentHoliday = new HolidayBuilder()
                                                .from(mon_2011_06_27)
                                                .to(mon_2011_06_27.plusWeeks(2).minusDays(1))
                                                .withNextMeetingRule()
                                                .build();
        // Next moratorium follows for 14 days
        Holiday twoWeekMoratorium = new HolidayBuilder()
                                                .from(mon_2011_06_27.plusWeeks(2))
                                                .to(mon_2011_06_27.plusWeeks(4).plusDays(-1))
                                                .withRepaymentMoratoriumRule()
                                                .build();
        /*
         * Generate a Wednesday schedule from the Monday the week before the first holiday starts, 2011/6/20.
         * Schedule should start on the next Wednesday, 2011/6/22 (2 days from the starting point).
         * The second and third dates get shifted into the first week of the moratorium, on the same
         * date as the fourth date. These and the remaining schedule get shifted two mor weeks
         *  past the moratorium.
         */
        validateDates(new ScheduleBuilder().withHolidays(twoWeekNextRepaymentHoliday, twoWeekMoratorium)
                                           .withWeeklyEvent(1, DayOfWeek.wednesday())
                                           .withNumberOfDates(6)
                                           .withStartDate(mon_2011_06_20)
                                           .build(),
                      wed_2011_06_22,              wed_2011_06_22.plusWeeks(5),
                      wed_2011_06_22.plusWeeks(5), wed_2011_06_22.plusWeeks(5),
                      wed_2011_06_22.plusWeeks(6), wed_2011_06_22.plusWeeks(7));
    }

    @Test
    public void weeklyScheduledEventWithMoratoriumImmediatelyFollowingSameDayHoliday() {

        //First holiday extends for 14 consecutive days
        Holiday twoWeekSameDayHoliday = new HolidayBuilder()
                                                .from(mon_2011_06_27)
                                                .to(mon_2011_06_27.plusWeeks(2).minusDays(1))
                                                .withSameDayAsRule()
                                                .build();
        // Next moratorium follows for 14 days
        Holiday twoWeekMoratorium = new HolidayBuilder()
                                                .from(mon_2011_06_27.plusWeeks(2))
                                                .to(mon_2011_06_27.plusWeeks(4).minusDays(1))
                                                .withRepaymentMoratoriumRule()
                                                .build();
        /*
         * Generate a Wednesday schedule from the Monday the week before the first holiday starts, 2011/6/20.
         * Schedule should start on the next Wednesday, 2011/6/22 (2 days from the starting point).
         * The second and third dates don't move (same-day holiday)
         * Fourth and fifth dates get shifted two weeks past the moratorium.
         */
        validateDates(new ScheduleBuilder().withHolidays(twoWeekSameDayHoliday, twoWeekMoratorium)
                                           .withWeeklyEvent(1, DayOfWeek.wednesday())
                                           .withNumberOfDates(6)
                                           .withStartDate(mon_2011_06_20)
                                           .build(),
                     wed_2011_06_22,              wed_2011_06_22.plusWeeks(1),
                     wed_2011_06_22.plusWeeks(2), wed_2011_06_22.plusWeeks(5),
                     wed_2011_06_22.plusWeeks(6), wed_2011_06_22.plusWeeks(7));
    }

    @Test
    public void weeklyScheduledEventWithMoratoriumImmediatelyFollowingNextWorkingDayHoliday() {

        Holiday twoWeekNextWorkingDayHoliday = new HolidayBuilder()
                                                .from(mon_2011_06_27)
                                                .to(mon_2011_06_27.plusWeeks(2).minusDays(1))
                                                .withNextWorkingDayRule()
                                                .build();
        Holiday twoWeekMoratorium = new HolidayBuilder()
                                                .from(mon_2011_06_27.plusWeeks(2))
                                                .to(mon_2011_06_27.plusWeeks(4).minusDays(1))
                                                .withRepaymentMoratoriumRule()
                                                .build();

        /*
         * Schedule should start on the next Wednesday (2 days from the starting point).
         * The second and third unadjusted dates, being in the next-working-day holiday shift to the
         * first Monday after the holiday and moratorium. Because the fourth unadjusted date is in the moratorium,
         * it and the remaining schedule are shifted two more weeks past the moratorium.
         */
        validateDates(new ScheduleBuilder().withHolidays(twoWeekNextWorkingDayHoliday, twoWeekMoratorium)
                                           .withWeeklyEvent(1, DayOfWeek.wednesday())
                                           .withNumberOfDates(6)
                                           .withStartDate(mon_2011_06_20)
                                           .build(),
                      wed_2011_06_22,
                      wed_2011_06_22.plusWeeks(5).withDayOfWeek(DayOfWeek.monday()),
                      wed_2011_06_22.plusWeeks(5).withDayOfWeek(DayOfWeek.monday()),
                      wed_2011_06_22.plusWeeks(5),
                      wed_2011_06_22.plusWeeks(6), wed_2011_06_22.plusWeeks(7));
    }

    @Test
    public void biWeeklyScheduleNoHoliday() {

        validateDates(new ScheduleBuilder().withHolidays()
                                           .withWeeklyEvent(2, DateTimeConstants.MONDAY)
                                           .withStartDate(date(2011,6,27))
                                           .withNumberOfDates(3)
                                           .build(),
                      date(2011, 6, 27), date(2011, 7, 11), date(2011, 7, 25));
   }

    @Test
    public void biWeeklyScheduleSecondDateInNextMeetingHolidayShouldShiftSecondDateByTwoWeeks() {

        Holiday twoWeekNextWorkingDayHoliday = new HolidayBuilder()
                                                      .from(date(2011,7,11))
                                                      .to(date(2011,7,17))
                                                      .withNextMeetingRule()
                                                      .build();
        validateDates(new ScheduleBuilder().withHolidays(twoWeekNextWorkingDayHoliday)
                                           .withWeeklyEvent(2, DateTimeConstants.MONDAY)
                                           .withStartDate(date(2011,6,27))
                                           .withNumberOfDates(3)
                                           .build(),
                      date(2011, 6, 27), date(2011, 7, 25), date(2011, 7, 25));
   }

    @Test
    public void biWeeklyScheduleSecondDateHitsThreeDayMoratoriumShouldPushOutSecondAndThirdDates() {

        Holiday independenceDay = new HolidayBuilder()
                                         .from(date(2011,7,4))
                                         .to(date(2011,7,6))
                                         .withRepaymentMoratoriumRule()
                                         .build();
        validateDates(new ScheduleBuilder().withHolidays(independenceDay)
                                           .withWeeklyEvent(2, DateTimeConstants.MONDAY)
                                           .withStartDate(date(2011,6,20))
                                           .withNumberOfDates(3)
                                           .build(),
                      date(2011, 6, 20), date(2011,7,18), date(2011,8,1));
    }

    /**************************************************
     * Tests for schedules recurring monthly by day of month
     **************************************************/

    @Test
    public void monthlyByDayScheduleNoHolidaysShouldNotShiftAnyDates() {

        //July & August dates shifted one month past moratorium, then adjusted for working day
        validateDates(new ScheduleBuilder().withHolidays()
                                           .withDayOfMonthEvent(1, 6)
                                           .withStartDate(date(2011,6,6))
                                           .withNumberOfDates(10)
                                           .build(),
                      date(2011,  6, 6), date(2011,  7, 6), date(2011, 8, 8), date(2011, 9, 6), date(2011, 10, 6),
                      date(2011, 11, 7), date(2011, 12, 6), date(2012, 1, 6), date(2012, 2, 6), date(2012,  3, 6));
   }

    @Test
    public void monthlyByDayScheduleSecondDateInMoratoriumShouldShiftSecondAndThirdDatesbyOneMonth() {

        validateDates(new ScheduleBuilder().withHolidays(sevenDayMoratoriumStartingJuly4th)
                                           .withDayOfMonthEvent(1, 6)
                                           .withStartDate(date(2011,6,6))
                                           .withNumberOfDates(3)
                                           .build(),
                      date(2011, 6, 6), date(2011, 8, 8), date(2011, 9, 6));
   }

    @Test
    public void monthlyByDayScheduleSecondDateInNextWorkingDayHolidayShouldShiftSecondDateToNextWorkingDay() {

        Holiday twoWeekNextWorkingDayHoliday = new HolidayBuilder()
                                                      .from(date(2011,7,4))
                                                      .to(date(2011,7,17))
                                                      .withNextWorkingDayRule()
                                                      .build();
        validateDates(new ScheduleBuilder().withHolidays(twoWeekNextWorkingDayHoliday)
                                           .withDayOfMonthEvent(1, 6)
                                           .withStartDate(date(2011,6,6))
                                           .withNumberOfDates(3)
                                           .build(),
                      date(2011, 6, 6), date(2011, 7, 18), date(2011, 8, 8));
   }

    @Test
    public void monthlyByDayScheduleSecondDateInNextMeetingHolidayShouldShiftSecondDateByOneMonth() {

        Holiday twoWeekNextWorkingDayHoliday = new HolidayBuilder()
                                                      .from(date(2011,7,4))
                                                      .to(date(2011,7,17))
                                                      .withNextMeetingRule()
                                                      .build();
        validateDates(new ScheduleBuilder().withHolidays(twoWeekNextWorkingDayHoliday)
                                           .withDayOfMonthEvent(1, 6)
                                           .withStartDate(date(2011,6,6))
                                           .withNumberOfDates(3)
                                           .build(),
                      date(2011, 6, 6), date(2011, 8, 8), date(2011, 8, 8));
   }

    @Test
    public void monthlyByDayScheduleSecondDateInSameDayHolidayShouldNotShiftAnyDates() {

        Holiday twoWeekNextWorkingDayHoliday = new HolidayBuilder()
                                                      .from(date(2011,7,4))
                                                      .to(date(2011,7,17))
                                                      .withSameDayAsRule()
                                                      .build();
        validateDates(new ScheduleBuilder().withHolidays(twoWeekNextWorkingDayHoliday)
                                           .withDayOfMonthEvent(1, 6)
                                           .withStartDate(date(2011,6,6))
                                           .withNumberOfDates(3)
                                           .build(),
                      date(2011, 6, 6), date(2011, 7, 6), date(2011, 8, 8));
   }

    @Test
    public void monthlyByDayScheduleSecondDateMissesMoratoriumShouldNotShiftAnyDates() {

        //Moratorium does not include 13th of month -- no shifts except to adjust for next working day in August
        validateDates(new ScheduleBuilder().withHolidays(sevenDayMoratoriumStartingJuly4th)
                                           .withDayOfMonthEvent(1, 13)
                                           .withStartDate(date(2011,6,13))
                                           .withNumberOfDates(3)
                                           .build(),
                      date(2011, 6, 13), date(2011, 7, 13), date(2011, 8, 15));
   }

    @Test
    public void biMonthlyByDayScheduleNoHolidaysShouldNotShiftAnyDates() {

        //July & August dates shifted one month past moratorium, then adjusted for working day
        validateDates(new ScheduleBuilder().withHolidays()
                                           .withDayOfMonthEvent(2, 6)
                                           .withStartDate(date(2011, 5, 6))
                                           .withNumberOfDates(4)
                                           .build(),
                      date(2011, 5, 6), date(2011, 7, 6), date(2011, 9, 6), date(2011, 11, 7));
   }

    @Test
    public void biMonthlyByDayScheduleSecondDateInMoratoriumShouldShiftSecondAndThirdDatesbyTwoMonths() {

        validateDates(new ScheduleBuilder().withHolidays(sevenDayMoratoriumStartingJuly4th)
                                           .withDayOfMonthEvent(2, 6)
                                           .withStartDate(date(2011,5,6))
                                           .withNumberOfDates(3)
                                           .build(),
                      date(2011, 5, 6), date(2011, 9, 6), date(2011, 11, 7));
   }

    @Test
    public void biMonthlyByDayScheduleSecondDateInNextWorkingDayHolidayShouldShiftSecondDateToNextWorkingDay() {

        Holiday twoWeekNextWorkingDayHoliday = new HolidayBuilder()
                                                      .from(date(2011,7,4))
                                                      .to(date(2011,7,17))
                                                      .withNextWorkingDayRule()
                                                      .build();
        validateDates(new ScheduleBuilder().withHolidays(twoWeekNextWorkingDayHoliday)
                                           .withDayOfMonthEvent(2, 6)
                                           .withStartDate(date(2011,5,6))
                                           .withNumberOfDates(3)
                                           .build(),
                      date(2011, 5, 6), date(2011, 7, 18), date(2011, 9, 6));
   }

    @Test
    public void biMonthlyByDayScheduleSecondDateInNextMeetingHolidayShouldShiftSecondDateByOneMonth() {

        Holiday twoWeekNextWorkingDayHoliday = new HolidayBuilder()
                                                      .from(date(2011,7,4))
                                                      .to(date(2011,7,17))
                                                      .withNextMeetingRule()
                                                      .build();
        validateDates(new ScheduleBuilder().withHolidays(twoWeekNextWorkingDayHoliday)
                                           .withDayOfMonthEvent(2, 6)
                                           .withStartDate(date(2011,5,6))
                                           .withNumberOfDates(3)
                                           .build(),
                      date(2011, 5, 6), date(2011, 9, 6), date(2011, 9, 6));
   }

    @Test
    public void biMonthlyByDayScheduleSecondDateInSameDayHolidayShouldNotShiftAnyDates() {

        Holiday twoWeekNextWorkingDayHoliday = new HolidayBuilder()
                                                      .from(date(2011,7,4))
                                                      .to(date(2011,7,17))
                                                      .withSameDayAsRule()
                                                      .build();
        validateDates(new ScheduleBuilder().withHolidays(twoWeekNextWorkingDayHoliday)
                                           .withDayOfMonthEvent(2, 6)
                                           .withStartDate(date(2011,5,6))
                                           .withNumberOfDates(3)
                                           .build(),
                      date(2011, 5, 6), date(2011, 7, 6), date(2011, 9, 6));
   }

    @Test
    public void biMonthlyByDayScheduleSecondDateMissesMoratoriumShouldNotShiftAnyDates() {

        //Moratorium does not include 13th of month -- no shifts except to adjust for next working day in August
        validateDates(new ScheduleBuilder().withHolidays(sevenDayMoratoriumStartingJuly4th)
                                           .withDayOfMonthEvent(1, 13)
                                           .withStartDate(date(2011,6,13))
                                           .withNumberOfDates(3)
                                           .build(),
                      date(2011, 6, 13), date(2011, 7, 13), date(2011, 8, 15));
   }

    @Test
    public void shouldNotGoIntoRecursiveLoopWhenThroughDateOccursMoreThanTenPeriodsFromStartDate() {

        MeetingBO meeting = new MeetingBuilder().customerMeeting().weekly().every(1).build();
        ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(meeting);

        DateTime startDate = new DateTime().withYear(2010).withMonthOfYear(4).withDayOfMonth(1).toDateMidnight().toDateTime();
        DateTime throughDate = new DateTime().withYear(2010).withMonthOfYear(6).withDayOfMonth(21).toDateMidnight().toDateTime();

        scheduleGeneration.generateScheduledDatesThrough(startDate, throughDate, scheduledEvent);
    }

    @Test
    public void shouldNotGoIntoRecursiveLoopWhenThroughDateOccursExactlyTenPeriodsFromStartDate() {

        MeetingBO meeting = new MeetingBuilder().customerMeeting().weekly().every(1).build();
        ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(meeting);

        DateTime startDate = new DateTime().withYear(2010).withMonthOfYear(4).withDayOfMonth(1).toDateMidnight().toDateTime();
        DateTime throughDate = new DateTime().withYear(2010).withMonthOfYear(6).withDayOfMonth(7).toDateMidnight().toDateTime();

        scheduleGeneration.generateScheduledDatesThrough(startDate, throughDate, scheduledEvent);
    }

    /*******************************
     * Helper methods
     *******************************/


    private void validateDates (List<DateTime> actualDates, DateTime... expectedDates) {
        assertThat (actualDates.size(), is (expectedDates.length));
        for (short i = 0; i < actualDates.size(); i++) {
            assertThat (actualDates.get(i), is (expectedDates[i]));
        }
    }

    private DateTime date (int year, int month, int day) {
        return new DateTime().withDate(year, month, day).toDateMidnight().toDateTime();
    }

    private class ScheduleBuilder {

        private List<Holiday> holidays = new ArrayList<Holiday>();
        private List<Days> defaultWorkingDays = Arrays.asList(DayOfWeek.mondayAsDay(), DayOfWeek.tuesdayAsDay(),
                DayOfWeek.wednesdayAsDay(), DayOfWeek.thursdayAsDay(), DayOfWeek.fridayAsDay());
        private List<Days> workingDays = defaultWorkingDays;
        private ScheduledEvent scheduledEvent;
        private int numberOfDatesToGenerate;
        private DateTime startDate;

        public List<DateTime> build() {
            HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration generator
                = new HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration(workingDays, holidays);
            return generator.generateScheduledDates(numberOfDatesToGenerate, startDate, scheduledEvent);
        }


        public ScheduleBuilder withWeeklyEvent (int recurrence, int dayOfWeek) {
            scheduledEvent = new ScheduledEventBuilder().every(recurrence).weeks().on(dayOfWeek).build();
            return this;
        }

        public ScheduleBuilder withDayOfMonthEvent (int recurrence, int dayOfMonth) {
            scheduledEvent = new ScheduledEventBuilder().every(recurrence).months().onDayOfMonth(dayOfMonth).build();
            return this;
        }

        public ScheduleBuilder withHolidays(Holiday...upComingHolidays) {
            holidays = Arrays.asList(upComingHolidays);
            return this;
        }

        public ScheduleBuilder withWorkingDays (List<Days> days) {
            workingDays = days;
            return this;
        }

        public ScheduleBuilder withStartDate (DateTime date) {
            this.startDate = date;
            return this;
        }

        public ScheduleBuilder withNumberOfDates (int dateCount) {
            this.numberOfDatesToGenerate = dateCount;
            return this;
        }

    }
}