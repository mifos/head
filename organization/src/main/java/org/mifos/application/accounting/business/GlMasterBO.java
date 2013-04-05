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
import java.util.List;

import org.mifos.framework.business.AbstractBusinessObject;

public class GlMasterBO extends AbstractBusinessObject{

   private int transactionMasterId;
   private Date transactionDate;
   private String transactionType;
   private int fromOfficeLevel;
   private String fromOfficeId;
   private String mainAccount;
   private BigDecimal transactionAmount;
   private String amountAction;
   private String transactionNarration;
   private int toOfficeLevel;
   private String toOfficeId;
   private List<GlDetailBO> glDetailBOList;
   private String status;
   private int transactionBy;
   private int stage;
   private String memberId;

public String getMemberId() {
	return memberId;
}

public void setMemberId(String memberId) {
	this.memberId = memberId;
}

public int getStage() {
	return stage;
}

public void setStage(int stage) {
	this.stage = stage;
}

public GlMasterBO() {
	   //default constructor for Hibernate
}

   public GlMasterBO(Date transactionDate,String transactionType,int fromOfficeLevel,String fromOfficeId,String mainAccount,BigDecimal transactionAmount,String amountAction,
		   String transactionNarration,List<GlDetailBO> glDetailBOList){
	   this.transactionDate=transactionDate;
	   this.transactionType=transactionType;
	   this.fromOfficeLevel=fromOfficeLevel;
	   this.fromOfficeId=fromOfficeId;
	   this.mainAccount=mainAccount;
	   this.transactionAmount=transactionAmount;
	   this.amountAction=amountAction;
	   this.transactionNarration=transactionNarration;
	   this.glDetailBOList=glDetailBOList;
     }

public int getTransactionMasterId() {
	return transactionMasterId;
}

public void setTransactionMasterId(int transactionMasterId) {
	this.transactionMasterId = transactionMasterId;
}

public Date getTransactionDate() {
	return transactionDate;
}

public void setTransactionDate(Date transactionDate) {
	this.transactionDate = transactionDate;
}

public String getTransactionType() {
	return transactionType;
}

public void setTransactionType(String transactionType) {
	this.transactionType = transactionType;
}

public int getFromOfficeLevel() {
	return fromOfficeLevel;
}

public void setFromOfficeLevel(int fromOfficeLevel) {
	this.fromOfficeLevel = fromOfficeLevel;
}

public String getFromOfficeId() {
	return fromOfficeId;
}

public void setFromOfficeId(String fromOfficeId) {
	this.fromOfficeId = fromOfficeId;
}

public String getMainAccount() {
	return mainAccount;
}

public void setMainAccount(String mainAccount) {
	this.mainAccount = mainAccount;
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

public String getTransactionNarration() {
	return transactionNarration;
}

public void setTransactionNarration(String transactionNarration) {
	this.transactionNarration = transactionNarration;
}

public int getToOfficeLevel() {
	return toOfficeLevel;
}

public void setToOfficeLevel(int toOfficeLevel) {
	this.toOfficeLevel = toOfficeLevel;
}

public String getToOfficeId() {
	return toOfficeId;
}

public void setToOfficeId(String toOfficeId) {
	this.toOfficeId = toOfficeId;
}

public List<GlDetailBO> getGlDetailBOList() {
	return glDetailBOList;
}

public void setGlDetailBOList(List<GlDetailBO> glDetailBOList) {
	this.glDetailBOList = glDetailBOList;
}

public String getStatus() {
	return status;
}

public void setStatus(String status) {
	this.status = status;
}

public int getTransactionBy() {
	return transactionBy;
}

public void setTransactionBy(int transactionBy) {
	this.transactionBy = transactionBy;
}

}
