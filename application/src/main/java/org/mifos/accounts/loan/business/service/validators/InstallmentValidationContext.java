package org.mifos.accounts.loan.business.service.validators;

import org.mifos.accounts.productdefinition.business.VariableInstallmentDetailsBO;
import org.mifos.config.FiscalCalendarRules;

import java.util.Date;

public class InstallmentValidationContext {
    private Date disbursementDate;
    private VariableInstallmentDetailsBO variableInstallmentDetails;
    private FiscalCalendarRules fiscalCalendarRules;

    public InstallmentValidationContext(Date disbursementDate, VariableInstallmentDetailsBO variableInstallmentDetails,
                                        FiscalCalendarRules fiscalCalendarRules) {
        this.disbursementDate = disbursementDate;
        this.variableInstallmentDetails = variableInstallmentDetails;
        this.fiscalCalendarRules = fiscalCalendarRules;
    }

    public Date getDisbursementDate() {
        return disbursementDate;
    }

    public VariableInstallmentDetailsBO getVariableInstallmentDetails() {
        return variableInstallmentDetails;
    }

    public FiscalCalendarRules getFiscalCalendarRules() {
        return fiscalCalendarRules;
    }
}
