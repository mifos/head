package org.mifos.accounts.loan.util.helpers;

import java.text.ParseException;
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

    public RepaymentScheduleInstallmentBuilder withMiscFee(Money miscFee){
        this.repaymentScheduleInstallment.setMiscFees(miscFee);
        return this;
    }

    public RepaymentScheduleInstallmentBuilder withMiscPenality(Money miscPenality){
        this.repaymentScheduleInstallment.setMiscPenalty(miscPenality);
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
        this.repaymentScheduleInstallment = new RepaymentScheduleInstallment();
        return this;
    }

    public RepaymentScheduleInstallmentBuilder withTotalValue(String totalValue) {
        this.repaymentScheduleInstallment.setTotalValue(Double.valueOf(totalValue));
        return this;
    }

    public RepaymentScheduleInstallmentBuilder withDueDateValue(String dueDate) {
        Date dateValue = getDate(dueDate);
        this.repaymentScheduleInstallment.setDueDateValue(dateValue);
        return this;
    }

    private Date getDate(String dueDate) {
        Date date;
        try {
            date = DateUtils.parseDate(dueDate);
        } catch (ParseException e) {
            date = null;
        }
        return date;
    }
}
