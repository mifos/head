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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.financial.business.COABO;
import org.mifos.accounts.financial.business.FinancialActionBO;
import org.mifos.accounts.financial.business.FinancialTransactionBO;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.financial.business.service.activity.BaseFinancialActivity;
import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.util.helpers.Money;
import org.mockito.ArgumentCaptor;

public class BaseAccountingEntryTestCase {

    /**
     * Return an iterator on the list of TransactionBO objects created by an AccountEntry object, sorted by GLCode
     * string. First verify the correct number of transactions was created.
     *
     * @param mockedFinancialActivity
     *            a Mockito-mocked BaseFinancialActivity instance
     * @param expectedTransCount
     *            the size of the created list of FinancialTransactionBO instances
     */
    protected void executeBuildAccountingEntryForAction(BaseAccountingEntry accountingEntry,
            BaseFinancialActivity financialActivity) throws FinancialException {

        accountingEntry.buildAccountEntryForAction(financialActivity);
    }

    protected Iterator<FinancialTransactionBO> getIteratorOnSortedTransactions(
            BaseFinancialActivity mockedFinancialActivity, int expectedTransCount) {

        ArgumentCaptor<FinancialTransactionBO> transactionCaptor = ArgumentCaptor
                .forClass(FinancialTransactionBO.class);
        verify(mockedFinancialActivity, times(expectedTransCount)).addFinancialTransaction(transactionCaptor.capture());

        List<FinancialTransactionBO> capturedTransactions = transactionCaptor.getAllValues();

        /*
         * Sort by GLCode strings so "1" (asset account) should be first, followed by "2" (client savings)
         */
        Collections.sort(capturedTransactions, new GLCodeComparator());
        return capturedTransactions.iterator();
    }

    protected void verifyCreatedFinancialTransaction(FinancialTransactionBO actualTransaction,
            AccountTrxnEntity expectedAccountTrxn, FinancialTransactionBO expectedRelatedFinancialTrxn,
            FinancialActionBO expectedFinancialAction, GLCodeEntity expectedGlcode, Date expectedActionDate,
            PersonnelBO expectedPostedBy, Short expectedAccountingUpdated, Money expectedPostedAmount,
            String expectedNotes, Short expectedDebitCreditFlag, Date expectedPostedDate) {

        assertThat(actualTransaction.getAccountTrxn(), is(expectedAccountTrxn));
        assertThat(actualTransaction.getRelatedFinancialTrxn(), is(expectedRelatedFinancialTrxn));
        assertThat(actualTransaction.getFinancialAction(), is(expectedFinancialAction));
        assertThat(actualTransaction.getGlcode(), is(expectedGlcode));
        assertThat(actualTransaction.getActionDate(), is(expectedActionDate));
        assertThat(actualTransaction.getPostedBy(), is(expectedPostedBy));
        assertThat(actualTransaction.getAccountingUpdated(), is(expectedAccountingUpdated));
        assertThat(actualTransaction.getPostedAmount(), is(expectedPostedAmount));
        assertThat(actualTransaction.getNotes(), is(expectedNotes));
        assertThat(actualTransaction.getDebitCreditFlag(), is(expectedDebitCreditFlag));
        assertThat(actualTransaction.getPostedDate(), is(expectedPostedDate));
    }

    /**
     * Compare FinancialTransactionBO objects lexicographically by GL Code string values. Use this to sort collections
     * of transactions created by AccountingEntry objects to verify the transactions in order of gl codes.
     *
     */
    protected class GLCodeComparator implements Comparator<FinancialTransactionBO> {
        public int compare(final FinancialTransactionBO tran1, final FinancialTransactionBO tran2) {
            return tran1.getGlcode().getGlcode().compareTo(tran2.getGlcode().getGlcode());
        }
    }

    /**
     * Return Set containing just one account. Useful for mocking FinancialActionBO.getAssociatedChartOfAccounts()
     */
    protected Set<COABO> setWith(COABO account) {
        Set<COABO> chart = new HashSet<COABO>();
        chart.add(account);
        return chart;
    }

}
