package org.mifos.clientportfolio.newloan.domain;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.schedule.ScheduledEvent;

public class LoanInstallmentFactoryImpl implements LoanInstallmentFactory {

	private final RecurringScheduledEventFactory scheduledEventFactory;

	public LoanInstallmentFactoryImpl(RecurringScheduledEventFactory scheduledEventFactory) {
		this.scheduledEventFactory = scheduledEventFactory;
	}

	@Override
	public LoanInstallmentGenerator create(MeetingBO meeting, boolean repaymentsShouldMatchCustomerMeetingSchedule) {
		
		if (repaymentsShouldMatchCustomerMeetingSchedule) {
			return new CustomerMeetingScheduleLoanInstallmentGenerator(meeting);
		} 
		
		ScheduledEvent scheduledEvent = scheduledEventFactory.createScheduledEventFrom(meeting);
		return new AnyScheduledEventLoanInstallmentGenerator(scheduledEvent);
	}
}