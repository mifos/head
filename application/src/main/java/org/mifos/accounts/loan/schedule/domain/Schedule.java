package org.mifos.accounts.loan.schedule.domain;

import org.apache.commons.lang.ObjectUtils;
import org.mifos.accounts.loan.schedule.utils.Utilities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.mifos.accounts.loan.schedule.utils.Utilities.getDaysInBetween;

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
        for (Installment dueInstallment : getInstallmentsOnOrBefore(transactionDate)) {
            amount = dueInstallment.pay(amount, transactionDate);
        }
        return amount;
    }

    public void adjustFutureInstallments(BigDecimal balance, Date transactionDate) {
        List<Installment> futureInstallments = getInstallmentsAfter(transactionDate);
        BigDecimal principalOutstanding = adjustPrincipalsForInstallments(balance, transactionDate, futureInstallments);
        adjustInterestForInstallments(futureInstallments, principalOutstanding);
    }

    private void adjustInterestForInstallments(List<Installment> futureInstallments, BigDecimal principalOutstanding) {
        for (Installment installment : futureInstallments) {
            long duration = getDurationForAdjustment(installment, installment.getDueDate());
            if (duration <= 0) continue;
            BigDecimal principalForInterest = computePrincipalForInterest(principalOutstanding, installment);
            installment.setEffectiveInterest(computeInterest(principalForInterest, duration));
        }
    }

    private long getDurationForAdjustment(Installment installment, Date toDate) {
        Installment previousInstallment = getPreviousInstallment(installment);
        Date prevDueDate = previousInstallment != null ? previousInstallment.getEarliestPaidDate() : this.disbursementDate;
        prevDueDate = (Date) ObjectUtils.max(prevDueDate, installment.getRecentPartialPaymentDate());
        return getDaysInBetween(toDate, prevDueDate);
    }

    private BigDecimal getPrincipalDueTill(Installment installment) {
        BigDecimal principalDue = BigDecimal.ZERO;
        for (Installment _installment : installments.values()) {
            if (_installment.compareTo(installment) >= 0) break;
            principalDue = principalDue.add(_installment.getPrincipalDue());
        }
        return principalDue;
    }

    private BigDecimal adjustPrincipalsForInstallments(BigDecimal balance, Date transactionDate, List<Installment> futureInstallments) {
        BigDecimal principalOutstanding = this.loanAmount.subtract(getPrincipalPaid());
        for (Installment installment : futureInstallments) {
            balance = installment.payOverdueInterest(balance, transactionDate);
            balance = installment.payInterestDueTillDate(balance, transactionDate,
                    computeInterestTillDueDate(transactionDate, principalOutstanding, installment));
            balance = installment.payPrincipal(balance, transactionDate);
            principalOutstanding = principalOutstanding.subtract(installment.getRecentPrincipalPayment());
        }
        return principalOutstanding;
    }

    private BigDecimal computeInterestTillDueDate(Date transactionDate, BigDecimal principalOutstanding, Installment installment) {
        long duration = getDurationForAdjustment(installment, transactionDate);
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

    private Installment getNextInstallment(Installment installment) {
        return installments.get(installment.getId() + 1);
    }

    public void computeOverdueInterest(Date transactionDate) {
        for (Installment installment : installments.values()) {
            Installment nextInstallment = getNextInstallment(installment);
            if (nextInstallment != null && installment.isPrincipalDue()) {
                BigDecimal principalDue = installment.getPrincipalDue();
                if (installment.isAnyPrincipalPaid()) {
                    updateOverdueInterest(transactionDate, installment, nextInstallment, principalDue);
                } else {
                    setOverdueInterest(transactionDate, installment, nextInstallment, principalDue);
                }
            }
        }
    }

    private void setOverdueInterest(Date transactionDate, Installment installment, Installment nextInstallment, BigDecimal principalDue) {
        long duration = getDaysInBetween(transactionDate, installment.getDueDate());
        if (duration <= 0) return;
        nextInstallment.setOverdueInterest(computeInterest(principalDue, duration));
    }

    private void updateOverdueInterest(Date transactionDate, Installment installment, Installment nextInstallment, BigDecimal principalDue) {
        long duration = getDaysInBetween(transactionDate, installment.getRecentPrincipalPaidDate());
        if (duration <= 0) return;
        nextInstallment.addOverdueInterest(computeInterest(principalDue, duration));
    }
}
