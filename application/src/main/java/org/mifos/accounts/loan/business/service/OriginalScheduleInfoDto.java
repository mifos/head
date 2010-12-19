package org.mifos.accounts.loan.business.service;

import org.mifos.accounts.business.AccountCustomFieldEntity;
import org.mifos.accounts.business.AccountNotesEntity;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.customers.api.DataTransferObject;
import org.mifos.dto.domain.LoanActivityDto;
import org.mifos.dto.domain.SurveyDto;
import org.mifos.framework.util.helpers.Money;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class OriginalScheduleInfoDto implements DataTransferObject {
    private String loanAmount;
    private Date disbursementDate;
    private List<RepaymentScheduleInstallment> originalLoanScheduleInstallment;

    public OriginalScheduleInfoDto(String loanAmount, Date disbursementDate, List<RepaymentScheduleInstallment> originalLoanScheduleInstallment) {
        this.loanAmount = loanAmount;
        this.disbursementDate = disbursementDate;
        this.originalLoanScheduleInstallment = originalLoanScheduleInstallment;
    }

    public String getLoanAmount() {
        return loanAmount;
    }

    public Date getDisbursementDate() {
        return disbursementDate;
    }

    public List<RepaymentScheduleInstallment> getOriginalLoanScheduleInstallment() {
        return originalLoanScheduleInstallment;
    }
}
