/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.accounts.savings.business;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.collectionsheet.persistence.SavingsAccountBuilder;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsProductBuilder;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * I test {@link SavingsBO}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SavingsWithdrawalTest {

    private static MifosCurrency defaultCurrency;

    // class under test
    private SavingsBO savingsAccount;
    private SavingsAccountBuilder savingsAccountBuilder;

    // collaborators
    private SavingsOfferingBO savingsProduct;

    @Mock
    private SavingsTransactionActivityHelper savingsTransactionActivityHelper;

    @Mock
    private AccountPaymentEntity accountPayment;

    @Mock
    private PersonnelBO savingsOfficer;

    @Mock
    private CustomerBO payingCustomer;

    @Mock
    private CustomerBO savingsAccountCustomer;

    @Mock
    private CustomerPersistence customerDao;

    @Mock
    private SavingsTrxnDetailEntity savingsTrxnDetail;
    
    @Mock
    private SavingsActivityEntity savingsActivityDetail;

    @BeforeClass
    public static void setupMifosLoggerDueToUseOfStaticClientRules() {
        defaultCurrency = TestUtils.RUPEE;
        Money.setDefaultCurrency(defaultCurrency);
    }

    @Before
    public void setupForEachTest() {
        savingsAccountBuilder = new SavingsAccountBuilder().withCustomer(savingsAccountCustomer).withTransactionHelper(
                savingsTransactionActivityHelper).withCustomerDao(customerDao).withSavingsOfficer(savingsOfficer);
    }

    @Test(expected = AccountException.class)
    public void throwsAccountExceptionWhenInsufficientFundsInAccount() throws AccountException {

        // setup
        final Money zero = new Money(defaultCurrency);
        savingsAccount = savingsAccountBuilder.withBalanceOf(zero).build();
        final Money amountToWithdraw = new Money(TestUtils.getCurrency(), "100.0");

        // stubbing
        when(accountPayment.getAmount()).thenReturn(amountToWithdraw);

        // exercise test
        savingsAccount.withdraw(accountPayment, payingCustomer);
    }

    @Test(expected = AccountException.class)
    public void throwsAccountExceptionWhenWithdrawalAmountExceedsMaxWithdrawalAllowedBySavingsProduct()
            throws AccountException {

        // setup
        savingsProduct = new SavingsProductBuilder().withMaxWithdrawalAmount(new Money(TestUtils.getCurrency(), "50.0")).buildForUnitTests();
        savingsAccount = savingsAccountBuilder.withSavingsProduct(savingsProduct).withBalanceOf(new Money(TestUtils.getCurrency(), "80.0"))
                .build();
        final Money amountToWithdraw = new Money(TestUtils.getCurrency(), "75.0");

        // stubbing
        when(accountPayment.getAmount()).thenReturn(amountToWithdraw);

        // exercise test
        savingsAccount.withdraw(accountPayment, payingCustomer);
    }

    @Test
    public void accountIsAlwaysSetToActiveWhenAWithdrawalIsMade() throws AccountException {

        // setup
        savingsProduct = new SavingsProductBuilder().withMaxWithdrawalAmount(new Money(TestUtils.getCurrency(), "50.0")).buildForUnitTests();
        savingsAccount = savingsAccountBuilder.withSavingsProduct(savingsProduct).withBalanceOf(new Money(TestUtils.getCurrency(), "80.0"))
                .build();
        final Money amountToWithdraw = new Money(TestUtils.getCurrency(), "25.0");

        // stubbing
        when(accountPayment.getAmount()).thenReturn(amountToWithdraw);

        // exercise test
        savingsAccount.withdraw(accountPayment, payingCustomer);

        // verification
        assertThat(savingsAccount.getAccountState().getId(), is(AccountState.SAVINGS_ACTIVE.getValue()));
    }

    @Test
    public void savingsBalanceIsDecrementedByTotalAmountWithdrawn() throws AccountException {

        // setup
        final Money startingBalance = new Money(TestUtils.getCurrency(), "80.0");
        savingsProduct = new SavingsProductBuilder().withMaxWithdrawalAmount(new Money(TestUtils.getCurrency(), "50.0")).buildForUnitTests();
        savingsAccount = savingsAccountBuilder.withSavingsProduct(savingsProduct).withBalanceOf(startingBalance)
                .build();
        final Money amountToWithdraw = new Money(TestUtils.getCurrency(), "25.0");

        // stubbing
        when(accountPayment.getAmount()).thenReturn(amountToWithdraw);

        // exercise test
        savingsAccount.withdraw(accountPayment, payingCustomer);

        // verification
        assertThat(savingsAccount.getSavingsBalance(), is(startingBalance.subtract(amountToWithdraw)));
    }

    @Test
    public void savingsPerformanceWithdrawalsIsIncrementedByTotalAmountWithdrawn() throws AccountException {

        // setup
        final Money startingBalance = new Money(TestUtils.getCurrency(), "80.0");
        savingsProduct = new SavingsProductBuilder().withMaxWithdrawalAmount(new Money(TestUtils.getCurrency(), "50.0")).buildForUnitTests();
        savingsAccount = savingsAccountBuilder.withSavingsProduct(savingsProduct).withBalanceOf(startingBalance)
                .build();
        final Money amountToWithdraw = new Money(TestUtils.getCurrency(), "25.0");

        // stubbing
        when(accountPayment.getAmount()).thenReturn(amountToWithdraw);

        // exercise test
        savingsAccount.withdraw(accountPayment, payingCustomer);

        // verification
        assertThat(savingsAccount.getSavingsPerformance().getTotalWithdrawals(),is(amountToWithdraw));
    }
    
    @Test
    public void whenSingleWithdrawalThenOneSavingsActivityIsCreated() throws AccountException {

        // setup
        final Money startingBalance = new Money(TestUtils.getCurrency(), "80.0");
        savingsProduct = new SavingsProductBuilder().withMaxWithdrawalAmount(new Money(TestUtils.getCurrency(), "50.0")).buildForUnitTests();
        savingsAccount = savingsAccountBuilder.withSavingsProduct(savingsProduct).withBalanceOf(startingBalance)
                .build();
        final Money amountToWithdraw = new Money(TestUtils.getCurrency(), "25.0");

        // stubbing
        when(accountPayment.getAmount()).thenReturn(amountToWithdraw);
        when(savingsTransactionActivityHelper.createSavingsActivityForWithdrawal(accountPayment, startingBalance,
                savingsAccount)).thenReturn(savingsActivityDetail);

        // exercise test
        savingsAccount.withdraw(accountPayment, payingCustomer);

        // verification
        assertThat(savingsAccount.getSavingsActivityDetails().size(), is(1));
    }

    @Test
    public void whenSingleWithdrawalThenOneSavingsTrxnDetailIsAddedToAccountPayment() throws AccountException {

        // setup
        final Money startingBalance = new Money(TestUtils.getCurrency(), "80.0");
        savingsProduct = new SavingsProductBuilder().withMaxWithdrawalAmount(new Money(TestUtils.getCurrency(), "50.0")).buildForUnitTests();
        savingsAccount = savingsAccountBuilder.withSavingsProduct(savingsProduct).withBalanceOf(startingBalance)
                .build();
        final Money amountToWithdraw = new Money(TestUtils.getCurrency(), "25.0");

        // stubbing
        when(accountPayment.getAmount()).thenReturn(amountToWithdraw);
        when(
                savingsTransactionActivityHelper.createSavingsTrxnForWithdrawal(accountPayment, amountToWithdraw,
                        payingCustomer, startingBalance.subtract(amountToWithdraw))).thenReturn(savingsTrxnDetail);

        // exercise test
        savingsAccount.withdraw(accountPayment, payingCustomer);

        // verification
        verify(accountPayment).addAccountTrxn(savingsTrxnDetail);
    }
}
