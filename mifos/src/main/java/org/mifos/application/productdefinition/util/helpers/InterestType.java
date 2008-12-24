package org.mifos.application.productdefinition.util.helpers;


public enum InterestType {

	FLAT((short) 1), 
	DECLINING((short) 2), 
	COMPOUND((short) 3),
	DECLINING_EPI((short) 4); //Equal Principal Installments

	private Short value;

	private InterestType(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}

	public static InterestType fromInt(int value) {
		for (InterestType candidate : InterestType.values()) {
			if (candidate.getValue() == value) {
				return candidate;
			}
		}
		throw new RuntimeException("interest type " + value + " not recognized");
	}

}
