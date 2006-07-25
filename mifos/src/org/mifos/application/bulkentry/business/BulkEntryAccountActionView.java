/**

 * BulkEntryAccountActionView.java    version: 1.0

 

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

package org.mifos.application.bulkentry.business;

import java.util.Date;
import java.util.List;

import org.mifos.framework.business.View;
import org.mifos.framework.util.helpers.Money;

public class BulkEntryAccountActionView extends View {

	private final Integer actionDateId;

	private final Integer accountId;

	private final Integer customerId;

	private final Date actionDate;

	private final Money deposit;

	private final Money principal;

	private final Money interest;

	private final Money penalty;

	private final Money miscFee;

	private final Money miscFeePaid;

	private final Money miscPenalty;

	private final Money miscPenaltyPaid;

	private final Money depositPaid;

	private final Money principalPaid;

	private final Money interestPaid;

	private final Money penaltyPaid;

	private final Short installmentId;

	private List<BulkEntryAccountFeeActionView> bulkEntryAccountFeeActions;

	public BulkEntryAccountActionView(Integer accountId, Integer customerId,
			Short installmentId, Date actionDate, Money principal,
			Money principalPaid, Money interest, Money interestPaid,
			Money miscFee, Money miscFeePaid, Money penalty, Money penaltyPaid,
			Money miscPenalty, Money miscPenaltyPaid, Money deposit,
			Money depositPaid, Integer actionDateId) {
		this.accountId = accountId;
		this.customerId = customerId;
		this.actionDate = actionDate;
		this.deposit = deposit;
		this.principal = principal;
		this.interest = interest;
		this.penalty = penalty;
		this.miscFee = miscFee;
		this.miscFeePaid = miscFeePaid;
		this.miscPenalty = miscPenalty;
		this.miscPenaltyPaid = miscPenaltyPaid;
		this.depositPaid = depositPaid;
		this.principalPaid = principalPaid;
		this.interestPaid = interestPaid;
		this.penaltyPaid = penaltyPaid;
		this.installmentId = installmentId;
		this.actionDateId = actionDateId;
	}

	public BulkEntryAccountActionView(Integer accountId, Integer customerId) {
		this(accountId, customerId, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null);
	}

	public Integer getActionDateId() {
		return actionDateId;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public Date getActionDate() {
		return actionDate;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public Money getDeposit() {
		return deposit;
	}

	public Money getDepositPaid() {
		return depositPaid;
	}

	public Short getInstallmentId() {
		return installmentId;
	}

	public Money getInterest() {
		return interest;
	}

	public Money getInterestPaid() {
		return interestPaid;
	}

	public Money getMiscFee() {
		return miscFee;
	}

	public Money getMiscFeePaid() {
		return miscFeePaid;
	}

	public Money getMiscPenalty() {
		return miscPenalty;
	}

	public Money getMiscPenaltyPaid() {
		return miscPenaltyPaid;
	}

	public Money getPenalty() {
		return penalty;
	}

	public Money getPenaltyPaid() {
		return penaltyPaid;
	}

	public Money getPrincipal() {
		return principal;
	}

	public Money getPrincipalPaid() {
		return principalPaid;
	}

	public List<BulkEntryAccountFeeActionView> getBulkEntryAccountFeeActions() {
		return bulkEntryAccountFeeActions;
	}

	public void setBulkEntryAccountFeeActions(
			List<BulkEntryAccountFeeActionView> bulkEntryAccountFeeActions) {
		this.bulkEntryAccountFeeActions = bulkEntryAccountFeeActions;
	}

	public Money getPrincipalDue() {
		return getPrincipal().subtract(getPrincipalPaid());
	}

	public Money getInterestDue() {
		return getInterest().subtract(getInterestPaid());
	}

	public Money getPenaltyDue() {
		return (getPenalty().add(getMiscPenalty())).subtract(getPenaltyPaid()
				.subtract(getMiscPenaltyPaid()));
	}

	public Money getMiscFeeDue() {
		return getMiscFee().subtract(getMiscFeePaid());
	}

	public Money getTotalFeeDue() {
		Money totalFees = new Money();
		if (bulkEntryAccountFeeActions != null)
			for (BulkEntryAccountFeeActionView obj : bulkEntryAccountFeeActions) {
				totalFees = totalFees.add(obj.getFeeDue());
			}

		return totalFees;
	}

	public Money getTotalFees() {
		return getMiscFee().add(getTotalFeeDue());
	}

	public Money getTotalDueWithFees() {
		return getTotalDue().add(getTotalFeeDue());
	}

	public Money getTotalDue() {
		return getPrincipalDue().add(getInterestDue()).add(getPenaltyDue())
				.add(getMiscFeeDue());

	}

	public Money getTotalDueWithoutPricipal() {
		return getInterestDue().add(getPenaltyDue()).add(getMiscFeeDue());
	}

	public Money getTotalDepositDue() {
		return getDeposit().subtract(getDepositPaid());
	}

	public Money getTotalPenalty() {
		return getPenalty().add(getMiscPenalty());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof BulkEntryAccountActionView) {
			BulkEntryAccountActionView bulkEntryAccountActionView = (BulkEntryAccountActionView) obj;
			if (bulkEntryAccountActionView.getAccountId()
					.equals(getAccountId())
					&& bulkEntryAccountActionView.getCustomerId().equals(
							getCustomerId()))
				return true;
		}
		return false;
	}

}
