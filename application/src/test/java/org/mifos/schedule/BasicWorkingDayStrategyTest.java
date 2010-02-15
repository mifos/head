package org.mifos.schedule;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Before;
import org.junit.Test;
import org.mifos.calendar.BasicWorkingDayStrategy;
import org.mifos.calendar.DayOfWeek;

public class BasicWorkingDayStrategyTest {

    private BasicWorkingDayStrategy workingDayStrategy;

    @Before
    public void setupAndInjectDependencies() {
        List<Days> workingDays = Arrays.asList(DayOfWeek.mondayAsDay(), DayOfWeek.tuesdayAsDay(), DayOfWeek
                .wednesdayAsDay());
        workingDayStrategy = new BasicWorkingDayStrategy(workingDays);
    }

    @Test
    public void shouldRollForwardDateToNextWorkingDay() {

        DateTime sunday = new DateTime().withDayOfWeek(DayOfWeek.sunday());

        DateTime nextWorkingDay = workingDayStrategy.adjust(sunday);

        assertThat(nextWorkingDay.getDayOfWeek(), is(DayOfWeek.monday()));
    }

    @Test
    public void shouldNotRollForwardDateToNextWorkingDayWhenAlreadyOnAWorkingDay() {

        DateTime monday = new DateTime().withDayOfWeek(DayOfWeek.monday());

        DateTime nextWorkingDay = workingDayStrategy.adjust(monday);

        assertThat(nextWorkingDay.getDayOfWeek(), is(DayOfWeek.monday()));
    }
}
