/**

 * FinancialBusinessService.java    version: 1.0

 

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

package org.mifos.application.accounts.financial.business.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.financial.business.COABO;
import org.mifos.application.accounts.financial.business.FinancialActionBO;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.financial.business.service.activity.BaseFinancialActivity;
import org.mifos.application.accounts.financial.business.service.activity.CustomerAccountRepaymentFinancialActivity;
import org.mifos.application.accounts.financial.business.service.activity.CustomerAdjustmentFinancialActivity;
import org.mifos.application.accounts.financial.business.service.activity.FeeRepaymentFinancialActivity;
import org.mifos.application.accounts.financial.business.service.activity.LoanAdjustmentFinancialActivity;
import org.mifos.application.accounts.financial.business.service.activity.LoanDisbursementFinantialActivity;
import org.mifos.application.accounts.financial.business.service.activity.LoanRepaymentFinancialActivity;
import org.mifos.application.accounts.financial.business.service.activity.SavingsAdjustmentFinancialActivity;
import org.mifos.application.accounts.financial.business.service.activity.SavingsDepositFinancialActivity;
import org.mifos.application.accounts.financial.business.service.activity.SavingsInterestPostingFinancialActivity;
import org.mifos.application.accounts.financial.business.service.activity.SavingsWithdrawalFinancialActivity;
import org.mifos.application.accounts.financial.business.service.activity.WriteOffFinancialActivity;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.FinancialActionCache;
import org.mifos.application.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;

public class FinancialBusinessService extends BusinessService {

	public FinancialBusinessService() {
		super();
	}

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}

	public void buildAccountingEntries(AccountTrxnEntity accounttrxn)
			throws FinancialException {
		BaseFinancialActivity baseFinancialActivity = null;
		if (accounttrxn.getAccountActionEntity().getId().shortValue() == AccountConstants.ACTION_LOAN_REPAYMENT) {
			baseFinancialActivity = new LoanRepaymentFinancialActivity(
					accounttrxn);
		} else if (accounttrxn.getAccountActionEntity().getId().shortValue() == (AccountConstants.ACTION_SAVINGS_INTEREST_POSTING)) {
			baseFinancialActivity = new SavingsInterestPostingFinancialActivity(
					accounttrxn);
		} else if (accounttrxn.getAccountActionEntity().getId().shortValue() == (AccountConstants.ACTION_SAVINGS_WITHDRAWAL)) {
			baseFinancialActivity = new SavingsWithdrawalFinancialActivity(
					accounttrxn);
		} else if (accounttrxn.getAccountActionEntity().getId().shortValue() == AccountConstants.ACTION_SAVINGS_DEPOSIT) {
			baseFinancialActivity = new SavingsDepositFinancialActivity(
					accounttrxn);
		} else if (accounttrxn.getAccountActionEntity().getId().shortValue() == AccountConstants.ACTION_DISBURSAL) {
			baseFinancialActivity = new LoanDisbursementFinantialActivity(
					accounttrxn);
		} else if (accounttrxn.getAccountActionEntity().getId().shortValue() == AccountConstants.ACTION_FEE_REPAYMENT) {
			baseFinancialActivity = new FeeRepaymentFinancialActivity(
					accounttrxn);
		} else if (accounttrxn.getAccountActionEntity().getId().shortValue() == AccountConstants.ACTION_CUSTOMER_ACCOUNT_REPAYMENT) {
			baseFinancialActivity = new CustomerAccountRepaymentFinancialActivity(
					accounttrxn);
		} else if (accounttrxn.getAccountActionEntity().getId().shortValue() == AccountConstants.ACTION_SAVINGS_ADJUSTMENT) {
			baseFinancialActivity = new SavingsAdjustmentFinancialActivity(
					accounttrxn);
		} else if (accounttrxn.getAccountActionEntity().getId().shortValue() == AccountConstants.ACTION_CUSTOMER_ADJUSTMENT) {
			baseFinancialActivity = new CustomerAdjustmentFinancialActivity(
					accounttrxn);
		} else if (accounttrxn.getAccountActionEntity().getId().shortValue() == AccountConstants.ACTION_LOAN_ADJUSTMENT) {
			baseFinancialActivity = new LoanAdjustmentFinancialActivity(
					accounttrxn);
		} else if (accounttrxn.getAccountActionEntity().getId().shortValue() == AccountConstants.ACTION_WRITEOFF) {
			baseFinancialActivity = new WriteOffFinancialActivity(
					accounttrxn);
		}
		if (baseFinancialActivity != null)
			baseFinancialActivity.buildAccountEntries();
	}

	public List<GLCodeEntity> getGLCodes(short financialAction,
			Short debitCredit) throws SystemException, ApplicationException {
		List<GLCodeEntity> glCodeList = new ArrayList<GLCodeEntity>();
		Set<COABO> applicableCategory = null;
		FinancialActionBO finActionFees = FinancialActionCache
				.getFinancialAction(financialAction);
		if (debitCredit.equals(FinancialConstants.DEBIT))
			applicableCategory = finActionFees.getApplicableDebitCOA();
		else if (debitCredit.equals(FinancialConstants.CREDIT))
			applicableCategory = finActionFees.getApplicableCreditCOA();

		for (COABO coabo : applicableCategory) {
			glCodeList.add(coabo.getAssociatedGlcode());
		}
		return glCodeList;
	}

}
