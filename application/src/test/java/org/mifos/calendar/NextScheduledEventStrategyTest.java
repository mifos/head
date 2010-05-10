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
