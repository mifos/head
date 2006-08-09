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

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.util.valueobjects.AccountFees;
import org.mifos.application.fees.business.AmountFeeBO;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.business.RateFeeBO;
import org.mifos.application.fees.util.helpers.RateAmountFlag;
import org.mifos.application.fees.util.valueobjects.FeeFrequency;
import org.mifos.application.fees.util.valueobjects.Fees;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.meeting.util.valueobjects.MeetingDetails;
import org.mifos.application.meeting.util.valueobjects.MeetingRecurrence;
import org.mifos.application.meeting.util.valueobjects.MeetingType;
import org.mifos.application.meeting.util.valueobjects.RecurrenceType;
import org.mifos.framework.components.interestcalculator.InterestCalculatorConstansts;
import org.mifos.framework.util.helpers.Money;

/**
 * 
 * This class takes in the relavant data required to perform various repayment
 * schedule operations
 */
public class RepaymentScheduleInputs implements RepaymentScheduleInputsIfc {

	private Meeting meeting = null;

	private Meeting repaymentFrequency = null;

	private int graceType = RepaymentScheduleConstansts.GRACE_NONE; // defaults
																	// to all
																	// repayments

	private int gracePeriodInInstallments = 0;

	private boolean isInterestDeducted = false; // defaults to no interest
												// dedecution at disburesement

	private boolean isPrincipalInLastPayment = false; // defaults to principal
														// paid from begginig

	private Money principal = new Money();

	private double interestRate = 0;

	private int noOfInstallments = 0;

	private int interestType = InterestCalculatorConstansts.FLAT_INTEREST; // defaults
																			// to
																			// flat
																			// interest
																			// rate

	private Money loanInterest = new Money();

	private Set<AccountFees> feeList = new HashSet();

	private List<InstallmentDate> installmentDate;

	private Date feeStartDate = null;

	private Money totalFees = new Money();

	private int meetingToConsider = RepaymentScheduleConstansts.MEETING_LOAN;

	private Date disburesmentDate;

	private Money miscFee = null;

	private Money miscPenalty = null;
	
	private Set<AccountFeesEntity> accountFeesEntity;

	public void setDisbursementDate(Date disburesmentDate) {
		this.disburesmentDate = disburesmentDate;
	}

	public Date getDisbursementDate() {
		return disburesmentDate;
	}

	public void setMeeting(Meeting meeting) {
		this.meeting = meeting;
	}

	public void setMeeting(MeetingBO meeting) {
		this.meeting = convertMeeting(meeting);
	}

	public void setRepaymentFrequency(Meeting repaymentFrequency) {
		this.repaymentFrequency = repaymentFrequency;
	}

	public void setRepaymentFrequency(MeetingBO repaymentFrequency) {
		this.repaymentFrequency = convertMeeting(repaymentFrequency);
	}

	public void setGraceType(int graceType) {
		this.graceType = graceType;
	}

	public void setGracePeriod(int gracePeriodInInstallments) {
		this.gracePeriodInInstallments = gracePeriodInInstallments;
	}

	public void setIsInterestDedecutedAtDisburesement(boolean isInterestDeducted) {
		this.isInterestDeducted = isInterestDeducted;
	}

	public void setIsPrincipalInLastPayment(boolean isPrincipalInLastPayment) {
		this.isPrincipalInLastPayment = isPrincipalInLastPayment;
	}

	public void setPrincipal(Money principal) {
		this.principal = principal;
	}

	public void setInterestRate(double interestRate) {
		this.interestRate = interestRate;
	}

	public void setNoOfInstallments(int noOfInstallments) {
		this.noOfInstallments = noOfInstallments;
	}

	public void setInterestType(int interestType) {
		this.interestType = interestType;
	}

	public void setLoanInterest(Money loanInterest) {
		this.loanInterest = loanInterest;
	}

	public void setAccountFee(Set<AccountFees> fees) {
		this.feeList = fees;
	}

	public void setAccountFeeEntity(Set<AccountFeesEntity> fees){
		accountFeesEntity = fees;
		this.feeList = convertToAccountFees(fees);
	}
	
	public void setInstallmentDate(List<InstallmentDate> installmentDate) {
		this.installmentDate = installmentDate;
	}

	public Set<AccountFees> getAccountFee() {
		return feeList;
	}

	public void setFeeStartDate(Date feeStartDate) {
		this.feeStartDate = feeStartDate;
	}

	public void setTotalFees(Money totalFees) {
		this.totalFees = totalFees;
	}

	public void setMeetingToConsider(int meetingToConsider) {
		this.meetingToConsider = meetingToConsider;
	}

	public int getMeetingToConsider() {
		return meetingToConsider;
	}

	public Money getTotalFees() {
		return totalFees;
	}

	public Date getFeeStartDate() {
		return feeStartDate;
	}

	public Money getLoanInterest() {
		return loanInterest;
	}

	public Meeting getMeeting() {
		return meeting;
	}

	public Meeting getRepaymentFrequency() {
		return repaymentFrequency;
	}

	public int getGraceType() {
		return graceType;
	}

	public int getGracePeriod() {
		return gracePeriodInInstallments;
	}

	public boolean getIsInterestDedecutedAtDisburesement() {
		return isInterestDeducted;
	}

	public boolean getIsPrincipalInLastPayment() {
		return isPrincipalInLastPayment;
	}

	public Money getPrincipal() {
		return principal;
	}

	public double getInterestRate() {
		return interestRate;
	}

	public int getNoOfInstallments() {
		return noOfInstallments;
	}

	public int getInterestType() {
		return interestType;
	}

	public List<InstallmentDate> getInstallmentDate() {
		return installmentDate;
	}

	public void setMiscFees(Money miscFee) {
		this.miscFee = miscFee;

	}

	public void setMiscPenlty(Money miscPenalty) {
		this.miscPenalty = miscPenalty;
	}

	public Money getMiscFees() {
		return miscFee;
	}

	public Money getMiscPenalty() {
		return miscPenalty;
	}

	private Meeting convertMeeting(MeetingBO M2meeting) {
		Meeting meetingToReturn = new Meeting();
		meetingToReturn.setMeetingStartDate(M2meeting.getMeetingStartDate());
		meetingToReturn.setMeetingPlace("");
		MeetingType meetingType = new MeetingType();
		meetingType.setMeetingTypeId(M2meeting.getMeetingType()
				.getMeetingTypeId());
		meetingToReturn.setMeetingType(meetingType);

		MeetingRecurrence meetingRecToReturn = new MeetingRecurrence();
		meetingRecToReturn.setDayNumber(M2meeting.getMeetingDetails()
				.getMeetingRecurrence().getDayNumber());
		if(M2meeting.getMeetingDetails()
				.getMeetingRecurrence().getRankOfDays()!=null){
			meetingRecToReturn.setRankOfDays(M2meeting.getMeetingDetails()
					.getMeetingRecurrence().getRankOfDays().getRankOfDayId());
		}
		if(M2meeting.getMeetingDetails()
				.getMeetingRecurrence().getWeekDay()!=null){
			meetingRecToReturn.setWeekDay(M2meeting.getMeetingDetails()
					.getMeetingRecurrence().getWeekDay().getWeekDayId());
		}

		MeetingDetails meetingDetailsToReturn = new MeetingDetails();
		meetingDetailsToReturn.setMeetingRecurrence(meetingRecToReturn);
		meetingDetailsToReturn.setRecurAfter(M2meeting.getMeetingDetails()
				.getRecurAfter());

		RecurrenceType recurrenceType = new RecurrenceType();
		recurrenceType.setRecurrenceId(M2meeting.getMeetingDetails()
				.getRecurrenceType().getRecurrenceId());

		meetingDetailsToReturn.setRecurrenceType(recurrenceType);

		meetingToReturn.setMeetingDetails(meetingDetailsToReturn);

		return meetingToReturn;

	}
	
	public Set<AccountFeesEntity> getAccountFeesEntity()
	{
		return accountFeesEntity;
	}
	

	private Set<AccountFees> convertToAccountFees(Set<AccountFeesEntity> accountFeesEntities){
		
		Set<AccountFees> accountFees = new HashSet<AccountFees>();
		for(AccountFeesEntity accountFeeEntity: accountFeesEntities){
			accountFees.add(getAccountFees(accountFeeEntity));
		}
		return accountFees;
	}
	
	private AccountFees getAccountFees(AccountFeesEntity afe){
		AccountFees af = new AccountFees();
		af.setAccountFeeAmount(afe.getAccountFeeAmount());
		af.setFeeAmount(afe.getFeeAmount().getAmountDoubleValue());
		af.setFees(getFees(afe.getFees()));
		return af;
	}
	
	private Fees getFees(FeeBO feebo){
		Fees fee = new Fees();
		fee.setFeeId(feebo.getFeeId());
		if(feebo.getFeeType().equals(RateAmountFlag.AMOUNT)) {
			fee.setFeeAmount(((AmountFeeBO)feebo).getFeeAmount());
			fee.setRateFlatFalg(RateAmountFlag.AMOUNT.getValue());
		}else {
			fee.setFeeAmount(new Money(((RateFeeBO)feebo).getRate().toString()));
			fee.setRateFlatFalg(RateAmountFlag.RATE.getValue());
			fee.setFormulaId(((RateFeeBO)feebo).getFeeFormula().getId());
		}
		FeeFrequency feeFrequency = new FeeFrequency();
		feeFrequency.setFeeFrequencyTypeId(feebo.getFeeFrequency().getFeeFrequencyType().getId());
		if(feebo.isPeriodic())
			feeFrequency.setFeeMeetingFrequency(convertMeeting(feebo.getFeeFrequency().getFeeMeetingFrequency()));
		fee.setFeeFrequency(feeFrequency);
		return fee;
	}

}
