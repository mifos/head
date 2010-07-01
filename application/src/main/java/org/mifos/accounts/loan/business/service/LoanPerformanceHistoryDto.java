package org.mifos.accounts.loan.business.service;

import java.sql.Date;

import org.mifos.framework.business.service.DataTransferObject;

public class LoanPerformanceHistoryDto implements DataTransferObject {

    private final Integer noOfPayments;
    private final Integer totalNoOfMissedPayments;
    private final Short daysInArrears;
    private final Date loanMaturityDate;

    public LoanPerformanceHistoryDto(Integer noOfPayments, Integer totalNoOfMissedPayments, Short daysInArrears,
            Date loanMaturityDate) {
        super();
        this.noOfPayments = noOfPayments;
        this.totalNoOfMissedPayments = totalNoOfMissedPayments;
        this.daysInArrears = daysInArrears;
        this.loanMaturityDate = loanMaturityDate;
    }

    public Integer getNoOfPayments() {
        return this.noOfPayments;
    }

    public Integer getTotalNoOfMissedPayments() {
        return this.totalNoOfMissedPayments;
    }

    public Short getDaysInArrears() {
        return this.daysInArrears;
    }

    public Date getLoanMaturityDate() {
        return this.loanMaturityDate;
    }
}
