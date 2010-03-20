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
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanTrxnDetailEntity;

public class RescheduleAccountingEntry extends BaseAccountingEntry {

    @Override
    protected void applySpecificAccountActionEntry() throws FinancialException {
        LoanTrxnDetailEntity loanTrxn = (LoanTrxnDetailEntity) financialActivity.getAccountTrxn();

        FinancialActionBO finActionReschedule = FinancialActionCache
                .getFinancialAction(FinancialActionConstants.RESCHEDULE);
        addAccountEntryDetails(loanTrxn.getPrincipalAmount(), finActionReschedule, getGLcode(finActionReschedule
                .getApplicableDebitCharts()), FinancialConstants.DEBIT);
        GLCodeEntity glcodeCredit = ((LoanBO) loanTrxn.getAccount()).getLoanOffering().getPrincipalGLcode();
        addAccountEntryDetails(loanTrxn.getPrincipalAmount(), finActionReschedule, glcodeCredit,
                FinancialConstants.CREDIT);
        // no 999 account entries are made for close-rescheduled loans because
        // if some payments have already been made
        // the 999 account amount is probably very small and is ignored in 1.1
        // release. In the future if we want to
        // calculate the 999 amount in this case we need to store the raw amount
        // for each installment.

    }

}
