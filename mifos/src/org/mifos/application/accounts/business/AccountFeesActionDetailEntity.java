/**

 * AccountFeesActionDetailEntity.java    version: 1.0

 

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

import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.util.helpers.Money;

public class AccountFeesActionDetailEntity extends PersistentObject {

	private final Integer accountFeesActionDetailId;

	private final AccountActionDateEntity accountActionDate;

	private final Short installmentId;

	private final FeeBO fee;

	private final AccountFeesEntity accountFee;

	private Money feeAmount;

	private Money feeAmountPaid;
	
	protected AccountFeesActionDetailEntity(
			AccountActionDateEntity accountActionDate,
			FeeBO fee, AccountFeesEntity accountFee, Money feeAmount) {
		this.accountFeesActionDetailId = null;
		this.accountActionDate = accountActionDate;
		if(accountActionDate!=null)
			this.installmentId = accountActionDate.getInstallmentId();
		else
			this.installmentId =null;
		this.fee = fee;
		this.accountFee = accountFee;
		this.feeAmount = feeAmount;
	}

	public AccountActionDateEntity getAccountActionDate() {
		return accountActionDate;
	}

	public AccountFeesEntity getAccountFee() {
		return accountFee;
	}

	public Integer getAccountFeesActionDetailId() {
		return accountFeesActionDetailId;
	}

	public FeeBO getFee() {
		return fee;
	}

	public Money getFeeAmount() {
		return feeAmount;
	}

	public void setFeeAmount(Money feeAmount) {
		this.feeAmount = feeAmount;
	}

	public Money getFeeAmountPaid() {
		return feeAmountPaid;
	}

	public void setFeeAmountPaid(Money feeAmountPaid) {
		this.feeAmountPaid = feeAmountPaid;
	}

	public Short getInstallmentId() {
		return installmentId;
	}

	public void makePayment(Money feePaid) {
		if(getFeeAmountPaid()==null)
			setFeeAmountPaid(new Money());
		this.feeAmountPaid = getFeeAmountPaid().add(feePaid);
	}

	public Money getFeeDue() {

		return getFeeAmount().subtract(getFeeAmountPaid());
	}

	public void makeRepaymentEnteries(String payFullOrPartial) {
		if (payFullOrPartial.equals(LoanConstants.PAY_FEES_PENALTY_INTEREST)) {
			setFeeAmountPaid(getFeeAmountPaid().add(getFeeDue()));
		} else {
			setFeeAmount(getFeeAmountPaid());
		}
	}

	public Money waiveCharges() {
		Money chargeWaived = new Money();
		chargeWaived = chargeWaived.add(getFeeDue());
		setFeeAmount(getFeeAmountPaid());
		return chargeWaived;
	}

}
