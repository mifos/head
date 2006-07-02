/**

 * BaseAccountingEntry.java    version: 1.0

 

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

import org.mifos.application.accounts.financial.business.COABO;
import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.business.FinancialTransactionBO;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.financial.business.service.activity.BaseFinancialActivity;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.CategoryConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.framework.util.helpers.Money;

public abstract class BaseAccountingEntry {
	protected BaseFinancialActivity financialActivity;

	public void buildAccountEntryForAction(
			BaseFinancialActivity financialActivity) throws FinancialException {
		this.financialActivity = financialActivity;
		getSpecificAccountActionEntry();
	}

	protected void addAccountEntryDetails(Money postedMoney,
			FinancialActionBO financialAction, GLCodeEntity glcode,Short debitCreditFlag) {
		if (postedMoney.getAmountDoubleValue() != 0) {
			 postedMoney = getAmountToPost(postedMoney,
                     financialAction, glcode,debitCreditFlag);
			FinancialTransactionBO financialTransaction=new FinancialTransactionBO(financialAction,postedMoney,
					glcode,financialActivity.getAccountTrxn().getActionDate(),financialActivity.getAccountTrxn().getPersonnel(),debitCreditFlag);
			 financialTransaction.setNotes(financialActivity.getAccountTrxn().getComments());
			financialActivity.addFinancialTransaction(financialTransaction);
		}

	}

	protected abstract void getSpecificAccountActionEntry()
			throws FinancialException;

	protected GLCodeEntity getGLcode(Set<COABO> coaSet) {
		Iterator<COABO> iter = coaSet.iterator();
		GLCodeEntity glcode = null;
		while (iter.hasNext()) {
			glcode = iter.next().getAssociatedGlcode();
		}
		return glcode;

	}
	
	
	private Money getAmountToPost(Money postedMoney,FinancialActionBO financialAction, GLCodeEntity glcode,Short debitCreditFlag){
				
		if(glcode.getAssociatedCOA().getCOAHead().getCategoryId().equals(CategoryConstants.ASSETS) || glcode.getAssociatedCOA().getCOAHead().getCategoryId().equals(CategoryConstants.EXPENDITURE))
		{
            if(debitCreditFlag.equals(FinancialConstants.DEBIT))
            	return postedMoney;
            else
                return postedMoney.negate();
		}
		if(glcode.getAssociatedCOA().getCOAHead().getCategoryId().equals(CategoryConstants.LIABILITIES) || glcode.getAssociatedCOA().getCOAHead().getCategoryId().equals(CategoryConstants.INCOME))
		{
            if(debitCreditFlag.equals(FinancialConstants.DEBIT))
                return postedMoney.negate();
            else
                return postedMoney;
		}
		return null;
	}

	
}

