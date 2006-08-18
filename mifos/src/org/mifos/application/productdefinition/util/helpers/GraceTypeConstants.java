package org.mifos.application.productdefinition.util.helpers;

public enum GraceTypeConstants {

	NONE((short) 1), GRACEONALLREPAYMENTS((short) 2), PRINCIPALONLYGRACE(
			(short) 3);
	Short value;
	
	GraceTypeConstants(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}
}
