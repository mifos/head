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

import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;

public class AccountPaymentEntity extends PersistentObject {


	private final Integer paymentId=null;

	private final AccountBO account;

	private final PaymentTypeEntity paymentType;

	private final String receiptNumber;

	private final String voucherNumber;

	private final String checkNumber;

	private final Date receiptDate;

	private final String bankName;
	
	private final Date paymentDate;
	
	private Money amount;

	private Set<AccountTrxnEntity> accountTrxns;
	
	protected AccountPaymentEntity() {
		accountTrxns = new HashSet<AccountTrxnEntity>();
		paymentDate = new Date(System.currentTimeMillis());
		account=null;
		paymentType=null;
		receiptNumber=null;
		voucherNumber=null;
		checkNumber=null;
		receiptDate=null;
		bankName=null;
	}
	
	public AccountPaymentEntity(AccountBO account,Money amount,
			String receiptNumber, 
			Date receiptDate, PaymentTypeEntity paymentType) {
		this.accountTrxns=new HashSet<AccountTrxnEntity>();
		this.paymentDate = new Date(System.currentTimeMillis());
		this.account=account;
		this.receiptNumber=receiptNumber;
		this.paymentType=paymentType;
		this.receiptDate=receiptDate;
		this.amount=amount;
		this.bankName=null;
		this.voucherNumber=null;
		this.checkNumber=null;
		
	}

	
	public Integer getPaymentId() {
		return paymentId;
	}

	public AccountBO getAccount() {
		return account;
	}
	
	public PaymentTypeEntity getPaymentType() {
		return paymentType;
	}

	public Date getPaymentDate() {
		return paymentDate;
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

	public String getCheckNumber() {
		return checkNumber;
	}

	public Date getReceiptDate() {
		return receiptDate;
	}

	public String getReceiptNumber() {
		return receiptNumber;
	}

	public String getVoucherNumber() {
		return voucherNumber;
	}
	
	public Money getAmount() {
		return amount;
	}
	
	public void setAmount(Money amount) {
		this.amount = amount;
	}

	public void addAcountTrxn(AccountTrxnEntity accountTrxn) {
		accountTrxns.add(accountTrxn);
	}

	/**
	 * Create reverse entries of all the transactions associated with this payment
	 * and adds them to the set of transactions associated. 
	 */
	public List<AccountTrxnEntity> reversalAdjustment(String adjustmentComment)
	throws AccountException {
		List<AccountTrxnEntity> newlyAddedTrxns = null;
		this.setAmount(getAmount().subtract(getAmount()));
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("The amount in account payment is " + getAmount().getAmountDoubleValue());
		
		
		if(null != getAccountTrxns() && getAccountTrxns().size() > 0){
			newlyAddedTrxns = new ArrayList<AccountTrxnEntity>();
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("The number of transactions before adjustment are " + getAccountTrxns().size());
			Set<AccountTrxnEntity> reverseAccntTrxns = new HashSet<AccountTrxnEntity>();
			for(AccountTrxnEntity accntTrxn : getAccountTrxns()){
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
