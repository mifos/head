package org.mifos.accounts.loan.util.helpers;

import org.mifos.framework.util.helpers.Money;

import java.util.Locale;

public class RepaymentScheduleInstallmentBuilder {
    private RepaymentScheduleInstallment repaymentScheduleInstallment;

    public RepaymentScheduleInstallmentBuilder(Locale locale) {
        reset(locale);
    }

    public RepaymentScheduleInstallmentBuilder withInstallment(Integer installment) {
        this.repaymentScheduleInstallment.setInstallment(installment);
        return this;
    }

    public RepaymentScheduleInstallmentBuilder withDueDate(String dueDate) {
        this.repaymentScheduleInstallment.setDueDate(dueDate);
        return this;
    }

    public RepaymentScheduleInstallmentBuilder withFees(Money fees) {
        this.repaymentScheduleInstallment.setFees(fees);
        return this;
    }

    public RepaymentScheduleInstallmentBuilder withInterest(Money interest) {
        this.repaymentScheduleInstallment.setInterest(interest);
        return this;
    }

    public RepaymentScheduleInstallmentBuilder withPrincipal(Money principal) {
        this.repaymentScheduleInstallment.setPrincipal(principal);
        return this;
    }

    public RepaymentScheduleInstallmentBuilder withTotal(String total) {
        this.repaymentScheduleInstallment.setTotal(total);
        return this;
    }

    public RepaymentScheduleInstallment build() {
        return repaymentScheduleInstallment;
    }

    public RepaymentScheduleInstallmentBuilder reset(Locale locale) {
        this.repaymentScheduleInstallment = new RepaymentScheduleInstallment(locale);
        return this;
    }

}
