package org.mifos.clientportfolio.newloan.domain;

import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.util.helpers.InstallmentDate;
import org.mifos.schedule.ScheduledEvent;

public class AnyScheduledEventLoanInstallmentGenerator implements LoanInstallmentGenerator {

	private final ScheduledEvent scheduledEvent;

	public AnyScheduledEventLoanInstallmentGenerator(ScheduledEvent scheduledEvent) {
		this.scheduledEvent = scheduledEvent;
	}

	@Override
	public List<InstallmentDate> generate(LocalDate actualDisbursementDate, int numberOfInstallments, GraceType graceType, int gracePeriodDuration) {
		// TODO Auto-generated method stub
		return null;
	}

}
