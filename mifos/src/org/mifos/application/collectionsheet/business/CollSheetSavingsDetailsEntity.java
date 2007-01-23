/**

 * CollSheetSavingsDetailsEntity.java    version: 1.0

 

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
package org.mifos.application.collectionsheet.business;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.Money;

public class CollSheetSavingsDetailsEntity extends PersistentObject {

	public CollSheetSavingsDetailsEntity() {
		super();
	}
	
	private Long savingsDetailsId;
	
	private CollSheetCustBO collectionSheetCustomer;
	
	private Integer accountId;
	
	private Money accountBalance ;
	
	private Money recommendedAmntDue ;
	
	private Money amntOverDue;
	
	private Short installmentId;
	
	public Money getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(Money accountBalance) {
		this.accountBalance = accountBalance;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public Money getAmntOverDue() {
		return amntOverDue;
	}

	public void setAmntOverDue(Money amntOverDue) {
		this.amntOverDue = amntOverDue;
	}

	public CollSheetCustBO getCollectionSheetCustomer() {
		return collectionSheetCustomer;
	}

	public void setCollectionSheetCustomer(
			CollSheetCustBO collectionSheetCustomer) {
		this.collectionSheetCustomer = collectionSheetCustomer;
	}

	public Short getInstallmentId() {
		return installmentId;
	}

	public void setInstallmentId(Short installmentId) {
		this.installmentId = installmentId;
	}

	public Money getRecommendedAmntDue() {
		return recommendedAmntDue;
	}

	public void setRecommendedAmntDue(Money recommendedAmntDue) {
		this.recommendedAmntDue = recommendedAmntDue;
	}

	public Long getSavingsDetailsId() {
		return savingsDetailsId;
	}

	public void setSavingsDetailsId(Long savingsDetailsId) {
		this.savingsDetailsId = savingsDetailsId;
	}

	public Money getTotalSavingsAmntDue() {
		return getAmntOverDue().add(getRecommendedAmntDue());
	}

	/**
	 * Is this column in the database used for anything?  Reports?
	 */
	public void setTotalSavingsAmntDue(Money totalSavingsAmntDue) {
	}

	@Override
	public boolean equals(Object obj){
		CollSheetSavingsDetailsEntity collSheetSavingsDetailsObj = (CollSheetSavingsDetailsEntity)obj;
		if(null != savingsDetailsId && null != collSheetSavingsDetailsObj.getSavingsDetailsId()){
			return savingsDetailsId.equals(collSheetSavingsDetailsObj.getSavingsDetailsId());
		}else if(null != this.collectionSheetCustomer && null != collSheetSavingsDetailsObj.getCollectionSheetCustomer()){
			return this.accountId.equals(collSheetSavingsDetailsObj.getAccountId()) && this.collectionSheetCustomer.equals(collSheetSavingsDetailsObj.getCollectionSheetCustomer()); 
		}else{
			return super.equals(collSheetSavingsDetailsObj);
		}
		
	}
	
	@Override
	public int hashCode(){
		return this.accountId.hashCode();
	}

	/**
	 * This method sets values to the account details object , it also calculates over due amounts upto the current installment
	 * and sets it here.		
	 * @param accountActionDate
	 */
	public void addAccountDetails(AccountActionDateEntity accountActionDate)throws SystemException,ApplicationException   {
		SavingsScheduleEntity savingsSchedule = (SavingsScheduleEntity)accountActionDate;
		this.accountId = savingsSchedule.getAccount().getAccountId();
		SavingsBO savings= new SavingsPersistence().findById(accountId);
		this.installmentId = savingsSchedule.getInstallmentId();
		this.accountBalance = savings.getSavingsBalance();
		this.recommendedAmntDue = savingsSchedule.getDeposit() .subtract( savingsSchedule.getDepositPaid());
		this.amntOverDue = savings.getOverDueDepositAmount(savingsSchedule.getActionDate());
	}

}
