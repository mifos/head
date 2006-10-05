/**

 * AccountTrxnEntity.java    version: 1.0

 

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

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.financial.business.FinancialTransactionBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.helpers.Money;

public abstract class AccountTrxnEntity extends PersistentObject {

	private final Integer accountTrxnId = null;

	private final AccountBO account;

	private final AccountPaymentEntity accountPayment;

	private final PersonnelBO personnel;

	private final AccountActionEntity accountActionEntity;

	private final Money amount;

	private final Date dueDate;

	private final String comments;

	private final Date actionDate;

	private final CustomerBO customer;

	private final Timestamp trxnCreatedDate;

	private final Set<FinancialTransactionBO> financialTransactions;

	private final Short installmentId;

	private final AccountTrxnEntity relatedTrxn;

	protected AccountTrxnEntity() {
		createdDate = new Date(System.currentTimeMillis());
		trxnCreatedDate = new Timestamp(System.currentTimeMillis());
		financialTransactions = new HashSet<FinancialTransactionBO>();
		accountActionEntity = null;
		installmentId = null;
		dueDate = null;
		customer = null;
		personnel = null;
		actionDate = null;
		account = null;
		relatedTrxn = null;
		amount = null;
		accountPayment = null;
		comments = null;
	}

	public AccountTrxnEntity(AccountPaymentEntity accountPayment,
			AccountActionEntity accountActionEntity, Short installmentId,
			Date dueDate, PersonnelBO personnel, CustomerBO customer,
			Date actionDate, Money amount, String comments,
			AccountTrxnEntity relatedTrxn) {
		createdDate = new Date(System.currentTimeMillis());
		trxnCreatedDate = new Timestamp(System.currentTimeMillis());
		financialTransactions = new HashSet<FinancialTransactionBO>();
		this.account = accountPayment.getAccount();
		this.accountActionEntity = accountActionEntity;
		this.installmentId = installmentId;
		this.dueDate = dueDate;
		if (customer == null)
			this.customer = account.getCustomer();
		else
			this.customer = customer;
		this.personnel = personnel;
		this.actionDate = actionDate;
		this.amount = amount;
		this.relatedTrxn = relatedTrxn;
		this.accountPayment = accountPayment;
		this.comments = comments;
	}

	public AccountBO getAccount() {
		return account;
	}

	public AccountActionEntity getAccountActionEntity() {
		return accountActionEntity;
	}

	public AccountPaymentEntity getAccountPayment() {
		return accountPayment;
	}

	public Integer getAccountTrxnId() {
		return accountTrxnId;
	}

	public Date getActionDate() {
		return actionDate;
	}

	public Money getAmount() {
		return amount;
	}

	public String getComments() {
		return comments;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public CustomerBO getCustomer() {
		return customer;
	}

	public PersonnelBO getPersonnel() {
		return personnel;
	}

	public void addFinancialTransction(
			FinancialTransactionBO financialTransaction) {
		financialTransaction.setAccountTrxn(this);
		this.financialTransactions.add(financialTransaction);
	}

	public Set<FinancialTransactionBO> getFinancialTransactions() {
		return financialTransactions;
	}

	public Short getInstallmentId() {
		return installmentId;
	}

	public AccountTrxnEntity getRelatedTrxn() {
		return relatedTrxn;
	}

	public Timestamp getTrxnCreatedDate() {
		return trxnCreatedDate;
	}

	public abstract AccountTrxnEntity generateReverseTrxn(String adjustmentComment)
	throws AccountException;

}
