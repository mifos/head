package org.mifos.accounts.loan.schedule.domain;

import java.math.BigDecimal;
import java.util.Date;

public class InstallmentBuilder {
    private Installment installment;
    private InstallmentPayment installmentPayment;

    public InstallmentBuilder(String installmentId) {
        installment = new Installment();
        installmentPayment = new InstallmentPayment();
        installment.addPayment(installmentPayment);
        withInstallmentId(installmentId).
                withDueDate(new Date(0)).withPaymentDate(new Date(0)).
                withPrincipal(0).withPrincipalPaid(0).
                withInterest(0).withInterestPaid(0).
                withExtraInterest(0).withExtraInterestPaid(0).
                withFees(0).withFeesPaid(0).
                withMiscFees(0).withMiscFeesPaid(0).
                withPenalty(0).withPenaltyPaid(0).
                withMiscPenalty(0).withMiscPenaltyPaid(0);
    }

    public InstallmentBuilder withInstallmentId(String installmentId) {
        installment.setId(Integer.valueOf(installmentId));
        return this;
    }

    public InstallmentBuilder withPrincipal(double principal) {
        installment.setAmount(InstallmentComponent.PRINCIPAL, BigDecimal.valueOf(principal));
        return this;
    }

    public InstallmentBuilder withInterest(double interest) {
        installment.setAmount(InstallmentComponent.INTEREST, BigDecimal.valueOf(interest));
        return this;
    }

    public InstallmentBuilder withExtraInterest(double extraInterest) {
        installment.setAmount(InstallmentComponent.EXTRA_INTEREST, BigDecimal.valueOf(extraInterest));
        return this;
    }

    public InstallmentBuilder withFees(double fees) {
        installment.setAmount(InstallmentComponent.FEES, BigDecimal.valueOf(fees));
        return this;
    }

    public InstallmentBuilder withMiscFees(double miscFees) {
        installment.setAmount(InstallmentComponent.MISC_FEES, BigDecimal.valueOf(miscFees));
        return this;
    }

    public InstallmentBuilder withPenalty(double penalty) {
        installment.setAmount(InstallmentComponent.PENALTY, BigDecimal.valueOf(penalty));
        return this;
    }

    public InstallmentBuilder withMiscPenalty(double miscPenalty) {
        installment.setAmount(InstallmentComponent.MISC_PENALTY, BigDecimal.valueOf(miscPenalty));
        return this;
    }

    public InstallmentBuilder withPrincipalPaid(double principalPaid) {
        installmentPayment.setAmount(InstallmentComponent.PRINCIPAL, BigDecimal.valueOf(principalPaid));
        return this;
    }

    public InstallmentBuilder withInterestPaid(double interestPaid) {
        installmentPayment.setAmount(InstallmentComponent.INTEREST, BigDecimal.valueOf(interestPaid));
        return this;
    }

    public InstallmentBuilder withExtraInterestPaid(double extraInterestPaid) {
        installmentPayment.setAmount(InstallmentComponent.EXTRA_INTEREST, BigDecimal.valueOf(extraInterestPaid));
        return this;
    }

    public InstallmentBuilder withFeesPaid(double fees) {
        installmentPayment.setAmount(InstallmentComponent.FEES, BigDecimal.valueOf(fees));
        return this;
    }

    public InstallmentBuilder withMiscFeesPaid(double miscFeesPaid) {
        installmentPayment.setAmount(InstallmentComponent.MISC_FEES, BigDecimal.valueOf(miscFeesPaid));
        return this;
    }

    public InstallmentBuilder withPenaltyPaid(double penaltyPaid) {
        installmentPayment.setAmount(InstallmentComponent.PENALTY, BigDecimal.valueOf(penaltyPaid));
        return this;
    }

    public InstallmentBuilder withMiscPenaltyPaid(double miscPenaltyPaid) {
        installmentPayment.setAmount(InstallmentComponent.MISC_PENALTY, BigDecimal.valueOf(miscPenaltyPaid));
        return this;
    }

    public InstallmentBuilder withDueDate(Date dueDate) {
        installment.setDueDate(dueDate);
        return this;
    }

    public InstallmentBuilder withPaymentDate(Date paymentDate) {
        installment.setDueDate(paymentDate);
        return this;
    }

    public Installment build() {
        return installment;
    }
}