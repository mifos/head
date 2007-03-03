package org.mifos.application.productdefinition.util.helpers;


public enum RecommendedAmountUnit {
	PER_INDIVIDUAL((short) 1), COMPLETE_GROUP((short) 2);

	private Short value;

	private RecommendedAmountUnit(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}

	public static RecommendedAmountUnit fromInt(int value) {
		for (RecommendedAmountUnit candidate : RecommendedAmountUnit.values()) {
			if (candidate.getValue() == value) {
				return candidate;
			}
		}
		throw new RuntimeException("no recommended amount unit " + value);
	}

}
