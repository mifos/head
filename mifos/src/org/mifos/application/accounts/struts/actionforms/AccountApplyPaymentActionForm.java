/**

 * AccountApplyPaymentActionForm.java    version: xxx

 

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
package org.mifos.application.accounts.struts.actionforms;

import org.apache.struts.validator.ValidatorActionForm;
import org.mifos.framework.util.helpers.Money;

public class AccountApplyPaymentActionForm extends ValidatorActionForm{
	private String input;
	private String transactionDate;
	private Money amount;
	private String receiptId;
	private String receiptDate;
	private String paymentTypeId;
	private String globalAccountNum;
	private String accountId;
	

	public Money getAmount() {
		return amount;
	}
	
	public void setAmount(Money amount) {
		this.amount = amount;
	}
	
	public String getInput() {
		return input;
	}
	
	public void setInput(String input) {
		this.input = input;
	}
	
	public String getPaymentTypeId() {
		return paymentTypeId;
	}
	
	public void setPaymentTypeId(String paymentTypeId) {
		this.paymentTypeId = paymentTypeId;
	}
	
	public String getReceiptDate() {
		return receiptDate;
	}
	
	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
	}
	
	public String getReceiptId() {
		return receiptId;
	}
	
	public void setReceiptId(String receiptId) {
		this.receiptId = receiptId;
	}
	
	public String getTransactionDate() {
		return transactionDate;
	}
	
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	
	public String getGlobalAccountNum() {
		return globalAccountNum;
	}

	public void setGlobalAccountNum(String globalAccountNum) {
		this.globalAccountNum = globalAccountNum;
	}
}
