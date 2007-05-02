package org.mifos.application.util.helpers;

public enum CustomFieldType {

	NUMERIC((short)1), 
	ALPHA_NUMERIC((short)2), 
	DATE((short)3);
	
	public static CustomFieldType NONE = null;

	Short value;

	CustomFieldType(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}
	
	public static CustomFieldType fromInt(int value) {
		for (CustomFieldType candidate : values()) {
			if (candidate.getValue() == value) {
				return candidate;
			}
		}
		throw new RuntimeException("no field type " + value);
	}

}
