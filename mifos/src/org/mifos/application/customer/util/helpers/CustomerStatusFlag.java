package org.mifos.application.customer.util.helpers;

public enum CustomerStatusFlag {

	CLIENT_CANCEL_WITHDRAW(Short.valueOf("1")), CLIENT_CANCEL_REJECTED(Short
			.valueOf("2")), CLIENT_CANCEL_BLACKLISTED(Short.valueOf("3")), CLIENT_CANCEL_DUPLICATE(
			Short.valueOf("4")), CLIENT_CANCEL_OTHER(Short.valueOf("5")), CLIENT_CLOSED_TRANSFERRED(
			Short.valueOf("6")), CLIENT_CLOSED_DUPLICATE(Short.valueOf("7")), CLIENT_CLOSED_BLACKLISTED(
			Short.valueOf("8")), CLIENT_CLOSED_LEFTPROGRAM(Short.valueOf("9")), CLIENT_CLOSED_OTHER(
			Short.valueOf("10")), GROUP_CANCEL_WITHDRAW(Short.valueOf("11")), GROUP_CANCEL_REJECTED(
			Short.valueOf("12")), GROUP_CANCEL_BLACKLISTED(Short.valueOf("13")), GROUP_CANCEL_DUPLICATE(
			Short.valueOf("14")), GROUP_CANCEL_OTHER(Short.valueOf("15")), GROUP_CLOSED_TRANSFERRED(
			Short.valueOf("16")), GROUP_CLOSED_DUPLICATE(Short.valueOf("17")), GROUP_CLOSED_BLACKLISTED(
			Short.valueOf("18")), GROUP_CLOSED_LEFTPROGRAM(Short.valueOf("19")), GROUP_CLOSED_OTHER(
			Short.valueOf("20")), ;

	Short value;

	CustomerStatusFlag(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}
}
