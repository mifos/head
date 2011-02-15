package org.mifos.clientportfolio.newloan.domain;

public class LoanUseageDetailImpl implements LoanUsuageDetail {

    private final boolean interestDeductedAtDisbursement;
    private final boolean principalDueOnLastInstallment;

    public LoanUseageDetailImpl(boolean interestDeductedAtDisbursement, boolean principalDueOnLastInstallment) {
        this.interestDeductedAtDisbursement = interestDeductedAtDisbursement;
        this.principalDueOnLastInstallment = principalDueOnLastInstallment;
    }

    @Override
    public boolean isInterestDeductedAtDisbursement() {
        return this.interestDeductedAtDisbursement;
    }

    @Override
    public boolean isPrincipalDueOnLastInstallment() {
        return this.principalDueOnLastInstallment;
    }
}