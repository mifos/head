package org.mifos.accounts.loan.util.helpers;

import java.util.Date;
import java.util.Locale;

import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;

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

    public RepaymentScheduleInstallmentBuilder withTotalValue(String totalValue) {
        this.repaymentScheduleInstallment.setTotalValue(Double.valueOf(totalValue));
        return this;
    }

    public RepaymentScheduleInstallmentBuilder withDueDateValue(String dueDate) {
        Locale dateLocale = repaymentScheduleInstallment.getLocale();
        String dateFormat = repaymentScheduleInstallment.getDateFormat();
        Date dateValue = DateUtils.getDate(dueDate, dateLocale, dateFormat);
        this.repaymentScheduleInstallment.setDueDateValue(dateValue);
        return this;
    }
}
