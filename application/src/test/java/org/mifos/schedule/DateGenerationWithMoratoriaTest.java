package org.mifos.schedule;

import static org.hamcrest.CoreMatchers.is;
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
import org.mifos.schedule.internal.MoratoriumExampleByKeithScheduledDateGeneration;

public class DateGenerationWithMoratoriaTest {

    private ScheduledDateGeneration scheduledDateGeneration;

    private List<Days> workingDays = new ArrayList<Days>();
    private List<Holiday> holidays = new ArrayList<Holiday>();
    private List<Holiday> moratoria = new ArrayList<Holiday>();

    private DateTime startSchedulesFrom = null;
    private DateTime firstExpectedScheduleDate = null;
    private ScheduledEvent scheduledEvent;

    @Before
    public void setupAndInjectDependencies() {

        workingDays = Arrays.asList(DayOfWeek.mondayAsDay(), DayOfWeek.tuesdayAsDay(), DayOfWeek
                .wednesdayAsDay(), DayOfWeek.thursdayAsDay(), DayOfWeek.fridayAsDay());
        holidays = new ArrayList<Holiday>();
        moratoria = new ArrayList<Holiday>();

        startSchedulesFrom = new DateTime().withYear(2009).withMonthOfYear(12).withDayOfMonth(7).toDateMidnight().toDateTime();
        firstExpectedScheduleDate = startSchedulesFrom.plusDays(2);
        scheduledEvent = new ScheduledEventBuilder().every(1).weeks().on(firstExpectedScheduleDate.getDayOfWeek()).build();
    }

    @Test
    public void shouldGenerateDatesAWeekApartOccurringOnSameDayOfWeek() {

        // setup
        scheduledDateGeneration = new MoratoriumExampleByKeithScheduledDateGeneration(workingDays, holidays, moratoria);

        // exercise test
        List<DateTime> scheduledDateResults = scheduledDateGeneration.generateScheduledDates(6, startSchedulesFrom, scheduledEvent);

        // verification
        assertThat(scheduledDateResults.get(0), is(firstExpectedScheduleDate));
        assertThat(scheduledDateResults.get(1), is(firstExpectedScheduleDate.plusDays(7)));
        assertThat(scheduledDateResults.get(2), is(firstExpectedScheduleDate.plusDays(14)));
        assertThat(scheduledDateResults.get(3), is(firstExpectedScheduleDate.plusDays(21)));
        assertThat(scheduledDateResults.get(4), is(firstExpectedScheduleDate.plusDays(28)));
        assertThat(scheduledDateResults.get(5), is(firstExpectedScheduleDate.plusDays(35)));
    }

    @Test
    public void shouldPushOutScheduledDateWhenFallingOnAMoratorium() {

        // setup
        Holiday twoWeekMoratorium = new HolidayBuilder().from(firstExpectedScheduleDate.plusDays(13)).to(firstExpectedScheduleDate.plusDays(27)).withRepaymentMoratoriumRule().build();
        moratoria.add(twoWeekMoratorium);

        scheduledDateGeneration = new MoratoriumExampleByKeithScheduledDateGeneration(workingDays, holidays, moratoria);

        // exercise test
        List<DateTime> scheduledDateResults = scheduledDateGeneration.generateScheduledDates(6, startSchedulesFrom, scheduledEvent);

        // verification
        assertThat(scheduledDateResults.get(0), is(firstExpectedScheduleDate));
        assertThat(scheduledDateResults.get(1), is(firstExpectedScheduleDate.plusDays(7)));
        assertThat(scheduledDateResults.get(2), is(firstExpectedScheduleDate.plusDays(28)));
        assertThat(scheduledDateResults.get(3), is(firstExpectedScheduleDate.plusDays(35)));
        assertThat(scheduledDateResults.get(4), is(firstExpectedScheduleDate.plusDays(42)));
        assertThat(scheduledDateResults.get(5), is(firstExpectedScheduleDate.plusDays(49)));
    }

    @Test
    public void shouldPushOutScheduledDateWhenFallingOnAMoratoriumAndAHoliday() {

        // setup
        Holiday twoWeekMoratorium = new HolidayBuilder().from(firstExpectedScheduleDate.plusDays(13)).to(firstExpectedScheduleDate.plusDays(27)).withRepaymentMoratoriumRule().build();
        moratoria.add(twoWeekMoratorium);

        Holiday threeDayHolidayNotCausingOverlapWithMoratorium = new HolidayBuilder().from(firstExpectedScheduleDate.plusDays(5)).to(firstExpectedScheduleDate.plusDays(8)).withNextWorkingDayRule().build();
        holidays.add(threeDayHolidayNotCausingOverlapWithMoratorium);

        scheduledDateGeneration = new MoratoriumExampleByKeithScheduledDateGeneration(workingDays, holidays, moratoria);

        // exercise test
        List<DateTime> scheduledDateResults = scheduledDateGeneration.generateScheduledDates(6, startSchedulesFrom, scheduledEvent);

        // verification
        assertThat(scheduledDateResults.get(0), is(firstExpectedScheduleDate));
        assertThat(scheduledDateResults.get(1), is(firstExpectedScheduleDate.plusDays(9))); // next working day after holiday
        assertThat(scheduledDateResults.get(2), is(firstExpectedScheduleDate.plusDays(28)));
        assertThat(scheduledDateResults.get(3), is(firstExpectedScheduleDate.plusDays(35)));
        assertThat(scheduledDateResults.get(4), is(firstExpectedScheduleDate.plusDays(42)));
        assertThat(scheduledDateResults.get(5), is(firstExpectedScheduleDate.plusDays(49)));
    }

    @Test
    public void shouldPushOutScheduledDateWhenFallingOnAHolidayThatForcesScheduledDateIntoAMoratoriumPeriod() {

        // setup
        Holiday twoWeekMoratorium = new HolidayBuilder().from(firstExpectedScheduleDate.plusDays(13)).to(firstExpectedScheduleDate.plusDays(27)).withRepaymentMoratoriumRule().build();
        moratoria.add(twoWeekMoratorium);

        Holiday threeDayHolidayCausingOverlapWithMoratorium = new HolidayBuilder().from(firstExpectedScheduleDate.plusDays(5)).to(firstExpectedScheduleDate.plusDays(8)).withNextMeetingRule().build();
        holidays.add(threeDayHolidayCausingOverlapWithMoratorium);

        scheduledDateGeneration = new MoratoriumExampleByKeithScheduledDateGeneration(workingDays, holidays, moratoria);

        // exercise test
        List<DateTime> scheduledDateResults = scheduledDateGeneration.generateScheduledDates(6, startSchedulesFrom, scheduledEvent);

        // verification
        assertThat(scheduledDateResults.get(0), is(firstExpectedScheduleDate));
        assertThat(scheduledDateResults.get(1), is(firstExpectedScheduleDate.plusDays(28))); // next meeting that overlaps with moratorium
        assertThat(scheduledDateResults.get(2), is(firstExpectedScheduleDate.plusDays(28)));
        assertThat(scheduledDateResults.get(3), is(firstExpectedScheduleDate.plusDays(35)));
        assertThat(scheduledDateResults.get(4), is(firstExpectedScheduleDate.plusDays(42)));
        assertThat(scheduledDateResults.get(5), is(firstExpectedScheduleDate.plusDays(49)));
    }
}