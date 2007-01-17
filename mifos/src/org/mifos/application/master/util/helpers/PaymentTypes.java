package org.mifos.application.master.util.helpers;

public enum PaymentTypes {
	CASH((short) 1), VOUCHER((short) 2), CHEQUE((short) 3);
	
	Short value;

	PaymentTypes(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}
}
