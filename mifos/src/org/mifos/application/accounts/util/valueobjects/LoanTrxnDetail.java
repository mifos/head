/**

 * LoanTrxnDetail.java    version: xxx

 

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
package org.mifos.application.accounts.util.valueobjects;

import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.valueobjects.ValueObject;

public class LoanTrxnDetail extends ValueObject{
	
	public LoanTrxnDetail(){
	}
	private Integer accountTrxnId;
	private AccountTrxn accountTrxn;
	private Short installmentId;
	private Money principalAmount;
	private Money interestAmount;
	private Money penaltyAmount;
	private Integer relatedTrxnId;
	
	
	public AccountTrxn getAccountTrxn() {
		return accountTrxn;
	}
	public void setAccountTrxn(AccountTrxn accountTrxn) {
		this.accountTrxn = accountTrxn;
	}
	public Money getInterestAmount() {
		return interestAmount;
	}
	public void setInterestAmount(Money interestAmount) {
		this.interestAmount = interestAmount;
	}	
	public Money getPenaltyAmount() {
		return penaltyAmount;
	}
	public void setPenaltyAmount(Money penaltyAmount) {
		this.penaltyAmount = penaltyAmount;
	}
	public Money getPrincipalAmount() {
		return principalAmount;
	}
	public void setPrincipalAmount(Money principalAmount) {
		this.principalAmount = principalAmount;
	}
	public Integer getAccountTrxnId() {
		return accountTrxnId;
	}
	public void setAccountTrxnId(Integer accountTrxnId) {
		this.accountTrxnId = accountTrxnId;
	}
	public Short getInstallmentId() {
		return installmentId;
	}
	public void setInstallmentId(Short installmentId) {
		this.installmentId = installmentId;
	}
	public Integer getRelatedTrxnId() {
		return relatedTrxnId;
	}

	
	public void setRelatedTrxnId(Integer relatedTrxnId) {
		this.relatedTrxnId = relatedTrxnId;
	}
	

}
