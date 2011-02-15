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

package org.mifos.clientportfolio.newloan.domain;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.framework.util.helpers.Money;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DecliningBalanceLoanInterestCalculatorTest {

    private DecliningBalanceLoanInterestCalculator decliningBalanceLoanInterestCalculator;

    @Before
    public void setup() {
        decliningBalanceLoanInterestCalculator = new DecliningBalanceLoanInterestCalculator();
    }

    @Test
    public void shouldCalculateLoanInterest() {

        // setup
        LoanInterestCalculationDetails loanInterestCalculationDetails = new LoanInterestCalculationDetailsBuilder().withLoanAmount("100.0")
                                                                                                                   .withInterestRate("10.0")
                                                                                                                   .withDurationInYears("1.0")
                                                                                                                   .withGraceType(GraceType.NONE)
                                                                                                                   .withGraceDurationOf(0)
                                                                                                                   .withNumberOfInstallments(6)
                                                                                                                   .withInterestFractionalRatePerInstallmentOf("0.16")
                                                                                                                   .build();

        // exercise test
        Money calculatedInterest = decliningBalanceLoanInterestCalculator.calculate(loanInterestCalculationDetails);

        // verification
        assertThat(moneyOf(calculatedInterest), is(moneyOf(TestMoneyUtil.createMoney("62.8"))));
    }

    @Test
    public void shouldCalculateLoanInterestWithPrincipalOnlyGrace() {

        // setup
        LoanInterestCalculationDetails loanInterestCalculationDetails = new LoanInterestCalculationDetailsBuilder().withLoanAmount("100.0")
                                                                                                                   .withInterestRate("10.0")
                                                                                                                   .withDurationInYears("1.0")
                                                                                                                   .withGraceType(GraceType.PRINCIPALONLYGRACE)
                                                                                                                   .withGraceDurationOf(0)
                                                                                                                   .withNumberOfInstallments(6)
                                                                                                                   .withInterestFractionalRatePerInstallmentOf("0.16")
                                                                                                                   .build();

        // exercise test
        Money calculatedInterest = decliningBalanceLoanInterestCalculator.calculate(loanInterestCalculationDetails);

        // verification
        assertThat(moneyOf(calculatedInterest), is(moneyOf(TestMoneyUtil.createMoney("62.8"))));
    }

    @Test
    public void shouldCalculateLoanInterestWithPrincipalOnlyGraceAndGraceDuration() {

        // setup
        LoanInterestCalculationDetails loanInterestCalculationDetails = new LoanInterestCalculationDetailsBuilder().withLoanAmount("100.0")
                                                                                                                   .withInterestRate("10.0")
                                                                                                                   .withDurationInYears("1.0")
                                                                                                                   .withGraceType(GraceType.PRINCIPALONLYGRACE)
                                                                                                                   .withGraceDurationOf(3)
                                                                                                                   .withNumberOfInstallments(6)
                                                                                                                   .withInterestFractionalRatePerInstallmentOf("0.16")
                                                                                                                   .build();

        // exercise test
        Money calculatedInterest = decliningBalanceLoanInterestCalculator.calculate(loanInterestCalculationDetails);

        // verification
        assertThat(moneyOf(calculatedInterest), is(moneyOf(TestMoneyUtil.createMoney("81.6"))));
    }

    @Test
    public void shouldCalculateLoanInterestWithZeroInterest() {

        // setup
        LoanInterestCalculationDetails loanInterestCalculationDetails = new LoanInterestCalculationDetailsBuilder().withLoanAmount("100.0")
                                                                                                                   .withInterestRate("0.0")
                                                                                                                   .withDurationInYears("1.0")
                                                                                                                   .withGraceType(GraceType.PRINCIPALONLYGRACE)
                                                                                                                   .withGraceDurationOf(3)
                                                                                                                   .withNumberOfInstallments(6)
                                                                                                                   .withInterestFractionalRatePerInstallmentOf("0.16")
                                                                                                                   .build();

        // exercise test
        Money calculatedInterest = decliningBalanceLoanInterestCalculator.calculate(loanInterestCalculationDetails);

        // verification
        assertThat(moneyOf(calculatedInterest), is(moneyOf(TestMoneyUtil.createMoney("48.0"))));
    }

    private String moneyOf(Money money) {
        return money.toString(Short.valueOf("1"));
    }
}