package org.mifos.application.checklist.util.helpers;

public enum CheckListType {
	CUSTOMER_CHECKLIST((short) 1), ACCOUNT_CHECKLIST((short) 2);

	Short value;

	CheckListType(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}
}
