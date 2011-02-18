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