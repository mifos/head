package org.mifos.accounts.loan.business.service;

import java.util.Date;
import java.util.List;

import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.customers.api.DataTransferObject;
import org.mifos.framework.util.helpers.Money;

public class LoanScheduleGenerationDto implements DataTransferObject {
    private Date disbursementDate;
    private LoanBO loanBO;
    private boolean variableInstallmentsAllowed;
    private Money loanAmountValue;
    private Double interestRate;
    private List<RepaymentScheduleInstallment> installments;

    public LoanScheduleGenerationDto(Date disbursementDate, LoanBO loanBO, boolean variableInstallmentsAllowed,
                                     Money loanAmountValue, Double interestRate) {
        this(disbursementDate, loanAmountValue, interestRate, null);
        this.loanBO = loanBO;
        this.variableInstallmentsAllowed = variableInstallmentsAllowed;
    }

    public LoanScheduleGenerationDto(Date disbursementDate, Money loanAmountValue, Double interestRate,
                                     List<RepaymentScheduleInstallment> installments) {
        this.loanAmountValue = loanAmountValue;
        this.interestRate = interestRate;
        this.installments = installments;
        this.disbursementDate = disbursementDate;
    }

    public Date getDisbursementDate() {
        return disbursementDate;
    }

    public LoanBO getLoanBO() {
        return loanBO;
    }

    public boolean isVariableInstallmentsAllowed() {
        return variableInstallmentsAllowed;
    }

    public Money getLoanAmountValue() {
        return loanAmountValue;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public List<RepaymentScheduleInstallment> getInstallments() {
        return installments;
    }

    public void setInstallments(List<RepaymentScheduleInstallment> installments) {
        this.installments = installments;
    }
}
