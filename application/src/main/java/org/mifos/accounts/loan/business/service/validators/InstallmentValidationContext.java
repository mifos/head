package org.mifos.accounts.loan.business.service.validators;

import org.mifos.accounts.productdefinition.business.VariableInstallmentDetailsBO;
import org.mifos.application.admin.servicefacade.HolidayServiceFacade;

import java.util.Date;

public class InstallmentValidationContext {
    private Date disbursementDate;
    private VariableInstallmentDetailsBO variableInstallmentDetails;
    private HolidayServiceFacade holidayServiceFacade;
    private Short officeId;

    public InstallmentValidationContext(Date disbursementDate, VariableInstallmentDetailsBO variableInstallmentDetails,
                                        HolidayServiceFacade holidayServiceFacade, Short officeId) {
        this.disbursementDate = disbursementDate;
        this.variableInstallmentDetails = variableInstallmentDetails;
        this.holidayServiceFacade = holidayServiceFacade;
        this.officeId = officeId;
    }

    public Date getDisbursementDate() {
        return disbursementDate;
    }

    public VariableInstallmentDetailsBO getVariableInstallmentDetails() {
        return variableInstallmentDetails;
    }

    public HolidayServiceFacade getHolidayServiceFacade() {
        return holidayServiceFacade;
    }

    public Short getOfficeId() {
        return officeId;
    }
}
