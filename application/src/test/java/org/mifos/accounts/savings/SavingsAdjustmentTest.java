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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.financial.business.COABO;
import org.mifos.accounts.financial.business.FinancialActionTypeEntity;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.financial.util.helpers.ChartOfAccountsCache;
import org.mifos.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.accounts.financial.util.helpers.FinancialRules;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsProductBuilder;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.application.collectionsheet.persistence.ClientBuilder;
import org.mifos.application.collectionsheet.persistence.SavingsAccountBuilder;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.business.CustomerBO;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SavingsAdjustmentTest {

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

        // FIXME - see how this can be made part of builder process to hide complexity.
        // Set up GLCode, chart of accounts cache and financial Rules and action caches so deposits on account are handled when building account.
        Short glcodeId = Short.valueOf("1");
        String glcode = "123456";
        GLCodeEntity glCodeEntity = new GLCodeEntity(glcodeId, glcode);

        COABO coabo = new COABO("testAccountName", glCodeEntity);
        ChartOfAccountsCache chartOfAccountsCache = new ChartOfAccountsCache();
        chartOfAccountsCache.add(coabo);

        Map<FinancialActionConstants, String> actionToCreditAccount = new HashMap<FinancialActionConstants, String>();
        actionToCreditAccount.put(FinancialActionConstants.MANDATORYDEPOSIT, glcode);
        actionToCreditAccount.put(FinancialActionConstants.MANDATORYWITHDRAWAL, glcode);

        FinancialRules.getInstance().setActionToCreditAccount(actionToCreditAccount);

        Map<FinancialActionConstants, String> actionToDebitAccount = new HashMap<FinancialActionConstants, String>();
        actionToDebitAccount.put(FinancialActionConstants.MANDATORYDEPOSIT, glcode);
        actionToDebitAccount.put(FinancialActionConstants.MANDATORYWITHDRAWAL, glcode);

        FinancialRules.getInstance().setActionToDebitAccount(actionToDebitAccount);

        FinancialActionTypeEntity depositAction = new FinancialActionTypeEntity();
        depositAction.setId(FinancialActionConstants.MANDATORYDEPOSIT.getValue());

        FinancialActionTypeEntity withdrawalAction = new FinancialActionTypeEntity();
        withdrawalAction.setId(FinancialActionConstants.MANDATORYWITHDRAWAL.getValue());

        FinancialActionCache.addToCache(depositAction);
        FinancialActionCache.addToCache(withdrawalAction);
    }

    @Before
    public void setupForEachTest() {

        client = new ClientBuilder().active().buildForUnitTests();

        savingsProduct = new SavingsProductBuilder().mandatory()
                                                                        .withMandatoryAmount("33.0")
                                                                        .appliesToClientsOnly()
                                                                        .buildForUnitTests();
    }

    @Test
    public void cannotAdjustLastTransactionOfAccountThatIsNotInActiveOrInactiveState() {

        Money amountAdjustedTo = TestUtils.createMoney("25");
        savingsAccount = new SavingsAccountBuilder().mandatory()
                                                    .asPendingApproval()
                                                    .withSavingsProduct(savingsProduct)
                                                    .withCustomer(client)
                                                    .build();

        // exercise test
        boolean result = savingsAccount.isAdjustPossibleOnLastTrxn(amountAdjustedTo);

        // verification
        assertFalse(result);
    }

    @Test
    public void cannotAdjustLastTransactionThatIsNotADepositOrWithdrawal() {

        Money amountAdjustedTo = TestUtils.createMoney("25");
        savingsAccount = new SavingsAccountBuilder().mandatory()
                                                    .active()
                                                    .withSavingsProduct(savingsProduct)
                                                    .withCustomer(client)
                                                    .build();

        // exercise test
        boolean result = savingsAccount.isAdjustPossibleOnLastTrxn(amountAdjustedTo);

        // verification
        assertFalse(result);
    }

    @Test
    public void canAdjustLastTransactionThatIsADeposit() {

        Money amountAdjustedTo = TestUtils.createMoney("25");
        savingsAccount = new SavingsAccountBuilder().mandatory()
                                                    .active()
                                                    .withSavingsProduct(savingsProduct)
                                                    .withCustomer(client)
                                                    .withDepositOf("15")
                                                    .build();

        // exercise test
        boolean result = savingsAccount.isAdjustPossibleOnLastTrxn(amountAdjustedTo);

        // verification
        assertTrue(result);
    }

    @Test
    public void cannotAdjustLastTransactionToSameMonetaryAmount() {

        Money amountAdjustedTo = TestUtils.createMoney("15");
        savingsAccount = new SavingsAccountBuilder().mandatory()
                                                    .active()
                                                    .withSavingsProduct(savingsProduct)
                                                    .withCustomer(client)
                                                    .withDepositOf("15")
                                                    .build();

        // exercise test
        boolean result = savingsAccount.isAdjustPossibleOnLastTrxn(amountAdjustedTo);

        // verification
        assertFalse(result);
    }

    @Test
    public void cannotAdjustLastTransactionWhenItsAWithdrawalThatExceedsAccountBalance() {

        savingsProduct = new SavingsProductBuilder().mandatory()
                                                    .withMandatoryAmount("33.0")
                                                    .withMaxWithdrawalAmount(TestUtils.createMoney("600"))
                                                    .appliesToClientsOnly()
                                                    .buildForUnitTests();

        savingsAccount = new SavingsAccountBuilder().mandatory()
                                                    .active()
                                                    .withSavingsProduct(savingsProduct)
                                                    .withCustomer(client)
                                                    .withBalanceOf(TestUtils.createMoney("100"))
                                                    .withWithdrawalOf("15")
                                                    .build();

        Money amountGreaterThanSavingsBalance = savingsAccount.getSavingsBalance().add(TestUtils.createMoney("20"));

        // exercise test
        boolean result = savingsAccount.isAdjustPossibleOnLastTrxn(amountGreaterThanSavingsBalance);

        // verification
        assertFalse(result);
    }

    @Test
    public void cannotAdjustLastTransactionWhenItsAWithdrawalThatExceedsSavingsProductMaxWithdrawalAmount() {

        savingsProduct = new SavingsProductBuilder().mandatory()
                                                    .withMandatoryAmount("33.0")
                                                    .withMaxWithdrawalAmount(TestUtils.createMoney("50"))
                                                    .appliesToClientsOnly()
                                                    .buildForUnitTests();

        savingsAccount = new SavingsAccountBuilder().mandatory()
                                                    .active()
                                                    .withSavingsProduct(savingsProduct)
                                                    .withCustomer(client)
                                                    .withBalanceOf(TestUtils.createMoney("100"))
                                                    .withWithdrawalOf("15")
                                                    .build();

        Money withdrawalAdjustment = TestUtils.createMoney("60");

        // exercise test
        boolean result = savingsAccount.isAdjustPossibleOnLastTrxn(withdrawalAdjustment);

        // verification
        assertFalse(result);
    }

    @Test
    public void canAdjustLastTransactionThatIsAWithdrawal() {

        savingsProduct = new SavingsProductBuilder().mandatory()
                                                    .withMandatoryAmount("33.0")
                                                    .withMaxWithdrawalAmount(TestUtils.createMoney("50"))
                                                    .appliesToClientsOnly()
                                                    .buildForUnitTests();

        savingsAccount = new SavingsAccountBuilder().mandatory()
                                                    .active()
                                                    .withSavingsProduct(savingsProduct)
                                                    .withCustomer(client)
                                                    .withBalanceOf(TestUtils.createMoney("100"))
                                                    .withWithdrawalOf("15")
                                                    .build();

        Money withdrawalAdjustment = TestUtils.createMoney("20");

        // exercise test
        boolean result = savingsAccount.isAdjustPossibleOnLastTrxn(withdrawalAdjustment);

        // verification
        assertTrue(result);
    }
}