/**
 * RepaymentScheduleInputsIfc.java version:1.0
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

package org.mifos.framework.components.repaymentschedule;

import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.accounts.util.valueobjects.AccountFees;
import org.mifos.framework.util.helpers.Money;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Date;
/**
 *
 *  This interface takes in the relavant data required to perform various repayment schedule operations
 */
public interface RepaymentScheduleInputsIfc
{



	public void setMeeting(Meeting meeting);
	public void setRepaymentFrequency(Meeting repaymentFrequency);
	public void setGraceType(int graceType);
	public void setGracePeriod(int gracePeriodInInstallments);
	public void setIsInterestDedecutedAtDisburesement(boolean isInterestDeducted);
	public void setIsPrincipalInLastPayment(boolean isPrincipalInLastPayment);
	public void setPrincipal(Money principal);
	public void setInterestRate(double interestRate);
	public void setNoOfInstallments(int noOfInstallments);
	public void setInterestType(int interestType);
	public void setLoanInterest(Money loanInterest);
	public void setAccountFee(Set<AccountFees> fees);
	public void setInstallmentDate(List<InstallmentDate> installmentDate);
	public void setFeeStartDate(Date feeStartDate);
	public void setTotalFees(Money totalFees);
	public void setMeetingToConsider(int meetingToConsider);
	public void setDisbursementDate(Date disburesmentDate);
	public void setMiscFees(Money miscFee);
	public void setMiscPenlty(Money miscPenalty);

	public Date getDisbursementDate();
	public Meeting getMeeting();
	public Meeting getRepaymentFrequency();
	public int getGraceType();
	public int getGracePeriod();
	public boolean getIsInterestDedecutedAtDisburesement();
	public boolean getIsPrincipalInLastPayment();
	public Money getPrincipal();
	public double getInterestRate();
	public int getNoOfInstallments();
	public int getInterestType();
	public Money getLoanInterest();
	public Set<AccountFees> getAccountFee();
	public List<InstallmentDate> getInstallmentDate();
	public Date getFeeStartDate();
	public Money getTotalFees();
	public int getMeetingToConsider();
	public Money getMiscFees();
	public Money getMiscPenalty();
}
