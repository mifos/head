package org.mifos.application.office.util.helpers;

public enum OfficeStatus {
	ACTIVE(Short.valueOf("1")), INACTIVE(Short.valueOf("2"));
	Short value;

	OfficeStatus(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}
}
