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
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SavingsInterestCalculatorTest {

    private InterestCalculator interestCalculator;

    private InterestCalculationPeriodDetail interestCalculationPeriodDetail;

    private Money minBalanceRequired;
    private Money balanceBeforeInterval;

    @Mock
    private PrincipalCalculationStrategy principalCalculationStrategy;

    @Mock
    private PrincipalCalculationStrategy totalPrincipalCalculationStrategy;

    @Mock
    private SimpleInterestCalculationStrategy compoundInterestCalculationStrategy;

    @Before
    public void setup() {
        interestCalculator = new SavingsInterestCalculator(principalCalculationStrategy);
        ((SavingsInterestCalculator)interestCalculator).setCompoundInterestCaluclation(compoundInterestCalculationStrategy);
        ((SavingsInterestCalculator)interestCalculator).setTotalPrincipalCalculationStrategy(totalPrincipalCalculationStrategy);

        LocalDate startDate = new LocalDate();
        LocalDate endDate = new LocalDate();
        InterestCalculationInterval calculationInterval = new InterestCalculationInterval(startDate, endDate);
        List<EndOfDayDetail> dailyDetails = new ArrayList<EndOfDayDetail>();

        minBalanceRequired = TestUtils.createMoney("20");
        balanceBeforeInterval = TestUtils.createMoney("100");
        interestCalculationPeriodDetail = new InterestCalculationPeriodDetail(calculationInterval, dailyDetails, minBalanceRequired, balanceBeforeInterval, minBalanceRequired.getCurrency(), 10.0);
    }

    @Test
    public void shouldCalculatePrincipalUsingPrincipalCalculationStrategy() {

        // stub
        when(principalCalculationStrategy.calculatePrincipal(interestCalculationPeriodDetail)).thenReturn(TestUtils.createMoney("0"));
        when(principalCalculationStrategy.calculatePrincipal(interestCalculationPeriodDetail)).thenReturn(TestUtils.createMoney("0"));

        // exercise test
        interestCalculator.calculateSavingsDetailsForPeriod(interestCalculationPeriodDetail);

        // verification
        verify(principalCalculationStrategy).calculatePrincipal(interestCalculationPeriodDetail);
    }

    @Test
    public void shouldCalculateInterestWhenPrincipalReturnedSatisfiesMinBalanceRequired() {

        Money expectedInterest = TestUtils.createMoney("21");

        // stubbing
        when(principalCalculationStrategy.calculatePrincipal(interestCalculationPeriodDetail)).thenReturn(minBalanceRequired);
        when(compoundInterestCalculationStrategy.calculateInterest(minBalanceRequired, Double.valueOf("10"), 0)).thenReturn(expectedInterest);

        // exercise test
        InterestCalculationPeriodResult result = interestCalculator.calculateSavingsDetailsForPeriod(interestCalculationPeriodDetail);

        assertThat(result.getInterest(), is(expectedInterest));
    }
}