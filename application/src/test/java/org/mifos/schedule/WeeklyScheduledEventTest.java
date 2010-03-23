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
import org.mifos.calendar.DayOfWeek;
import org.mifos.domain.builders.ScheduledEventBuilder;

public class WeeklyScheduledEventTest {

    private ScheduledEvent scheduledEvent;

    @Test
    public void canRollForwardDateToMatchDayOfWeekStartingFromTwoDaysBehind() {

        scheduledEvent = new ScheduledEventBuilder().every(1).weeks().on(DayOfWeek.wednesday()).build();
        DateTime monday = DayOfWeek.mondayMidnight();

        DateTime result = scheduledEvent.nearestMatchingDateBeginningAt(monday);

        assertThat(result, is(monday.plusDays(2)));
    }

    @Test
    public void canRollForwardDateToNextNearestDateMatchingSchedule() {

        scheduledEvent = new ScheduledEventBuilder().every(1).weeks().on(DayOfWeek.wednesday()).build();
        DateTime monday = DayOfWeek.mondayMidnight();

        DateTime result = scheduledEvent.nextEventDateAfter(monday);

        assertThat(result, is(monday.plusDays(9)));
    }
}