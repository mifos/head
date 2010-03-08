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
import org.joda.time.Days;
import org.junit.Before;
import org.junit.Test;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.calendar.DayOfWeek;
import org.mifos.domain.builders.HolidayBuilder;
import org.mifos.domain.builders.ScheduledEventBuilder;
import org.mifos.schedule.internal.HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration;

/**
 * I test {@link HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration}.
 *
 */
public class HolidayAndWorkingDaysAndMoratoriaScheduledDateGenerationTest {

    private ScheduledDateGeneration scheduleGeneration;

    private List<Days> workingDays;

    private DateTime mon_2011_07_04;
    private DateTime wed_2011_06_29;

    @Before
    public void setupAndInjectDependencies() {

        workingDays = Arrays.asList(DayOfWeek.mondayAsDay(), DayOfWeek.tuesdayAsDay(), DayOfWeek
                .wednesdayAsDay(), DayOfWeek.thursdayAsDay(), DayOfWeek.fridayAsDay());

        List<Holiday> upcomingHolidays = new ArrayList<Holiday>();

        scheduleGeneration = new HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration(workingDays, upcomingHolidays);

        //Dates frequently used in tests
        mon_2011_07_04 = new DateTime().withYear(2011).withMonthOfYear(7).withDayOfMonth(4)
                                       .toDateMidnight().toDateTime();
        wed_2011_06_29 = new DateTime().withYear(2011).withMonthOfYear(6).withDayOfMonth(29)
                                       .toDateMidnight().toDateTime();
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
    public void aWeeklyScheduledEventWithSecondDateInOneWeekNextMeetingHolidayShouldPushOutSecondDateOneWeek() {

        // setup
        Holiday independenceDay = new HolidayBuilder().from(mon_2011_07_04).to(mon_2011_07_04.plusDays(3))
                                        .withNextMeetingRule().build();
        List<Holiday> upcomingHolidays = Arrays.asList(independenceDay);
        scheduleGeneration = new HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration(workingDays, upcomingHolidays);
        ScheduledEvent scheduleEvent = new ScheduledEventBuilder().every(1).weeks()
                                                                  .on(wed_2011_06_29.getDayOfWeek()).build();

        // exercise test
        List<DateTime> scheduledDates = scheduleGeneration.generateScheduledDates
                                                              (4, wed_2011_06_29.minusDays(1), scheduleEvent);
        // verify results
        assertThat(scheduledDates.size(), is(4));
        assertThat(scheduledDates.get(0), is(wed_2011_06_29));
        assertThat(scheduledDates.get(1), is(wed_2011_06_29.plusWeeks(2)));
        assertThat(scheduledDates.get(2), is(wed_2011_06_29.plusWeeks(2)));
        assertThat(scheduledDates.get(3), is(wed_2011_06_29.plusWeeks(3)));
    }

    @Test
    public void adjustWeeklyScheduledEventWithSecondThirdFourthDateInThreeWeekNextMeetingHolidayShouldQuadrupleUpDatesAfterHoliday() {

        // setup
        DateTime fourthOfJuly = new DateTime()
                                        .plusYears(1)
                                        .withMonthOfYear(7)
                                        .withDayOfMonth(4)
                                        .toDateMidnight()
                                        .toDateTime();
        Holiday independenceDay = new HolidayBuilder()
                                        .from(fourthOfJuly)
                                        .to(fourthOfJuly.plusWeeks(3))
                                        .withNextMeetingRule()
                                        .build();
        List<Holiday> upcomingHolidays = Arrays.asList(independenceDay);

        scheduleGeneration = new HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration(workingDays, upcomingHolidays);

        DateTime june29thNextYear = new DateTime().plusYears(1).withMonthOfYear(6).withDayOfMonth(29).toDateMidnight().toDateTime();
        DateTime startingFrom = june29thNextYear.minusDays(1);
        ScheduledEvent scheduleEvent = new ScheduledEventBuilder().every(1).weeks().on(june29thNextYear.getDayOfWeek()).build();

        // exercise test
        List<DateTime> scheduledDates = scheduleGeneration.generateScheduledDates(5, startingFrom, scheduleEvent);

        assertThat(scheduledDates.size(), is(5));
        assertThat(scheduledDates.get(0), is(june29thNextYear));
        assertThat(scheduledDates.get(1), is(june29thNextYear.plusWeeks(4)));
        assertThat(scheduledDates.get(2), is(june29thNextYear.plusWeeks(4)));
        assertThat(scheduledDates.get(3), is(june29thNextYear.plusWeeks(4)));
        assertThat(scheduledDates.get(3), is(june29thNextYear.plusWeeks(4)));
    }

    @Test
    public void adjusteWeeklyScheduleThreeDayMoratoriumEnclosesSecondScheduledDate() {

        // setup
        DateTime fourthOfJuly = new DateTime().plusYears(1).withMonthOfYear(7).withDayOfMonth(4).toDateMidnight().toDateTime();
        Holiday independenceDay = new HolidayBuilder()
                                         .from(fourthOfJuly)
                                         .to(fourthOfJuly.plusDays(3))
                                         .withRepaymentMoratoriumRule()
                                         .build();
        List<Holiday> upcomingHolidays = Arrays.asList(independenceDay);

        scheduleGeneration = new HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration(workingDays, upcomingHolidays);

        DateTime june29thNextYear = new DateTime().plusYears(1).withMonthOfYear(6).withDayOfMonth(29).toDateMidnight().toDateTime();
        DateTime startingFrom = june29thNextYear.minusDays(1);
        ScheduledEvent scheduleEvent = new ScheduledEventBuilder().every(1).weeks().on(june29thNextYear.getDayOfWeek()).build();

        // exercise test
        List<DateTime> scheduledDates = scheduleGeneration.generateScheduledDates(3, startingFrom,
                scheduleEvent);

        assertThat(scheduledDates.get(0), is(june29thNextYear));
        assertThat(scheduledDates.get(1), is(june29thNextYear.plusWeeks(2)));
        assertThat(scheduledDates.get(2), is(june29thNextYear.plusWeeks(3)));
    }

    @Test
    public void adjustWeeklyScheduleTwoWeekMoratoriumEnclosesSecondAndThirdScheduledDates() {

        // setup
        DateTime fourthOfJuly = new DateTime().plusYears(1).withMonthOfYear(7)
                                              .withDayOfMonth(4).toDateMidnight().toDateTime();
        Holiday independenceDay = new HolidayBuilder()
                                         .from(fourthOfJuly)
                                         .to(fourthOfJuly.plusWeeks(2))
                                         .withRepaymentMoratoriumRule()
                                         .build();
        List<Holiday> upcomingHolidays = Arrays.asList(independenceDay);

        scheduleGeneration = new HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration(workingDays, upcomingHolidays);

        DateTime june29thNextYear = new DateTime().plusYears(1).withMonthOfYear(6)
                                                  .withDayOfMonth(29).toDateMidnight().toDateTime();
        DateTime startingFrom = june29thNextYear.minusDays(1);
        ScheduledEvent scheduleEvent = new ScheduledEventBuilder().every(1)
                                                                  .weeks()
                                                                  .on(june29thNextYear.getDayOfWeek())
                                                                  .build();

        // exercise test
        List<DateTime> scheduledDates = scheduleGeneration.generateScheduledDates(3, startingFrom,
                scheduleEvent);

        assertThat(scheduledDates.get(0), is(june29thNextYear));
        assertThat(scheduledDates.get(1), is(june29thNextYear.plusWeeks(3)));
        assertThat(scheduledDates.get(2), is(june29thNextYear.plusWeeks(4)));
    }

    @Test
    public void adjustWeeklyScheduledEventWithSecondThirdFourthDateInThreeWeekSameDayHolidayExpectNoAdjustment() {

        // setup
        DateTime fourthOfJuly = new DateTime()
                                        .plusYears(1)
                                        .withMonthOfYear(7)
                                        .withDayOfMonth(4)
                                        .toDateMidnight()
                                        .toDateTime();
        Holiday threeWeekSameDay = new HolidayBuilder()
                                        .from(fourthOfJuly)
                                        .to(fourthOfJuly.plusWeeks(3))
                                        .withSameDayAsRule()
                                        .build();
        List<Holiday> upcomingHolidays = Arrays.asList(threeWeekSameDay);

        scheduleGeneration = new HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration(workingDays, upcomingHolidays);

        DateTime june29thNextYear = new DateTime().plusYears(1).withMonthOfYear(6).withDayOfMonth(29).toDateMidnight().toDateTime();
        DateTime startingFrom = june29thNextYear.minusDays(1);
        ScheduledEvent scheduleEvent = new ScheduledEventBuilder().every(1).weeks().on(june29thNextYear.getDayOfWeek()).build();

        // exercise test
        List<DateTime> scheduledDates = scheduleGeneration.generateScheduledDates(5, startingFrom, scheduleEvent);

        assertThat(scheduledDates.size(), is(5));
        assertThat(scheduledDates.get(0), is(june29thNextYear));
        assertThat(scheduledDates.get(1), is(june29thNextYear.plusWeeks(1)));
        assertThat(scheduledDates.get(2), is(june29thNextYear.plusWeeks(2)));
        assertThat(scheduledDates.get(3), is(june29thNextYear.plusWeeks(3)));
        assertThat(scheduledDates.get(4), is(june29thNextYear.plusWeeks(4)));
    }

    @Test
    public void adjustWeeklyScheduledEventWithMoratoriumImmediatelyFollowingNexRepaymentHoliday() {

        // setup. Start first holiday on a Monday near the fourth of July.
        DateTime startNextRepaymentHoliday = new DateTime()
                                        .plusYears(1)
                                        .withMonthOfYear(7)
                                        .withDayOfMonth(1)
                                        .toDateMidnight()
                                        .toDateTime()
                                        .withDayOfWeek(DayOfWeek.monday());
        //First holiday extends for 14 consecutive days
        Holiday twoWeekNextRepaymentHoliday = new HolidayBuilder()
                                                .from(startNextRepaymentHoliday)
                                                .to(startNextRepaymentHoliday.plusWeeks(2).plusDays(-1))
                                                .withNextMeetingRule()
                                                .build();
        // Next moratorium follows for 14 days
        Holiday twoWeekMoratorium = new HolidayBuilder()
                                                .from(startNextRepaymentHoliday.plusWeeks(2))
                                                .to(startNextRepaymentHoliday.plusWeeks(4).plusDays(-1))
                                                .withRepaymentMoratoriumRule()
                                                .build();
        List<Holiday> upcomingHolidays = Arrays.asList(twoWeekNextRepaymentHoliday, twoWeekMoratorium);

        scheduleGeneration = new HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration(workingDays, upcomingHolidays);

        //Start generating the schedule from the Monday the week before the first holiday starts
        DateTime startingFrom = startNextRepaymentHoliday.minusWeeks(1);
        ScheduledEvent scheduledEvent = new ScheduledEventBuilder()
                                                    .every(1).weeks().on(DayOfWeek.wednesday())
                                                    .build();

        // exercise test
        List<DateTime> scheduledDates = scheduleGeneration.generateScheduledDates(6, startingFrom, scheduledEvent);

        /*
         * Schedule should start on the next Wednesday (2 days from the starting point).
         * The second and third dates get shifted into the first week of the moratorium, on the same
         * date as the fourth date. These and the remaining schedule get shifted two mor weeks
         *  past the moratorium.
         */
        DateTime expectedFirstScheduledDate = startingFrom.withDayOfWeek(DayOfWeek.wednesday());
        assertThat(scheduledDates.size(), is(6));
        assertThat(scheduledDates.get(0), is(expectedFirstScheduledDate));
        assertThat(scheduledDates.get(1), is(expectedFirstScheduledDate.plusWeeks(5)));
        assertThat(scheduledDates.get(2), is(expectedFirstScheduledDate.plusWeeks(5)));
        assertThat(scheduledDates.get(3), is(expectedFirstScheduledDate.plusWeeks(5)));
        assertThat(scheduledDates.get(4), is(expectedFirstScheduledDate.plusWeeks(6)));
        assertThat(scheduledDates.get(5), is(expectedFirstScheduledDate.plusWeeks(7)));
    }

    @Test
    public void adjustWeeklyScheduledEventWithMoratoriumImmediatelyFollowingSameDayHoliday() {

        // setup. Start first holiday on a Monday near the fourth of July.
        DateTime startSameDayHoliday = new DateTime()
                                        .plusYears(1)
                                        .withMonthOfYear(7)
                                        .withDayOfMonth(1)
                                        .toDateMidnight()
                                        .toDateTime()
                                        .withDayOfWeek(DayOfWeek.monday());
        //First holiday extends for 14 consecutive days
        Holiday twoWeekSameDayHoliday = new HolidayBuilder()
                                                .from(startSameDayHoliday)
                                                .to(startSameDayHoliday.plusWeeks(2).plusDays(-1))
                                                .withSameDayAsRule()
                                                .build();
        // Next moratorium follows for 14 days
        Holiday twoWeekMoratorium = new HolidayBuilder()
                                                .from(startSameDayHoliday.plusWeeks(2))
                                                .to(startSameDayHoliday.plusWeeks(4).plusDays(-1))
                                                .withRepaymentMoratoriumRule()
                                                .build();
        List<Holiday> upcomingHolidays = Arrays.asList(twoWeekSameDayHoliday, twoWeekMoratorium);

        scheduleGeneration = new HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration(workingDays, upcomingHolidays);

        //Start generating the schedule from the Monday the week before the first holiday starts
        DateTime startingFrom = startSameDayHoliday.minusWeeks(1);
        ScheduledEvent scheduledEvent = new ScheduledEventBuilder()
                                                    .every(1).weeks().on(DayOfWeek.wednesday())
                                                    .build();

        // exercise test
        List<DateTime> scheduledDates = scheduleGeneration.generateScheduledDates(6, startingFrom, scheduledEvent);

        /*
         * Schedule should start on the next Wednesday (2 days from the starting point).
         * The second and third dates do not shift (same day repayment). The remaining schedule get shifted two weeks
         *  past the moratorium.
         */
        DateTime expectedFirstScheduledDate = startingFrom.withDayOfWeek(DayOfWeek.wednesday());
        assertThat(scheduledDates.size(), is(6));
        assertThat(scheduledDates.get(0), is(expectedFirstScheduledDate));
        assertThat(scheduledDates.get(1), is(expectedFirstScheduledDate.plusWeeks(1)));
        assertThat(scheduledDates.get(2), is(expectedFirstScheduledDate.plusWeeks(2)));
        assertThat(scheduledDates.get(3), is(expectedFirstScheduledDate.plusWeeks(5)));
        assertThat(scheduledDates.get(4), is(expectedFirstScheduledDate.plusWeeks(6)));
        assertThat(scheduledDates.get(5), is(expectedFirstScheduledDate.plusWeeks(7)));
    }

    @Test
    public void adjustWeeklyScheduledEventWithMoratoriumImmediatelyFollowingNextWorkingDayHoliday() {

        // setup. Start first holiday on a Monday near the fourth of July.
        DateTime startNextWorkingDayHoliday = new DateTime()
                                        .plusYears(1)
                                        .withMonthOfYear(7)
                                        .withDayOfMonth(1)
                                        .toDateMidnight()
                                        .toDateTime()
                                        .withDayOfWeek(DayOfWeek.monday());
        //First holiday extends for 14 consecutive days
        Holiday twoWeekNextWorkingDayHoliday = new HolidayBuilder()
                                                .from(startNextWorkingDayHoliday)
                                                .to(startNextWorkingDayHoliday.plusWeeks(2).minusDays(1))
                                                .withNextWorkingDayRule()
                                                .build();
        // Next moratorium follows for 14 days
        Holiday twoWeekMoratorium = new HolidayBuilder()
                                                .from(startNextWorkingDayHoliday.plusWeeks(2))
                                                .to(startNextWorkingDayHoliday.plusWeeks(4).minusDays(1))
                                                .withRepaymentMoratoriumRule()
                                                .build();
        List<Holiday> upcomingHolidays = Arrays.asList(twoWeekNextWorkingDayHoliday, twoWeekMoratorium);

        scheduleGeneration = new HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration(workingDays, upcomingHolidays);

        //Start generating the schedule from the Monday the week before the first holiday starts
        DateTime startingFrom = startNextWorkingDayHoliday.minusWeeks(1);
        ScheduledEvent scheduledEvent = new ScheduledEventBuilder()
                                                    .every(1).weeks().on(DayOfWeek.wednesday())
                                                    .build();

        // exercise test
        List<DateTime> scheduledDates = scheduleGeneration.generateScheduledDates(6, startingFrom, scheduledEvent);

        /*
         * Schedule should start on the next Wednesday (2 days from the starting point).
         * The second and third unadjusted dates, being in the next-working-day holiday shift to the
         * first Monday after the holiday and moratorium. Because the fourth unadjusted date is in the moratorium,
         * it and the remaining schedule are shifted two more weeks past the moratorium.
         */
        DateTime expectedFirstScheduledDate = startingFrom.withDayOfWeek(DayOfWeek.wednesday());
        assertThat(scheduledDates.size(), is(6));
        assertThat(scheduledDates.get(0), is(expectedFirstScheduledDate));
        assertThat(scheduledDates.get(1), is(expectedFirstScheduledDate.plusWeeks(5).withDayOfWeek(DayOfWeek.monday())));
        assertThat(scheduledDates.get(2), is(expectedFirstScheduledDate.plusWeeks(5).withDayOfWeek(DayOfWeek.monday())));
        assertThat(scheduledDates.get(3), is(expectedFirstScheduledDate.plusWeeks(5)));
        assertThat(scheduledDates.get(4), is(expectedFirstScheduledDate.plusWeeks(6)));
        assertThat(scheduledDates.get(5), is(expectedFirstScheduledDate.plusWeeks(7)));
    }

    @Test
    public void adjustDatesBiWeeklyScheduleSecondDateHitsThreeDayMoratorium() {

        // setup
        DateTime fourthOfJuly = new DateTime().plusYears(1).withMonthOfYear(7).withDayOfMonth(4).toDateMidnight().toDateTime();
        Holiday independenceDay = new HolidayBuilder()
                                         .from(fourthOfJuly)
                                         .to(fourthOfJuly.plusDays(3))
                                         .withRepaymentMoratoriumRule()
                                         .build();
        List<Holiday> upcomingHolidays = Arrays.asList(independenceDay);

        scheduleGeneration = new HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration(workingDays, upcomingHolidays);

        DateTime startingFrom = fourthOfJuly.minusWeeks(2);
        ScheduledEvent scheduleEvent = new ScheduledEventBuilder().every(2).weeks()
                                                                  .on(startingFrom.getDayOfWeek()).build();

        // exercise test
        List<DateTime> scheduledDates = scheduleGeneration.generateScheduledDates(3, startingFrom,
                scheduleEvent);

        assertThat(scheduledDates.get(0), is(startingFrom));
        assertThat(scheduledDates.get(1), is(startingFrom.plusWeeks(4))); // pushed out two weeks
        assertThat(scheduledDates.get(2), is(startingFrom.plusWeeks(6))); // pushed out two weeks

    }

    @Test
    public void adjustDatesMonthlyByDayScheduleSecondDateHitsThreeDayMoratorium() {

        // setup
        DateTime fourthOfJuly2011 = new DateTime().withYear(2011).withMonthOfYear(7)
                                                  .withDayOfMonth(4).toDateMidnight().toDateTime();
        Holiday independenceDay = new HolidayBuilder()
                                         .from(fourthOfJuly2011)
                                         .to(fourthOfJuly2011.plusDays(3))
                                         .withRepaymentMoratoriumRule()
                                         .build();
        List<Holiday> upcomingHolidays = Arrays.asList(independenceDay);

        scheduleGeneration = new HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration(workingDays, upcomingHolidays);

        DateTime startingFrom = fourthOfJuly2011.minusMonths(1).plusDays(2); //A Monday
        ScheduledEvent scheduleEvent = new ScheduledEventBuilder().every(1).months().onDayOfMonth(6).build();

        // exercise test
        List<DateTime> scheduledDates = scheduleGeneration.generateScheduledDates(3, startingFrom,
                scheduleEvent);

        assertThat(scheduledDates.get(0), is(startingFrom));
        assertThat(scheduledDates.get(1), is(startingFrom.plusMonths(2))); // pushed out one month
        assertThat(scheduledDates.get(2), is(startingFrom.plusMonths(3))); // pushed out one month

    }
}
