/**

 * DepositAccountingEntry.java    version: xxx

 

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
package org.mifos.application.accounts.financial.business.service.activity.accountingentry;

import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.application.productdefinition.util.helpers.SavingsType;

public class DepositAccountingEntry extends BaseAccountingEntry {

	@Override
	protected void getSpecificAccountActionEntry() throws FinancialException {
		SavingsTrxnDetailEntity savingsTrxn = (SavingsTrxnDetailEntity) financialActivity
				.getAccountTrxn();
		SavingsBO savings = (SavingsBO) savingsTrxn.getAccount();
		FinancialActionBO finActionDeposit = null;
		if (savings.getSavingsType().getId()
				.equals(SavingsType.MANDATORY.getValue())) {
			finActionDeposit = FinancialActionCache
					.getFinancialAction(FinancialActionConstants.MANDATORYDEPOSIT);
		}
		if (savings.getSavingsType().getId()
				.equals(SavingsType.VOLUNTARY.getValue())) {
			finActionDeposit = FinancialActionCache
					.getFinancialAction(FinancialActionConstants.VOLUNTORYDEPOSIT);
		}

		addAccountEntryDetails(savingsTrxn.getDepositAmount(),
				finActionDeposit, getGLcode(finActionDeposit
						.getApplicableDebitCharts()), FinancialConstants.DEBIT);
		addAccountEntryDetails(savingsTrxn.getDepositAmount(),
				finActionDeposit, savings.getSavingsOffering()
						.getDepositGLCode(), FinancialConstants.CREDIT);
	}

}
