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
import org.junit.Assert;
import org.junit.Test;

public class WorkingDayTest {

    @Test
    public void shouldIndicateIsWorkingDay() {

        DateTime monday = new DateTime().withDayOfWeek(DayOfWeek.monday());
        List<Days> workingDays = Arrays.asList(DayOfWeek.mondayAsDay());

        boolean result = WorkingDay.isWorkingDay(monday, workingDays);

        Assert.assertTrue("did not equate working day correctly", result);
    }

    @Test
    public void shouldIndicateIsOneOfTheWorkingDays() {

        DateTime monday = new DateTime().withDayOfWeek(DayOfWeek.monday());
        List<Days> workingDays = Arrays.asList(DayOfWeek.mondayAsDay(), DayOfWeek.tuesdayAsDay(), DayOfWeek
                .wednesdayAsDay());

        boolean result = WorkingDay.isWorkingDay(monday, workingDays);

        Assert.assertTrue("did not equate working day correctly", result);
    }

    @Test
    public void shouldIndicateIsNotAWorkingDay() {

        DateTime sunday = new DateTime().withDayOfWeek(DayOfWeek.sunday());
        List<Days> workingDays = Arrays.asList(DayOfWeek.mondayAsDay(), DayOfWeek.tuesdayAsDay(), DayOfWeek
                .wednesdayAsDay());

        boolean result = WorkingDay.isWorkingDay(sunday, workingDays);

        Assert.assertFalse("did not equate working day correctly", result);
    }

    @Test
    public void shouldIndicateIsNotAWorkingDayUsingNegationMethod() {

        DateTime sunday = new DateTime().withDayOfWeek(DayOfWeek.sunday());
        List<Days> workingDays = Arrays.asList(DayOfWeek.mondayAsDay(), DayOfWeek.tuesdayAsDay(), DayOfWeek
                .wednesdayAsDay());

        boolean result = WorkingDay.isNotWorkingDay(sunday, workingDays);

        Assert.assertTrue("did not equate working day correctly", result);
    }

    @Test(expected = IllegalStateException.class)
    public void throwExceptionIfWorkingDaysAreEmpty() {

        DateTime sunday = new DateTime().withDayOfWeek(DayOfWeek.sunday());
        List<Days> workingDays = Arrays.asList();

        WorkingDay.nextWorkingDay(sunday, workingDays);
    }

    @Test
    public void shouldRollForwardDateToNextWorkingDay() {

        DateTime sunday = new DateTime().withDayOfWeek(DayOfWeek.sunday());
        List<Days> workingDays = Arrays.asList(DayOfWeek.mondayAsDay(), DayOfWeek.tuesdayAsDay(), DayOfWeek
                .wednesdayAsDay());

        DateTime nextWorkingDay = WorkingDay.nextWorkingDay(sunday, workingDays);

        assertThat(nextWorkingDay.getDayOfWeek(), is(DayOfWeek.monday()));
    }

    @Test
    public void shouldRollForwardDateWhenAlreadyOnWorkingDay() {

        DateTime monday = new DateTime().withDayOfWeek(DayOfWeek.monday());
        List<Days> workingDays = Arrays.asList(DayOfWeek.mondayAsDay(), DayOfWeek.tuesdayAsDay());

        DateTime nextWorkingDay = WorkingDay.nextWorkingDay(monday, workingDays);

        assertThat(nextWorkingDay.getDayOfWeek(), is(DayOfWeek.tuesday()));
    }
}
