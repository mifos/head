package org.mifos.clientportfolio.newloan.domain;

import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.util.helpers.InstallmentDate;
import org.mifos.application.meeting.business.MeetingBO;

public class CustomerMeetingScheduleLoanInstallmentGenerator implements LoanInstallmentGenerator {

	private final MeetingBO customerMeeting;

	public CustomerMeetingScheduleLoanInstallmentGenerator(MeetingBO customerMeeting) {
		this.customerMeeting = customerMeeting;
	}

	@Override
	public List<InstallmentDate> generate(LocalDate actualDisbursementDate, int numberOfInstallments, GraceType graceType, int graceDuration) {
		return null;
	}

}
