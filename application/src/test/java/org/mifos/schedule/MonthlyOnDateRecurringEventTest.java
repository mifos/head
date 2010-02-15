package org.mifos.schedule;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mifos.domain.builders.ScheduledEventBuilder;

public class MonthlyOnDateRecurringEventTest {

    private ScheduledEvent recurringEvent;

    @Test
    public void canRollForwardDateToMatchDayOfMonthWhenCurrentDayOfMonthIsBehind() {

        recurringEvent = new ScheduledEventBuilder().every(1).months().onDayOfMonth(3).build();
        DateTime firstOfNextMonth = new DateTime().plusMonths(1).withDayOfMonth(1).toDateMidnight().toDateTime();

        DateTime result = recurringEvent.nearestMatchingDateBeginningAt(firstOfNextMonth);

        assertThat(result, is(firstOfNextMonth.plusDays(2)));
    }

    @Test
    public void canRollForwardDateToNextMonthToMatchDayOfMonthWhenDayOfMonthHasBeingPast() {

        recurringEvent = new ScheduledEventBuilder().every(1).months().onDayOfMonth(3).build();
        DateTime fifthDayOfMonth = new DateTime().plusMonths(1).withDayOfMonth(5).toDateMidnight().toDateTime();

        DateTime result = recurringEvent.nearestMatchingDateBeginningAt(fifthDayOfMonth);

        assertThat(result, is(fifthDayOfMonth.plusMonths(1).minusDays(2)));
    }

    @Test
    public void canRollForwardDateToLastDayOfMonthWhenDayOfMonthExceedsNumberOfDaysInActualMonth() {

        recurringEvent = new ScheduledEventBuilder().every(1).months().onDayOfMonth(31).build();
        DateTime secondLastDayOfJune = new DateTime().withMonthOfYear(6).withDayOfMonth(29).toDateMidnight()
                .toDateTime();

        DateTime result = recurringEvent.nearestMatchingDateBeginningAt(secondLastDayOfJune);

        assertThat(result, is(secondLastDayOfJune.plusDays(1)));
    }

    @Test
    public void willNotRollForwardDateIfStartingDateMatchesScheduledEventFrequency() {

        recurringEvent = new ScheduledEventBuilder().every(1).months().onDayOfMonth(29).build();
        DateTime secondLastDayOfJune = new DateTime().withMonthOfYear(6).withDayOfMonth(29).toDateMidnight()
                .toDateTime();

        DateTime result = recurringEvent.nearestMatchingDateBeginningAt(secondLastDayOfJune);

        assertThat(result, is(secondLastDayOfJune));
    }
}