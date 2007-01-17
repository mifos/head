package org.mifos.application.configuration.util.helpers;

public enum LabelConfigurations {
	CLIENT_PARTIAL_APPLICATION(Integer.valueOf("1")), CLIENT_PENDING_APPROVAL(
			Integer.valueOf("2")), CLIENT_ACTIVE(Integer.valueOf("3")), CLIENT_ON_HOLD(
			Integer.valueOf("4")), CLIENT_CANCEL(Integer.valueOf("5")), CLIENT_CLOSED(
			Integer.valueOf("6")),

	GROUP_PARTIAL_APPLICATION(Integer.valueOf("7")), GROUP_PENDING_APPROVAL(
			Integer.valueOf("8")), GROUP_ACTIVE(Integer.valueOf("9")), GROUP_ON_HOLD(
			Integer.valueOf("10")), GROUP_CANCEL(Integer.valueOf("11")), GROUP_CLOSED(
			Integer.valueOf("12")),

	CENTER_ACTIVE(Integer.valueOf("13")), CENTER_INACTIVE(Integer.valueOf("14")),

	OFFICE_ACTIVE(Integer.valueOf("15")), OFFICE_INACTIVE(Integer.valueOf("16")),

	LOAN_PARTIAL_APPLICATION(Integer.valueOf("17")), LOAN_PENDING_APPROVAL(
			Integer.valueOf("18")), LOAN_APPROVED(Integer.valueOf("19")), LOAN_ACTIVE_GOOD_STANDING(
			Integer.valueOf("21")), LOAN_CLOSED_OBLIGATION_MET(Integer
			.valueOf("22")), LOAN_CLOSED_WRITTEN_OFF(Integer.valueOf("23")), LOAN_CLOSED_RESCHEDULE(
			Integer.valueOf("24")), LOAN_ACTIVE_BAD_STANDING(Integer
			.valueOf("25")), LOAN_CANCEL(Integer.valueOf("141")),

	SAVINGS_PARTIAL_APPLICATION(Integer.valueOf("181")), SAVINGS_PENDING_APPROVAL(
			Integer.valueOf("182")), SAVINGS_ACTIVE(Integer.valueOf("184")), SAVINGS_INACTIVE(
			Integer.valueOf("210")), SAVINGS_CANCEL(Integer.valueOf("183")), SAVINGS_CLOSED(
			Integer.valueOf("185")),

	FEE_CATEGORY_CLIENT(Integer.valueOf("82")), FEE_CATEGORY_GROUP(Integer
			.valueOf("83")), FEE_CATEGORY_CENTER(Integer.valueOf("84")), FEE_CATEGORY_LOAN(
			Integer.valueOf("86")),

	FEE_ACTIVE(Integer.valueOf("165")), FEE_INACTIVE(Integer.valueOf("166")),

	PERSONNEL_ACTIVE(Integer.valueOf("152")), PERSONNEL_INACTIVE(Integer
			.valueOf("153")),

	PRD_CATEGORY_ACTIVE(Integer.valueOf("114")), PRD_CATEGORY_INACTIVE(Integer
			.valueOf("113")),

	PRD_ACTIVE(Integer.valueOf("115")), PRD_INACTIVE(Integer.valueOf("116"));

	Integer value;

	LabelConfigurations(Integer value) {
		this.value = value;
	}

	public Integer getValue() {
		return value;
	}
}
