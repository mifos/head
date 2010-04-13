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

import java.util.Iterator;
import java.util.Set;

import org.mifos.accounts.business.FeesTrxnDetailEntity;
import org.mifos.accounts.financial.business.FinancialActionTypeEntity;
import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.customers.business.CustomerTrxnDetailEntity;

public class CustomerFeesAdjustmentAccountingEntry extends BaseAccountingEntry {

    @Override
    protected void applySpecificAccountActionEntry() throws FinancialException {
        CustomerTrxnDetailEntity customertrxn = (CustomerTrxnDetailEntity) financialActivity.getAccountTrxn();
        Set<FeesTrxnDetailEntity> feesTrxn = customertrxn.getFeesTrxnDetails();
        Iterator<FeesTrxnDetailEntity> iterFees = feesTrxn.iterator();
        FinancialActionTypeEntity finActionFee = getFinancialAction(FinancialActionConstants.FEEPOSTING);
        while (iterFees.hasNext()) {
            FeesTrxnDetailEntity feeTrxn = iterFees.next();
            addAccountEntryDetails(feeTrxn.getFeeAmount(), finActionFee, feeTrxn.getAccountFees().getFees()
                    .getGlCode(), FinancialConstants.DEBIT);

            addAccountEntryDetails(feeTrxn.getFeeAmount(), finActionFee, getGLcode(finActionFee
                    .getApplicableDebitCharts()), FinancialConstants.CREDIT);
        }
        // For Misc Fee
        FinancialActionTypeEntity finActionMiscFee = FinancialActionCache
                .getFinancialAction(FinancialActionConstants.CUSTOMERACCOUNTMISCFEESPOSTING);
        addAccountEntryDetails(customertrxn.getMiscFeeAmount(), finActionMiscFee,
                getGLcode(finActionMiscFee.getApplicableDebitCharts()), FinancialConstants.CREDIT);

        addAccountEntryDetails(customertrxn.getMiscFeeAmount(), finActionMiscFee,
                getGLcode(finActionMiscFee.getApplicableCreditCharts()), FinancialConstants.DEBIT);

    }
}
