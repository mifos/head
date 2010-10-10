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
import org.mifos.accounts.savings.interest.schedule.InterestScheduledEvent;
import org.mifos.accounts.savings.interest.schedule.internal.DailyInterestScheduledEvent;
import org.mifos.accounts.savings.interest.schedule.internal.MonthlyOnLastDayOfMonthInterestScheduledEvent;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InterestCalculationIntervalHelperTest {

    private InterestCalculationIntervalHelper interestCalculationIntervalHelper;

    private LocalDate september30th = new LocalDate().withYear(2010).withMonthOfYear(9).withDayOfMonth(30);
    private LocalDate october5th = new LocalDate().withYear(2010).withMonthOfYear(10).withDayOfMonth(5);

    @Before
    public void setup() {
        this.interestCalculationIntervalHelper = new InterestCalculationIntervalHelper();
    }

    @Test
    public void shouldDetermineLowerAndUpperDateRangesForDailyScheduledEvent() {

        // setup
        LocalDate firstDepositDate = new LocalDate().withYear(2010).withMonthOfYear(9).withDayOfMonth(1);
        InterestScheduledEvent every14Days = new DailyInterestScheduledEvent(14);

        // exercise test
        List<InterestCalculationInterval> validIntervals = this.interestCalculationIntervalHelper.determineAllPossibleInterestCalculationPeriods(firstDepositDate, every14Days, october5th);

        // verification
        assertFalse(validIntervals.isEmpty());
        assertThat(validIntervals.get(0).getStartDate(), is(new LocalDate().withYear(2010).withMonthOfYear(9).withDayOfMonth(1)));
        assertThat(validIntervals.get(0).getEndDate(), is(new LocalDate().withYear(2010).withMonthOfYear(9).withDayOfMonth(9)));

        assertThat(validIntervals.get(1).getStartDate(), is(new LocalDate().withYear(2010).withMonthOfYear(9).withDayOfMonth(10)));
        assertThat(validIntervals.get(1).getEndDate(), is(new LocalDate().withYear(2010).withMonthOfYear(9).withDayOfMonth(23)));
    }

    @Test
    public void shouldDetermineLowerAndUpperDateRangesWithFirstDepositDateWithinFirstCalculationPeriod() {

        // setup
        LocalDate firstDepositDate = new LocalDate().withYear(2010).withMonthOfYear(1).withDayOfMonth(7);
        InterestScheduledEvent endOfMonthEveryThreeMonths = new MonthlyOnLastDayOfMonthInterestScheduledEvent(3);

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
        InterestScheduledEvent endOfMonthEveryThreeMonths = new MonthlyOnLastDayOfMonthInterestScheduledEvent(3);

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
        InterestScheduledEvent endOfMonthEveryThreeMonths = new MonthlyOnLastDayOfMonthInterestScheduledEvent(3);

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

    @Test
    public void shouldDetermineAllPossiblePostingIntervals() {

        // setup
        LocalDate march1st = new LocalDate().withYear(2010).withMonthOfYear(3).withDayOfMonth(1);
        LocalDate endOfYear = new LocalDate().withYear(2010).withMonthOfYear(12).withDayOfMonth(31);
        InterestScheduledEvent postingSixMonthly = new MonthlyOnLastDayOfMonthInterestScheduledEvent(6);

        // exercise test
        List<InterestCalculationInterval> validIntervals = this.interestCalculationIntervalHelper.determineAllPossibleInterestPostingPeriods(postingSixMonthly, march1st, endOfYear);

        // verification
        assertFalse(validIntervals.isEmpty());
        assertThat(validIntervals.get(0).getStartDate(), is(new LocalDate().withYear(2010).withMonthOfYear(1).withDayOfMonth(1)));
        assertThat(validIntervals.get(0).getEndDate(), is(new LocalDate().withYear(2010).withMonthOfYear(6).withDayOfMonth(30)));

        assertThat(validIntervals.get(1).getStartDate(), is(new LocalDate().withYear(2010).withMonthOfYear(7).withDayOfMonth(1)));
        assertThat(validIntervals.get(1).getEndDate(), is(new LocalDate().withYear(2010).withMonthOfYear(12).withDayOfMonth(31)));
    }

}