/**

 * LoanAccountView.java    version: 1.0

 

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

package org.mifos.application.accounts.loan.util.helpers;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.bulkentry.business.BulkEntryInstallmentView;
import org.mifos.application.bulkentry.business.BulkEntryLoanInstallmentView;
import org.mifos.framework.business.View;
import org.mifos.framework.util.helpers.Money;

public class LoanAccountView extends View {

	private Integer accountId;

	private Money loanAmount;

	private String prdOfferingShortName;

	private List<BulkEntryInstallmentView> accountTrxnDetails;

	private Short accountType;

	private Short prdOfferingId;

	private Short accountSate;

	private Short interestDeductedAtDisbursement;

	private Double amountPaidAtDisbursement;

	public LoanAccountView(Integer accountId, String prdOfferingShortName,
			AccountTypes accountType, Short prdOfferingId, AccountState state,
			boolean interestDeductedAtDisbursement, Money loanAmount) {
		this.accountId = accountId;
		this.prdOfferingShortName = prdOfferingShortName;
		this.accountType = accountType.getValue();
		this.prdOfferingId = prdOfferingId;
		accountTrxnDetails = new ArrayList<BulkEntryInstallmentView>();
		this.accountSate = state.getValue();
		this.interestDeductedAtDisbursement = 
			interestDeductedAtDisbursement
			? LoanConstants.INTEREST_DEDUCTED_AT_DISBURSMENT 
			: 0;
		this.loanAmount = loanAmount;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public String getPrdOfferingShortName() {
		return prdOfferingShortName;
	}

	public Short getAccountType() {
		return accountType;
	}

	public Short getPrdOfferingId() {
		return prdOfferingId;
	}

	public List<BulkEntryInstallmentView> getAccountTrxnDetails() {
		return accountTrxnDetails;
	}

	public void addTrxnDetails(
			List<BulkEntryInstallmentView> accountTrxnDetails) {
		if (null != accountTrxnDetails && accountTrxnDetails.size() > 0)
			this.accountTrxnDetails.addAll(accountTrxnDetails);
	}

	public Double getTotalAmountDue() {
		Money totalAmount = new Money();
		if (isDisbursalAccount()) {
			return amountPaidAtDisbursement;
		} else {
			if (accountTrxnDetails != null && accountTrxnDetails.size() > 0) {
				for (BulkEntryInstallmentView accountAction : accountTrxnDetails) {
					totalAmount = totalAmount.add(((BulkEntryLoanInstallmentView)accountAction)
							.getTotalDueWithFees());
				}
			}
			return totalAmount.getAmountDoubleValue();
		}
	}

	public Short getAccountSate() {
		return accountSate;
	}

	private Short getInterestDeductedAtDisbursement() {
		return interestDeductedAtDisbursement;
	}

	public boolean isInterestDeductedAtDisbursement() {
		return getInterestDeductedAtDisbursement() > 0 ? true : false;
	}

	public Double getAmountPaidAtDisbursement() {
		return amountPaidAtDisbursement;
	}

	public void setAmountPaidAtDisbursement(Double amountPaidAtDisbursement) {
		/* Would this check be a good idea?  If not, what does null mean?
		if (amountPaidAtDisbursement == null) {
			throw new NullPointerException(
				"amount paid at disbursement is required");
		}
		*/
		this.amountPaidAtDisbursement = amountPaidAtDisbursement;
	}

	public Double getTotalDisburseAmount() {
		return isDisbursalAccount() ? this.loanAmount.getAmountDoubleValue()
				: 0.0;
	}

	public boolean isDisbursalAccount() {
		return getAccountSate()
				.equals(AccountState.LOAN_APPROVED.getValue())
				|| getAccountSate().equals(
						AccountState.LOAN_DISBURSED_TO_LOAN_OFFICER.getValue());
	}
}
