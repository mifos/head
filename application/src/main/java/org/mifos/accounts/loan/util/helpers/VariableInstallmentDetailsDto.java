package org.mifos.accounts.loan.util.helpers;

import org.mifos.framework.business.service.DataTransferObject;
import org.mifos.framework.util.helpers.Money;

public class VariableInstallmentDetailsDto implements DataTransferObject {

    private Integer maxGapInDays;
    private Money minInstallmentAmount;
    private Boolean variableInstallmentsAllowed = Boolean.FALSE;
    private Integer minGapInDays;

    public Integer getMinGapInDays() {
        return this.minGapInDays;
    }

    public void setMinGapInDays(Integer minGapInDays) {
        this.minGapInDays = minGapInDays;
    }

    public Integer getMaxGapInDays() {
        return this.maxGapInDays;
    }

    public void setMaxGapInDays(Integer maxGapInDays) {
        this.maxGapInDays = maxGapInDays;
    }

    public Money getMinInstallmentAmount() {
        return this.minInstallmentAmount;
    }

    public void setMinInstallmentAmount(Money minInstallmentAmount) {
        this.minInstallmentAmount = minInstallmentAmount;
    }

    public Boolean isVariableInstallmentsAllowed() {
        return this.variableInstallmentsAllowed;
    }

    public void setVariableInstallmentsAllowed(Boolean variableInstallmentsAllowed) {
        this.variableInstallmentsAllowed = variableInstallmentsAllowed;
    }


}
