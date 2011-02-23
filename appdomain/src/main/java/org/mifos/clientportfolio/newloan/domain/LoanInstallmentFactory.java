package org.mifos.clientportfolio.newloan.domain;

import org.mifos.application.meeting.business.MeetingBO;

public interface LoanInstallmentFactory {

	LoanInstallmentGenerator create(MeetingBO loanMeeting, boolean repaymentsShouldMatchCustomerMeetingSchedule);

}