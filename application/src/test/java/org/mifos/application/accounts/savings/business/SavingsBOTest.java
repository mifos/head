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
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.collectionsheet.persistence.SavingsAccountBuilder;
import org.mifos.application.collectionsheet.persistence.SavingsScheduleBuilder;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * I test {@link SavingsBO}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SavingsBOTest {

    private static final Short defaultCurrencyId = Short.valueOf("2");
    private static MifosCurrency defaultCurrency;

    // class under test
    private SavingsBO savingsAccount;
    private SavingsAccountBuilder savingsAccountBuilder;

    // collaborators
    @Mock
    private SavingsPaymentStrategy paymentStrategy;

    @Mock
    private SavingsTransactionActivityHelper savingsTransactionActivityHelper;

    @Mock
    private AccountPaymentEntity accountPayment;

    @Mock
    private AccountPaymentEntity secondAccountPayment;

    @Mock
    private PersonnelBO savingsOfficer;
    
    @Mock
    private PersonnelBO createdByUser;

    @Mock
    private CustomerBO payingCustomer;

    @Mock
    private CustomerBO savingsAccountCustomer;

    @Mock
    private CustomerPersistence customerDao;

    @Mock
    private SavingsActivityEntity savingsActivity;
    
    @Mock
    private SavingsActivityEntity secondSavingsActivity;
    
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
        savingsAccountBuilder = new SavingsAccountBuilder().withCustomer(savingsAccountCustomer).withPaymentStrategy(
                paymentStrategy).withTransactionHelper(savingsTransactionActivityHelper).withCustomerDao(customerDao)
                .withSavingsOfficer(savingsOfficer);
    }

    @Test
    public void accountIsAlwaysSetToActiveWhenADepositIsMade() throws AccountException {

        // setup
        final Money zero = new Money(defaultCurrency);
        savingsAccount = savingsAccountBuilder.withBalanceOf(zero).asInActive().build();
        final Money amountToDeposit = new Money(TestUtils.getCurrency(), "100.0");
        final Date dateOfDeposit = new DateTime().toDate();

        // stubbing
        when(accountPayment.getAmount()).thenReturn(amountToDeposit);
        when(accountPayment.getPaymentDate()).thenReturn(dateOfDeposit);

        // exercise test
        savingsAccount.deposit(accountPayment, payingCustomer);

        // verification
        assertThat(savingsAccount.getAccountState().getId(), is(AccountState.SAVINGS_ACTIVE.getValue()));
    }

    @Test
    public void savingsBalanceIsIncrementedByTotalAmountDeposited() throws AccountException {

        // setup
        final Money zero = new Money(defaultCurrency);
        savingsAccount = savingsAccountBuilder.withBalanceOf(zero).asInActive().build();
        final Money amountToDeposit = new Money(TestUtils.getCurrency(), "100.0");
        final Date dateOfDeposit = new DateTime().toDate();

        // stubbing
        when(accountPayment.getAmount()).thenReturn(amountToDeposit);
        when(accountPayment.getPaymentDate()).thenReturn(dateOfDeposit);

        // exercise test
        savingsAccount.deposit(accountPayment, payingCustomer);

        // verification
        assertThat(savingsAccount.getSavingsBalance().getAmountDoubleValue(),
                is(amountToDeposit.getAmountDoubleValue()));
    }

    @Test
    public void savingsPerformanceDepositsIsIncrementedByTotalAmountDeposited() throws AccountException {

        // setup
        final Money zero = new Money(defaultCurrency);
        savingsAccount = savingsAccountBuilder.withBalanceOf(zero).asInActive().build();
        final Money amountToDeposit = new Money(TestUtils.getCurrency(), "100.0");
        final Date dateOfDeposit = new DateTime().toDate();

        // stubbing
        when(accountPayment.getAmount()).thenReturn(amountToDeposit);
        when(accountPayment.getPaymentDate()).thenReturn(dateOfDeposit);

        // exercise test
        savingsAccount.deposit(accountPayment, payingCustomer);

        // verification
        assertThat(savingsAccount.getSavingsPerformance().getTotalDeposits().getAmountDoubleValue(), is(amountToDeposit
                .getAmountDoubleValue()));
    }

    @Test
    public void whenSingleDepositOneSavingsActivityRecordIsCreated() throws AccountException {

        // setup
        final Money zero = new Money(defaultCurrency);
        savingsAccount = savingsAccountBuilder.withBalanceOf(zero).build();
        final Money amountToDeposit = new Money(TestUtils.getCurrency(), "100.0");
        final Date dateOfDeposit = new DateTime().toDate();
        final Money savingsBalance = new Money(TestUtils.getCurrency(), "100.0");

        // stubbing
        when(accountPayment.getAmount()).thenReturn(amountToDeposit);
        when(accountPayment.getPaymentDate()).thenReturn(dateOfDeposit);
        when(
                savingsTransactionActivityHelper.createSavingsActivityForDeposit(savingsOfficer, amountToDeposit,
                        savingsBalance, dateOfDeposit, savingsAccount)).thenReturn(savingsActivity);

        // exercise test
        savingsAccount.deposit(accountPayment, payingCustomer);

        // verification
        assertThat(savingsAccount.getSavingsActivityDetails().size(), is(1));
    }

    @Test
    public void whenMultipleDepositsOneSavingsActivityRecordIsCreatedForEachDeposit() throws AccountException {

        // setup
        final Money zero = new Money(defaultCurrency);
        savingsAccount = savingsAccountBuilder.withBalanceOf(zero).withSavingsOfficer(savingsOfficer).build();
        final Money amountToDeposit = new Money(TestUtils.getCurrency(), "100.0");
        final Date dateOfDeposit = new DateTime().toDate();
        final Money savingsBalance = new Money(TestUtils.getCurrency(), "100.0");

        // stubbing
        when(accountPayment.getAmount()).thenReturn(amountToDeposit);
        when(accountPayment.getCreatedByUser()).thenReturn(createdByUser);
        when(accountPayment.getPaymentDate()).thenReturn(dateOfDeposit);
        when(
                savingsTransactionActivityHelper.createSavingsActivityForDeposit(createdByUser, amountToDeposit,
                        savingsBalance, dateOfDeposit, savingsAccount)).thenReturn(savingsActivity);
        
        when(secondAccountPayment.getAmount()).thenReturn(amountToDeposit);
        when(secondAccountPayment.getPaymentDate()).thenReturn(dateOfDeposit);
        when(
                savingsTransactionActivityHelper.createSavingsActivityForDeposit(createdByUser, amountToDeposit,
                        savingsBalance, dateOfDeposit, savingsAccount)).thenReturn(secondSavingsActivity);

        // exercise test
        savingsAccount.deposit(accountPayment, payingCustomer);
        savingsAccount.deposit(secondAccountPayment, payingCustomer);

        // verification
        assertThat(savingsAccount.getSavingsActivityDetails().size(), is(2));
    }

    @Test
    public void whenZeroIsDepositedThenNoPaymentsAreMade() throws AccountException {

        // setup
        savingsAccount = savingsAccountBuilder.build();
        final Money zero = new Money(defaultCurrency);

        // stubbing
        when(accountPayment.getAmount()).thenReturn(zero);

        // exercise test
        savingsAccount.deposit(accountPayment, payingCustomer);

        // verification
        assertThat(savingsAccount.getAccountPayments().size(), is(0));
    }

    @Test
    public void whenSingleDepositThenOnePaymentIsMade() throws AccountException {

        // setup
        final Money zero = new Money(defaultCurrency);
        savingsAccount = savingsAccountBuilder.withBalanceOf(zero).build();
        final Money amountToDeposit = new Money(TestUtils.getCurrency(), "100.0");
        final Date dateOfDeposit = new DateTime().toDate();

        // stubbing
        when(accountPayment.getAmount()).thenReturn(amountToDeposit);
        when(accountPayment.getPaymentDate()).thenReturn(dateOfDeposit);

        // exercise test
        savingsAccount.deposit(accountPayment, payingCustomer);

        // verification
        assertThat(savingsAccount.getAccountPayments().size(), is(1));
    }

    @Test
    public void whenMultipleDepositsThenOnePaymentIsMadeForEachDeposit() throws AccountException {

        // setup
        final Money zero = new Money(defaultCurrency);
        savingsAccount = savingsAccountBuilder.withBalanceOf(zero).build();
        final Money amountToDeposit = new Money(TestUtils.getCurrency(), "100.0");
        final Date dateOfDeposit = new DateTime().toDate();

        // stubbing
        when(accountPayment.getAmount()).thenReturn(amountToDeposit);
        when(accountPayment.getPaymentDate()).thenReturn(dateOfDeposit);
        when(secondAccountPayment.getAmount()).thenReturn(amountToDeposit);
        when(secondAccountPayment.getPaymentDate()).thenReturn(dateOfDeposit);

        // exercise test
        savingsAccount.deposit(accountPayment, payingCustomer);
        savingsAccount.deposit(secondAccountPayment, payingCustomer);

        // verification
        assertThat(savingsAccount.getAccountPayments().size(), is(2));
    }

    @Test
    public void shouldPayOffAnyPaymentsAssociatedWithPayingCustomerAndVoluntarySavingsAccount() throws AccountException {

        // setup
        savingsAccount = savingsAccountBuilder.build();
        final SavingsScheduleEntity unpaidSaving1 = new SavingsScheduleBuilder().withAccount(savingsAccount)
                .withCustomer(payingCustomer).build();

        final List<SavingsScheduleEntity> unpaidDepositsForPayingCustomer = Arrays.asList(unpaidSaving1);

        final Money zero = new Money(defaultCurrency);
        savingsAccount = savingsAccountBuilder.withBalanceOf(zero).voluntary().completeGroup().withPayments(
                unpaidDepositsForPayingCustomer).build();

        final Money amountToDeposit = new Money(TestUtils.getCurrency(), "100.0");
        final Date dateOfDeposit = new DateTime().toDate();

        // stubbing
        when(accountPayment.getAmount()).thenReturn(amountToDeposit);
        when(accountPayment.getPaymentDate()).thenReturn(dateOfDeposit);

        // exercise test
        savingsAccount.deposit(accountPayment, payingCustomer);

        // verification
        verify(paymentStrategy).makeScheduledPayments(accountPayment, unpaidDepositsForPayingCustomer, payingCustomer,
                SavingsType.VOLUNTARY, zero);
    }

    @Test
    public void shouldPayOffAnyPaymentsAssociatedWithPayingCustomerAndMandatorySavingsAccount() throws AccountException {

        // setup
        savingsAccount = savingsAccountBuilder.build();
        final SavingsScheduleEntity unpaidSaving1 = new SavingsScheduleBuilder().withAccount(savingsAccount)
                .withCustomer(payingCustomer).build();

        final List<SavingsScheduleEntity> unpaidDepositsForPayingCustomer = Arrays.asList(unpaidSaving1);

        final Money zero = new Money(defaultCurrency);
        savingsAccount = savingsAccountBuilder.withBalanceOf(zero).mandatory().withPayments(
                unpaidDepositsForPayingCustomer).build();

        final Money amountToDeposit = new Money(TestUtils.getCurrency(), "100.0");
        final Date dateOfDeposit = new DateTime().toDate();

        // stubbing
        when(accountPayment.getAmount()).thenReturn(amountToDeposit);
        when(accountPayment.getPaymentDate()).thenReturn(dateOfDeposit);

        // exercise test
        savingsAccount.deposit(accountPayment, payingCustomer);

        // verification
        verify(paymentStrategy).makeScheduledPayments(accountPayment, unpaidDepositsForPayingCustomer, payingCustomer,
                SavingsType.MANDATORY, zero);
    }

    @Test
    public void overpayingMandatoryOrVoluntaryDepositAmountCausesAnExcessTransactionToBeCreated()
            throws AccountException {

        // setup
        savingsAccount = savingsAccountBuilder.build();
        final SavingsScheduleEntity unpaidSaving1 = new SavingsScheduleBuilder().withAccount(savingsAccount)
                .withCustomer(payingCustomer).build();

        final List<SavingsScheduleEntity> unpaidDepositsForPayingCustomer = Arrays.asList(unpaidSaving1);

        final Money zero = new Money(defaultCurrency);
        savingsAccount = savingsAccountBuilder.withBalanceOf(zero).mandatory().withPayments(
                unpaidDepositsForPayingCustomer).build();

        final Money amountToDeposit = new Money(TestUtils.getCurrency(), "100.0");
        final Date dateOfDeposit = new DateTime().toDate();
        final Money excessDepositAmount = new Money(TestUtils.getCurrency(), "25.0");
        final Money expectedTotalBalance = amountToDeposit.add(zero);

        // stubbing
        when(accountPayment.getAmount()).thenReturn(amountToDeposit);
        when(accountPayment.getPaymentDate()).thenReturn(dateOfDeposit);
        when(
                paymentStrategy.makeScheduledPayments(accountPayment, unpaidDepositsForPayingCustomer, payingCustomer,
                        SavingsType.MANDATORY, zero)).thenReturn(excessDepositAmount);

        // exercise test
        savingsAccount.deposit(accountPayment, payingCustomer);

        // verification
        verify(this.savingsTransactionActivityHelper).createSavingsTrxnForDeposit(accountPayment, excessDepositAmount,
                payingCustomer, null, expectedTotalBalance);
    }
    
    @Test
    public void overpayingMandatoryOrVoluntaryDepositAmountResultsInASavingsTrxnDetailForExcessAmountToBeAddedToAccountPaymentTransactions()
            throws AccountException {

        // setup
        savingsAccount = savingsAccountBuilder.build();
        final SavingsScheduleEntity unpaidSaving1 = new SavingsScheduleBuilder().withAccount(savingsAccount)
                .withCustomer(payingCustomer).build();

        final List<SavingsScheduleEntity> unpaidDepositsForPayingCustomer = Arrays.asList(unpaidSaving1);

        final Money zero = new Money(defaultCurrency);
        savingsAccount = savingsAccountBuilder.withBalanceOf(zero).voluntary().completeGroup().withPayments(
                unpaidDepositsForPayingCustomer).build();

        final Money amountToDeposit = new Money(TestUtils.getCurrency(), "100.0");
        final Date dateOfDeposit = new DateTime().toDate();
        final Money excessDepositAmount = new Money(TestUtils.getCurrency(), "25.0");
        final Money expectedTotalBalance = amountToDeposit.add(zero);

        // stubbing
        when(accountPayment.getAmount()).thenReturn(amountToDeposit);
        when(accountPayment.getPaymentDate()).thenReturn(dateOfDeposit);
        when(
                paymentStrategy.makeScheduledPayments(accountPayment, unpaidDepositsForPayingCustomer, payingCustomer,
                        SavingsType.VOLUNTARY, zero)).thenReturn(excessDepositAmount);
        when(
                savingsTransactionActivityHelper.createSavingsTrxnForDeposit(accountPayment, excessDepositAmount,
                        payingCustomer, null, expectedTotalBalance)).thenReturn(savingsTrxnDetail);

        // exercise test
        savingsAccount.deposit(accountPayment, payingCustomer);

        // verification
        verify(accountPayment).addAccountTrxn(savingsTrxnDetail);
    }
}
