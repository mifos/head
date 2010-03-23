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

import org.joda.time.DateTime;
import org.junit.Test;
import org.mifos.domain.builders.ScheduledEventBuilder;

public class MonthlyOnDateScheduledEventTest {

    private ScheduledEvent scheduledEvent;

    @Test
    public void canRollForwardDateToMatchDayOfMonthWhenCurrentDayOfMonthIsBehind() {

        scheduledEvent = new ScheduledEventBuilder().every(1).months().onDayOfMonth(3).build();
        DateTime firstOfNextMonth = new DateTime().plusMonths(1).withDayOfMonth(1).toDateMidnight().toDateTime();

        DateTime result = scheduledEvent.nearestMatchingDateBeginningAt(firstOfNextMonth);

        assertThat(result, is(firstOfNextMonth.plusDays(2)));
    }

    @Test
    public void canRollForwardDateToNextMonthToMatchDayOfMonthWhenDayOfMonthHasBeingPast() {

        scheduledEvent = new ScheduledEventBuilder().every(1).months().onDayOfMonth(3).build();
        DateTime fifthDayOfMonth = new DateTime().plusMonths(1).withDayOfMonth(5).toDateMidnight().toDateTime();

        DateTime result = scheduledEvent.nearestMatchingDateBeginningAt(fifthDayOfMonth);

        assertThat(result, is(fifthDayOfMonth.plusMonths(1).minusDays(2)));
    }
}