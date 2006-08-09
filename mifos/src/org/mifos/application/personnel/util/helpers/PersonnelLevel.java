package org.mifos.application.personnel.util.helpers;

public enum PersonnelLevel {

	LOAN_OFFICER(Short.valueOf("1")), NON_LOAN_OFFICER(Short.valueOf("2"));

	Short value;

	PersonnelLevel(Short value) {
		this.value = value;
	}

	public Short getValue() {
		return value;
	}

}
