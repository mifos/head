package org.mifos.accounts.loan.business.service;

import org.mifos.framework.util.helpers.Money;

import org.mifos.framework.business.service.DataTransferObject;

public class LoanSummaryDto implements DataTransferObject {

    private final Money originalPrincipal;
    private final Money principalPaid;
    private final Money principalDue;

    private final Money originalInterest;
    private final Money interestPaid;
    private final Money interestDue;

    private final Money originalFees;
    private final Money feesPaid;
    private final Money feesDue;

    private final Money originalPenalty;
    private final Money penaltyPaid;
    private final Money penaltyDue;

    private final Money totalLoanAmnt;
    private final Money totalAmntPaid;
    private final Money totalAmntDue;

    public LoanSummaryDto(Money originalPrincipal, Money principalPaid, Money principalDue, Money originalInterest,
            Money interestPaid, Money interestDue, Money originalFees, Money feesPaid, Money feesDue,
            Money originalPenalty, Money penaltyPaid, Money penaltyDue, Money totalLoanAmnt, Money totalAmntPaid,
            Money totalAmntDue) {
        super();
        this.originalPrincipal = originalPrincipal;
        this.principalPaid = principalPaid;
        this.principalDue = principalDue;
        this.originalInterest = originalInterest;
        this.interestPaid = interestPaid;
        this.interestDue = interestDue;
        this.originalFees = originalFees;
        this.feesPaid = feesPaid;
        this.feesDue = feesDue;
        this.originalPenalty = originalPenalty;
        this.penaltyPaid = penaltyPaid;
        this.penaltyDue = penaltyDue;
        this.totalLoanAmnt = totalLoanAmnt;
        this.totalAmntPaid = totalAmntPaid;
        this.totalAmntDue = totalAmntDue;
    }

    public Money getOriginalPrincipal() {
        return this.originalPrincipal;
    }
    public Money getPrincipalPaid() {
        return this.principalPaid;
    }
    public Money getPrincipalDue() {
        return this.principalDue;
    }
    public Money getOriginalInterest() {
        return this.originalInterest;
    }
    public Money getInterestPaid() {
        return this.interestPaid;
    }
    public Money getInterestDue() {
        return this.interestDue;
    }
    public Money getOriginalFees() {
        return this.originalFees;
    }
    public Money getFeesPaid() {
        return this.feesPaid;
    }
    public Money getFeesDue() {
        return this.feesDue;
    }
    public Money getOriginalPenalty() {
        return this.originalPenalty;
    }
    public Money getPenaltyPaid() {
        return this.penaltyPaid;
    }
    public Money getPenaltyDue() {
        return this.penaltyDue;
    }
    public Money getTotalLoanAmnt() {
        return this.totalLoanAmnt;
    }
    public Money getTotalAmntPaid() {
        return this.totalAmntPaid;
    }
    public Money getTotalAmntDue() {
        return this.totalAmntDue;
    }
}
