/**
 
 * SavingsHelper.java    version: xxx
 
 
 
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
package org.mifos.application.accounts.savings.util.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.application.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;

public class SavingsHelper {
	private Calendar cal;

	// TODO: pick from configuration
	/** I assume the hardcoding of 1 Jan 2006 is trying to say 
	   that the default fiscal year is January 1 to December 31.
	   Do we use the year?
	   What does this control, versus 
	   {@link SavingsConstants#POSTING_DAY}? */
	public Date getFiscalStartDate() {
		try {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			return format.parse("01/01/2006");
		} catch (ParseException pe) {
			throw new RuntimeException(pe);
		}
	}

	private int getFiscalStartDayNumber() {
		Calendar cal = getCalendar();
		cal.setTime(getFiscalStartDate());
		return cal.get(Calendar.DAY_OF_MONTH);
	}

	public SavingsHelper() {
		cal = Calendar.getInstance();
		cal.setTime(getFiscalStartDate());
	}

	/*
	 * set the day number as 1 for interest calculation and 31 for interest posting date
	 * and get the meeting date according to the rules set for meeting
	 */
	public Date getNextScheduleDate(Date accountActivationDate,
			Date currentScheduleDate, MeetingBO meeting)
			throws MeetingException {
		Date oldMeetingStartDate = meeting.getStartDate();
		Short oldDayNumber = meeting.getMeetingDetails().getDayNumber();
		setDetailsForMeeting(meeting);
		Date scheduleDate = (currentScheduleDate == null) ? getFirstDate(meeting,
				accountActivationDate) : meeting
				.getNextScheduleDateAfterRecurrence(currentScheduleDate);
		meeting.setStartDate(oldMeetingStartDate);
		meeting.getMeetingDetails().getMeetingRecurrence().setDayNumber(oldDayNumber);
		return scheduleDate;
	}	
	
	public Date getPrevScheduleDate(Date accountActivationDate,
			Date currentScheduleDate, MeetingBO meeting)
			throws MeetingException {
		Date oldMeetingStartDate = meeting.getStartDate();
		Short oldDayNumber = meeting.getMeetingDetails().getDayNumber();
		setDetailsForMeeting(meeting);
		Date prevScheduleDate = meeting
				.getPrevScheduleDateAfterRecurrence(currentScheduleDate);
		meeting.setStartDate(oldMeetingStartDate);
		meeting.getMeetingDetails().getMeetingRecurrence().setDayNumber(oldDayNumber);
		return (prevScheduleDate != null && getDate(accountActivationDate)
				.compareTo(prevScheduleDate) > 0) ? null : prevScheduleDate;
	}

	private Date getDate(Date date) {
		return new Date(date.getTime());
	}

	/**
	 * start from getting a next schedule date after fiscal start date and loop till get a 
	 * meeting date after account activation date
	 */
	private Date getFirstDate(MeetingBO meeting,
			Date accountActivationDate) throws MeetingException {
		Date date = null;
		for (date = meeting
				.getNextScheduleDateAfterRecurrence(getFiscalStartDate()); date
				.compareTo(accountActivationDate) <= 0; date = meeting
				.getNextScheduleDateAfterRecurrence(date))
			;
		return date;
	}

	private void setDetailsForMeeting(MeetingBO meeting){
		meeting.setStartDate(getFiscalStartDate());
		if (meeting.isMonthly()) {
			if (meeting.getMeetingTypeEnum() == 
				MeetingType.SAVINGS_INTEREST_CALCULATION_TIME_PERIOD) {
				meeting.getMeetingDetails().getMeetingRecurrence()
					.setDayNumber((short) getFiscalStartDayNumber());
			}
			else if(meeting.getMeetingTypeEnum() == 
				MeetingType.SAVINGS_INTEREST_POSTING) {
//				meeting.setStartDate(getFiscalEndDate());
				meeting.setStartDate(DateUtils.addDays(getFiscalStartDate(), -1));
				meeting.getMeetingDetails().getMeetingRecurrence()
					.setDayNumber(SavingsConstants.POSTING_DAY);
			}
		}
	}
	
	public int calculateDays(Date fromDate, Date toDate) {
		long oneDay = 1000 * 60 * 60 * 24;
		long days = 
			(getMFITime(toDate) / oneDay) - (getMFITime(fromDate) / oneDay);
		return (int) days;
	}

	private long getMFITime(Date date) {
		Calendar cal1 = Calendar.getInstance(Configuration.getInstance()
				.getSystemConfig().getMifosTimeZone());
		cal1.setTime(date);
		return date.getTime() + cal1.get(Calendar.ZONE_OFFSET)
				+ cal1.get(Calendar.DST_OFFSET);
	}

	private Calendar getCalendar() {
		return Calendar.getInstance(Configuration.getInstance()
				.getSystemConfig().getMifosTimeZone());
	}

	public Date getCurrentDate() {
		cal = Calendar.getInstance();
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal
				.get(Calendar.DATE), 0, 0, 0);
		return cal.getTime();
	}

	public AccountActionDateEntity createActionDateObject(AccountBO account,
			CustomerBO customer, Short installmentId, Date date, Short userId,
			Money amount) {
		AccountActionDateEntity actionDate = new SavingsScheduleEntity(account,
				customer, installmentId, new java.sql.Date(date.getTime()),
				PaymentStatus.UNPAID, amount);
		actionDate.setCreatedBy(userId);
		actionDate.setCreatedDate(new Date());
		return actionDate;
	}

	public AccountTrxnEntity createAccountPaymentTrxn(
			AccountPaymentEntity payment, Money balance,
			AccountActionEntity accountAction, CustomerBO customer,
			PersonnelBO createdBy) {
		SavingsTrxnDetailEntity savingsTrxn = new SavingsTrxnDetailEntity(
				payment, customer, accountAction, payment.getAmount(), balance,
				createdBy, null, new SavingsHelper().getCurrentDate(), null, "");
		return savingsTrxn;
	}

	public AccountPaymentEntity createAccountPayment(AccountBO account,
			Money amount, PaymentTypeEntity paymentTypeEntity,
			PersonnelBO createdBy) {
		AccountPaymentEntity payment = new AccountPaymentEntity(account,
				amount, null, null, paymentTypeEntity, new Date(System.currentTimeMillis()));
		if (createdBy != null)
			payment.setCreatedBy(createdBy.getPersonnelId());
		payment.setCreatedDate(getCurrentDate());
		payment.setAmount(amount);
		return payment;
	}

	public Short getPaymentActionType(AccountPaymentEntity payment) {
		for (AccountTrxnEntity accntTrxn : payment.getAccountTrxns()) {
			if (!accntTrxn.getAccountActionEntity().getId().equals(
					AccountActionTypes.SAVINGS_ADJUSTMENT.getValue()))
				return accntTrxn.getAccountActionEntity().getId();
		}
		return null;
	}
}
