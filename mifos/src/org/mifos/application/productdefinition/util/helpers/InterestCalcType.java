package org.mifos.application.productdefinition.util.helpers;


public enum InterestCalcType {
	MINIMUM_BALANCE((short) 1), AVERAGE_BALANCE((short) 2);

	private Short value;

	private InterestCalcType(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}

	public static InterestCalcType fromInt(int value) {
		for (InterestCalcType candidate : InterestCalcType.values()) {
			if (candidate.getValue() == value) {
				return candidate;
			}
		}
		throw new RuntimeException("no interest type " + value);
	}

}
