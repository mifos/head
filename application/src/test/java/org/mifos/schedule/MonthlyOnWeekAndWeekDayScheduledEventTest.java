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