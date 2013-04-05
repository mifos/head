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

package org.mifos.application.accounting.business;

import java.math.BigDecimal;
import java.util.Date;

public class GlDetailBO {
   private int transactionMasterId;
   private int transactionId;
   private String subAccount;
   private BigDecimal transactionAmount;
   private String amountAction;
   private String chequeNo;
   private Date chequeDate;
   private String bankName;
   private String bankBranch;
   private String transactionNarration;


public GlDetailBO(String subAccount,BigDecimal transactionAmount,String amountAction,
		   String chequeNo,Date chequeDate,String bankName,String bankBranch,String transactionnotes){
	   this.subAccount=subAccount;
	   this.transactionAmount=transactionAmount;
	   this.amountAction=amountAction;
	   this.chequeNo=chequeNo;
	   this.chequeDate=chequeDate;
	   this.bankName=bankName;
	   this.bankBranch=bankBranch;
	   this.transactionNarration=transactionnotes;
     }

   public GlDetailBO(int transactionId, String subAccount,BigDecimal transactionAmount,String amountAction,
		   String chequeNo,Date chequeDate,String bankName,String bankBranch){
	   this.transactionId = transactionId;
	   this.subAccount=subAccount;
	   this.transactionAmount=transactionAmount;
	   this.amountAction=amountAction;
	   this.chequeNo=chequeNo;
	   this.chequeDate=chequeDate;
	   this.bankName=bankName;
	   this.bankBranch=bankBranch;
     }


   public String getTransactionNarration() {
		return transactionNarration;
	}
	public void setTransactionNarration(String transactionNarration) {
		this.transactionNarration = transactionNarration;
	}
public int getTransactionMasterId() {
	return transactionMasterId;
}

public void setTransactionMasterId(int transactionMasterId) {
	this.transactionMasterId = transactionMasterId;
}

public int getTransactionId() {
	return transactionId;
}

public void setTransactionId(int transactionId) {
	this.transactionId = transactionId;
}

public String getSubAccount() {
	return subAccount;
}

public void setSubAccount(String subAccount) {
	this.subAccount = subAccount;
}

public BigDecimal getTransactionAmount() {
	return transactionAmount;
}

public void setTransactionAmount(BigDecimal transactionAmount) {
	this.transactionAmount = transactionAmount;
}

public String getAmountAction() {
	return amountAction;
}

public void setAmountAction(String amountAction) {
	this.amountAction = amountAction;
}

public String getChequeNo() {
	return chequeNo;
}

public void setChequeNo(String chequeNo) {
	this.chequeNo = chequeNo;
}

public Date getChequeDate() {
	return chequeDate;
}

public void setChequeDate(Date chequeDate) {
	this.chequeDate = chequeDate;
}

public String getBankName() {
	return bankName;
}

public void setBankName(String bankName) {
	this.bankName = bankName;
}

public String getBankBranch() {
	return bankBranch;
}

public void setBankBranch(String bankBranch) {
	this.bankBranch = bankBranch;
}









}
