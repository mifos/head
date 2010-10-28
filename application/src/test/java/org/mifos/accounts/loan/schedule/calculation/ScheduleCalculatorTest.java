package org.mifos.accounts.loan.schedule.calculation;

import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.loan.schedule.domain.Installment;
import org.mifos.accounts.loan.schedule.domain.Schedule;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ScheduleCalculatorTest {
    private ScheduleCalculator scheduleCalculator;
    private Schedule schedule;

    @Before
    public void setup() {
        scheduleCalculator = new ScheduleCalculator();
        schedule = getSchedule(getDate(25, 8, 2010), 0.000658, 1000d);
    }

    @Test
    public void shouldGenerateLoanSchedule_WithOneInstallmentAdjusted() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule.setInstallments(Arrays.asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(280d), getDate(25, 9, 2010));
        assertInstallment(installment1, 0, 20.40);
        assertInstallment(installment2, 230.31, 14.62);
        assertInstallment(installment3, 252.22, 10.40);
        assertInstallment(installment4, 257.87, 5.09);
    }

    @Test
    public void shouldGenerateLoanSchedule_WithTwoInstallmentsAdjusted() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule.setInstallments(Arrays.asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(550d), getDate(25, 9, 2010));
        assertInstallment(installment1, 0, 20.40);
        assertInstallment(installment2, 0, 14.96);
        assertInstallment(installment3, 212.53, 9.60);
        assertInstallment(installment4, 257.87, 5.09);
    }

    @Test
    public void shouldGenerateLoanSchedule_WithOverDueInterests() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0.8);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule.setInstallments(Arrays.asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(550d), getDate(30, 9, 2010));
        assertInstallment(installment1, 0, 20.40);
        assertInstallment(installment2, 0, 14.96);
        assertInstallment(installment3, 215.82, 9.66);
        assertInstallment(installment4, 257.87, 5.09);
    }

    @Test
    public void shouldGenerateLoanSchedule_WithShortPaymentBeforeDueDate() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule.setInstallments(Arrays.asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(240d), getDate(23, 9, 2010));
        assertInstallment(installment1, 21.32, 1.03);
        assertInstallment(installment2, 247.67, 14.96);
        assertInstallment(installment3, 252.22, 10.40);
        assertInstallment(installment4, 257.87, 5.09);
    }

    @Test
    public void shouldGenerateLoanSchedule_WithShortPaymentAfterDueDate() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0.64);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule.setInstallments(Arrays.asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(240d), getDate(29, 9, 2010));
        assertInstallment(installment1, 22.64, 20.40);
        assertInstallment(installment2, 247.67, 14.96);
        assertInstallment(installment3, 252.22, 10.40);
        assertInstallment(installment4, 257.87, 5.09);
    }

    @Test
    public void shouldGenerateLoanSchedule_WithExcessPaymentBeforeDueDate() {
        Installment installment1 = getInstallment(1, getDate(25, 9, 2010), 242.24, 20.40, 0);
        Installment installment2 = getInstallment(2, getDate(25, 10, 2010), 247.67, 14.96, 0);
        Installment installment3 = getInstallment(3, getDate(25, 11, 2010), 252.22, 10.40, 0);
        Installment installment4 = getInstallment(4, getDate(25, 12, 2010), 257.87, 5.09, 0);
        schedule.setInstallments(Arrays.asList(installment1, installment2, installment3, installment4));
        scheduleCalculator.applyPayment(schedule, BigDecimal.valueOf(280d), getDate(23, 9, 2010));
        assertInstallment(installment1, 0, 20.40);
        assertInstallment(installment2, 228.99, 14.59);
        assertInstallment(installment3, 252.22, 10.40);
        assertInstallment(installment4, 257.87, 5.09);
    }

    private Schedule getSchedule(Date disbursementDate, double dailyInterestRate, double loanAmount) {
        Schedule schedule = new Schedule();
        schedule.setDisbursementDate(disbursementDate);
        schedule.setDailyInterestRate(dailyInterestRate);
        schedule.setLoanAmount(BigDecimal.valueOf(loanAmount));
        return schedule;
    }

    private void assertInstallment(Installment installment, double principalDue, double interestApplicable) {
        assertThat(installment.getPrincipalDue().doubleValue(), is(principalDue));
        assertThat(installment.getInterest().doubleValue(), is(interestApplicable));
    }

    private Installment getInstallment(int id, Date dueDate, double principal, double interest, double overdueInterest) {
        Installment installment = new Installment();
        installment.setId(id);
        installment.setDueDate(dueDate);
        installment.setPrincipal(BigDecimal.valueOf(principal));
        installment.setInterest(BigDecimal.valueOf(interest));
        installment.setFees(BigDecimal.ZERO);
        installment.setOverdueInterest(BigDecimal.valueOf(overdueInterest));
        return installment;
    }

    private Date getDate(int date, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, date);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
