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

package org.mifos.accounts.financial.business.service.activity.accountingentry;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.Iterator;

import org.joda.time.DateMidnight;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountActionEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.financial.business.COABO;
import org.mifos.accounts.financial.business.FinancialActionTypeEntity;
import org.mifos.accounts.financial.business.FinancialTransactionBO;
import org.mifos.accounts.financial.business.GLCategoryType;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.financial.business.service.FinancialBusinessService;
import org.mifos.accounts.financial.business.service.activity.SavingsAdjustmentFinancialActivity;
import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsTypeEntity;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.accounts.savings.util.helpers.SavingsHelper;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * I test {@link SavingsAdjustmentAccountingEntry}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SavingsAdjustmentAccountingEntryTest extends BaseAccountingEntryTestCase {

    @Mock
    SavingsAdjustmentFinancialActivity mockedFinancialActivity;
    @Mock
    SavingsTrxnDetailEntity mockedIncomingTransaction;
    @Mock
    SavingsBO mockedSavingsBO;
    @Mock
    SavingsOfferingBO mockedSavingsOffering;
    @Mock
    SavingsTypeEntity mockedSavingsType;
    @Mock
    AccountPaymentEntity mockedAccountPayment;
    @Mock
    AccountTrxnEntity mockedLastTransaction;
    @Mock
    AccountActionEntity mockedAccountAction;
    @Mock
    GLCodeEntity mockedBankGLCode;
    @Mock
    GLCodeEntity mockedSavingsDepositGLCode;
    @Mock
    FinancialBusinessService mockedFinancialBusinessService;
    @Mock
    COABO mockedSavingsGLCategory;
    @Mock
    COABO mockedBankGLCategory;
    @Mock
    COABO mockedBankGLAccount;
    @Mock
    COABO mockedSavingsGLAccount;
    @Mock
    FinancialActionTypeEntity mockedFinancialAction;
    @Mock
    SavingsHelper mockedSavingsHelper;
    @Mock
    PersonnelBO mockedTransactionCreator;

    // Test parameters set up the same for all tests

    private Money withdrawalAmount;
    private Money depositAmount;
    private MifosCurrency currency;
    private DateMidnight transactionActionDate;
    private DateMidnight transactionPostedDate;
    private String savingsDepositGLCodeString;
    private String bankGLCodeString;
    private String commentString;
    private short expectedAccountingUpdatedFlag;

    @Before
    public void setupForAllTests() {

        // Refresh mocks before each test
        mockedFinancialActivity = mock(SavingsAdjustmentFinancialActivity.class);
        mockedIncomingTransaction = mock(SavingsTrxnDetailEntity.class);
        mockedSavingsBO = mock(SavingsBO.class);
        mockedSavingsOffering = mock(SavingsOfferingBO.class);
        mockedSavingsType = mock(SavingsTypeEntity.class);
        mockedAccountPayment = mock(AccountPaymentEntity.class);
        mockedLastTransaction = mock(AccountTrxnEntity.class);
        mockedAccountAction = mock(AccountActionEntity.class);
        mockedBankGLCode = mock(GLCodeEntity.class);
        mockedSavingsDepositGLCode = mock(GLCodeEntity.class);
        mockedFinancialBusinessService = mock(FinancialBusinessService.class);
        mockedSavingsGLCategory = mock(COABO.class);
        mockedBankGLCategory = mock(COABO.class);
        mockedBankGLAccount = mock(COABO.class);
        mockedSavingsGLAccount = mock(COABO.class);
        mockedFinancialAction = mock(FinancialActionTypeEntity.class);
        mockedSavingsHelper = mock(SavingsHelper.class);

        // Hard-coded values that are just passed through and thus not relevant to what's being tested

        currency = TestUtils.RUPEE;
        withdrawalAmount = new Money(currency, "20");
        depositAmount = new Money(currency, "30");
        transactionActionDate = new DateMidnight(2009, 9, 9);
        transactionPostedDate = new DateMidnight(2009, 1, 1);
        commentString = "test comment";
        expectedAccountingUpdatedFlag = (short) 1; // Always set to 1

        /*
         * The method under test creates a set with two transactions, a transaction on the savings account and a
         * transaction on the bank's account, each of which is verified differently. In order to know which is which,
         * the set is converted to a list that is sorted on GL code.
         */
        bankGLCodeString = "1"; // trans on asset acccount sorts first
        savingsDepositGLCodeString = "2"; // trans on savings account sorts second
    }

    private void setUpOneTest(SavingsType savingsType, AccountActionTypes accountActionType,
            GLCategoryType bankCategoryType, GLCategoryType savingsCategoryType,
            FinancialConstants debitOrCreditToBankGLAccount, FinancialActionConstants financialAction)
            throws FinancialException {

        when(mockedFinancialActivity.getAccountTrxn()).thenReturn(mockedIncomingTransaction);

        when(mockedIncomingTransaction.getAccount()).thenReturn(mockedSavingsBO);
        when(mockedIncomingTransaction.getWithdrawlAmount()).thenReturn(withdrawalAmount);
        when(mockedIncomingTransaction.getDepositAmount()).thenReturn(depositAmount);
        when(mockedIncomingTransaction.getActionDate()).thenReturn(transactionActionDate.toDate());
        when(mockedIncomingTransaction.getTrxnCreatedDate()).thenReturn(
                new Timestamp(transactionPostedDate.getMillis()));
        when(mockedIncomingTransaction.getPersonnel()).thenReturn(mockedTransactionCreator);
        when(mockedIncomingTransaction.getComments()).thenReturn(commentString);

        when(mockedSavingsBO.getSavingsOffering()).thenReturn(mockedSavingsOffering);
        when(mockedSavingsBO.getSavingsType()).thenReturn(mockedSavingsType);
        when(mockedSavingsOffering.getDepositGLCode()).thenReturn(mockedSavingsDepositGLCode);

        when(mockedSavingsDepositGLCode.getGlcode()).thenReturn(savingsDepositGLCodeString);

        when(mockedSavingsGLAccount.getGlCode()).thenReturn(savingsDepositGLCodeString);
        when(mockedSavingsGLAccount.getAccountName()).thenReturn("Clients Accounts");
        when(mockedSavingsGLAccount.getTopLevelCategoryType()).thenReturn(savingsCategoryType);

        when(mockedBankGLAccount.getAssociatedGlcode()).thenReturn(mockedBankGLCode);
        when(mockedBankGLAccount.getGlCode()).thenReturn(bankGLCodeString);
        when(mockedBankGLAccount.getTopLevelCategoryType()).thenReturn(bankCategoryType);

        when(mockedBankGLCode.getGlcode()).thenReturn(bankGLCodeString);

        when(mockedFinancialBusinessService.getGlAccount(savingsDepositGLCodeString))
                .thenReturn(mockedSavingsGLAccount);
        when(mockedFinancialBusinessService.getGlAccount(bankGLCodeString)).thenReturn(mockedBankGLAccount);
        when(mockedFinancialBusinessService.getFinancialAction(financialAction)).thenReturn(mockedFinancialAction);

        if (debitOrCreditToBankGLAccount.equals(FinancialConstants.DEBIT)) {
            when(mockedFinancialAction.getApplicableDebitCharts()).thenReturn(setWith(mockedBankGLAccount));
        } else {
            when(mockedFinancialAction.getApplicableCreditCharts()).thenReturn(setWith(mockedBankGLAccount));
        }

        when(mockedSavingsType.getId()).thenReturn(savingsType.getValue());

        when(mockedSavingsHelper.getPaymentActionType((AccountPaymentEntity) any())).thenReturn(
                accountActionType.getValue());
    }

    @Test
    public void testBuildAccountEntryAdjustSavingsWithdrawalForMandatorySavings() throws FinancialException {

        runOneTest(SavingsType.MANDATORY, AccountActionTypes.SAVINGS_WITHDRAWAL, GLCategoryType.ASSET,
                GLCategoryType.LIABILITY, FinancialActionConstants.MANDATORYWITHDRAWAL_ADJUSTMENT,
                FinancialConstants.DEBIT, FinancialConstants.CREDIT, withdrawalAmount);
    }

    @Test
    public void testBuildAccountEntryAdjustSavingsWithdrawalForVoluntarySavings() throws FinancialException {

        runOneTest(SavingsType.VOLUNTARY, AccountActionTypes.SAVINGS_WITHDRAWAL, GLCategoryType.ASSET,
                GLCategoryType.LIABILITY, FinancialActionConstants.VOLUNTORYWITHDRAWAL_ADJUSTMENT,
                FinancialConstants.DEBIT, FinancialConstants.CREDIT, withdrawalAmount);
    }

    @Test
    public void testBuildAccountEntryAdjustSavingsDepositForMandatorySavings() throws FinancialException {

        runOneTest(SavingsType.MANDATORY, AccountActionTypes.SAVINGS_DEPOSIT, GLCategoryType.ASSET,
                GLCategoryType.LIABILITY, FinancialActionConstants.MANDATORYDEPOSIT_ADJUSTMENT,
                FinancialConstants.CREDIT, FinancialConstants.DEBIT, depositAmount);
    }

    @Test
    public void testBuildAccountEntryAdjustSavingsDepositForVoluntarySavings() throws FinancialException {

        runOneTest(SavingsType.VOLUNTARY, AccountActionTypes.SAVINGS_DEPOSIT, GLCategoryType.ASSET,
                GLCategoryType.LIABILITY, FinancialActionConstants.VOLUNTORYDEPOSIT_ADJUSTMENT,
                FinancialConstants.CREDIT, FinancialConstants.DEBIT, depositAmount);
    }

    @Test(expected = FinancialException.class)
    public void testReThrowsFinancialExceptionAdjustSavingsWithdrawalForMandatorySavings() throws FinancialException {

        runOneTestMockFinancialBusinessServiceThrowsException(SavingsType.MANDATORY,
                AccountActionTypes.SAVINGS_WITHDRAWAL, GLCategoryType.ASSET, GLCategoryType.LIABILITY,
                FinancialActionConstants.MANDATORYWITHDRAWAL_ADJUSTMENT, FinancialConstants.DEBIT);
    }

    @Test(expected = FinancialException.class)
    public void testReThrowsFinancialExceptionAdjustSavingsWithdrawalForVoluntarySavings() throws FinancialException {

        runOneTestMockFinancialBusinessServiceThrowsException(SavingsType.VOLUNTARY,
                AccountActionTypes.SAVINGS_WITHDRAWAL, GLCategoryType.ASSET, GLCategoryType.LIABILITY,
                FinancialActionConstants.VOLUNTORYWITHDRAWAL_ADJUSTMENT, FinancialConstants.DEBIT);
    }

    @Test(expected = FinancialException.class)
    public void testReThrowsFinancialExceptionAdjustSavingsDepositForMandatorySavings() throws FinancialException {

        runOneTestMockFinancialBusinessServiceThrowsException(SavingsType.MANDATORY,
                AccountActionTypes.SAVINGS_DEPOSIT, GLCategoryType.ASSET, GLCategoryType.LIABILITY,
                FinancialActionConstants.MANDATORYDEPOSIT_ADJUSTMENT, FinancialConstants.CREDIT);
    }

    @Test(expected = FinancialException.class)
    public void testReThrowsFinancialExceptionAdjustSavingsDepositForVoluntarySavings() throws FinancialException {

        runOneTestMockFinancialBusinessServiceThrowsException(SavingsType.VOLUNTARY,
                AccountActionTypes.SAVINGS_DEPOSIT, GLCategoryType.ASSET, GLCategoryType.LIABILITY,
                FinancialActionConstants.VOLUNTORYDEPOSIT_ADJUSTMENT, FinancialConstants.CREDIT);
    }

    private void runOneTest(SavingsType savingsType, AccountActionTypes accountActionType,
            GLCategoryType bankCategoryType, GLCategoryType savingsCategoryType,
            FinancialActionConstants financialAction, FinancialConstants bankDebitCredit,
            FinancialConstants savingsDebitCredit, Money amount) throws FinancialException {

        /*
         * Test-specific settings
         */

        setUpOneTest(savingsType, accountActionType, bankCategoryType, savingsCategoryType, bankDebitCredit,
                financialAction);

        doBuild(new SavingsAdjustmentAccountingEntry(), mockedFinancialBusinessService, mockedSavingsHelper,
                mockedFinancialActivity);

        // verify the two transactions created
        verifyCreatedTransactions(amount, mockedFinancialActivity, mockedIncomingTransaction, mockedFinancialAction,
                mockedBankGLCode, mockedSavingsDepositGLCode, bankCategoryType, bankDebitCredit, savingsCategoryType,
                savingsDebitCredit);
    }

    private void doBuild(SavingsAdjustmentAccountingEntry entry, FinancialBusinessService financialBusinessService,
            SavingsHelper savingsHelper, SavingsAdjustmentFinancialActivity financialActivity)
            throws FinancialException {

        injectMocks(entry, financialBusinessService, savingsHelper);
        entry.buildAccountEntryForAction(financialActivity);
    }

    private void injectMocks(SavingsAdjustmentAccountingEntry entry, FinancialBusinessService financialBusinessService,
            SavingsHelper savingsHelper) {
        entry.setFinancialBusinessService(financialBusinessService);
        entry.setSavingsHelper(savingsHelper);
    }

    private void runOneTestMockFinancialBusinessServiceThrowsException(SavingsType savingsType,
            AccountActionTypes accountActionType, GLCategoryType bankCategoryType, GLCategoryType savingsCategoryType,
            FinancialActionConstants financialAction, FinancialConstants bankDebitCredit) throws FinancialException {

        /*
         * Test-specific settings
         */

        setUpOneTest(savingsType, accountActionType, bankCategoryType, savingsCategoryType, bankDebitCredit,
                financialAction);

        SavingsAdjustmentAccountingEntry entry = new SavingsAdjustmentAccountingEntry();

        when(mockedFinancialBusinessService.getFinancialAction(financialAction)).thenThrow(
                new FinancialException("a FinancialException"));

        doBuild(entry, mockedFinancialBusinessService, mockedSavingsHelper, mockedFinancialActivity);
    }

    private void verifyCreatedTransactions(Money transactionAmount,
            SavingsAdjustmentFinancialActivity mockedFinancialActivity,
            SavingsTrxnDetailEntity mockedIncomingTransaction, FinancialActionTypeEntity mockedFinancialAction,
            GLCodeEntity mockedBankGLCode, GLCodeEntity mockedSavingsDepositGLCode, GLCategoryType bankCategoryType,
            FinancialConstants bankDebitCredit, GLCategoryType savingsCategoryType,
            FinancialConstants savingsDebitCredit) {

        Iterator<FinancialTransactionBO> it = getIteratorOnSortedTransactions(mockedFinancialActivity, 2);

        /*
         * Verify the post to the bank's asset account
         */
        verifyCreatedFinancialTransaction(it.next(), mockedIncomingTransaction, null, mockedFinancialAction,
                mockedBankGLCode, transactionActionDate.toDate(), mockedTransactionCreator,
                expectedAccountingUpdatedFlag, transactionAmount, commentString, bankDebitCredit.getValue(), transactionPostedDate.toDate());

        /*
         * Verify the post to the savings liability account
         */
        verifyCreatedFinancialTransaction(it.next(), mockedIncomingTransaction, null, mockedFinancialAction,
                mockedSavingsDepositGLCode, transactionActionDate.toDate(), mockedTransactionCreator,
                expectedAccountingUpdatedFlag, transactionAmount, commentString, savingsDebitCredit.getValue(), transactionPostedDate
                        .toDate());

    }
}
