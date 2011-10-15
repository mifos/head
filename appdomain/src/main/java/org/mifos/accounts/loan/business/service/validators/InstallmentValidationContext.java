package org.mifos.accounts.loan.business.service.validators;

import java.math.BigDecimal;
import java.util.Date;

import org.mifos.accounts.productdefinition.business.VariableInstallmentDetailsBO;
import org.mifos.application.admin.servicefacade.HolidayServiceFacade;

public class InstallmentValidationContext {
    
    private final Date disbursementDate;
    private final VariableInstallmentDetailsBO variableInstallmentDetails;
    private final HolidayServiceFacade holidayServiceFacade;
    private final Short officeId;
    private final BigDecimal minInstallmentAmount;

    public InstallmentValidationContext(Date disbursementDate, VariableInstallmentDetailsBO variableInstallmentDetails,
                                        BigDecimal minInstallmentAmount, HolidayServiceFacade holidayServiceFacade, Short officeId) {
        this.disbursementDate = disbursementDate;
        this.variableInstallmentDetails = variableInstallmentDetails;
        this.minInstallmentAmount = minInstallmentAmount;
        this.holidayServiceFacade = holidayServiceFacade;
        this.officeId = officeId;
    }

    public Date getDisbursementDate() {
        return disbursementDate;
    }

    public VariableInstallmentDetailsBO getVariableInstallmentDetails() {
        return variableInstallmentDetails;
    }
    
    public BigDecimal getMinInstallmentAmount() {
        return minInstallmentAmount;
    }

    public HolidayServiceFacade getHolidayServiceFacade() {
        return holidayServiceFacade;
    }

    public Short getOfficeId() {
        return officeId;
    }
}
