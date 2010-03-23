package org.mifos.calendar;

import static org.mockito.Mockito.verify;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.schedule.ScheduledEvent;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NextScheduledEventStrategyTest {

    private NextScheduledEventStrategy scheduledEventStrategy;

    @Mock
    private ScheduledEvent scheduledEvent;

    @Before
    public void setupAndInjectDependencies() {

        scheduledEventStrategy = new NextScheduledEventStrategy(scheduledEvent);
    }

    @Test
    public void whenAdjustingDateUseScheduledEventImplementationOfNextEventDate() {

        DateTime firstOfNextMonth = new DateTime().plusMonths(1).withDayOfMonth(1).toDateMidnight().toDateTime();
        DateTime firstTuesdayOfNextMonth = firstOfNextMonth.withDayOfWeek(DayOfWeek.tuesday());

        // exercise test
        scheduledEventStrategy.adjust(firstTuesdayOfNextMonth);

        verify(scheduledEvent).nextEventDateAfter(firstTuesdayOfNextMonth);
    }
}
