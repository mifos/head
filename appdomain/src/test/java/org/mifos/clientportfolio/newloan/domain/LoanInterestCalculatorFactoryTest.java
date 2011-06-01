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

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.productdefinition.util.helpers.InterestType;
import org.mifos.core.MifosRuntimeException;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LoanInterestCalculatorFactoryTest {

    private LoanInterestCalculatorFactory loanInterestCalculatorFactory;

    @Before
    public void setup() {
        loanInterestCalculatorFactory = new LoanInterestCalculatorFactoryImpl();
    }

    @Test
    public void shouldUseFlatLoanInterestCalculator() {

        // exercise test
        LoanInterestCalculator loanInterestCalculator = loanInterestCalculatorFactory.create(InterestType.FLAT, false);

        // verification
        assertThat(loanInterestCalculator, is(instanceOf(FlatLoanInterestCalculator.class)));
    }

    @Test
    public void shouldUseDecliningBalanceLoanInterestCalculator() {

        // exercise test
        LoanInterestCalculator loanInterestCalculator = loanInterestCalculatorFactory.create(InterestType.DECLINING, false);

        // verification
        assertThat(loanInterestCalculator, is(instanceOf(DecliningBalanceLoanInterestCalculator.class)));
    }

    @Test
    public void shouldUseDecliningBalanceWithEqualPrincipalInstallmentsLoanInterestCalculator() {

        // exercise test
        LoanInterestCalculator loanInterestCalculator = loanInterestCalculatorFactory.create(InterestType.DECLINING_EPI, false);

        // verification
        assertThat(loanInterestCalculator, is(instanceOf(DecliningBalanceWithEqualPrincipalInstallmentsLoanInterestCalculator.class)));
    }

    @Test
    public void shouldUseNullLoanInterestCalculatorForDecliningPbInterestType() {

        // exercise test
        LoanInterestCalculator loanInterestCalculator = loanInterestCalculatorFactory.create(InterestType.DECLINING_PB, false);

        // verification
        assertThat(loanInterestCalculator, is(instanceOf(NullLoanInterestCalculator.class)));
    }

    @Test(expected = MifosRuntimeException.class)
    public void shouldThrowRuntimeExceptionForCompoundInterestType() {

        // exercise test
        loanInterestCalculatorFactory.create(InterestType.COMPOUND, false);
    }
}