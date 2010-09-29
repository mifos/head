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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mifos.accounts.productdefinition.util.helpers.InterestCalcType;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;

public class SavingsInterestCalculatorFactoryTest {

    private SavingsInterestCalculatorFactory savingsInterestCalculatorFactory;

    @Test
    public void shouldReturnSavingsInterest() {

        InterestCalcType interestCalcType = InterestCalcType.AVERAGE_BALANCE;
        InterestCalculator interestCalculator = savingsInterestCalculatorFactory.create(interestCalcType);

        InterestCalculationRange range = null;
        List<EndOfDayDetail> dailyDetails = new ArrayList<EndOfDayDetail>();
        InterestCalculationPeriodDetail interestCalculationPeriodDetail = new InterestCalculationPeriodDetail(range, dailyDetails);

        Money interestDueForPeriod = interestCalculator.calculateInterestForPeriod(interestCalculationPeriodDetail);

        // verification
        assertThat(interestDueForPeriod, is(TestUtils.createMoney("54.0")));
    }

    @Test
    public void shouldReturnSavingsInterestCalculatorWithAverageBalanceStrategy() {

        InterestCalcType interestCalcType = InterestCalcType.AVERAGE_BALANCE;

        // exercise test
        InterestCalculator interestCalculator = savingsInterestCalculatorFactory.create(interestCalcType);

        // verification
        assertNotNull(interestCalculator);
    }

    @Test
    public void shouldReturnSavingsInterestCalculatorWithMinimumBalanceStrategy() {

        InterestCalcType interestCalcType = InterestCalcType.MINIMUM_BALANCE;

        // exercise test
        InterestCalculator interestCalculator = savingsInterestCalculatorFactory.create(interestCalcType);

        // verification
        assertNotNull(interestCalculator);
    }
}