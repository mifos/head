package org.mifos.application.productdefinition.util.helpers;

public enum InterestTypeConstants {
	
	FLATINTERST((short) 1), DECLININGINTEREST((short) 2),COMPOUNDINTEREST((short) 3);
	
	Short value;
	
	InterestTypeConstants(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}
}
