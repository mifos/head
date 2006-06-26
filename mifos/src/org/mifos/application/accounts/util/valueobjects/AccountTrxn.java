/**

 * AccountTrxn.java    version: xxx

 

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
package org.mifos.application.accounts.util.valueobjects;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import org.mifos.application.master.util.valueobjects.AccountAction;
import org.mifos.application.master.util.valueobjects.Currency;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.valueobjects.ValueObject;

public class AccountTrxn extends ValueObject {
	public AccountTrxn() {
	}

	private Integer accountTrxnId;

	private Integer accountId;

	private Short personnelId;

	private AccountPayment accountPayment;

	private AccountAction accountAction;

	private Currency currency;

	private Money amount;

	private Date dueDate;

	private String comments;

	private Date actionDate;

	private Date createdDate;

	private Set<LoanTrxnDetail> loanDetails = new HashSet<LoanTrxnDetail>();

	private Set<FeesTrxnDetail> feeDetails = new HashSet<FeesTrxnDetail>();

	private Integer customerId;

	public AccountAction getAccountAction() {
		return accountAction;
	}

	public void setAccountAction(AccountAction accountAction) {
		this.accountAction = accountAction;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public AccountPayment getAccountPayment() {
		return accountPayment;
	}

	public void setAccountPayment(AccountPayment accountPayment) {
		this.accountPayment = accountPayment;
	}

	public Integer getAccountTrxnId() {
		return accountTrxnId;
	}

	public void setAccountTrxnId(Integer accountTrxnId) {
		this.accountTrxnId = accountTrxnId;
	}

	public Date getActionDate() {
		return actionDate;
	}

	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	public Money getAmount() {
		return amount;
	}

	public void setAmount(Money amount) {
		this.amount = amount;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Short getPersonnelId() {
		return personnelId;
	}

	public void setPersonnelId(Short personnelId) {
		this.personnelId = personnelId;
	}

	public Set<FeesTrxnDetail> getFeeDetails() {
		return feeDetails;
	}

	public void setFeeDetails(Set<FeesTrxnDetail> feeDetails) {
		this.feeDetails = feeDetails;
	}

	public Set<LoanTrxnDetail> getLoanDetails() {
		return loanDetails;
	}

	public void setLoanDetails(Set<LoanTrxnDetail> loanDetails) {
		this.loanDetails = loanDetails;
	}

	public void addLoanTrxnDetails(LoanTrxnDetail loanTrxn) {
		loanTrxn.setAccountTrxn(this);
		loanDetails.add(loanTrxn);
	}

	public void addFeesTrxnDetails(FeesTrxnDetail feesTrxn) {
		feesTrxn.setAccountTrxn(this);
		feeDetails.add(feesTrxn);

	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}



}
