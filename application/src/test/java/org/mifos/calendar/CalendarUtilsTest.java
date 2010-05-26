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

package org.mifos.calendar;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.joda.time.DateTime;
import org.junit.Test;

public class CalendarUtilsTest {

    @Test
    public void shouldRollDateForwardByOneDay() {

        DateTime firstOfNextMonth = new DateTime().plusMonths(1).withDayOfMonth(1).toDateMidnight().toDateTime();

        // exercise test
        DateTime adjustedDate = CalendarUtils.getNextDateForDay(firstOfNextMonth, 1);

        assertThat(adjustedDate, is(firstOfNextMonth.plusDays(1)));
    }

    @Test
    public void shouldRollDateForwardByTwoDay() {

        DateTime firstOfNextMonth = new DateTime().plusMonths(1).withDayOfMonth(1).toDateMidnight().toDateTime();

        // exercise test
        DateTime adjustedDate = CalendarUtils.getNextDateForDay(firstOfNextMonth, 2);

        assertThat(adjustedDate, is(firstOfNextMonth.plusDays(2)));
    }

    @Test
    public void shouldRollDateForwardToNearestDayOfWeek() {

        DateTime firstOfNextMonth = new DateTime().plusMonths(1).withDayOfMonth(1).toDateMidnight().toDateTime();
        DateTime firstTuesdayOfNextMonth = firstOfNextMonth.withDayOfWeek(DayOfWeek.tuesday());

        // exercise test
        DateTime adjustedDate = CalendarUtils.getFirstDateForWeek(firstTuesdayOfNextMonth, DayOfWeek.wednesday());

        assertThat(adjustedDate, is(firstTuesdayOfNextMonth.plusDays(1)));
    }

    @Test
    public void shouldRollDateForwardToNearestDayOfWeek2() {

        DateTime firstOfNextMonth = new DateTime().plusMonths(1).withDayOfMonth(1).toDateMidnight().toDateTime();
        DateTime firstThursdayOfNextMonth = firstOfNextMonth.withDayOfWeek(DayOfWeek.thursday());

        // exercise test
        DateTime adjustedDate = CalendarUtils.getFirstDateForWeek(firstThursdayOfNextMonth, DayOfWeek.wednesday());

        assertThat(adjustedDate, is(firstThursdayOfNextMonth.plusDays(6)));
    }

    @Test
    public void shouldNotRollDateForwardWhenItMatchesDayOfWeek() {

        DateTime firstOfNextMonth = new DateTime().plusMonths(1).withDayOfMonth(1).toDateMidnight().toDateTime();
        DateTime firstThursdayOfNextMonth = firstOfNextMonth.withDayOfWeek(DayOfWeek.thursday());

        // exercise test
        DateTime adjustedDate = CalendarUtils.getFirstDateForWeek(firstThursdayOfNextMonth, DayOfWeek.thursday());

        assertThat(adjustedDate, is(firstThursdayOfNextMonth));
    }

    @Test
    public void shouldRollDateForwardToNearestMonthAndDayOfMonth() {

        DateTime firstOfNextMonth = new DateTime().plusMonths(1).withDayOfMonth(1).toDateMidnight().toDateTime();

        // exercise test
        DateTime adjustedDate = CalendarUtils.getFirstDateForMonthOnDate(firstOfNextMonth, 15);

        assertThat(adjustedDate, is(firstOfNextMonth.plusDays(14)));
    }

    @Test
    public void shouldRollDateForwardToNearestMonthAndDayOfMonthWhenDayOfMonthHasBeenPassed() {

        DateTime twentiethOfNextMonth = new DateTime().plusMonths(1).withDayOfMonth(20).toDateMidnight().toDateTime();

        // exercise test
        DateTime adjustedDate = CalendarUtils.getFirstDateForMonthOnDate(twentiethOfNextMonth, 15);

        assertThat(adjustedDate, is(twentiethOfNextMonth.plusMonths(1).withDayOfMonth(15)));
    }


    @Test
    public void shouldNotRollForwardDateIfStartingDateMatchesDayOfMonth() {

        DateTime secondLastDayOfJune = new DateTime().withMonthOfYear(6).withDayOfMonth(29).toDateMidnight()
                .toDateTime();

        // exercise test
        DateTime adjustedDate = CalendarUtils.getFirstDateForMonthOnDate(secondLastDayOfJune, 29);

        assertThat(adjustedDate, is(secondLastDayOfJune));
    }

    @Test
    public void shouldRollDateForwardToNearestMonthGivenWeekAndWeekDay() {

        DateTime thursdayFirstOfApril2010 = new DateTime().withDayOfMonth(1).withMonthOfYear(4).withYear(2010).toDateMidnight().toDateTime();

        // exercise test
        DateTime adjustedDate = CalendarUtils.getFirstDayForMonthUsingWeekRankAndWeekday(thursdayFirstOfApril2010, 1, DayOfWeek.friday());

        assertThat(adjustedDate, is(thursdayFirstOfApril2010.plusDays(1)));
    }

    @Test
    public void shouldRollDateForwardToNearestMonthGivenWeekAndWeekDayHaveBeenPassedInCurrentMonth() {

        DateTime firstOfMarch2010 = new DateTime().withYear(2010).withMonthOfYear(3).withDayOfMonth(1).toDateMidnight().toDateTime();
        DateTime startingFromfirstThursdayOfNextMonth = firstOfMarch2010.withDayOfWeek(DayOfWeek.thursday());

        // exercise test
        DateTime adjustedDate = CalendarUtils.getFirstDayForMonthUsingWeekRankAndWeekday(startingFromfirstThursdayOfNextMonth, 1, DayOfWeek.wednesday());

        DateTime expectedAdjustedDate = new DateTime().withYear(2010).withMonthOfYear(4).withDayOfMonth(7).toDateMidnight().toDateTime();
        assertThat(adjustedDate, is(expectedAdjustedDate));
    }

    @Test
    public void shouldRollDateForwardToNearestMonthGivenWeekAndWeekDayForMonthWithFiveWeekdaysInMonth() {

        DateTime monday29thOfMarch2010 = new DateTime().withMonthOfYear(3).withDayOfMonth(29).withYear(2010).toDateMidnight().toDateTime();

        // exercise test
        DateTime adjustedDate = CalendarUtils.getFirstDayForMonthUsingWeekRankAndWeekday(monday29thOfMarch2010, 5, DayOfWeek.tuesday());

        assertThat(adjustedDate, is(monday29thOfMarch2010.plusDays(1)));
    }

    @Test
    public void shouldRollDateForwardToNextMonthAndDayOfMonthThatMatches() {

        DateTime firstOfNextMonth = new DateTime().plusMonths(1).withDayOfMonth(1).toDateMidnight().toDateTime();

        // exercise test
        DateTime adjustedDate = CalendarUtils.getNextDateForMonthOnDate(firstOfNextMonth, 1, 1);

        assertThat(adjustedDate, is(firstOfNextMonth.plusMonths(1)));
    }

    @Test
    public void shouldRollDateForwardToNextMonthAndDayOfMonthThatMatchesGivenMonthDoesNotHaveThatDayNumber() {

        DateTime lastDayInAugust = new DateTime().withMonthOfYear(8).withDayOfMonth(31).toDateMidnight().toDateTime();

        // exercise test
        DateTime adjustedDate = CalendarUtils.getNextDateForMonthOnDate(lastDayInAugust, 31, 1);

        assertThat(adjustedDate, is(lastDayInAugust.plusMonths(1)));
        assertThat(adjustedDate.getDayOfMonth(), is(30));
    }

    @Test
    public void shouldRollDateForwardToNextDateThatMatchesGivenWeekAndWeekDayHaveBeenPassedInCurrentMonth() {

        DateTime wednesday7thOfApril2010 = new DateTime().withDayOfMonth(7).withMonthOfYear(4).withYear(2010).toDateMidnight().toDateTime();
        DateTime thursday = wednesday7thOfApril2010.plusDays(1);

        // exercise test
        DateTime adjustedDate = CalendarUtils.getNextDayForMonthUsingWeekRankAndWeekday(thursday, 1, DayOfWeek.wednesday(), 1);

        DateTime wednesday5thOfMay2010 = new DateTime().withDayOfMonth(5).withMonthOfYear(5).withYear(2010).toDateMidnight().toDateTime();
        assertThat(adjustedDate, is(wednesday5thOfMay2010));
    }

    @Test
    public void shouldRollDateForwardToNextDateThatMatchesGivenWeekAndWeekDayForMonthWithFiveWeekdaysInMonth() {

        DateTime monday29thOfMarch2010 = new DateTime().withMonthOfYear(3).withDayOfMonth(29).withYear(2010).toDateMidnight().toDateTime();

        // exercise test
        DateTime adjustedDate = CalendarUtils.getFirstDayForMonthUsingWeekRankAndWeekday(monday29thOfMarch2010, 5, DayOfWeek.tuesday());

        assertThat(adjustedDate, is(monday29thOfMarch2010.plusDays(1)));
    }

    private DateTime firstWednesdayOfTwoMonthsAway() {
        DateTime firstOfTwoMonthsAway = new DateTime().plusMonths(2).withDayOfMonth(1).toDateMidnight().toDateTime();
        DateTime firstWednesdayOfTwoMonthsAway = firstOfTwoMonthsAway.withDayOfWeek(DayOfWeek.wednesday());

        if (firstWednesdayOfTwoMonthsAway.getMonthOfYear() != firstOfTwoMonthsAway.getMonthOfYear()) {
            return firstWednesdayOfTwoMonthsAway.plusWeeks(1);
        }

        return firstWednesdayOfTwoMonthsAway;
    }
}
