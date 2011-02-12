/*
 * Copyright Grameen Foundation USA
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

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class SavingsInterestCalculatorFactoryTest {

    private SavingsInterestCalculatorFactory savingsInterestCalculatorFactory;

    @Test
    public void shouldReturnSavingsInterestCalculatorWithAverageBalanceStrategy() {

        SavingsInterestDetail interestDetail = new SavingsInterestDetailBuilder().averageBalance().build();

        // exercise test
        InterestCalculator interestCalculator = savingsInterestCalculatorFactory.create(interestDetail);

        // verification
        assertNotNull(interestCalculator);
    }

    @Test
    public void shouldReturnSavingsInterestCalculatorWithMinimumBalanceStrategy() {

        SavingsInterestDetail interestDetail = new SavingsInterestDetailBuilder().mimimumBalance().build();

        // exercise test
        InterestCalculator interestCalculator = savingsInterestCalculatorFactory.create(interestDetail);

        // verification
        assertNotNull(interestCalculator);
    }
}