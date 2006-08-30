/**
 
 * LoanSummary.java    version: xxx
 
 
 
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

package org.mifos.application.accounts.loan.util.valueobjects;

import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class acts as value object for loan summary.This is used to 
 * show values on detail page of a loan account and it maps to Loan_Summary table.
 */
public class LoanSummary extends ValueObject {
	
	public LoanSummary() {
		super();
	}

	private Integer accountId;
	private Loan loan;
	
	private Money originalPrincipal;
	
	private Money originalInterest;
	
	private Money originalFees;
	
	private Money originalPenalty;
	
	private Money principalPaid;
	
	private Money interestPaid;
	
	private Money feesPaid;
	
	private Money penaltyPaid;
	
	
	
	
	
	
	
	/**
	 * @return Returns the loan}.
	 */
	public Loan getLoan() {
		return loan;
	}
	
	/**
	 * @param loan The loan to set.
	 */
	public void setLoan(Loan loan) {
		this.loan = loan;
	}
	
	/**
	 * @return Returns the originalFees}.
	 */
	public Money getOriginalFees() {
		return originalFees;
	}
	
	/**
	 * @param originalFees The originalFees to set.
	 */
	public void setOriginalFees(Money originalFees) {
		this.originalFees = originalFees;
	}
	
	/**
	 * @return Returns the originalInterest}.
	 */
	public Money getOriginalInterest() {
		return originalInterest;
	}
	
	/**
	 * @param originalInterest The originalInterest to set.
	 */
	public void setOriginalInterest(Money originalInterest) {
		this.originalInterest = originalInterest;
	}
	
	/**
	 * @return Returns the originalPenalty}.
	 */
	public Money getOriginalPenalty() {
		return originalPenalty;
	}
	
	/**
	 * @param originalPenalty The originalPenalty to set.
	 */
	public void setOriginalPenalty(Money originalPenalty) {
		this.originalPenalty = originalPenalty;
	}
	
	/**
	 * @return Returns the originalPrincipal}.
	 */
	public Money getOriginalPrincipal() {
		return originalPrincipal;
	}
	
	/**
	 * @param originalPrincipal The originalPrincipal to set.
	 */
	public void setOriginalPrincipal(Money originalPrincipal) {
		this.originalPrincipal = originalPrincipal;
	}
	
	/**
	 * @return Returns the accountId}.
	 */
	public Integer getAccountId() {
		return accountId;
	}
	
	/**
	 * @param accountId The accountId to set.
	 */
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	
	/**
	 * This method is just to display on the UI.
	 * @return
	 */
	public Money getTotalLoanAmnt(){
		return getOriginalPrincipal().add(getOriginalFees()).add(getOriginalInterest()).add(getOriginalPenalty());
	}
	
	/**
	 * This method is just to display on the UI.
	 * @return
	 */
	public Money getTotalAmntPaid(){
		return getPrincipalPaid().add(getFeesPaid()).add(getInterestPaid()).add(getPenaltyPaid());
	}
	
	/**
	 * This method is just to display on the UI.
	 * @return
	 */
	public Money getPrincipalDue(){
		return getOriginalPrincipal().subtract(getPrincipalPaid());
	}
	
	/**
	 * This method is just to display on the UI.
	 * @return
	 */
	public Money getInterestDue(){
		return getOriginalInterest().subtract(getInterestPaid());
	}
	
	/**
	 * This method is just to display on the UI.
	 * @return
	 */
	public Money getPenaltyDue(){
		return getOriginalPenalty().subtract(getPenaltyPaid());
	}
	
	/**
	 * This method is just to display on the UI.
	 * @return
	 */
	public Money getFeesDue(){
		return getOriginalFees().subtract(getFeesPaid());
	}
	
	/**
	 * This method is just to display on the UI.
	 * @return
	 */
	public Money getTotalAmntDue(){
		return getPrincipalDue().add(getInterestDue()).add(getPenaltyDue()).add(getFeesDue());
	}
	
	
	
	/**
	 * @return Returns the interestPaid}.
	 */
	public Money getInterestPaid() {
		return interestPaid;
	}
	
	/**
	 * @param interestPaid The interestPaid to set.
	 */
	public void setInterestPaid(Money interestPaid) {
		this.interestPaid = interestPaid;
	}
	
	/**
	 * @return Returns the penaltyPaid}.
	 */
	public Money getPenaltyPaid() {
		return penaltyPaid;
	}
	
	/**
	 * @param penaltyPaid The penaltyPaid to set.
	 */
	public void setPenaltyPaid(Money penaltyPaid) {
		this.penaltyPaid = penaltyPaid;
	}
	
	/**
	 * @return Returns the feesPaid}.
	 */
	public Money getFeesPaid() {
		return feesPaid;
	}
	
	/**
	 * @param feesPaid The feesPaid to set.
	 */
	public void setFeesPaid(Money feesPaid) {
		this.feesPaid = feesPaid;
	}
	
	/**
	 * @return Returns the principalPaid}.
	 */
	public Money getPrincipalPaid() {
		return principalPaid;
	}
	
	/**
	 * @param principalPaid The principalPaid to set.
	 */
	public void setPrincipalPaid(Money principalPaid) {
		this.principalPaid = principalPaid;
	}
	
	
	
	
	
}
