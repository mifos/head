package org.mifos.application.productdefinition.util.helpers;


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

	public static PrdStatus fromInt(int value) {
		for (PrdStatus candidate : PrdStatus.values()) {
			if (candidate.getValue() == value) {
				return candidate;
			}
		}
		throw new RuntimeException("no product status " + value);
	}

}
