/**

 * AccountPayment.java    version: xxx

 

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
package org.mifos.application.accounts.util.valueobjects;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import org.mifos.application.master.util.valueobjects.Currency;
import org.mifos.application.productdefinition.util.valueobjects.PaymentType;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.valueobjects.ValueObject;


public class AccountPayment extends ValueObject{
	
	private Integer paymentId;
	private Integer accountId;
	private short accountType;
	private Currency currency;
	private PaymentType paymentType;
	private Money amount;
	private String receiptNumber;
	private String voucherNumber;
	private String accountNumber;
	private String checkNumber;
	private String bankName;
	private Date paymentDate;
	private Short installmentId;
	private Date dueDate;
	private Set<AccountTrxn> accountTrxn=new HashSet<AccountTrxn>();
	private Short personnelId;
	private Date receiptDate;
	
	public AccountPayment(){
		super();
		this.setResultName("accountPayment");
	}
	
	/**
	 * @return Returns the personnelId.
	 */
	public Short getPersonnelId() {
		return personnelId;
	}
	/**
	 * @param personnelId The personnelId to set.
	 */
	public void setPersonnelId(Short personnelId) {
		this.personnelId = personnelId;
	}
	public Integer getAccountId() {
		return accountId;
	}
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	public Money getAmount() {
		return amount;
	}
	public void setAmount(Money amount) {
		this.amount = amount;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getCheckNumber() {
		return checkNumber;
	}
	public void setCheckNumber(String checkNumber) {
		this.checkNumber = checkNumber;
	}
	public Currency getCurrency() {
		return currency;
	}
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	public Date getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	public Integer getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}
	public PaymentType getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
	}
	public String getReceiptNumber() {
		return receiptNumber;
	}
	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}
	public String getVoucherNumber() {
		return voucherNumber;
	}
	public void setVoucherNumber(String voucherNumber) {
		this.voucherNumber = voucherNumber;
	}
	public Set getAccountTrxn() {
		return accountTrxn;
	}
	public void setAccountTrxn(Set<AccountTrxn> accountTrxn) {
		this.accountTrxn = accountTrxn;
	}
	
	public void addTrxn(AccountTrxn trxn){
		trxn.setAccountPayment(this);
		accountTrxn.add(trxn);
	}
	public short getAccountType() {
		return accountType;
	}
	public void setAccountType(short accountType) {
		this.accountType = accountType;
	}
	public Short getInstallmentId() {
		return installmentId;
	}
	public void setInstallmentId(Short installmentId) {
		this.installmentId = installmentId;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	/**
	 * @return Returns the accountNumber.
	 */
	public String getAccountNumber() {
		return accountNumber;
	}
	/**
	 * @param accountNumber The accountNumber to set.
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	/**
	 * @return Returns the receiptDate.
	 */
	public Date getReceiptDate() {
		return receiptDate;
	}

	/**
	 * @param receiptDate The receiptDate to set.
	 */
	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}
	
	
	
	
	
}
