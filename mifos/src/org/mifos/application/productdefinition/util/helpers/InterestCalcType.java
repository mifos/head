package org.mifos.application.productdefinition.util.helpers;

import org.mifos.framework.exceptions.PropertyNotFoundException;

public enum InterestCalcType {
	MINIMUM_BALANCE((short) 1), AVERAGE_BALANCE((short) 2);

	Short value;

	InterestCalcType(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}

	public static InterestCalcType getInterestCalcType(Short value)
			throws PropertyNotFoundException {
		for (InterestCalcType interestCalcType : InterestCalcType.values())
			if (interestCalcType.getValue().equals(value))
				return interestCalcType;
		throw new PropertyNotFoundException("InterestCalcType");
	}
}
