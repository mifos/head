/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class InterestPostingPeriodCalculatorTest {

    // class under test
    private CompoundingInterestCalculator interestPostingPeriodCalculator;

    @Mock
    private NonCompoundingInterestCalculator interestCalculationPeriodCalculator;

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
        interestPostingPeriodCalculator = new InterestPostingPeriodCalculator(interestCalculationPeriodCalculator, Money.getDefaultCurrency());
    }

    @Test
    public void shouldReturnEmptyResultListWhenNoPostingPeriodsExist() {

        // setup
        List<EndOfDayDetail> endOfDayDetailsForPeriods =  new ArrayList<EndOfDayDetail>();
        List<CalendarPeriod> postingPeriods = new ArrayList<CalendarPeriod>();

        // exercise test
        List<InterestPostingPeriodResult> results = interestPostingPeriodCalculator.calculateAllPostingPeriodDetails(endOfDayDetailsForPeriods, postingPeriods);

        assertTrue(results.isEmpty());
    }

    @Test
    public void shouldReturnSingleResultWhenNoInterestCalculationResultsAreReturned() {

        // setup
        List<EndOfDayDetail> endOfDayDetailsForPeriods =  new ArrayList<EndOfDayDetail>();

        CalendarPeriod today = new CalendarPeriodBuilder().build();
        List<CalendarPeriod> postingPeriods = Arrays.asList(new CalendarPeriod[] {today});

        List<InterestCalculationPeriodResult> noInterestCalculationResults = new ArrayList<InterestCalculationPeriodResult>();

        // stubbing
        when(interestCalculationPeriodCalculator.calculateDetails(today, TestUtils.createMoney("0"), endOfDayDetailsForPeriods)).thenReturn(noInterestCalculationResults);

        // exercise test
        List<InterestPostingPeriodResult> results = interestPostingPeriodCalculator.calculateAllPostingPeriodDetails(endOfDayDetailsForPeriods, postingPeriods);

        assertFalse(results.isEmpty());
    }

    @Test
    public void shouldSetPostingPeriodBalanceAsSumInterestCalculationResults() {

        // setup
        List<EndOfDayDetail> endOfDayDetailsForPeriods =  new ArrayList<EndOfDayDetail>();
        CalendarPeriod today = new CalendarPeriodBuilder().build();
        List<CalendarPeriod> postingPeriods = Arrays.asList(new CalendarPeriod[] {today});

        InterestCalculationPeriodResult periodResult = new InterestCalculationPeriodResultBuilder().withTotalPrincipal("25").build();
        InterestCalculationPeriodResult periodResult2 = new InterestCalculationPeriodResultBuilder().withTotalPrincipal("42").build();
        List<InterestCalculationPeriodResult> interestCalculationResults = new ArrayList<InterestCalculationPeriodResult>();
        interestCalculationResults.add(periodResult);
        interestCalculationResults.add(periodResult2);

        // stubbing
        when(interestCalculationPeriodCalculator.calculateDetails(today, TestUtils.createMoney("0"), endOfDayDetailsForPeriods)).thenReturn(interestCalculationResults);

        // exercise test
        List<InterestPostingPeriodResult> results = interestPostingPeriodCalculator.calculateAllPostingPeriodDetails(endOfDayDetailsForPeriods, postingPeriods);

        assertThat(results.get(0).getPeriodBalance(), is(TestUtils.createMoney("67")));
    }

    @Test
    public void shouldSetAmountOfInterestCalculatedForPostingPeriod() {

        // setup
        List<EndOfDayDetail> endOfDayDetailsForPeriods =  new ArrayList<EndOfDayDetail>();
        CalendarPeriod today = new CalendarPeriodBuilder().build();
        List<CalendarPeriod> postingPeriods = Arrays.asList(new CalendarPeriod[] {today});

        InterestCalculationPeriodResult periodResult = new InterestCalculationPeriodResultBuilder().withPreviousInterest("0")
                                                                                                   .withCalculatedInterest("12.45")
                                                                                                   .build();
        InterestCalculationPeriodResult periodResult2 = new InterestCalculationPeriodResultBuilder().build();
        List<InterestCalculationPeriodResult> interestCalculationResults = new ArrayList<InterestCalculationPeriodResult>();
        interestCalculationResults.add(periodResult);
        interestCalculationResults.add(periodResult2);

        // stubbing
        when(interestCalculationPeriodCalculator.calculateDetails(today, TestUtils.createMoney("0"), endOfDayDetailsForPeriods)).thenReturn(interestCalculationResults);

        // exercise test
        List<InterestPostingPeriodResult> results = interestPostingPeriodCalculator.calculateAllPostingPeriodDetails(endOfDayDetailsForPeriods, postingPeriods);

        assertThat(results.get(0).getDifferenceInInterest(), is(TestUtils.createMoney("12.45")));
    }

    @Test
    public void shouldCompoundAnyInterestFromPreviousPeriodWithBalance() {

        // setup
        List<EndOfDayDetail> endOfDayDetailsForPeriods =  new ArrayList<EndOfDayDetail>();
        CalendarPeriod postingPeriod1 = new CalendarPeriodBuilder().build();
        CalendarPeriod postingPeriod2 = new CalendarPeriodBuilder().build();
        List<CalendarPeriod> postingPeriods = Arrays.asList(new CalendarPeriod[] {postingPeriod1, postingPeriod2});

        InterestCalculationPeriodResult periodResult = new InterestCalculationPeriodResultBuilder().withTotalPrincipal("25")
                                                                                                   .withPreviousInterest("0")
                                                                                                   .withCalculatedInterest("12.45")
                                                                                                   .build();
        InterestCalculationPeriodResult periodResult2 = new InterestCalculationPeriodResultBuilder().withTotalPrincipal("42").build();
        List<InterestCalculationPeriodResult> interestCalculationResults = new ArrayList<InterestCalculationPeriodResult>();
        interestCalculationResults.add(periodResult);
        interestCalculationResults.add(periodResult2);

        List<InterestCalculationPeriodResult> noInterestCalculationResults = new ArrayList<InterestCalculationPeriodResult>();

        // stubbing
        when(interestCalculationPeriodCalculator.calculateDetails(postingPeriod1, TestUtils.createMoney("0"), endOfDayDetailsForPeriods)).thenReturn(interestCalculationResults);
        when(interestCalculationPeriodCalculator.calculateDetails(postingPeriod2, TestUtils.createMoney("79.45"), endOfDayDetailsForPeriods)).thenReturn(noInterestCalculationResults);

        // exercise test
        List<InterestPostingPeriodResult> results = interestPostingPeriodCalculator.calculateAllPostingPeriodDetails(endOfDayDetailsForPeriods, postingPeriods);

        assertThat(results.get(1).getPeriodBalance(), is(TestUtils.createMoney("79.45")));
    }
}