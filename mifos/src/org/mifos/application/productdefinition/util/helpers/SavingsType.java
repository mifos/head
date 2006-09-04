package org.mifos.application.productdefinition.util.helpers;

import org.mifos.framework.exceptions.PropertyNotFoundException;

public enum SavingsType {
	MANDATORY((short) 1), VOLUNTARY((short) 2);

	Short value;

	SavingsType(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}

	public static SavingsType getSavingsType(Short value)
			throws PropertyNotFoundException {
		for (SavingsType savingsType : SavingsType
				.values())
			if (savingsType.getValue().equals(value))
				return savingsType;
		throw new PropertyNotFoundException("SavingsType");
	}
}
