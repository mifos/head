package org.mifos.application.accounts.util.helpers;

public enum PaymentStatus {
	UNPAID((short) 0), PAID((short) 1);

	Short value;

	PaymentStatus(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}
	
	public static PaymentStatus fromInt(int value) {
		for (PaymentStatus candidate : PaymentStatus.values()) {
			if (candidate.getValue() == value) {
				return candidate;
			}
		}
		throw new RuntimeException("no payment status " + value);
	}

}
