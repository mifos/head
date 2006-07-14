/**

 * SavingsAdjustmentAccountingEntry.java    version: 1.0

 

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
import org.mifos.application.accounts.savings.util.helpers.SavingsHelper;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.framework.util.helpers.Money;

public class SavingsAdjustmentAccountingEntry extends BaseAccountingEntry {
	protected void getSpecificAccountActionEntry() throws FinancialException {
		SavingsTrxnDetailEntity savingsTrxn = (SavingsTrxnDetailEntity) financialActivity
				.getAccountTrxn();
		
		SavingsBO savings = (SavingsBO) savingsTrxn.getAccount();
		if(isAdjustmentForWithdrawal(savings))
			adjustWithdrawal(savings,savingsTrxn);
		else
			adjustDeposit(savings,savingsTrxn);
	}
	
	private Money removeSign(Money amount){
		if(amount!=null && amount.getAmountDoubleValue()<0)
			return amount.negate();
		else
			return amount;
	}
	
	private boolean isAdjustmentForWithdrawal(SavingsBO savings){
		return (new SavingsHelper().getPaymentActionType(savings.getLastPmnt()).equals(AccountConstants.ACTION_SAVINGS_WITHDRAWAL));		
	}
	
	private void adjustWithdrawal(SavingsBO savings,SavingsTrxnDetailEntity savingsTrxn)throws FinancialException{
		FinancialActionBO finActionWithrawal = null;
		
		if (savings.getSavingsType().getSavingsTypeId()
				.equals(ProductDefinitionConstants.MANDATORY)) {
			finActionWithrawal = FinancialActionCache
					.getFinancialAction(FinancialActionConstants.MANDATORYWITHDRAWAL_ADJUSTMENT);
		}
		if (savings.getSavingsType().getSavingsTypeId()
				.equals(ProductDefinitionConstants.VOLUNTARY)) {
			finActionWithrawal = FinancialActionCache
					.getFinancialAction(FinancialActionConstants.VOLUNTORYWITHDRAWAL_ADJUSTMENT);
		}
		addAccountEntryDetails(removeSign(savingsTrxn.getWithdrawlAmount()),
				finActionWithrawal, savings.getSavingsOffering()
						.getDepositGLCode(), FinancialConstants.CREDIT);
		addAccountEntryDetails(removeSign(savingsTrxn.getWithdrawlAmount()),
				finActionWithrawal, getGLcode(finActionWithrawal
						.getApplicableDebitCOA()), FinancialConstants.DEBIT);
	}
	
	private void adjustDeposit(SavingsBO savings,SavingsTrxnDetailEntity savingsTrxn)throws FinancialException{
		FinancialActionBO finActionDeposit = null;
		if (savings.getSavingsType().getSavingsTypeId()
				.equals(ProductDefinitionConstants.MANDATORY)) {
			finActionDeposit = FinancialActionCache
					.getFinancialAction(FinancialActionConstants.MANDATORYDEPOSIT_ADJUSTMENT);
		}
		if (savings.getSavingsType().getSavingsTypeId()
				.equals(ProductDefinitionConstants.VOLUNTARY)) {
			finActionDeposit = FinancialActionCache
					.getFinancialAction(FinancialActionConstants.VOLUNTORYDEPOSIT_ADJUSTMENT);
		}
		addAccountEntryDetails(removeSign(savingsTrxn.getDepositAmount()),
				finActionDeposit, getGLcode(finActionDeposit
						.getApplicableCreditCOA()), FinancialConstants.CREDIT);

		addAccountEntryDetails(removeSign(savingsTrxn.getDepositAmount()),
				finActionDeposit, savings.getSavingsOffering()
						.getDepositGLCode(), FinancialConstants.DEBIT);
	}
}
