package org.mifos.application.productdefinition.util.helpers;

import org.mifos.framework.exceptions.PropertyNotFoundException;

public enum InterestTypeConstants {

	FLATINTERST((short) 1), DECLININGINTEREST((short) 2), COMPOUNDINTEREST(
			(short) 3);

	Short value;

	InterestTypeConstants(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}

	public static InterestTypeConstants getInterestTypeConstants(Short value)
			throws PropertyNotFoundException {
		for (InterestTypeConstants interestTypeConstants : InterestTypeConstants
				.values())
			if (interestTypeConstants.getValue().equals(value))
				return interestTypeConstants;
		throw new PropertyNotFoundException("InterestTypeConstants");
	}
}
