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
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.application.customer.business.CustomerTrxnDetailEntity;

public class CustomerPenaltyAdjustmentAccountingEntry extends BaseAccountingEntry {

    @Override
    protected void getSpecificAccountActionEntry() throws FinancialException {
        CustomerTrxnDetailEntity customertrxn = (CustomerTrxnDetailEntity) financialActivity.getAccountTrxn();
        FinancialActionBO finActionMiscPenalty = FinancialActionCache
                .getFinancialAction(FinancialActionConstants.MISCPENALTYPOSTING);

        addAccountEntryDetails(removeSign(customertrxn.getMiscPenaltyAmount()), finActionMiscPenalty,
                getGLcode(finActionMiscPenalty.getApplicableDebitCharts()), FinancialConstants.CREDIT);

        addAccountEntryDetails(removeSign(customertrxn.getMiscPenaltyAmount()), finActionMiscPenalty,
                getGLcode(finActionMiscPenalty.getApplicableCreditCharts()), FinancialConstants.DEBIT);

    }
}
