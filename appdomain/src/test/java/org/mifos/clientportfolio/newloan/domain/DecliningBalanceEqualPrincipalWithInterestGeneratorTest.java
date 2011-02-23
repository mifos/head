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

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.loan.util.helpers.InstallmentPrincipalAndInterest;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DecliningBalanceEqualPrincipalWithInterestGeneratorTest {

    // class under test
    private PrincipalWithInterestGenerator principalWithInterestGenerator;

    @Before
    public void setup() {
        principalWithInterestGenerator = new DecliningBalanceEqualPrincipalWithInterestGenerator();
    }

    @Test
    public void shouldReturnPrincipalAndInterestDetailsForMatchingNumberOfInstallments() {

        LoanInterestCalculationDetails loanInterestCalculationDetails = new LoanInterestCalculationDetailsBuilder().withLoanAmount("100.0")
                                                                                                                    .withGraceType(GraceType.NONE)
                                                                                                                    .withGraceDurationOf(0)
                                                                                                                    .withNumberOfInstallments(1)
                                                                                                                    .withInterestFractionalRatePerInstallmentOf("0.16")
                                                                                                                    .withInterestRate("10.0")
                                                                                                                    .build();

        // exercise test
        List<InstallmentPrincipalAndInterest> equalInstallments = principalWithInterestGenerator.generateEqualInstallments(loanInterestCalculationDetails);

        // verification
        assertThat(equalInstallments.size(), is(1));
    }

    @Test
    public void shouldReturnPrincipalAndInterestDetailsForMatchingNumberOfInstallments_larger() {

        LoanInterestCalculationDetails loanInterestCalculationDetails = new LoanInterestCalculationDetailsBuilder().withLoanAmount("100.0")
                                                                                                                    .withGraceType(GraceType.NONE)
                                                                                                                    .withGraceDurationOf(0)
                                                                                                                    .withNumberOfInstallments(20)
                                                                                                                    .withInterestFractionalRatePerInstallmentOf("0.16")
                                                                                                                    .withInterestRate("10.0")
                                                                                                                    .build();

        // exercise test
        List<InstallmentPrincipalAndInterest> equalInstallments = principalWithInterestGenerator.generateEqualInstallments(loanInterestCalculationDetails);

        // verification
        assertThat(equalInstallments.size(), is(20));
    }

    @Test
    public void givenNoGraceOnAnyInstallmentShouldReturnPrincipalAndInterestDueForEachInstallment() {

        LoanInterestCalculationDetails loanInterestCalculationDetails = new LoanInterestCalculationDetailsBuilder().withLoanAmount("100.0")
                                                                                                                    .withGraceType(GraceType.NONE)
                                                                                                                    .withGraceDurationOf(0)
                                                                                                                    .withNumberOfInstallments(1)
                                                                                                                    .withInterestFractionalRatePerInstallmentOf("0.16")
                                                                                                                    .withInterestRate("10.0")
                                                                                                                    .build();

        // exercise test
        List<InstallmentPrincipalAndInterest> equalInstallments = principalWithInterestGenerator.generateEqualInstallments(loanInterestCalculationDetails);

        // verification
        assertThat(TestMoneyUtil.moneyOf(equalInstallments.get(0).getPrincipal()), is(TestMoneyUtil.moneyOf("100")));
        assertThat(TestMoneyUtil.moneyOf(equalInstallments.get(0).getInterest()), is(TestMoneyUtil.moneyOf("16.0")));
    }

    @Test
    public void givenGraceOnAllRepaymentsShouldReturnPrincipalAndInterestDueForEachInstallment() {

        LoanInterestCalculationDetails loanInterestCalculationDetails = new LoanInterestCalculationDetailsBuilder().withLoanAmount("100.0")
                                                                                                                    .withGraceType(GraceType.GRACEONALLREPAYMENTS)
                                                                                                                    .withGraceDurationOf(0)
                                                                                                                    .withNumberOfInstallments(1)
                                                                                                                    .withInterestFractionalRatePerInstallmentOf("0.16")
                                                                                                                    .withInterestRate("10.0")
                                                                                                                    .build();

        // exercise test
        List<InstallmentPrincipalAndInterest> equalInstallments = principalWithInterestGenerator.generateEqualInstallments(loanInterestCalculationDetails);

        // verification
        assertThat(TestMoneyUtil.moneyOf(equalInstallments.get(0).getPrincipal()), is(TestMoneyUtil.moneyOf("100")));
        assertThat(TestMoneyUtil.moneyOf(equalInstallments.get(0).getInterest()), is(TestMoneyUtil.moneyOf("16.0")));
    }

    @Test
    public void givenGraceOnAllRepaymentsShouldReturnPrincipalAndInterestDueForEachInstallmentRelativeToLoanAmount() {

        LoanInterestCalculationDetails loanInterestCalculationDetails = new LoanInterestCalculationDetailsBuilder().withLoanAmount("100.0")
                                                                                                                    .withGraceType(GraceType.GRACEONALLREPAYMENTS)
                                                                                                                    .withGraceDurationOf(0)
                                                                                                                    .withNumberOfInstallments(10)
                                                                                                                    .withInterestFractionalRatePerInstallmentOf("0.16")
                                                                                                                    .withInterestRate("10.0")
                                                                                                                    .build();

        // exercise test
        List<InstallmentPrincipalAndInterest> equalInstallments = principalWithInterestGenerator.generateEqualInstallments(loanInterestCalculationDetails);

        // verification
        assertThat(TestMoneyUtil.moneyOf(equalInstallments.get(0).getPrincipal()), is(TestMoneyUtil.moneyOf("10.0")));
        assertThat(TestMoneyUtil.moneyOf(equalInstallments.get(0).getInterest()), is(TestMoneyUtil.moneyOf("16.0")));
    }

    @Test
    public void givenPrincipalOnlyGraceShouldReturnPrincipalAndInterestDueForEachInstallmentRelativeToLoanAmount() {

        LoanInterestCalculationDetails loanInterestCalculationDetails = new LoanInterestCalculationDetailsBuilder().withLoanAmount("100.0")
                                                                                                                    .withGraceType(GraceType.PRINCIPALONLYGRACE)
                                                                                                                    .withGraceDurationOf(0)
                                                                                                                    .withNumberOfInstallments(10)
                                                                                                                    .withInterestFractionalRatePerInstallmentOf("0.16")
                                                                                                                    .withInterestRate("10.0")
                                                                                                                    .build();

        // exercise test
        List<InstallmentPrincipalAndInterest> equalInstallments = principalWithInterestGenerator.generateEqualInstallments(loanInterestCalculationDetails);

        // verification
        assertThat(TestMoneyUtil.moneyOf(equalInstallments.get(0).getPrincipal()), is(TestMoneyUtil.moneyOf("10.0")));
        assertThat(TestMoneyUtil.moneyOf(equalInstallments.get(0).getInterest()), is(TestMoneyUtil.moneyOf("16.0")));
    }

    @Test
    public void givenPrincipalOnlyGraceForFourInstallmentsShouldReturnPrincipalAndInterestDueForEachInstallmentRelativeToLoanAmount() {

        LoanInterestCalculationDetails loanInterestCalculationDetails = new LoanInterestCalculationDetailsBuilder().withLoanAmount("100.0")
                                                                                                                    .withGraceType(GraceType.PRINCIPALONLYGRACE)
                                                                                                                    .withGraceDurationOf(4)
                                                                                                                    .withNumberOfInstallments(10)
                                                                                                                    .withInterestFractionalRatePerInstallmentOf("0.16")
                                                                                                                    .withInterestRate("10.0")
                                                                                                                    .build();

        // exercise test
        List<InstallmentPrincipalAndInterest> equalInstallments = principalWithInterestGenerator.generateEqualInstallments(loanInterestCalculationDetails);

        // verification
        assertThat(TestMoneyUtil.moneyOf(equalInstallments.get(0).getPrincipal()), is(TestMoneyUtil.moneyOf("0.0")));
        assertThat(TestMoneyUtil.moneyOf(equalInstallments.get(0).getInterest()), is(TestMoneyUtil.moneyOf("16.0")));
    }

    @Test
    public void givenNoGraceWithZeroInterestShouldReturnPrincipalAndInterestDueForEachInstallmentRelativeToLoanAmount() {

        LoanInterestCalculationDetails loanInterestCalculationDetails = new LoanInterestCalculationDetailsBuilder().withLoanAmount("100.0")
                                                                                                                    .withGraceType(GraceType.NONE)
                                                                                                                    .withGraceDurationOf(0)
                                                                                                                    .withNumberOfInstallments(10)
                                                                                                                    .withInterestFractionalRatePerInstallmentOf("0.16")
                                                                                                                    .withInterestRate("0.0")
                                                                                                                    .build();

        // exercise test
        List<InstallmentPrincipalAndInterest> equalInstallments = principalWithInterestGenerator.generateEqualInstallments(loanInterestCalculationDetails);

        // verification
        assertThat(TestMoneyUtil.moneyOf(equalInstallments.get(0).getPrincipal()), is(TestMoneyUtil.moneyOf("10.0")));
        assertThat(TestMoneyUtil.moneyOf(equalInstallments.get(0).getInterest()), is(TestMoneyUtil.moneyOf("16.0")));
    }
}