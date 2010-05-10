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
