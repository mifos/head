/**

 * AccountActionDateEntity.java    version: 1.0

 

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

package org.mifos.application.accounts.business;


import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.CustomerAccountPaymentData;
import org.mifos.application.accounts.util.helpers.LoanPaymentData;
import org.mifos.application.accounts.util.helpers.OverDueAmounts;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.helpers.Money;

public class AccountActionDateEntity extends PersistentObject {

	private Integer actionDateId;

	private AccountBO account;

	private CustomerBO customer;

	private Date actionDate;

	private Money deposit;

	private Money principal;

	private Money interest;

	private Money penalty;

	private Money miscFee;

	private Money miscFeePaid;

	private Money miscPenalty;
	
	private Money miscPenaltyPaid;

	private Short parentFlag;

	private Money depositPaid;

	private Money principalPaid;

	private Money interestPaid;

	private Money penaltyPaid;

	private Short paymentStatus;

	private Short installmentId;

	private Date paymentDate;

	private Set<AccountFeesActionDetailEntity> accountFeesActionDetails;

	public AccountActionDateEntity() {
		super();
		accountFeesActionDetails = new HashSet<AccountFeesActionDetailEntity>();
	}

	public AccountBO getAccount() {
		return account;
	}

	public void setAccount(AccountBO account) {
		this.account = account;
	}

	public Set<AccountFeesActionDetailEntity> getAccountFeesActionDetails() {
		return accountFeesActionDetails;
	}

	private void setAccountFeesActionDetails(
			Set<AccountFeesActionDetailEntity> accountFeesActionDetails) {
		this.accountFeesActionDetails = accountFeesActionDetails;
	}

	public Date getActionDate() {
		return actionDate;
	}

	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	public Integer getActionDateId() {
		return actionDateId;
	}

	public void setActionDateId(Integer actionDateId) {
		this.actionDateId = actionDateId;
	}

	public CustomerBO getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerBO customer) {
		this.customer = customer;
	}

	public Money getDeposit() {
		return deposit;
	}

	public void setDeposit(Money deposit) {
		this.deposit = deposit;
	}

	public Money getDepositPaid() {
		return depositPaid;
	}

	public void setDepositPaid(Money depositPaid) {
		this.depositPaid = depositPaid;
	}

	public Short getInstallmentId() {
		return installmentId;
	}

	public void setInstallmentId(Short installmentId) {
		this.installmentId = installmentId;
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

	public Short getParentFlag() {
		return parentFlag;
	}

	public void setParentFlag(Short parentFlag) {
		this.parentFlag = parentFlag;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Short getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(Short paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public Money getPenalty() {
		return penalty;
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
	
	public Money getMiscPenaltyPaid() {
		return miscPenaltyPaid;
	}

	public void setMiscPenaltyPaid(Money miscPenaltyPaid) {
		this.miscPenaltyPaid = miscPenaltyPaid;
	}

	public void addAccountFeesAction(
			AccountFeesActionDetailEntity accountFeesAction) {
		accountFeesAction.setAccountActionDate(this);
		accountFeesActionDetails.add(accountFeesAction);
	}

	public void setPaymentDetails(LoanPaymentData loanPaymentData,Date transactionDate) {
		this.principalPaid = loanPaymentData.getPrincipalPaid();
		this.interestPaid = loanPaymentData.getInterestPaid();
		this.penaltyPaid = loanPaymentData.getPenaltyPaid();
		this.miscFeePaid = loanPaymentData.getMiscFeePaid();
		this.miscPenaltyPaid = loanPaymentData.getMiscPenaltyPaid();
		this.paymentStatus = loanPaymentData.getPaymentStatus();
		paymentDate = transactionDate;
	}
	
	public void setPaymentDetails(Money depositAmount,Short paymentStatus,Date transactionDate) {
		this.depositPaid = this.depositPaid.add(depositAmount);
		this.paymentStatus = paymentStatus;
		paymentDate = transactionDate;
	}
	
	public void setPaymentDetails(CustomerAccountPaymentData customerAccountPaymentData,Date transactionDate) {
		this.miscFeePaid = customerAccountPaymentData.getMiscFeePaid();
		this.miscPenaltyPaid = customerAccountPaymentData.getMiscPenaltyPaid();
		this.paymentStatus = customerAccountPaymentData.getPaymentStatus();
		paymentDate = transactionDate;
	}
	
	public Money getPrincipalDue() {
		return getPrincipal().subtract(getPrincipalPaid());
	}

	public Money getInterestDue() {
		return getInterest().subtract(getInterestPaid()) ;
	}

	public Money getPenaltyDue() {
		return (getPenalty().add(getMiscPenalty())).subtract(getPenaltyPaid().subtract(getMiscPenaltyPaid()));
	}

	public Money getMiscFeeDue() {
		return getMiscFee().subtract(getMiscFeePaid());
	}

	public Money getTotalFeeDue() {
		Money totalFees = new Money();
		for (AccountFeesActionDetailEntity obj : accountFeesActionDetails) {
			totalFees = totalFees.add(obj.getFeeDue()) ;
		}
		return totalFees;
	}
	
	public Money getTotalFees(){
		return getMiscFee().add(getTotalFeeDue());
	}

	public Money getTotalDueWithFees() {
		return getTotalDue().add(getTotalFeeDue()) ;
	}

	public Money getTotalDue() {
		return getPrincipalDue().add(getInterestDue()).add(getPenaltyDue()).add(getMiscFeeDue()); 
			
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

	/**
	 * This method returns the amounts due for this particular installment.
	 * It creates a new OverDueAmounts object and returns that.
	 */
	public OverDueAmounts getDueAmnts() {
		OverDueAmounts	overDueAmounts = new OverDueAmounts();
		overDueAmounts.setFeesOverdue(getTotalFeeDue().add(getMiscFeeDue()));
		overDueAmounts.setInterestOverdue(getInterestDue());
		overDueAmounts.setPenaltyOverdue(getPenaltyDue());
		overDueAmounts.setPrincipalOverDue(getPrincipalDue());
		overDueAmounts.setTotalPrincipalPaid(getPrincipalPaid());
		return overDueAmounts;
	}
	
	public void removeAccountFeesActionDetailEntity(AccountFeesActionDetailEntity accountFeesActionDetailEntity){
		accountFeesActionDetails.remove(accountFeesActionDetailEntity);
		setAccountFeesActionDetails(accountFeesActionDetails);
	}
	
	
	public Money removeFees(Short feeId)
	{
		Money feeAmount =null;
		AccountFeesActionDetailEntity objectToRemove = null;
		Set<AccountFeesActionDetailEntity> accountFeesActionDetailSet=this.getAccountFeesActionDetails();
		for(AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountFeesActionDetailSet){
			if(accountFeesActionDetailEntity.getFee().getFeeId().equals(feeId)){
				objectToRemove = accountFeesActionDetailEntity;
				break;
			}
		}
		if(objectToRemove!=null){
			feeAmount=objectToRemove.getFeeAmount();
			this.removeAccountFeesActionDetailEntity(objectToRemove);
		}
		
		return feeAmount;
	}
	
	public void makeEarlyRepaymentEnteries(String payFullOrPartial){
		if(payFullOrPartial.equals(LoanConstants.PAY_FEES_PENALTY_INTEREST)){
			setPrincipalPaid(getPrincipal());
			setInterestPaid(getInterest());
			setPenaltyPaid(getPenalty());
			setMiscFeePaid(getMiscFee());
			setMiscPenaltyPaid(getMiscPenalty());
			setPaymentStatus(PaymentStatus.PAID.getValue());
			setPaymentDate(new Date(System.currentTimeMillis()));
			Set<AccountFeesActionDetailEntity> accountFeesActionDetailSet=this.getAccountFeesActionDetails();
			for(AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountFeesActionDetailSet){
				accountFeesActionDetailEntity.makeRepaymentEnteries(payFullOrPartial);
			}
		}else{
			setPrincipalPaid(getPrincipal());
			setInterest(new Money());
			setPenalty(new Money());
			setMiscFee(new Money());
			setMiscPenalty(new Money());
			setPaymentStatus(PaymentStatus.PAID.getValue());
			setPaymentDate(new Date(System.currentTimeMillis()));
			Set<AccountFeesActionDetailEntity> accountFeesActionDetailSet=this.getAccountFeesActionDetails();
			for(AccountFeesActionDetailEntity accountFeesActionDetailEntity : accountFeesActionDetailSet){
				accountFeesActionDetailEntity.makeRepaymentEnteries(payFullOrPartial);
			}
		}
	}
	
	public int compareDate(java.util.Date date){
		return getActionDate().compareTo(date);
	}
	
	public AccountFeesActionDetailEntity getAccountFeesAction(Integer accountFeeId) {
		for(AccountFeesActionDetailEntity accountFeesAction : getAccountFeesActionDetails()) {
			if(accountFeesAction.getAccountFee().getAccountFeeId().equals(accountFeeId)) {
				return accountFeesAction;
			}
		}
		return null;
	}
	
	public void updatePaymentDetails(Money principal, Money interest, Money penalty, Money miscPenalty,Money miscFee) {
		principalPaid = principalPaid.add(principal);
		interestPaid = interestPaid.add(interest);
		penaltyPaid = penaltyPaid.add(penalty);
		miscPenaltyPaid = miscPenaltyPaid.add(miscPenalty);
		miscFeePaid = miscFeePaid.add(miscFee);
	}
	
	public Money waiveCharges(){
		Money chargeWaived=new Money();
		chargeWaived=chargeWaived.add(getMiscFee()).add(getMiscPenalty());
		setMiscFee(new Money());
		setMiscPenalty(new Money());
		for(AccountFeesActionDetailEntity accountFeesActionDetailEntity :  getAccountFeesActionDetails()){
			chargeWaived=chargeWaived.add(accountFeesActionDetailEntity.waiveCharges());
		}
		return chargeWaived;
	}
	
	public void waiveDepositDue() {
		Money depositDue = getTotalDepositDue();
		deposit = deposit.subtract(depositDue);
		setPaymentStatus(PaymentStatus.PAID.getValue());
	}
	public Money waiveFeeCharges(){
		Money chargeWaived=new Money();
		chargeWaived=chargeWaived.add(getMiscFee());
		setMiscFee(new Money());		
		for(AccountFeesActionDetailEntity accountFeesActionDetailEntity :  getAccountFeesActionDetails()){
			chargeWaived=chargeWaived.add(accountFeesActionDetailEntity.waiveCharges());
		}
		return chargeWaived;
	}
	public Money waivePenaltyCharges(){
		Money chargeWaived=new Money();		
		chargeWaived=chargeWaived.add(getMiscPenalty());
		setMiscPenalty(new Money());		
		return chargeWaived;
	}
	
	public void applyPeriodicFees(Short feeId){
		AccountFeesActionDetailEntity accountFeesActionDetailEntity = new AccountFeesActionDetailEntity();
		AccountFeesEntity accountFeesEntity  = account.getPeriodicAccountFees(feeId);		
		accountFeesActionDetailEntity.setInstallmentId(getInstallmentId());				
		accountFeesActionDetailEntity.setFee(accountFeesEntity.getFees());		
		accountFeesActionDetailEntity.setAccountFee(accountFeesEntity);
		accountFeesActionDetailEntity.setFeeAmount(accountFeesEntity.getAccountFeeAmount());		
		accountFeesActionDetailEntity.setAccountFee(accountFeesEntity);
		addAccountFeesAction(accountFeesActionDetailEntity);
	}
}
