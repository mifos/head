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

package org.mifos.schedule;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

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
import org.mifos.schedule.internal.HolidayAndWorkingDaysScheduledDateGeneration;

public class HolidayAndWorkingDaysScheduleGenerationStrategyTest {

    private ScheduledDateGeneration scheduleGeneration;

    @Before
    public void setupAndInjectDependencies() {

        List<Days> workingDays = Arrays.asList(DayOfWeek.mondayAsDay(), DayOfWeek.tuesdayAsDay(), DayOfWeek
                .wednesdayAsDay(), DayOfWeek.thursdayAsDay(), DayOfWeek.fridayAsDay());

        DateTime fourthOfJuly = new DateTime().withMonthOfYear(7).withDayOfMonth(4);
        Holiday independenceDay = new HolidayBuilder().from(fourthOfJuly).to(fourthOfJuly).build();
        List<Holiday> upcomingHolidays = Arrays.asList(independenceDay);

        scheduleGeneration = new HolidayAndWorkingDaysScheduledDateGeneration(workingDays, upcomingHolidays);
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
    public void canGenerateScheduledDatesThatMatchRecurringEventBasedOnLastScheduledDate() {

        DateTime lastScheduledDate = DayOfWeek.mondayMidnight();

        ScheduledEvent recurringEvent = new ScheduledEventBuilder().every(1).weeks().on(DayOfWeek.monday()).build();

        List<DateTime> scheduledDates = scheduleGeneration
                .generateScheduledDates(10, lastScheduledDate, recurringEvent);

        assertThat(scheduledDates.get(0), is(lastScheduledDate));
        assertThat(scheduledDates.get(1), is(DayOfWeek.oneWeekFrom(lastScheduledDate)));
    }

    @Test
    public void canGenerateAllScheduledDatesThatMatchRecurringEvent() {

        DateTime lastScheduledDate = DayOfWeek.mondayMidnight();
        DateTime dayAfterLastScheduledDate = DayOfWeek.tuesdayMidnight();

        ScheduledEvent recurringEvent = new ScheduledEventBuilder().every(1).weeks().on(DayOfWeek.monday()).build();

        List<DateTime> scheduledDates = scheduleGeneration.generateScheduledDates(10, dayAfterLastScheduledDate,
                recurringEvent);

        DateTime lastDate = lastScheduledDate;
        for (DateTime generatedDate : scheduledDates) {
            assertThat(generatedDate, is(DayOfWeek.oneWeekFrom(lastDate)));
            lastDate = generatedDate;
        }
    }
}
