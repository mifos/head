package org.mifos.application.productdefinition.util.helpers;


public enum GraceType {

	NONE((short) 1), 
	GRACEONALLREPAYMENTS((short) 2), 
	PRINCIPALONLYGRACE((short) 3);
	Short value;

	GraceType(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}

	public static GraceType fromInt(int value) {
		for (GraceType graceTypeConstants : GraceType.values()) {
			if (graceTypeConstants.getValue() == value) {
				return graceTypeConstants;
			}
		}
		throw new RuntimeException("no grace type " + value);
	}
}
