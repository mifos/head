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

import org.mifos.accounts.financial.business.FinancialActionBO;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanTrxnDetailEntity;
import org.mifos.config.AccountingRules;
import org.mifos.framework.util.helpers.Money;

public class PrincipalAccountingEntry extends BaseAccountingEntry {
    @Override
    protected void applySpecificAccountActionEntry() throws FinancialException {

        LoanTrxnDetailEntity loanTrxn = (LoanTrxnDetailEntity) financialActivity.getAccountTrxn();
        GLCodeEntity glcodeCredit = ((LoanBO) loanTrxn.getAccount()).getLoanOffering().getPrincipalGLcode();

        Money principalAmountNotRounded = loanTrxn.getPrincipalAmount();
        Money amountToPost = null;

        if (((LoanBO) loanTrxn.getAccount()).isLastInstallment(loanTrxn.getInstallmentId())) {
            amountToPost = Money.round(loanTrxn.getPrincipalAmount(), loanTrxn.getPrincipalAmount().getCurrency().getRoundingAmount(),
                    AccountingRules.getCurrencyRoundingMode());
        } else {
            amountToPost = principalAmountNotRounded;
        }

        FinancialActionBO finActionPrincipal = FinancialActionCache
                .getFinancialAction(FinancialActionConstants.PRINCIPALPOSTING);
        addAccountEntryDetails(amountToPost, finActionPrincipal, getGLcode(finActionPrincipal
                .getApplicableDebitCharts()), FinancialConstants.DEBIT);

        addAccountEntryDetails(amountToPost, finActionPrincipal, glcodeCredit, FinancialConstants.CREDIT);
        LoanBO loan = (LoanBO) loanTrxn.getAccount();
        // the new version of financial calculation will log 999 account to
        // interest account and not to the principal account
        if (!loan.isLegacyLoan()) {
            return;
        }

        // v1 version log the 999 account to the principal account
        // check if rounding is required
        FinancialActionBO finActionRounding = FinancialActionCache
                .getFinancialAction(FinancialActionConstants.ROUNDING);

        if (amountToPost.getAmount().compareTo(principalAmountNotRounded.getAmount()) > 0) {
            addAccountEntryDetails(amountToPost.subtract(principalAmountNotRounded), finActionRounding, glcodeCredit,
                    FinancialConstants.DEBIT);

            addAccountEntryDetails(amountToPost.subtract(principalAmountNotRounded), finActionRounding,
                    getGLcode(finActionRounding.getApplicableCreditCharts()), FinancialConstants.CREDIT);

        } else if (amountToPost.getAmount().compareTo(principalAmountNotRounded.getAmount()) < 0) {
            addAccountEntryDetails(principalAmountNotRounded.subtract(amountToPost), finActionRounding,
                    getGLcode(finActionRounding.getApplicableDebitCharts()), FinancialConstants.DEBIT);

            addAccountEntryDetails(principalAmountNotRounded.subtract(amountToPost), finActionRounding, glcodeCredit,
                    FinancialConstants.CREDIT);

        }

    }

}
