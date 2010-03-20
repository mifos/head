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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.Iterator;

import org.joda.time.DateMidnight;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.financial.business.COABO;
import org.mifos.accounts.financial.business.FinancialActionBO;
import org.mifos.accounts.financial.business.FinancialTransactionBO;
import org.mifos.accounts.financial.business.GLCategoryType;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.financial.business.service.FinancialBusinessService;
import org.mifos.accounts.financial.business.service.activity.SavingsDepositFinancialActivity;
import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsTypeEntity;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * I test {@link DepositAccountingEntry}.
 */
@RunWith(MockitoJUnitRunner.class)
public class DepositAccountingEntryTest extends BaseAccountingEntryTestCase {

    private String depositAmount;
    private MifosCurrency currency;
    private DateMidnight transactionActionDate;
    private DateMidnight transactionPostedDate;
    private PersonnelBO transactionCreator;
    private String savingsDepositGLCode;
    private String bankGLCode;
    private String comments;

    @Mock
    SavingsDepositFinancialActivity mockedFinancialActivity;
    @Mock
    SavingsTrxnDetailEntity mockedIncomingTransaction;
    @Mock
    SavingsBO mockedSavingsBO;
    @Mock
    SavingsOfferingBO mockedSavingsOffering;
    @Mock
    SavingsTypeEntity mockedSavingsType;
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
    FinancialActionBO mockedFinancialAction;

    @Before
    public void setupForAllTests() {

        currency = TestUtils.RUPEE;
        transactionActionDate = new DateMidnight(2009, 9, 9);
        transactionPostedDate = new DateMidnight(2009, 1, 1);
        depositAmount = "30";
        bankGLCode = "1"; // trans on asset acccount sorts first in returned
        // list of transactions
        savingsDepositGLCode = "2"; // trans on savings account sorts second in
        // returned list of transactions
        comments = "test comment";

        mockedFinancialActivity = mock(SavingsDepositFinancialActivity.class);
        mockedIncomingTransaction = mock(SavingsTrxnDetailEntity.class);
        mockedSavingsBO = mock(SavingsBO.class);
        mockedSavingsOffering = mock(SavingsOfferingBO.class);
        mockedSavingsType = mock(SavingsTypeEntity.class);
        mockedBankGLCode = mock(GLCodeEntity.class);
        mockedSavingsDepositGLCode = mock(GLCodeEntity.class);
        mockedFinancialBusinessService = mock(FinancialBusinessService.class);
        mockedSavingsGLCategory = mock(COABO.class);
        mockedBankGLCategory = mock(COABO.class);
        mockedBankGLAccount = mock(COABO.class);
        mockedSavingsGLAccount = mock(COABO.class);
        mockedFinancialAction = mock(FinancialActionBO.class);

    }

    private void setupForOneTest(SavingsType savingsType, GLCategoryType bankCategoryType,
            GLCategoryType savingsCategoryType, FinancialActionConstants financialAction) throws FinancialException {

        when(mockedFinancialActivity.getAccountTrxn()).thenReturn(mockedIncomingTransaction);

        when(mockedIncomingTransaction.getAccount()).thenReturn(mockedSavingsBO);
        when(mockedIncomingTransaction.getDepositAmount()).thenReturn(new Money(currency, depositAmount));
        when(mockedIncomingTransaction.getActionDate()).thenReturn(transactionActionDate.toDate());
        when(mockedIncomingTransaction.getTrxnCreatedDate()).thenReturn(
                new Timestamp(transactionPostedDate.getMillis()));
        when(mockedIncomingTransaction.getPersonnel()).thenReturn(transactionCreator);
        when(mockedIncomingTransaction.getComments()).thenReturn(comments);

        when(mockedSavingsBO.getSavingsOffering()).thenReturn(mockedSavingsOffering);

        when(mockedSavingsBO.getSavingsType()).thenReturn(mockedSavingsType);

        when(mockedSavingsOffering.getDepositGLCode()).thenReturn(mockedSavingsDepositGLCode);

        when(mockedSavingsDepositGLCode.getGlcode()).thenReturn(savingsDepositGLCode);

        when(mockedSavingsGLAccount.getGlCode()).thenReturn(savingsDepositGLCode);
        // when(mockedSavingsGLAccount.getCOAHead()) .thenReturn(mockedSavingsGLCategory);
        when(mockedSavingsGLAccount.getTopLevelCategoryType()).thenReturn(savingsCategoryType);
        when(mockedSavingsGLAccount.getAccountName()).thenReturn("Clients Accounts");

        // when(mockedBankGLAccount.getCOAHead()) .thenReturn(mockedBankGLCategory);
        when(mockedBankGLAccount.getTopLevelCategoryType()).thenReturn(bankCategoryType);
        when(mockedBankGLAccount.getAssociatedGlcode()).thenReturn(mockedBankGLCode);
        when(mockedBankGLAccount.getGlCode()).thenReturn(bankGLCode);
        when(mockedBankGLCode.getGlcode()).thenReturn(bankGLCode);

        when(mockedFinancialBusinessService.getGlAccount(savingsDepositGLCode)).thenReturn(mockedSavingsGLAccount);
        when(mockedFinancialBusinessService.getGlAccount(bankGLCode)).thenReturn(mockedBankGLAccount);
        when(mockedFinancialBusinessService.getFinancialAction(financialAction)).thenReturn(mockedFinancialAction);

        when(mockedFinancialAction.getApplicableDebitCharts()).thenReturn(setWith(mockedBankGLAccount));

        when(mockedSavingsType.getId()).thenReturn(savingsType.getValue());
        // when(mockedSavingsGLCategory.getCategoryType()).thenReturn(savingsCategoryType);
        // when(mockedBankGLCategory.getCategoryType()).thenReturn(bankCategoryType);
    }

    @Test
    public void testBuildAccountEntryDepositIntoMandatorySavings() throws FinancialException {

        SavingsType savingsType = SavingsType.MANDATORY;
        GLCategoryType bankCategoryType = GLCategoryType.ASSET;
        GLCategoryType savingsCategoryType = GLCategoryType.LIABILITY;
        FinancialActionConstants financialActionConstant = FinancialActionConstants.MANDATORYDEPOSIT;

        setupForOneTest(savingsType, bankCategoryType, savingsCategoryType, financialActionConstant);

        doBuild(new DepositAccountingEntry(), mockedFinancialBusinessService, mockedFinancialActivity);

        verifyCreatedTransactions(depositAmount, mockedFinancialActivity, mockedIncomingTransaction,
                mockedFinancialAction, mockedBankGLCode, mockedSavingsDepositGLCode, bankCategoryType,
                FinancialConstants.DEBIT, savingsCategoryType, FinancialConstants.CREDIT);
    }

    @Test
    public void testBuildAccountEntryDepositIntoVoluntarySavings() throws FinancialException {

        SavingsType savingsType = SavingsType.VOLUNTARY;
        GLCategoryType bankCategoryType = GLCategoryType.ASSET;
        GLCategoryType savingsCategoryType = GLCategoryType.LIABILITY;
        FinancialActionConstants financialActionConstant = FinancialActionConstants.VOLUNTORYDEPOSIT;

        setupForOneTest(savingsType, bankCategoryType, savingsCategoryType, financialActionConstant);

        doBuild(new DepositAccountingEntry(), mockedFinancialBusinessService, mockedFinancialActivity);

        // verify the two transactions created
        verifyCreatedTransactions(depositAmount, mockedFinancialActivity, mockedIncomingTransaction,
                mockedFinancialAction, mockedBankGLCode, mockedSavingsDepositGLCode, bankCategoryType,
                FinancialConstants.DEBIT, savingsCategoryType, FinancialConstants.CREDIT);

    }

    private void doBuild(DepositAccountingEntry entry, FinancialBusinessService financialBusinessService,
            SavingsDepositFinancialActivity financialActivity) throws FinancialException {

        injectMocks(entry, financialBusinessService);
        entry.buildAccountEntryForAction(financialActivity);
    }

    private void injectMocks(DepositAccountingEntry entry, FinancialBusinessService financialBusinessService) {
        entry.setFinancialBusinessService(financialBusinessService);
    }

    private void verifyCreatedTransactions(String transactionAmount,
            SavingsDepositFinancialActivity mockedFinancialActivity, SavingsTrxnDetailEntity mockedIncomingTransaction,
            FinancialActionBO mockedFinancialAction, GLCodeEntity mockedBankGLCode,
            GLCodeEntity mockedSavingsDepositGLCode, GLCategoryType bankCategoryType,
            FinancialConstants bankDebitCredit, GLCategoryType savingsCategoryType,
            FinancialConstants savingsDebitCredit) {

        Iterator<FinancialTransactionBO> it = getIteratorOnSortedTransactions(mockedFinancialActivity, 2);

        /*
         * Verify the post to the bank's asset account
         */
        verifyCreatedFinancialTransaction(it.next(), mockedIncomingTransaction, null, mockedFinancialAction,
                mockedBankGLCode, transactionActionDate.toDate(), transactionCreator, (short) 1, new Money(currency,
                        transactionAmount), comments, bankDebitCredit.getValue(), transactionPostedDate.toDate());

        /*
         * Verify the post to the savings liability account
         */
        verifyCreatedFinancialTransaction(it.next(), mockedIncomingTransaction, null, mockedFinancialAction,
                mockedSavingsDepositGLCode, transactionActionDate.toDate(), transactionCreator, (short) 1, new Money(
                        currency, transactionAmount), comments, savingsDebitCredit.getValue(), transactionPostedDate
                        .toDate());

    }

}
