/**

 * FeesTrxnDetailEntity.java    version: 1.0

 

 * Copyright © 2005-2006 Grameen Foundation USA

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


import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.helpers.Money;

public class FeesTrxnDetailEntity extends PersistentObject {

	private Integer feesTrxnId;

	private AccountTrxnEntity accountTrxn;

	private AccountFeesEntity accountFees;

	private Money feeAmount;

	public AccountFeesEntity getAccountFees() {
		return accountFees;
	}

	public void setAccountFees(AccountFeesEntity accountFees) {
		this.accountFees = accountFees;
	}

	public AccountTrxnEntity getAccountTrxn() {
		return accountTrxn;
	}

	public void setAccountTrxn(AccountTrxnEntity accountTrxn) {
		this.accountTrxn = accountTrxn;
	}

	

	public Money getFeeAmount() {
		return feeAmount;
	}

	public void setFeeAmount(Money feeAmount) {
		this.feeAmount = feeAmount;
	}

	public Integer getFeesTrxnId() {
		return feesTrxnId;
	}

	public void setFeesTrxnId(Integer feesTrxnId) {
		this.feesTrxnId = feesTrxnId;
	}

	public void makePayment(
			AccountFeesActionDetailEntity accountFeesActionDetail) {
		accountFees = accountFeesActionDetail.getAccountFee();
		feeAmount = accountFeesActionDetail.getFeeAmount();
	}
	
	
	public void setFeeDetails(AccountFeesEntity accountFeesEntity, Money feeAmount){
		this.setAccountFees(accountFeesEntity);
		this.setFeeAmount(feeAmount);
	}

	public FeesTrxnDetailEntity generateReverseTrxn() {
		FeesTrxnDetailEntity reverseFeeTrxn = new FeesTrxnDetailEntity();
		reverseFeeTrxn.setAccountFees(getAccountFees());
		reverseFeeTrxn.setFeeAmount(getFeeAmount().negate());
		return reverseFeeTrxn;
	}
	
	public void adjustFeeTransaction(FeesTrxnDetailEntity feeTrxnDetail)
	{
		setAccountFees(feeTrxnDetail.getAccountFees());
		setFeeAmount(feeTrxnDetail.getFeeAmount());
	}

}
