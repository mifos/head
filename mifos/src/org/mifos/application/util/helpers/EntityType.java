package org.mifos.application.util.helpers;

public enum EntityType {

	PERSONNEL(Short.valueOf("17")), CENTER(Short.valueOf("20")), CLIENT(Short.valueOf("1")),OFFICE(Short.valueOf("15"));

	Short value;

	EntityType(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}

}
