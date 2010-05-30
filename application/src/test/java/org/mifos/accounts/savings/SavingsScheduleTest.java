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

package org.mifos.accounts.savings;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.collectionsheet.persistence.SavingsScheduleBuilder;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.business.CustomerBO;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * I test {@link SavingsScheduleEntity}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SavingsScheduleTest {

    private static MifosCurrency defaultCurrency;
    private static Money recommendedAmount;

    // class under test
    private SavingsScheduleEntity savingsSchedule;

    // collaborators
    @Mock
    private CustomerBO payingCustomer;

    @Mock
    private SavingsBO savingsAccount;

    @BeforeClass
    public static void setupMifosLoggerDueToUseOfStaticClientRules() {
        defaultCurrency = TestUtils.RUPEE;
        Money.setDefaultCurrency(defaultCurrency);
        recommendedAmount = new Money(TestUtils.RUPEE, "10.0");
    }

    @Before
    public void setupForEachTest() {
        when(savingsAccount.getCurrency()).thenReturn(defaultCurrency);
    }

    @Test
    public void whenASavingsScheduleForAnActiveSavingsAccountIsCreatedDepositEqualsTheAccountRecommendedAmount() {

        AccountStateEntity savingsAccountState = new AccountStateEntity(AccountState.SAVINGS_ACTIVE);
        // stubbing
        when(savingsAccount.getAccountState()).thenReturn(savingsAccountState);

        // exercise
        savingsSchedule = new SavingsScheduleBuilder().withInstallmentNumber(1).withAccount(savingsAccount)
                .withCustomer(payingCustomer).withDepositDue(recommendedAmount).build();

        // verification
        assertThat(savingsSchedule.getDeposit(), is(recommendedAmount));
    }

    @Test
    public void whenASavingsScheduleForAnInActiveSavingsAccountIsCreatedDepositEqualsZero() {

        AccountStateEntity savingsAccountState = new AccountStateEntity(AccountState.SAVINGS_INACTIVE);
        // stubbing
        when(savingsAccount.getAccountState()).thenReturn(savingsAccountState);

        // exercise
        savingsSchedule = new SavingsScheduleBuilder().withInstallmentNumber(1).withAccount(savingsAccount)
                .withCustomer(payingCustomer).withDepositDue(recommendedAmount).build();

        // verification
        assertThat(savingsSchedule.getDeposit(), is(new Money(defaultCurrency)));
    }
}
