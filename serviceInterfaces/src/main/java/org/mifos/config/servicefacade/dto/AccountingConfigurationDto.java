package org.mifos.config.servicefacade.dto;

public class AccountingConfigurationDto {

    private Short digitsBeforeDecimal;
    private Short digitsAfterDecimal;
    private int glCodeMode;

    public int getGlCodeMode() {
		return glCodeMode;
	}

	public void setGlCodeMode(int glCodeMode) {
		this.glCodeMode = glCodeMode;
	}

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
