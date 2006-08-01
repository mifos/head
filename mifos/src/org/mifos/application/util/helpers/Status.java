package org.mifos.application.util.helpers;

public enum Status {

	INACTIVE(Short.valueOf("0")), ACTIVE(Short.valueOf("1"));

	Short value;

	Status(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}

}
