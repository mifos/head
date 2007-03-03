package org.mifos.application.productdefinition.util.helpers;

import org.mifos.framework.exceptions.PropertyNotFoundException;

public enum PrdStatus {
	LOAN_ACTIVE((short) 1), 
	SAVINGS_ACTIVE((short) 2), 

	LOAN_INACTIVE((short) 4), 
	SAVINGS_INACTIVE((short) 5);

	Short value;

	PrdStatus(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}

	public static PrdStatus getPrdStatus(Short value)
			throws PropertyNotFoundException {
		for (PrdStatus prdStatus : PrdStatus.values())
			if (prdStatus.getValue().equals(value))
				return prdStatus;
		throw new PropertyNotFoundException("PrdStatus");
	}
}
