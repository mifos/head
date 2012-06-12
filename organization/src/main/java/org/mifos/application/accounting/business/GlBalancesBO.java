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

import org.mifos.framework.business.AbstractBusinessObject;

public class GlBalancesBO extends AbstractBusinessObject{

   private int glBalancesId;
   private int officeLevel;
   private String officeId;
   private String glCodeValue;
   private BigDecimal openingBalance;
   private BigDecimal transactionDebitSum;
   private BigDecimal transactionCreditSum;
   private BigDecimal closingBalance;
   private FinancialYearBO financialYearBO;



public int getGlBalancesId() {
	return glBalancesId;
}

public void setGlBalancesId(int glBalancesId) {
	this.glBalancesId = glBalancesId;
}

public FinancialYearBO getFinancialYearBO() {
	return financialYearBO;
}

public void setFinancialYearBO(FinancialYearBO financialYearBO) {
	this.financialYearBO = financialYearBO;
}

public int getOfficeLevel() {
	return officeLevel;
}

public void setOfficeLevel(int officeLevel) {
	this.officeLevel = officeLevel;
}

public String getOfficeId() {
	return officeId;
}

public void setOfficeId(String officeId) {
	this.officeId = officeId;
}



public String getGlCodeValue() {
	return glCodeValue;
}

public void setGlCodeValue(String glCodeValue) {
	this.glCodeValue = glCodeValue;
}

public BigDecimal getOpeningBalance() {
	return openingBalance;
}

public void setOpeningBalance(BigDecimal openingBalance) {
	this.openingBalance = openingBalance;
}

public BigDecimal getTransactionDebitSum() {
	return transactionDebitSum;
}

public void setTransactionDebitSum(BigDecimal transactionDebitSum) {
	this.transactionDebitSum = transactionDebitSum;
}

public BigDecimal getTransactionCreditSum() {
	return transactionCreditSum;
}

public void setTransactionCreditSum(BigDecimal transactionCreditSum) {
	this.transactionCreditSum = transactionCreditSum;
}

public BigDecimal getClosingBalance() {
	return closingBalance;
}

public void setClosingBalance(BigDecimal closingBalance) {
	this.closingBalance = closingBalance;
}

public GlBalancesBO() {
	   this.transactionDebitSum=new BigDecimal(0.0);
	   this.transactionCreditSum=new BigDecimal(0.0);
	   this.openingBalance=new BigDecimal(0.0);
	   this.closingBalance=new BigDecimal(0.0);
}


   public GlBalancesBO(int glBalancesId,int officeLevel,String officeId,String glCodeValue,BigDecimal openingBalance,BigDecimal transactionDebitSum,
		   BigDecimal transactionCreditSum,BigDecimal closingBalance){
	   this.glBalancesId=glBalancesId;
	   this.officeLevel=officeLevel;
	   this.officeId=officeId;
	   this.glCodeValue=glCodeValue;
	   this.openingBalance=openingBalance;
	   this.transactionDebitSum=transactionDebitSum;
	   this.transactionCreditSum=transactionCreditSum;
	   this.closingBalance=closingBalance;
     }



}
