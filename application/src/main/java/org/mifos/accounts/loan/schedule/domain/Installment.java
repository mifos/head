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
        return overdueInterest;
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
        BigDecimal balance = amount;
        if (isGreaterThanZero(balance) && isDue()) {
            InstallmentPayment installmentPayment = new InstallmentPayment();
            installmentPayment.setPaidDate(transactionDate);
            balance = payFees(balance, installmentPayment);
            balance = payInterest(balance, installmentPayment);
            balance = payPrincipal(balance, installmentPayment);
            balance = payOverdueInterest(balance, installmentPayment);
            payments.addPayment(installmentPayment);
        }
        return balance;
    }

    private BigDecimal payFees(BigDecimal amount, InstallmentPayment installmentPayment) {
        BigDecimal balance = amount;
        if (isGreaterThanZero(balance) && isFeesDue()) {
            BigDecimal feesDue = getFeesDue();
            if (amount.compareTo(feesDue) >= 0) {
                installmentPayment.setFeesPaid(feesDue);
                balance = amount.subtract(feesDue);
            } else {
                installmentPayment.setFeesPaid(amount);
                balance = BigDecimal.ZERO;
            }
        }
        return balance;
    }

    private BigDecimal payInterest(BigDecimal amount, InstallmentPayment installmentPayment) {
        BigDecimal balance = amount;
        if (isGreaterThanZero(balance) && isInterestDue()) {
            BigDecimal interestDue = getInterestDue();
            if (amount.compareTo(interestDue) >= 0) {
                installmentPayment.setInterestPaid(interestDue);
                balance = amount.subtract(interestDue);
            } else {
                installmentPayment.setInterestPaid(amount);
                balance = BigDecimal.ZERO;
            }
        }
        return balance;
    }

    public BigDecimal payInterestDueTillDate(BigDecimal amount, Date transactionDate, BigDecimal interestDueTillDate) {
        BigDecimal balance = amount;
        if (isGreaterThanZero(balance) && isGreaterThanZero(interestDueTillDate)) {
            InstallmentPayment installmentPayment = new InstallmentPayment();
            installmentPayment.setPaidDate(transactionDate);
            if (amount.compareTo(interestDueTillDate) >= 0) {
                installmentPayment.setInterestPaid(interestDueTillDate);
                balance = amount.subtract(interestDueTillDate);
            } else {
                installmentPayment.setInterestPaid(amount);
                balance = BigDecimal.ZERO;
            }
            payments.addPayment(installmentPayment);
        }
        return balance;
    }

    private BigDecimal payOverdueInterest(BigDecimal amount, InstallmentPayment installmentPayment) {
        BigDecimal balance = amount;
        if (isGreaterThanZero(balance) && isOverdueInterestDue()) {
            BigDecimal overdueInterestDue = getOverdueInterestDue();
            if (amount.compareTo(overdueInterestDue) >= 0) {
                installmentPayment.setOverdueInterestPaid(overdueInterestDue);
                balance = amount.subtract(overdueInterestDue);
            } else {
                installmentPayment.setOverdueInterestPaid(amount);
                balance = BigDecimal.ZERO;
            }
        }
        return balance;
    }

    private BigDecimal payPrincipal(BigDecimal amount, InstallmentPayment installmentPayment) {
        BigDecimal balance = amount;
        if (isGreaterThanZero(balance) && isPrincipalDue()) {
            BigDecimal principalDue = getPrincipalDue();
            if (amount.compareTo(principalDue) >= 0) {
                installmentPayment.setPrincipalPaid(principalDue);
                balance = amount.subtract(principalDue);
            } else {
                installmentPayment.setPrincipalPaid(amount);
                balance = BigDecimal.ZERO;
            }
        }
        return balance;
    }

    public BigDecimal payPrincipal(BigDecimal amount, Date transactionDate) {
        InstallmentPayment installmentPayment = new InstallmentPayment();
        installmentPayment.setPaidDate(transactionDate);
        BigDecimal balance = payPrincipal(amount, installmentPayment);
        payments.addPayment(installmentPayment);
        return balance;
    }

    public BigDecimal payOverdueInterest(BigDecimal amount, Date transactionDate) {
        InstallmentPayment installmentPayment = new InstallmentPayment();
        installmentPayment.setPaidDate(transactionDate);
        BigDecimal balance = payOverdueInterest(amount, installmentPayment);
        payments.addPayment(installmentPayment);
        return balance;
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
}
