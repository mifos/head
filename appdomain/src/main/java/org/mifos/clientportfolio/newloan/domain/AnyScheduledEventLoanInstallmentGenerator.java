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
import org.mifos.schedule.internal.DailyScheduledEvent;
import org.mifos.schedule.internal.HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration;

public class AnyScheduledEventLoanInstallmentGenerator implements LoanInstallmentGenerator {

	private final ScheduledEvent scheduledEvent;
	private final HolidayDao holidayDao;

	public AnyScheduledEventLoanInstallmentGenerator(ScheduledEvent scheduledEvent, HolidayDao holidayDao) {
		this.scheduledEvent = scheduledEvent;
		this.holidayDao = holidayDao;
	}

	@Override
	public List<InstallmentDate> generate(LocalDate actualDisbursementDate, int numberOfInstallments, GraceType graceType, int gracePeriodDuration, Short officeId) {
		
		List<InstallmentDate> installmentDates = new ArrayList<InstallmentDate>();
		
        if (numberOfInstallments > 0) {
            List<Days> workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();
            List<Holiday> holidays = new ArrayList<Holiday>();

            LocalDate startFromMeetingDate = null;
            if (scheduledEvent instanceof DailyScheduledEvent) {
                startFromMeetingDate = actualDisbursementDate.plusDays(scheduledEvent.getEvery());
            } else {
                startFromMeetingDate = actualDisbursementDate.plusDays(1);
            }

            holidays = holidayDao.findAllHolidaysFromDateAndNext(officeId, startFromMeetingDate.toString());

            int occurrences = numberOfInstallments;
            if (graceType == GraceType.GRACEONALLREPAYMENTS) {
                occurrences += gracePeriodDuration;
            }

            ScheduledDateGeneration dateGeneration = new HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration(workingDays, holidays);

            List<Date> dueDates = new ArrayList<Date>();
            List<DateTime> installmentDateTimes = dateGeneration.generateScheduledDates(occurrences, startFromMeetingDate.toDateMidnight().toDateTime(), scheduledEvent, false);
            for (DateTime installmentDate : installmentDateTimes) {
                dueDates.add(installmentDate.toDate());
            }

            installmentDates = createInstallmentDates(dueDates);
            if (graceType == GraceType.GRACEONALLREPAYMENTS) {
                removeInstallmentsNeedNotPay(gracePeriodDuration, installmentDates);
            }
        }
		
		return installmentDates;
	}
	
    private List<InstallmentDate> createInstallmentDates(final List<Date> dueDates) {
        List<InstallmentDate> installmentDates = new ArrayList<InstallmentDate>();
        int installmentId = 1;
        for (Date date : dueDates) {
            installmentDates.add(new InstallmentDate((short) installmentId++, date));
        }

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