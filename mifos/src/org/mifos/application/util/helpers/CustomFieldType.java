package org.mifos.application.util.helpers;

public enum CustomFieldType {

	NUMERIC(Short.valueOf("1")), ALPHA_NUMERIC(Short.valueOf("2")), DATE(Short.valueOf("3"));

	Short value;

	CustomFieldType(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}
}
