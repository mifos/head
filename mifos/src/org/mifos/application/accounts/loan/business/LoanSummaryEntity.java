/**

 * LoanSummaryEntity.java    version: 1.0

 

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

package org.mifos.application.accounts.loan.business;

import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.helpers.Money;

public class LoanSummaryEntity extends PersistentObject {

	@SuppressWarnings("unused") // see .hbm.xml file
	private Integer accountId;

	@SuppressWarnings("unused") // see .hbm.xml file
	private LoanBO loan;

	private Money originalPrincipal;

	private Money originalInterest;

	private Money originalFees;

	private Money originalPenalty;

	private Money principalPaid;

	private Money interestPaid;

	private Money feesPaid;

	private Money penaltyPaid;
	
	private Money rawAmountTotal;
	

	public Money getRawAmountTotal() {
		return rawAmountTotal;
	}

	public void setRawAmountTotal(Money rawAmountTotal) {
		this.rawAmountTotal = rawAmountTotal;
	}

	

	protected LoanSummaryEntity() {
		super();
		this.accountId = null;
		this.loan = null;
	}
	
	

	public LoanSummaryEntity(LoanBO loan, Money originalPrincipal,
			Money originalInterest, Money originalFees,  Money rawAmountTotal) {
		super();
		this.accountId = null;
		this.loan = loan;
		this.originalPrincipal = originalPrincipal;
		this.originalInterest = originalInterest;
		this.originalFees = originalFees;
		this.rawAmountTotal = rawAmountTotal;
		this.originalPenalty = new Money();
		this.principalPaid = new Money();
		this.interestPaid = new Money();
		this.feesPaid = new Money();
		this.penaltyPaid = new Money();
	}

	public Money getFeesPaid() {
		return feesPaid;
	}

	void setFeesPaid(Money feesPaid) {
		this.feesPaid = feesPaid;
	}

	public Money getInterestPaid() {
		return interestPaid;
	}

	void setInterestPaid(Money interestPaid) {
		this.interestPaid = interestPaid;
	}

	public Money getOriginalFees() {
		return originalFees;
	}

	void setOriginalFees(Money originalFees) {
		this.originalFees = originalFees;
	}

	public Money getOriginalInterest() {
		return originalInterest;
	}

	void setOriginalInterest(Money originalInterest) {
		this.originalInterest = originalInterest;
	}

	public Money getOriginalPenalty() {
		return originalPenalty;
	}

	void setOriginalPenalty(Money originalPenalty) {
		this.originalPenalty = originalPenalty;
	}

	public Money getOriginalPrincipal() {
		return originalPrincipal;
	}

	void setOriginalPrincipal(Money originalPrincipal) {
		this.originalPrincipal = originalPrincipal;
	}

	public Money getPenaltyPaid() {
		return penaltyPaid;
	}

	void setPenaltyPaid(Money penaltyPaid) {
		this.penaltyPaid = penaltyPaid;
	}

	public Money getPrincipalPaid() {
		return principalPaid;
	}

	void setPrincipalPaid(Money principalPaid) {
		this.principalPaid = principalPaid;
	}

	void updateFeePaid(Money totalPayment) {
		feesPaid = feesPaid.add(totalPayment);
	}
	
	public Money getPrincipalDue(){
		return getOriginalPrincipal().subtract(getPrincipalPaid());
	}
	
	public Money getInterestDue(){
		return getOriginalInterest().subtract(getInterestPaid());
	}

	public Money getPenaltyDue(){
		return getOriginalPenalty().subtract(getPenaltyPaid());
	}

	public Money getFeesDue(){
		return getOriginalFees().subtract(getFeesPaid());
	}

	public Money getTotalAmntDue(){
		return getPrincipalDue().add(getInterestDue()).add(getPenaltyDue()).add(getFeesDue());
	}
	
	public Money getTotalLoanAmnt(){
		return getOriginalPrincipal().add(getOriginalFees()).add(getOriginalInterest()).add(getOriginalPenalty());
	}

	public Money getTotalAmntPaid(){
		return getPrincipalPaid().add(getFeesPaid()).add(getInterestPaid()).add(getPenaltyPaid());
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

	void updatePaymentDetails(Money principal, Money interest,
			Money penalty, Money fees) {
		principalPaid = principalPaid.add(principal);
		interestPaid = interestPaid.add(interest);
		penaltyPaid = penaltyPaid.add(penalty);
		feesPaid = feesPaid.add(fees);
	}

	void decreaseBy(Money principal, Money interest, Money penalty,
			Money fees) {
		originalPrincipal = originalPrincipal.subtract(principal);
		originalFees = originalFees.subtract(fees);
		originalPenalty = originalPenalty.subtract(penalty);
		originalInterest = originalInterest.subtract(interest);
		if (loan.isUsingNewLoanSchedulingMethod())
		{
			rawAmountTotal = rawAmountTotal.subtract(interest.add(fees));
		}
	}
	
	void updateOriginalFees(Money charge){
		setOriginalFees(getOriginalFees().add(charge));
		if (loan.isUsingNewLoanSchedulingMethod())
		{
			rawAmountTotal = rawAmountTotal.add(charge);
		}
	}
	
	void updateOriginalPenalty(Money charge){
		setOriginalPenalty(getOriginalPenalty().add(charge));
	}
}
