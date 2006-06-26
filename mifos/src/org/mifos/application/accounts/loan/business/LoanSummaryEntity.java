/**

 * LoanSummaryEntity.java    version: 1.0

 

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

package org.mifos.application.accounts.loan.business;

import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.helpers.Money;

public class LoanSummaryEntity extends PersistentObject {

	private Integer accountId;

	private Money originalPrincipal;

	private Money originalInterest;

	private Money originalFees;

	private Money originalPenalty;

	private Money principalPaid;

	private Money interestPaid;

	private Money feesPaid;

	private Money penaltyPaid;

	private LoanBO loan;

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public LoanBO getLoan() {
		return loan;
	}

	public void setLoan(LoanBO loan) {
		this.loan = loan;
	}

	public Money getFeesPaid() {
		return feesPaid;
	}

	public void setFeesPaid(Money feesPaid) {
		this.feesPaid = feesPaid;
	}

	public Money getInterestPaid() {
		return interestPaid;
	}

	public void setInterestPaid(Money interestPaid) {
		this.interestPaid = interestPaid;
	}

	public Money getOriginalFees() {
		return originalFees;
	}

	public void setOriginalFees(Money originalFees) {
		this.originalFees = originalFees;
	}

	public Money getOriginalInterest() {
		return originalInterest;
	}

	public void setOriginalInterest(Money originalInterest) {
		this.originalInterest = originalInterest;
	}

	public Money getOriginalPenalty() {
		return originalPenalty;
	}

	public void setOriginalPenalty(Money originalPenalty) {
		this.originalPenalty = originalPenalty;
	}

	public Money getOriginalPrincipal() {
		return originalPrincipal;
	}

	public void setOriginalPrincipal(Money originalPrincipal) {
		this.originalPrincipal = originalPrincipal;
	}

	public Money getPenaltyPaid() {
		return penaltyPaid;
	}

	public void setPenaltyPaid(Money penaltyPaid) {
		this.penaltyPaid = penaltyPaid;
	}

	public Money getPrincipalPaid() {
		return principalPaid;
	}

	public void setPrincipalPaid(Money principalPaid) {
		this.principalPaid = principalPaid;
	}

	public void updateFeePaid(Money totalPayment) {
		feesPaid = feesPaid.add(totalPayment);
	}

	public Money getOustandingBalance() {
		Money totalAmount = new Money();
		totalAmount = totalAmount.add(getOriginalPrincipal()).subtract(
				getPrincipalPaid());
		totalAmount = totalAmount.add(getOriginalInterest()).subtract(
				getInterestPaid());
		totalAmount = totalAmount.add(getOriginalPenalty()).subtract(
				getPenaltyPaid());
		totalAmount = totalAmount.add(getOriginalFees())
				.subtract(getFeesPaid());
		return totalAmount;
	}

	public void updatePaymentDetails(Money principal, Money interest,
			Money penalty, Money fees) {
		principalPaid = principalPaid.add(principal);
		interestPaid = interestPaid.add(interest);
		penaltyPaid = penaltyPaid.add(penalty);
		feesPaid = feesPaid.add(fees);
	}

	public void decreaseBy(Money principal, Money interest, Money penalty,
			Money fees) {
		originalPrincipal = originalPrincipal.subtract(principal);
		originalFees = originalFees.subtract(fees);
		originalPenalty = originalPenalty.subtract(penalty);
		originalInterest = originalInterest.subtract(interest);
	}
}