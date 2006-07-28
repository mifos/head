/**

 * FeesAccountingEntry.java    version: 1.0

 

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

import java.util.Iterator;
import java.util.Set;

import org.mifos.application.accounts.business.FeesTrxnDetailEntity;
import org.mifos.application.accounts.business.LoanTrxnDetailEntity;
import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialConstants;

public class FeesAccountingEntry extends BaseAccountingEntry {
	protected void getSpecificAccountActionEntry() throws FinancialException {
		LoanTrxnDetailEntity loanTrxn = (LoanTrxnDetailEntity) financialActivity
				.getAccountTrxn();
		Set<FeesTrxnDetailEntity> feesTrxn = loanTrxn.getFeesTrxnDetails();
		Iterator<FeesTrxnDetailEntity> iterFees = feesTrxn.iterator();
		FinancialActionBO finActionFee = FinancialActionCache
				.getFinancialAction(FinancialActionConstants.FEEPOSTING);
		while (iterFees.hasNext()) {
			FeesTrxnDetailEntity feeTrxn = iterFees.next();

			
			addAccountEntryDetails(feeTrxn.getFeeAmount(), finActionFee, feeTrxn
					.getAccountFees().getFees().getGlCode(),FinancialConstants.CREDIT);

			
			addAccountEntryDetails(feeTrxn.getFeeAmount(), finActionFee,
					getGLcode(finActionFee.getApplicableDebitCOA()),FinancialConstants.DEBIT);
		}
		//For Misc Fee
		FinancialActionBO finActionMiscFee = FinancialActionCache.getFinancialAction(FinancialActionConstants.MISCFEEPOSTING);
		addAccountEntryDetails(loanTrxn.getMiscFeeAmount(), finActionMiscFee,
				getGLcode(finActionMiscFee.getApplicableDebitCOA()),FinancialConstants.DEBIT);

		
		addAccountEntryDetails(loanTrxn.getMiscFeeAmount(), finActionMiscFee,
				getGLcode(finActionMiscFee.getApplicableCreditCOA()),FinancialConstants.CREDIT);
		
	}

}
