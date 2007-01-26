package org.mifos.application.productdefinition.util.helpers;

public enum PrdApplicableMaster {
	CLIENTS((short) 1), GROUPS((short) 2),CENTERS((short)3),ALLCUSTOMERS((short)4);

	private Short value;

	private PrdApplicableMaster(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}

	public static PrdApplicableMaster fromInt(int value) {
		for (PrdApplicableMaster candidate : PrdApplicableMaster.values()) {
			if (candidate.getValue() == value)
				return candidate;
		}
		throw new RuntimeException("no applicable master for " + value);
	}

}
