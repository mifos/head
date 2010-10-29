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
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.savings.interest.schedule.InterestScheduledEvent;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InterestCalculationPeriodCalculatorTest {

    // class under test
    private NonCompoundingInterestCalculator interestCalculationPeriodCalculator;

    @Mock
    private InterestCalculator interestCalculator;
    @Mock
    private InterestScheduledEvent interestCalculationSchedule;
    @Mock
    private CalendarPeriodHelper interestCalculationIntervalHelper;

    private static MifosCurrency oldCurrency;

    @BeforeClass
    public static void setCurrency() {
        oldCurrency = Money.getDefaultCurrency();
        Money.setDefaultCurrency(TestUtils.RUPEE);
    }

    @AfterClass
    public static void resetCurrency() {
        Money.setDefaultCurrency(oldCurrency);
    }

    @Before
    public void setup() {
        interestCalculationPeriodCalculator = new InterestCalculationPeriodCalculator(interestCalculator, interestCalculationSchedule, interestCalculationIntervalHelper);
    }

    @Test
    public void shouldReturnEmptyResultListWhenNoValidInterestCalculationInterevalsExist() {

        // setup
        List<EndOfDayDetail> endOfDayDetailsForCalculationPeriod =  new ArrayList<EndOfDayDetail>();

        CalendarPeriod calculationPeriod = new CalendarPeriodBuilder().build();
        Money totalBalanceBeforeCalculationPeriod = TestUtils.createMoney("0");

        List<CalendarPeriod> noValidCalculationIntervalsInPeriod = new ArrayList<CalendarPeriod>();

        // stubbing
        when(interestCalculationIntervalHelper.determineAllPossiblePeriods(calculationPeriod.getStartDate(), interestCalculationSchedule, calculationPeriod.getEndDate())).thenReturn(noValidCalculationIntervalsInPeriod);

        // exercise test
        List<InterestCalculationPeriodResult> results = this.interestCalculationPeriodCalculator.calculateDetails(calculationPeriod, totalBalanceBeforeCalculationPeriod, endOfDayDetailsForCalculationPeriod);

        assertTrue(results.isEmpty());
    }

    @Test
    public void shouldReturnInterestCalculationPeriodResultForEveryValidInterestCalculationInterval() {

        // setup
        List<EndOfDayDetail> endOfDayDetailsForCalculationPeriod =  new ArrayList<EndOfDayDetail>();

        CalendarPeriod calculationPeriod = new CalendarPeriodBuilder().build();
        Money totalBalanceBeforeCalculationPeriod = TestUtils.createMoney("0");

        CalendarPeriod validPeriod1 = new CalendarPeriodBuilder().build();
        List<CalendarPeriod> validCalculationIntervalsInPeriods = new ArrayList<CalendarPeriod>();
        validCalculationIntervalsInPeriods.add(validPeriod1);

        InterestCalculationPeriodResult interstResultsForPeriod = new InterestCalculationPeriodResultBuilder().build();

        // stubbing
        when(interestCalculationIntervalHelper.determineAllPossiblePeriods(calculationPeriod.getStartDate(), interestCalculationSchedule, calculationPeriod.getEndDate())).thenReturn(validCalculationIntervalsInPeriods);
        when(interestCalculator.calculateSavingsDetailsForPeriod((InterestCalculationPeriodDetail)anyObject())).thenReturn(interstResultsForPeriod);

        // exercise test
        List<InterestCalculationPeriodResult> results = this.interestCalculationPeriodCalculator.calculateDetails(calculationPeriod, totalBalanceBeforeCalculationPeriod, endOfDayDetailsForCalculationPeriod);

        assertFalse(results.isEmpty());
        assertThat(results.get(0), is(interstResultsForPeriod));
    }
}