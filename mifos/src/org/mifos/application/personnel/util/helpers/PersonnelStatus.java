package org.mifos.application.personnel.util.helpers;

public enum PersonnelStatus {
	ACTIVE(Short.valueOf("1")), INACTIVE(Short.valueOf("2"));

	Short value;

	PersonnelStatus(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}

}
