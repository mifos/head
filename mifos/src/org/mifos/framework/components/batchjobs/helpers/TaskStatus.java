package org.mifos.framework.components.batchjobs.helpers;


public enum TaskStatus {
	COMPLETE(Short.valueOf("1")), INCOMPLETE(Short.valueOf("0"));
	Short value;

	TaskStatus(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}

	public static TaskStatus fromInt(int value) {
		for (TaskStatus candidate : TaskStatus.values()) {
			if (candidate.getValue() == value) {
				return candidate;
			}
		}
		throw new RuntimeException("task status " + value + " not recognized");
	}

}
