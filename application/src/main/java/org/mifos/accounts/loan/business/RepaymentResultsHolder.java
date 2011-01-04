package org.mifos.accounts.loan.business;

import java.math.BigDecimal;

public class RepaymentResultsHolder {
    private BigDecimal totalRepaymentAmount;
    private BigDecimal waiverAmount;

    public RepaymentResultsHolder() {
        totalRepaymentAmount = BigDecimal.ZERO;
        waiverAmount = BigDecimal.ZERO;
    }

    public BigDecimal getTotalRepaymentAmount() {
        return totalRepaymentAmount;
    }

    public void setTotalRepaymentAmount(BigDecimal totalRepaymentAmount) {
        this.totalRepaymentAmount = totalRepaymentAmount;
    }

    public BigDecimal getWaiverAmount() {
        return waiverAmount;
    }

    public void setWaiverAmount(BigDecimal waiverAmount) {
        this.waiverAmount = waiverAmount;
    }
}
