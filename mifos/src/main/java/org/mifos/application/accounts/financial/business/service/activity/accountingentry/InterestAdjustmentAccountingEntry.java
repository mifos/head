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

import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanTrxnDetailEntity;
import org.mifos.framework.util.helpers.Money;

public class InterestAdjustmentAccountingEntry extends BaseAccountingEntry {

    @Override
    protected void getSpecificAccountActionEntry() throws FinancialException {
        LoanTrxnDetailEntity loanTrxn = (LoanTrxnDetailEntity) financialActivity.getAccountTrxn();
        LoanBO loan = (LoanBO) loanTrxn.getAccount();
        if (loan.isLegacyLoan()) {
            logTransactions_v1(loanTrxn);
        } else {
            logTransactions_v2(loanTrxn);
        }

    }

    private void logTransactions_v1(LoanTrxnDetailEntity loanTrxn) throws FinancialException {
        GLCodeEntity glcodeCredit = ((LoanBO) loanTrxn.getAccount()).getLoanOffering().getInterestGLcode();

        FinancialActionBO finActionInterest = FinancialActionCache
                .getFinancialAction(FinancialActionConstants.INTERESTPOSTING);
        addAccountEntryDetails(removeSign(loanTrxn.getInterestAmount()), finActionInterest, getGLcode(finActionInterest
                .getApplicableDebitCharts()), FinancialConstants.CREDIT);

        addAccountEntryDetails(removeSign(loanTrxn.getInterestAmount()), finActionInterest, glcodeCredit,
                FinancialConstants.DEBIT);

        // check if rounding is required
        Money roundedAmount = Money.round(loanTrxn.getInterestAmount());
        if (!roundedAmount.equals(loanTrxn.getInterestAmount())) {
            FinancialActionBO finActionRounding = FinancialActionCache
                    .getFinancialAction(FinancialActionConstants.ROUNDING);

            addAccountEntryDetails(roundedAmount.subtract(loanTrxn.getInterestAmount()).negate(), finActionRounding,
                    getGLcode(finActionInterest.getApplicableCreditCharts()), FinancialConstants.DEBIT);

            addAccountEntryDetails(roundedAmount.subtract(loanTrxn.getInterestAmount()), finActionRounding,
                    getGLcode(finActionRounding.getApplicableCreditCharts()), FinancialConstants.CREDIT);
        }

    }

    private void logTransactions_v2(LoanTrxnDetailEntity loanTrxn) throws FinancialException {

        GLCodeEntity glcodeCredit = ((LoanBO) loanTrxn.getAccount()).getLoanOffering().getInterestGLcode();

        FinancialActionBO finActionInterest = FinancialActionCache
                .getFinancialAction(FinancialActionConstants.INTERESTPOSTING);
        addAccountEntryDetails(removeSign(loanTrxn.getInterestAmount()), finActionInterest, getGLcode(finActionInterest
                .getApplicableDebitCharts()), FinancialConstants.CREDIT);

        addAccountEntryDetails(removeSign(loanTrxn.getInterestAmount()), finActionInterest, glcodeCredit,
                FinancialConstants.DEBIT);
        boolean isLastPayment = ((LoanBO) loanTrxn.getAccount()).isLastInstallment(loanTrxn.getInstallmentId());
        if (!isLastPayment) {
            return;
        }
        boolean interestWasCharged = loanTrxn.getInterestAmount().getAmountDoubleValue() < 0;
        // 999 account may not be logged when the last payment is made so there
        // is no need to
        // log the reversed 999 account
        if (!interestWasCharged) {
            return;
        }
        Money account999 = ((LoanBO) loanTrxn.getAccount()).calculate999Account(!isLastPayment);
        Money zeroAmount = new Money("0");
        // only log if amount > or < 0
        if (account999.equals(zeroAmount)) {
            return;
        }

        FinancialActionBO finActionRounding = FinancialActionCache
                .getFinancialAction(FinancialActionConstants.ROUNDING);
        GLCodeEntity codeToDebit = null;
        GLCodeEntity codeToCredit = null;
        if (account999.getAmountDoubleValue() > 0) {
            // this code is defined as below in chart of account
            // <GLAccount code="31401" name="Income from 999 Account" />
            codeToDebit = getGLcode(finActionRounding.getApplicableCreditCharts());
            codeToCredit = glcodeCredit;
        } else if (account999.getAmountDoubleValue() < 0) {
            codeToDebit = glcodeCredit;
            codeToCredit = getGLcode(finActionRounding.getApplicableDebitCharts());
            account999 = account999.negate();

        }
        addAccountEntryDetails(account999, finActionRounding, codeToDebit, FinancialConstants.DEBIT);
        addAccountEntryDetails(account999, finActionRounding, codeToCredit, FinancialConstants.CREDIT);

    }
}
