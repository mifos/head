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
import org.mifos.application.collectionsheet.persistence.service.CollectionSheetPersistenceService;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.PersistenceServiceName;

public class CollSheetSavingsDetailsEntity extends PersistentObject {

	CollectionSheetPersistenceService collSheetPersistService;
	public CollSheetSavingsDetailsEntity() {
		super();
		//this.accountBalance = new Money();
	}
	
	private Long savingsDetailsId;
	
	private CollSheetCustBO collectionSheetCustomer;
	
	private Integer accountId;
	
	private Money accountBalance ;
	
	private Money recommendedAmntDue ;
	
	private Money amntOverDue;
	
	private Short installmentId;
	
	private Money totalSavingsAmntDue;

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
		return getAmntOverDue() .add(getRecommendedAmntDue());
	}

	public void setTotalSavingsAmntDue(Money totalSavingsAmntDue) {
		this.totalSavingsAmntDue = totalSavingsAmntDue;
	}
	
/**
	 * If the loanDetailsId is not null it compares for equality based on it else it checks for collectionSheetCustomer not being
	 * null in which case it returns true if both loanDetailsId and collectionSheetLoanDetails.collSheetCustomer is equal to the corresponding 
	 * properties of the passed object else it calls super.equals();
	 * @param obj - Object to be compared for equality.
	 * @return - Returns true if the objects are equal else returns false.
	 */
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
		collSheetPersistService = (CollectionSheetPersistenceService)ServiceFactory.getInstance().getPersistenceService(PersistenceServiceName.CollectionSheet);
		SavingsBO savings= collSheetPersistService.getSavingsAccount(accountId);
		this.installmentId = savingsSchedule.getInstallmentId();
		this.accountBalance = savings.getSavingsBalance();
		this.recommendedAmntDue = savingsSchedule.getDeposit() .subtract( savingsSchedule.getDepositPaid());
		this.amntOverDue = savings.getOverDueDepositAmount(savingsSchedule.getActionDate());
	}

}
