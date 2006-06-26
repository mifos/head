/**

 * FeesTrxnDetail.java    version: xxx

 

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

import org.mifos.application.master.util.valueobjects.Currency;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.valueobjects.ValueObject;

public class FeesTrxnDetail extends ValueObject {
	
	public FeesTrxnDetail(){
		super();
	}
	private Integer feesTrxnId;
	private AccountTrxn accountTrxn;	
	private AccountFees accountFees;
	private Money feeAmount;
	
	
	
	public AccountTrxn getAccountTrxn() {
		return accountTrxn;
	}
	public void setAccountTrxn(AccountTrxn accountTrxn) {
		this.accountTrxn = accountTrxn;
	}
	public Integer getFeesTrxnId() {
		return feesTrxnId;
	}
	public void setFeesTrxnId(Integer feesTrxnId) {
		this.feesTrxnId = feesTrxnId;
	}
	public AccountFees getAccountFees() {
		return accountFees;
	}
	public void setAccountFees(AccountFees accountFees) {
		this.accountFees = accountFees;
	}

	public Money getFeeAmount() {
		return feeAmount;
	}
	public void setFeeAmount(Money feeAmount) {
		this.feeAmount = feeAmount;
	}
	
	


}
