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
import org.mifos.framework.util.helpers.Money;
import org.mifos.service.BusinessRuleException;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EqualInstallmentGeneratorFactoryTest {

    // class under test
    private EqualInstallmentGeneratorFactory equalInstallmentGeneratorFactory;

    // test doubles
    private Money loanInterest = TestMoneyUtil.createMoney("100.0");

    @Before
    public void setup() {
        equalInstallmentGeneratorFactory = new EqualInstallmentGeneratorFactoryImpl();
    }

    @Test
    public void shouldUseFlatLoanInterestEqualInstallmentGenerator() {

        InterestType interestType = InterestType.FLAT;

        // exercise test
        PrincipalWithInterestGenerator equalInstallmentGenerator = equalInstallmentGeneratorFactory.create(interestType, loanInterest, false);

        // verification
        assertThat(equalInstallmentGenerator, is(instanceOf(FlatLoanPrincipalWithInterestGenerator.class)));
    }

    @Test
    public void shouldUseDecliningBalanceEqualInstallmentGenerator() {

        InterestType interestType = InterestType.DECLINING;

        // exercise test
        PrincipalWithInterestGenerator equalInstallmentGenerator = equalInstallmentGeneratorFactory.create(interestType, loanInterest, false);

        // verification
        assertThat(equalInstallmentGenerator, is(instanceOf(DecliningBalancePrincipalWithInterestGenerator.class)));
    }

    @Test
    public void shouldAlsoUseDecliningBalanceEqualInstallmentGenerator() {

        InterestType interestType = InterestType.DECLINING_PB;

        // exercise test
        PrincipalWithInterestGenerator equalInstallmentGenerator = equalInstallmentGeneratorFactory.create(interestType, loanInterest, false);

        // verification
        assertThat(equalInstallmentGenerator, is(instanceOf(DecliningBalancePrincipalWithInterestGenerator.class)));
    }

    @Test
    public void shouldUseDecliningBalanceEqualPrincipalEqualInstallmentGenerator() {

        InterestType interestType = InterestType.DECLINING_EPI;

        // exercise test
        PrincipalWithInterestGenerator equalInstallmentGenerator = equalInstallmentGeneratorFactory.create(interestType, loanInterest, false);

        // verification
        assertThat(equalInstallmentGenerator, is(instanceOf(DecliningBalanceEqualPrincipalWithInterestGenerator.class)));
    }

    @Test(expected=BusinessRuleException.class)
    public void shouldThrowRuntimeException() {

        InterestType interestType = InterestType.COMPOUND;

        // exercise test
        equalInstallmentGeneratorFactory.create(interestType, loanInterest, false);
    }

}