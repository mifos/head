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

import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Before;
import org.junit.Test;

public class BasicWorkingDayStrategyTest {

    private BasicWorkingDayStrategy workingDayStrategy;

    private List<Days> workingDays;

    @Before
    public void setupAndInjectDependencies() {

        workingDays = Arrays.asList(DayOfWeek.mondayAsDay(), DayOfWeek.tuesdayAsDay(), DayOfWeek.wednesdayAsDay(),
                DayOfWeek.thursdayAsDay(), DayOfWeek.fridayAsDay());

        workingDayStrategy = new BasicWorkingDayStrategy(workingDays);
    }

    @Test
    public void whenADateFallsOnAWorkingDayItShouldNotBeAdjusted() {

        DateTime firstOfNextMonth = new DateTime().plusMonths(1).withDayOfMonth(1).toDateMidnight().toDateTime();
        DateTime firstTuesdayOfNextMonth = firstOfNextMonth.withDayOfWeek(DayOfWeek.tuesday());

        // exercise test
        DateTime adjustedDate = workingDayStrategy.adjust(firstTuesdayOfNextMonth);

        assertThat(adjustedDate, is(firstTuesdayOfNextMonth));
    }

    @Test
    public void whenADateFallsOnNonWorkingDayItShouldBeAdjustedToTheNearestWorkingDay() {

        DateTime firstOfNextMonth = new DateTime().plusMonths(1).withDayOfMonth(1).toDateMidnight().toDateTime();
        DateTime firstSaturdayOfNextMonth = firstOfNextMonth.withDayOfWeek(DayOfWeek.saturday());

        // exercise test
        DateTime adjustedDate = workingDayStrategy.adjust(firstSaturdayOfNextMonth);

        assertThat(adjustedDate, is(firstSaturdayOfNextMonth.plusDays(2)));
    }
}
