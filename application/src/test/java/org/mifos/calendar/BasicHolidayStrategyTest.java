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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.schedule.ScheduledEvent;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BasicHolidayStrategyTest {

    private BasicHolidayStrategy holidayStrategy;

    @Mock
    private ScheduledEvent scheduledEvent;

    @Mock
    private Holiday holiday;

    @Mock
    private Holiday holiday2;

    private List<Days> workingDays;

    @Before
    public void setupAndInjectDependencies() {

        workingDays = Arrays.asList(DayOfWeek.mondayAsDay(), DayOfWeek.tuesdayAsDay(), DayOfWeek.wednesdayAsDay(),
                DayOfWeek.thursdayAsDay(), DayOfWeek.fridayAsDay());
    }

    @Test
    public void whenAHolidayFallsOnDateTheDateShouldBeAdjusted() {

        // setup
        List<Holiday> upcomingHolidays = Arrays.asList(holiday);
        holidayStrategy = new BasicHolidayStrategy(upcomingHolidays, workingDays, scheduledEvent);

        DateTime firstOfNextMonth = new DateTime().plusMonths(1).withDayOfMonth(1).toDateMidnight().toDateTime();

        // stubbing
        when(holiday.encloses(firstOfNextMonth.toDate())).thenReturn(true);
        when(holiday.adjust(firstOfNextMonth, workingDays, scheduledEvent)).thenReturn(firstOfNextMonth.plusDays(1));

        // exercise test
        DateTime adjustedDate = holidayStrategy.adjust(firstOfNextMonth);

        assertThat(adjustedDate, is(firstOfNextMonth.plusDays(1)));
    }

    @Test
    public void whenAHolidayAdjustmentCausesAdjustedDateToFallOnAnotherHolidayTheDateShouldAgainBeAdjustedForSubsequentHoliday() {

        // setup
        List<Holiday> upcomingHolidays = Arrays.asList(holiday, holiday2);
        holidayStrategy = new BasicHolidayStrategy(upcomingHolidays, workingDays, scheduledEvent);

        DateTime firstOfNextMonth = new DateTime().plusMonths(1).withDayOfMonth(1).toDateMidnight().toDateTime();
        DateTime secondOfNextMonth = firstOfNextMonth.plusDays(1);

        when(holiday.encloses(firstOfNextMonth.toDate())).thenReturn(true);
        when(holiday.adjust(firstOfNextMonth, workingDays, scheduledEvent)).thenReturn(secondOfNextMonth);

        when(holiday2.encloses(secondOfNextMonth.toDate())).thenReturn(true);
        when(holiday2.adjust(secondOfNextMonth, workingDays, scheduledEvent)).thenReturn(secondOfNextMonth.plusDays(1));

        DateTime adjustedDate = holidayStrategy.adjust(firstOfNextMonth);

        assertThat(adjustedDate, is(secondOfNextMonth.plusDays(1)));
    }
}
