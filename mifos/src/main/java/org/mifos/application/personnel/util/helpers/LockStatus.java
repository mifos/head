package org.mifos.application.personnel.util.helpers;

public enum LockStatus {
	
	LOCK(Short.valueOf("1")), UNLOCK(Short.valueOf("0"));

	Short value;

	LockStatus(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}

}
