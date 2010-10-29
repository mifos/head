package org.mifos.accounts.loan.schedule.domain;

import org.apache.commons.lang.ObjectUtils;
import org.mifos.accounts.loan.schedule.utils.Utilities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.mifos.accounts.loan.schedule.utils.Utilities.isGreaterThanZero;

public class Schedule {
    private Map<Integer, Installment> installments;
    private Date disbursementDate;
    private Double dailyInterestRate;
    private BigDecimal loanAmount;

    public Schedule() {
        installments = new TreeMap<Integer, Installment>();
    }

    public void setInstallments(List<Installment> installments) {
        for (Installment installment : installments) {
            this.installments.put(installment.getId(), installment);
        }
    }

    public Date getDisbursementDate() {
        return disbursementDate;
    }

    public void setDisbursementDate(Date disbursementDate) {
        this.disbursementDate = disbursementDate;
    }

    public Double getDailyInterestRate() {
        return dailyInterestRate;
    }

    public void setDailyInterestRate(Double dailyInterestRate) {
        this.dailyInterestRate = dailyInterestRate;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public void addInstallment(Installment installment) {
        this.installments.put(installment.getId(), installment);
    }

    public BigDecimal payInstallmentsOnOrBefore(Date transactionDate, BigDecimal amount) {
        BigDecimal balance = amount;
        if (isGreaterThanZero(balance)) {
            for (Installment dueInstallment : getInstallmentsOnOrBefore(transactionDate)) {
                balance = dueInstallment.pay(balance, transactionDate);
            }
        }
        return balance;
    }

    public void adjustFutureInstallments(BigDecimal balance, Date date) {
        if (isGreaterThanZero(balance)) {
            List<Installment> futureInstallments = getInstallmentsAfter(date);
            BigDecimal principalOutstanding = adjustPrincipalsForInstallments(balance, date, futureInstallments);
            adjustInterestForInstallments(futureInstallments, principalOutstanding);
        }
    }

    private void adjustInterestForInstallments(List<Installment> futureInstallments, BigDecimal principalOutstanding) {
        for (Installment installment : futureInstallments) {
            if (installment.isPrincipalDue()) {
                long duration = getDurationForAdjustment(installment, installment.getDueDate());
                if (duration <= 0) continue;
                BigDecimal principalForInterest = computePrincipalForInterest(principalOutstanding, installment);
                installment.setInterest(computeInterest(principalForInterest, duration));
            }
        }
    }

    private long getDurationForAdjustment(Installment installment, Date toDate) {
        Installment previousInstallment = getPreviousInstallment(installment);
        Date prevDueDate = previousInstallment != null ? previousInstallment.getEarliestPaidDate() : this.disbursementDate;
        prevDueDate = (Date) ObjectUtils.max(prevDueDate, installment.getRecentPartialPaymentDate());
        return Utilities.getDaysInBetween(toDate, prevDueDate);
    }

    private BigDecimal getPrincipalDueTill(Installment installment) {
        BigDecimal principalDue = BigDecimal.ZERO;
        for (Installment _installment : installments.values()) {
            if (_installment.compareTo(installment) >= 0) break;
            principalDue = principalDue.add(_installment.getPrincipalDue());
        }
        return principalDue;
    }

    private BigDecimal adjustPrincipalsForInstallments(BigDecimal balance, Date date, List<Installment> futureInstallments) {
        BigDecimal principalOutstanding = this.loanAmount.subtract(getPrincipalPaid());
        for (Installment installment : futureInstallments) {
            balance = installment.payOverdueInterest(balance, date);
            if (Utilities.isLesserThanOrEqualToZero(balance)) break;
            BigDecimal interestDueTillDate = computeInterestTillDueDate(date, principalOutstanding, installment);
            balance = installment.payInterestDueTillDate(balance, date, interestDueTillDate);
            if (Utilities.isLesserThanOrEqualToZero(balance)) break;
            balance = installment.payPrincipal(balance, date);
            principalOutstanding = principalOutstanding.subtract(installment.getPrincipalPaid());
            if (Utilities.isLesserThanOrEqualToZero(balance)) break;
        }
        return principalOutstanding;
    }

    private BigDecimal computeInterestTillDueDate(Date date, BigDecimal principalOutstanding, Installment installment) {
        long duration = getDurationForAdjustment(installment, date);
        if (duration <= 0) return BigDecimal.ZERO;
        BigDecimal principalForInterest = computePrincipalForInterest(principalOutstanding, installment);
        return computeInterest(principalForInterest, duration);
    }

    private BigDecimal computePrincipalForInterest(BigDecimal principalOutstanding, Installment installment) {
        return principalOutstanding.subtract(getPrincipalDueTill(installment));
    }

    private List<Installment> getInstallmentsOnOrBefore(Date date) {
        List<Installment> result = new ArrayList<Installment>();
        for (Installment installment : installments.values()) {
            Date dueDate = installment.getDueDate();
            // TODO Refine this date comparison logic
            if (dueDate.compareTo(date) <= 0) result.add(installment);
        }
        return result;
    }

    private List<Installment> getInstallmentsAfter(Date date) {
        List<Installment> result = new ArrayList<Installment>();
        for (Installment installment : installments.values()) {
            Date dueDate = installment.getDueDate();
            // TODO Refine this date comparison logic
            if (dueDate.compareTo(date) > 0) result.add(installment);
        }
        return result;
    }

    private BigDecimal computeInterest(BigDecimal principalOutstanding, long duration) {
        BigDecimal computedInterest = principalOutstanding.multiply(BigDecimal.valueOf(duration)).multiply(BigDecimal.valueOf(this.dailyInterestRate));
        return Utilities.round(computedInterest);
    }

    private BigDecimal getPrincipalPaid() {
        BigDecimal principalPaid = BigDecimal.ZERO;
        for (Installment installment : installments.values()) {
            principalPaid = principalPaid.add(installment.getPrincipalPaid());
        }
        return principalPaid;
    }

    private Installment getPreviousInstallment(Installment installment) {
        return installments.get(installment.getId() - 1);
    }
}
