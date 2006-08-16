/**
 * EMIInputs.java version:1.0
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
import org.mifos.framework.components.interestcalculator.InterestCalculatorConstants;
import org.mifos.framework.util.helpers.Money;

/**
 *
 *  This class holds the emi inputs
 */

public class EMIInputs
{

	private int graceType = RepaymentScheduleConstansts.GRACE_ALLREPAYMENTS; //defaults to all repayments
	private int gracePeriodInInstallments = 0;
	private boolean isInterestDeducted = false; // defaults to no interest dedecution at disburesement
	private boolean isPrincipalInLastPayment = false; // defaults to principal paid from begginig
	private Money principal = new Money();
	private int noOfInstallments = 0;
	private Money loanInterest = new Money();


	// need to set for fee objects

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
	public void setNoOfInstallments(int noOfInstallments)
	{
		this.noOfInstallments = noOfInstallments;
	}

	public void setLoanInterest(Money loanInterest)
	{
		this.loanInterest = loanInterest;
	}

	public Money getLoanInterest()
	{
		return loanInterest;
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
	public int getNoOfInstallments()
	{
		return noOfInstallments;
	}






}
