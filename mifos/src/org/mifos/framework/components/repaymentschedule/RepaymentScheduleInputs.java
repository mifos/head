/**
 * RepaymentScheduleInputs.java version:1.0
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

import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.framework.components.interestcalculator.InterestCalculatorConstansts;
import org.mifos.framework.util.helpers.Money;
import org.mifos.application.accounts.util.valueobjects.AccountFees;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Date;
/**
 *
 *  This class takes in the relavant data required to perform various repayment schedule operations
 */
public class RepaymentScheduleInputs implements RepaymentScheduleInputsIfc
{

	private Meeting meeting = null;
	private Meeting repaymentFrequency = null;
	private int graceType = RepaymentScheduleConstansts.GRACE_NONE; //defaults to all repayments
	private int gracePeriodInInstallments = 0;
	private boolean isInterestDeducted = false; // defaults to no interest dedecution at disburesement
	private boolean isPrincipalInLastPayment = false; // defaults to principal paid from begginig
	private Money principal = new Money();
	private double interestRate = 0;
	private int noOfInstallments = 0;
	private int interestType = InterestCalculatorConstansts.FLAT_INTEREST; // defaults to flat interest rate
	private Money loanInterest = new Money();
	private Set<AccountFees> feeList = new HashSet();
	private List<InstallmentDate> installmentDate;
	private Date feeStartDate = null;
	private Money totalFees = new Money();
	private int meetingToConsider = RepaymentScheduleConstansts.MEETING_LOAN;
	private Date disburesmentDate;
	private Money miscFee=null;
	private Money miscPenalty=null;


	public void setDisbursementDate(Date disburesmentDate)
	{
		this.disburesmentDate = disburesmentDate;
	}

	public Date getDisbursementDate()
	{
		return disburesmentDate;
	}

	public void setMeeting(Meeting meeting)
	{
		this.meeting = meeting;
	}
	public void setRepaymentFrequency(Meeting repaymentFrequency)
	{
		this.repaymentFrequency = repaymentFrequency;
	}
	public void setGraceType(int graceType)
	{
		this.graceType = graceType;
	}
	public void setGracePeriod(int gracePeriodInInstallments)
	{
		this.gracePeriodInInstallments= gracePeriodInInstallments;
	}
	public void setIsInterestDedecutedAtDisburesement(boolean isInterestDeducted)
	{
		this.isInterestDeducted = isInterestDeducted;
	}
	public void setIsPrincipalInLastPayment(boolean isPrincipalInLastPayment)
	{
		this.isPrincipalInLastPayment = isPrincipalInLastPayment;
	}
	public void setPrincipal(Money principal)
	{
		this.principal= principal;
	}
	public void setInterestRate(double interestRate)
	{
		this.interestRate = interestRate;
	}
	public void setNoOfInstallments(int noOfInstallments)
	{
		this.noOfInstallments = noOfInstallments;
	}
	public void setInterestType(int interestType)
	{
		this.interestType = interestType;
	}

	public void setLoanInterest(Money loanInterest)
	{
		this.loanInterest = loanInterest;
	}

	public void setAccountFee(Set<AccountFees> fees)
	{
		this.feeList = fees;
	}

	public void setInstallmentDate(List<InstallmentDate> installmentDate)
	{
		this.installmentDate =installmentDate;
	}

	public Set<AccountFees> getAccountFee()
	{
		return feeList;
	}

	public void setFeeStartDate(Date feeStartDate)
	{
		this.feeStartDate = feeStartDate;
	}


	public void setTotalFees(Money totalFees)
	{
		this.totalFees = totalFees;
	}

	public void setMeetingToConsider(int meetingToConsider)
	{
		this.meetingToConsider = meetingToConsider;
	}

	public int getMeetingToConsider()
	{
		return meetingToConsider;
	}

	public Money getTotalFees()
	{
		return totalFees;
	}

	public Date getFeeStartDate()
	{
		return feeStartDate;
	}

	public Money getLoanInterest()
	{
		return loanInterest;
	}

	public Meeting getMeeting()
	{
		return meeting;
	}
	public Meeting getRepaymentFrequency()
	{
		return repaymentFrequency;
	}
	public int getGraceType()
	{
		return graceType;
	}
	public int getGracePeriod()
	{
		return gracePeriodInInstallments;
	}
	public boolean getIsInterestDedecutedAtDisburesement()
	{
		return isInterestDeducted;
	}
	public boolean getIsPrincipalInLastPayment()
	{
		return isPrincipalInLastPayment;
	}
	public Money getPrincipal()
	{
		return principal;
	}
	public double getInterestRate()
	{
		return interestRate;
	}
	public int getNoOfInstallments()
	{
		return noOfInstallments;
	}
	public int getInterestType()
	{
		return interestType;
	}

	public List<InstallmentDate> getInstallmentDate()
	{
		return installmentDate;
	}

	public void setMiscFees(Money miscFee) {
		this.miscFee=miscFee;
		
	}

	public void setMiscPenlty(Money miscPenalty) {
		this.miscPenalty=miscPenalty;
	}

	public Money getMiscFees() {
		return miscFee;
	}

	public Money getMiscPenalty() {
		return miscPenalty;
	}


}
