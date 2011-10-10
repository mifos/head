package org.mifos.config.servicefacade.dto;

public class AccountingConfigurationDto {

    private Short digitsBeforeDecimal;
    private Short digitsAfterDecimal;

    public Short getDigitsAfterDecimal() {
        return digitsAfterDecimal;
    }

    public void setDigitsAfterDecimal(Short digitsAfterDecimal) {
        this.digitsAfterDecimal = digitsAfterDecimal;
    }

    public Short getDigitsBeforeDecimal() {
        return digitsBeforeDecimal;
    }

    public void setDigitsBeforeDecimal(Short digitsBeforeDecimal) {
        this.digitsBeforeDecimal = digitsBeforeDecimal;
    }

}
