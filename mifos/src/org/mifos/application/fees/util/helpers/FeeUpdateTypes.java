package org.mifos.application.fees.util.helpers;

public enum FeeUpdateTypes {

	AMOUNT_UPDATED((short) 1), STATUS_UPDATED((short) 2), AMOUNT_AND_STATUS_UPDATED((short) 3);

	Short value;

	FeeUpdateTypes(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}
}
