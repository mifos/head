package org.mifos.application.customer.util.helpers;

public enum CustomerStatusFlag {
	
	CLIENT_WITHDRAW (Short.valueOf("1")), CLIENT_REJECTED (Short.valueOf("2"));
	
	Short value;

	CustomerStatusFlag(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}
}
