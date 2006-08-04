/**
 * FeeInputs.java version:1.0
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
package org.mifos.framework.components.repaymentschedule;

import java.util.Set;
import java.util.List;

import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.util.valueobjects.AccountFees;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.framework.util.helpers.Money;

import java.util.Date;

/**
 *
 *  This class holds the fee inputs
 */

public class FeeInputs
{

	private Set<AccountFees> accountFees = null;
	private List<InstallmentDate> installmentDates = null;
	private Meeting repaymentFrequency = null;
	private Money loanAmount = new Money();
	private Money loanInterest = new Money();
	private Date feeStartDate = null;
	private Money feeAmount = new Money();
	private int meetingToConsider;
	private Set<AccountFeesEntity> accountFeesEntity = null;
	
	public int getMeetingToConsider() {
		return meetingToConsider;
	}

	public void setMeetingToConsider(int meetingToConsider) {
		this.meetingToConsider = meetingToConsider;
	}

	public void setFeeAmount(Money feeAmount)
	{
		this.feeAmount = feeAmount;
	}

	public Money getFeeAmount()
	{
		return feeAmount;
	}
	public void setAccountFees(Set<AccountFees> accountFees)
	{
		this.accountFees = accountFees;
	}

	public void setInstallmentDate(List<InstallmentDate> installmentDates)
	{
		this.installmentDates = installmentDates;
	}

	public Set<AccountFees> getAccountFees()
	{
		return accountFees;
	}

	public List<InstallmentDate> getInstallmentDate()
	{
		return installmentDates;
	}

	public void setRepaymentFrequency(Meeting repaymentFrequency)
	{
		this.repaymentFrequency =repaymentFrequency;
	}

	public Meeting getRepaymentFrequency()
	{
		return repaymentFrequency;
	}
	public void setLoanAmount(Money loanAmount)
	{
		this.loanAmount = loanAmount;
	}

	public void setLoanInterest(Money loanInterest)
	{
		this.loanInterest = loanInterest;
	}

	public Money getLoanAmount()
	{
		return loanAmount;
	}

	public Money getLoanInterest()
	{
		return loanInterest;
	}

	public void setFeeStartDate(Date feeStartDate)
	{
		this.feeStartDate = feeStartDate;
	}

	public Date getFeeStartDate()
	{
		return feeStartDate;
	}

	public Set<AccountFeesEntity> getAccountFeesEntity() {
		return accountFeesEntity;
	}

	public void setAccountFeesEntity(Set<AccountFeesEntity> accountFeesEntity) {
		this.accountFeesEntity = accountFeesEntity;
	}

}
