package org.mifos.accounts.loan.schedule.domain;

import java.math.BigDecimal;
import java.util.Date;

import static org.mifos.accounts.loan.schedule.utils.Utilities.isGreaterThanZero;

public class InstallmentPayment {
    private Date paidDate;
    private BigDecimal principalPaid;
    private BigDecimal interestPaid;
    private BigDecimal feesPaid;
    private BigDecimal overdueInterestPaid;

    public Date getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(Date paidDate) {
        this.paidDate = paidDate;
    }

    public BigDecimal getPrincipalPaid() {
        return principalPaid == null ? BigDecimal.ZERO : principalPaid;
    }

    public void setPrincipalPaid(BigDecimal principalPaid) {
        this.principalPaid = principalPaid;
    }

    public BigDecimal getInterestPaid() {
        return interestPaid == null ? BigDecimal.ZERO : interestPaid;
    }

    public void setInterestPaid(BigDecimal interestPaid) {
        this.interestPaid = interestPaid;
    }

    public BigDecimal getFeesPaid() {
        return feesPaid == null ? BigDecimal.ZERO : feesPaid;
    }

    public void setFeesPaid(BigDecimal feesPaid) {
        this.feesPaid = feesPaid;
    }

    public BigDecimal getOverdueInterestPaid() {
        return overdueInterestPaid == null ? BigDecimal.ZERO : overdueInterestPaid;
    }

    public void setOverdueInterestPaid(BigDecimal overdueInterestPaid) {
        this.overdueInterestPaid = overdueInterestPaid;
    }

    public boolean isPartialPayment() {
        return isGreaterThanZero(getFeesPaid()) || isGreaterThanZero(getInterestPaid()) || isPrincipalPayment();
    }

    public boolean isPrincipalPayment() {
        return isGreaterThanZero(getPrincipalPaid());
    }
}
