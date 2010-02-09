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
import org.mifos.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;

public class InterestPostingAccountingEntry extends BaseAccountingEntry {

    @Override
    protected void getSpecificAccountActionEntry() throws FinancialException {

        SavingsTrxnDetailEntity savingsTrxn = (SavingsTrxnDetailEntity) financialActivity.getAccountTrxn();

        FinancialActionBO finIntPostingAction = getFinancialAction(FinancialActionConstants.SAVINGS_INTERESTPOSTING);
        SavingsOfferingBO savingsOffering = ((SavingsBO) savingsTrxn.getAccount()).getSavingsOffering();

        addAccountEntryDetails(savingsTrxn.getInterestAmount(), finIntPostingAction, savingsOffering
                .getInterestGLCode(), FinancialConstants.DEBIT);
        addAccountEntryDetails(savingsTrxn.getInterestAmount(), finIntPostingAction,
                savingsOffering.getDepositGLCode(), FinancialConstants.CREDIT);
    }

}
