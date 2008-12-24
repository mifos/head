package org.mifos.application.productdefinition.util.helpers;

public enum ApplicableTo {
	CLIENTS((short)1), 
	GROUPS((short)2),
	CENTERS((short)3),
	ALLCUSTOMERS((short)4);

	private Short value;

	private ApplicableTo(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}

	public static ApplicableTo fromInt(int value) {
		for (ApplicableTo candidate : ApplicableTo.values()) {
			if (candidate.getValue() == value)
				return candidate;
		}
		throw new RuntimeException("no applicable master for " + value);
	}

}
