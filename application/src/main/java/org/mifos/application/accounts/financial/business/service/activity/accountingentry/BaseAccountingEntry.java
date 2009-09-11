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

package org.mifos.application.accounts.financial.business.service.activity.accountingentry;

import java.util.Iterator;
import java.util.Set;

import org.mifos.application.accounts.financial.business.COABO;
import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.business.FinancialTransactionBO;
import org.mifos.application.accounts.financial.business.GLCategoryType;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.financial.business.service.activity.BaseFinancialActivity;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.ChartOfAccountsCache;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.framework.util.helpers.Money;

public abstract class BaseAccountingEntry {
    protected BaseFinancialActivity financialActivity;

    /*
     * Factor out access to the static cache to allow this method to be overridden for testing.
     * Globals like FinancialActionCache should be eliminated or refactored to be injectable
     */
    protected FinancialActionBO getFinancialAction(final FinancialActionConstants financialActionId) throws FinancialException {
        return FinancialActionCache.getFinancialAction(financialActionId);
    }

    protected COABO getChartOfAccountsEntry(final String glcode) throws FinancialException {
        return ChartOfAccountsCache.get(glcode);
    }

    public void buildAccountEntryForAction(final BaseFinancialActivity financialActivity) throws FinancialException {
        this.financialActivity = financialActivity;
        getSpecificAccountActionEntry();
    }

    protected void addAccountEntryDetails(final Money postedMoney, final FinancialActionBO financialAction, final GLCodeEntity glcode,
            final FinancialConstants debitCredit) throws FinancialException {
        if (postedMoney.getAmountDoubleValue() != 0) {
            Money amountToPost = getAmountToPost(postedMoney, glcode, debitCredit);
            FinancialTransactionBO financialTransaction = new FinancialTransactionBO(
                    financialActivity.getAccountTrxn(), null, financialAction, glcode, financialActivity
                            .getAccountTrxn().getActionDate(), financialActivity.getAccountTrxn().getPersonnel(),
                    (short) 1, amountToPost, financialActivity.getAccountTrxn().getComments(), debitCredit.getValue(),
                    financialActivity.getAccountTrxn().getTrxnCreatedDate());
            financialActivity.addFinancialTransaction(financialTransaction);
        }
    }

    protected abstract void getSpecificAccountActionEntry() throws FinancialException;

    protected GLCodeEntity getGLcode(final Set<COABO> chartsOfAccounts) {
        Iterator<COABO> iter = chartsOfAccounts.iterator();
        GLCodeEntity glcode = null;
        while (iter.hasNext()) {
            glcode = iter.next().getAssociatedGlcode();
        }
        return glcode;

    }

    private Money getAmountToPost(final Money postedMoney, final GLCodeEntity glcode,
            final FinancialConstants debitCredit) throws FinancialException {
        
        COABO chartOfAccounts = getChartOfAccountsEntry(glcode.getGlcode());
        if (chartOfAccounts.getCOAHead().getCategoryType() == GLCategoryType.ASSET
                || chartOfAccounts.getCOAHead().getCategoryType() == GLCategoryType.EXPENDITURE) {
            if (debitCredit == FinancialConstants.DEBIT) {
                return postedMoney;
            }
            
            return postedMoney.negate();
        }
        if (chartOfAccounts.getCOAHead().getCategoryType() == GLCategoryType.LIABILITY
                || chartOfAccounts.getCOAHead().getCategoryType() == GLCategoryType.INCOME) {
            if (debitCredit == FinancialConstants.DEBIT) {
                return postedMoney.negate();
            }
            
            return postedMoney;
        }
        return null;
    }

    protected Money removeSign(final Money amount) {
        if (amount != null && amount.getAmountDoubleValue() < 0) {
            return amount.negate();
        }
        return amount;
    }
}
