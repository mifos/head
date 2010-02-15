package org.mifos.schedule;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mifos.calendar.DayOfWeek;
import org.mifos.domain.builders.ScheduledEventBuilder;

public class WeeklyRecurringEventTest {

    private ScheduledEvent recurringEvent;

    @Test
    public void canRollForwardDateToMatchDayOfWeekStartingFromTwoDaysBehind() {

        recurringEvent = new ScheduledEventBuilder().every(1).weeks().on(DayOfWeek.wednesday()).build();
        DateTime monday = DayOfWeek.mondayMidnight();

        DateTime result = recurringEvent.nearestMatchingDateBeginningAt(monday);

        assertThat(result, is(monday.plusDays(2)));
    }

    @Test
    public void canRollForwardDateToMatchDayOfWeekStartingFromOneDayBehind() {

        recurringEvent = new ScheduledEventBuilder().every(1).weeks().on(DayOfWeek.wednesday()).build();
        DateTime tuesday = DayOfWeek.tuesdayMidnight();

        DateTime result = recurringEvent.nearestMatchingDateBeginningAt(tuesday);

        assertThat(result, is(tuesday.plusDays(1)));
    }

    @Test
    public void canRollForwardDateToMatchDayOfWeekStartingFromSameDayOfWeek() {

        recurringEvent = new ScheduledEventBuilder().every(1).weeks().on(DayOfWeek.wednesday()).build();
        DateTime wednesday = DayOfWeek.wednesdayMidnight();

        DateTime result = recurringEvent.nearestMatchingDateBeginningAt(wednesday);

        assertThat(result, is(wednesday));
    }

    @Test
    public void canRollForwardDateToMatchDayOfWeekStartingFromOneDayAhead() {

        recurringEvent = new ScheduledEventBuilder().every(1).weeks().on(DayOfWeek.wednesday()).build();
        DateTime thursday = DayOfWeek.thursdayMidnight();

        DateTime result = recurringEvent.nearestMatchingDateBeginningAt(thursday);

        assertThat(result, is(thursday.plusDays(6)));
    }

    @Test
    public void canRollForwardDateToMatchDayOfWeekStartingFromTwoDayAhead() {

        recurringEvent = new ScheduledEventBuilder().every(1).weeks().on(DayOfWeek.wednesday()).build();
        DateTime friday = DayOfWeek.fridayMidnight();

        DateTime result = recurringEvent.nearestMatchingDateBeginningAt(friday);

        assertThat(result, is(friday.plusDays(5)));
    }
}