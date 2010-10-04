package org.mifos.accounts.loan.business.service.validators;

import org.mifos.accounts.loan.util.helpers.VariableInstallmentDetailsDto;

import java.util.Date;

public class InstallmentValidationContext {
    private Date disbursementDate;
    private VariableInstallmentDetailsDto variableInstallmentDetails;

    public InstallmentValidationContext(Date disbursementDate, VariableInstallmentDetailsDto variableInstallmentDetails) {
        this.disbursementDate = disbursementDate;
        this.variableInstallmentDetails = variableInstallmentDetails;
    }

    public Date getDisbursementDate() {
        return disbursementDate;
    }

    public VariableInstallmentDetailsDto getVariableInstallmentDetails() {
        return variableInstallmentDetails;
    }
}
