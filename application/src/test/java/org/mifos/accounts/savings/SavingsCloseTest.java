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

package org.mifos.accounts.savings;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountNotesEntity;
import org.mifos.accounts.business.AccountNotesEntityBuilder;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountPaymentEntityBuilder;
import org.mifos.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.accounts.financial.business.COABO;
import org.mifos.accounts.financial.business.FinancialActionTypeEntity;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.financial.util.helpers.ChartOfAccountsCache;
import org.mifos.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.accounts.financial.util.helpers.FinancialRules;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.domain.builders.ClientBuilder;
import org.mifos.domain.builders.PersonnelBuilder;
import org.mifos.domain.builders.SavingsAccountBuilder;
import org.mifos.domain.builders.SavingsProductBuilder;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SavingsCloseTest {

    // class under test
    private SavingsBO savingsAccount;

    // collaborators
    @Mock
    private CustomerBO client;

    private SavingsOfferingBO savingsProduct;

    private static MifosCurrency defaultCurrency;

    @BeforeClass
    public static void setupChartOfAccountsAndFinancialTransactions() {
        defaultCurrency = TestUtils.RUPEE;
        Money.setDefaultCurrency(defaultCurrency);

        Short glcodeId = Short.valueOf("1");
        String glcode = "123456";
        GLCodeEntity glCodeEntity = new GLCodeEntity(glcodeId, glcode);

        COABO coabo = new COABO("testAccountName", glCodeEntity);
        ChartOfAccountsCache chartOfAccountsCache = new ChartOfAccountsCache();
        chartOfAccountsCache.clear();
        chartOfAccountsCache.add(coabo);

        Map<FinancialActionConstants, String> actionToCreditAccount = new HashMap<FinancialActionConstants, String>();
        actionToCreditAccount.put(FinancialActionConstants.SAVINGS_INTERESTPOSTING, glcode);
        actionToCreditAccount.put(FinancialActionConstants.MANDATORYDEPOSIT, glcode);
        actionToCreditAccount.put(FinancialActionConstants.MANDATORYWITHDRAWAL, glcode);
        actionToCreditAccount.put(FinancialActionConstants.MANDATORYDEPOSIT_ADJUSTMENT, glcode);
        actionToCreditAccount.put(FinancialActionConstants.MANDATORYWITHDRAWAL_ADJUSTMENT, glcode);
        actionToCreditAccount.put(FinancialActionConstants.ROUNDING, glcode);

        FinancialRules.getInstance().setActionToCreditAccount(actionToCreditAccount);

        Map<FinancialActionConstants, String> actionToDebitAccount = new HashMap<FinancialActionConstants, String>();
        actionToDebitAccount.put(FinancialActionConstants.SAVINGS_INTERESTPOSTING, glcode);
        actionToDebitAccount.put(FinancialActionConstants.MANDATORYDEPOSIT, glcode);
        actionToDebitAccount.put(FinancialActionConstants.MANDATORYWITHDRAWAL, glcode);
        actionToDebitAccount.put(FinancialActionConstants.MANDATORYDEPOSIT_ADJUSTMENT, glcode);
        actionToDebitAccount.put(FinancialActionConstants.MANDATORYWITHDRAWAL_ADJUSTMENT, glcode);
        actionToDebitAccount.put(FinancialActionConstants.ROUNDING, glcode);

        FinancialRules.getInstance().setActionToDebitAccount(actionToDebitAccount);

        FinancialActionTypeEntity roundingAction = new FinancialActionTypeEntity();
        roundingAction.setId(FinancialActionConstants.ROUNDING.getValue());

        FinancialActionTypeEntity depositAction = new FinancialActionTypeEntity();
        depositAction.setId(FinancialActionConstants.MANDATORYDEPOSIT.getValue());

        FinancialActionTypeEntity withdrawalAction = new FinancialActionTypeEntity();
        withdrawalAction.setId(FinancialActionConstants.MANDATORYWITHDRAWAL.getValue());

        FinancialActionTypeEntity depositAdjustmentAction = new FinancialActionTypeEntity();
        depositAdjustmentAction.setId(FinancialActionConstants.MANDATORYDEPOSIT_ADJUSTMENT.getValue());

        FinancialActionTypeEntity withdrawalAdjustmentAction = new FinancialActionTypeEntity();
        withdrawalAdjustmentAction.setId(FinancialActionConstants.MANDATORYWITHDRAWAL_ADJUSTMENT.getValue());

        FinancialActionTypeEntity interestPostingAction = new FinancialActionTypeEntity();
        interestPostingAction.setId(FinancialActionConstants.SAVINGS_INTERESTPOSTING.getValue());

        FinancialActionCache.addToCache(depositAction);
        FinancialActionCache.addToCache(withdrawalAction);
        FinancialActionCache.addToCache(depositAdjustmentAction);
        FinancialActionCache.addToCache(withdrawalAdjustmentAction);
        FinancialActionCache.addToCache(interestPostingAction);
        FinancialActionCache.addToCache(roundingAction);
    }

    @Before
    public void setupForEachTest() {

        client = new ClientBuilder().active().buildForUnitTests();

        savingsProduct = new SavingsProductBuilder().mandatory().withMandatoryAmount("33.0")
                                                                .withMaxWithdrawalAmount(TestUtils.createMoney("400"))
                                                                .appliesToClientsOnly()
                                                                .buildForUnitTests();
    }

    @Test
    public void whenClosingAccountShouldSetStatusAsClosed() {

        Money remainingBalance = TestUtils.createMoney("100");

        savingsAccount = new SavingsAccountBuilder().active()
                                                    .withSavingsProduct(savingsProduct)
                                                    .withCustomer(client)
                                                    .withBalanceOf(remainingBalance)
                                                    .build();

        AccountPaymentEntity payment = new AccountPaymentEntityBuilder().with(savingsAccount).with(remainingBalance).build();
        AccountNotesEntity notes = new AccountNotesEntityBuilder().build();
        CustomerBO customer = new ClientBuilder().buildForUnitTests();
        PersonnelBO loggedInUser = new PersonnelBuilder().build();

        // exercise test
        savingsAccount.closeAccount(payment, notes, customer, loggedInUser);

        // verification
        assertTrue(savingsAccount.getAccountState().isInState(AccountState.SAVINGS_CLOSED));
    }

    @Test
    public void whenClosingAccountShouldCreateStatusChangeHistoryForClosure() {

        Money remainingBalance = TestUtils.createMoney("100");

        savingsAccount = new SavingsAccountBuilder().active()
                                                    .withSavingsProduct(savingsProduct)
                                                    .withCustomer(client)
                                                    .withBalanceOf(remainingBalance)
                                                    .build();

        AccountPaymentEntity payment = new AccountPaymentEntityBuilder().with(savingsAccount).with(remainingBalance).build();
        AccountNotesEntity notes = new AccountNotesEntityBuilder().build();
        CustomerBO customer = new ClientBuilder().buildForUnitTests();
        PersonnelBO loggedInUser = new PersonnelBuilder().build();

        // pre verification
        assertThat(savingsAccount.getAccountStatusChangeHistory().size(), is(1));

        // exercise test
        savingsAccount.closeAccount(payment, notes, customer, loggedInUser);

        // verification
        assertThat(savingsAccount.getAccountStatusChangeHistory().size(), is(2));

        AccountStatusChangeHistoryEntity closure = savingsAccount.getAccountStatusChangeHistory().get(1);
        assertThat(closure.getAccount(), is((AccountBO)savingsAccount));
        assertTrue(closure.getOldStatus().isInState(AccountState.SAVINGS_ACTIVE));
        assertTrue(closure.getNewStatus().isInState(AccountState.SAVINGS_CLOSED));
    }

    @Test
    public void whenClosingAccountShouldSetClosedDate() {

        Money remainingBalance = TestUtils.createMoney("100");

        savingsAccount = new SavingsAccountBuilder().active()
                                                    .withSavingsProduct(savingsProduct)
                                                    .withCustomer(client)
                                                    .withBalanceOf(remainingBalance)
                                                    .build();

        AccountPaymentEntity payment = new AccountPaymentEntityBuilder().with(savingsAccount).with(remainingBalance).build();
        AccountNotesEntity notes = new AccountNotesEntityBuilder().build();
        CustomerBO customer = new ClientBuilder().buildForUnitTests();
        PersonnelBO loggedInUser = new PersonnelBuilder().build();

        // exercise test
        savingsAccount.closeAccount(payment, notes, customer, loggedInUser);

        // verification
        assertThat(new LocalDate(savingsAccount.getClosedDate()), is(new LocalDate()));
    }

    @Test
    public void whenClosingAccountShouldWithdrawRemainingBalanceOnAccount() {

        Money remainingBalance = TestUtils.createMoney("100");

        savingsAccount = new SavingsAccountBuilder().active()
                                                    .withSavingsProduct(savingsProduct)
                                                    .withCustomer(client)
                                                    .withBalanceOf(remainingBalance)
                                                    .build();

        AccountPaymentEntity payment = new AccountPaymentEntityBuilder().with(savingsAccount).with(remainingBalance).build();
        AccountNotesEntity notes = new AccountNotesEntityBuilder().build();
        CustomerBO customer = new ClientBuilder().buildForUnitTests();
        PersonnelBO loggedInUser = new PersonnelBuilder().build();

        // pre verification
        assertTrue(savingsAccount.getAccountPayments().isEmpty());

        // exercise test
        savingsAccount.closeAccount(payment, notes, customer, loggedInUser);

        // verification
        assertFalse(savingsAccount.getAccountPayments().isEmpty());
        AccountPaymentEntity withdrawalPayment = savingsAccount.getAccountPayments().get(0);
        assertThat(withdrawalPayment.getAmount(), is(TestUtils.createMoney("100")));
    }

    @Test
    public void whenClosingAccountShouldPostInterestWhenRemainingSavingsBalanceIsLessThanAmountBeingWithdrawn() {

        Money remainingBalance = TestUtils.createMoney("100");

        savingsAccount = new SavingsAccountBuilder().active()
                                                    .withSavingsProduct(savingsProduct)
                                                    .withCustomer(client)
                                                    .withBalanceOf(remainingBalance)
                                                    .build();

        Money withdrawalAtCloseAmount = TestUtils.createMoney("105.56");
        AccountPaymentEntity payment = new AccountPaymentEntityBuilder().with(savingsAccount).with(withdrawalAtCloseAmount).build();
        AccountNotesEntity notes = new AccountNotesEntityBuilder().build();
        CustomerBO customer = new ClientBuilder().buildForUnitTests();
        PersonnelBO loggedInUser = new PersonnelBuilder().build();

        // pre verification
        assertTrue(savingsAccount.getAccountPayments().isEmpty());

        // exercise test
        savingsAccount.closeAccount(payment, notes, customer, loggedInUser);

        // verification
        assertFalse(savingsAccount.getAccountPayments().isEmpty());
        assertThat(savingsAccount.getAccountPayments().size(), is(2));

        AccountPaymentEntity interestPayment = savingsAccount.getAccountPayments().get(0);
        assertThat(interestPayment.getAmount(), is(TestUtils.createMoney("5.56")));

        AccountPaymentEntity withdrawalPayment = savingsAccount.getAccountPayments().get(1);
        assertThat(withdrawalPayment.getAmount(), is(TestUtils.createMoney("105.56")));
    }
}