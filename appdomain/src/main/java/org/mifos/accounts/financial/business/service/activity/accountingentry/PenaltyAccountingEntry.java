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

package org.mifos.accounts.financial.business.service.activity.accountingentry;

import java.util.Iterator;
import java.util.Set;

import org.mifos.accounts.business.PenaltiesTrxnDetailEntity;
import org.mifos.accounts.financial.business.FinancialActionTypeEntity;
import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.accounts.loan.business.LoanTrxnDetailEntity;
import org.mifos.framework.util.helpers.Money;

public class PenaltyAccountingEntry extends BaseAccountingEntry {

    @Override
    protected void applySpecificAccountActionEntry() throws FinancialException {
        if (financialActivity.getAccountTrxn() instanceof LoanTrxnDetailEntity) {
            LoanTrxnDetailEntity loanTrxn = (LoanTrxnDetailEntity) financialActivity.getAccountTrxn();
            Set<PenaltiesTrxnDetailEntity> penaltiesTrxn = loanTrxn.getPenaltiesTrxnDetails();
            Iterator<PenaltiesTrxnDetailEntity> iterPenalties = penaltiesTrxn.iterator();
            FinancialActionTypeEntity finActionPenalty = getFinancialAction(FinancialActionConstants.PENALTYPOSTING);
            while (iterPenalties.hasNext()) {
                PenaltiesTrxnDetailEntity penaltyTrxn = iterPenalties.next();

                addAccountEntryDetails(penaltyTrxn.getPenaltyAmount(), finActionPenalty, penaltyTrxn
                        .getAccountPenalties().getPenalty().getGlCode(), FinancialConstants.CREDIT);

                addAccountEntryDetails(penaltyTrxn.getPenaltyAmount(), finActionPenalty,
                        getGLcode(finActionPenalty.getApplicableDebitCharts()), FinancialConstants.DEBIT);
            }

            FinancialActionTypeEntity finActionMiscPenalty = FinancialActionCache
                    .getFinancialAction(FinancialActionConstants.MISCPENALTYPOSTING);
            addAccountEntryDetails(loanTrxn.getMiscPenaltyAmount(), finActionMiscPenalty,
                    getGLcode(finActionMiscPenalty.getApplicableDebitCharts()), FinancialConstants.DEBIT);

            addAccountEntryDetails(loanTrxn.getMiscPenaltyAmount(), finActionMiscPenalty,
                    getGLcode(finActionMiscPenalty.getApplicableCreditCharts()), FinancialConstants.CREDIT);
        } else {
            Money amount = financialActivity.getMiscPenaltyAmount();

            FinancialActionTypeEntity finActionMiscPenalty = FinancialActionCache
                    .getFinancialAction(FinancialActionConstants.MISCPENALTYPOSTING);
            addAccountEntryDetails(amount, finActionMiscPenalty,
                    getGLcode(finActionMiscPenalty.getApplicableDebitCharts()), FinancialConstants.DEBIT);

            addAccountEntryDetails(amount, finActionMiscPenalty,
                    getGLcode(finActionMiscPenalty.getApplicableCreditCharts()), FinancialConstants.CREDIT);
        }
    }

}
