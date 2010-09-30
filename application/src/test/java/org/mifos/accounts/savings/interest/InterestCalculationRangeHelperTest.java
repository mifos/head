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
public class InterestCalculationRangeHelperTest {

    private InterestCalculationRangeHelper interestCalculationRangeHelper;

    private LocalDate september30th = new LocalDate().withYear(2010).withMonthOfYear(9).withDayOfMonth(30);

    @Before
    public void setup() {
        this.interestCalculationRangeHelper = new InterestCalculationRangeHelper();
    }

    @Test
    public void shouldDetermineLowerAndUpperDateRangesWithFirstDepositDateWithinFirstCalculationPeriod() {

        // setup
        LocalDate firstDepositDate = new LocalDate().withYear(2010).withMonthOfYear(1).withDayOfMonth(7);
        ScheduledEvent endOfMonthEveryThreeMonths = new MonthlyOnDateScheduledEvent(3, 31);

        // exercise test
        List<InterestCalculationRange> validRanges = this.interestCalculationRangeHelper.determineAllPossibleInterestCalculationPeriods(firstDepositDate, endOfMonthEveryThreeMonths, september30th);

        // verification
        assertFalse(validRanges.isEmpty());
        assertThat(validRanges.get(0).getLowerDate(), is(firstDepositDate));
        assertThat(validRanges.get(0).getUpperDate(), is(new LocalDate().withYear(2010).withMonthOfYear(3).withDayOfMonth(31)));

        assertThat(validRanges.get(1).getLowerDate(), is(new LocalDate().withYear(2010).withMonthOfYear(4).withDayOfMonth(1)));
        assertThat(validRanges.get(1).getUpperDate(), is(new LocalDate().withYear(2010).withMonthOfYear(6).withDayOfMonth(30)));

        assertThat(validRanges.get(2).getLowerDate(), is(new LocalDate().withYear(2010).withMonthOfYear(7).withDayOfMonth(1)));
        assertThat(validRanges.get(2).getUpperDate(), is(new LocalDate().withYear(2010).withMonthOfYear(9).withDayOfMonth(30)));
    }

    @Test
    public void shouldDetermineLowerAndUpperDateRangesWhenFirstDepositDateIsNotWithinFirstCalculationPeriod() {

        // setup
        LocalDate firstDepositDate = new LocalDate().withYear(2010).withMonthOfYear(5).withDayOfMonth(21);
        ScheduledEvent endOfMonthEveryThreeMonths = new MonthlyOnDateScheduledEvent(3, 31);

        // exercise test
        List<InterestCalculationRange> validRanges = this.interestCalculationRangeHelper.determineAllPossibleInterestCalculationPeriods(firstDepositDate, endOfMonthEveryThreeMonths, september30th);

        // verification
        assertFalse(validRanges.isEmpty());
        assertThat(validRanges.get(0).getLowerDate(), is(firstDepositDate));
        assertThat(validRanges.get(0).getUpperDate(), is(new LocalDate().withYear(2010).withMonthOfYear(6).withDayOfMonth(30)));

        assertThat(validRanges.get(1).getLowerDate(), is(new LocalDate().withYear(2010).withMonthOfYear(7).withDayOfMonth(1)));
        assertThat(validRanges.get(1).getUpperDate(), is(new LocalDate().withYear(2010).withMonthOfYear(9).withDayOfMonth(30)));
    }

    @Test
    public void shouldDetermineLowerAndUpperDateRangesWhenFirstDepositDateIsInDifferentYear() {

        // setup
        LocalDate firstDepositDate = new LocalDate().withYear(2009).withMonthOfYear(8).withDayOfMonth(21);
        ScheduledEvent endOfMonthEveryThreeMonths = new MonthlyOnDateScheduledEvent(3, 31);

        // exercise test
        List<InterestCalculationRange> validRanges = this.interestCalculationRangeHelper.determineAllPossibleInterestCalculationPeriods(firstDepositDate, endOfMonthEveryThreeMonths, september30th);

        // verification
        assertFalse(validRanges.isEmpty());
        assertThat(validRanges.get(0).getLowerDate(), is(firstDepositDate));
        assertThat(validRanges.get(0).getUpperDate(), is(new LocalDate().withYear(2009).withMonthOfYear(9).withDayOfMonth(30)));

        assertThat(validRanges.get(1).getLowerDate(), is(new LocalDate().withYear(2009).withMonthOfYear(10).withDayOfMonth(1)));
        assertThat(validRanges.get(1).getUpperDate(), is(new LocalDate().withYear(2009).withMonthOfYear(12).withDayOfMonth(31)));

        assertThat(validRanges.get(2).getLowerDate(), is(new LocalDate().withYear(2010).withMonthOfYear(1).withDayOfMonth(1)));
        assertThat(validRanges.get(2).getUpperDate(), is(new LocalDate().withYear(2010).withMonthOfYear(3).withDayOfMonth(31)));

        assertThat(validRanges.get(3).getLowerDate(), is(new LocalDate().withYear(2010).withMonthOfYear(4).withDayOfMonth(1)));
        assertThat(validRanges.get(3).getUpperDate(), is(new LocalDate().withYear(2010).withMonthOfYear(6).withDayOfMonth(30)));

        assertThat(validRanges.get(4).getLowerDate(), is(new LocalDate().withYear(2010).withMonthOfYear(7).withDayOfMonth(1)));
        assertThat(validRanges.get(4).getUpperDate(), is(new LocalDate().withYear(2010).withMonthOfYear(9).withDayOfMonth(30)));
    }

}