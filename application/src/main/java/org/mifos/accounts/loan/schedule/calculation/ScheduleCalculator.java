package org.mifos.accounts.loan.schedule.calculation;

import org.mifos.accounts.loan.schedule.domain.Schedule;

import java.math.BigDecimal;
import java.util.Date;

public class ScheduleCalculator {
    public void applyPayment(Schedule schedule, BigDecimal amount, Date transactionDate) {
        BigDecimal balance = schedule.payInstallmentsOnOrBefore(transactionDate, amount);
        schedule.adjustFutureInstallments(balance, transactionDate);
    }

    public void computeOverdueInterest(Schedule schedule, Date transactionDate) {
        schedule.computeOverdueInterest(transactionDate);
    }
}
