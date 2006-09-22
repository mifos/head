package org.mifos.framework.components.cronjobs.helpers;

public enum TaskStatus {
	COMPLETE(Short.valueOf("1")), INCOMPLETE(Short.valueOf("0"));
	Short value;

	TaskStatus(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}

}
