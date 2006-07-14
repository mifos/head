/**

 * AccountPaymentEntity.java    version: 1.0

 

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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;

public class AccountPaymentEntity extends PersistentObject {


	private Integer paymentId;

	private AccountBO account;

	private PaymentTypeEntity paymentType;

	private Money amount;
	
	private String receiptNumber;

	private String voucherNumber;

	private String checkNumber;

	private Date paymentDate;

	private Date receiptDate;

	private String bankName;
	
	private UserContext userContext;

	private Set<AccountTrxnEntity> accountTrxns;
	public AccountPaymentEntity() {
		paymentDate = new Date(System.currentTimeMillis());
		paymentType = new PaymentTypeEntity();
		accountTrxns = new HashSet<AccountTrxnEntity>();
	}

	public AccountBO getAccount() {
		return account;
	}

	public void setAccount(AccountBO account) {
		this.account = account;
	}

	public Set<AccountTrxnEntity> getAccountTrxns() {
		return accountTrxns;
	}

	private void setAccountTrxns(Set<AccountTrxnEntity> accountTrxns) {
		this.accountTrxns = accountTrxns;
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

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}


	public Date getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
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

	public Integer getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}

	public void addAcountTrxn(AccountTrxnEntity accountTrxn) {
		accountTrxn.setAccountPayment(this);
		accountTrxn.setAccount(this.account);
		accountTrxns.add(accountTrxn);
	}

	public void setPaymentDetails(Money totalAmount, String recieptNum,
			Date recieptDate, Short paymentTypeId) {
		this.amount = totalAmount;
		this.receiptNumber = recieptNum;
		this.receiptDate = recieptDate;
		this.paymentType.setId(paymentTypeId);
	}

	public PaymentTypeEntity getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(PaymentTypeEntity paymentType) {
		this.paymentType = paymentType;
	}

	public Money getAmount() {
		return amount;
	}

	public void setAmount(Money amount) {
		this.amount = amount;
	}
	
	public UserContext getUserContext() {
		return userContext;
	}

	public void setUserContext(UserContext userContext) {
		this.userContext = userContext;
	}

	/**
	 *Create reverse entries of all the transactions associated with this payment
	 *and adds them to the set of transactions associated. 
	 * @throws SystemException 
	 */
	public List<AccountTrxnEntity> reversalAdjustment(String adjustmentComment)throws ApplicationException, SystemException {
		List<AccountTrxnEntity> newlyAddedTrxns = null;
		this.setAmount(getAmount().subtract(getAmount()));
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("The amount in account payment is " + getAmount().getAmountDoubleValue());
		
		
		if(null != getAccountTrxns() && getAccountTrxns().size() > 0){
			newlyAddedTrxns = new ArrayList<AccountTrxnEntity>();
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("The number of transactions before adjustment are " + getAccountTrxns().size());
			Set<AccountTrxnEntity> reverseAccntTrxns = new HashSet<AccountTrxnEntity>();
			for(AccountTrxnEntity accntTrxn : getAccountTrxns()){
				accntTrxn.setUserContext(getUserContext());
				MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Generating reverse transactions for transaction id " + accntTrxn.getAccountTrxnId());
				AccountTrxnEntity reverseAccntTrxn = accntTrxn.generateReverseTrxn(adjustmentComment);
				MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Amount associated with reverse transaction is " + reverseAccntTrxn.getAmount().getAmountDoubleValue());
				reverseAccntTrxns.add(reverseAccntTrxn);
				MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("After succesfully adding the reverse transaction" );
			}
			
			for(AccountTrxnEntity reverseAccntTrxn : reverseAccntTrxns){
				addAcountTrxn(reverseAccntTrxn);
			}
			
			newlyAddedTrxns.addAll(reverseAccntTrxns);
		}
		
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("After adding adjustment transactions the total no of transactions are " + getAccountTrxns().size());
		return newlyAddedTrxns;
	}	
}
