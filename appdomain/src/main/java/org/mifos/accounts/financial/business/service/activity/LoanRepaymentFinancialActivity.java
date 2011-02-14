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

package org.mifos.accounts.financial.business.service.activity;

import java.util.ArrayList;
import java.util.List;

import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.financial.business.service.activity.accountingentry.BaseAccountingEntry;
import org.mifos.accounts.financial.business.service.activity.accountingentry.FeesAccountingEntry;
import org.mifos.accounts.financial.business.service.activity.accountingentry.InterestAccountingEntry;
import org.mifos.accounts.financial.business.service.activity.accountingentry.PenaltyAccountingEntry;
import org.mifos.accounts.financial.business.service.activity.accountingentry.PrincipalAccountingEntry;

public class LoanRepaymentFinancialActivity extends BaseFinancialActivity {
    public LoanRepaymentFinancialActivity(AccountTrxnEntity accountTrxn) {
        super(accountTrxn);
    }

    @Override
    protected List<BaseAccountingEntry> getFinancialActionEntry() {
        List<BaseAccountingEntry> financialActionEntryList = new ArrayList<BaseAccountingEntry>();
        financialActionEntryList.add(new PrincipalAccountingEntry());
        financialActionEntryList.add(new InterestAccountingEntry());
        financialActionEntryList.add(new FeesAccountingEntry());
        financialActionEntryList.add(new PenaltyAccountingEntry());

        return financialActionEntryList;
    }

}
