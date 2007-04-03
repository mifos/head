package org.mifos.application.util.helpers;

public enum CustomFieldType {

	NUMERIC((short)1), 
	ALPHA_NUMERIC((short)2), 
	DATE((short)3);

	Short value;

	CustomFieldType(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}
}
