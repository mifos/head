package org.mifos.schedule;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mifos.calendar.CalendarUtils;
import org.mifos.calendar.DayOfWeek;
import org.mifos.domain.builders.ScheduledEventBuilder;

public class MonthlyOnWeekAndWeekDayScheduledEventTest {

    private ScheduledEvent scheduledEvent;

    @Test
    public void canRollForwardDateToMatchWeekOfMonthAndDayOfWeekWhenStartingInSameWeekButOneDayBehind() {

        scheduledEvent = new ScheduledEventBuilder().every(1).months().onWeekOfMonth(1).on(DayOfWeek.tuesday()).build();

        DateTime firstOfNextMonth = new DateTime().plusMonths(1).withDayOfMonth(1).toDateMidnight().toDateTime();
        DateTime firstMondayOfNextMonth = CalendarUtils.nearestDayOfWeekTo(DayOfWeek.monday(), firstOfNextMonth);

        DateTime result = scheduledEvent.nearestMatchingDateBeginningAt(firstMondayOfNextMonth);

        assertThat(result, is(firstMondayOfNextMonth.plusDays(1)));
    }

    @Test
    public void canRollForwardDateToMatchWeekOfMonthAndDayOfWeekWhenStartingOneWeekAndDayBehind() {

        scheduledEvent = new ScheduledEventBuilder().every(1).months().onWeekOfMonth(2).on(DayOfWeek.tuesday()).build();

        DateTime firstOfNextMonth = new DateTime().plusMonths(1).withDayOfMonth(1).toDateMidnight().toDateTime();
        DateTime firstMondayOfNextMonth = CalendarUtils.nearestDayOfWeekTo(DayOfWeek.monday(), firstOfNextMonth);

        DateTime result = scheduledEvent.nearestMatchingDateBeginningAt(firstMondayOfNextMonth);

        assertThat(result, is(firstMondayOfNextMonth.plusDays(1).plusWeeks(1)));
    }

    @Test
    public void canRollForwardDateToMatchWeekOfMonthAndDayOfWeekWhenStartingOneWeekAndDayAhead() {

        scheduledEvent = new ScheduledEventBuilder().every(1).months().onWeekOfMonth(1).on(DayOfWeek.tuesday()).build();

        DateTime ninthOfNextMonth = new DateTime().plusMonths(1).withDayOfMonth(9).withDayOfWeek(DayOfWeek.wednesday())
                .toDateMidnight().toDateTime();

        DateTime result = scheduledEvent.nearestMatchingDateBeginningAt(ninthOfNextMonth);

        assertThat(result, is(tuesdayOnFirstWeekOneMonthFrom(ninthOfNextMonth)));
    }

    private DateTime tuesdayOnFirstWeekOneMonthFrom(final DateTime from) {
        return from.plusMonths(1).withDayOfMonth(7).withDayOfWeek(DayOfWeek.tuesday());
    }
}