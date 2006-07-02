/**

 * AccountFeesActionDetail.java    version: 1.0



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

package org.mifos.application.accounts.util.valueobjects;

import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.valueobjects.ValueObject;

public class AccountFeesActionDetail  extends ValueObject implements java.io.Serializable
{
	private Short installmentId;
	private Integer accountFeesActionDetailId;
	private Short  feeId;
	private Money feeAmount;
	private Money amountPaid;
	private AccountActionDate accountActionDate;
	private Integer versionNo;
	private AccountFees accountFee;


	public void setAccountFeesActionDetailId(Integer accountFeesActionDetailId)
	{
		this.accountFeesActionDetailId=accountFeesActionDetailId;
	}

	public Integer getAccountFeesActionDetailId()
	{
		return accountFeesActionDetailId;
	}
	public void setInstallmentId(Short installmentId)
	{
		this.installmentId=installmentId;
	}

	public void setAccountFee(AccountFees accountFee)
	{
		this.accountFee = accountFee;
	}

	public void setAccountFeeDetailId(AccountActionDate accountActionDate)
	{
		this.accountActionDate=accountActionDate;
	}

	public void setFeeId(Short feeId)
	{
		this.feeId=feeId;
	}

	public void setFeeAmount(Money feeAmount)
	{
		this.feeAmount=feeAmount;
	}

	public void setAmountPaid(Money amountPaid)
	{
		this.amountPaid=amountPaid;
	}

	public Short getInstallmentId()
	{
		return installmentId;
	}

	public AccountFees getAccountFee()
	{
		return accountFee;
	}

	public AccountActionDate getAccountFeeDetailId()
	{
		return accountActionDate;
	}

	public Short getFeeId()
	{
		return feeId;
	}

	public Money getFeeAmount()
	{
		return feeAmount;
	}

	public Money getAmountPaid()
	{
		return amountPaid;
	}
	public Integer getVersionNo() {
		return versionNo;
	}

	/**
	 * @param versionNo The versionNo to set.
	 */
	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}

	 public Money getFeeDue()
	 {	if(getFeeAmount()==null)
		 	setFeeAmount(new Money());
		 return getFeeAmount().subtract(getAmountPaid());
	 }

}
