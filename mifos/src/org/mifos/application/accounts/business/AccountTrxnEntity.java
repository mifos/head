/**

 * AccountTrxnEntity.java    version: 1.0

 

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

package org.mifos.application.accounts.business;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.mifos.application.accounts.financial.business.FinancialTransactionBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;

public class AccountTrxnEntity extends PersistentObject {

	private Integer accountTrxnId;

	private AccountBO account;

	private AccountPaymentEntity accountPayment;

	private PersonnelBO personnel;

	private AccountActionEntity accountActionEntity;

	private Money amount;

	private Date dueDate;

	private String comments;

	private Date actionDate;

	private CustomerBO customer;
	
	private UserContext userContext;
	
	private Timestamp trxnCreatedDate;

	private Set<FinancialTransactionBO> financialTransactions;

	private Short installmentId;

	private AccountTrxnEntity relatedTrxn;

	public AccountTrxnEntity() {
		super();
		createdDate = new Date(System.currentTimeMillis());
		financialTransactions = new HashSet<FinancialTransactionBO>();
	}

	public AccountBO getAccount() {
		return account;
	}

	public void setAccount(AccountBO account) {
		this.account = account;
	}

	public AccountActionEntity getAccountActionEntity() {
		return accountActionEntity;
	}

	public void setAccountActionEntity(AccountActionEntity accountActionEntity) {
		this.accountActionEntity = accountActionEntity;
	}

	public AccountPaymentEntity getAccountPayment() {
		return accountPayment;
	}

	public void setAccountPayment(AccountPaymentEntity accountPayment) {
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

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public CustomerBO getCustomer() {
		return customer;
	}

	public void setCustomer(CustomerBO customer) {
		this.customer = customer;
	}

	public PersonnelBO getPersonnel() {
		return personnel;
	}

	public void setPersonnel(PersonnelBO personnel) {
		this.personnel = personnel;
	}
	
	public UserContext getUserContext() {
		return userContext;
	}

	public void setUserContext(UserContext userContext) {
		this.userContext = userContext;
	}

	public void addFinancialTransction(
			FinancialTransactionBO financialTransaction) {
		financialTransaction.setAccountTrxn(this);
		this.financialTransactions.add(financialTransaction);
		setFinancialTransactions(this.financialTransactions);
	}

	public Set<FinancialTransactionBO> getFinancialTransactions() {
		return financialTransactions;
	}

	protected void setFinancialTransactions(
			Set<FinancialTransactionBO> financialTransactions) {
		this.financialTransactions = financialTransactions;
	}
	
	public Short getInstallmentId() {
		return installmentId;
	}

	public void setInstallmentId(Short installmentId) {
		this.installmentId = installmentId;
	}

	public AccountTrxnEntity getRelatedTrxn() {
		return relatedTrxn;
	}

	public void setRelatedTrxn(AccountTrxnEntity relatedTrxn) {
		this.relatedTrxn = relatedTrxn;
	}

	public Timestamp getTrxnCreatedDate() {
		return trxnCreatedDate;
	}

	public void setTrxnCreatedDate(Timestamp trxnCreatedDate) {
		this.trxnCreatedDate = trxnCreatedDate;
	}


	/**
	 * This method will be implemented by the sub class.
	 */
	public AccountTrxnEntity generateReverseTrxn(String adjustmentComment)throws ApplicationException, SystemException {
		return null;
	}

	
	

}
