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

package org.mifos.clientportfolio.newloan.domain;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.util.helpers.Money;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FlatLoanInterestCalculatorTest {

    private FlatLoanInterestCalculator flatLoanInterestCalculator;

    @Before
    public void setup() {
        flatLoanInterestCalculator = new FlatLoanInterestCalculator();
    }

    @Test
    public void shouldCalculateFlatLoanInterest() {

        // setup
        LoanInterestCalculationDetails loanInterestCalculationDetails = new LoanInterestCalculationDetailsBuilder().withLoanAmount("100.0")
                                                                                                                   .withInterestRate("10.0")
                                                                                                                   .withDurationInYears("1.0")
                                                                                                                   .build();

        // exercise test
        Money calculatedInterest = flatLoanInterestCalculator.calculate(loanInterestCalculationDetails);

        // verification
        assertThat(calculatedInterest, is(TestMoneyUtil.createMoney("10.0")));
    }
}