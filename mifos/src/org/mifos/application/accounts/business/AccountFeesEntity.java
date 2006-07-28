/**
 
 * AccountFeesEntity.java    version: xxx
 
 
 
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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.business.PersistentObject;
import org.mifos.framework.components.repaymentschedule.MeetingScheduleHelper;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleException;
import org.mifos.framework.components.scheduler.SchedulerException;
import org.mifos.framework.components.scheduler.SchedulerIntf;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;

/**
 * @author ashishsm
 * 
 */
public class AccountFeesEntity extends PersistentObject {

	public AccountFeesEntity() {
		super();

	}

	private Integer accountFeeId;

	private AccountBO account;

	private FeeBO fees;

	private Money accountFeeAmount;

	private Money feeAmount;

	private Short feeStatus;

	private Date statusChangeDate;

	private Date lastAppliedDate;

	public AccountBO getAccount() {
		return account;
	}

	public void setAccount(AccountBO account) {
		this.account = account;
	}

	public Integer getAccountFeeId() {
		return accountFeeId;
	}

	public void setAccountFeeId(Integer accountFeeId) {
		this.accountFeeId = accountFeeId;
	}

	/**
	 * @return Returns the accountFeeAmount.
	 */
	public Money getAccountFeeAmount() {
		return accountFeeAmount;
	}

	/**
	 * @param accountFeeAmount
	 *            The accountFeeAmount to set.
	 */
	public void setAccountFeeAmount(Money accountFeeAmount) {
		this.accountFeeAmount = accountFeeAmount;
	}

	/**
	 * @return Returns the feeAmount.
	 */
	public Money getFeeAmount() {
		return feeAmount;
	}

	/**
	 * @param feeAmount
	 *            The feeAmount to set.
	 */
	public void setFeeAmount(Money feeAmount) {
		this.feeAmount = feeAmount;
	}

	public FeeBO getFees() {
		return fees;
	}

	public void setFees(FeeBO fees) {
		this.fees = fees;
	}

	/**
	 * @return Returns the feeStatus.
	 */
	public Short getFeeStatus() {
		return feeStatus;
	}

	/**
	 * @param feeStatus
	 *            The feeStatus to set.
	 */
	public void setFeeStatus(Short feeStatus) {
		this.feeStatus = feeStatus;
	}

	/**
	 * @return Returns the statusChangeDate.
	 */
	public Date getStatusChangeDate() {
		return statusChangeDate;
	}

	/**
	 * @param statusChangeDate
	 *            The statusChangeDate to set.
	 */
	public void setStatusChangeDate(Date statusChangeDate) {
		this.statusChangeDate = statusChangeDate;
	}

	public Date getLastAppliedDate() {
		return lastAppliedDate;
	}

	public void setLastAppliedDate(Date lastAppliedDate) {
		this.lastAppliedDate = lastAppliedDate;
	}

	public void changeFeesStatus(Short status, Date changeDate) {
		this.setFeeStatus(status);
		this.setStatusChangeDate(changeDate);
	}

	public boolean isTimeOfDisbursement() {
		return getFees().isTimeOfDisbursement();
	}

	public boolean isApplicable(Date date) throws RepaymentScheduleException,
			SchedulerException {
		boolean isApplicable = false;
		SchedulerIntf schedulerIntf;
		if (getLastAppliedDate() != null) {
			MeetingBO meetingBO = getAccount().getCustomer()
					.getCustomerMeeting().getMeeting();
			Calendar meetingStartDate = new GregorianCalendar();
			meetingStartDate.setTime(getLastAppliedDate());
			Calendar customerMeetingStartDate = meetingBO.getMeetingStartDate();
			Short recurAfter = meetingBO.getMeetingDetails().getRecurAfter();
			meetingBO.setMeetingStartDate(meetingStartDate);
			meetingBO.getMeetingDetails().setRecurAfter(
					getFees().getFeeFrequency().getFeeMeetingFrequency()
							.getMeetingDetails().getRecurAfter());
			schedulerIntf = MeetingScheduleHelper.getSchedulerObject(meetingBO);
			List<Date> applDates = schedulerIntf.getAllDates(date);
			if (applDates != null && !applDates.isEmpty()) {
				Iterator<Date> itr = applDates.iterator();
				while (itr.hasNext()) {
					Date appliedDate = itr.next();
					if (DateUtils
							.getDateWithoutTimeStamp(appliedDate.getTime())
							.compareTo(
									DateUtils
											.getDateWithoutTimeStamp(getLastAppliedDate()
													.getTime())) == 0) {
						itr.remove();
					}
				}
				isApplicable = applDates.size() > 0 ? true : false;
			}
			meetingBO.setMeetingStartDate(customerMeetingStartDate);
			meetingBO.getMeetingDetails().setRecurAfter(recurAfter);
		}
		return isApplicable;
	}

}
