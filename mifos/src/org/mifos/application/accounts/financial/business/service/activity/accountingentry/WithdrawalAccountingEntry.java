/**

 * WithdrawalAccountingEntry.java    version: xxx

 

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
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.framework.util.helpers.Money;

public class WithdrawalAccountingEntry extends BaseAccountingEntry {

	protected void getSpecificAccountActionEntry() throws FinancialException {
		SavingsTrxnDetailEntity savingsTrxn = (SavingsTrxnDetailEntity) financialActivity
				.getAccountTrxn();
		SavingsBO savings = (SavingsBO) savingsTrxn.getAccount();
		FinancialActionBO finActionWithrawal = null;
		if (savings.getSavingsType().getSavingsTypeId()
				.equals(ProductDefinitionConstants.MANDATORY)) {
			finActionWithrawal = FinancialActionCache
					.getFinancialAction(FinancialActionConstants.MANDATORYWITHDRAWAL);
		}
		if (savings.getSavingsType().getSavingsTypeId()
				.equals(ProductDefinitionConstants.VOLUNTARY)) {
			finActionWithrawal = FinancialActionCache
					.getFinancialAction(FinancialActionConstants.VOLUNTORYWITHDRAWAL);
		}

		if(savings.getAccountState().getId().equals(AccountStates.SAVINGS_ACC_CLOSED))
			handleRoundingForWithdrawal(savings,savingsTrxn);
		
		addAccountEntryDetails(savingsTrxn.getWithdrawlAmount(),
				finActionWithrawal, savings.getSavingsOffering()
						.getDepositGLCode(), FinancialConstants.DEBIT);
		addAccountEntryDetails(savingsTrxn.getWithdrawlAmount(),
				finActionWithrawal, getGLcode(finActionWithrawal
						.getApplicableCreditCOA()), FinancialConstants.CREDIT);				
	}

	private void handleRoundingForWithdrawal(SavingsBO savings, SavingsTrxnDetailEntity savingsTrxn)throws FinancialException{
		Money roundedAmount = Money.round(savingsTrxn.getWithdrawlAmount());
		if (!roundedAmount.equals(savingsTrxn.getWithdrawlAmount())) {
			FinancialActionBO finActionRounding = FinancialActionCache.getFinancialAction(FinancialActionConstants.ROUNDING);
			if(roundedAmount.getAmountDoubleValue() > savingsTrxn.getWithdrawlAmount().getAmountDoubleValue())
				addEntriesForIncreasedAmount(savings, finActionRounding, roundedAmount, savingsTrxn.getWithdrawlAmount());
			else
				addEntriesForDecreasedAmount(savings, finActionRounding, roundedAmount, savingsTrxn.getWithdrawlAmount());
			
			//savingsTrxn.setWithdrawlAmount(roundedAmount);
		}
	}
	
	private void addEntriesForIncreasedAmount(SavingsBO savings, FinancialActionBO finActionRounding, Money roundedAmt, Money withdrawalAmt)throws FinancialException{
		addAccountEntryDetails(roundedAmt.subtract(withdrawalAmt),
				finActionRounding, getGLcode(finActionRounding.getApplicableDebitCOA()), FinancialConstants.DEBIT);
		addAccountEntryDetails(roundedAmt.subtract(withdrawalAmt), finActionRounding,
				savings.getSavingsOffering().getDepositGLCode(),FinancialConstants.CREDIT);
	}
	
	private void addEntriesForDecreasedAmount(SavingsBO savings, FinancialActionBO finActionRounding, Money roundedAmt, Money withdrawalAmt)throws FinancialException{
		addAccountEntryDetails(withdrawalAmt.subtract(roundedAmt),
				finActionRounding, savings.getSavingsOffering().getDepositGLCode(),FinancialConstants.DEBIT);
		addAccountEntryDetails(withdrawalAmt.subtract(roundedAmt), finActionRounding,
				getGLcode(finActionRounding.getApplicableCreditCOA()),FinancialConstants.CREDIT);		
	}

}
