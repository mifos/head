package org.mifos.accounts.loan.business.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Test;
import org.mifos.accounts.loan.util.helpers.LoanExceptionConstants;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.calendar.DayOfWeek;
import org.mifos.domain.builders.HolidayBuilder;
import org.mifos.framework.exceptions.ApplicationException;

public class LoanServiceTest {

    @Test
    public void validateDisbursementDateIsWorkingDayShouldReturnNormally () throws Exception {
        DateTime disbursementDate = new DateMidnight().toDateTime().withDayOfWeek(DayOfWeek.monday());
        List<Days> mondayIsWorkingDay = new ArrayList<Days>();
        mondayIsWorkingDay.add(Days.ONE);
        new LoanService().validateDisbursementDateIsWorkingDay(disbursementDate, mondayIsWorkingDay);
    }

    @Test
    public void validateDisbursementDateIsWorkingDayShouldThrowException () throws Exception {
        DateTime tuesdayDisbursementDate = new DateMidnight().toDateTime().withDayOfWeek(DayOfWeek.tuesday());
        List<Days> mondayIsSoleWorkingDay = new ArrayList<Days>();
        mondayIsSoleWorkingDay.add(Days.ONE);
        try {
        new LoanService().validateDisbursementDateIsWorkingDay(tuesdayDisbursementDate, mondayIsSoleWorkingDay);
        fail("Should have thrown ApplicationException");
        } catch (ApplicationException e) {
            assertThat(e.getKey(), is(LoanExceptionConstants.DISBURSEMENTDATE_MUST_BE_A_WORKING_DAY));
        }
    }

    @Test
    public void validateDisbursementDateIsNotInHolidayShouldReturnNormallyWhenThereAreNoHolidays() throws Exception {
        DateTime disbursementDate = new DateMidnight().toDateTime().withDayOfWeek(DayOfWeek.monday());
        new LoanService().validateDisbursementDateIsNotInHoliday(disbursementDate, new ArrayList<Holiday>());
    }

    @Test
    public void validateDisbursementDateIsNotInHolidayShouldReturnNormallyWhenNotInAHoliday() throws Exception {
        DateTime disbursementDate = new DateMidnight().toDateTime().withDayOfWeek(DayOfWeek.monday());
        List<Holiday> holidays = new ArrayList<Holiday>();
        Holiday holiday = new HolidayBuilder().from(disbursementDate.plusDays(1)).to(disbursementDate.plusDays(6)).build();
        holidays.add(holiday);
        new LoanService().validateDisbursementDateIsNotInHoliday(disbursementDate, holidays);
    }

    @Test
    public void validateDisbursementDateIsNotInHolidayShouldThrowExceptionWhenDateIsInAHoliday () throws Exception {
        DateTime disbursementDate = new DateMidnight().toDateTime().withDayOfWeek(DayOfWeek.monday());
        List<Holiday> holidays = new ArrayList<Holiday>();
        Holiday holiday = new HolidayBuilder().from(disbursementDate.minusDays(1)).to(disbursementDate.plusDays(6)).build();
        holidays.add(holiday);
        try {
        new LoanService().validateDisbursementDateIsNotInHoliday(disbursementDate, holidays);
        fail("Should have thrown ApplicationException");
        } catch (ApplicationException e) {
            assertThat(e.getKey(), is(LoanExceptionConstants.DISBURSEMENTDATE_MUST_NOT_BE_IN_A_HOLIDAY));
        }
    }

    @Test
    public void validateDisbursementDateIsNotInHolidayShouldThrowExceptionWhenDateIsFirstDayOfHoliday () throws Exception {
        DateTime disbursementDate = new DateMidnight().toDateTime().withDayOfWeek(DayOfWeek.monday());
        List<Holiday> holidays = new ArrayList<Holiday>();
        Holiday holiday = new HolidayBuilder().from(disbursementDate).to(disbursementDate.plusDays(6)).build();
        holidays.add(holiday);
        try {
        new LoanService().validateDisbursementDateIsNotInHoliday(disbursementDate, holidays);
        fail("Should have thrown ApplicationException");
        } catch (ApplicationException e) {
            assertThat(e.getKey(), is(LoanExceptionConstants.DISBURSEMENTDATE_MUST_NOT_BE_IN_A_HOLIDAY));
        }
    }

    @Test
    public void validateDisbursementDateIsNotInHolidayShouldThrowExceptionWhenDateIsLastDayOfHoliday () throws Exception {
        DateTime disbursementDate = new DateMidnight().toDateTime().withDayOfWeek(DayOfWeek.monday());
        List<Holiday> holidays = new ArrayList<Holiday>();
        Holiday holiday = new HolidayBuilder().from(disbursementDate.minusDays(5)).to(disbursementDate).build();
        holidays.add(holiday);
        try {
        new LoanService().validateDisbursementDateIsNotInHoliday(disbursementDate, holidays);
        fail("Should have thrown ApplicationException");
        } catch (ApplicationException e) {
            assertThat(e.getKey(), is(LoanExceptionConstants.DISBURSEMENTDATE_MUST_NOT_BE_IN_A_HOLIDAY));
        }
    }

}
