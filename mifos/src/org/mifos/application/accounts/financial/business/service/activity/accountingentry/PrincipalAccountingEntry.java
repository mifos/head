/**

 * PrincipalAccountingEntry.java    version: 1.0

 

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
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanTrxnDetailEntity;
import org.mifos.framework.util.helpers.Money;

public class PrincipalAccountingEntry extends BaseAccountingEntry {
	@Override
	protected void getSpecificAccountActionEntry() throws FinancialException {
		
		LoanTrxnDetailEntity loanTrxn = (LoanTrxnDetailEntity) financialActivity
		.getAccountTrxn();
		LoanBO loan =  (LoanBO)loanTrxn.getAccount();
		if (loan.isLegacyLoan())
		{
			logTransactions_v1(loanTrxn);
		}
		else
		{
			logTransactions_v2(loanTrxn);
		}
		

		
	}
	
	private void logTransactions_v2(LoanTrxnDetailEntity loanTrxn) throws FinancialException
	{
		GLCodeEntity glcodeCredit = ((LoanBO) loanTrxn.getAccount())
		.getLoanOffering().getPrincipalGLcode();

		Money amountToPost = loanTrxn.getPrincipalAmount();
		
		
			FinancialActionBO finActionPrincipal = FinancialActionCache
				.getFinancialAction(FinancialActionConstants.PRINCIPALPOSTING);
		addAccountEntryDetails(amountToPost,
				finActionPrincipal, getGLcode(finActionPrincipal
						.getApplicableDebitCharts()), FinancialConstants.DEBIT);
		
		addAccountEntryDetails(amountToPost,
				finActionPrincipal, glcodeCredit, FinancialConstants.CREDIT);
		
		boolean isLastPayment = ((LoanBO)loanTrxn.getAccount()).isLastInstallment(loanTrxn.getInstallmentId());
		if (isLastPayment)
		{
			Money account999 = ((LoanBO)loanTrxn.getAccount()).calculate999Account();
			Money zeroAmount = new Money("0");
			// only log if amount > or < 0
			if (account999.equals(zeroAmount))
			{
				return;
			}
			
			FinancialActionBO finActionRounding = FinancialActionCache
			.getFinancialAction(FinancialActionConstants.ROUNDING);
			GLCodeEntity code1 = null; 
			GLCodeEntity code2 = null;
			if (account999.getAmountDoubleValue() > 0)
			{
				// this code is defined as below in chart of account 
				// <GLAccount code="31401" name="Income from 999 Account" />
				code1 = glcodeCredit;
				code2 = getGLcode(finActionRounding.getApplicableCreditCharts()); 
				
			}
			else if (account999.getAmountDoubleValue() < 0)
			{
				code1 = getGLcode(finActionRounding.getApplicableDebitCharts());
				code2 = glcodeCredit;
				account999 = account999.negate();
			}
			addAccountEntryDetails(account999, finActionRounding, code1, FinancialConstants.DEBIT);
			addAccountEntryDetails(account999, finActionRounding, code2, FinancialConstants.CREDIT);	
			
		}
	}
	
	private void logTransactions_v1(LoanTrxnDetailEntity loanTrxn) throws FinancialException
	{
		GLCodeEntity glcodeCredit = ((LoanBO) loanTrxn.getAccount())
		.getLoanOffering().getPrincipalGLcode();

		Money principalAmountNotRounded = loanTrxn.getPrincipalAmount();
		Money amountToPost = null;
		
		if(((LoanBO)loanTrxn.getAccount()).isLastInstallment(loanTrxn.getInstallmentId()))
			amountToPost = Money.round(loanTrxn.getPrincipalAmount());
		else
			amountToPost = principalAmountNotRounded;
		
			FinancialActionBO finActionPrincipal = FinancialActionCache
				.getFinancialAction(FinancialActionConstants.PRINCIPALPOSTING);
		addAccountEntryDetails(amountToPost,
				finActionPrincipal, getGLcode(finActionPrincipal
						.getApplicableDebitCharts()), FinancialConstants.DEBIT);
		
		addAccountEntryDetails(amountToPost,
				finActionPrincipal, glcodeCredit, FinancialConstants.CREDIT);
		
		
		// check if rounding is required
		FinancialActionBO finActionRounding = FinancialActionCache
		.getFinancialAction(FinancialActionConstants.ROUNDING);
		
		
		if(amountToPost.getAmount().compareTo(principalAmountNotRounded.getAmount()) > 0 )
		{
			addAccountEntryDetails(amountToPost.subtract(principalAmountNotRounded)
					, finActionRounding,glcodeCredit,
					FinancialConstants.DEBIT);
		
			addAccountEntryDetails(amountToPost.subtract(principalAmountNotRounded), finActionRounding,
					getGLcode(finActionRounding.getApplicableCreditCharts()),
					FinancialConstants.CREDIT);
			
		}else if(amountToPost.getAmount().compareTo(principalAmountNotRounded.getAmount()) < 0 )
		{
			addAccountEntryDetails(principalAmountNotRounded.subtract(amountToPost)
					, finActionRounding,getGLcode(finActionRounding.getApplicableDebitCharts()),
					FinancialConstants.DEBIT);
		
			addAccountEntryDetails(principalAmountNotRounded.subtract(amountToPost), finActionRounding,
					glcodeCredit,
					FinancialConstants.CREDIT);
			
		}
	}
}
