package org.mifos.accounts.loan.schedule.domain;

import org.apache.commons.lang.ObjectUtils;

import java.math.BigDecimal;
import java.util.Date;

import static org.mifos.accounts.loan.schedule.utils.Utilities.isGreaterThanZero;

public class Installment implements Comparable<Installment> {
    private Integer id;
    private Date dueDate;
    private BigDecimal principal;
    private BigDecimal interest;
    private BigDecimal fees;
    private BigDecimal overdueInterest;
    private InstallmentPayments payments;

    public Installment() {
        payments = new InstallmentPayments();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public BigDecimal getOverdueInterest() {
        return overdueInterest == null ? BigDecimal.ZERO : overdueInterest;
    }

    public void setOverdueInterest(BigDecimal overdueInterest) {
        this.overdueInterest = overdueInterest;
    }

    public BigDecimal getFees() {
        return fees;
    }

    public void setFees(BigDecimal fees) {
        this.fees = fees;
    }

    public boolean isDue() {
        return isFeesDue() || isInterestDue() || isPrincipalDue() || isOverdueInterestDue();
    }

    public int compareTo(Installment installment) {
        return this.getId().compareTo(installment.getId());
    }

    public BigDecimal getFeesDue() {
        return fees.subtract(payments.getFeesPaid());
    }

    public BigDecimal getInterestDue() {
        return interest.subtract(payments.getInterestPaid());
    }

    public BigDecimal getPrincipalDue() {
        return principal.subtract(getPrincipalPaid());
    }

    public BigDecimal getOverdueInterestDue() {
        return overdueInterest.subtract(payments.getOverdueInterestPaid());
    }

    public boolean isFeesDue() {
        return isGreaterThanZero(getFeesDue());
    }

    public boolean isInterestDue() {
        return isGreaterThanZero(getInterestDue());
    }

    public boolean isOverdueInterestDue() {
        return isGreaterThanZero(getOverdueInterestDue());
    }

    public boolean isPrincipalDue() {
        return isGreaterThanZero(getPrincipalDue());
    }

    public BigDecimal pay(BigDecimal amount, Date transactionDate) {
        InstallmentPayment installmentPayment = new InstallmentPayment();
        installmentPayment.setPaidDate(transactionDate);
        amount = payOverdueInterest(amount, installmentPayment);
        amount = payFees(amount, installmentPayment);
        amount = payInterest(amount, installmentPayment);
        amount = payPrincipal(amount, installmentPayment);
        payments.addPayment(installmentPayment);
        return amount;
    }

    private BigDecimal payFees(BigDecimal amount, InstallmentPayment installmentPayment) {
        BigDecimal payable = getLowestOf(getFeesDue(), amount);
        installmentPayment.setFeesPaid(installmentPayment.getFeesPaid().add(payable));
        return amount.subtract(payable);
    }

    private BigDecimal payInterest(BigDecimal amount, InstallmentPayment installmentPayment) {
        BigDecimal payable = getLowestOf(getInterestDue(), amount);
        installmentPayment.setInterestPaid(installmentPayment.getInterestPaid().add(payable));
        return amount.subtract(payable);
    }

    public BigDecimal payInterestDueTillDate(BigDecimal amount, Date transactionDate, BigDecimal interestDueTillDate) {
        InstallmentPayment installmentPayment = new InstallmentPayment();
        installmentPayment.setPaidDate(transactionDate);
        BigDecimal payable = getLowestOf(interestDueTillDate, amount);
        installmentPayment.setInterestPaid(installmentPayment.getInterestPaid().add(payable));
        payments.addPayment(installmentPayment);
        return amount.subtract(payable);
    }

    private BigDecimal payOverdueInterest(BigDecimal amount, InstallmentPayment installmentPayment) {
        BigDecimal payable = getLowestOf(getOverdueInterestDue(), amount);
        installmentPayment.setOverdueInterestPaid(installmentPayment.getOverdueInterestPaid().add(payable));
        return amount.subtract(payable);
    }

    private BigDecimal getLowestOf(BigDecimal overdueInterest, BigDecimal amount) {
        return amount.compareTo(overdueInterest) > 0 ? overdueInterest : amount;
    }

    private BigDecimal payPrincipal(BigDecimal amount, InstallmentPayment installmentPayment) {
        BigDecimal payable = getLowestOf(getPrincipalDue(), amount);
        installmentPayment.setPrincipalPaid(installmentPayment.getPrincipalPaid().add(payable));
        return amount.subtract(payable);
    }

    public BigDecimal payPrincipal(BigDecimal amount, Date transactionDate) {
        InstallmentPayment installmentPayment = new InstallmentPayment();
        installmentPayment.setPaidDate(transactionDate);
        payments.addPayment(installmentPayment);
        return payPrincipal(amount, installmentPayment);
    }

    public BigDecimal payOverdueInterest(BigDecimal amount, Date transactionDate) {
        InstallmentPayment installmentPayment = new InstallmentPayment();
        installmentPayment.setPaidDate(transactionDate);
        payments.addPayment(installmentPayment);
        return payOverdueInterest(amount, installmentPayment);
    }

    public BigDecimal getPrincipalPaid() {
        return payments.getPrincipalPaid();
    }

    public Date getEarliestPaidDate() {
        return (Date) ObjectUtils.min(getTotalPaymentDate(), dueDate);
    }

    private Date getTotalPaymentDate() {
        return isDue() ? this.dueDate : getRecentPartialPaymentDate();
    }

    public Date getRecentPartialPaymentDate() {
        return payments.getRecentPartialPaymentDate();
    }

    public boolean isAnyPrincipalPaid() {
        return isGreaterThanZero(getPrincipalPaid());
    }

    public Date getRecentPrincipalPaidDate() {
        return payments.getRecentPrincipalPaidDate();
    }

    public void addOverdueInterest(BigDecimal overdueInterest) {
        setOverdueInterest(getOverdueInterest().add(overdueInterest));
    }
}
