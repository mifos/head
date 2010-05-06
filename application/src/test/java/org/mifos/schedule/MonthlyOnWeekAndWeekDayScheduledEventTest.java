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
import static org.junit.Assert.assertThat;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mifos.calendar.DayOfWeek;
import org.mifos.domain.builders.ScheduledEventBuilder;

public class MonthlyOnWeekAndWeekDayScheduledEventTest {

    private ScheduledEvent scheduledEvent;

    @Test
    public void canRollForwardDateToMatchWeekOfMonthAndDayOfWeekWhenStartingInSameWeekButOneDayBehind() {

        scheduledEvent = new ScheduledEventBuilder().every(1).months().onWeekOfMonth(1).on(DayOfWeek.tuesday()).build();

        DateTime firstMondayOfMay2010 = new DateTime().withYear(2010).withMonthOfYear(5).withDayOfMonth(3).toDateMidnight().toDateTime();

        DateTime result = scheduledEvent.nearestMatchingDateBeginningAt(firstMondayOfMay2010);

        assertThat(result, is(firstMondayOfMay2010.plusDays(1)));
    }

    @Test
    public void canRollForwardDateToMatchWeekOfMonthAndDayOfWeekWhenStartingOneWeekAndDayBehind() {

        scheduledEvent = new ScheduledEventBuilder().every(1).months().onWeekOfMonth(2).on(DayOfWeek.tuesday()).build();

        DateTime firstMondayOfMay2010 = new DateTime().withYear(2010).withMonthOfYear(5).withDayOfMonth(3).toDateMidnight().toDateTime();

        DateTime result = scheduledEvent.nearestMatchingDateBeginningAt(firstMondayOfMay2010);

        assertThat(result, is(firstMondayOfMay2010.plusDays(1).plusWeeks(1)));
    }

    @Test
    public void canRollForwardDateToMatchWeekOfMonthAndDayOfWeekWhenStartingOneWeekAndDayAhead() {

        scheduledEvent = new ScheduledEventBuilder().every(1).months().onWeekOfMonth(1).on(DayOfWeek.tuesday()).build();

        DateTime wednesday7thOfApril = new DateTime().withDayOfMonth(7).withMonthOfYear(4).withYear(2010).withDayOfWeek(DayOfWeek.wednesday())
                .toDateMidnight().toDateTime();

        DateTime result = scheduledEvent.nearestMatchingDateBeginningAt(wednesday7thOfApril);

        assertThat(result, is(tuesdayOnFirstWeekOneMonthFrom(wednesday7thOfApril)));
    }

    private DateTime tuesdayOnFirstWeekOneMonthFrom(final DateTime from) {
        return from.plusMonths(1).withDayOfMonth(7).withDayOfWeek(DayOfWeek.tuesday());
    }
}
