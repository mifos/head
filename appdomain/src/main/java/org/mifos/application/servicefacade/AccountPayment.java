/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
package org.mifos.application.servicefacade;

import java.math.BigDecimal;
import java.util.Date;

import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.framework.util.helpers.Constants;

/**
 * By Hugo Technologies
 */
public class AccountPayment {
    private Integer paymentId;
    private Integer customerId;
    private String customerName;
    private Integer accountId;
    private String productName;
    private BigDecimal amount;
    private BigDecimal principal;
    private BigDecimal interest;//= BigDecimal.ZERO
    private Date transactiondate;
    
    public AccountPayment() {
        // default constructor for hibernate
    }



	public BigDecimal getPrincipal() {
		return principal;
	}



	public void setPrincipal(BigDecimal principal) {
		this.principal = principal;
	}



	public BigDecimal getInterest() {
		return interest;
	}



	public void setInterest(BigDecimal interest) {
		this.interest = interest;
	}



	public Integer getPaymentId() {
		return paymentId;
	}



	public void setPaymentId(Integer paymentId) {
		this.paymentId = paymentId;
	}



	public Integer getCustomerId() {
		return customerId;
	}



	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}



	public String getCustomerName() {
		return customerName;
	}



	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}



	public Integer getAccountId() {
		return accountId;
	}



	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}



	public String getProductName() {
		return productName;
	}



	public void setProductName(String productName) {
		this.productName = productName;
	}



	public BigDecimal getAmount() {
		return amount;
	}



	public void setAmount(BigDecimal amount) {
		 if (amount != null) {
	            this.amount = amount;
	        }
	}



	public Date getTransactiondate() {
		return transactiondate;
	}



	public void setTransactiondate(Date transactiondate) {
		this.transactiondate = transactiondate;
	}

   
}
