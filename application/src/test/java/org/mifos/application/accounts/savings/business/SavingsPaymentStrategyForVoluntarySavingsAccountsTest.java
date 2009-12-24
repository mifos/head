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

package org.mifos.application.accounts.savings.business;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.collectionsheet.persistence.SavingsScheduleBuilder;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * I test {@link SavingsPaymentStrategyImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SavingsPaymentStrategyForVoluntarySavingsAccountsTest {

    private static final Short defaultCurrencyId = Short.valueOf("2");
    private static MifosCurrency defaultCurrency;

    // class under test
    private SavingsPaymentStrategy paymentStrategy;

    // collaborators
    @Mock
    private SavingsTransactionActivityHelper savingsTransactionActivityHelper;

    @Mock
    private AccountPaymentEntity accountPayment;

    @Mock
    private CustomerBO payingCustomer;

    @Mock
    private SavingsBO savingsAccount;

    @Mock
    private SavingsTrxnDetailEntity savingsTrxnDetail;
    
    @BeforeClass
    public static void setupMifosLoggerDueToUseOfStaticClientRules() {
        MifosLogManager.configureLogging();
        defaultCurrency = TestUtils.RUPEE;
        Money.setDefaultCurrency(defaultCurrency);
    }

    @Before
    public void setupForEachTest() {
        paymentStrategy = new SavingsPaymentStrategyImpl(savingsTransactionActivityHelper);
        when(accountPayment.getAccount()).thenReturn(savingsAccount);
        when(savingsAccount.getCurrency()).thenReturn(defaultCurrency);
    }

    @Test
    public void whenNoUnpaidScheduledInstallmentsExistNoPaymentsAreMade() {

        // setup
        final Money balanceBeforeDeposit = new Money(TestUtils.getCurrency(), "0.0");
        final Money fullDepositAmount = new Money(TestUtils.getCurrency(), "100.0");
        final Date dateOfDeposit = new DateTime().toDate();

        final List<SavingsScheduleEntity> unpaidDepositsForPayingCustomer = Arrays.asList();

        // stubbing
        when(accountPayment.getAmount()).thenReturn(fullDepositAmount);
        when(accountPayment.getPaymentDate()).thenReturn(dateOfDeposit);

        // exercise test
        paymentStrategy.makeScheduledPayments(accountPayment, unpaidDepositsForPayingCustomer, payingCustomer,
                SavingsType.VOLUNTARY, balanceBeforeDeposit);

        // verification
        verify(accountPayment, times(0)).addAccountTrxn(any(AccountTrxnEntity.class));
    }

    @Test
    public void whenNoUnpaidScheduledInstallmentsExistTheFullAmountOfTheDepositIsReturned() {

        // setup
        final Money balanceBeforeDeposit = new Money(TestUtils.getCurrency(), "0.0");
        final Money fullDepositAmount = new Money(TestUtils.getCurrency(), "100.0");
        final Date dateOfDeposit = new DateTime().toDate();

        final List<SavingsScheduleEntity> unpaidDepositsForPayingCustomer = Arrays.asList();

        // stubbing
        when(accountPayment.getAmount()).thenReturn(fullDepositAmount);
        when(accountPayment.getPaymentDate()).thenReturn(dateOfDeposit);

        // exercise test
        final Money remainingAmount = paymentStrategy.makeScheduledPayments(accountPayment,
                unpaidDepositsForPayingCustomer, payingCustomer, SavingsType.VOLUNTARY, balanceBeforeDeposit);

        // verification
        assertThat(remainingAmount, is(fullDepositAmount));
    }

    @Test
    public void whenUnpaidScheduledInstallmentsExistAllAreMarkedAsPaid() {

        // setup
        final Money balanceBeforeDeposit = new Money(TestUtils.getCurrency(), "0.0");
        final Money fullDepositAmount = new Money(TestUtils.getCurrency(), "100.0");
        final Date dateOfDeposit = new DateTime().toDate();

        final SavingsScheduleEntity unpaidSaving1 = new SavingsScheduleBuilder().withInstallmentNumber(1).withAccount(
                savingsAccount).withCustomer(payingCustomer).build();
        final SavingsScheduleEntity unpaidSaving2 = new SavingsScheduleBuilder().withInstallmentNumber(2).withAccount(
                savingsAccount).withCustomer(payingCustomer).build();

        final List<SavingsScheduleEntity> unpaidDepositsForPayingCustomer = Arrays.asList(unpaidSaving1, unpaidSaving2);

        // stubbing
        when(accountPayment.getAmount()).thenReturn(fullDepositAmount);
        when(accountPayment.getPaymentDate()).thenReturn(dateOfDeposit);

        // exercise test
        paymentStrategy.makeScheduledPayments(accountPayment, unpaidDepositsForPayingCustomer, payingCustomer,
                SavingsType.VOLUNTARY, balanceBeforeDeposit);

        // verification
        assertThat(unpaidSaving1.isPaid(), is(true));
        assertThat(unpaidSaving2.isPaid(), is(true));
    }

    @Test
    public void whenUnpaidScheduledInstallmentsExistTheLastScheduledPaymentIsOnlyPaidOffTheRecommendedAmount() {

        // setup
        final Money balanceBeforeDeposit = new Money(TestUtils.getCurrency(), "0.0");
        final Money fullDepositAmount = new Money(TestUtils.getCurrency(), "100.0");
        final Money recommendedDepositDue = new Money(TestUtils.getCurrency(), "36.0");
        final Date dateOfDeposit = new DateTime().toDate();

        final SavingsScheduleEntity unpaidSaving1 = new SavingsScheduleBuilder().withInstallmentNumber(1).withAccount(
                savingsAccount).withCustomer(payingCustomer).build();
        final SavingsScheduleEntity unpaidSaving2 = new SavingsScheduleBuilder().withInstallmentNumber(2).withAccount(
                savingsAccount).withCustomer(payingCustomer).withDepositDue(recommendedDepositDue).build();

        final List<SavingsScheduleEntity> unpaidDepositsForPayingCustomer = Arrays.asList(unpaidSaving1, unpaidSaving2);

        // stubbing
        when(accountPayment.getAmount()).thenReturn(fullDepositAmount);
        when(accountPayment.getPaymentDate()).thenReturn(dateOfDeposit);

        // exercise test
        paymentStrategy.makeScheduledPayments(accountPayment, unpaidDepositsForPayingCustomer, payingCustomer,
                SavingsType.VOLUNTARY, balanceBeforeDeposit);

        // verification
        assertThat(unpaidSaving1.getDepositPaid().getAmountDoubleValue(), is(Double.valueOf("0.0")));
        assertThat(unpaidSaving2.getDepositPaid().getAmountDoubleValue(), is(recommendedDepositDue
                .getAmountDoubleValue()));
    }

    @Test
    public void whenUnpaidScheduledInstallmentsExistAnyExcessPaymentOverDepositDueShouldBeReturnedAsRemainingAmount() {

        // setup
        final Money balanceBeforeDeposit = new Money(TestUtils.getCurrency(), "0.0");
        final Money fullDepositAmount = new Money(TestUtils.getCurrency(), "100.0");
        final Money recommendedDepositDue = new Money(TestUtils.getCurrency(), "36.0");
        final Date dateOfDeposit = new DateTime().toDate();

        final SavingsScheduleEntity unpaidSaving1 = new SavingsScheduleBuilder().withInstallmentNumber(1).withAccount(
                savingsAccount).withCustomer(payingCustomer).build();
        final SavingsScheduleEntity unpaidSaving2 = new SavingsScheduleBuilder().withInstallmentNumber(2).withAccount(
                savingsAccount).withCustomer(payingCustomer).withDepositDue(recommendedDepositDue).build();

        final List<SavingsScheduleEntity> unpaidDepositsForPayingCustomer = Arrays.asList(unpaidSaving1, unpaidSaving2);

        // stubbing
        when(accountPayment.getAmount()).thenReturn(fullDepositAmount);
        when(accountPayment.getPaymentDate()).thenReturn(dateOfDeposit);

        // exercise test
        final Money remainingAmount = paymentStrategy.makeScheduledPayments(accountPayment,
                unpaidDepositsForPayingCustomer, payingCustomer, SavingsType.VOLUNTARY, balanceBeforeDeposit);

        // verification
        assertThat(remainingAmount.getAmountDoubleValue(), is(fullDepositAmount.subtract(recommendedDepositDue)
                .getAmountDoubleValue()));
    }

    @Test
    public void whenUnpaidScheduledInstallmentsExistTheLastScheduledPaymentDateIsUpdated() {

        // setup
        final Money balanceBeforeDeposit = new Money(TestUtils.getCurrency(), "0.0");
        final Money fullDepositAmount = new Money(TestUtils.getCurrency(), "100.0");
        final Date dateOfDeposit = new DateTime().toDate();

        final SavingsScheduleEntity unpaidSaving1 = new SavingsScheduleBuilder().withInstallmentNumber(1).withAccount(
                savingsAccount).withCustomer(payingCustomer).build();
        final SavingsScheduleEntity unpaidSaving2 = new SavingsScheduleBuilder().withInstallmentNumber(2).withAccount(
                savingsAccount).withCustomer(payingCustomer).build();

        final List<SavingsScheduleEntity> unpaidDepositsForPayingCustomer = Arrays.asList(unpaidSaving1, unpaidSaving2);

        // stubbing
        when(accountPayment.getAmount()).thenReturn(fullDepositAmount);
        when(accountPayment.getPaymentDate()).thenReturn(dateOfDeposit);

        // exercise test
        paymentStrategy.makeScheduledPayments(accountPayment, unpaidDepositsForPayingCustomer, payingCustomer,
                SavingsType.VOLUNTARY, balanceBeforeDeposit);

        // verification
        assertThat(unpaidSaving2.getPaymentDate(), is(dateOfDeposit));
    }
    

    @Test
    public void whenUnpaidScheduledInstallmentsExistASavingsTrxnDetailIsCreatedWithLatestBalance() {

        // setup
        final Money balanceBeforeDeposit = new Money(TestUtils.getCurrency(), "0.0");
        final Money fullDepositAmount = new Money(TestUtils.getCurrency(), "100.0");
        final Money recommendedDepositDue = new Money(TestUtils.getCurrency(), "36.0");
        final Date dateOfDeposit = new DateTime().toDate();

        final SavingsScheduleEntity unpaidSaving1 = new SavingsScheduleBuilder().withInstallmentNumber(1).withAccount(
                savingsAccount).withCustomer(payingCustomer).build();
        final SavingsScheduleEntity unpaidSaving2 = new SavingsScheduleBuilder().withInstallmentNumber(2).withAccount(
                savingsAccount).withCustomer(payingCustomer).withDepositDue(recommendedDepositDue).build();

        final List<SavingsScheduleEntity> unpaidDepositsForPayingCustomer = Arrays.asList(unpaidSaving1, unpaidSaving2);

        // stubbing
        when(accountPayment.getAmount()).thenReturn(fullDepositAmount);
        when(accountPayment.getPaymentDate()).thenReturn(dateOfDeposit);

        // exercise test
        paymentStrategy.makeScheduledPayments(accountPayment, unpaidDepositsForPayingCustomer, payingCustomer,
                SavingsType.VOLUNTARY, balanceBeforeDeposit);

        // verification
        verify(savingsTransactionActivityHelper, times(1)).createSavingsTrxnForDeposit(accountPayment,
                recommendedDepositDue, payingCustomer, unpaidSaving2,
                balanceBeforeDeposit.add(recommendedDepositDue));
    }
    
    @Test
    public void whenUnpaidScheduledInstallmentsExistTheCreatedSavingsTrxnDetailIsAddedToAccountPaymentTrxns() {

        // setup
        final Money balanceBeforeDeposit = new Money(TestUtils.getCurrency(), "0.0");
        final Money fullDepositAmount = new Money(TestUtils.getCurrency(), "100.0");
        final Money recommendedDepositDue = new Money(TestUtils.getCurrency(), "36.0");
        final Date dateOfDeposit = new DateTime().toDate();

        final SavingsScheduleEntity unpaidSaving1 = new SavingsScheduleBuilder().withInstallmentNumber(1).withAccount(
                savingsAccount).withCustomer(payingCustomer).build();
        final SavingsScheduleEntity unpaidSaving2 = new SavingsScheduleBuilder().withInstallmentNumber(2).withAccount(
                savingsAccount).withCustomer(payingCustomer).withDepositDue(recommendedDepositDue).build();

        final List<SavingsScheduleEntity> unpaidDepositsForPayingCustomer = Arrays.asList(unpaidSaving1, unpaidSaving2);

        // stubbing
        when(accountPayment.getAmount()).thenReturn(fullDepositAmount);
        when(accountPayment.getPaymentDate()).thenReturn(dateOfDeposit);
        when(savingsTransactionActivityHelper.createSavingsTrxnForDeposit(accountPayment, recommendedDepositDue,
                payingCustomer, unpaidSaving2, balanceBeforeDeposit.add(recommendedDepositDue)))
                .thenReturn(savingsTrxnDetail);
        
        // exercise test
        paymentStrategy.makeScheduledPayments(accountPayment, unpaidDepositsForPayingCustomer, payingCustomer,
                SavingsType.VOLUNTARY, balanceBeforeDeposit);

        // verification
        verify(accountPayment, times(1)).addAccountTrxn(savingsTrxnDetail);
    }
}
