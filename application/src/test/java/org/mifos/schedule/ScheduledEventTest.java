package org.mifos.schedule;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.mifos.domain.builders.ScheduledEventBuilder;

public class ScheduledEventTest {

    @Test(expected=IllegalArgumentException.class)
    public void numberOfEventsRollingUpToThisShouldThrowExceptionWhenInstallmentIsZero() {
        ScheduledEvent master = new ScheduledEventBuilder().every(1).weeks().build();
        ScheduledEvent slave = new ScheduledEventBuilder().every(1).weeks().build();
        master.numberOfEventsRollingUpToThis(slave, 0);
    }

    @Test
    public void numberOfEventsRollingUpToThisMasterEveryOneSlaveEveryTwo() {
        ScheduledEvent master = new ScheduledEventBuilder().every(1).weeks().build();
        ScheduledEvent slave = new ScheduledEventBuilder().every(2).weeks().build();
        assertThat(master.numberOfEventsRollingUpToThis(slave, 1), is(1));
        assertThat(master.numberOfEventsRollingUpToThis(slave, 2), is(0));
        assertThat(master.numberOfEventsRollingUpToThis(slave, 3), is(1));
        assertThat(master.numberOfEventsRollingUpToThis(slave, 4), is(0));
    }

    @Test
    public void numberOfEventsRollingUpToThisMasterEveryTwoSlaveEveryOne() {
        ScheduledEvent master = new ScheduledEventBuilder().every(2).weeks().build();
        ScheduledEvent slave = new ScheduledEventBuilder().every(1).weeks().build();
        assertThat(master.numberOfEventsRollingUpToThis(slave, 1), is(1));
        assertThat(master.numberOfEventsRollingUpToThis(slave, 2), is(2));
        assertThat(master.numberOfEventsRollingUpToThis(slave, 3), is(2));
        assertThat(master.numberOfEventsRollingUpToThis(slave, 4), is(2));
    }

    @Test
    public void numberOfEventsRollingUpToThisMasterEveryTwoSlaveEveryOneWithStart() {
        ScheduledEvent master = new ScheduledEventBuilder().every(2).weeks().build();
        ScheduledEvent slave = new ScheduledEventBuilder().every(1).weeks().build();
        assertThat(master.numberOfDependentOccurrencesRollingUpToThisOccurrenceStartingWith(slave, 4, 4), is(1));
        assertThat(master.numberOfDependentOccurrencesRollingUpToThisOccurrenceStartingWith(slave, 5, 4), is(2));
        assertThat(master.numberOfDependentOccurrencesRollingUpToThisOccurrenceStartingWith(slave, 6, 4), is(2));
        assertThat(master.numberOfDependentOccurrencesRollingUpToThisOccurrenceStartingWith(slave, 7, 4), is(2));
    }

    @Test
    public void numberOfEventsRollingUpToThisMasterEveryTwoSlaveEveryThree() {
        ScheduledEvent master = new ScheduledEventBuilder().every(2).weeks().build();
        ScheduledEvent slave = new ScheduledEventBuilder().every(3).weeks().build();
        assertThat(master.numberOfEventsRollingUpToThis(slave, 1), is(1));
        assertThat(master.numberOfEventsRollingUpToThis(slave, 2), is(0));
        assertThat(master.numberOfEventsRollingUpToThis(slave, 3), is(1));
        assertThat(master.numberOfEventsRollingUpToThis(slave, 4), is(1));
        assertThat(master.numberOfEventsRollingUpToThis(slave, 5), is(0));
        assertThat(master.numberOfEventsRollingUpToThis(slave, 6), is(1));
        assertThat(master.numberOfEventsRollingUpToThis(slave, 7), is(1));
        assertThat(master.numberOfEventsRollingUpToThis(slave, 8), is(0));
    }

    @Test
    public void numberOfEventsRollingUpToThisMasterEveryTwoSlaveEveryThreeStartingWithFourthOccurrence() {
        ScheduledEvent master = new ScheduledEventBuilder().every(2).weeks().build();
        ScheduledEvent slave = new ScheduledEventBuilder().every(3).weeks().build();
        assertThat(master.numberOfDependentOccurrencesRollingUpToThisOccurrenceStartingWith(slave, 4, 4), is(1));
        assertThat(master.numberOfDependentOccurrencesRollingUpToThisOccurrenceStartingWith(slave, 5, 4), is(0));
        assertThat(master.numberOfDependentOccurrencesRollingUpToThisOccurrenceStartingWith(slave, 6, 4), is(1));
        assertThat(master.numberOfDependentOccurrencesRollingUpToThisOccurrenceStartingWith(slave, 7, 4), is(1));
        assertThat(master.numberOfDependentOccurrencesRollingUpToThisOccurrenceStartingWith(slave, 8, 4), is(0));
        assertThat(master.numberOfDependentOccurrencesRollingUpToThisOccurrenceStartingWith(slave, 9, 4), is(1));
        assertThat(master.numberOfDependentOccurrencesRollingUpToThisOccurrenceStartingWith(slave, 10, 4), is(1));
    }

}
