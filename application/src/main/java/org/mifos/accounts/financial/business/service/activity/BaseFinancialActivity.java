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

package org.mifos.accounts.financial.business.service.activity;

import java.util.ArrayList;
import java.util.List;

import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.financial.business.FinancialTransactionBO;
import org.mifos.accounts.financial.business.service.activity.accountingentry.BaseAccountingEntry;
import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.accounts.loan.business.LoanTrxnDetailEntity;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.customers.business.CustomerTrxnDetailEntity;
import org.mifos.framework.util.helpers.Money;

public abstract class BaseFinancialActivity {
    private final AccountTrxnEntity accountTrxn;

    private final List<FinancialTransactionBO> financialTransactions = new ArrayList<FinancialTransactionBO>();

    public BaseFinancialActivity(AccountTrxnEntity accountTrxn) {
        this.accountTrxn = accountTrxn;
    }

    public AccountTrxnEntity getAccountTrxn() {
        return accountTrxn;
    }

    public void buildAccountEntries() throws FinancialException {
        final List<BaseAccountingEntry> financialActionEntryList = getFinancialActionEntry();

        for (BaseAccountingEntry financialActionEntry : financialActionEntryList) {
            financialActionEntry.buildAccountEntryForAction(this);
        }

        for (FinancialTransactionBO financialTransaction : financialTransactions) {
            accountTrxn.addFinancialTransction(financialTransaction);
        }
    }

    public void addFinancialTransaction(FinancialTransactionBO financialTransaction) {
        financialTransactions.add(financialTransaction);
    }

    public List<FinancialTransactionBO> getFinanacialTransaction() {
        return financialTransactions;
    }

    protected abstract List<BaseAccountingEntry> getFinancialActionEntry();

    public Money getMiscPenaltyAmount() {
        AccountBO account = getAccountTrxn().getAccount();
        Money amount = new Money(account.getCurrency());
        if (account.getType() == AccountTypes.LOAN_ACCOUNT) {
            amount = ((LoanTrxnDetailEntity) getAccountTrxn()).getMiscPenaltyAmount();
        } else if (account.getType() == AccountTypes.CUSTOMER_ACCOUNT) {
            amount = ((CustomerTrxnDetailEntity) getAccountTrxn()).getMiscPenaltyAmount();
        }
        return amount;
    }

}
