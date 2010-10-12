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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.*;

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

    @Mock
    private PrincipalCalculationStrategy principalCalculationStrategy;

    @Mock
    private SimpleInterestCalculationStrategy compoundInterestCalculationStrategy;

    @Mock
    private InterestCalucationRule minimumBalanceRule;

    @Before
    public void setup() {
        interestCalculator = new SavingsInterestCalculator(principalCalculationStrategy, compoundInterestCalculationStrategy, minimumBalanceRule);
        interestCalculationPeriodDetail = new InterestCalculationPeriodBuilder().build();
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
        when(minimumBalanceRule.isCalculationAllowed(minBalanceRequired)).thenReturn(true);
        when(compoundInterestCalculationStrategy.calculateInterest((Money)anyObject(), anyInt())).thenReturn(expectedInterest);

        // exercise test
        InterestCalculationPeriodResult result = interestCalculator.calculateSavingsDetailsForPeriod(interestCalculationPeriodDetail);

        assertThat(result.getInterest(), is(expectedInterest));
    }
}