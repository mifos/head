/**

 * FeesTrxnDetailEntity.java    version: 1.0

 

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

package org.mifos.application.accounts.business;

import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.helpers.Money;

public class FeesTrxnDetailEntity extends PersistentObject {

	private final Integer feesTrxnId;

	private final AccountTrxnEntity accountTrxn;

	private final AccountFeesEntity accountFees;

	private final Money feeAmount;

	protected FeesTrxnDetailEntity() {
		feesTrxnId = null;
		accountTrxn = null;
		accountFees = null;
		feeAmount = null;
	}

	public FeesTrxnDetailEntity(AccountTrxnEntity accountTrxnEntity,
			AccountFeesEntity accountFeesEntity, Money amount) {
		feesTrxnId = null;
		accountTrxn = accountTrxnEntity;
		accountFees = accountFeesEntity;
		feeAmount = amount;
	}

	public AccountFeesEntity getAccountFees() {
		return accountFees;
	}

	public AccountTrxnEntity getAccountTrxn() {
		return accountTrxn;
	}

	public Money getFeeAmount() {
		return feeAmount;
	}

	public Integer getFeesTrxnId() {
		return feesTrxnId;
	}

	public FeesTrxnDetailEntity generateReverseTrxn(
			AccountTrxnEntity accountTrxn) {
		return new FeesTrxnDetailEntity(accountTrxn, getAccountFees(),
				getFeeAmount().negate());
	}

}
