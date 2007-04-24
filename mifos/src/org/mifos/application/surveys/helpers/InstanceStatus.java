package org.mifos.application.surveys.helpers;

public enum InstanceStatus {
	COMPLETED(1),
	INCOMPLETE(0);

	private final int value;

	private InstanceStatus(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static InstanceStatus fromInt(int status) {
		for (InstanceStatus candidate : InstanceStatus.values()) {
			if (status == candidate.getValue()) {
				return candidate;
			}
		}
		throw new RuntimeException("no instance status " + status);
	}

}
