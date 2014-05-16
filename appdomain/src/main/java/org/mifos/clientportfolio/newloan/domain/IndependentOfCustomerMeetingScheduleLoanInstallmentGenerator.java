package org.mifos.clientportfolio.newloan.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.util.helpers.InstallmentDate;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.schedule.ScheduledDateGeneration;
import org.mifos.schedule.ScheduledEvent;
import org.mifos.schedule.internal.HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration;

public class IndependentOfCustomerMeetingScheduleLoanInstallmentGenerator implements LoanInstallmentGenerator {

	private final ScheduledEvent scheduledEvent;
	private final HolidayDao holidayDao;
	private final LocalDate meetingStartDate;

	public IndependentOfCustomerMeetingScheduleLoanInstallmentGenerator(ScheduledEvent scheduledEvent, HolidayDao holidayDao, LocalDate meetingStartDate) {
		this.scheduledEvent = scheduledEvent;
		this.holidayDao = holidayDao;
		this.meetingStartDate = meetingStartDate;
	}

	@Override
	public List<InstallmentDate> generate(LocalDate actualDisbursementDate, int numberOfInstallments, GraceType graceType, int graceDuration, Short officeId) {
		
		List<InstallmentDate> installmentDates = new ArrayList<InstallmentDate>();
		
		int installmentsToSkip = getInstallmentSkipToStartRepayment(graceType, graceDuration);
        installmentDates = getInstallmentDates(this.scheduledEvent, actualDisbursementDate, numberOfInstallments, installmentsToSkip, officeId);
		
		return installmentDates;
	}
	
    private int getInstallmentSkipToStartRepayment(GraceType graceType, int graceDuration) {

        // if LoanScheduleIndependentofMeeting is on, then repayments start on
        // the first meeting in the schedule (#0)
    	int firstRepaymentInstallment = 0;

        if (graceType == GraceType.PRINCIPALONLYGRACE || graceType == GraceType.NONE) {
            return firstRepaymentInstallment;
        }
        return graceDuration + firstRepaymentInstallment;
    }

    private final List<InstallmentDate> getInstallmentDates(final ScheduledEvent scheduledEvent, LocalDate meetingStartDate, final int noOfInstallments, final int installmentToSkip, Short officeId) {

        List<InstallmentDate> dueInstallmentDates = new ArrayList<InstallmentDate>();
        if (noOfInstallments > 0) {
            List<Days> workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();
            List<Holiday> holidays = new ArrayList<Holiday>();

            DateTime startFromMeetingDate = meetingStartDate.toDateMidnight().toDateTime();
            //holidays = holidayDao.findAllHolidaysFromDateAndNext(officeId, startFromMeetingDate.toLocalDate().toString());

            final int occurrences = noOfInstallments + installmentToSkip;
            ScheduledDateGeneration dateGeneration = new HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration(workingDays, holidays);

            List<Date> dueDates = new ArrayList<Date>();
            DateTime startFromDayAfterAssignedMeetingDateRatherThanSkippingInstallments = startFromMeetingDate;
			
            // ensure loans that are created or disbursed on a meeting date start on next valid meeting date and not todays meeting
			// ensure loans that are created or disbursed before a meeting date start on next valid meeting date
			startFromDayAfterAssignedMeetingDateRatherThanSkippingInstallments = startFromMeetingDate.plusDays(1);
			
            List<DateTime> installmentDates = dateGeneration.generateScheduledDates(occurrences, startFromDayAfterAssignedMeetingDateRatherThanSkippingInstallments, scheduledEvent, false);
            for (DateTime installmentDate : installmentDates) {
                dueDates.add(installmentDate.toDate());
            }

            dueInstallmentDates = createInstallmentDates(installmentToSkip, dueDates);
        }
        return dueInstallmentDates;
    }
    
    private List<InstallmentDate> createInstallmentDates(final int installmentToSkip, final List<Date> dueDates) {
        List<InstallmentDate> installmentDates = new ArrayList<InstallmentDate>();
        int installmentId = 1;
        for (Date date : dueDates) {
            installmentDates.add(new InstallmentDate((short) installmentId++, date));
        }
        removeInstallmentsNeedNotPay(installmentToSkip, installmentDates);
        return installmentDates;
    }

    private void removeInstallmentsNeedNotPay(final int installmentSkipToStartRepayment, final List<InstallmentDate> installmentDates) {
        int removeCounter = 0;
        for (int i = 0; i < installmentSkipToStartRepayment; i++) {
            installmentDates.remove(removeCounter);
        }
        // re-adjust the installment ids
        if (installmentSkipToStartRepayment > 0) {
            int count = installmentDates.size();
            for (int i = 0; i < count; i++) {
                InstallmentDate instDate = installmentDates.get(i);
                instDate.setInstallmentId(new Short(Integer.toString(i + 1)));
            }
        }
    }
}