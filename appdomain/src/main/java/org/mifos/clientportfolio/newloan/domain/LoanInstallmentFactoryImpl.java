/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.clientportfolio.newloan.domain;

import org.joda.time.LocalDate;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.schedule.ScheduledEvent;

public class LoanInstallmentFactoryImpl implements LoanInstallmentFactory {

	private final RecurringScheduledEventFactory scheduledEventFactory;

	public LoanInstallmentFactoryImpl(RecurringScheduledEventFactory scheduledEventFactory) {
		this.scheduledEventFactory = scheduledEventFactory;
	}

	@Override
	public LoanInstallmentGenerator create(MeetingBO meeting, boolean repaymentsShouldNotHaveToMatchCustomerMeetingSchedule) {
		
		HolidayDao holidayDao = ApplicationContextProvider.getBean(HolidayDao.class);
		ScheduledEvent scheduledEvent = scheduledEventFactory.createScheduledEventFrom(meeting);
		
		if (repaymentsShouldNotHaveToMatchCustomerMeetingSchedule) {
			LocalDate meetingStartDate = new LocalDate(meeting.getMeetingStartDate());
			return new IndependentOfCustomerMeetingScheduleLoanInstallmentGenerator(scheduledEvent, holidayDao, meetingStartDate);
		} 
		
		return new AnyScheduledEventLoanInstallmentGenerator(scheduledEvent, holidayDao);
	}
}