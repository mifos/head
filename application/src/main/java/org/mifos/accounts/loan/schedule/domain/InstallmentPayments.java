package org.mifos.accounts.loan.schedule.domain;

import org.apache.commons.lang.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InstallmentPayments {
    private List<InstallmentPayment> installmentPayments;

    public InstallmentPayments() {
        installmentPayments = new ArrayList<InstallmentPayment>();
    }

    public BigDecimal getFeesPaid() {
        BigDecimal feesPaid = BigDecimal.ZERO;
        for (InstallmentPayment installmentPayment : installmentPayments) {
            feesPaid = feesPaid.add(installmentPayment.getFeesPaid());
        }
        return feesPaid;
    }

    public BigDecimal getInterestPaid() {
        BigDecimal interestPaid = BigDecimal.ZERO;
        for (InstallmentPayment installmentPayment : installmentPayments) {
            interestPaid = interestPaid.add(installmentPayment.getInterestPaid());
        }
        return interestPaid;
    }

    public BigDecimal getPrincipalPaid() {
        BigDecimal principalPaid = BigDecimal.ZERO;
        for (InstallmentPayment installmentPayment : installmentPayments) {
            principalPaid = principalPaid.add(installmentPayment.getPrincipalPaid());
        }
        return principalPaid;
    }

    public BigDecimal getOverdueInterestPaid() {
        BigDecimal overdueInterestPaid = BigDecimal.ZERO;
        for (InstallmentPayment installmentPayment : installmentPayments) {
            overdueInterestPaid = overdueInterestPaid.add(installmentPayment.getOverdueInterestPaid());
        }
        return overdueInterestPaid;
    }

    public void addPayment(InstallmentPayment installmentPayment) {
        installmentPayments.add(installmentPayment);
    }

    public Date getRecentPartialPaymentDate() {
        Date lastPaymentDate = null;
        for (InstallmentPayment installmentPayment : installmentPayments) {
            if (installmentPayment.isPartialPayment()) {
                lastPaymentDate = (Date) ObjectUtils.max(lastPaymentDate, installmentPayment.getPaidDate());
            }
        }
        return lastPaymentDate;
    }
}
