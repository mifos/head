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

package org.mifos.accounts.financial.business.service.activity.accountingentry;

import org.mifos.accounts.financial.business.FinancialActionBO;
import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.accounts.savings.util.helpers.SavingsHelper;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;

public class SavingsAdjustmentAccountingEntry extends BaseAccountingEntry {
    @Override
    protected void getSpecificAccountActionEntry() throws FinancialException {
        SavingsTrxnDetailEntity savingsTrxn = (SavingsTrxnDetailEntity) financialActivity.getAccountTrxn();

        SavingsBO savings = (SavingsBO) savingsTrxn.getAccount();
        if (isAdjustmentForWithdrawal(savings))
            adjustWithdrawal(savings, savingsTrxn);
        else
            adjustDeposit(savings, savingsTrxn);
    }

    private boolean isAdjustmentForWithdrawal(SavingsBO savings) {
        return (new SavingsHelper().getPaymentActionType(savings.getLastPmnt())
                .equals(AccountActionTypes.SAVINGS_WITHDRAWAL.getValue()));
    }

    private void adjustWithdrawal(SavingsBO savings, SavingsTrxnDetailEntity savingsTrxn) throws FinancialException {
        FinancialActionBO finActionWithrawal = null;

        if (savings.getSavingsType().getId().equals(SavingsType.MANDATORY.getValue())) {
            finActionWithrawal = FinancialActionCache
                    .getFinancialAction(FinancialActionConstants.MANDATORYWITHDRAWAL_ADJUSTMENT);
        }
        if (savings.getSavingsType().getId().equals(SavingsType.VOLUNTARY.getValue())) {
            finActionWithrawal = FinancialActionCache
                    .getFinancialAction(FinancialActionConstants.VOLUNTORYWITHDRAWAL_ADJUSTMENT);
        }
        addAccountEntryDetails(removeSign(savingsTrxn.getWithdrawlAmount()), finActionWithrawal, savings
                .getSavingsOffering().getDepositGLCode(), FinancialConstants.CREDIT);
        addAccountEntryDetails(removeSign(savingsTrxn.getWithdrawlAmount()), finActionWithrawal,
                getGLcode(finActionWithrawal.getApplicableDebitCharts()), FinancialConstants.DEBIT);
    }

    private void adjustDeposit(SavingsBO savings, SavingsTrxnDetailEntity savingsTrxn) throws FinancialException {
        FinancialActionBO finActionDeposit = null;
        if (savings.getSavingsType().getId().equals(SavingsType.MANDATORY.getValue())) {
            finActionDeposit = FinancialActionCache
                    .getFinancialAction(FinancialActionConstants.MANDATORYDEPOSIT_ADJUSTMENT);
        }
        if (savings.getSavingsType().getId().equals(SavingsType.VOLUNTARY.getValue())) {
            finActionDeposit = FinancialActionCache
                    .getFinancialAction(FinancialActionConstants.VOLUNTORYDEPOSIT_ADJUSTMENT);
        }
        addAccountEntryDetails(removeSign(savingsTrxn.getDepositAmount()), finActionDeposit, getGLcode(finActionDeposit
                .getApplicableCreditCharts()), FinancialConstants.CREDIT);

        addAccountEntryDetails(removeSign(savingsTrxn.getDepositAmount()), finActionDeposit, savings
                .getSavingsOffering().getDepositGLCode(), FinancialConstants.DEBIT);
    }
}
