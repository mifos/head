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

package org.mifos.accounts.savings.interest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.schedule.ScheduledEvent;
import org.mifos.schedule.internal.MonthlyOnDateScheduledEvent;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InterestCalculationIntevalHelperTest {

    private InterestCalculationIntervalHelper interestCalculationIntervalHelper;

    private LocalDate september30th = new LocalDate().withYear(2010).withMonthOfYear(9).withDayOfMonth(30);

    @Before
    public void setup() {
        this.interestCalculationIntervalHelper = new InterestCalculationIntervalHelper();
    }

    @Test
    public void shouldDetermineLowerAndUpperDateRangesWithFirstDepositDateWithinFirstCalculationPeriod() {

        // setup
        LocalDate firstDepositDate = new LocalDate().withYear(2010).withMonthOfYear(1).withDayOfMonth(7);
        ScheduledEvent endOfMonthEveryThreeMonths = new MonthlyOnDateScheduledEvent(3, 31);

        // exercise test
        List<InterestCalculationInterval> validIntervals = this.interestCalculationIntervalHelper.determineAllPossibleInterestCalculationPeriods(firstDepositDate, endOfMonthEveryThreeMonths, september30th);

        // verification
        assertFalse(validIntervals.isEmpty());
        assertThat(validIntervals.get(0).getStartDate(), is(firstDepositDate));
        assertThat(validIntervals.get(0).getEndDate(), is(new LocalDate().withYear(2010).withMonthOfYear(3).withDayOfMonth(31)));

        assertThat(validIntervals.get(1).getStartDate(), is(new LocalDate().withYear(2010).withMonthOfYear(4).withDayOfMonth(1)));
        assertThat(validIntervals.get(1).getEndDate(), is(new LocalDate().withYear(2010).withMonthOfYear(6).withDayOfMonth(30)));

        assertThat(validIntervals.get(2).getStartDate(), is(new LocalDate().withYear(2010).withMonthOfYear(7).withDayOfMonth(1)));
        assertThat(validIntervals.get(2).getEndDate(), is(new LocalDate().withYear(2010).withMonthOfYear(9).withDayOfMonth(30)));
    }

    @Test
    public void shouldDetermineLowerAndUpperDateRangesWhenFirstDepositDateIsNotWithinFirstCalculationPeriod() {

        // setup
        LocalDate firstDepositDate = new LocalDate().withYear(2010).withMonthOfYear(5).withDayOfMonth(21);
        ScheduledEvent endOfMonthEveryThreeMonths = new MonthlyOnDateScheduledEvent(3, 31);

        // exercise test
        List<InterestCalculationInterval> validIntervals = this.interestCalculationIntervalHelper.determineAllPossibleInterestCalculationPeriods(firstDepositDate, endOfMonthEveryThreeMonths, september30th);

        // verification
        assertFalse(validIntervals.isEmpty());
        assertThat(validIntervals.get(0).getStartDate(), is(firstDepositDate));
        assertThat(validIntervals.get(0).getEndDate(), is(new LocalDate().withYear(2010).withMonthOfYear(6).withDayOfMonth(30)));

        assertThat(validIntervals.get(1).getStartDate(), is(new LocalDate().withYear(2010).withMonthOfYear(7).withDayOfMonth(1)));
        assertThat(validIntervals.get(1).getEndDate(), is(new LocalDate().withYear(2010).withMonthOfYear(9).withDayOfMonth(30)));
    }

    @Test
    public void shouldDetermineLowerAndUpperDateRangesWhenFirstDepositDateIsInDifferentYear() {

        // setup
        LocalDate firstDepositDate = new LocalDate().withYear(2009).withMonthOfYear(8).withDayOfMonth(21);
        ScheduledEvent endOfMonthEveryThreeMonths = new MonthlyOnDateScheduledEvent(3, 31);

        // exercise test
        List<InterestCalculationInterval> validIntervals = this.interestCalculationIntervalHelper.determineAllPossibleInterestCalculationPeriods(firstDepositDate, endOfMonthEveryThreeMonths, september30th);

        // verification
        assertFalse(validIntervals.isEmpty());
        assertThat(validIntervals.get(0).getStartDate(), is(firstDepositDate));
        assertThat(validIntervals.get(0).getEndDate(), is(new LocalDate().withYear(2009).withMonthOfYear(9).withDayOfMonth(30)));

        assertThat(validIntervals.get(1).getStartDate(), is(new LocalDate().withYear(2009).withMonthOfYear(10).withDayOfMonth(1)));
        assertThat(validIntervals.get(1).getEndDate(), is(new LocalDate().withYear(2009).withMonthOfYear(12).withDayOfMonth(31)));

        assertThat(validIntervals.get(2).getStartDate(), is(new LocalDate().withYear(2010).withMonthOfYear(1).withDayOfMonth(1)));
        assertThat(validIntervals.get(2).getEndDate(), is(new LocalDate().withYear(2010).withMonthOfYear(3).withDayOfMonth(31)));

        assertThat(validIntervals.get(3).getStartDate(), is(new LocalDate().withYear(2010).withMonthOfYear(4).withDayOfMonth(1)));
        assertThat(validIntervals.get(3).getEndDate(), is(new LocalDate().withYear(2010).withMonthOfYear(6).withDayOfMonth(30)));

        assertThat(validIntervals.get(4).getStartDate(), is(new LocalDate().withYear(2010).withMonthOfYear(7).withDayOfMonth(1)));
        assertThat(validIntervals.get(4).getEndDate(), is(new LocalDate().withYear(2010).withMonthOfYear(9).withDayOfMonth(30)));
    }

}