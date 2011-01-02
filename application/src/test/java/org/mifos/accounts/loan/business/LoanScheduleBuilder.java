package org.mifos.accounts.loan.business;

import java.util.Date;

import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.framework.util.helpers.Money;

public class LoanScheduleBuilder {
    private LoanScheduleEntity loanScheduleEntity;

    public LoanScheduleBuilder(String installmentId, LoanBO loanBO) {
        loanScheduleEntity = new LoanScheduleEntity();
        withLoanBO(loanBO).withInstallmentId(installmentId).withPrincipal(0).withPrincipalPaid(0).withInterest(0).
                withInterestPaid(0).withExtraInterest(0).withExtraInterestPaid(0).withMiscFees(0).withMiscFeesPaid(0).
                withPenalty(0).withPenaltyPaid(0).withMiscPenalty(0).withMiscPenaltyPaid(0);
    }

    public LoanScheduleBuilder withLoanBO(LoanBO loanBO) {
        loanScheduleEntity.setAccount(loanBO);
        return this;
    }

    public LoanScheduleBuilder withInstallmentId(String installmentId) {
        loanScheduleEntity.setInstallmentId(Short.valueOf(installmentId));
        return this;
    }

    public LoanScheduleBuilder withDueDate(Date dueDate) {
        loanScheduleEntity.setActionDate(new java.sql.Date(dueDate.getTime()));
        return this;
    }

    public LoanScheduleBuilder withPaymentStatus(PaymentStatus paymentStatus) {
        loanScheduleEntity.setPaymentStatus(paymentStatus);
        return this;
    }

    public LoanScheduleBuilder withPrincipal(double principal) {
        loanScheduleEntity.setPrincipal(new Money(loanScheduleEntity.getCurrency(), principal));
        return this;
    }

    public LoanScheduleBuilder withInterest(double interest) {
        loanScheduleEntity.setInterest(new Money(loanScheduleEntity.getCurrency(), interest));
        return this;
    }

    public LoanScheduleBuilder withExtraInterest(double extraInterest) {
        loanScheduleEntity.setExtraInterest(new Money(loanScheduleEntity.getCurrency(), extraInterest));
        return this;
    }

    public LoanScheduleBuilder addFees(Integer feeId, double fees, double feesPaid) {
        LoanFeeScheduleEntity loanFeeScheduleEntity = new LoanFeeScheduleEntity();
        loanFeeScheduleEntity.setAccountFeesActionDetailId(feeId);
        loanFeeScheduleEntity.setInstallmentId(Short.valueOf(loanScheduleEntity.getInstallmentId()));
        loanFeeScheduleEntity.setFeeAmount(new Money(loanScheduleEntity.getCurrency(), fees));
        loanFeeScheduleEntity.setFeeAmountPaid(new Money(loanScheduleEntity.getCurrency(), feesPaid));
        loanScheduleEntity.addAccountFeesAction(loanFeeScheduleEntity);
        return this;
    }

    public LoanScheduleBuilder withMiscFees(double miscFees) {
        loanScheduleEntity.setMiscFee(new Money(loanScheduleEntity.getCurrency(), miscFees));
        return this;
    }

    public LoanScheduleBuilder withPenalty(double penalty) {
        loanScheduleEntity.setPenalty(new Money(loanScheduleEntity.getCurrency(), penalty));
        return this;
    }
    
    public LoanScheduleBuilder withMiscPenalty(double miscPenalty) {
        loanScheduleEntity.setMiscPenalty(new Money(loanScheduleEntity.getCurrency(), miscPenalty));
        return this;
    }

    public LoanScheduleBuilder withPrincipalPaid(double principalPaid) {
        loanScheduleEntity.setPrincipalPaid(new Money(loanScheduleEntity.getCurrency(), principalPaid));
        return this;
    }

    public LoanScheduleBuilder withInterestPaid(double interestPaid) {
        loanScheduleEntity.setInterestPaid(new Money(loanScheduleEntity.getCurrency(), interestPaid));
        return this;
    }

    public LoanScheduleBuilder withExtraInterestPaid(double extraInterestPaid) {
        loanScheduleEntity.setExtraInterestPaid(new Money(loanScheduleEntity.getCurrency(), extraInterestPaid));
        return this;
    }

    public LoanScheduleBuilder withMiscFeesPaid(double miscFeesPaid) {
        loanScheduleEntity.setMiscFeePaid(new Money(loanScheduleEntity.getCurrency(), miscFeesPaid));
        return this;
    }

    public LoanScheduleBuilder withPenaltyPaid(double penaltyPaid) {
        loanScheduleEntity.setPenaltyPaid(new Money(loanScheduleEntity.getCurrency(), penaltyPaid));
        return this;
    }

    public LoanScheduleBuilder withMiscPenaltyPaid(double miscPenaltyPaid) {
        loanScheduleEntity.setMiscPenaltyPaid(new Money(loanScheduleEntity.getCurrency(), miscPenaltyPaid));
        return this;
    }

    public LoanScheduleBuilder withPaymentDate(Date paymentDate) {
        loanScheduleEntity.setPaymentDate(new java.sql.Date(paymentDate.getTime()));
        return this;
    }

    public LoanScheduleEntity build() {
        return loanScheduleEntity;
    }
}
