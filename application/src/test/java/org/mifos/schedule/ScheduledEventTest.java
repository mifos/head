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
        assertThat(master.numberOfEventsRollingUpToThis(slave, 1), is(0));
        assertThat(master.numberOfEventsRollingUpToThis(slave, 2), is(1));
        assertThat(master.numberOfEventsRollingUpToThis(slave, 3), is(0));
        assertThat(master.numberOfEventsRollingUpToThis(slave, 4), is(1));
    }

    @Test
    public void numberOfEventsRollingUpToThisMasterEveryTwoSlaveEveryOne() {
        ScheduledEvent master = new ScheduledEventBuilder().every(2).weeks().build();
        ScheduledEvent slave = new ScheduledEventBuilder().every(1).weeks().build();
        assertThat(master.numberOfEventsRollingUpToThis(slave, 1), is(2));
        assertThat(master.numberOfEventsRollingUpToThis(slave, 2), is(2));
        assertThat(master.numberOfEventsRollingUpToThis(slave, 3), is(2));
        assertThat(master.numberOfEventsRollingUpToThis(slave, 4), is(2));
    }

    @Test
    public void numberOfEventsRollingUpToThisMasterEveryTwoSlaveEveryThree() {
        ScheduledEvent master = new ScheduledEventBuilder().every(2).weeks().build();
        ScheduledEvent slave = new ScheduledEventBuilder().every(3).weeks().build();
        assertThat(master.numberOfEventsRollingUpToThis(slave, 1), is(0));
        assertThat(master.numberOfEventsRollingUpToThis(slave, 2), is(1));
        assertThat(master.numberOfEventsRollingUpToThis(slave, 3), is(1));
        assertThat(master.numberOfEventsRollingUpToThis(slave, 4), is(0));
        assertThat(master.numberOfEventsRollingUpToThis(slave, 5), is(1));
        assertThat(master.numberOfEventsRollingUpToThis(slave, 6), is(1));
        assertThat(master.numberOfEventsRollingUpToThis(slave, 7), is(0));
    }

}
