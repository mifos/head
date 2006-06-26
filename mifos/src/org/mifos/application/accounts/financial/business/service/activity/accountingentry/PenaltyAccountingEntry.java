/**

 * PenaltyAccountingEntry.java    version: 1.0

 

 * Copyright © 2005-2006 Grameen Foundation USA

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

import org.mifos.application.accounts.business.LoanTrxnDetailEntity;
import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.customer.business.CustomerTrxnDetailEntity;
import org.mifos.framework.util.helpers.Money;

public class PenaltyAccountingEntry extends BaseAccountingEntry {


	protected void getSpecificAccountActionEntry() throws FinancialException {
		Money amount = new Money();
		if(financialActivity.getAccountTrxn().getAccount().getAccountType().getAccountTypeId().equals(Short.valueOf(AccountTypes.LOANACCOUNT))) 
			amount = ((LoanTrxnDetailEntity) financialActivity.getAccountTrxn()).getMiscPenaltyAmount();
		else if(financialActivity.getAccountTrxn().getAccount().getAccountType().getAccountTypeId().equals(Short.valueOf(AccountTypes.CUSTOMERACCOUNT)))
				amount = ((CustomerTrxnDetailEntity) financialActivity.getAccountTrxn()).getMiscPenaltyAmount();	
			
		FinancialActionBO finActionMiscPenalty = FinancialActionCache.getFinancialAction(FinancialActionConstants.MISCPENALTYPOSTING);
		addAccountEntryDetails(amount, finActionMiscPenalty,
				getGLcode(finActionMiscPenalty.getApplicableDebitCOA()),FinancialConstants.DEBIT);

		
		addAccountEntryDetails(amount, finActionMiscPenalty,
				getGLcode(finActionMiscPenalty.getApplicableCreditCOA()),FinancialConstants.CREDIT);
	}

}
