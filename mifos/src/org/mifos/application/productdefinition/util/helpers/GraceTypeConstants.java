package org.mifos.application.productdefinition.util.helpers;

import org.mifos.framework.exceptions.PropertyNotFoundException;

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

	public static GraceTypeConstants getGraceTypeConstants(Short value)
			throws PropertyNotFoundException {
		for (GraceTypeConstants graceTypeConstants : GraceTypeConstants
				.values())
			if (graceTypeConstants.getValue().equals(value))
				return graceTypeConstants;
		throw new PropertyNotFoundException("GraceTypeConstants");
	}
}
