package org.mifos.application.productdefinition.util.helpers;


public enum SavingsType {
	MANDATORY((short) 1), VOLUNTARY((short) 2);

	private Short value;

	private SavingsType(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}

	public static SavingsType fromInt(int value) {
		for (SavingsType savingsType : SavingsType.values()) {
			if (savingsType.getValue() == value) {
				return savingsType;
			}
		}
		throw new RuntimeException("no savings type " + value);
	}
}
