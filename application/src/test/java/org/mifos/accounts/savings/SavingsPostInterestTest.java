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

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.financial.business.FinancialActionTypeEntity;
import org.mifos.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsProductBuilder;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.interest.schedule.InterestScheduledEvent;
import org.mifos.accounts.savings.interest.schedule.internal.MonthlyOnLastDayOfMonthInterestScheduledEvent;
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

/**
 *
 */
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

        // exercise
        savingsAccount.postInterest(postingSchedule);

        // verification
        assertThat(savingsAccount.getSavingsBalance(), is(TestUtils.createMoney("100")));
    }
}
