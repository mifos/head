/**

 * LoanScheduleEntity.java    version: xxx

 

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

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.LoanPaymentData;
import org.mifos.application.accounts.util.helpers.OverDueAmounts;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.framework.util.helpers.Money;

public class LoanScheduleEntity extends AccountActionDateEntity {
	private Money principal;

	private Money interest;

	private Money penalty;

	private Money miscFee;

	private Money miscPenalty;

	private Money principalPaid;

	private Money interestPaid;

	private Money penaltyPaid;

	private Money miscFeePaid;

	private Money miscPenaltyPaid;

	private Set<AccountFeesActionDetailEntity> accountFeesActionDetails;

	protected LoanScheduleEntity() {
		super(null, null, null, null, null);
	}

	public LoanScheduleEntity(AccountBO account, CustomerBO customer,
			Short installmentId, Date actionDate, PaymentStatus paymentStatus,
			Money principal, Money interest) {
		super(account, customer, installmentId, actionDate, paymentStatus);
		this.principal = principal;
		this.interest = interest;
		accountFeesActionDetails = new HashSet<AccountFeesActionDetailEntity>();
		this.penalty = new Money();
		this.miscFee = new Money();
		this.miscPenalty = new Money();
		this.principalPaid = new Money();
		this.interestPaid = new Money();
		this.penaltyPaid = new Money();
		this.miscFeePaid = new Money();
		this.miscPenaltyPaid = new Money();
	}

	public Money getInterest() {
		return interest;
	}

	public void setInterest(Money interest) {
		this.interest = interest;
	}

	public Money getInterestPaid() {
		return interestPaid;
	}

	public void setInterestPaid(Money interestPaid) {
		this.interestPaid = interestPaid;
	}

	public void setPenalty(Money penalty) {
		this.penalty = penalty;
	}

	public Money getPenaltyPaid() {
		return penaltyPaid;
	}

	public void setPenaltyPaid(Money penaltyPaid) {
		this.penaltyPaid = penaltyPaid;
	}

	public Money getPrincipal() {
		return principal;
	}

	public void setPrincipal(Money principal) {
		this.principal = principal;
	}

	public Money getPrincipalPaid() {
		return principalPaid;
	}

	public void setPrincipalPaid(Money principalPaid) {
		this.principalPaid = principalPaid;
	}

	public Money getPrincipalDue() {
		return getPrincipal().subtract(getPrincipalPaid());
	}

	public Money getInterestDue() {
		return getInterest().subtract(getInterestPaid());
	}

	public Money getPenalty() {
		return penalty;
	}

	public Set<AccountFeesActionDetailEntity> getAccountFeesActionDetails() {
		return accountFeesActionDetails;
	}

	public void addAccountFeesAction(
			AccountFeesActionDetailEntity accountFeesAction) {
		accountFeesActionDetails.add(accountFeesAction);
	}

	public Money getMiscFee() {
		return miscFee;
	}

	public void setMiscFee(Money miscFee) {
		this.miscFee = miscFee;
	}

	public Money getMiscFeePaid() {
		return miscFeePaid;
	}

	public void setMiscFeePaid(Money miscFeePaid) {
		this.miscFeePaid = miscFeePaid;
	}

	public Money getMiscPenalty() {
		return miscPenalty;
	}

	public void setMiscPenalty(Money miscPenalty) {
		this.miscPenalty = miscPenalty;
	}

	public Money getMiscPenaltyPaid() {
		return miscPenaltyPaid;
	}
	
	public Money getMiscPenaltyDue() {
		return getMiscPenalty().subtract(getMiscPenaltyPaid());
	}

	public void setMiscPenaltyPaid(Money miscPenaltyPaid) {
		this.miscPenaltyPaid = miscPenaltyPaid;
	}

	public Money getPenaltyDue() {
		return (getPenalty().add(getMiscPenalty())).subtract(getPenaltyPaid()
				.subtract(getMiscPenaltyPaid()));
	}

	public Money getTotalDue() {
		return getPrincipalDue().add(getInterestDue()).add(getPenaltyDue())
				.add(getMiscFeeDue());

	}

	public Money getTotalDueWithoutPricipal() {
		return getInterestDue().add(getPenaltyDue()).add(getMiscFeeDue());
	}

	public Money getTotalPenalty() {
		return getPenalty().add(getMiscPenalty());
	}

	public Money getTotalDueWithFees() {
		return getTotalDue().add(getTotalFeeDue());
	}

	public Money getTotalScheduleAmountWithFees() {
		return getPrincipal().add(
				getInterest().add(getPenalty()).add(
						getTotalScheduledFeeAmountWithMiscFee()).add(
						getMiscPenalty()));
	}
	
	public void setPaymentDetails(LoanPaymentData loanPaymentData,
			Date paymentDate) {
		this.principalPaid = this.principalPaid.add(loanPaymentData
				.getPrincipalPaid());
		this.interestPaid = this.interestPaid.add(loanPaymentData
				.getInterestPaid());
		this.penaltyPaid = this.penaltyPaid.add(loanPaymentData
				.getPenaltyPaid());
		this.miscFeePaid = this.miscFeePaid.add(loanPaymentData
				.getMiscFeePaid());
		this.miscPenaltyPaid = this.miscPenaltyPaid.add(loanPaymentData
				.getMiscPenaltyPaid());
		this.paymentStatus = loanPaymentData.getPaymentStatus();
		this.paymentDate = paymentDate;
	}

	public OverDueAmounts getDueAmnts() {
		OverDueAmounts overDueAmounts = new OverDueAmounts();
		overDueAmounts.setFeesOverdue(getTotalFeeDue().add(getMiscFeeDue()));
		overDueAmounts.setInterestOverdue(getInterestDue());
		overDueAmounts.setPenaltyOverdue(getPenaltyDue());
		overDueAmounts.setPrincipalOverDue(getPrincipalDue());
		overDueAmounts.setTotalPrincipalPaid(getPrincipalPaid());
		return overDueAmounts;
	}

	public void makeEarlyRepaymentEnteries(String payFullOrPartial) {
		if (payFullOrPartial.equals(LoanConstants.PAY_FEES_PENALTY_INTEREST)) {
			setPrincipalPaid(getPrincipalPaid().add(getPrincipalDue()));
			setInterestPaid(getInterestPaid().add(getInterestDue()));
			setPenaltyPaid(getPenaltyPaid().add(getPenaltyDue()));
			setMiscFeePaid(getMiscFeePaid().add(getMiscFee()));
			setMiscPenaltyPaid(getMiscPenaltyPaid().add(getMiscPenalty()));
			setPaymentStatus(PaymentStatus.PAID.getValue());
			setPaymentDate(new Date(System.currentTimeMillis()));
			Set<AccountFeesActionDetailEntity> accountFeesActionDetailSet = this
					.getAccountFeesActionDetails();
			for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountFeesActionDetailSet) {
				accountFeesActionDetailEntity
						.makeRepaymentEnteries(payFullOrPartial);
			}
		} else {
			setPrincipalPaid(getPrincipalPaid().add(getPrincipalDue()));
			setInterest(getInterestPaid());
			setPenalty(getPenaltyPaid());
			setMiscFee(getMiscFeePaid());
			setMiscPenalty(getMiscPenaltyPaid());
			setPaymentStatus(PaymentStatus.PAID.getValue());
			setPaymentDate(new Date(System.currentTimeMillis()));
			Set<AccountFeesActionDetailEntity> accountFeesActionDetailSet = this
					.getAccountFeesActionDetails();
			for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountFeesActionDetailSet) {
				accountFeesActionDetailEntity
						.makeRepaymentEnteries(payFullOrPartial);
			}
		}
	}

	public void updatePaymentDetails(Money principal, Money interest,
			Money penalty, Money miscPenalty, Money miscFee) {
		principalPaid = principalPaid.add(principal);
		interestPaid = interestPaid.add(interest);
		penaltyPaid = penaltyPaid.add(penalty);
		miscPenaltyPaid = miscPenaltyPaid.add(miscPenalty);
		miscFeePaid = miscFeePaid.add(miscFee);
	}

	public Money waiveCharges() {
		Money chargeWaived = new Money();
		chargeWaived = chargeWaived.add(getMiscFee()).add(getMiscPenalty());
		setMiscFee(new Money());
		setMiscPenalty(new Money());
		for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : getAccountFeesActionDetails()) {
			chargeWaived = chargeWaived.add(accountFeesActionDetailEntity
					.waiveCharges());
		}
		return chargeWaived;
	}

	public Money waiveFeeCharges() {
		Money chargeWaived = new Money();
		chargeWaived = chargeWaived.add(getMiscFeeDue());
		setMiscFee(new Money());
		for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : getAccountFeesActionDetails()) {
			chargeWaived = chargeWaived.add(accountFeesActionDetailEntity
					.waiveCharges());
		}
		return chargeWaived;
	}

	public void removeAccountFeesActionDetailEntity(
			AccountFeesActionDetailEntity accountFeesActionDetailEntity) {
		accountFeesActionDetails.remove(accountFeesActionDetailEntity);
	}

	public Money getMiscFeeDue() {
		return getMiscFee().subtract(getMiscFeePaid());
	}

	public Money getTotalFeeDue() {
		Money totalFees = new Money();
		for (AccountFeesActionDetailEntity obj : accountFeesActionDetails) {
			totalFees = totalFees.add(obj.getFeeDue());
		}
		return totalFees;
	}
	
	public Money getTotalFeeAmountPaidWithMiscFee() {
		Money totalFees = new Money();
		for (AccountFeesActionDetailEntity obj : accountFeesActionDetails) {
			totalFees = totalFees.add(obj.getFeeAmountPaid());
		}
		totalFees =totalFees.add(getMiscFeePaid());
		return totalFees;
	}
	
	public Money getTotalScheduledFeeAmountWithMiscFee() {
		Money totalFees = new Money();
		for (AccountFeesActionDetailEntity obj : accountFeesActionDetails) {
			totalFees = totalFees.add(obj.getFeeAmount());
		}
		totalFees = totalFees.add(getMiscFee());
		return totalFees;
	}

	public Money getTotalFees() {
		return getMiscFee().add(getTotalFeeDue());
	}
	
	public Money getTotalFeeDueWithMiscFeeDue() {
		return getMiscFeeDue().add(getTotalFeeDue());
	}

	public Money removeFees(Short feeId) {
		Money feeAmount = null;
		AccountFeesActionDetailEntity objectToRemove = null;
		Set<AccountFeesActionDetailEntity> accountFeesActionDetailSet = this
				.getAccountFeesActionDetails();
		for (AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountFeesActionDetailSet) {
			if (accountFeesActionDetailEntity.getFee().getFeeId().equals(feeId)) {
				objectToRemove = accountFeesActionDetailEntity;
				break;
			}
		}
		if (objectToRemove != null) {
			feeAmount = objectToRemove.getFeeAmount();
			this.removeAccountFeesActionDetailEntity(objectToRemove);
		}

		return feeAmount;
	}

	public AccountFeesActionDetailEntity getAccountFeesAction(
			Integer accountFeeId) {
		for (AccountFeesActionDetailEntity accountFeesAction : getAccountFeesActionDetails()) {
			if (accountFeesAction.getAccountFee().getAccountFeeId().equals(
					accountFeeId)) {
				return accountFeesAction;
			}
		}
		return null;
	}

	public Money waivePenaltyCharges() {
		Money chargeWaived = new Money();
		chargeWaived = chargeWaived.add(getMiscPenalty());
		setMiscPenalty(new Money());
		return chargeWaived;
	}

	public void applyPeriodicFees(Short feeId) {
		AccountFeesEntity accountFeesEntity = account
				.getAccountFees(feeId);
		AccountFeesActionDetailEntity accountFeesActionDetailEntity = new LoanFeeScheduleEntity(
				this, accountFeesEntity.getFees(),
				accountFeesEntity, accountFeesEntity.getAccountFeeAmount());
		addAccountFeesAction(accountFeesActionDetailEntity);
	}
	
	public void applyMiscCharge(Short chargeType,Money charge){
		if(chargeType.equals(Short.valueOf(AccountConstants.MISC_FEES)))
			setMiscFee(getMiscFee().add(charge));
		else if(chargeType.equals(Short.valueOf(AccountConstants.MISC_PENALTY)))
			setMiscPenalty(getMiscPenalty().add(charge));
	}
	
	public boolean isPrincipalZero(){
		return principal.getAmountDoubleValue() == 0.0 ;
	}

}
