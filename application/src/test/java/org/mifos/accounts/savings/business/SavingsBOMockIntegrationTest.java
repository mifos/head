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

package org.mifos.accounts.savings.business;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.collectionsheet.persistence.SavingsAccountBuilder;
import org.mifos.application.collectionsheet.persistence.SavingsScheduleBuilder;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.calendar.DayOfWeek;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerLevelEntity;
import org.mifos.customers.business.CustomerMeetingEntity;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.domain.builders.HolidayBuilder;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * I test {@link SavingsBO}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SavingsBOMockIntegrationTest {

    private static MifosCurrency defaultCurrency;

    // class under test
    private SavingsBO savingsAccount;
    private SavingsAccountBuilder savingsAccountBuilder;
    private DateTimeService dateTimeService = new DateTimeService();
    private List<Days> allWorkingDays;
    private List<Holiday> emptyListOfHolidays;


    // collaborators
    @Mock private SavingsPaymentStrategy paymentStrategy;
    @Mock private SavingsTransactionActivityHelper savingsTransactionActivityHelper;
    @Mock private AccountPaymentEntity accountPayment;
    @Mock private AccountPaymentEntity secondAccountPayment;
    @Mock private PersonnelBO savingsOfficer;
    @Mock private PersonnelBO createdByUser;
    @Mock private CustomerBO payingCustomer;
    @Mock private MeetingBO defaultWeeklyCustomerMeeting;
    @Mock private CustomerMeetingEntity customerMeetingEntity;
    @Mock private CustomerLevelEntity customerLevelEntity;
    @Mock private CustomerBO savingsAccountCustomer;
    @Mock private CustomerPersistence customerDao;
    @Mock private SavingsActivityEntity savingsActivity;
    @Mock private SavingsActivityEntity secondSavingsActivity;
    @Mock private SavingsTrxnDetailEntity savingsTrxnDetail;

    @BeforeClass
    public static void setupMifosLoggerDueToUseOfStaticClientRules() {
        defaultCurrency = TestUtils.RUPEE;
        Money.setDefaultCurrency(defaultCurrency);
    }

    @Before
    public void setupForEachTest() {
        savingsAccountBuilder = new SavingsAccountBuilder().withCustomer(savingsAccountCustomer).withPaymentStrategy(
                paymentStrategy).withTransactionHelper(savingsTransactionActivityHelper).withCustomerDao(customerDao)
                .withSavingsOfficer(savingsOfficer);
        allWorkingDays = Arrays.asList(DayOfWeek.mondayAsDay(), DayOfWeek.tuesdayAsDay(), DayOfWeek.wednesdayAsDay(),
                DayOfWeek.thursdayAsDay(), DayOfWeek.fridayAsDay(), DayOfWeek.saturdayAsDay(), DayOfWeek.sundayAsDay());
        emptyListOfHolidays = new ArrayList<Holiday>();
        defaultWeeklyCustomerMeeting = new MeetingBuilder().every(1).weekly().startingFrom(new DateMidnight().toDate()).build();



    }

    @Test
    public void accountIsAlwaysSetToActiveWhenADepositIsMade() throws AccountException {

        // setup
        final Money zero = new Money(defaultCurrency);
        savingsAccount = savingsAccountBuilder.withBalanceOf(zero).asInActive().build();
        final Money amountToDeposit = new Money(TestUtils.RUPEE, "100.0");
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
        final Money amountToDeposit = new Money(TestUtils.RUPEE, "100.0");
        final Date dateOfDeposit = new DateTime().toDate();

        // stubbing
        when(accountPayment.getAmount()).thenReturn(amountToDeposit);
        when(accountPayment.getPaymentDate()).thenReturn(dateOfDeposit);

        // exercise test
        savingsAccount.deposit(accountPayment, payingCustomer);

        // verification
        assertThat(savingsAccount.getSavingsBalance(),is(amountToDeposit));
    }

    @Test
    public void savingsPerformanceDepositsIsIncrementedByTotalAmountDeposited() throws AccountException {

        // setup
        final Money zero = new Money(defaultCurrency);
        savingsAccount = savingsAccountBuilder.withBalanceOf(zero).asInActive().build();
        final Money amountToDeposit = new Money(TestUtils.RUPEE, "100.0");
        final Date dateOfDeposit = new DateTime().toDate();

        // stubbing
        when(accountPayment.getAmount()).thenReturn(amountToDeposit);
        when(accountPayment.getPaymentDate()).thenReturn(dateOfDeposit);

        // exercise test
        savingsAccount.deposit(accountPayment, payingCustomer);

        // verification
        assertThat(savingsAccount.getSavingsPerformance().getTotalDeposits(), is(amountToDeposit));
    }

    @Test
    public void whenSingleDepositOneSavingsActivityRecordIsCreated() throws AccountException {

        // setup
        final Money zero = new Money(defaultCurrency);
        savingsAccount = savingsAccountBuilder.withBalanceOf(zero).build();
        final Money amountToDeposit = new Money(TestUtils.RUPEE, "100.0");
        final Date dateOfDeposit = new DateTime().toDate();
        final Money savingsBalance = new Money(TestUtils.RUPEE, "100.0");

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
        final Money amountToDeposit = new Money(TestUtils.RUPEE, "100.0");
        final Date dateOfDeposit = new DateTime().toDate();
        final Money savingsBalance = new Money(TestUtils.RUPEE, "100.0");

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
        final Money amountToDeposit = new Money(TestUtils.RUPEE, "100.0");
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
        final Money amountToDeposit = new Money(TestUtils.RUPEE, "100.0");
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

        final Money amountToDeposit = new Money(TestUtils.RUPEE, "100.0");
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

        final Money amountToDeposit = new Money(TestUtils.RUPEE, "100.0");
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

        final Money amountToDeposit = new Money(TestUtils.RUPEE, "100.0");
        final Date dateOfDeposit = new DateTime().toDate();
        final Money excessDepositAmount = new Money(TestUtils.RUPEE, "25.0");
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

        final Money amountToDeposit = new Money(TestUtils.RUPEE, "100.0");
        final Date dateOfDeposit = new DateTime().toDate();
        final Money excessDepositAmount = new Money(TestUtils.RUPEE, "25.0");
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

    @Test
    public void depositScheduleIsGeneratedOnCreationNoHoliday() {

        //  setup
        when(savingsAccountCustomer.getCustomerMeeting()) .thenReturn(customerMeetingEntity);
        when(customerMeetingEntity.getMeeting())          .thenReturn(defaultWeeklyCustomerMeeting);
        when(savingsAccountCustomer.getCustomerLevel())   .thenReturn(customerLevelEntity);
        when(customerLevelEntity.getId())                 .thenReturn(CustomerLevel.CLIENT.getValue());

        // exercise test
        savingsAccount = savingsAccountBuilder.build();

        // verify
        List<SavingsScheduleEntity> sortedSavingsSchedules = getSortedSavingsScheduleEntities();
        assertThat(sortedSavingsSchedules.size(), is(10));
        DateTime installmentDate = new DateMidnight().toDateTime();
        for (SavingsScheduleEntity savingsScheduleEntity : sortedSavingsSchedules) {
            assertThat(savingsScheduleEntity.getActionDate(), is(installmentDate.toDate()));
            assertThat(savingsScheduleEntity.getDeposit(), is(new Money(TestUtils.RUPEE, "13.0")));
            assertThat(savingsScheduleEntity.getDepositPaid(), is(new Money(TestUtils.RUPEE, "0.0")));
            installmentDate = installmentDate.plusWeeks(1);
        }
    }

    @Test
    public void depositScheduleIsGeneratedOnCreationWithMoratoriumOnSecondScheduledDeposit() {

        // setup
        Holiday moratorium = new HolidayBuilder().from(new DateMidnight().toDateTime().plusWeeks(1))
                                                 .to(new DateMidnight().toDateTime().plusWeeks(1))
                                                 .withRepaymentMoratoriumRule()
                                                 .build();
        when(savingsAccountCustomer.getCustomerMeeting()) .thenReturn(customerMeetingEntity);
        when(customerMeetingEntity.getMeeting())          .thenReturn(defaultWeeklyCustomerMeeting);
        when(savingsAccountCustomer.getCustomerLevel())   .thenReturn(customerLevelEntity);
        when(customerLevelEntity.getId())                 .thenReturn(CustomerLevel.CLIENT.getValue());

        // exercise test
        savingsAccount = savingsAccountBuilder.with(moratorium).build();

        // verify
        List<SavingsScheduleEntity> sortedSavingsSchedules = getSortedSavingsScheduleEntities();
        assertThat(sortedSavingsSchedules.size(), is(10));
        DateTime installmentDate = new DateMidnight().toDateTime();
        int installmentId = 1;
        for (SavingsScheduleEntity savingsScheduleEntity : sortedSavingsSchedules) {
            if (installmentId < 2) {
                assertThat("Installment " + installmentId, savingsScheduleEntity.getActionDate(), is(installmentDate.toDate()));
            } else {
                // Second and later deposits are pushed out one week by the moratorium
                assertThat("Installment " + installmentId, savingsScheduleEntity.getActionDate(), is(installmentDate.plusWeeks(1).toDate()));
            }
            assertThat(savingsScheduleEntity.getDeposit(), is(new Money(TestUtils.RUPEE, "13.0")));
            assertThat(savingsScheduleEntity.getDepositPaid(), is(new Money(TestUtils.RUPEE, "0.0")));
            installmentDate = installmentDate.plusWeeks(1);
            installmentId++;
        }
    }

    @Test
    public void generateNextSetOfMeetingDatesNoHoliday() throws Exception {

        // setup
        when(savingsAccountCustomer.getCustomerMeeting()) .thenReturn(customerMeetingEntity);
        when(customerMeetingEntity.getMeeting())          .thenReturn(defaultWeeklyCustomerMeeting);
        when(savingsAccountCustomer.getCustomerLevel())   .thenReturn(customerLevelEntity);
        when(customerLevelEntity.getId())                 .thenReturn(CustomerLevel.CLIENT.getValue());

        // exercise test
        savingsAccount = savingsAccountBuilder.build();
        savingsAccount.generateNextSetOfMeetingDates(allWorkingDays, emptyListOfHolidays);

        // verify
        List<SavingsScheduleEntity> sortedSavingsSchedules = getSortedSavingsScheduleEntities();
        assertThat(sortedSavingsSchedules.size(), is(20));
         DateTime installmentDate = new DateMidnight().toDateTime();
        int installmentId = 1;
        for (SavingsScheduleEntity savingsScheduleEntity : getSortedSavingsScheduleEntities()) {
            assertThat(savingsScheduleEntity.getActionDate(), is(installmentDate.toDate()));
            assertThat(savingsScheduleEntity.getDeposit(), is(new Money(TestUtils.RUPEE, "13.0")));
            assertThat(savingsScheduleEntity.getDepositPaid(), is(new Money(TestUtils.RUPEE, "0.0")));
            installmentDate = installmentDate.plusWeeks(1);
            installmentId++;
        }
    }

    @Test
    public void generateNextSetOfMeetingDatesMoratoriumOn12thInstallment() throws Exception {

        // setup
        Holiday moratorium = new HolidayBuilder().from(new DateMidnight().toDateTime().plusWeeks(11))
                                                 .to(new DateMidnight().toDateTime().plusWeeks(11))
                                                 .withRepaymentMoratoriumRule()
                                                 .build();
        List<Holiday> withMoratorium = new ArrayList<Holiday>();
        withMoratorium.add(moratorium);
        when(savingsAccountCustomer.getCustomerMeeting()) .thenReturn(customerMeetingEntity);
        when(customerMeetingEntity.getMeeting())          .thenReturn(defaultWeeklyCustomerMeeting);
        when(savingsAccountCustomer.getCustomerLevel())   .thenReturn(customerLevelEntity);
        when(customerLevelEntity.getId())                 .thenReturn(CustomerLevel.CLIENT.getValue());

        // exercise test
        savingsAccount = savingsAccountBuilder.with(moratorium).build();
        savingsAccount.generateNextSetOfMeetingDates(allWorkingDays, withMoratorium);

        // verify
        List<SavingsScheduleEntity> sortedSavingsSchedules = getSortedSavingsScheduleEntities();
        assertThat(sortedSavingsSchedules.size(), is(20));
         DateTime installmentDate = new DateMidnight().toDateTime();
        int installmentId = 1;
        for (SavingsScheduleEntity savingsScheduleEntity : getSortedSavingsScheduleEntities()) {
            if (installmentId < 12) {
                assertThat("Installment " + installmentId, savingsScheduleEntity.getActionDate(), is(installmentDate.toDate()));
            } else {
                // Second and later deposits are pushed out one week by the moratorium
                assertThat("Installment " + installmentId, savingsScheduleEntity.getActionDate(), is(installmentDate.plusWeeks(1).toDate()));
            }
            assertThat(savingsScheduleEntity.getDeposit(), is(new Money(TestUtils.RUPEE, "13.0")));
            assertThat(savingsScheduleEntity.getDepositPaid(), is(new Money(TestUtils.RUPEE, "0.0")));
            installmentDate = installmentDate.plusWeeks(1);
            installmentId++;
        }
    }

    @Test
    public void generateNextSetOfMeetingDatesMoratoriumOn2ndInstallment() throws Exception {

        // setup
        Holiday moratorium = new HolidayBuilder().from(new DateMidnight().toDateTime().plusWeeks(1))
                                                 .to(new DateMidnight().toDateTime().plusWeeks(1))
                                                 .withRepaymentMoratoriumRule()
                                                 .build();
        List<Holiday> withMoratorium = new ArrayList<Holiday>();
        withMoratorium.add(moratorium);
        when(savingsAccountCustomer.getCustomerMeeting()) .thenReturn(customerMeetingEntity);
        when(customerMeetingEntity.getMeeting())          .thenReturn(defaultWeeklyCustomerMeeting);
        when(savingsAccountCustomer.getCustomerLevel())   .thenReturn(customerLevelEntity);
        when(customerLevelEntity.getId())                 .thenReturn(CustomerLevel.CLIENT.getValue());

        // exercise test
        savingsAccount = savingsAccountBuilder.with(moratorium).build();
        savingsAccount.generateNextSetOfMeetingDates(allWorkingDays, withMoratorium);

        // verify
        List<SavingsScheduleEntity> sortedSavingsSchedules = getSortedSavingsScheduleEntities();
        assertThat(sortedSavingsSchedules.size(), is(20));
         DateTime installmentDate = new DateMidnight().toDateTime();
        int installmentId = 1;
        for (SavingsScheduleEntity savingsScheduleEntity : getSortedSavingsScheduleEntities()) {
            if (installmentId < 2) {
                assertThat("Installment " + installmentId, savingsScheduleEntity.getActionDate(), is(installmentDate.toDate()));
            } else {
                // Second and later deposits are pushed out one week by the moratorium
                assertThat("Installment " + installmentId, savingsScheduleEntity.getActionDate(), is(installmentDate.plusWeeks(1).toDate()));
            }
            assertThat(savingsScheduleEntity.getDeposit(), is(new Money(TestUtils.RUPEE, "13.0")));
            assertThat(savingsScheduleEntity.getDepositPaid(), is(new Money(TestUtils.RUPEE, "0.0")));
            installmentDate = installmentDate.plusWeeks(1);
            installmentId++;
        }
    }

    private List<SavingsScheduleEntity> getSortedSavingsScheduleEntities () {
        List<SavingsScheduleEntity> sortedSavingsScheduleEntities = new ArrayList<SavingsScheduleEntity>();
        for (AccountActionDateEntity actionDateEntity : savingsAccount.getAccountActionDates()) {
            sortedSavingsScheduleEntities.add( (SavingsScheduleEntity) actionDateEntity);
        }
        Collections.sort(sortedSavingsScheduleEntities);
        return sortedSavingsScheduleEntities;
    }
}
