package org.mifos.application.util.helpers;

public enum EntityType {

	PERSONNEL(Short.valueOf("17")), CENTER(Short.valueOf("20"));

	Short value;

	EntityType(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}

}
