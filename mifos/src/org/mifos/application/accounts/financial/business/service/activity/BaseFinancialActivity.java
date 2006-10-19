/**

 * BaseFinancialActivity.java    version: 1.0

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied.  

 *

 */

package org.mifos.application.accounts.financial.business.service.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.financial.business.FinancialTransactionBO;
import org.mifos.application.accounts.financial.business.service.activity.accountingentry.BaseAccountingEntry;
import org.mifos.application.accounts.financial.exceptions.FinancialException;

public abstract class BaseFinancialActivity {
	private AccountTrxnEntity accountTrxn;

	private List<FinancialTransactionBO> financialTransactions = new ArrayList<FinancialTransactionBO>();

	public BaseFinancialActivity(AccountTrxnEntity accountTrxn) {
		this.accountTrxn = accountTrxn;
	}

	public AccountTrxnEntity getAccountTrxn() {
		return accountTrxn;
	}

	public void buildAccountEntries() throws FinancialException {
		List<BaseAccountingEntry> financialActionEntryList = getFinancialActionEntry();
		Iterator<BaseAccountingEntry> iterFinancialActionEntry = financialActionEntryList
				.iterator();
		while (iterFinancialActionEntry.hasNext()) {
			BaseAccountingEntry financialActionEntry = iterFinancialActionEntry
					.next();
			financialActionEntry.buildAccountEntryForAction(this);
		}

		Iterator<FinancialTransactionBO> iterFinancialTransactions = financialTransactions
				.iterator();
		while (iterFinancialTransactions.hasNext()) {
			FinancialTransactionBO financialTransaction = iterFinancialTransactions
					.next();
			accountTrxn.addFinancialTransction(financialTransaction);
		}

	}

	public void addFinancialTransaction(
			FinancialTransactionBO financialTransaction) {
		financialTransactions.add(financialTransaction);
	}

	public List<FinancialTransactionBO> getFinanacialTransaction() {
		return financialTransactions;
	}

	protected abstract List<BaseAccountingEntry> getFinancialActionEntry();

}
