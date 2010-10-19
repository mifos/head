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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.financial.business.FinancialActionTypeEntity;
import org.mifos.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsProductBuilder;
import org.mifos.accounts.savings.business.SavingsActivityEntity;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.accounts.savings.interest.schedule.InterestScheduledEvent;
import org.mifos.accounts.savings.interest.schedule.internal.MonthlyOnLastDayOfMonthInterestScheduledEvent;
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.collectionsheet.persistence.ClientBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.collectionsheet.persistence.SavingsAccountBuilder;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SavingsPostInterestTest {

    // class under test
    private SavingsBO savingsAccount;

    // collaborators
    @Mock
    private CustomerBO client;

    private SavingsOfferingBO savingsProduct;

    private static MifosCurrency defaultCurrency;

    @BeforeClass
    public static void setupMifosLoggerDueToUseOfStaticClientRules() {
        defaultCurrency = TestUtils.RUPEE;
        Money.setDefaultCurrency(defaultCurrency);

        FinancialActionTypeEntity financialAction = new FinancialActionTypeEntity();
        financialAction.setId(FinancialActionConstants.SAVINGS_INTERESTPOSTING.getValue());

        FinancialActionCache.addToCache(financialAction);
    }

    @Before
    public void setupForEachTest() {

        client = new ClientBuilder().active().buildForUnitTests();

        MeetingBO interestPostingMeeting = new MeetingBuilder().savingsInterestPostingSchedule()
                                                                          .monthly().every(1)
                                                                          .build();

//        MeetingBO interestCalculationMeeting = new MeetingBuilder().savingsInterestCalulationSchedule()
//                                                                              .monthly().every(1)
//                                                                              .build();

        savingsProduct = new SavingsProductBuilder().mandatory()
                                                                        .withMandatoryAmount("33.0")
                                                                        .appliesToClientsOnly()
                                                                        .withInterestPostingSchedule(interestPostingMeeting)
                                                                        .buildForUnitTests();
    }

    @Test
    public void whenPostingInterestSavingsAccountBalanceIsUpdatedWithAmountToBePosted() {

        // setup
        InterestScheduledEvent postingSchedule = new MonthlyOnLastDayOfMonthInterestScheduledEvent(1);

        Money interestToBePosted = TestUtils.createMoney("100");
        DateTime activationDate = new DateTime().withDate(2010, 7, 20);
        savingsAccount = new SavingsAccountBuilder().mandatory()
                                                    .active()
                                                    .withActivationDate(activationDate)
                                                    .withSavingsProduct(savingsProduct)
                                                    .withCustomer(client)
                                                    .withInterestToBePostedAmount(interestToBePosted)
                                                    .build();

        // pre verification
        assertThat(savingsAccount.getSavingsBalance(), is(TestUtils.createMoney("0")));

        // exercise
        savingsAccount.postInterest(postingSchedule);

        // verification
        assertThat(savingsAccount.getSavingsBalance(), is(TestUtils.createMoney("100")));
    }

    @Test
    public void whenPostingInterestSavingsPerformanceDetailsForInterestIsUpdatedWithAmountToBePosted() {

        // setup
        InterestScheduledEvent postingSchedule = new MonthlyOnLastDayOfMonthInterestScheduledEvent(1);

        Money interestToBePosted = TestUtils.createMoney("100");
        DateTime activationDate = new DateTime().withDate(2010, 7, 20);
        savingsAccount = new SavingsAccountBuilder().mandatory()
                                                    .active()
                                                    .withActivationDate(activationDate)
                                                    .withSavingsProduct(savingsProduct)
                                                    .withCustomer(client)
                                                    .withInterestToBePostedAmount(interestToBePosted)
                                                    .build();

        // pre verification
        assertThat(savingsAccount.getSavingsPerformance().getTotalInterestEarned(), is(TestUtils.createMoney("0")));

        // exercise
        savingsAccount.postInterest(postingSchedule);

        // verification
        assertThat(savingsAccount.getSavingsPerformance().getTotalInterestEarned(), is(TestUtils.createMoney("100")));
    }

    @Test
    public void whenPostingInterestASavingsAcitvityIsAddedForInterestPostingEvent() {

        // setup
        InterestScheduledEvent postingSchedule = new MonthlyOnLastDayOfMonthInterestScheduledEvent(1);

        Money interestToBePosted = TestUtils.createMoney("100");
        DateTime activationDate = new DateTime().withDate(2010, 7, 20);
        savingsAccount = new SavingsAccountBuilder().mandatory()
                                                    .active()
                                                    .withActivationDate(activationDate)
                                                    .withSavingsProduct(savingsProduct)
                                                    .withCustomer(client)
                                                    .withInterestToBePostedAmount(interestToBePosted)
                                                    .build();

        // pre verification
        List<SavingsActivityEntity> preSavingsActivityityDetails = new ArrayList<SavingsActivityEntity>(savingsAccount.getSavingsActivityDetails());
        assertThat(preSavingsActivityityDetails.size(), is(0));

        // exercise
        savingsAccount.postInterest(postingSchedule);

        // verification
        List<SavingsActivityEntity> savingsActivityityDetails = new ArrayList<SavingsActivityEntity>(savingsAccount.getSavingsActivityDetails());
        assertThat(savingsActivityityDetails.size(), is(1));
    }

    @Test
    public void whenPostingInterestASavingsAcitvityIsAddedWithCorrectDetails() {

        // setup
        InterestScheduledEvent postingSchedule = new MonthlyOnLastDayOfMonthInterestScheduledEvent(1);

        Money interestToBePosted = TestUtils.createMoney("100");
        DateTime activationDate = new DateTime().withDate(2010, 7, 20);
        DateTime nextInterestPostingDate = new DateTime().withDate(2010, 7, 31);
        savingsAccount = new SavingsAccountBuilder().mandatory()
                                                    .active()
                                                    .withActivationDate(activationDate)
                                                    .withSavingsProduct(savingsProduct)
                                                    .withCustomer(client)
                                                    .withInterestToBePostedAmount(interestToBePosted)
                                                    .build();

        // pre verification
        assertThat(new LocalDate(savingsAccount.getNextIntPostDate()), is(nextInterestPostingDate.toLocalDate()));

        // exercise
        savingsAccount.postInterest(postingSchedule);

        // verification
        List<SavingsActivityEntity> savingsActivityityDetails = new ArrayList<SavingsActivityEntity>(savingsAccount.getSavingsActivityDetails());
        SavingsActivityEntity interestPostingActivity = savingsActivityityDetails.get(0);
        assertThat(interestPostingActivity.getAccount(), is((AccountBO)savingsAccount));
        assertThat(interestPostingActivity.getActivity().getId(), is(AccountActionTypes.SAVINGS_INTEREST_POSTING.getValue()));
        assertThat(interestPostingActivity.getAmount(), is(interestToBePosted));
        assertThat(datePartOf(interestPostingActivity.getTrxnCreatedDate()), is(nextInterestPostingDate.toLocalDate()));
    }

    private LocalDate datePartOf(Timestamp timestamp) {
        return new LocalDate(timestamp);
    }

    @Test
    public void whenPostingInterestASingleAccountPaymentIsMade() {

        // setup
        InterestScheduledEvent postingSchedule = new MonthlyOnLastDayOfMonthInterestScheduledEvent(1);

        Money interestToBePosted = TestUtils.createMoney("100");
        DateTime activationDate = new DateTime().withDate(2010, 7, 20);
        DateTime nextInterestPostingDate = new DateTime().withDate(2010, 7, 31);
        savingsAccount = new SavingsAccountBuilder().mandatory()
                                                    .active()
                                                    .withActivationDate(activationDate)
                                                    .withSavingsProduct(savingsProduct)
                                                    .withCustomer(client)
                                                    .withInterestToBePostedAmount(interestToBePosted)
                                                    .build();

        // pre verification
        assertTrue(savingsAccount.getAccountPayments().isEmpty());

        // exercise
        savingsAccount.postInterest(postingSchedule);

        // verification

        assertFalse(savingsAccount.getAccountPayments().isEmpty());
        AccountPaymentEntity interestPostingPayment = savingsAccount.getAccountPayments().get(0);

        assertThat(interestPostingPayment.getAccount(), is((AccountBO)savingsAccount));
        assertThat(interestPostingPayment.getAmount(), is(interestToBePosted));
        assertThat(interestPostingPayment.getPaymentType().getId(), is(SavingsConstants.DEFAULT_PAYMENT_TYPE));
        assertThat(new LocalDate(interestPostingPayment.getPaymentDate()), is(nextInterestPostingDate.toLocalDate()));
    }

    @Test
    public void whenPostingInterestASingleAccountTransactionIsAssociatedWithAccountPayment() {

        // setup
        InterestScheduledEvent postingSchedule = new MonthlyOnLastDayOfMonthInterestScheduledEvent(1);

        Money interestToBePosted = TestUtils.createMoney("100");
        DateTime activationDate = new DateTime().withDate(2010, 7, 20);
        DateTime nextInterestPostingDate = new DateTime().withDate(2010, 7, 31);
        savingsAccount = new SavingsAccountBuilder().mandatory()
                                                    .active()
                                                    .withActivationDate(activationDate)
                                                    .withSavingsProduct(savingsProduct)
                                                    .withCustomer(client)
                                                    .withInterestToBePostedAmount(interestToBePosted)
                                                    .build();

        // pre verification
        assertTrue(savingsAccount.getAccountPayments().isEmpty());

        // exercise
        savingsAccount.postInterest(postingSchedule);

        // verification


        AccountPaymentEntity interestPostingPayment = savingsAccount.getAccountPayments().get(0);
        assertFalse(interestPostingPayment.getAccountTrxns().isEmpty());

        List<AccountTrxnEntity> accountTransactions = new ArrayList<AccountTrxnEntity>(interestPostingPayment.getAccountTrxns());
        SavingsTrxnDetailEntity interestPostingTransaction = (SavingsTrxnDetailEntity) accountTransactions.get(0);

        assertThat(interestPostingTransaction.getAccount(), is((AccountBO)savingsAccount));
        assertThat(interestPostingTransaction.getAmount(), is(interestToBePosted));
        assertThat(interestPostingTransaction.getInterestAmount(), is(interestToBePosted));
        assertThat(interestPostingTransaction.getAccountActionEntity().getId(), is(AccountActionTypes.SAVINGS_INTEREST_POSTING.getValue()));
        assertThat(new LocalDate(interestPostingTransaction.getActionDate()), is(nextInterestPostingDate.toLocalDate()));
        assertThat(new LocalDate(interestPostingTransaction.getDueDate()), is(today()));
        assertThat(datePartOf(interestPostingTransaction.getTrxnCreatedDate()), is(today()));
    }

    private LocalDate today() {
        return new LocalDate();
    }

    @Test
    public void whenPostingInterestLastInterestPostingDateIsPopulated() {

        // setup
        InterestScheduledEvent postingSchedule = new MonthlyOnLastDayOfMonthInterestScheduledEvent(1);

        Money interestToBePosted = TestUtils.createMoney("100");
        DateTime activationDate = new DateTime().withDate(2010, 7, 20);
        DateTime nextInterestPostingDate = new DateTime().withDate(2010, 7, 31);
        savingsAccount = new SavingsAccountBuilder().mandatory()
                                                    .active()
                                                    .withActivationDate(activationDate)
                                                    .withSavingsProduct(savingsProduct)
                                                    .withCustomer(client)
                                                    .withInterestToBePostedAmount(interestToBePosted)
                                                    .build();

        // pre verification
        assertThat(savingsAccount.getLastIntPostDate(), is(nullValue()));

        // exercise
        savingsAccount.postInterest(postingSchedule);

        // verification
        assertThat(new LocalDate(savingsAccount.getLastIntPostDate()), is(nextInterestPostingDate.toLocalDate()));
    }

    @Test
    public void whenPostingInterestNextInterestPostingDateIsPopulatedWithNextPostingDate() {

        // setup
        InterestScheduledEvent postingSchedule = new MonthlyOnLastDayOfMonthInterestScheduledEvent(1);

        Money interestToBePosted = TestUtils.createMoney("100");
        DateTime activationDate = new DateTime().withDate(2010, 7, 20);
        DateTime nextInterestPostingDate = new DateTime().withDate(2010, 7, 31);
        savingsAccount = new SavingsAccountBuilder().mandatory()
                                                    .active()
                                                    .withActivationDate(activationDate)
                                                    .withSavingsProduct(savingsProduct)
                                                    .withCustomer(client)
                                                    .withInterestToBePostedAmount(interestToBePosted)
                                                    .build();

        // pre verification
        assertThat(new LocalDate(savingsAccount.getNextIntPostDate()), is(nextInterestPostingDate.toLocalDate()));

        // exercise
        savingsAccount.postInterest(postingSchedule);

        // verification
        assertThat(new LocalDate(savingsAccount.getNextIntPostDate()), is(endOfMonthAfter(nextInterestPostingDate)));
    }

    private LocalDate endOfMonthAfter(DateTime currentDate) {
        return new LocalDate(currentDate.plusMonths(1).dayOfMonth().withMaximumValue());
    }


    @Test
    public void whenPostingInterestInterestToBePostedIsResetToZero() {

        // setup
        InterestScheduledEvent postingSchedule = new MonthlyOnLastDayOfMonthInterestScheduledEvent(1);

        Money interestToBePosted = TestUtils.createMoney("100");
        DateTime activationDate = new DateTime().withDate(2010, 7, 20);
        savingsAccount = new SavingsAccountBuilder().mandatory()
                                                    .active()
                                                    .withActivationDate(activationDate)
                                                    .withSavingsProduct(savingsProduct)
                                                    .withCustomer(client)
                                                    .withInterestToBePostedAmount(interestToBePosted)
                                                    .build();

        // pre verification
        assertThat(savingsAccount.getInterestToBePosted(), is(interestToBePosted));

        // exercise
        savingsAccount.postInterest(postingSchedule);

        // verification
        assertThat(savingsAccount.getInterestToBePosted(), is(TestUtils.createMoney("0")));
    }
}